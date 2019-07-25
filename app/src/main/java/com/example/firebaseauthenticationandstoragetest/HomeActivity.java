package com.example.firebaseauthenticationandstoragetest;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;


import com.example.firebaseauthenticationandstoragetest.Fragments.ChatFragment;
import com.example.firebaseauthenticationandstoragetest.Fragments.HomeFragment;
import com.example.firebaseauthenticationandstoragetest.Fragments.UsersFragment;
import com.example.firebaseauthenticationandstoragetest.Fragments.ProfileFragment;
import com.example.firebaseauthenticationandstoragetest.Notifications.Token;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.picasso.Picasso;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseDatabase database;
    private DatabaseReference ref;

    private String myUID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Firebase Instances
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        ref = database.getReference("Users");
        myUID = mAuth.getUid();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        updateUserinfo();
        //update token
        updateToken(FirebaseInstanceId.getInstance().getToken());

        getSupportActionBar().setTitle("Users");
        getSupportFragmentManager().beginTransaction().replace(R.id.container,new UsersFragment()).commit();

        checkuserStatus();
    }

    public void updateToken(String token)
    {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Tokens");
        Token mToken = new Token(token);
        ref.child(myUID).setValue(mToken);

    }

    private void checkuserStatus()
    {
        //checking if user is currently logged on
        FirebaseUser user = mAuth.getCurrentUser();
        if(user == null)
        {
            Intent i = new Intent(HomeActivity.this, LoginActivity.class);
            startActivity(i);
            finish();
        }
        else{

            myUID = mAuth.getUid();

            //Saving user id within shared preferences
            SharedPreferences sp = getSharedPreferences("SP_USER",MODE_PRIVATE);
            SharedPreferences.Editor  editor = sp.edit();
            editor.putString("Current_USERID",myUID);
            editor.apply();
        }
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
           /* getSupportActionBar().setTitle("Home");
            getSupportFragmentManager().beginTransaction().replace(R.id.container,new HomeFragment()).commit();*/
            Toast.makeText(this, "Screen Not Ready Yet", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_profile) {
            getSupportActionBar().setTitle("Profile");
            getSupportFragmentManager().beginTransaction().replace(R.id.container,new ProfileFragment()).commit();
        } else if (id == R.id.nav_users) {
            getSupportActionBar().setTitle("Users");
            getSupportFragmentManager().beginTransaction().replace(R.id.container,new UsersFragment()).commit();
        }  else if (id == R.id.nav_message) {
            /*getSupportActionBar().setTitle("Chats");
            getSupportFragmentManager().beginTransaction().replace(R.id.container,new ChatFragment()).commit();*/
            Toast.makeText(this, "Screen Not Ready Yet", Toast.LENGTH_SHORT).show();
        }else if (id == R.id.nav_friends) {
            /*getSupportActionBar().setTitle("Friends");
            getSupportFragmentManager().beginTransaction().replace(R.id.container,new FriendsFragment()).commit();*/
            Toast.makeText(this, "Screen Not Ready Yet", Toast.LENGTH_SHORT).show();
        }else if (id == R.id.nav_friendrequest) {
            /*getSupportActionBar().setTitle("Friend Request");
            getSupportFragmentManager().beginTransaction().replace(R.id.container,new FriendRequestFragment()).commit();*/
            Toast.makeText(this, "Screen Not Ready Yet", Toast.LENGTH_SHORT).show();
        }  else if (id == R.id.nav_logout) {
            mAuth.signOut();
            Intent i = new Intent(HomeActivity.this,LoginActivity.class);
            startActivity(i);
            finish();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void updateUserinfo()
    {
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        final TextView tvProfilename = headerView.findViewById(R.id.tvUsername);
        final TextView tvEmail = headerView.findViewById(R.id.tvEmailAddress);
        final ImageView ivProfileimage = headerView.findViewById(R.id.ivImageProfile);

        Query query = ref.orderByChild("email").equalTo(user.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //check until required data is found
                for (DataSnapshot ds : dataSnapshot.getChildren())
                {
                    String name = ""+ ds.child("name").getValue();
                    String email = ""+ ds.child("email").getValue();
                    String image = ""+ ds.child("image").getValue();

                    //setting user data
                    tvProfilename.setText(name);
                    tvEmail.setText(email);

                    try {
                        Picasso.get().load(image).into(ivProfileimage);
                    }
                    catch (Exception e)
                    {
                        //Toast.makeText(HomeActivity.this, "Error Loading Image", Toast.LENGTH_SHORT).show();
                        Log.d("Error: ",e.getMessage());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //Toast.makeText(HomeActivity.this, "Unexpected Error Occurred", Toast.LENGTH_SHORT).show();
                Log.d("onCancelled: ", databaseError.getMessage());
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        checkuserStatus();
    }

    @Override
    protected void onStart() {
        super.onStart();

        //checking if user is currently logged on
        checkuserStatus();

    }
}
