package com.example.firebaseauthenticationandstoragetest.Adapters;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firebaseauthenticationandstoragetest.Models.ChatModel;
import com.example.firebaseauthenticationandstoragetest.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;


import java.util.Calendar;

import java.util.List;
import java.util.Locale;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MyHolder>{


    private static final int MSG_TYPE_LEFT = 0;
    private static final int MSG_TYPE_RIGHT= 1;
    Context context;
    List<ChatModel> chatList;
    String imageUrl;

    //Firebase instance
    FirebaseUser mUser;

    public ChatAdapter(Context context, List<ChatModel> chatList, String imageUrl) {
        this.context = context;
        this.chatList = chatList;
        this.imageUrl = imageUrl;
    }


    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflating layout rows for sender and receiver

        if(viewType == MSG_TYPE_RIGHT)
        {
            View view = LayoutInflater.from(context).inflate(R.layout.row_chat_right,parent,false);
            return new MyHolder(view);
        }
        else{
            View view = LayoutInflater.from(context).inflate(R.layout.row_chat_left,parent,false);
            return new MyHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
            String message = chatList.get(position).getMessage();
            String timeStamp = chatList.get(position).getTimestamp();

            //convert time stamp to dd/mm/yyyy hh:mm am/pm\
            Calendar cal = Calendar.getInstance(Locale.ENGLISH);
            cal.setTimeInMillis(Long.parseLong(timeStamp));
            String dateTime = DateFormat.format("dd/MM/yyyy hh:mm aa" ,cal).toString();

            holder.messageTv.setText(message);
            holder.timeTv.setText(dateTime);
            try{
                Picasso.get().load(imageUrl).into(holder.chatImage);
            }catch (Exception e)
            {

            }

            //setting status of message as seen/delivered
            if(position==chatList.size()-1)
            {
                if(chatList.get(position).isSeen())
                {
                    holder.isSeenTv.setText("Seen");
                }
                else{
                    holder.isSeenTv.setText("Delivered");
                }
            }
            else{
                holder.isSeenTv.setVisibility(View.GONE);
            }

    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    @Override
    public int getItemViewType(int position) {
        //get currently signed in user

        mUser = FirebaseAuth.getInstance().getCurrentUser();

        if(chatList.get(position).getSender().equals(mUser.getUid()))
        {
            return MSG_TYPE_RIGHT;
        }
        else{
            return MSG_TYPE_LEFT;
        }
    }

    //holder class
    class MyHolder extends RecyclerView.ViewHolder{

        //views
        ImageView chatImage;
        TextView messageTv ,timeTv,isSeenTv;

        public MyHolder(@NonNull View itemView)
        {
            super(itemView);

            //init views
            chatImage = itemView.findViewById(R.id.chatLeftprofileIV);
            messageTv = itemView.findViewById(R.id.chatMessageTV);
            timeTv = itemView.findViewById(R.id.timeTV);
            isSeenTv = itemView.findViewById(R.id.isSeenTV);

        }
    }
}
