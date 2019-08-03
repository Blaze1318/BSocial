package com.example.firebaseauthenticationandstoragetest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

public class ChangePasswordActivity extends AppCompatActivity {

    private EditText etCurrent,etNew,etConfirm;
    private String currentPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        etCurrent = findViewById(R.id.etCurrentpass);
        etNew = findViewById(R.id.etNewpass);
        etConfirm = findViewById(R.id.etConfirmNewpass);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        currentPassword = preferences.getString("password","");

        changePassword(currentPassword);
    }

    //Changing current password
    private void changePassword(String currentPassword)
    {
        if (etCurrent.getText().toString().trim().equals(currentPassword)) {
            if (etNew.getText().toString().trim().equals(currentPassword)) {
                Toast.makeText(this, "New password should be different from old one!", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
