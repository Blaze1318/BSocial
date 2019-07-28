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

import com.example.firebaseauthenticationandstoragetest.ChatActivity;
import com.example.firebaseauthenticationandstoragetest.Models.FriendRequestModel;
import com.example.firebaseauthenticationandstoragetest.R;
import com.example.firebaseauthenticationandstoragetest.UsersProfileActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.MyHolder> {

    Context context;
    List<FriendRequestModel> friendsList;

    public FriendsAdapter(Context context,List<FriendRequestModel> friendsList)
    {
        this.context = context;
        this.friendsList = friendsList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.friend_list_layout,parent,false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        //get data
        //get data
        final String userid = friendsList.get(position).getRequestFrom();
        final String userProfileimage = friendsList.get(position).getRequesterImage();
        String userName = friendsList.get(position).getRequesterName();
        final String userEmail = friendsList.get(position).getRequesterEmail();
        final String key = friendsList.get(position).getRequestKey();

        holder.mNameTV.setText(userName);
        holder.mEmailTV.setText(userEmail);
        try{
            Picasso.get().load(userProfileimage)
                    .placeholder(R.drawable.defaultimg)
                    .into(holder.mAvatarIV);
        }catch(Exception e)
        {

        }

        holder.mAvatarIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("");
                builder.setMessage("View Profile Or Chat");
                //delete button set up
                builder.setPositiveButton("Chat", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(context, ChatActivity.class);
                        intent.putExtra("userid",userid);
                        context.startActivity(intent);
                    }


                });

                builder.setNegativeButton("View Profile", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(context, UsersProfileActivity.class);
                        intent.putExtra("userid",userid);
                        context.startActivity(intent);
                    }
                });

                builder.create().show();
            }
        });
    }


    @Override
    public int getItemCount() {
        return friendsList.size();
    }

    //view holder class


    class MyHolder extends RecyclerView.ViewHolder{
        ImageView mAvatarIV;
        TextView mNameTV, mEmailTV;

        public MyHolder(@NonNull View itemView)
        {
            super(itemView);

            mAvatarIV = itemView.findViewById(R.id.friendProfileIV);
            mNameTV = itemView.findViewById(R.id.friendnameTV);
            mEmailTV = itemView.findViewById(R.id.friendEmailTV);


        }
    }
}
