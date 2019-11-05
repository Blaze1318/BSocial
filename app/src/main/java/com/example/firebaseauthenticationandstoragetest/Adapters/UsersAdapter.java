package com.example.firebaseauthenticationandstoragetest.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firebaseauthenticationandstoragetest.ChatActivity;
import com.example.firebaseauthenticationandstoragetest.Fragments.FriendRequestFragment;
import com.example.firebaseauthenticationandstoragetest.HomeActivity;
import com.example.firebaseauthenticationandstoragetest.Models.FriendRequestModel;
import com.example.firebaseauthenticationandstoragetest.Models.UsersModel;
import com.example.firebaseauthenticationandstoragetest.Notifications.APIService;
import com.example.firebaseauthenticationandstoragetest.Notifications.Client;
import com.example.firebaseauthenticationandstoragetest.Notifications.Data;
import com.example.firebaseauthenticationandstoragetest.Notifications.Response;
import com.example.firebaseauthenticationandstoragetest.Notifications.Sender;
import com.example.firebaseauthenticationandstoragetest.Notifications.Token;
import com.example.firebaseauthenticationandstoragetest.R;
import com.example.firebaseauthenticationandstoragetest.UsersProfileActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.security.Key;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.MyHolder> {

    Context context;
    List<UsersModel> usersList;
    APIService service;
    private String image,email,name,key;

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
        final String userProfileimage = usersList.get(position).getImage();
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
/*
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("");
                builder.setMessage("View Profile Or Add As A Friend");
                //delete button set up
                builder.setPositiveButton("Send Friend Request", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                      createfriendrequest(userid);
                    }


                });

                builder.setNegativeButton("View Profile", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(context, UsersProfileActivity.class);
                        intent.putExtra("userid",userid);
                        context.startActivity(intent);
                    }
                });*/

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

    private void createfriendrequest(final String userid)
    {
       FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
       final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Friends");
       final String myid = user.getUid();

       Query query = ref.orderByChild(myid).equalTo(myid);

       query.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               if(dataSnapshot.hasChild(myid)){
                   final DatabaseReference refFriend = ref.child(myid);
               }
               else{
                   HashMap<Object,String> friends = new HashMap<>();
                   friends.put(userid,"Pending");

                   ref.child(myid).setValue(friends);
               }
           }

           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {

           }
       });

    }


    private void senNotification(final String userid,final String recipientId,final  String name) {
        DatabaseReference allTokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = allTokens.orderByKey().equalTo(recipientId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren())
                {
                    Token token = ds.getValue(Token.class);
                    Data data = new Data(userid,name,"New Friend Request",recipientId,R.drawable.logo);
                    Sender sender = new Sender(data,token.getToken());
                    service = Client.getRetrofit("https://fcm.googleapis.com/").create(APIService.class);
                    service.sendNotification(sender)
                            .enqueue(new Callback<Response>() {
                                @Override
                                public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                                    //Toast.makeText(ChatActivity.this, ""+response.message(), Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onFailure(Call<Response> call, Throwable t) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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
