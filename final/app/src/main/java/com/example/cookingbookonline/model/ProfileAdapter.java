package com.example.cookingbookonline.model;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cookingbookonline.MainActivity;
import com.example.cookingbookonline.R;
import com.example.cookingbookonline.database.DatabaseHelper;

import java.util.List;

public class ProfileAdapter extends RecyclerView.Adapter {

    List<Post> post;
    Profile showProfile;
    Context context;

    ItemClickListener listener;

    DatabaseHelper dbHelper;

    Profile userProfile;

    public interface ItemClickListener {
        void OnShowMore(Post post);
        void OnCreateNotification(Post post, char type);
        void OnRemoveNotification(int receiverId, int postId, char type);
    }

    public ProfileAdapter(Context ct, Profile showProfile, Profile userProfile, List<Post> post, ItemClickListener listener){
        context = ct;
        this.post = post;
        this.showProfile = showProfile;
        this.listener = listener;
        this.userProfile = userProfile;

        dbHelper = new DatabaseHelper(context, new DatabaseHelper.LoadingListener() {
            @Override
            public void OnLoading() {

            }

            @Override
            public void OnFinsishLoading() {

            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) return 0;
        return 1;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view;
        if (viewType == 0){
            view = layoutInflater.inflate(R.layout.a_profile, parent, false);
            return new ViewHolderProfile(view);
        }

        view = layoutInflater.inflate(R.layout.a_post, parent, false);
        return new ViewHodlerPost(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (position == 0){
            // bind viewholder profile
            ViewHolderProfile profile = (ViewHolderProfile) holder;
            updateLayoutUpper(profile);
        } else {
            // bind viewholder post
            int newpos = position - 1;
            ViewHodlerPost post_holder = (ViewHodlerPost) holder;
            updateLayoutBotton(post_holder, post.get(newpos));

            post_holder.show_more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    post_holder.showmored = !post_holder.showmored;
                    if (post_holder.showmored){
                        post_holder.show_more.setTextColor(Color.GREEN);
                    }else{
                        post_holder.show_more.setTextColor(Color.parseColor("#a9a9a9"));
                    }
                    //show_more.setTextColor(Color.GREEN);
                    //intent to full post page

                    listener.OnShowMore(post.get(newpos));
                }
            });

            post_holder.comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //intent to full post page

                    listener.OnShowMore(post.get(newpos));
                }
            });

            post_holder.like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!post_holder.liked){
                        ++post.get(newpos).likeNum;

                        listener.OnCreateNotification(post.get(newpos), 'L');
                    } else {
                        --post.get(newpos).likeNum;

                        listener.OnRemoveNotification(post.get(newpos).profileId, post.get(newpos).ID, 'L');
                    }
                    post_holder.setLike(!post_holder.liked);
                    post_holder.likeNum.setText(String.valueOf(post.get(newpos).likeNum));

                    updateLayoutBotton(post_holder, post.get(newpos));
                }
            });

            post_holder.share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!post_holder.shared){
                        ++post.get(newpos).shareNum;

                        listener.OnCreateNotification(post.get(newpos), 'S');
                    } else {
                        --post.get(newpos).shareNum;

                        listener.OnRemoveNotification(post.get(newpos).profileId, post.get(newpos).ID, 'S');
                    }
                    post_holder.setShare(!post_holder.shared);
                    post_holder.shareNum.setText(String.valueOf(post.get(newpos).shareNum));

                    updateLayoutBotton(post_holder, post.get(newpos));
                }
            });

        }
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        dbHelper.close();
    }

    private void updateLayoutUpper(ViewHolderProfile holder){
        holder.background.setImageResource(showProfile.backgroundImg);
        holder.avatar.setImageResource(showProfile.avatar);
        holder.name.setText(showProfile.name);
        holder.mail.setText(showProfile.gmail);
    }

    private void updateLayoutBotton(ViewHodlerPost holder, Post post){

        post = dbHelper.getPost(post.ID, userProfile.ID);

        if (post != null){
            holder.avatar.setImageResource(post.profileAvatar);
            holder.name.setText(post.profileName);
            holder.status.setText(post.status);
            holder.text.setText(post.text);
            holder.image.setImageResource(post.image);
            holder.likeNum.setText(String.valueOf(post.likeNum));
            holder.commentNum.setText(String.valueOf(post.commentNum));
            holder.shareNum.setText(String.valueOf(post.shareNum));
            holder.setLike(post.liked);
            holder.setShare(post.shared);
        }


    }

    @Override
    public int getItemCount() {
        return post.size() + 1;
    }

    public class ViewHolderProfile extends RecyclerView.ViewHolder {

        ImageView background, avatar;
        TextView name, mail;

        public ViewHolderProfile(@NonNull View itemView) {
            super(itemView);

            background = itemView.findViewById(R.id.profile_background);
            avatar = itemView.findViewById(R.id.profile_avatar);
            name = itemView.findViewById(R.id.profile_name);
            mail = itemView.findViewById(R.id.profile_gmail);
        }
    }

    public class ViewHodlerPost extends RecyclerView.ViewHolder {

        Button  subscription, like, comment, share;
        TextView name, status, text, show_more, likeNum, commentNum, shareNum;
        ImageView avatar, image;

        boolean liked = false;
        boolean shared = false;

        boolean showmored = false;

        public ViewHodlerPost(@NonNull View itemView) {
            super(itemView);

            avatar = itemView.findViewById(R.id.post_avatar);               // Button
            name = itemView.findViewById(R.id.post_name);                   // TextView
            status = itemView.findViewById(R.id.post_status);               // TextView
            subscription = itemView.findViewById(R.id.post_subscription);   // Button
            text = itemView.findViewById(R.id.post_text);                   // TextView
            show_more = itemView.findViewById(R.id.post_showMore);          // TextView
            image = itemView.findViewById(R.id.post_image);                 // Image
            likeNum = itemView.findViewById(R.id.post_likeNumber);          // TextView
            commentNum = itemView.findViewById(R.id.post_commentNumber);    // TextView
            shareNum = itemView.findViewById(R.id.post_shareNumber);        // TextView
            like = itemView.findViewById(R.id.post_likeButton);             // Button
            comment = itemView.findViewById(R.id.post_commentButton);       // Button
            share = itemView.findViewById(R.id.post_shareButton);           // Button

            avatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //intent to profile page
                }
            });

//            show_more.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    showmored = !showmored;
//                    if (showmored){
//                        show_more.setTextColor(Color.GREEN);
//                    }else{
//                        show_more.setTextColor(Color.parseColor("#a9a9a9"));
//                    }
//                    //show_more.setTextColor(Color.GREEN);
//                    //intent to full post page
//                }
//            });

//            like.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (!liked){
//
//                    }
//                    setLike(!liked);
//                }
//            });

//            comment.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    //intent to full post page
//                }
//            });

//            share.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    setShare(!shared);
//                }
//            });
        }

        private void setLike(boolean liked){
            this.liked = liked;

            if (liked){
                like.setTextColor(Color.GREEN);
            } else {
                like.setTextColor(Color.WHITE);
            }
        }

        private void setShare(boolean shared){
            this.shared = shared;

            if (shared){
                share.setTextColor(Color.GREEN);
            } else {
                share.setTextColor(Color.WHITE);
            }
        }
    }
}
