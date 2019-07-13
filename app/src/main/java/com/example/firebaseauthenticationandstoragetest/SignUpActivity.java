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
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {
    EditText etEmail, etRemail, etPassword, etRepassword , etDisplayName;
    ImageView ivProfile;
    private static final int CHOOSE_IMAGE = 101;
    ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private StorageReference mStorageRef;
    private String profileUrl;
    private String username;

    Uri uriProfileimage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_sign_up);
        mAuth = FirebaseAuth.getInstance();
        etEmail = findViewById(R.id.etEmail);
        etRemail = findViewById(R.id.etRemail);
        etPassword = findViewById(R.id.etPassword);
        etRepassword = findViewById(R.id.etRepassword);
        etDisplayName = findViewById(R.id.etDisplayname);
        progressBar = findViewById(R.id.progressBar);
        ivProfile = findViewById(R.id.ivProfile);
        findViewById(R.id.btnLogin).setOnClickListener(this);
        findViewById(R.id.ivProfile).setOnClickListener(this);

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
                    FirebaseUser user = mAuth.getCurrentUser();
                    uploadImageToFirebaseStorage();

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
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
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

    private void uploadImageToFirebaseStorage()
    {
        mStorageRef = FirebaseStorage.getInstance().getReference().child("profilepics");
        StorageReference imagepicker = mStorageRef.child(uriProfileimage.getLastPathSegment());
        if(uriProfileimage != null)
        {
            progressBar.setVisibility(View.VISIBLE);
            imagepicker.putFile(uriProfileimage)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressBar.setVisibility(View.GONE);
                            profileUrl = taskSnapshot.getMetadata().getReference().getDownloadUrl().toString();

                            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(username)
                                    .setPhotoUri(Uri.parse(profileUrl))
                                    .build();

                            FirebaseUser user = mAuth.getCurrentUser();
                            user.updateProfile(profile).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful())
                                    {
                                        Toast.makeText(SignUpActivity.this, "Profile Saved!", Toast.LENGTH_SHORT).show();
                                    }
                                    else{
                                        Toast.makeText(SignUpActivity.this, "Profile Not Saved!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });


        }
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
