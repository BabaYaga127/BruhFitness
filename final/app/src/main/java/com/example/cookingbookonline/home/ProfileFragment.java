package com.example.cookingbookonline.home;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.example.cookingbookonline.model.ProfileAdapter;

import java.util.List;

public class ProfileFragment extends Fragment implements ProfileAdapter.ItemClickListener {

    static public final String KEY_PROFILE_FRAGMENT = "key_profile_fragment";
    static public final int interfaceID = R.id.bot_nav_profile;

    RecyclerView recyclerView;

    List<Post> posts;

    Profile showProfile;

    Profile userProfile;

    ItemClickListener listener;

    DatabaseHelper dbHelper = MainActivity.dbHelper;

    public interface ItemClickListener{
        void OnShowFullPost(Post post);
        void OnCreateNotification(Post post, char type);
        void OnRemoveNotification(int receiverId, int postId, char type);
    }

    public static ProfileFragment newInstance(Profile showProfile, Profile userProfile){
        ProfileFragment fragment = new ProfileFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(KEY_PROFILE_FRAGMENT, showProfile);
        bundle.putParcelable(KEY_PROFILE_FRAGMENT + "2", userProfile);
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
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        if (getArguments() != null){
            showProfile = getArguments().getParcelable(KEY_PROFILE_FRAGMENT);
            userProfile = getArguments().getParcelable(KEY_PROFILE_FRAGMENT + "2");
        }


        posts = dbHelper.getAllPost(showProfile.ID, userProfile.ID);

        recyclerView = view.findViewById(R.id.profile_recyclerView);

        ProfileAdapter myAdapter = new ProfileAdapter(view.getContext(),showProfile, userProfile, posts, this);
        recyclerView.setAdapter(myAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        return view;
    }

    @Override
    public void OnShowMore(Post post) {
        listener.OnShowFullPost(post);
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
