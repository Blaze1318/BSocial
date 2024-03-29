package com.example.firebaseauthenticationandstoragetest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.firebaseauthenticationandstoragetest.Adapters.ChatAdapter;
import com.example.firebaseauthenticationandstoragetest.Models.ChatModel;
import com.example.firebaseauthenticationandstoragetest.Models.UsersModel;
import com.example.firebaseauthenticationandstoragetest.Notifications.APIService;
import com.example.firebaseauthenticationandstoragetest.Notifications.Client;
import com.example.firebaseauthenticationandstoragetest.Notifications.Data;
import com.example.firebaseauthenticationandstoragetest.Notifications.Response;
import com.example.firebaseauthenticationandstoragetest.Notifications.Sender;
import com.example.firebaseauthenticationandstoragetest.Notifications.Token;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;


public class ChatActivity extends AppCompatActivity {

    //views from xml
    Toolbar toolbar;
    RecyclerView recyclerView;
    ImageView profileIV;
    TextView nameTV,userstatusTV;
    EditText chatET;
    ImageButton sendIB;

    FirebaseAuth mUser;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    //checking if user seen message or not
    ValueEventListener seenListener;
    DatabaseReference seenRef;

    List<ChatModel> chatModelList;
    ChatAdapter chatAdapter;

    String recipientId;
    String myId;
    String recipientImage;

    APIService service;
    boolean notify = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        toolbar.setTitle("");
        recyclerView = findViewById(R.id.recycleChat);
        profileIV = findViewById(R.id.usersprofileIV);
        nameTV = findViewById(R.id.tvUsern);
        userstatusTV = findViewById(R.id.userStatus);
        chatET = findViewById(R.id.messageEt);
        sendIB = findViewById(R.id.ibSend);

