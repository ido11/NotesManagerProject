package com.example.notesmanager.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.notesmanager.R;
import com.example.notesmanager.Objects.ToastManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText email;
    private EditText password;
    private Button login;
    private TextView changeRegister;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // connect the layouts
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        login = findViewById(R.id.login);
        changeRegister = findViewById(R.id.changeRegister);

        auth = FirebaseAuth.getInstance();

        // login the user when clicking on register button
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailText = email.getText().toString();
                String passwordText = password.getText().toString();
                loginUser(emailText, passwordText);
            }
        });

        // if the user clicks the register button, change to register activity
        changeRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                finish();
            }
        });
    }

    // Login the user using firebase auth
    private void loginUser(String emailText, String passwordText) {
        if(emailText == null || emailText.isEmpty() || passwordText == null || passwordText.isEmpty()){
            ToastManager.makeCustomToast(LoginActivity.this, "Empty credentials", Color.rgb(128,0,0), Color.BLACK);
            return;
        }
        auth.signInWithEmailAndPassword(emailText, passwordText).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                startActivity(new Intent(LoginActivity.this, MenuActivity.class));
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                ToastManager.makeCustomToast(LoginActivity.this ,e.getMessage(), Color.rgb(128,0,0), Color.BLACK);
            }
        });

    }

    public void onBackPressed(){
        startActivity(new Intent(LoginActivity.this, LoginActivity.class));
        finish();
    }
}