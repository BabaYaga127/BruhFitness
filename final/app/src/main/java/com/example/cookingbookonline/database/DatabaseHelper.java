package com.example.cookingbookonline.database;

import android.app.DownloadManager;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.strictmode.SqliteObjectLeakedViolation;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.cookingbookonline.R;
import com.example.cookingbookonline.model.Notification;
import com.example.cookingbookonline.model.Post;
import com.example.cookingbookonline.model.Profile;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Node;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "cookbookonline.db";
    public static final int DATABASE_VERSION = 1;

    private Context context;

    static private int oldMaxProfileId = -1;
    static private int oldMaxPostId = -1;
    static private int oldMaxNotificationId = -1;
    static public boolean onCreatedCalled = false;

    LoadingListener listener;

    public interface LoadingListener{
        void OnLoading();
        void OnFinsishLoading();
    }

    static public void DeleteDatabase(Context context){
        File file = context.getDatabasePath(DATABASE_NAME);
        while (file.exists()){
            //Toast.makeText(context, "DATABASE DELETED", Toast.LENGTH_LONG).show();
            file.delete();
        }
    }

    static public boolean CheckDatabase(Context context){
        SQLiteDatabase checkDB = null;
        try {
            checkDB = SQLiteDatabase.openDatabase(DatabaseHelper.DATABASE_NAME, null, SQLiteDatabase.OPEN_READONLY);
            checkDB.close();
        } catch (SQLiteException e) {
            // database doesn't exist yet.
            return false;
        }
        return checkDB != null;
    }

    static public void CheckAndDeleteDatabase(Context context){
//        if (CheckDatabase(context)){
//            context.deleteDatabase(DatabaseHelper.DATABASE_NAME);
//        }
        // Delete old database part 2
        DeleteDatabase(context);
    }

    public DatabaseHelper(@Nullable Context context, LoadingListener listener) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        this.listener = listener;

        //Toast.makeText(context, "db constructor " , Toast.LENGTH_LONG).show();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        onCreatedCalled = true;

        //Toast.makeText(context, "db onCreate", Toast.LENGTH_LONG).show();

        try {
            listener.OnLoading();

            int result = insertFromFile(context,db, R.raw.create_table, 1, "");
            Toast.makeText(context, "loading ...", Toast.LENGTH_SHORT).show();

            DataApi api = new DataApi(context);


            api.GetProfiles(new DataApi.ProfilesResponseListener() {
                @Override
                public void OnError(String message) { }

                @Override
                public void OnResponse(List<Profile> Profiles) {
                    int result = insertProfileFromServer(context, db, Profiles);
                    //Toast.makeText(context, "profiles downloaded " + String.valueOf(Profiles.size()), Toast.LENGTH_SHORT).show();

                    api.GetPosts(new DataApi.PostsResponseListener() {
                        @Override
                        public void OnError(String message) { }

                        @Override
                        public void OnResponse(List<Post> Posts) {
                            int result = insertPostFromServer(context, db, Posts);
                            //Toast.makeText(context, "Posts downloaded " + String.valueOf(Posts.size()), Toast.LENGTH_SHORT).show();

                            api.GetNotifications(new DataApi.NotificationsResponseListener() {
                                @Override
                                public void OnError(String message) { }

                                @Override
                                public void OnResponse(List<Notification> notifications) {
                                    int result = insertNotificationFromServer(context, db, notifications, false);
                                    //Toast.makeText(context, "notifications downloaded " + String.valueOf(notifications.size()), Toast.LENGTH_SHORT).show();

                                    api.GetNotifications(new DataApi.NotificationsResponseListener() {
                                        @Override
                                        public void OnError(String message) { }

                                        @Override
                                        public void OnResponse(List<Notification> notifications) {
                                            int result = insertNotificationFromServer(context, db, notifications, true);
                                            //Toast.makeText(context, "Rnotifications downloaded " + String.valueOf(notifications.size()), Toast.LENGTH_SHORT).show();

                                            Toast.makeText(context, "finished", Toast.LENGTH_SHORT).show();
                                            listener.OnFinsishLoading();
                                        }
                                    }, true);
                                }
                            }, false);
                        }
                    });
                }
            });

        } catch (Exception e){

            Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();

            try {
                int result = insertFromFile(context,db, R.raw.create_table, 1, "");
                //Toast toast = Toast.makeText(context, "sql create table " + String.valueOf(result), Toast.LENGTH_SHORT);
                //toast.show();

                result = insertFromFile(context, db, R.raw.add_first_data, 2, "");
                //toast = Toast.makeText(context, "sql inserted " + String.valueOf(result) + " rows", Toast.LENGTH_SHORT);
                //toast.show();

            } catch (Exception e2){
                Toast toast = Toast.makeText(context, e2.toString(), Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            int result = insertFromFile(context,db, R.raw.table_names, 2, "DROP TABLE IF EXISTS ");
            //Toast toast = Toast.makeText(context, "sql drop table " + String.valueOf(result), Toast.LENGTH_SHORT);
            //toast.show();
        } catch (Exception e){
            Toast toast = Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT);
            toast.show();
        }
        onCreate(db);
    }

    /*
    *==========================================================================================================================================================================================================================================================================================
    * Initializing Code
    *
    * */

    private int insertProfileFromServer(Context context, SQLiteDatabase db, List<Profile> profiles) {
        int result = 0;

        for (Profile profile : profiles){

            String sql = QueryHelper.insertProfile(profile.ID, profile.backgroundImg, profile.avatar, profile.name, profile.gmail, profile.password);
            db.execSQL(sql);
            ++result;

        }

        return result;
    }

    private int insertPostFromServer(Context context, SQLiteDatabase db, List<Post> posts) {
        int result = 0;

        for (Post post : posts){

            String sql = QueryHelper.insertPost(post.ID, post.profileId, post.status, post.text, post.image);
            db.execSQL(sql);
            ++result;

        }

        return result;
    }

    private int insertNotificationFromServer(Context context, SQLiteDatabase db, List<Notification> notifications, boolean receivednotification){
        int result = 0;

        for (Notification not : notifications){

            String sql = QueryHelper.insertNotification(not.ID, not.postId, not.profileId, not.type, not.status, not.comment);
            if (receivednotification) sql = QueryHelper.insertReceivedNotification(not.profileId, not.postId);

            db.execSQL(sql);
            ++result;

        }

        return result;
    }

    private int insertFromFile(Context context, SQLiteDatabase db, int resourceId, int mode, String preSql) throws IOException{
        int result = 0; // number of inserted date or number of table created

        // open resource
        InputStream stream = context.getResources().openRawResource(resourceId);
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

        while (reader.ready()){
            String sql = "";
            if (mode == 1){ // create table
                String temp = reader.readLine();
                while (temp.length() > 0){ // null line
                    sql = sql + temp;
                    temp = reader.readLine();
                }
            } else {    // insert data
                sql = reader.readLine();
                while ((sql == null || sql.length() == 0) && reader.ready())
                    sql = reader.readLine();
            }

            // preSql to cooperate with raw file
            if(sql != null && sql.length() > 0) {
                db.execSQL(preSql + sql);
                ++result;
            }
        }

        reader.close();

        return result;
    }

    /*
     *==========================================================================================================================================================================================================================================================================================
     * Api communicating code
     *
     * */

    public void DeleteNotificationToServer(int ID){
        DataApi api = new DataApi(context);

        api.DeleteToServerRequest(DataApi.URL_GET_A_RECEIVEDNOTIFICATIONS, ID);
        api.DeleteToServerRequest(DataApi.URL_GET_A_NOTIFICATIONS, ID);
    }

    private void EditProfileToServer(Profile profile) {
        DataApi api = new DataApi(context);
        api.PutToServerRequest(api.URL_GET_A_PROFILES, profile);
    }

    public void InsertProfileToServer(Profile profile) {
        DataApi api = new DataApi(context);
        api.PostToServerRequest(api.URL_GET_PROFILES, profile.toHashMap());
    }
    public void InsertPostToServer(Post post) {
        DataApi api = new DataApi(context);
        api.PostToServerRequest(DataApi.URL_GET_POSTS, post.toHashMap());
    }

    public void InsertNotificationToServer(Notification not, int receiverId){
        DataApi api = new DataApi(context);
        api.PostToServerRequest(DataApi.URL_GET_NOTIFICATIONS, not.toHashMap(false, receiverId));
        api.PostToServerRequest(DataApi.URL_GET_RECEIVEDNOTIFICATIONS, not.toHashMap(true, receiverId));
    }

    /*
     *==========================================================================================================================================================================================================================================================================================
     * local database sql server communicating Code
     *
     * */

    public int CountEnterty(String table, int id){
        int result = -1;

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor;
        if (id >= 0) cursor = db.rawQuery("select count(*) from '" + table + "' where ID = " + String.valueOf(id), null);
        else cursor = db.rawQuery("select count(*) from '" + table + "'", null);

        if (cursor.moveToFirst()) {
            result = cursor.getInt(0);
        }

        cursor.close();
        //db.close();

        return result;
    }

    public List<Profile> getAllProfile(){
        List<Profile> result = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(QueryHelper.getAllProfiles(), null);

        if (cursor.moveToFirst()){
            do {
                Profile profile = QueryHelper.ProfileParser(cursor);
                result.add(profile);
            }while (cursor.moveToNext());
        }

        cursor.close();
        //db.close();

        return result;
    }

    public int getProfileMaxId(){
        int result = -1;

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(QueryHelper.getProfileMaxId(), null);

        if (cursor.moveToFirst()) {
            result = cursor.getInt(0);
        }

        cursor.close();
        //db.close();

        result = Math.max(result, oldMaxPostId);
        oldMaxProfileId = result;

        return result;
    }

    public int getCommentMaxId(){
        int result = -1;

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(QueryHelper.getNotificationMaxId(), null);

        if (cursor.moveToFirst()) {
            result = cursor.getInt(0);
        }

        cursor.close();
        //db.close();

        result = Math.max(result, oldMaxNotificationId);
        oldMaxNotificationId = result;

        return result;
    }

    public int getPostMaxId(){
        int result = -1;

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(QueryHelper.getPostMaxId(), null);

        if (cursor.moveToFirst()) {
            result = cursor.getInt(0);
        }

        cursor.close();
        //db.close();

        result = Math.max(result, oldMaxPostId);
        oldMaxPostId = result;

        return result;
    }

    public Profile getProfile(int profileId){
        Profile result = null;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(QueryHelper.getProfile(profileId), null);

        if (cursor.moveToFirst()) {
            result = QueryHelper.ProfileParser(cursor);
        }

        cursor.close();
        //db.close();

        return result;
    }

    private Post getFullPost(Post post, int userId){
        // get the rest of the post
        Profile profile = getProfile(post.profileId);

        post.profileAvatar = profile.avatar;
        post.profileName = profile.name;
        post.likeNum = getLikeCount(post.ID, -1);
        post.commentNum = getCommentCount(post.ID, -1);
        post.shareNum = getShareCount(post.ID, -1);
        post.liked = getLikeCount(post.ID, userId) > 0;
        post.shared = getShareCount(post.ID, userId) > 0;

        return post;
    }

    public Post getPost(int postId, int userId){
        Post result = null;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(QueryHelper.getPost(postId), null);

        if (cursor.moveToFirst()) {
            // get the basic of post
            result = QueryHelper.PostParser(cursor);

            // get the rest of post
            result = getFullPost(result, userId);

        }

        cursor.close();
        //db.close();

        return result;
    }

    public Profile findProfile(String gmail, String password){
        Profile result = null;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(QueryHelper.findProfile(gmail, password), null);

        if (cursor.moveToFirst()) {
            result = QueryHelper.ProfileParser(cursor);
        }

        cursor.close();
        //db.close();

        return result;
    }

    public int findNotificationId(int postId, int senderId, char type){
        int result = -1;

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(QueryHelper.findNotficicationId(postId, senderId, type), null);

        if (cursor.moveToFirst()) {
            result = cursor.getInt(0);
        }

        cursor.close();
        //db.close();

        return result;
    }

    public int getLikeCount(int postId, int profileId){
        int result = 0;

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(QueryHelper.getLikeCount(postId, profileId), null);

        if (cursor.moveToFirst()) {
            result = cursor.getInt(0);
        }

        cursor.close();
        //db.close();

        return result;
    }

    public int getCommentCount(int postId, int profileId){
        int result = 0;

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(QueryHelper.getCommentCount(postId, profileId), null);

        if (cursor.moveToFirst()) {
            result = cursor.getInt(0);
        }

        cursor.close();
        //db.close();

        return result;
    }

    public int getShareCount(int postId, int profileId){
        int result = 0;

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(QueryHelper.getShareCount(postId, profileId), null);

        if (cursor.moveToFirst()) {
            result = cursor.getInt(0);
        }

        cursor.close();
        //db.close();

        return result;
    }

    public List<Post> getAllPost(int profileId, int userId){
        List<Post> result = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(QueryHelper.getAllPosts(profileId), null);

        if (cursor.moveToFirst()){
            do {
                // get basic of post
                Post post = QueryHelper.PostParser(cursor);

                // get the rest of post
                post = getFullPost(post, userId);

                result.add(post);
            }while (cursor.moveToNext());
        }

        cursor.close();
        //db.close();

        return result;
    }

    public List<Notification> getAllComment(int postId, int profileId){
        List<Notification> result = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(QueryHelper.getAllComment(postId, profileId), null);

        if (cursor.moveToFirst()){
            do {
                // get the basic of comment
                Notification comment = QueryHelper.CommentParser(cursor);

                // get the rest (avatar, name)
                Profile profile = getProfile(comment.profileId);
                comment.profileAvatar = profile.avatar;
                comment.profileName = profile.name;

                result.add(comment);
            }while (cursor.moveToNext());
        }

        cursor.close();
        //db.close();

        return result;
    }
    public int addProfile(Profile profile){

        int result = -1;

        // find max id first for no duplicate insert
        profile.ID = getProfileMaxId() + 1;

        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL(QueryHelper.insertProfile(profile.ID, profile.backgroundImg, profile.avatar, profile.name, profile.gmail, profile.password));

        result = profile.ID;

        //db.close();

        return result;
    }

    public void insertProfile(Profile profile){
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL(QueryHelper.insertProfile(profile.ID, profile.backgroundImg, profile.avatar, profile.name, profile.gmail, profile.password));

        //db.close();
    }

    public void addPost(Post post){

        post.status = QueryHelper.getStatus();

        try {
            InsertPostToServer(post);
        } catch (Exception e){

        }


        // find max id first for no duplicate insert
        post.ID = getPostMaxId() + 1;

        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL(QueryHelper.insertPost(post.ID, post.profileId, post.status, post.text, post.image));

        //db.close();
    }


    public void insertPost(Post post){
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL(QueryHelper.insertPost(post.ID, post.profileId, post.status, post.text, post.image));

        //db.close();
    }

    public int addNotification(Notification notification, int receiverId){
        int result = -1;

        // find max id first for no duplicate insert
        notification.ID = getCommentMaxId() + 1;

        notification.status = QueryHelper.getStatus();

        try {
            InsertNotificationToServer(notification, receiverId);
        } catch (Exception e){

        }

        SQLiteDatabase db = this.getWritableDatabase();

        //Toast toast = Toast.makeText(context, QueryHelper.insertNotification(notification.ID, notification.postId, notification.profileId, notification.type, notification.status, notification.comment), Toast.LENGTH_LONG);
        //toast.show();

        db.execSQL(QueryHelper.insertNotification(notification.ID, notification.postId, notification.profileId, notification.type, notification.status, notification.comment));
        db.execSQL(QueryHelper.insertReceivedNotification(receiverId, notification.ID));

        result = notification.ID;

        //db.close();

        return result;
    }

    public void insertNotification(Notification notification){
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL(QueryHelper.insertNotification(notification.ID, notification.postId, notification.profileId, notification.type, notification.status, notification.comment));

        //db.close();
    }

    public void insertReceivedNotification(Notification notification){
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL(QueryHelper.insertReceivedNotification(notification.profileId, notification.postId));

        //db.close();
    }

    public void updateProfile(Profile userProfile){

        try {
            EditProfileToServer(userProfile);
        }catch (Exception e){

        }

        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL(QueryHelper.editProfile(userProfile.ID, userProfile.backgroundImg, userProfile.avatar, userProfile.name, userProfile.gmail, userProfile.password));

//        Toast toast = Toast.makeText(context, QueryHelper.editProfile(userProfile.ID, userProfile.backgroundImg, userProfile.avatar, userProfile.name, userProfile.gmail, userProfile.password), Toast.LENGTH_LONG);
//        toast.show();

        //db.close();
    }

    public void removeNotification(int receiverId, int postId, int senderId, char type){

        int ID = findNotificationId(postId, senderId, type);

        if (ID < 0 || CountEnterty(QueryHelper.Notifications, ID) <= 0) return;

        try {
            DeleteNotificationToServer(ID);
        } catch (Exception e){
            Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
        }

//        Toast toast = Toast.makeText(context, String.valueOf(ID) + " " + String.valueOf( CountEnterty(QueryHelper.Notifications, ID) ) + " " + type, Toast.LENGTH_LONG);
//        toast.show();

        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL(QueryHelper.removeReceivedNotification(receiverId, ID));
        db.execSQL(QueryHelper.removeNotification(ID));

        ////db.close();
    }
}
