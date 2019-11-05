package com.example.firebaseauthenticationandstoragetest.Adapters;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firebaseauthenticationandstoragetest.ChatActivity;
import com.example.firebaseauthenticationandstoragetest.Models.ChatListModel;
import com.example.firebaseauthenticationandstoragetest.Models.UsersModel;
import com.example.firebaseauthenticationandstoragetest.R;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.MyHolder> {

   Context context;
   List<UsersModel> userModels; //get user info
   private HashMap<String,String> lastMessageMap;

    public ChatListAdapter(Context context, List<UsersModel> userModels) {
        this.context = context;
        this.userModels = userModels;
        lastMessageMap = new HashMap<>();
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate layout
        View view = LayoutInflater.from(context).inflate(R.layout.chat_list_layout,parent,false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        //get data
        final String hisUid = userModels.get(position).getUserid();
        String userImage = userModels.get(position).getImage();
        String userName = userModels.get(position).getName();
        String lastMessage = lastMessageMap.get(hisUid);

        //set data
        holder.nameTv.setText(userName);
        if (lastMessage == null || lastMessage.equals("default"))
        {
            holder.lastmessageTv.setVisibility(View.GONE);
        }
        else{
            holder.lastmessageTv.setVisibility(View.VISIBLE);
            holder.lastmessageTv.setText(lastMessage);
        }
        try{
            Picasso.get().load(userImage).placeholder(R.drawable.ic_mood_black_24dp).into(holder.profileivIv);
        }
        catch (Exception e)
        {
            Picasso.get().load(R.drawable.ic_mood_black_24dp).into(holder.profileivIv);
        }

        //set online status of other users in chatlist
        if(userModels.get(position).getOnlineStatus().equals("online")){
            // online
           holder.onlineStatusIv.setImageResource(R.drawable.circle_online_background);

        }
        else{
            //offline
            holder.onlineStatusIv.setImageResource(R.drawable.circle_offline_background);
        }

        //handle click of user in chatlist
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("userid",hisUid);
                context.startActivity(intent);
            }
        });
    }

    public void setLastMessageMap(String userid, String lastMessage)
    {
        lastMessageMap.put(userid, lastMessage);
    }

    @Override
    public int getItemCount() {
        return userModels.size();
    }


    class MyHolder extends RecyclerView.ViewHolder{
        //views of row chat_list_layout

        ImageView profileivIv,onlineStatusIv;
        TextView nameTv,lastmessageTv;

        public MyHolder(@NonNull View itemView)
        {
            super(itemView);

            //init views
            profileivIv = itemView.findViewById(R.id.profileivIv);
            onlineStatusIv = itemView.findViewById(R.id.onlineStatusIv);
            nameTv = itemView.findViewById(R.id.chatNameTv);
            lastmessageTv = itemView.findViewById(R.id.lastmessageTv);
        }
    }
}
