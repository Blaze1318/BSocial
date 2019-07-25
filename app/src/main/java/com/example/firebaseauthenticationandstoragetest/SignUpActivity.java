package com.example.firebaseauthenticationandstoragetest;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {
    EditText etEmail, etRemail, etPassword, etRepassword , etDisplayName;
    ImageView ivProfile;
    TextView tvLogin;
    private static final int CHOOSE_IMAGE = 101;
    ProgressBar progressBar;

    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private FirebaseUser user;
    private DatabaseReference ref;
    private StorageReference mStorageRef;
    private String username;

    //Storage path
    String storagePath = "Users_Profile_Imgs/";

    //check for image
    String profileImage = "image";
    Uri uriProfileimage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_sign_up);
        mAuth = FirebaseAuth.getInstance();
        etEmail = findViewById(R.id.etUsersEmail);
        etRemail = findViewById(R.id.etRemail);
        etPassword = findViewById(R.id.etPassword);
        etRepassword = findViewById(R.id.etRepassword);
        etDisplayName = findViewById(R.id.etDisplayname);
        tvLogin = findViewById(R.id.tvLogin);
        progressBar = findViewById(R.id.progressBar);
        ivProfile = findViewById(R.id.ivProfile);
        findViewById(R.id.btnLogin).setOnClickListener(this);
        findViewById(R.id.ivProfile).setOnClickListener(this);
        findViewById(R.id.tvLogin).setOnClickListener(this);

    }

    private void Registeruser()
    {

        final String email = etEmail.getText().toString().trim();
        String reemail = etRemail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String repassword = etRepassword.getText().toString().trim();
        username = etDisplayName.getText().toString().trim();


        if(username.isEmpty())
        {
            etDisplayName.setError("Display name Cannot Be Empty");
            etDisplayName.requestFocus();
            return;
        }
        if (email.isEmpty())
        {
            etEmail.setError("Email is required");
            etEmail.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            etEmail.setError("Please Enter A Valid Email!");
            etEmail.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(reemail).matches())
        {
            etRemail.setError("Please Enter A Valid Email!");
            etRemail.requestFocus();
            return;
        }

        if(reemail.isEmpty())
        {
            etRemail.setError("Must Retype Email");
            etRemail.requestFocus();
            return;
        }

        if (password.isEmpty())
        {
            etPassword.setError("Password is required");
            etPassword.requestFocus();
            return;
        }

        if(password.length() < 6)
        {
            etPassword.setError("Password Must Be 6 Characters Or More!");
            etPassword.requestFocus();
            return;
        }

        if(repassword.isEmpty())
        {
            etRepassword.setError("Must Retype Password");
            etRepassword.requestFocus();
            return;
        }

        if (!reemail.equals(email))
        {
            etRemail.setError("Incorrect! Emails Do Not Match!");
            etRemail.requestFocus();
            return;
        }

        if (!repassword.equals(password) )
        {
            etRepassword.setError("Incorrect! Passwords Do Not Match!");
            etRepassword.requestFocus();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressBar.setVisibility(View.GONE);
                if(task.isSuccessful())
                {
                    user = mAuth.getCurrentUser();
                    //Storing user info within realtime database
                    String email = user.getEmail();
                    String userid = user.getUid();
                    String name = username;

                    //storing info using hashmap
                    HashMap<Object,String> userinfo = new HashMap<>();
                    userinfo.put("email",email);
                    userinfo.put("userid",userid);
                    userinfo.put("name",name);
                    userinfo.put("image","");
                    userinfo.put("onlineStatus","online");
                    userinfo.put("typingTo","no one");
                    //getting firebase database instance
                    database = FirebaseDatabase.getInstance();

                    //creating database reference
                    ref = database.getReference("Users");
                    //place hashmap data within database
                    ref.child(userid).setValue(userinfo);


                    if(uriProfileimage != null) {
                        //uploading and updating user profile image if one exist
                        uploadImageToFirebaseStorage(uriProfileimage);
                    }


                    user.sendEmailVerification()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d("Email:", "Email sent.");
                                    }
                                }
                            });
                    Toast.makeText(SignUpActivity.this, "Registration Successful!", Toast.LENGTH_SHORT).show();
                    final Intent i = new Intent(SignUpActivity.this,LoginActivity.class);
                    startActivity(i);
                    finish();
                }
                else{
                   if(task.getException() instanceof FirebaseAuthUserCollisionException)
                   {
                       Toast.makeText(SignUpActivity.this, "Email is already in use!", Toast.LENGTH_SHORT).show();
                   }
                   else{
                       Toast.makeText(SignUpActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                   }
                }
            }
        });

    }

    private void ImageChooser()
    {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(i,"Select Profile Image"),CHOOSE_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CHOOSE_IMAGE && resultCode == RESULT_OK && data.getData() != null)
        {
            uriProfileimage = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),uriProfileimage);
                ivProfile.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadImageToFirebaseStorage(Uri uri)
    {
        user = mAuth.getCurrentUser();

        //creating image url path
        String filePathAndName = storagePath + "" + profileImage + "_"+ user.getUid();

        mStorageRef = FirebaseStorage.getInstance().getReference();
        StorageReference mStorage2 = mStorageRef.child(filePathAndName);

        mStorage2.putFile(uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while(!uriTask.isSuccessful());
                        Uri downloadUrl = uriTask.getResult();

                        //check if image is uploaded or not
                        if(uriTask.isSuccessful()) {
                            //image uploaded
                            HashMap<String, Object> results = new HashMap<>();
                            results.put(profileImage, downloadUrl.toString());

                            ref.child(user.getUid()).updateChildren(results)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            //do nothing
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(SignUpActivity.this, "Image not uploaded", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                        else{
                            //not uploaded
                            Toast.makeText(SignUpActivity.this, "Error Uploading Profile Image", Toast.LENGTH_SHORT).show();
                        }

                    }
                })

                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SignUpActivity.this, "Profile Image Not Uploaded", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onClick(View view) {
        switch(view.getId())
        {
            case R.id.btnLogin:
              Registeruser();
              break;

            case R.id.ivProfile:
                ImageChooser();
                break;

            case R.id.tvLogin:
                Intent i = new Intent(SignUpActivity.this,LoginActivity.class);
                startActivity(i);
        }
    }



}
