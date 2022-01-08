package com.example.cookingbookonline.home;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.cookingbookonline.R;
import com.example.cookingbookonline.model.Post;
import com.example.cookingbookonline.model.Profile;

public class NewPostFragment extends Fragment {

    static public final String KEY_NEW_POST_FRAGMENT = "key_new_post_fragment";
    //static public final int interfaceID = R.id.home_toolbar_newpostButton;

    Profile userProfile;

    TextView name, status;
    ImageView avatar, image, removeImg;
    EditText text;
    Button  uploadImg, cancel, post;

    boolean hasImage = false;

    ItemClickListener listener;

    Post tempPost;

    public interface ItemClickListener {
        void OnPostNewPost(Post post);
        void OnGoBack();
        void OnGoBack(int id);
    }

    static public NewPostFragment newInstance(Profile userProfile) {
        Bundle args = new Bundle();
        args.putParcelable(KEY_NEW_POST_FRAGMENT, userProfile);
        NewPostFragment fragment = new NewPostFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof ItemClickListener){
            listener = (ItemClickListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement ItemClicKListener");
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
        View view = inflater.inflate(R.layout.fragment_new_post, container, false);

        userProfile = getArguments().getParcelable(KEY_NEW_POST_FRAGMENT);
        tempPost = new Post();
        tempPost.profileId = userProfile.ID;
        tempPost.profileName = userProfile.name;
        tempPost.profileAvatar = userProfile.avatar;


        name = view.findViewById(R.id.new_post_name);
        status = view.findViewById(R.id.new_post_status);
        avatar = view.findViewById(R.id.new_post_avatar);
        image = view.findViewById(R.id.new_post_image);
        text = view.findViewById(R.id.new_post_text);
        removeImg = view.findViewById(R.id.new_post_removeImg);
        removeImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideRemoveButton();
            }
        });

        uploadImg = view.findViewById(R.id.new_post_uploadImage);
        uploadImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // find new image
                showRemoveButton();
                image.setImageResource(R.drawable.cooking_title_background);
            }
        });

        cancel = view.findViewById(R.id.new_post_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideRemoveButton();
                text.setText("");
            }
        });

        post = view.findViewById(R.id.new_post_post);
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                tempPost.text = text.getText().toString();

                // intent to post
                listener.OnPostNewPost(tempPost);
            }
        });

        Button goBack = view.findViewById(R.id.new_post_go_back);
        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //listener.OnGoBack(R.id.home_toolbar_newpostButton);
            }
        });

        changeLayout();

        return view;
    }

    private void hideRemoveButton(){
        tempPost.image = R.drawable.ic_baseline_add_a_photo_24;
        image.setImageResource(R.drawable.ic_baseline_add_a_photo_24);
        hasImage = false;
        removeImg.setVisibility(View.GONE);
    }

    private void showRemoveButton(){
        hasImage = true;
        removeImg.setVisibility(View.VISIBLE);
        tempPost.image = R.drawable.cooking_title_background;
        image.setImageResource(R.drawable.cooking_title_background);
    }

    private void changeLayout(){
        //tempPost.profile = userProfile;

        name.setText(userProfile.name);
        status.setText("now");
        avatar.setImageResource(userProfile.avatar);

        if (hasImage) showRemoveButton();
        else hideRemoveButton();
    }
}
