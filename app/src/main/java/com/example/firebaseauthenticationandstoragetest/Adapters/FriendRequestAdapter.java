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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firebaseauthenticationandstoragetest.Models.FriendRequestModel;
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

import java.util.HashMap;
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
        final String key = friendRequestModelList.get(position).getRequestKey();

        holder.mNameTV.setText(userName);
        holder.mEmailTV.setText(userEmail);
        try{
            Picasso.get().load(userProfileimage)
                    .placeholder(R.drawable.defaultimg)
                    .into(holder.mAvatarIV);
        }catch(Exception e)
        {

        }

        holder.addIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addUser(key);

            }
        });

        holder.denyIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                denyUser(key);
            }
        });


    }

    private void addUser(final String key)
    {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("FriendRequest");
        Query query = ref.orderByChild("requestKey").equalTo(key);

       query.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               for (DataSnapshot ds : dataSnapshot.getChildren())
               {

                   if(ds.child("requestKey").getValue().equals(key))
                   {
                       HashMap<String,Object> requestUpdate = new HashMap<>();
                       requestUpdate.put("status","accepted");

                       ds.getRef().updateChildren(requestUpdate).addOnSuccessListener(new OnSuccessListener<Void>() {
                           @Override
                           public void onSuccess(Void aVoid) {
                               Toast.makeText(context, "Request Accepted", Toast.LENGTH_SHORT).show();
                           }
                       });
                   }
               }
           }

           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {

           }
       });
    }

    private void denyUser(final String key)
    {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("FriendRequest");
        Query query = ref.orderByChild("requestKey").equalTo(key);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren())
                {

                    if(ds.child("requestKey").getValue().equals(key))
                    {
                        HashMap<String,Object> requestUpdate = new HashMap<>();
                        requestUpdate.put("status","denied");

                        ds.getRef().updateChildren(requestUpdate).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(context, "Request Denied", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return friendRequestModelList.size();
    }

    //view holder class


    class MyHolder extends RecyclerView.ViewHolder{
        ImageView mAvatarIV,denyIV,addIV;
        TextView mNameTV, mEmailTV;

        public MyHolder(@NonNull View itemView)
        {
            super(itemView);

            mAvatarIV = itemView.findViewById(R.id.requestProfileIV);
            mNameTV = itemView.findViewById(R.id.requesteenameTV);
            mEmailTV = itemView.findViewById(R.id.requesteeEmailTV);
            denyIV = itemView.findViewById(R.id.deleteIV);
            addIV = itemView.findViewById(R.id.addIV);

        }
    }
}
