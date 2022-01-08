package com.example.cookingbookonline.model;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
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

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.MyViewHolder> {

    List<Notification> data;
    Context context;

    int postId, profileId;

    ItemClickListener listener;

    DatabaseHelper dbHelper;

    public interface ItemClickListener{
        void OnClick(int profileId);
    }

    public NotificationAdapter(Context ct, int postId, int profileId, ItemClickListener listener){
        context = ct;
        this.listener = listener;
        this.postId = postId;
        this.profileId = profileId;

        dbHelper = new DatabaseHelper(context, new DatabaseHelper.LoadingListener() {
            @Override
            public void OnLoading() {

            }

            @Override
            public void OnFinsishLoading() {

            }
        });

        data = dbHelper.getAllComment(postId, profileId);
    }

    @NonNull
    @Override
    public NotificationAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.a_notification, parent, false);
        return new NotificationAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationAdapter.MyViewHolder holder, int position) {
        holder.avatar.setImageResource(data.get(position).profileAvatar);
        holder.name.setText(data.get(position).profileName);
        holder.status.setText(data.get(position).status);

        String comment = "";
        if (postId < 0)
            switch (data.get(position).type ){
                case 'L':
                    comment = "has liked your post";
                    break;
                case 'C':
                    comment = "commented: ";
                    break;
                case 'S':
                    comment = "has shared your post";
                    break;
            }

        holder.action.setText(comment + data.get(position).comment);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.OnClick(data.get(position).profileId);
            }
        });
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        dbHelper.close();
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView avatar;
        TextView name, status, action;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.notification_avatar);               // Button
            name = itemView.findViewById(R.id.notification_name);                   // TextView
            status = itemView.findViewById(R.id.notification_status);               // TextView
            action = itemView.findViewById(R.id.notification_action);
        }
    }
}
