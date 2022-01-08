package com.example.cookingbookonline.home;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cookingbookonline.MainActivity;
import com.example.cookingbookonline.R;
import com.example.cookingbookonline.database.DatabaseHelper;
import com.example.cookingbookonline.model.HomesAdapter;
import com.example.cookingbookonline.model.Post;
import com.example.cookingbookonline.model.Profile;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements HomesAdapter.ItemClickListener {

    static public final String KEY_HOME_FRAGMENT = "key_home_fragment";
    static public final int interfaceID = R.id.bot_nav_home;

    RecyclerView recyclerView;

    static List<Post> post = null;

    Context context;

    ItemClickListener listener;

    Profile userProfile;

    public interface ItemClickListener {
        void OnShowFullPost(Post post);
        void OnGoToPersonalProfie(int profileId);
        void OnCreateNotification(Post post, char type);
        void OnRemoveNotification(int receiverId, int postId, char type);
    }

    public static HomeFragment newInstance(List<Post> post, Profile userProfile){
        HomeFragment fragment = new HomeFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(KEY_HOME_FRAGMENT, (ArrayList<? extends Parcelable>) post);
        bundle.putParcelable(KEY_HOME_FRAGMENT + "2", userProfile);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof ItemClickListener){
            listener = (ItemClickListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement ItemClickListener");
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

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        context = view.getContext();

        recyclerView = view.findViewById(R.id.home_recyclerView);

        if (getArguments() != null) {
            post = getArguments().getParcelableArrayList(KEY_HOME_FRAGMENT);
            userProfile = getArguments().getParcelable(KEY_HOME_FRAGMENT + "2");
        }

        HomesAdapter myAdapter = new HomesAdapter(view.getContext(), post, userProfile, this);
        recyclerView.setAdapter(myAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        return view;
    }

    @Override
    public void OnShowMore(Post post) {
        listener.OnShowFullPost(post);
        //Toast toast = Toast.makeText(context, post.profile.name, Toast.LENGTH_SHORT);
        //toast.show();
    }

    @Override
    public void OnShowPersonalProfile(int profileId) {
        listener.OnGoToPersonalProfie(profileId);
    }

    @Override
    public void OnCreateNotification(Post post, char type) {
        listener.OnCreateNotification(post, type);
    }

    @Override
    public void OnRemoveNotification(int receiverId, int postId, char type) {
        listener.OnRemoveNotification(receiverId, postId, type);
    }
}
