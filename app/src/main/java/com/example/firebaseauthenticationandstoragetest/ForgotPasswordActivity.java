package com.example.firebaseauthenticationandstoragetest;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity implements View.OnClickListener {
    private ProgressBar progressBar;
    private EditText etMail;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_forgot_password);

        mAuth = FirebaseAuth.getInstance();
        findViewById(R.id.btnSendlink).setOnClickListener(this);
        etMail= findViewById(R.id.etEmailverify);
        progressBar = findViewById(R.id.progressBar2);
    }

    private void SendEmailLink()
    {
        String email = etMail.getText().toString().trim();

        if(email.isEmpty())
        {
            etMail.setError("Must Enter A Valid Email For Link");
            etMail.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            etMail.setError("Please Enter A Valid Email!");
            etMail.requestFocus();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressBar.setVisibility(View.GONE);
                if(task.isSuccessful())
                {
                    Toast.makeText(ForgotPasswordActivity.this, "Password Reset Link Sent To The Email", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                    startActivity(i);
                    finish();
                }
                else{
                    Toast.makeText(ForgotPasswordActivity.this, "Error Sending Email Link", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.btnSendlink:
                SendEmailLink();
                break;
        }
    }
}
