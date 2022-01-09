package com.example.bruhfiness.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Post implements Parcelable {
    static public final String KEY_POST = "post_key";

    public int ID;
    public int profileId;
    public int profileAvatar;
    public String profileName;
    public int image, likeNum, commentNum, shareNum;
    public String status, text;
    public Notification[] notification;
    public boolean liked, shared;

    public Post(){

    }

    public  Post(JSONObject data) throws JSONException {
        ID = data.getInt("ID");
        profileId = data.getInt("ownerId");
        status = data.getString("status");
        text = data.getString("text");
        image = data.getInt("image");
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject data = new JSONObject();

        data.put("ID", ID);
        data.put("ownerId", profileId);
        data.put("status", status);
        data.put("text", text);
        data.put("image", image);

        return data;
    }

    public Map<String, String> toHashMap()  {
        Map<String, String> data = new HashMap<>();

        data.put("ID", String.valueOf(ID));
        data.put("ownerId", String.valueOf(profileId));
        data.put("status", status);
        data.put("text", text);
        data.put("image", String.valueOf(image));

        return data;
    }

    public Post(int ID, int profileId, String status, String text, int image){
        this.ID = ID;
        this.profileId = profileId;
        this.status = status;
        this.text = text;
        this.image = image;
    }

    public Post (int ID, int profileId, int profileAvatar, String profileName, String status, String text,
          int image, int likeNum, int commentNum, int shareNum,
          Notification[] notification,
                 boolean liked, boolean shared){
        this.ID = ID;
        this.profileId = profileId;
        this.profileAvatar = profileAvatar;
        this.profileName = profileName;
        this.status = status;
        this.text = text;
        this.image = image;
        this.likeNum = likeNum;
        this.commentNum = commentNum;
        this.shareNum = shareNum;
        this.notification = notification;
        this.liked = liked;
        this.shared = shared;
    }


    protected Post(Parcel in) {
        ID = in.readInt();
        profileId = in.readInt();
        profileAvatar = in.readInt();
        profileName = in.readString();
        image = in.readInt();
        likeNum = in.readInt();
        commentNum = in.readInt();
        shareNum = in.readInt();
        status = in.readString();
        text = in.readString();
        liked = in.readByte() != 0;
        shared = in.readByte() != 0;
    }

    public static final Creator<Post> CREATOR = new Creator<Post>() {
        @Override
        public Post createFromParcel(Parcel in) {
            return new Post(in);
        }

        @Override
        public Post[] newArray(int size) {
            return new Post[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(ID);
        dest.writeInt(profileId);
        dest.writeInt(profileAvatar);
        dest.writeString(profileName);
        dest.writeInt(image);
        dest.writeInt(likeNum);
        dest.writeInt(commentNum);
        dest.writeInt(shareNum);
        dest.writeString(status);
        dest.writeString(text);
        dest.writeByte((byte) (liked ? 1 : 0));
        dest.writeByte((byte) (shared ? 1 : 0));
    }
}
