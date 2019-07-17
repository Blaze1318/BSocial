package com.example.firebaseauthenticationandstoragetest.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firebaseauthenticationandstoragetest.ChatActivity;
import com.example.firebaseauthenticationandstoragetest.Models.UsersModel;
import com.example.firebaseauthenticationandstoragetest.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.MyHolder> {

    Context context;
    List<UsersModel> usersList;

    //constructor
    public UsersAdapter(Context context, List<UsersModel> usersList) {
        this.context = context;
        this.usersList = usersList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.users_recycler_layout,parent,false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        //get data
        final String userid = usersList.get(position).getUserid();
        String userProfileimage = usersList.get(position).getImage();
        String userName = usersList.get(position).getName();
        final String userEmail = usersList.get(position).getEmail();

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
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("userid",userid);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    //view holder class


    class MyHolder extends RecyclerView.ViewHolder{
        ImageView mAvatarIV;
        TextView mNameTV, mEmailTV;

        public MyHolder(@NonNull View itemView)
        {
            super(itemView);

            mAvatarIV = itemView.findViewById(R.id.ivAvatar);
            mNameTV = itemView.findViewById(R.id.nameTV);
            mEmailTV = itemView.findViewById(R.id.emailTV);

        }
    }
}
