package com.example.firebaseauthenticationandstoragetest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.firebaseauthenticationandstoragetest.Notifications.APIService;
import com.example.firebaseauthenticationandstoragetest.Notifications.Client;
import com.example.firebaseauthenticationandstoragetest.Notifications.Data;
import com.example.firebaseauthenticationandstoragetest.Notifications.Response;
import com.example.firebaseauthenticationandstoragetest.Notifications.Sender;
import com.example.firebaseauthenticationandstoragetest.Notifications.Token;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;

public class UsersProfileActivity extends AppCompatActivity {

    Toolbar toolbar;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    DatabaseReference friendRequest;
    DatabaseReference friends;

    String recipientId;
    String recipientImage;
    String senderId;

    ImageView userImage;
    EditText userDname,userEmail;

    private String myName ="";
    private boolean isImageFitToScreen;


    Button sendFriendRequestButton;

    APIService service;

    String CURRENT_STATE = "not_friends";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_profile);

        toolbar = findViewById(R.id.toolbar3);
        setSupportActionBar(toolbar);
        toolbar.setTitle("User Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        userImage = findViewById(R.id.usersprofileIV);
        userDname = findViewById(R.id.etUsersDName);
        userEmail = findViewById(R.id.etUsersEmail);

        sendFriendRequestButton = findViewById(R.id.btnRequest);

        Intent intent = getIntent();
        recipientId = intent.getStringExtra("userid");
        senderId = FirebaseAuth.getInstance().getUid();

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users");
        friendRequest = FirebaseDatabase.getInstance().getReference("FriendsRequest");
        friends = FirebaseDatabase.getInstance().getReference("Friends");

        service = Client.getRetrofit("https://fcm.googleapis.com/").create(APIService.class);

        //search user to get info
        Query userQuery = databaseReference.orderByChild("userid").equalTo(recipientId);

        //get users photo and name
        userQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //get data
                for(DataSnapshot ds : dataSnapshot.getChildren())
                {
                    String name = ""+ds.child("name").getValue();
                    recipientImage = ""+ds.child("image").getValue();
                    String email = ""+ds.child("email").getValue();

                    userDname.setText(name);
                    userEmail.setText(email);
                    try{
                        Picasso.get().load(recipientImage).placeholder(R.drawable.ic_mood_black_24dp).into(userImage);
                    }catch (Exception e)
                    {
                        Picasso.get().load(R.drawable.ic_mood_black_24dp).into(userImage);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        final Query myQuery = databaseReference.orderByChild("userid").equalTo(senderId);

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


        maintainButtonState();

        if (!senderId.equals(recipientId))
        {
            sendFriendRequestButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   if(CURRENT_STATE.equals("not_friends"))
                   {
                       sendFriendRequest();
                   }
                   else if(CURRENT_STATE.equals("request_sent"))
                   {
                       AlertDialog.Builder builder = new  AlertDialog.Builder(getApplicationContext());
                       builder.setTitle("Friend Request")
                               .setMessage("Cancel Friend Request?")
                               .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                   @Override
                                   public void onClick(DialogInterface dialog, int which) {
                                       cancelFriendRequest();
                                   }
                               })

                               .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                   @Override
                                   public void onClick(DialogInterface dialog, int which) {
                                       //do nothing
                                   }
                               });

                   }
                   else if(CURRENT_STATE.equals("request_received"))
                   {
                       Toast.makeText(UsersProfileActivity.this, "Already Received Request From This User", Toast.LENGTH_SHORT).show();
                   }
                   else if (CURRENT_STATE.equals("friends"))
                   {
                       AlertDialog.Builder builder = new  AlertDialog.Builder(getApplicationContext());
                       builder.setTitle("Friends")
                               .setMessage("Remove As A Friend?")
                               .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                   @Override
                                   public void onClick(DialogInterface dialog, int which) {
                                       removeFriend();
                                   }
                               })

                               .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                   @Override
                                   public void onClick(DialogInterface dialog, int which) {
                                       //do nothing
                                   }
                               });
                   }
                }
            });
        }

        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void removeFriend() {

        friends.child(senderId).child(recipientId)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            friends.child(recipientId).child(senderId)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful())
                                            {
                                                CURRENT_STATE = "not_friends";
                                                sendFriendRequestButton.setText("Send Friend Request");
                                                senNotification(recipientId,myName,"");
                                            }
                                        }
                                    });
                        }
                    }
                });

    }

    private void cancelFriendRequest() {
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
                                                CURRENT_STATE = "not_friends";
                                                sendFriendRequestButton.setText("Send Friend Request");
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void maintainButtonState() {
        friends.child(senderId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(recipientId))
                {
                    CURRENT_STATE = "friends";
                    sendFriendRequestButton.setText("Remove As Friend");
                }
                else{
                    friendRequest.child(senderId)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.hasChild(recipientId))
                                    {
                                        String request_type = dataSnapshot.child(recipientId).child("request_type").getValue().toString();

                                        if (request_type.equals("sent"))
                                        {
                                            CURRENT_STATE = "request_sent";
                                            sendFriendRequestButton.setText("Cancel Friend Request");
                                        }
                                        else if (request_type.equals("received"))
                                        {
                                            CURRENT_STATE = "request_received";
                                            sendFriendRequestButton.setText("Request Received");
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendFriendRequest()
    {
        HashMap<String,String> fRequestSent = new HashMap<>();
        fRequestSent.put("id",recipientId);
        fRequestSent.put("request_type","sent");
        friendRequest.child(senderId).child(recipientId)
                .setValue(fRequestSent)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            HashMap<String,String> fRequestReceived = new HashMap<>();
                            fRequestReceived.put("id",senderId);
                            fRequestReceived.put("request_type","received");
                            friendRequest.child(recipientId).child(senderId)
                                    .setValue(fRequestReceived)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful())
                                            {
                                                CURRENT_STATE = "request_sent";
                                                sendFriendRequestButton.setText("Cancel Friend Request");
                                                senNotification(recipientId,myName,"");
                                            }
                                        }
                                    });
                        }
                    }
                });

    }

    private void senNotification(final String recipientId,final  String name, final String msg) {
        DatabaseReference allTokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = allTokens.orderByKey().equalTo(recipientId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren())
                {
                    Token token = ds.getValue(Token.class);
                    Data data = new Data(senderId,name+":"+msg,"New Friend Request",recipientId,R.drawable.logo);
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

}
