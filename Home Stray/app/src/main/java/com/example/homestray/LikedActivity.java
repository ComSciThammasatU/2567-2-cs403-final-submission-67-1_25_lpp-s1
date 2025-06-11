package com.example.homestray;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class LikedActivity extends MainActivity {
    private LinearLayout likedLayout;
    private LikedAdapter adapter;
    private List<Animal> likedAnimalsList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liked);

        likedLayout = findViewById(R.id.likedLayout);

        RecyclerView recyclerView = findViewById(R.id.recyclerView_liked);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        adapter = new LikedAdapter(this, likedAnimalsList);
        recyclerView.setAdapter(adapter);


        //Declaration
        drawerLayout = findViewById(R.id.drawerLayout_liked);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);

        //Toolbar
        setSupportActionBar(toolbar);

        //Navigation Drawer Menu
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        //Set so that user won't exist app when press back button
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if(drawerLayout.isDrawerOpen(GravityCompat.START)){
                    drawerLayout.closeDrawer(GravityCompat.START);
                }else{
                    Intent intent = new Intent(LikedActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        });

        loadLikedAnimals();
    }

    private void loadLikedAnimals() {
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference likedRef = FirebaseDatabase.getInstance().getReference("likedAnimal").child(userID);
        DatabaseReference animalsRef = FirebaseDatabase.getInstance().getReference("animals");

        likedRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot likedSnapshot) {
                Set<String> likedIds = new HashSet<>();

                for (DataSnapshot likedAnimalSnapshot : likedSnapshot.getChildren()) {
                    String id = likedAnimalSnapshot.getKey(); // animalId
                    if (id != null) {
                        likedIds.add(id);
                        Log.d("LikedActivity", "Liked ID = " + id);
                    }
                }

                animalsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot animalsSnapshot) {
                        likedAnimalsList.clear();

                        for (DataSnapshot animalSnapshot : animalsSnapshot.getChildren()) {
                            Animal animal = animalSnapshot.getValue(Animal.class);
                            if (animal != null) {
                                String animalId = animalSnapshot.child("id").getValue(String.class);
                                if (likedIds.contains(animalId)) {
                                    animal.setId(animalId);
                                    likedAnimalsList.add(animal);
                                }
                            }
                        }
                        Log.d("FinalCount", "Liked animal list size: " + likedAnimalsList.size());
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(LikedActivity.this, "Failed to load animals.", Toast.LENGTH_SHORT).show();
                    }
                });            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(LikedActivity.this, "Failed to load liked animals.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return super.onNavigationItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        ActivityStateHelper.saveLastActivity(this, this.getClass().getName());
    }
}
