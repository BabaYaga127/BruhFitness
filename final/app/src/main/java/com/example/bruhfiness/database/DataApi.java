package com.example.bruhfiness.database;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.example.bruhfiness.model.Notification;
import com.example.bruhfiness.model.Post;
import com.example.bruhfiness.model.Profile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataApi {

    static public String URL_GET_PROFILES = "https://babayaga1279.pythonanywhere.com/api/Profiles/?format=json";
    static public String URL_GET_A_PROFILES = "https://babayaga1279.pythonanywhere.com/api/Profiles";
    static public String URL_GET_POSTS = "https://babayaga1279.pythonanywhere.com/api/Posts/?format=json";
    static public String URL_GET_NOTIFICATIONS = "https://babayaga1279.pythonanywhere.com/api/Notifications/?format=json";
    static public String URL_GET_A_NOTIFICATIONS = "https://babayaga1279.pythonanywhere.com/api/Notifications";
    static public String URL_GET_RECEIVEDNOTIFICATIONS = "https://babayaga1279.pythonanywhere.com/api/ReceivedNotifications/?format=json";
    static public String URL_GET_A_RECEIVEDNOTIFICATIONS = "https://babayaga1279.pythonanywhere.com/api/ReceivedNotifications";
    static public String URL_GET_FORMAT = "?format=json";

    Context context;

    public DataApi(Context context){
        this.context = context;
    }

    /*
     * ==========================================================================================================================================================================================================================================
     * PUT VS POST API
     *
     * */

    public void DeleteToServerRequest(String url, int ID){
        url = url + "/" + String.valueOf(ID) + "/" + URL_GET_FORMAT;

        //Toast.makeText(context, url, Toast.LENGTH_LONG).show();

        StringRequest dr = new StringRequest(Request.Method.DELETE, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        //Toast.makeText(context, response, Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error.
                        Toast.makeText(context, error.toString(), Toast.LENGTH_LONG).show();
                    }
                }
        );

        MySingleton.getInstance(context).addToRequestQueue(dr);
    }

    public void PutToServerRequest(String url, Profile data){

        url = url + "/" + String.valueOf(data.ID) + "/" + URL_GET_FORMAT;

        StringRequest putRequest = new StringRequest(Request.Method.PUT, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        //Toast.makeText(context, response.toString(), Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
        ) {

            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String> ();
                params.put("ID", String.valueOf(data.ID));
                params.put("backgroundImg", String.valueOf(data.backgroundImg));
                params.put("avatar", String.valueOf(data.avatar));
                params.put("name", data.name);
                params.put("gmail", data.gmail);
                params.put("password", data.password);

                return params;
            }

        };

        MySingleton.getInstance(context).addToRequestQueue(putRequest);
    }

    public void PostToServerRequest(String url, Map<String, String> data){

        //Toast.makeText(context, url, Toast.LENGTH_SHORT).show();

        StringRequest putRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        //Toast.makeText(context, response.toString(), Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
        ) {

            @Override
            protected Map<String, String> getParams()
            {
                return data;
            }

        };

        MySingleton.getInstance(context).addToRequestQueue(putRequest);
    }

    /*
    * ==========================================================================================================================================================================================================================================
    * Get API
    *
    * */

    public interface ProfilesResponseListener{
        void OnError(String message);
        void OnResponse(List<Profile> Profiles);
    }

    public void GetProfiles(ProfilesResponseListener listener){
        String url = URL_GET_PROFILES;

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                List<Profile> profiles = new ArrayList<>();

                for (int i = 0; i < response.length(); ++i){
                    try {
                        JSONObject object = response.getJSONObject(i);
                        Profile data = new Profile(object);
                        profiles.add(data);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                //Toast.makeText(context, "downloading ...", Toast.LENGTH_SHORT).show();
                listener.OnResponse(profiles);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, error.toString(), Toast.LENGTH_LONG).show();
                listener.OnError("can not request Profiles table " + error.toString());
            }
        });

        request.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 5000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 5000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });

        MySingleton.getInstance(context).addToRequestQueue(request);
    }

    public interface PostsResponseListener{
        void OnError(String message);
        void OnResponse(List<Post> Posts);
    }

    public void GetPosts(PostsResponseListener listener){
        String url = URL_GET_POSTS;

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                List<Post> posts = new ArrayList<>();

                for (int i = 0; i < response.length(); ++i){
                    try {
                        JSONObject object = response.getJSONObject(i);
                        Post data = new Post(object);
                        posts.add(data);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                //Toast.makeText(context, "downloading ...", Toast.LENGTH_SHORT).show();
                listener.OnResponse(posts);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, error.toString(), Toast.LENGTH_LONG).show();
                listener.OnError("can not request Posts table");
            }
        });

        MySingleton.getInstance(context).addToRequestQueue(request);
    }

    public interface NotificationsResponseListener{
        void OnError(String message);
        void OnResponse(List<Notification> notifications);
    }

    public void GetNotifications(NotificationsResponseListener listener, boolean receivedNotification){
        String url = URL_GET_NOTIFICATIONS;

        if (receivedNotification) url = URL_GET_RECEIVEDNOTIFICATIONS;

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                List<Notification> notifications = new ArrayList<>();

                for (int i = 0; i < response.length(); ++i){
                    try {
                        JSONObject object = response.getJSONObject(i);
                        Notification data;

                        if (receivedNotification) data = new Notification(object, i + 1);
                        else data = new Notification(object, 0);

                        notifications.add(data);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                //if (receivedNotification) Toast.makeText(context, "downloading ...", Toast.LENGTH_SHORT).show();
                //else Toast.makeText(context, "downloading ...", Toast.LENGTH_SHORT).show();
                listener.OnResponse(notifications);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, error.toString(), Toast.LENGTH_LONG).show();
                listener.OnError("can not request Notifications table");
            }
        });

        MySingleton.getInstance(context).addToRequestQueue(request);
    }

}
