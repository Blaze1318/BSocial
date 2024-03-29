package com.example.firebaseauthenticationandstoragetest;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.SharedPreferences;
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
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{
    EditText usernameText, passwordText;
    ProgressBar progressBar;
    private FirebaseAuth mAuth;
    SharedPreferences preferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        findViewById(R.id.tvSignUp).setOnClickListener(this);
        findViewById(R.id.btnLogin).setOnClickListener(this);
        findViewById(R.id.tvClickhere).setOnClickListener(this);
        usernameText = findViewById(R.id.etLoginEmail);
        passwordText = findViewById(R.id.etLoginpass);
        progressBar = findViewById(R.id.progressBar);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
    }

    private void Loginuser()
    {
        String email = usernameText.getText().toString().trim();
        final String pass = passwordText.getText().toString().trim();

        if(email.isEmpty())
        {
            usernameText.setError("Email Field Empty");
            usernameText.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            usernameText.setError("Invalid Email Address");
            usernameText.requestFocus();
            return;
        }
        if(pass.isEmpty())
        {
            passwordText.setError("Password Field Empty");
            passwordText.requestFocus();
            return;
        }

        if(pass.length() < 6)
        {
            passwordText.setError("Password Must Be 6 Characters Or More!");
            passwordText.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressBar.setVisibility(View.GONE);
                if(task.isSuccessful())
                {
                    //Saving user password for later use if needed
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("password",pass);
                    editor.apply();

                    //load home activity when login is successful
                    Toast.makeText(LoginActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show();
                    final Intent i = new Intent(LoginActivity.this, HomeActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                    finish();
                }
                else{
                    if(task.getException() instanceof FirebaseAuthUserCollisionException)
                    {
                        Toast.makeText(LoginActivity.this, "Login Failed!", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch(view.getId())
        {
            case R.id.tvSignUp:
                Intent i = new Intent(LoginActivity.this,SignUpActivity.class);
                startActivity(i);
                break;

            case R.id.btnLogin:
                Loginuser();
                break;

            case R.id.tvClickhere:
                Intent ii = new Intent(LoginActivity.this,ForgotPasswordActivity.class);
                startActivity(ii);
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = mAuth.getCurrentUser();
        if(user != null)
        {
            Intent i = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(i);
            finish();
        }
    }
}
