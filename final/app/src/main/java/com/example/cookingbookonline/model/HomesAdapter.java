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

public class HomesAdapter extends RecyclerView.Adapter<HomesAdapter.MyViewHolder> {

    List<Post> post;
    Context context;

    ItemClickListener listener;

    Profile userProfile;

    DatabaseHelper dbHelper;

    public interface ItemClickListener{
        void OnShowMore(Post post);
        void OnShowPersonalProfile(int profileId);
        void OnCreateNotification(Post post, char type);
        void OnRemoveNotification(int receiverId, int postId, char type);
    }

    public HomesAdapter(Context ct, List<Post> post, Profile userProfile, ItemClickListener listener){
        context = ct;
        this.post = post;
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

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.a_post, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HomesAdapter.MyViewHolder holder, int position) {
        holder.avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.OnShowPersonalProfile(post.get(position).profileId);
            }
        });

        holder.show_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.showmored = !holder.showmored;
                if (holder.showmored){
                    holder.show_more.setTextColor(Color.GREEN);
                }else{
                    holder.show_more.setTextColor(Color.parseColor("#a9a9a9"));
                }
                //show_more.setTextColor(Color.GREEN);
                //intent to full post page
                listener.OnShowMore(post.get(position));
            }
        });

        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!holder.liked) {
                    ++ post.get(position).likeNum;
                    listener.OnCreateNotification(post.get(position), 'L');
                }
                else {
                    -- post.get(position).likeNum;
                    listener.OnRemoveNotification(post.get(position).profileId, post.get(position).ID, 'L');
                }
                holder.likeNum.setText(String.valueOf(post.get(position).likeNum));
                holder.setLike(!holder.liked);

                updateLayout(holder, post.get(position));
            }
        });

        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //intent to full post page
                listener.OnShowMore(post.get(position));
            }
        });

        holder.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!holder.shared) {
                    ++post.get(position).shareNum;
                    listener.OnCreateNotification(post.get(position), 'S');
                }
                else {
                    --post.get(position).shareNum;
                    listener.OnRemoveNotification(post.get(position).profileId, post.get(position).ID, 'S');
                }
                holder.shareNum.setText(String.valueOf(post.get(position).shareNum));
                holder.setShare(!holder.shared);

                updateLayout(holder, post.get(position));
            }
        });

        updateLayout(holder, post.get(position));


    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull  RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        dbHelper.close();
    }

    private void updateLayout(HomesAdapter.MyViewHolder holder, Post post){

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
        return post.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        Button subscription, like, comment, share;
        TextView name, status, text, show_more, likeNum, commentNum, shareNum;
        ImageView image, avatar;

        boolean liked = false;
        boolean shared = false;

        boolean showmored = false;


        public MyViewHolder(@NonNull View itemView) {
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

//            avatar.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    //intent to profile page
//                }
//            });

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
//
//                }
//            });

//            like.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
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
