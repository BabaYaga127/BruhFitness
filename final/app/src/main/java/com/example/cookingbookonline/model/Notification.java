package com.example.cookingbookonline.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Notification {
    public int ID;
    public int postId;
    public int profileId, profileAvatar;
    public String profileName;
    public char type;
    public String status, comment;

    public Notification(){
    }

    public Notification(JSONObject data, int receivedNotification) throws JSONException {
        if (receivedNotification == 0){
            ID = data.getInt("ID");
            postId = data.getInt("postId");
            profileId = data.getInt("senderId");
            type = data.getString("type").charAt(0);
            status = data.getString("status");
            comment = data.getString("comment");
        } else {
            ID = receivedNotification;
            profileId = data.getInt("receiverId");
            postId = data.getInt("notificationId");
        }

    }

    public JSONObject toJSON(boolean receivedNotification) throws JSONException {
        JSONObject data = new JSONObject();

        if (receivedNotification){
            data.put("receiverId", profileId);
            data.put("notificationId", postId);
        } else {
            data.put("ID", ID);
            data.put("postId", postId);
            data.put("senderId", profileId);
            data.put("type", type);
            data.put("status", status);
            data.put("comment", comment);
        }

        return data;
    }

    public Map<String, String> toHashMap(boolean receivedNotification, int receiverId)   {
        Map<String, String> data = new HashMap<>();

        if (receivedNotification){
            data.put("receiverId", String.valueOf(receiverId));
            data.put("notificationId", String.valueOf(ID));
        } else {
            data.put("postId", String.valueOf(postId));
            data.put("senderId", String.valueOf(profileId));
            data.put("type", String.valueOf(type));
            data.put("status", String.valueOf(status));
            data.put("comment", String.valueOf(comment));
        }

        return data;
    }

    public Notification(int ID, int postId, int profileId, char type, String status, String comment){
        this.ID = ID;
        this.postId = postId;
        this.profileId = profileId;
        this.type = type;
        this.status = status;
        this.comment = comment;
    }
}
