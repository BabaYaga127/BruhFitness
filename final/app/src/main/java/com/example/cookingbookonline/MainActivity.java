package com.example.cookingbookonline;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.icu.text.UnicodeSetSpanner;
import android.os.Bundle;
import android.os.strictmode.SqliteObjectLeakedViolation;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.StringRequest;
import com.example.cookingbookonline.database.DatabaseHelper;
import com.example.cookingbookonline.home.HomeActivity;
import com.example.cookingbookonline.login.LoginActivity;
import com.example.cookingbookonline.model.Post;
import com.example.cookingbookonline.model.Profile;

import java.io.File;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    static public final String KEY_USER_PROFILE = "user_profile";
    static public final String KEY_USER_ID = "user_id";
    static public final String KEY_USER_BACKGROUNDIMG = "user_backgroundImg";
    static public final String KEY_USER_AVATAR = "user_avatar";
    static public final String KEY_USER_NAME = "user_name";
    static public final String KEY_USER_GMAIL = "user_gmail";
    static public final String KEY_USER_PASSWORD = "user_password";
    static public final String SHARE_PREFERENCE = "CBO_share_preferences";

    LinearLayout loginShortCutLayout;
    ImageView avatar;
    TextView loginPromt, login, register;

    Profile userProfile;

    static public DatabaseHelper dbHelper;
    static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;

        if (dbHelper == null){

            //=====================================================================================
            // Delete old database
            if (DatabaseHelper.onCreatedCalled == false){
                DatabaseHelper.CheckAndDeleteDatabase(context);
            }
            //==========================================================================================

            View view = this.findViewById(R.id.main_main_layout);

            dbHelper = new DatabaseHelper(this, new DatabaseHelper.LoadingListener() {
                @Override
                public void OnLoading() {
                    view.setVisibility(View.GONE);
                }
                @Override
                public void OnFinsishLoading() {
                    view.setVisibility(View.VISIBLE);
                }
            });

            dbHelper.getWritableDatabase();
        }

        userProfile = getIntent().getParcelableExtra(KEY_USER_PROFILE);
        //if (userProfile != null && userProfile.ID >= 0) SaveData(context, userProfile);
        if (userProfile == null ) LoadData();

        loginShortCutLayout = this.findViewById(R.id.main_remenberLayout);
        avatar = this.findViewById(R.id.main_avatar);
        loginPromt = this.findViewById(R.id.main_loginPromt);
        login = this.findViewById(R.id.main_login);
        register = this.findViewById(R.id.main_register);

        context = this;

        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get login infomation
                // ...

                Intent intent = new Intent(context, HomeActivity.class);
                intent.putExtra(KEY_USER_PROFILE, userProfile);
                startActivity(intent);
            }
        });

        loginPromt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToLoginPage(true);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToLoginPage(true);
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToLoginPage(false);
            }
        });

        ChangeLayout();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null){
            dbHelper.close();
        }
    }

    public void deleteDirectory(File file) {
        if( file.exists() ) {
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                for(int i=0; i<files.length; i++) {
                    if(files[i].isDirectory()) {
                        deleteDirectory(files[i]);
                    }
                    else {
                        files[i].delete();
                    }
                }
            }
            file.delete();
        }
    }

    public boolean checkDatabase(){

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

    private void ChangeLayout(){

        if (userProfile != null && userProfile.user_remember){

            if (userProfile.backgroundImg < R.drawable.ic_baseline_remove_red_eye_24){
                userProfile.backgroundImg = R.drawable.ic_baseline_home_24;
            }
            if (userProfile.avatar < R.drawable.ic_baseline_remove_red_eye_24){
                userProfile.avatar = R.drawable.ic_baseline_person_24;
            }

            loginShortCutLayout.setVisibility(View.VISIBLE);
            avatar.setImageResource(userProfile.avatar);
            login.setVisibility(View.GONE);
        } else {
            loginShortCutLayout.setVisibility(View.GONE);
            login.setVisibility(View.VISIBLE);
        }
    }

    static public void SaveData(Context context, Profile userProfile){
        // save data internaly
        //SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.SHARE_PREFERENCE, MODE_PRIVATE);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        SharedPreferences.Editor editor = sharedPreferences.edit();

//        Toast toats = Toast.makeText(this, "login infomation saved" , Toast.LENGTH_LONG);
//        toats.show();

        editor.putInt(KEY_USER_ID, userProfile.ID);
        editor.putInt(KEY_USER_BACKGROUNDIMG, userProfile.backgroundImg);
        editor.putInt(KEY_USER_AVATAR, userProfile.avatar);
        editor.putString(KEY_USER_NAME, userProfile.name);
        editor.putString(KEY_USER_GMAIL, userProfile.gmail);
        editor.putString(KEY_USER_PASSWORD, userProfile.password);
        editor.putBoolean(LoginActivity.KEY_LOGIN_REMEBERME, userProfile.user_remember);

        editor.commit();
    }

    private void LoadData(){
        //SharedPreferences sharedPreferences = getSharedPreferences(SHARE_PREFERENCE, MODE_PRIVATE);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        userProfile = new Profile(0);

        //Toast toats1 = Toast.makeText(this, "before: " + userProfile.name + " " + userProfile.gmail, Toast.LENGTH_LONG);
        //toats1.show();

        userProfile.user_remember = sharedPreferences.getBoolean(LoginActivity.KEY_LOGIN_REMEBERME, false);
        if (!userProfile.user_remember) return;

        userProfile.ID = sharedPreferences.getInt(KEY_USER_ID, -1);
        userProfile.backgroundImg = sharedPreferences.getInt(KEY_USER_BACKGROUNDIMG, R.drawable.ic_baseline_home_24);
        userProfile.avatar = sharedPreferences.getInt(KEY_USER_AVATAR, R.drawable.ic_baseline_person_24);
        userProfile.name = sharedPreferences.getString(KEY_USER_NAME, "Name");
        userProfile.gmail = sharedPreferences.getString(KEY_USER_GMAIL, "mymail@gmail.com");
        userProfile.password = sharedPreferences.getString(KEY_USER_PASSWORD, "password");


        //Toast toats = Toast.makeText(this, "after: " + userProfile.name + " " + userProfile.gmail, Toast.LENGTH_LONG);
        //toats.show();
    }

    public static void test(){
        List<Profile> list = dbHelper.getAllProfile();
        Toast toats = Toast.makeText(context, String.valueOf(list.size()), Toast.LENGTH_LONG);
        toats.show();
    }

    public void ToLoginPage(boolean loginState) {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra(LoginActivity.KEY_LOGINSTATE, loginState);
        intent.putExtra(KEY_USER_PROFILE, userProfile);
        startActivity(intent);
    }
}