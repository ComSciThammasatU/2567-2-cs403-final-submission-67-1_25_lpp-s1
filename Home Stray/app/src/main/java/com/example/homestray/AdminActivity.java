package com.example.homestray;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AdminActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    boolean doubleBackToExitPressedOnce = false;
    List<Animal> animalList = new ArrayList<>();
    AdminAdapter adapter = new AdminAdapter(this, animalList);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        //Declaration
        drawerLayout = findViewById(R.id.drawerLayout_admin);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);
        RecyclerView recyclerView = findViewById(R.id.recyclerView_admin);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(adapter);


        ImageButton addAnimalButton = findViewById(R.id.addAnimalButton);
        FrameLayout frameLayout = findViewById(R.id.frameLayout);

        //Toolbar
        setSupportActionBar(toolbar);

        //Navigation Drawer Menu
        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        addAnimalButton.setOnClickListener(v -> {
            Intent intent = new Intent(AdminActivity.this, CreateAnimalProfile.class);
            startActivity(intent);
            finish();
        });

        //Set so that user won't exist app when press back button
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if(drawerLayout.isDrawerOpen(GravityCompat.START)){
                    drawerLayout.closeDrawer(GravityCompat.START);
                }else{
                    if(doubleBackToExitPressedOnce){
                        ActivityStateHelper.clearLastActivity(AdminActivity.this);
                        FirebaseAuth.getInstance().signOut();

                        Intent intent = new Intent(AdminActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        doubleBackToExitPressedOnce = true;
                        Toast.makeText(AdminActivity.this, "Press BACK again to exit", Toast.LENGTH_SHORT).show();

                        //Reset doubleBackToExitPressedOnce after 2 seconds
                        new android.os.Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
                    }
                }
            }
        });

        frameLayout.setOnClickListener(v -> {
            Intent intent = new Intent(AdminActivity.this, OpenAnimalProfileAdminActivity.class);
            startActivity(intent);
        });

        loadAnimalCreated();
    }

    private void loadAnimalCreated() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("animals");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                animalList.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Animal animal = data.getValue(Animal.class);
                    if (animal != null) {
                        animalList.add(animal);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        handleNavigation(item.getItemId());
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    public void handleNavigation(int itemId) {
        Intent intent = null;

        if (itemId == R.id.nav_create_animal_profile) {
            intent = new Intent(AdminActivity.this, CreateAnimalProfile.class);
        } else if (itemId == R.id.nav_logout) {
            FirebaseAuth.getInstance().signOut();
            intent = new Intent(AdminActivity.this, LoginActivity.class);
        }
        if (intent != null) {
            startActivity(intent);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        ActivityStateHelper.saveLastActivity(this, this.getClass().getName());
    }
}
