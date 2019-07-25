package com.example.firebaseauthenticationandstoragetest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.Locale;

public class UsersProfileActivity extends AppCompatActivity {

    Toolbar toolbar;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    String recipientId;
    String recipientImage;

    ImageView userImage;
    EditText userDname,userEmail;
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
    }
}
