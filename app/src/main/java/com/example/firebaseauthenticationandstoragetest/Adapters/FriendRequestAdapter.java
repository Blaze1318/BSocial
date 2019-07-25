package com.example.firebaseauthenticationandstoragetest.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firebaseauthenticationandstoragetest.Models.FriendRequestModel;
import com.example.firebaseauthenticationandstoragetest.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class FriendRequestAdapter extends RecyclerView.Adapter<FriendRequestAdapter.MyHolder> {
    Context context;
    List<FriendRequestModel> friendRequestModelList;

    public FriendRequestAdapter(Context context,List<FriendRequestModel> friendRequestModelList)
    {
        this.context = context;
        this.friendRequestModelList = friendRequestModelList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.friend_request_layout,parent,false);
        return new MyHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        //get data
        final String userid = friendRequestModelList.get(position).getRequestFrom();
        final String userProfileimage = friendRequestModelList.get(position).getRequesterImage();
        String userName = friendRequestModelList.get(position).getRequesterName();
        final String userEmail = friendRequestModelList.get(position).getRequesterEmail();

        holder.mNameTV.setText(userName);
        holder.mEmailTV.setText(userEmail);
        try{
            Picasso.get().load(userProfileimage)
                    .placeholder(R.drawable.defaultimg)
                    .into(holder.mAvatarIV);
        }catch(Exception e)
        {

        }


    }

    @Override
    public int getItemCount() {
        return friendRequestModelList.size();
    }

    //view holder class


    class MyHolder extends RecyclerView.ViewHolder{
        ImageView mAvatarIV;
        TextView mNameTV, mEmailTV;

        public MyHolder(@NonNull View itemView)
        {
            super(itemView);

            mAvatarIV = itemView.findViewById(R.id.requestProfileIV);
            mNameTV = itemView.findViewById(R.id.requesteenameTV);
            mEmailTV = itemView.findViewById(R.id.requesteeEmailTV);

        }
    }
}
