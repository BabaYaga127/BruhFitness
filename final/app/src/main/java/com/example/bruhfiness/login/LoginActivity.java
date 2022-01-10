package com.example.bruhfiness.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bruhfiness.MainActivity;
import com.example.bruhfiness.R;
import com.example.bruhfiness.database.DataApi;
import com.example.bruhfiness.database.DatabaseHelper;
import com.example.bruhfiness.home.HomeActivity;
import com.example.bruhfiness.model.Profile;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {


    static public final String KEY_LOGINSTATE = "loginstate";
    static public final String KEY_LOGIN_REMEBERME = "login_rememberme";

    static public final String loginPromt = "Don't have an account? register";
    static public final String registerPromt = "return to login";

    boolean loginState = true;
    boolean showpassword = false;

    TextView title, promt;

    EditText name, gmail, password, password2;

    Button  login;
    CheckBox remember;

    Context context;

    Profile userProfile;

    static DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(com.example.bruhfiness.R.layout.activity_login);

        context = this;

        dbHelper = new DatabaseHelper(this, new DatabaseHelper.LoadingListener() {
            @Override
            public void OnLoading() {

            }

            @Override
            public void OnFinsishLoading() {

            }
        });

        // get loginstate passed from main activity
        Intent intent = getIntent();
        loginState = intent.getBooleanExtra(KEY_LOGINSTATE, false);
        userProfile = intent.getParcelableExtra(MainActivity.KEY_USER_PROFILE);

        title = this.findViewById(R.id.login_title);

        name = this.findViewById(R.id.login_name);
        gmail = this.findViewById(R.id.login_gmail);
        password = this.findViewById(R.id.login_password);
        password2 = this.findViewById(R.id.login_password2);

        remember = this.findViewById(R.id.login_rememberMe);
        remember.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //
            }
        });

        login = this.findViewById(R.id.login_loginButton);

        promt = this.findViewById(R.id.login_promt);

        Context context = this;



        // toogle password button
        ImageButton toogleButton = this.findViewById(R.id.login_tooglePassword);
        toogleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showpassword = !showpassword;
                if (showpassword){
                    toogleButton.setImageResource(R.drawable.ic_baseline_remove_red_eye_24);
                    password.setTransformationMethod(null);
                    password2.setTransformationMethod(null);
                } else {
                    toogleButton.setImageResource(R.drawable.ic_baseline_security_24);
                    password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    password2.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });

        View view = this.findViewById(R.id.login_main_layout);

        // login or register button
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (loginState) {

                    String str_gmail, str_pass;
                    str_gmail = gmail.getText().toString();
                    str_pass = password.getText().toString();

                    if (str_gmail.length() > 0 && str_pass.length() > 0){
                        // check login infomation
                        //...
                        userProfile = dbHelper.findProfile(str_gmail, str_pass);

                        if (userProfile != null) {
                            userProfile.user_remember = remember.isChecked();
                            MainActivity.SaveData(context, userProfile);
                            ToHomePage();
                        } else {
                            Toast toast = Toast.makeText(context, "no login infomation found", Toast.LENGTH_SHORT);
                            toast.show();
                        }

                    } else {
                        Toast toast = Toast.makeText(context, "all boxes must be filled", Toast.LENGTH_LONG);
                        toast.show();
                    }
                } else {

                    String str_name, str_gmail, str_pass, str_pass2;
                    str_name = name.getText().toString();
                    str_gmail = gmail.getText().toString();
                    str_pass = password.getText().toString();
                    str_pass2 = password2.getText().toString();

                    if (str_name.length() > 0 && str_gmail.length() > 0 && str_pass.length() > 0 && str_pass2.length() > 0){
                        if (str_pass.equals(str_pass2)){
                            loginState = true;
                            userProfile = new Profile(-1);
                            userProfile.name = str_name;
                            userProfile.gmail = str_gmail;
                            userProfile.password = str_pass;
                            userProfile.ID = dbHelper.addProfile(userProfile);

                            DataApi api = new DataApi(context);

                            try {
                                dbHelper.InsertProfileToServer(userProfile);

                                DatabaseHelper.CheckAndDeleteDatabase(context);
                                dbHelper = new DatabaseHelper(context, new DatabaseHelper.LoadingListener() {
                                    @Override
                                    public void OnLoading() {
                                        view.setVisibility(View.GONE);
                                    }

                                    @Override
                                    public void OnFinsishLoading() {
                                        view.setVisibility(View.VISIBLE);
                                        ChangeLayout();
                                    }
                                });

                                dbHelper.getWritableDatabase();

                            } catch (Exception e){

                            }



                            //Toast toast = Toast.makeText(context, "register " + String.valueOf(userProfile.ID) + " successful, return to login", Toast.LENGTH_LONG);
                            //toast.show();
                        } else {
                            Toast toast = Toast.makeText(context, "2 passwords don't match each other", Toast.LENGTH_LONG);
                            toast.show();
                        }
                    } else {
                        Toast toast = Toast.makeText(context, "all boxes must be filled", Toast.LENGTH_LONG);
                        toast.show();
                    }

                }
            }
        });

        // promt button
        promt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginState = !loginState;
                ChangeLayout();
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

    private void ChangeLayout(){
        if (loginState){
            title.setText("Login");

            name.setVisibility(View.GONE);
            password2.setVisibility(View.GONE);

            remember.setVisibility(View.VISIBLE);
            login.setText("Login");

            promt.setText(loginPromt);

        } else {
            title.setText("Register");

            name.setVisibility(View.VISIBLE);
            name.setText("");
            gmail.setText("");

            password2.setVisibility(View.VISIBLE);
            password.setText("");
            password2.setText("");

            remember.setVisibility(View.GONE);
            login.setText("Register");

            promt.setText(registerPromt);
        }
    }

    public void ToHomePage() {

        userProfile.user_remember = remember.isChecked();
        Intent intent = new Intent(this, HomeActivity.class);
        intent.putExtra(MainActivity.KEY_USER_PROFILE, userProfile);
        startActivity(intent);
        // kill the activity
        finish();
    }
}