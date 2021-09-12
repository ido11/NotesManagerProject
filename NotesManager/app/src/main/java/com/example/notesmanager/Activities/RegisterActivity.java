package com.example.notesmanager.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.notesmanager.R;
import com.example.notesmanager.Objects.ToastManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    private EditText email;
    private EditText password;
    private Button register;
    private TextView changeLogin;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // connect the layouts
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        register = findViewById(R.id.register);
        changeLogin = findViewById(R.id.changeLogin);

        auth = FirebaseAuth.getInstance();

        // register the user when clicking on register button
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailText = email.getText().toString();
                String passwordText = password.getText().toString();
                if(checkValidity(emailText, passwordText)){
                    registerUser(emailText, passwordText);
                }
            }
        });

        // if the user clicks the login button, change to login activity
        changeLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
            }
        });
    }

    // Register the user using firebase auth
    private void registerUser(String emailText, String passwordText) {
        auth.createUserWithEmailAndPassword(emailText, passwordText).addOnCompleteListener(RegisterActivity.this , new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    startActivity(new Intent(RegisterActivity.this, MenuActivity.class));
                    finish();
                }
                else{
                    ToastManager.makeCustomToast(RegisterActivity.this, task.getException().getMessage(), Color.rgb(128,0,0), Color.BLACK);
                }
            }
        });
    }

    // Checks if the values are valid for registration
    private boolean checkValidity(String email, String password){
        if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password)){
            ToastManager.makeCustomToast(RegisterActivity.this, "Empty credentials", Color.rgb(128,0,0), Color.BLACK);
        }
        else if(password.length() < 6){
            ToastManager.makeCustomToast(RegisterActivity.this, "Password too short", Color.rgb(128,0,0), Color.BLACK);
        }
        else{
            return true;
        }
        return false;
    }

    public void onBackPressed(){
        startActivity(new Intent(RegisterActivity.this, RegisterActivity.class));
        finish();
    }
}