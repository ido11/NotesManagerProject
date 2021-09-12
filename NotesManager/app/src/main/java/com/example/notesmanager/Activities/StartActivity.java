package com.example.notesmanager.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.notesmanager.R;
import com.google.firebase.auth.FirebaseAuth;

public class StartActivity extends AppCompatActivity {

    private FirebaseAuth auth;


    // This class starts the project
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        // get current user from firebase
        auth = FirebaseAuth.getInstance();

        // if there is a logged in user got to menu activity
        if(auth.getCurrentUser() != null){
            startActivity(new Intent(StartActivity.this, MenuActivity.class));
        }
        // else go to registration activity
        else{
            startActivity(new Intent(StartActivity.this, RegisterActivity.class));
        }

    }
}
