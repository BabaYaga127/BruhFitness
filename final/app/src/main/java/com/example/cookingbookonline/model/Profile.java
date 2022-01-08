package com.example.cookingbookonline.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.example.cookingbookonline.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Profile implements Parcelable {

    public int ID, backgroundImg, avatar;
    public String name, gmail, password;

    public boolean user_remember = false;

    static final public Profile[] data = {
            new Profile(0),
            new Profile(1),
            new Profile(2),
            new Profile(3),
            new Profile(4),
            new Profile(5),
            new Profile(6),
            new Profile(7),
            new Profile(8),
            new Profile(9),
            new Profile(10)
    };

    public Profile(JSONObject object) throws JSONException {
        ID = object.getInt("ID");
        backgroundImg = object.getInt("backgroundImg");
        avatar = object.getInt("avatar");
        name = object.getString("name");
        gmail = object.getString("gmail");
        password = object.getString("password");
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject object = new JSONObject();
        object.put("backgroundImg", backgroundImg);
        object.put("avatar", avatar);
        object.put("name", name);
        object.put("gmail", gmail);
        object.put("password", password);

        return object;
    }

    public Map<String, String> toHashMap()  {
        Map<String, String> object = new HashMap<>();
        object.put("backgroundImg", String.valueOf(backgroundImg));
        object.put("avatar", String.valueOf(avatar));
        object.put("name", name);
        object.put("gmail", gmail);
        object.put("password", password);

        return object;
    }

    public Profile(int ID){
        this.ID = ID;
        //get existed profile from database

        name = "Default Name" + (char)(ID + 48);
        gmail = (char)(ID + 48) + "mymail@gmail.com";
        backgroundImg = R.drawable.ic_baseline_home_24;
        avatar = R.drawable.ic_baseline_person_24;
        password = "password";
        user_remember = false;
    }

    public Profile(int ID, int backgroundImg, int avatar, String name, String gmail, String password){
        this.ID = ID;
        this.backgroundImg = backgroundImg;
        this.avatar = avatar;
        this.name = name;
        this.gmail = gmail;
        this.password = password;
    }

    public Profile(@NonNull Profile profile){
        this.backgroundImg = profile.backgroundImg;
        this.avatar = profile.avatar;
        this.name = profile.name;
        this.gmail = profile.gmail;
        this.password = profile.password;
    }

    protected Profile(Parcel in) {
        ID = in.readInt();
        backgroundImg = in.readInt();
        avatar = in.readInt();
        name = in.readString();
        gmail = in.readString();
        password = in.readString();
        user_remember = in.readByte() != 0;
    }

    public static final Creator<Profile> CREATOR = new Creator<Profile>() {
        @Override
        public Profile createFromParcel(Parcel in) {
            return new Profile(in);
        }

        @Override
        public Profile[] newArray(int size) {
            return new Profile[size];
        }
    };

    public void Copy(@NonNull Profile profile){
        this.backgroundImg = profile.backgroundImg;
        this.avatar = profile.avatar;
        this.name = profile.name;
        this.gmail = profile.gmail;
        this.password = profile.password;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(ID);
        dest.writeInt(backgroundImg);
        dest.writeInt(avatar);
        dest.writeString(name);
        dest.writeString(gmail);
        dest.writeString(password);
        dest.writeByte((byte) (user_remember ? 1 : 0));
    }
}
