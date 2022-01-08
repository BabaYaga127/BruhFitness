package com.example.bruhfiness.database;

import android.database.Cursor;

import com.example.bruhfiness.model.Notification;
import com.example.bruhfiness.model.Post;
import com.example.bruhfiness.model.Profile;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class QueryHelper {
    static public String Profiles = "Profiles";
    static public String Posts = "Posts";
    static public String Notifications = "Notifications";
    static public String ReceivedNotifications = "ReceivedNotifications";


    public QueryHelper(){

    }

    public static Profile ProfileParser(Cursor cursor) {
        Profile result = new Profile(cursor.getInt(0),
                cursor.getInt(1),
                cursor.getInt(2),
                cursor.getString(3),
                cursor.getString(4),
                cursor.getString(5));

        return result;
    }

    public static Post PostParser(Cursor cursor){
        int ID, ownerId, image;
        String status, text;
        ID = cursor.getInt(0);
        ownerId = cursor.getInt(1);
        status = cursor.getString(2);
        text = cursor.getString(3);
        image = cursor.getInt(4);

        Post post = new Post(ID, ownerId, status, text, image);

        return post;
    }

    public static Notification CommentParser(Cursor cursor){
        Notification result = new Notification(cursor.getInt(0),            // ID
                cursor.getInt(1),           // profileId
                cursor.getInt(2),           // senderId
                cursor.getString(3).charAt(0), // type
                cursor.getString(4),        // status
                cursor.getString(5));       // commnent

        return result;
    }

    public static String getStatus(){
        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);

        SimpleDateFormat df = new SimpleDateFormat("MM-dd-yyyy", Locale.getDefault());
        String formattedDate = df.format(c);

        return formattedDate;
    }

    public static String countProfile(int profileId){
        return ("select count(*) from Profiles where ID = " + String.valueOf(profileId));
    }

    public static String getProfileMaxId(){
        return ("select max(ID) from Profiles");
    }

    public static String getCommentMaxId() {return ("select max(ID) from Notifications");}

    public static String getPostMaxId(){
        return ("select max(ID) from Posts");
    }

    public static String getNotificationMaxId(){
        return ("select max(ID) from Notifications");
    }

    public static String getReceivedNotificationMaxId(){
        return ("select max(ID) from ReceivedNotifications");
    }

    public static String getProfile(int profileId){
        return ("select * from Profiles where ID = " + String.valueOf(profileId));
    }

    public static String getPost(int postId){
        return ("select * from Posts where ID = " + String.valueOf(postId));
    }

    public static String getAllProfiles(){
        return ("select * from '" + Profiles + "'");
    }

    public static String getAllPosts(int profileId){
        if (profileId < 0) return ("select * from '" + Posts + "' order by ID desc");
        return ("select * " +
                "from Posts " +
                "where ownerId = " + String.valueOf(profileId) + " order by ID desc");
    }

    public static String getAllNotifications(){
        return ("select * from '" + Notifications + "'");
    }

    public static String getReceivedNotifications(){
        return ("select * from '" + ReceivedNotifications + "'");
    }

    public static String getLikeCount(int postId, int profileId){
        if (profileId < 0)
            return ("select count(*) from Notifications where postId = " +
                String.valueOf(postId) + " and type = 'L'");

        return ("select count(*) " +
                "from Notifications " +
                "where postId = " + String.valueOf(postId) + " and type = 'L' and senderId = " + String.valueOf(profileId));
    }

    public static String getCommentCount(int postId, int profileId){
        if (profileId < 0)
            return ("select count(*) from Notifications where postId = " +
                    String.valueOf(postId) + " and type = 'C'");

        return ("select count(*) " +
                "from Notifications " +
                "where postId = " + String.valueOf(postId) + " and type = 'C' and senderId = " + String.valueOf(profileId));
    }

    public static String getShareCount(int postId, int profileId){
        if (profileId < 0)
            return ("select count(*) from Notifications where postId = " +
                    String.valueOf(postId) + " and type = 'S'");

        return ("select count(*) " +
                "from Notifications " +
                "where postId = " + String.valueOf(postId) + " and type = 'S' and senderId = " + String.valueOf(profileId));
    }

    public static String getAllComment(int postId, int profileId){
        if (profileId < 0) return ("select * " +
                        "from Notifications " +
                        "where postId = " +
                        String.valueOf(postId) + " and type = 'C' order by ID desc");

        return ("select * " +
                "from Notifications " +
                "where ID in ( select notificationId " +
                "from ReceivedNotifications " +
                "where receiverId = " + String.valueOf(profileId) + ")  order by ID desc" );
    }



    public static String findProfile(String gmail, String password){
        return ("select * " +
                "from Profiles " +
                "where gmail like '" + gmail + "' and password like '" + password + "'");
    }

    public static String findNotficicationId(int postId, int senderId, char type){
        return ("select ID " +
                "from Notifications " +
                "where postId = " + String.valueOf(postId) + " and senderId = " + String.valueOf(senderId) + " and type = '" + type + "'");
    }

    public static String insertProfile(int ID, int backgroundImg, int avatar, String name, String gmail, String password){

        return ("insert into Profiles (ID, backgroundImg, avatar, name, gmail, password) values (" +
                String.valueOf(ID) + ", " +
                String.valueOf(backgroundImg) + ", " +
                String.valueOf(avatar) + ", " +
                "'"+ name + "', " +
                "'"+ gmail + "', " +
                "'"+ password +  "')" );
    }

    public static String editProfile(int ID, int backgroundImg, int avatar, String name, String gmail, String password){

        return ("update Profiles " +
                "set " +
                "backgroundImg = " + String.valueOf(backgroundImg) + ", " +
                "avatar = " + String.valueOf(avatar) + ", " +
                "name = '" + name  + "', " +
                "gmail = '" + gmail + "', " +
                "password = '" + password + "' " +
                "where " +
                "ID = " + String.valueOf(ID));
    }

    public static String insertPost(int ID, int ownerId, String status, String text, int image){

        if (status == null || status == "") status = getStatus();

        return ("insert into Posts (ID, ownerId, status, text, image) values (" +
                String.valueOf(ID) + ", " +
                String.valueOf(ownerId) + ", " +
                "'"+ status + "', " +
                "'"+ text + "', " +
                String.valueOf(image) + ")" );
    }

    public static String insertNotification(int ID, int postId, int senderId, char type, String status, String comment){

        status = getStatus();

        return ("insert into Notifications(ID, postId, senderId, type, status, comment) values (" +
                String.valueOf(ID) + ", " +
                String.valueOf(postId) + ", " +
                String.valueOf(senderId) + ", " +
                "'"+ type + "', " +
                "'"+ status + "', " +
                "'"+ comment + "')" );
    }



    public static String insertReceivedNotification(int receiverId, int notificationId){
        return ("insert into ReceivedNotifications (receiverId, notificationId) values (" +
                String.valueOf(receiverId) + ", " +
                String.valueOf(notificationId) + ")" );
    }

    public static String removeNotification(int ID){
        return ("delete from Notifications where ID = " + String.valueOf(ID));
    }

    public static String removeReceivedNotification(int receiverId,int ID) {
        return ("delete from ReceivedNotifications  where notificationId = " + String.valueOf(ID) + " and receiverId = " + String.valueOf(receiverId));
    }
}
