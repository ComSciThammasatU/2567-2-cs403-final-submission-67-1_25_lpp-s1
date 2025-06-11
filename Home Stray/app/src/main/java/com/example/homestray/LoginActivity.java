package com.example.homestray;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private FirebaseDatabase db;
    private DatabaseReference ref;
    private EditText emailEditText, passwordEditText;
    boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activitiy_login);

        db = FirebaseDatabase.getInstance();
        ref = db.getReference("test_connection");
        auth = FirebaseAuth.getInstance();
        Button loginButton = findViewById(R.id.loginButton);
        TextView signupTextView = findViewById(R.id.signup);

        emailEditText = findViewById(R.id.editTextEmail);
        passwordEditText = findViewById(R.id.editTextPassword);

        //Test Firebase Realtime Database Connection
        ref.setValue("Hello Firebase!").addOnSuccessListener(aVoid -> {
            Log.d("FirebaseDebug", "Successfully connected to Firebase Realtime Database");
        }).addOnFailureListener(e -> {
            Log.e("FirebaseDebug", "Failed to connect: " + e.getMessage());
        });

        loginButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString();
            String pass = passwordEditText.getText().toString();

            if(!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                if(!pass.isEmpty()){
                    auth.signInWithEmailAndPassword(email, pass).addOnSuccessListener(authResult -> {
                        String userID = Objects.requireNonNull(auth.getCurrentUser()).getUid();

                        if(userID.equals(BuildConfig.ADMIN_USER_ID)){
                            Toast.makeText(LoginActivity.this, "Welcome Admin", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(LoginActivity.this, AdminActivity.class));
                        }else{
                            Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                            DatabaseReference userRef = db.getReference("users").child(userID);
                            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(snapshot.exists() && snapshot.hasChild("History") && snapshot.hasChild("Preference")){
                                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                    }else{
                                        startActivity(new Intent(LoginActivity.this, UserHistoryFormActivity.class));
                                    }
                                    finish();
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(LoginActivity.this, "Failed to check user data", Toast.LENGTH_SHORT).show();
                                }
                            });
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        }
                        finish();
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(LoginActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                        }
                    });
                }else{
                    passwordEditText.setError("Password cannot be empty");
                }
            }else if(email.isEmpty()){
                emailEditText.setError("Email cannot be empty");
            }else{
                emailEditText.setError("Please enter a valid email");
            }
        });

        signupTextView.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, SignupActivity.class));
            finish();
        });

        //Set so that user won't exist app when press back button
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if(doubleBackToExitPressedOnce){
                    ActivityStateHelper.clearLastActivity(LoginActivity.this);
                    System.exit(0);
                } else {
                    doubleBackToExitPressedOnce = true;
                    Toast.makeText(LoginActivity.this, "Press BACK again to exit", Toast.LENGTH_SHORT).show();

                    //Reset doubleBackToExitPressedOnce after 2 seconds
                    new android.os.Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
                }
            }
        });
    }
}
