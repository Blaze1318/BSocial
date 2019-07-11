package com.example.firebaseauthenticationandstoragetest;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {
    EditText etEmail, etRemail, etPassword, etRepassword;
    ProgressBar progressBar;
    private FirebaseAuth mAuth;
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
        progressBar = findViewById(R.id.progressBar);
        findViewById(R.id.btnLogin).setOnClickListener(this);

    }

    private void Registeruser()
    {

        String email = etEmail.getText().toString().trim();
        String reemail = etRemail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String repassword = etRepassword.getText().toString().trim();

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
                    Toast.makeText(SignUpActivity.this, "Registration Successful!", Toast.LENGTH_SHORT).show();
                    final Intent i = new Intent(SignUpActivity.this,LoginActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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

    @Override
    public void onClick(View view) {
        switch(view.getId())
        {
            case R.id.btnLogin:
              Registeruser();
              break;
        }
    }



}
