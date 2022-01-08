package com.example.bruhfiness.home;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bruhfiness.R;
import com.example.bruhfiness.database.DatabaseHelper;
import com.example.bruhfiness.model.NotificationAdapter;
import com.example.bruhfiness.model.Post;
import com.example.bruhfiness.model.Profile;

public class PostFragment extends Fragment implements NotificationAdapter.ItemClickListener{

    static public final String KEY_POST_FRAGMENT = "KEY_POST_FRAGMENT";
    static public final int interfaceID = R.id.post_showMore;

    RecyclerView recyclerView;

    Post post;

    Profile userProfile;

    View view;

    ImageView  avatar, image;
    ImageButton postComment;
    TextView name, status, text, likeNum, commentNum, shareNum;
    Button subcription, like, share, back;
    EditText comment;

    ItemClickListener listener;

    DatabaseHelper dbHelper;

    public interface ItemClickListener {
        void OnGoBack();
        void OnGoBack(int id);
        void OnGoToPersonalProfie(int profileId);
        void OnCreateNotification(Post post, char type);
        void OnCreateComment(Post post, String text);
        void OnRemoveNotification(int receiverId, int postId, char type);
    }

    public static PostFragment newInstance(Post post, Profile userProfile){
        PostFragment fragment = new PostFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(KEY_POST_FRAGMENT, post);
        bundle.putParcelable(KEY_POST_FRAGMENT + "2", userProfile);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onAttach(@NonNull  Context context) {
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
        view = inflater.inflate(R.layout.fragment_post, container, false);

        if (getArguments() != null){
            post = (Post) getArguments().getParcelable(KEY_POST_FRAGMENT);
            userProfile = (Profile) getArguments().getParcelable(KEY_POST_FRAGMENT + "2");
        }

        dbHelper = new DatabaseHelper(view.getContext(), new DatabaseHelper.LoadingListener() {
            @Override
            public void OnLoading() {

            }

            @Override
            public void OnFinsishLoading() {

            }
        });

        back = view.findViewById(R.id.frag_post_back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.OnGoBack();
            }
        });

        avatar = view.findViewById(R.id.frag_post_avatar);
        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // intent to go to personal profile
                listener.OnGoToPersonalProfie(post.profileId);
            }
        });

        name = view.findViewById(R.id.frag_post_name);
        status = view.findViewById(R.id.frag_post_status);
        subcription = view.findViewById(R.id.frag_post_subscription);
        text = view.findViewById(R.id.frag_post_text);
        image = view.findViewById(R.id.frag_post_image);
        likeNum = view.findViewById(R.id.frag_post_likeNumber);
        commentNum = view.findViewById(R.id.frag_post_commentNumber);
        shareNum = view.findViewById(R.id.frag_post_shareNumber);

        comment = view.findViewById(R.id.frag_post_comment);
        postComment = view.findViewById(R.id.frag_post_sendCommentButton);
        postComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // exec sql insert
                listener.OnCreateComment(post, comment.getText().toString());

                comment.setText("");
                ChangeLayout();
                updateCommentSection();
            }
        });


        like = view.findViewById(R.id.frag_post_likeButton);
        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // increase the like
                post.liked = !post.liked;
                if (post.liked) {
                    ++post.likeNum;
                    listener.OnCreateNotification(post, 'L');
                }
                else {
                    --post.likeNum;
                    listener.OnRemoveNotification(post.profileId ,post.ID, 'L');
                }
                ChangeLayout();
            }
        });

        //comment = view.findViewById(R.id.frag_post_commentButton);
        share = view.findViewById(R.id.frag_post_shareButton);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                post.shared = !post.shared;
                if (post.shared) {
                    ++post.shareNum;
                    listener.OnCreateNotification(post, 'S');
                }
                else {
                    --post.shareNum;
                    listener.OnRemoveNotification(post.profileId, post.ID, 'S');
                }
                ChangeLayout();
            }
        });

        comment = view.findViewById(R.id.frag_post_comment);

        ChangeLayout();
        updateCommentSection();

        return view;
    }

    private void ChangeLayout(){
        post = dbHelper.getPost(post.ID, userProfile.ID);

        if (post != null){
            avatar.setImageResource(post.profileAvatar);
            name.setText(post.profileName);
            status.setText(post.status);
            text.setText(post.text);
            image.setImageResource(post.image);
            likeNum.setText(String.valueOf(post.likeNum) + " likes");
            commentNum.setText(String.valueOf(post.commentNum) + " likes");
            shareNum.setText(String.valueOf(post.shareNum) + " likes");

            if (post.liked){
                like.setTextColor(Color.GREEN);
            } else {
                like.setTextColor(Color.WHITE);
            }

            if (post.shared){
                share.setTextColor(Color.GREEN);
            } else {
                share.setTextColor(Color.WHITE);
            }
        }
    }

    public void updateCommentSection(){
        recyclerView = view.findViewById(R.id.frag_post_recyclerView);

        NotificationAdapter myAdapter = new NotificationAdapter(view.getContext(), post.ID, -1, this);
        recyclerView.setAdapter(myAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
    }

    @Override
    public void OnClick(int profileId) {
        listener.OnGoToPersonalProfie(profileId);
    }
}
