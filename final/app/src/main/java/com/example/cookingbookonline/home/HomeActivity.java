package com.example.cookingbookonline.home;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.cookingbookonline.MainActivity;
import com.example.cookingbookonline.R;
import com.example.cookingbookonline.database.DatabaseHelper;
import com.example.cookingbookonline.model.Notification;
import com.example.cookingbookonline.model.Post;
import com.example.cookingbookonline.model.Profile;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class HomeActivity extends AppCompatActivity implements SettingFragment.SettingListener, HomeFragment.ItemClickListener, ProfileFragment.ItemClickListener,
                                                                PostFragment.ItemClickListener, NewPostFragment.ItemClickListener, NotificationFragment.ItemClickListener{

    static public Profile userProfile;

    ImageButton newPost;

    DatabaseHelper dbHeper;

    Context context;

    static boolean firstTime = true;
    static boolean profilechange = false;
    static boolean postnewpost = false;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        context = this;

        // get userProfile
        Intent intent = getIntent();
        userProfile = intent.getParcelableExtra(MainActivity.KEY_USER_PROFILE);

        newPost = this.findViewById(R.id.home_toolbar_newpostButton);

        newPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetTrackableFragmentContainer2(NewPostFragment.newInstance(userProfile), NewPostFragment.interfaceID);
                //OnPreLayoutChangeHandle(NewPostFragment.interfaceID, context);

            }
        });

        dbHeper = new DatabaseHelper(context, new DatabaseHelper.LoadingListener() {
            @Override
            public void OnLoading() {

            }

            @Override
            public void OnFinsishLoading() {

            }
        });

        //Toast toats = Toast.makeText(this, "home: " + userProfile.name + " " + userProfile.gmail, Toast.LENGTH_LONG);
        //toats.show();

        // initialize first appearance
        //SetTrackableFragmentContainer(HomeFragment.newInstance( dbHeper.getAllPost(-1, userProfile.ID), userProfile ), HomeFragment.interfaceID);
        OnPreLayoutChangeHandle(R.id.bot_nav_home, context);

        // bottom navigation bar
        BottomNavigationView bottomNav = (BottomNavigationView) findViewById(R.id.bottom_navigation);

        bottomNav.setOnItemSelectedListener( new NavigationBarView.OnItemSelectedListener()
        {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int id = item.getItemId();

                OnPreLayoutChangeHandle(id, context);

                return true;
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbHeper.close();
    }

    private void OnPreLayoutChangeHandle(int id, Context context){

        if (!firstTime && (id == R.id.bot_nav_home || id == R.id.bot_nav_notification || profilechange || postnewpost)){

            postnewpost = false;
            profilechange = false;

            DatabaseHelper.CheckAndDeleteDatabase(context);

            View view = this.findViewById(R.id.home_main_layout);

            dbHeper = new DatabaseHelper(context, new DatabaseHelper.LoadingListener() {
                @Override
                public void OnLoading() {
                    //Toast.makeText(context, "Reloading", Toast.LENGTH_LONG).show();
                    view.setVisibility(View.GONE);
                }

                @Override
                public void OnFinsishLoading() {
                    //Toast.makeText(context, "Done reloading", Toast.LENGTH_LONG).show();
                    view.setVisibility(View.VISIBLE);

                    SetTrackableFragmentContainer(id);
                }
            });

            dbHeper.getWritableDatabase();
        } else {
            firstTime = false;
            SetTrackableFragmentContainer(id);
        }


    }

    private void SetTrackableFragmentContainer(int id){
        Fragment selectedFragment = null;

        // every fragment id is the id of the button that lead to that fragment
        // button  HomeFragment.interfaceID = R.id.bot_nav_home
        switch (id){
            case R.id.bot_nav_home:
                selectedFragment = HomeFragment.newInstance( dbHeper.getAllPost(-1, userProfile.ID), userProfile );
                break;
            case R.id.bot_nav_notification:
                selectedFragment =  NotificationFragment.newInstance(userProfile.ID);
                break;
            case R.id.bot_nav_profile:
                selectedFragment = ProfileFragment.newInstance(userProfile, userProfile);
                break;
            case R.id.bot_nav_setting:
                selectedFragment = new SettingFragment();
                break;
        }

        SetTrackableFragmentContainer2(selectedFragment, id);

    }

    private void SetTrackableFragmentContainer2(Fragment fragment, int id){
        int commited = getSupportFragmentManager().beginTransaction().replace(R.id.home_fragment_container, fragment).addToBackStack(String.valueOf(id)).commit();
    }

    private void GoBack(){
        getSupportFragmentManager().popBackStack(null, 0);
    }

    private void GoBack(int id){
        getSupportFragmentManager().popBackStack(String.valueOf(id), FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    @Override
    public void onLogout() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(MainActivity.KEY_USER_PROFILE, userProfile);
        startActivity(intent);
        // kill the activity
        finish();
    }

    @Override
    public void OnChangeUserProfile(Profile userProfile) {
        dbHeper.updateProfile(userProfile);
        profilechange = true;
    }

    @Override
    public void OnShowFullPost(Post post) {
        Fragment fragment = PostFragment.newInstance(post, userProfile);
        SetTrackableFragmentContainer2(fragment, R.id.post_showMore);

    }

    @Override
    public void OnGoToPersonalProfie(int profileId) {
        Fragment fragment = ProfileFragment.newInstance(dbHeper.getProfile(profileId), userProfile);
        SetTrackableFragmentContainer2(fragment, R.id.post_avatar);
    }

    @Override
    public void OnCreateNotification(Post post, char type) {
        Notification notification = new Notification();
        notification.type = type;
        notification.postId = post.ID;
        notification.profileId = userProfile.ID;
        notification.profileName = userProfile.name;
        notification.profileAvatar = userProfile.avatar;
        notification.comment = "now";

        dbHeper.addNotification(notification, post.profileId);
    }

    @Override
    public void OnCreateComment(Post post, String text) {
        Notification comment = new Notification();
        comment.type = 'C';
        comment.postId = post.ID;
        comment.profileId = userProfile.ID;
        comment.profileName = userProfile.name;
        comment.profileAvatar = userProfile.avatar;
        comment.status = "now";
        comment.comment = text;


        //Toast toast = Toast.makeText(this, comment.comment, Toast.LENGTH_SHORT);
        //toast.show();

        dbHeper.addNotification(comment, post.profileId);
    }

    @Override
    public void OnRemoveNotification(int receiverId, int postId, char type) {
        int senderId = userProfile.ID;
        //Toast toast = Toast.makeText(this, String.valueOf(postId) + " " + String.valueOf(senderId) + " " + type, Toast.LENGTH_LONG);
        //toast.show();
        dbHeper.removeNotification(receiverId,postId, senderId, type);
    }

    @Override
    public void OnGoBack() {
        GoBack();
    }

    @Override
    public void OnGoBack(int id) {
        GoBack(id);
    }

    @Override
    public void OnPostNewPost(Post post) {
        //Post new post
        dbHeper.addPost(post);
        postnewpost = true;
        OnPreLayoutChangeHandle(R.id.bot_nav_home, context);
    }
}