package com.example.homestray;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class UserHistoryShow extends MainActivity {
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    TextView textViewDes;
    ImageView imageView, showChoosePhoto01, showChoosePhoto02, showChoosePhoto03;
    DatabaseReference userRef, imageRef;
    ImageButton imageButtonEdit;

    ValueEventListener userValueListener;
    ValueEventListener imageValueListener;

    private ActivityResultLauncher<Intent> editHistoryLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_history);

        drawerLayout = findViewById(R.id.drawerLayout_user_history);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);
        textViewDes = findViewById(R.id.textViewDescription);
        imageView = findViewById(R.id.imageView);
        showChoosePhoto01 = findViewById(R.id.showChoosePhoto01);
        showChoosePhoto02 = findViewById(R.id.showChoosePhoto02);
        showChoosePhoto03 = findViewById(R.id.showChoosePhoto03);
        imageButtonEdit = findViewById(R.id.imageButtonEdit);

        editHistoryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if(result.getResultCode() == RESULT_OK){
                        loadUserData();
                    }
                }
        );

        //Toolbar
        setSupportActionBar(toolbar);

        //Navigation Drawer Menu
        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        //Set so that user won't exist app when press back button
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if(drawerLayout.isDrawerOpen(GravityCompat.START)){
                    drawerLayout.closeDrawer(GravityCompat.START);
                }else{
                    Intent intent = new Intent(UserHistoryShow.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        });

        String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        userRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("History");
        imageRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("Images");

        userValueListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String firstName = snapshot.child("firstName").getValue(String.class);
                    String lastName = snapshot.child("lastName").getValue(String.class);
                    String phone = snapshot.child("phone").getValue(String.class);
                    String line = snapshot.child("line").getValue(String.class);
                    String salary = snapshot.child("salary").getValue(String.class);
                    String freeTime = snapshot.child("freeTime").getValue(String.class);
                    String consent = snapshot.child("consent").getValue(String.class);
                    String address = snapshot.child("address").getValue(String.class);

                    imageButtonEdit.setOnClickListener(v -> {
                        Intent intent = new Intent(UserHistoryShow.this, EditUserHistoryActivity.class);
                        intent.putExtra("firstName", firstName);
                        intent.putExtra("lastName", lastName);
                        intent.putExtra("phone", phone);
                        intent.putExtra("line", line);
                        intent.putExtra("salary", salary);
                        intent.putExtra("freeTime", freeTime);
                        intent.putExtra("consent", consent);
                        intent.putExtra("address", address);
                        intent.putExtra("user_id", userId);
                        Log.d("UserHistoryShow Intent Send",
                                "User ID : " + userId + "\n" +
                                        "First Name : " + firstName + "\n" +
                                        "Last Name : " + lastName + "\n" +
                                        "Phone : " + phone + "\n" +
                                        "Salary : " + salary + "\n" +
                                        "Free Time : " + freeTime + "\n" +
                                        "Consent : " + consent + "\n" +
                                        "Address : " + address + "\n");
                        editHistoryLauncher.launch(intent);
                    });

                    String desc = "First Name: " + firstName + "\n"
                            + "Last Name : " + lastName + "\n"
                            + "Phone : " + phone + "\n"
                            + "Line : " + line + "\n"
                            + "Salary : " + salary + "\n"
                            + "Free Time : " + freeTime + "\n"
                            + "Consent : " + consent + "\n"
                            + "Address : " + address + "\n";
                    textViewDes.setText(desc);
                } else {
                    Toast.makeText(UserHistoryShow.this, "No history found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UserHistoryShow.this, "Failed to load user history", Toast.LENGTH_SHORT).show();
            }
        };
        userRef.addValueEventListener(userValueListener);

        imageValueListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String profile = snapshot.child("profile").getValue(String.class);
                String home1 = snapshot.child("home1").getValue(String.class);
                String home2 = snapshot.child("home2").getValue(String.class);
                String home3 = snapshot.child("home3").getValue(String.class);

                Glide.with(UserHistoryShow.this).load(profile).into(imageView);
                Glide.with(UserHistoryShow.this).load(home1).into(showChoosePhoto01);
                Glide.with(UserHistoryShow.this).load(home2).into(showChoosePhoto02);
                Glide.with(UserHistoryShow.this).load(home3).into(showChoosePhoto03);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UserHistoryShow.this, "No Photo found", Toast.LENGTH_SHORT).show();
            }
        };
        imageRef.addValueEventListener(imageValueListener);


    }

    private void loadUserData() {
        String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        userRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("History");
        imageRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("Images");

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String firstName = snapshot.child("firstName").getValue(String.class);
                    String lastName = snapshot.child("lastName").getValue(String.class);
                    String phone = snapshot.child("phone").getValue(String.class);
                    String line = snapshot.child("line").getValue(String.class);
                    String salary = snapshot.child("salary").getValue(String.class);
                    String freeTime = snapshot.child("freeTime").getValue(String.class);
                    String consent = snapshot.child("consent").getValue(String.class);
                    String address = snapshot.child("address").getValue(String.class);

                    String desc = "First Name: " + firstName + "\n"
                            + "Last Name : " + lastName + "\n"
                            + "Phone : " + phone + "\n"
                            + "Line : " + line + "\n"
                            + "Salary : " + salary + "\n"
                            + "Free Time : " + freeTime + "\n"
                            + "Consent : " + consent + "\n"
                            + "Address : " + address + "\n";
                    textViewDes.setText(desc);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UserHistoryShow.this, "Failed to load user history", Toast.LENGTH_SHORT).show();
            }
        });

        imageRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String profile = snapshot.child("profile").getValue(String.class);
                String home1 = snapshot.child("home1").getValue(String.class);
                String home2 = snapshot.child("home2").getValue(String.class);
                String home3 = snapshot.child("home3").getValue(String.class);

                Glide.with(UserHistoryShow.this).load(profile).into(imageView);
                Glide.with(UserHistoryShow.this).load(home1).into(showChoosePhoto01);
                Glide.with(UserHistoryShow.this).load(home2).into(showChoosePhoto02);
                Glide.with(UserHistoryShow.this).load(home3).into(showChoosePhoto03);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(userValueListener != null){
            userRef.removeEventListener(userValueListener);
        }
        if(imageValueListener != null){
            imageRef.removeEventListener(imageValueListener);
        }
    }
}
