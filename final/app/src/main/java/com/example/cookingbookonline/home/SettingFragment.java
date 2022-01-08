package com.example.cookingbookonline.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.text.method.TransformationMethod;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.cookingbookonline.R;
import com.example.cookingbookonline.model.Profile;

public class SettingFragment extends Fragment {

    static public final int interfaceID = R.id.bot_nav_setting;

    static public final String KEY_SETTING_FRAGMENT = "key_setting_fragment";

    boolean showpassword = false;

    ImageView showBackground, showAvatar;
    TextView showName, showGmail;

    EditText name, gmail, password, newPassword, newPassword2;

    Profile userProfile, tempProfile;

    SettingListener listener;

    public interface SettingListener {
        void onLogout();
        void OnChangeUserProfile(Profile userProfile);
    }

    public static SettingFragment newInstance(Profile userProfile) {

        Bundle args = new Bundle();
        args.putParcelable(KEY_SETTING_FRAGMENT, userProfile);

        SettingFragment fragment = new SettingFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof SettingListener){
            listener = (SettingListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement SettingListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);

        // get profile
        if (getArguments() != null){
            userProfile = getArguments().getParcelable(KEY_SETTING_FRAGMENT);
        } else {
            userProfile = HomeActivity.userProfile;
        }

        tempProfile = new Profile(-1);
        tempProfile.Copy(userProfile);

        showBackground = view.findViewById(R.id.setting_showBackgroundImg);
        showAvatar = view.findViewById(R.id.setting_showAvatarImg);
        showName = view.findViewById(R.id.setting_showName);
        showGmail = view.findViewById(R.id.setting_showGmail);

        name = view.findViewById(R.id.setting_name);
        gmail = view.findViewById(R.id.setting_gmail);
        password = view.findViewById(R.id.setting_password);
        newPassword = view.findViewById(R.id.setting_newPassword);
        newPassword2 = view.findViewById(R.id.setting_newPassword2);

        TextView logout = view.findViewById(R.id.setting_logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onLogout();
            }
        });

        // changeName button
        Button changeNameButton = view.findViewById(R.id.setting_changeName);
        changeNameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetName(name.getText().toString());
            }
        });

        // changeGmail button
        Button changeGmailButton = view.findViewById(R.id.setting_changeGmail);
        changeGmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetGmail(gmail.getText().toString());
            }
        });

        // changePassWord button
        Button changePasswordButton = view.findViewById(R.id.setting_changePassword);
        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str_pass, str_pass2, str_pass3;
                str_pass = password.getText().toString();
                str_pass2 = newPassword.getText().toString();
                str_pass3 = newPassword2.getText().toString();

                if (userProfile.password.equals(str_pass)){
                    if (str_pass2.equals(str_pass3)){
                        SetPassword(str_pass2);
                    } else {
//                        Toast toast = Toast.makeText(view.getContext(), "new passwords don't match each other", Toast.LENGTH_SHORT);
//                        toast.show();
                    }
                } else {
//                    Toast toast = Toast.makeText(view.getContext(), "incorect old password", Toast.LENGTH_LONG);
//                    toast.show();
                }
            }
        });

        // toogle show password button
        ImageButton toogleButton = view.findViewById(R.id.setting_togglePassword);
        toogleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showpassword = !showpassword;
                if (showpassword){
                    toogleButton.setImageResource(R.drawable.ic_baseline_remove_red_eye_24);
                    password.setTransformationMethod(null);
                    newPassword.setTransformationMethod(null);
                    newPassword2.setTransformationMethod(null);
                } else {
                    toogleButton.setImageResource(R.drawable.ic_baseline_security_24);
                    password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    newPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    newPassword2.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });


        // cancel button
        Button cancelButton = view.findViewById(R.id.setting_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowProfile();
                tempProfile.Copy(userProfile);
            }
        });

        // save button
        Button saveButton = view.findViewById(R.id.setting_save);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userProfile.Copy(tempProfile);
                ShowProfile();
                listener.OnChangeUserProfile(userProfile);
            }
        });

        ShowProfile();

        return view;
    }

    void ShowProfile(){
        showBackground.setImageResource(userProfile.backgroundImg);
        showAvatar.setImageResource(userProfile.avatar);
        showName.setText(userProfile.name);
        showGmail.setText(userProfile.gmail);

        // clear editText
        name.setText("");
        gmail.setText("");
        password.setText("");
        newPassword.setText("");
        newPassword2.setText("");
    }

    void SetBackGroundImg(int backgroundImg){
        tempProfile.backgroundImg = backgroundImg;
        showBackground.setImageResource(backgroundImg);
    }

    void SetAvatar(int avatar){
        tempProfile.avatar = avatar;
        showAvatar.setImageResource(avatar);
    }

    void SetName(String name){
        tempProfile.name = name;
        showName.setText(name);
    }

    void SetGmail(String gmail){
        tempProfile.gmail = gmail;
        showGmail.setText(gmail);
    }

    void SetPassword(String password){
        tempProfile.password = password;
    }
}