        //Setting up RecyclerView and layout
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);


        //firebase instance
        mUser = FirebaseAuth.getInstance();

        service = Client.getRetrofit("https://fcm.googleapis.com/").create(APIService.class);
        Intent intent = getIntent();
        recipientId = intent.getStringExtra("userid");

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users");

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
                    String typingTo = ""+ds.child("typingTo").getValue();

                    //check if user typing status
                    if(typingTo.equals(myId))
                    {
                        userstatusTV.setText("typing...");
                    }
                    else{
                        //setting value of online status
                        String onlinestatus = ""+ds.child("onlineStatus").getValue();

                        if(onlinestatus.equals("online"))
                        {
                            userstatusTV.setText(onlinestatus);
                        }
                        else{
                            //set last seen with time stamp
                            Calendar cal = Calendar.getInstance(Locale.ENGLISH);
                            cal.setTimeInMillis(Long.parseLong(onlinestatus));
                            String dateTime = DateFormat.format("dd/MM/yyyy hh:mm aa" ,cal).toString();
                            userstatusTV.setText("Last seen at: "+dateTime);
                        }
                    }



                    nameTV.setText(name);
                    try{
                        Picasso.get().load(recipientImage).placeholder(R.drawable.ic_mood_black_24dp).into(profileIV);
                    }catch (Exception e)
                    {
                        Picasso.get().load(R.drawable.ic_mood_black_24dp).into(profileIV);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        sendIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notify = true;
                String message = chatET.getText().toString().trim();

                if(TextUtils.isEmpty(message))
                {
                    Toast.makeText(ChatActivity.this, "Cannot send empty message...", Toast.LENGTH_SHORT).show();
                }
                else{
                    sendMessage(message);
                }
                //reset edit text after sending a message
                chatET.setText("");
            }
        });

        chatET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.toString().trim().length() == 0)
                {
                    checktypingStatus("no one");
                }
                else{
                    checktypingStatus(recipientId);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        readMessages();
        seenMessage();

    }

    private void seenMessage() {
        seenRef = FirebaseDatabase.getInstance().getReference("Chats");
        seenListener = seenRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren())
                {
                    ChatModel chat = ds.getValue(ChatModel.class);
                    if(chat.getReceiver().equals(myId) && chat.getSender().equals(recipientId))
                    {
                        HashMap<String, Object> hasSeen = new HashMap<>();
                        hasSeen.put("isSeen",true);
                        ds.getRef().updateChildren(hasSeen);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readMessages() {
        chatModelList = new ArrayList<>();

        DatabaseReference dR = FirebaseDatabase.getInstance().getReference("Chats");
        dR.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatModelList.clear();
                for(DataSnapshot ds : dataSnapshot.getChildren())
                {
                    ChatModel chat = ds.getValue(ChatModel.class);
                    if(chat.getReceiver().equals(myId) && chat.getSender().equals(recipientId) ||
                            chat.getReceiver().equals(recipientId) && chat.getSender().equals(myId))
                    {
                        chatModelList.add(chat);
                    }
                    chatAdapter = new ChatAdapter(ChatActivity.this,chatModelList,recipientImage);
                    chatAdapter.notifyDataSetChanged();

                    recyclerView.setAdapter(chatAdapter);
                    recyclerView.scrollToPosition(chatAdapter.getItemCount()-1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendMessage(String message) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();


        String timeStamp = String.valueOf(System.currentTimeMillis());
        HashMap<String,Object> h = new HashMap<>();
        h.put("sender",myId);
        h.put("receiver",recipientId);
        h.put("message",message);
        h.put("timestamp",timeStamp);
        h.put("isSeen",false);
        databaseReference.child("Chats").push().setValue(h);

        final String msg = message;
        DatabaseReference database = FirebaseDatabase.getInstance().getReference("Users").child(myId);
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UsersModel model = dataSnapshot.getValue(UsersModel.class);

                if(notify)
                {
                    senNotification(recipientId,model.getName(),msg);
                }
                notify = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        /* Updating chat list node */
        final DatabaseReference refChat = FirebaseDatabase.getInstance().getReference("ChatList")
                .child(myId)
                .child(recipientId);

        refChat.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists())
                {
                    refChat.child("id").setValue(recipientId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        final DatabaseReference refChat2 = FirebaseDatabase.getInstance().getReference("ChatList")
                .child(recipientId)
                .child(myId);

        refChat2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists())
                {
                    refChat2.child("id").setValue(myId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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
                    Data data = new Data(myId,name+":"+msg,"New Message",recipientId,R.drawable.logo);
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

    private void checkUserstatus()
    {
        //checking if current user is logged on
        FirebaseUser user = mUser.getCurrentUser();

        if(user != null)
        {
            myId = user.getUid();
        }
        else
        {
            Intent intent = new Intent(this,LoginActivity.class);
            startActivity(intent);
        }
    }

    private void checkonlineStatus(String status){
        DatabaseReference dr = FirebaseDatabase.getInstance().getReference("Users").child(myId);
        HashMap<String,Object> hm = new HashMap<>();

        //updating value of online status
        hm.put("onlineStatus",status);
        dr.updateChildren(hm);

    }

    private void checktypingStatus(String typing){
        DatabaseReference dr = FirebaseDatabase.getInstance().getReference("Users").child(myId);
        HashMap<String,Object> hm = new HashMap<>();

        //updating value of online status
        hm.put("typingTo",typing);
        dr.updateChildren(hm);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);

        //hiding search view because it not needed here
        menu.findItem(R.id.action_search).setVisible(false);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        checkUserstatus();
        //set online
        checkonlineStatus("online");
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();

        //get timestamp
        String timestamp = String.valueOf(System.currentTimeMillis());


        //set offline with last seen
        checkonlineStatus(timestamp);

        //set typing status
        checktypingStatus("no one");
        seenRef.removeEventListener(seenListener);
    }

    @Override
    protected void onResume() {
        checkonlineStatus("online");
        super.onResume();

    }
}
