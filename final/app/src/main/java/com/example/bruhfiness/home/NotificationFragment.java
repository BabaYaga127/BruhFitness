package com.example.bruhfiness.home;

import android.annotation.SuppressLint;
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

import com.example.bruhfiness.R;
import com.example.bruhfiness.model.NotificationAdapter;

public class NotificationFragment extends Fragment implements NotificationAdapter.ItemClickListener {

    static public final int interfaceID = R.id.bot_nav_notification;
    static public final String KEY_NOTIFICATION_FRAGMENT = "key_notification_fragment";

    int profileId;

    RecyclerView recyclerView;

    Context context;

    ItemClickListener listener;

    public interface ItemClickListener{
        void OnGoToPersonalProfie(int profileId);
    }

    public static NotificationFragment newInstance(int profileId) {

        Bundle args = new Bundle();
        args.putInt(KEY_NOTIFICATION_FRAGMENT, profileId);
        NotificationFragment fragment = new NotificationFragment();
        fragment.setArguments(args);
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

    @SuppressLint("ResourceType")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification, container, false);

        context = view.getContext();

        if (getArguments() != null)
        profileId = getArguments().getInt(KEY_NOTIFICATION_FRAGMENT);

        recyclerView = view.findViewById(R.id.notification_recyclerView);

        NotificationAdapter myAdapter = new NotificationAdapter(view.getContext(), -1, profileId, this);
        recyclerView.setAdapter(myAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        return view;
    }


    @Override
    public void OnClick(int profileId) {
        listener.OnGoToPersonalProfie(profileId);
//        Toast toast = Toast.makeText(context, profile.name, Toast.LENGTH_SHORT);
//        toast.show();
    }
}
