package com.example.firebaseauthenticationandstoragetest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

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

    String recipientId;
    String myId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        toolbar.setTitle("");
        recyclerView = findViewById(R.id.recycleChat);
        profileIV = findViewById(R.id.profileIV);
        nameTV = findViewById(R.id.tvUsern);
        userstatusTV = findViewById(R.id.userStatus);
        chatET = findViewById(R.id.messageEt);
        sendIB = findViewById(R.id.ibSend);

        //firebase instance
        mUser = FirebaseAuth.getInstance();

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
                    String image = ""+ds.child("image").getValue();

                    nameTV.setText(name);
                    try{
                        Picasso.get().load(image).placeholder(R.drawable.ic_mood_black_24dp).into(profileIV);
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
                String message = chatET.getText().toString().trim();

                if(TextUtils.isEmpty(message))
                {
                    Toast.makeText(ChatActivity.this, "Cannot send empty message...", Toast.LENGTH_SHORT).show();
                }
                else{
                    sendMessage(message);
                }
            }
        });

    }

    private void sendMessage(String message) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        HashMap<String,Object> h = new HashMap<>();
        h.put("sender",myId);
        h.put("receiver",recipientId);
        h.put("message",message);
        databaseReference.child("Chats").push().setValue(h);

        //reset edit text after sending a message
        chatET.setText("");
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
        super.onStart();
    }
}
