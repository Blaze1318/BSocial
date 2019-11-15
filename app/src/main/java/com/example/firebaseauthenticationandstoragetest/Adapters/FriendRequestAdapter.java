package com.example.firebaseauthenticationandstoragetest.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firebaseauthenticationandstoragetest.Models.FriendRequestModel;
import com.example.firebaseauthenticationandstoragetest.Models.UsersModel;
import com.example.firebaseauthenticationandstoragetest.Notifications.APIService;
import com.example.firebaseauthenticationandstoragetest.Notifications.Client;
import com.example.firebaseauthenticationandstoragetest.Notifications.Data;
import com.example.firebaseauthenticationandstoragetest.Notifications.Response;
import com.example.firebaseauthenticationandstoragetest.Notifications.Sender;
import com.example.firebaseauthenticationandstoragetest.Notifications.Token;
import com.example.firebaseauthenticationandstoragetest.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class FriendRequestAdapter extends RecyclerView.Adapter<FriendRequestAdapter.MyHolder> {
    Context context;
    List<UsersModel> usersModels;

    //firebase database
    DatabaseReference friendRequest;
    DatabaseReference friends;

    //firebase user
    String myId;
    APIService service;

    String myName;

    public FriendRequestAdapter(Context context, List<UsersModel> usersModels) {
        this.context = context;
        this.usersModels = usersModels;
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
        final String hisUid = usersModels.get(position).getUserid();
        String userImage = usersModels.get(position).getImage();
        String userName = usersModels.get(position).getName();
        String userEmail = usersModels.get(position).getEmail();
        myId = FirebaseAuth.getInstance().getUid();

        holder.mNameTV.setText(userName);
        holder.mEmailTV.setText(userEmail);

        service = Client.getRetrofit("https://fcm.googleapis.com/").create(APIService.class);

        try{
            Picasso.get().load(userImage).placeholder(R.drawable.ic_mood_black_24dp).into(holder.mAvatarIV);
        }
        catch (Exception e)
        {
            Picasso.get().load(R.drawable.ic_mood_black_24dp).into(holder.mAvatarIV);
        }

        holder.addIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                acceptFriendRequest(myId,hisUid);
            }
        });

        holder.denyIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelFriendRequest(myId,hisUid);
            }
        });

    }



    @Override
    public int getItemCount() {
        return usersModels.size();
    }

    //view holder class


    class MyHolder extends RecyclerView.ViewHolder{
        ImageView mAvatarIV,denyIV,addIV;
        TextView mNameTV, mEmailTV;

        public MyHolder(@NonNull View itemView)
        {
            super(itemView);

            mAvatarIV = itemView.findViewById(R.id.friendProfileIV);
            mNameTV = itemView.findViewById(R.id.friendnameTV);
            mEmailTV = itemView.findViewById(R.id.requesteeEmailTV);
            denyIV = itemView.findViewById(R.id.deleteIV);
            addIV = itemView.findViewById(R.id.addIV);

        }
    }

    private void cancelFriendRequest(final String senderId,final String recipientId) {
        friendRequest = FirebaseDatabase.getInstance().getReference("FriendsRequest");
        friendRequest.child(senderId).child(recipientId)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            friendRequest.child(recipientId).child(senderId)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful())
                                            {
                                                getName();
                                                Toast.makeText(context, "Friend Request Denied", Toast.LENGTH_SHORT).show();
                                                senNotification(recipientId,myName,"","Request Denied");
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void acceptFriendRequest(final String senderId,final String recipientId)
    {
        friendRequest = FirebaseDatabase.getInstance().getReference("Friends");
        Date date = new Date();
        final String currentDate = ""+date.getDay() + " "+ date.getMonth() + " "+date.getYear();
        HashMap<String,String> friends = new HashMap<>();
        friends.put("id",recipientId);
        friends.put("friends","YES");
        friends.put("date",currentDate);
        friendRequest.child(senderId).child(recipientId)
                .setValue(friends)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            HashMap<String,String> friends = new HashMap<>();
                            friends.put("id",senderId);
                            friends.put("friends","YES");
                            friends.put("date",currentDate);
                            friendRequest.child(recipientId).child(senderId)
                                    .setValue(friends)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful())
                                            {
                                               removeFriendRequest(senderId,recipientId);
                                               getName();
                                               senNotification(recipientId,myName,"","New Friend");
                                            }
                                        }
                                    });
                        }
                    }
                });

    }

    private void removeFriendRequest(final String senderId,final String recipientId) {
        friendRequest = FirebaseDatabase.getInstance().getReference("FriendsRequest");
        friendRequest.child(senderId).child(recipientId)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            friendRequest.child(recipientId).child(senderId)
                                    .removeValue();

                        }
                        else{
                            Toast.makeText(context, "Unknown Error Occurred", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void senNotification(final String recipientId,final  String name, final String msg,final String title) {
        DatabaseReference allTokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = allTokens.orderByKey().equalTo(recipientId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren())
                {
                    Token token = ds.getValue(Token.class);
                    Data data = new Data(myId,name+":"+msg,title,recipientId,R.drawable.logo);
                    Sender sender = new Sender(data,token.getToken());

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

    private void getName()
    {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        final Query myQuery = databaseReference.orderByChild("userid").equalTo(myId);

        //get users photo and name
        myQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //get data
                for(DataSnapshot ds : dataSnapshot.getChildren())
                {
                    myName = ""+ds.child("name").getValue();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
