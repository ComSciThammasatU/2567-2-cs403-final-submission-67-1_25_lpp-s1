package com.example.homestray;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yuyakaido.android.cardstackview.CardStackLayoutManager;
import com.yuyakaido.android.cardstackview.CardStackListener;
import com.yuyakaido.android.cardstackview.CardStackView;
import com.yuyakaido.android.cardstackview.Direction;
import com.yuyakaido.android.cardstackview.Duration;
import com.yuyakaido.android.cardstackview.SwipeAnimationSetting;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    boolean doubleBackToExitPressedOnce = false;
    CardStackView cardStackView;
    CardStackLayoutManager cardStackLayoutManager;
    CardAdapter cardAdapter;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    List<Animal> animalList;

    FirebaseDatabase database;
    DatabaseReference animalsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Declaration
        database = FirebaseDatabase.getInstance();
        animalsRef = database.getReference("animals");

        drawerLayout = findViewById(R.id.drawerLayout_swipe);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);

        animalList = new ArrayList<>();
        cardAdapter = new CardAdapter(new ArrayList<>(), this);
        cardStackView = findViewById(R.id.card_stack_view);

        cardStackLayoutManager = new CardStackLayoutManager(this, new CardStackListener() {
            @Override
            public void onCardDragging(Direction direction, float ratio) {}
            @Override
            public void onCardSwiped(Direction direction) {
                if(direction == Direction.Right){
                    Toast.makeText(MainActivity.this, "Liked!", Toast.LENGTH_SHORT).show();
                    Animal likedAnimal = animalList.get(cardStackLayoutManager.getTopPosition() - 1);
                    saveToLikedList(likedAnimal);
                }else if(direction == Direction.Left){
                    Toast.makeText(MainActivity.this, "Disliked!", Toast.LENGTH_SHORT).show();
                    Animal dislikedAnimal = animalList.get(cardStackLayoutManager.getTopPosition() - 1);
                    saveToDislikedAnimal(dislikedAnimal);
                }
            }
            @Override
            public void onCardRewound() {}
            @Override
            public void onCardCanceled() {}
            @Override
            public void onCardAppeared(View view, int position) {}
            @Override
            public void onCardDisappeared(View view, int position) {}
        });

        cardStackView.setLayoutManager(cardStackLayoutManager);
        cardStackView.setAdapter(cardAdapter);

        loadAnimalsFromFirebase();

        //Toolbar
        setSupportActionBar(toolbar);

        //Navigation Drawer Menu
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        ImageButton like_btn = findViewById(R.id.like_btn);
        ImageButton not_like_btn = findViewById(R.id.not_like);


        like_btn.setOnClickListener(v -> {
            SwipeAnimationSetting setting = new SwipeAnimationSetting.Builder()
                    .setDirection(Direction.Right)
                    .setDuration(Duration.Normal.duration)
                    .build();
            cardStackLayoutManager.setSwipeAnimationSetting(setting);
            cardStackView.swipe();
        });

        not_like_btn.setOnClickListener(v -> {
            SwipeAnimationSetting setting = new SwipeAnimationSetting.Builder()
                    .setDirection(Direction.Left)
                    .setDuration(Duration.Normal.duration)
                    .build();
            cardStackLayoutManager.setSwipeAnimationSetting(setting);
            cardStackView.swipe();
        });

        navigationView.setNavigationItemSelectedListener(this);

        //Set so that user won't exist app when press back button
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if(drawerLayout.isDrawerOpen(GravityCompat.START)){
                    drawerLayout.closeDrawer(GravityCompat.START);
                }else{
                    if(doubleBackToExitPressedOnce){
                        ActivityStateHelper.clearLastActivity(MainActivity.this);
                        FirebaseAuth.getInstance().signOut();

                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        doubleBackToExitPressedOnce = true;
                        Toast.makeText(MainActivity.this, "Press BACK again to exit", Toast.LENGTH_SHORT).show();

                        //Reset doubleBackToExitPressedOnce after 2 seconds
                        new android.os.Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
                    }
                }
            }
        });
    }

    private void saveToDislikedAnimal(Animal dislikedAnimal) {
        String userId = FirebaseAuth.getInstance().getUid();
        if (userId == null || dislikedAnimal.getId() == null) return;

        DatabaseReference likedAnimalsRef = FirebaseDatabase.getInstance()
                .getReference("dislikedAnimal")
                .child(userId)
                .child(dislikedAnimal.getId());

        likedAnimalsRef.child(dislikedAnimal.getName()).setValue(true);
        Log.d("Disliked Animal", "Disliked Animal : " + dislikedAnimal.getName() + "\n" + "Disliked Animal ID : " + dislikedAnimal.getId());
        DatabaseReference dislikedAnimalsRef = FirebaseDatabase.getInstance().getReference("dislikedAnimal")
                .child(Objects.requireNonNull(dislikedAnimal.getId()));
        dislikedAnimalsRef.push().child(dislikedAnimal.getName()).setValue(true);
        Log.d("Disliked Animal","Disliked Animal : " + dislikedAnimal.getName() + "\n" + "Disliked Animal ID : " + dislikedAnimal.getId());
    }

    private void saveToLikedList(Animal likedAnimal) {
        String userId = FirebaseAuth.getInstance().getUid();
        if (userId == null || likedAnimal.getId() == null) return;

        DatabaseReference likedAnimalsRef = FirebaseDatabase.getInstance()
                .getReference("likedAnimal")
                .child(userId)
                .child(likedAnimal.getId());

        likedAnimalsRef.child(likedAnimal.getName()).setValue(true);
        Log.d("Liked Animal", "Liked Animal : " + likedAnimal.getName() + "\n" + "Liked Animal ID : " + likedAnimal.getId());
    }

    private void loadAnimalsFromFirebase() {
        String user = FirebaseAuth.getInstance().getUid();
        if(user == null) return;

        Set<String> excludeAnimalIds = new HashSet<>();

        DatabaseReference dislikedRef = FirebaseDatabase.getInstance().getReference("dislikedAnimal").child(user);
        DatabaseReference likedRef = FirebaseDatabase.getInstance().getReference("likedAnimal").child(user);

        dislikedRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dislikedSnapshot) {
                for (DataSnapshot snapshot : dislikedSnapshot.getChildren()) {
                    excludeAnimalIds.add(snapshot.getKey());
                }

                likedRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot likedSnapshot) {
                        for (DataSnapshot snapshot: likedSnapshot.getChildren()){
                            excludeAnimalIds.add(snapshot.getKey());
                        }
                        animalsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                animalList.clear();
                                if (!snapshot.exists()) {
                                    Animal defaultAnimal = new Animal();
                                    defaultAnimal.setName("None Exist Animal");
                                    defaultAnimal.setImgURL("https://i.imgur.com/ALwDyak.jpeg");
                                    defaultAnimal.setBreed("None");
                                    defaultAnimal.setType("None");
                                    defaultAnimal.setColor("None");
                                    defaultAnimal.setHabits("None");
                                    defaultAnimal.setAge(0);
                                    defaultAnimal.setLat((double) 0);
                                    defaultAnimal.setLng((double) 0);
                                    animalList.add(defaultAnimal);
                                    cardAdapter.setAnimalList(animalList);
                                    cardAdapter.notifyDataSetChanged();
                                    return;
                                }

                                for (DataSnapshot animalSnapshot : snapshot.getChildren()) {
                                    String animalId = animalSnapshot.child("id").getValue(String.class);
                                    String name = animalSnapshot.child("name").getValue(String.class);
                                    String imgURL = animalSnapshot.child("imgURL").getValue(String.class);
                                    String breed = animalSnapshot.child("breed").getValue(String.class);
                                    String type = animalSnapshot.child("type").getValue(String.class);
                                    String color = animalSnapshot.child("color").getValue(String.class);
                                    String habits = animalSnapshot.child("habits").getValue(String.class);
                                    Integer age = animalSnapshot.child("age").getValue(Integer.class);

                                    if (animalId != null && name != null && imgURL != null &&
                                            !imgURL.trim().isEmpty() && age != null && type != null
                                            && breed != null && color != null && habits != null
                                            && !excludeAnimalIds.contains(animalId)) {
                                        Animal animal = new Animal(animalId, name, imgURL, age, breed, type, color, habits);
                                        animalList.add(animal);
                                    }
                                }

                                if (animalList.isEmpty()) {
                                    Animal defaultAnimal = new Animal();
                                    defaultAnimal.setName("None Exist Animal");
                                    defaultAnimal.setImgURL("https://i.imgur.com/ALwDyak.jpeg");
                                    animalList.add(defaultAnimal);
                                }

                                if (animalList != null && !animalList.isEmpty()) {
                                    cardAdapter.setAnimalList(animalList);
                                    cardStackView.setAdapter(cardAdapter);
                                } else {
                                    Toast.makeText(MainActivity.this, "Can't find animal in system", Toast.LENGTH_SHORT).show();
                                }
                                cardAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(MainActivity.this, "Failed to load data.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Failed to load data.", Toast.LENGTH_SHORT).show();
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

        if (itemId == R.id.nav_swipe_animal) {
            intent = new Intent(this, MainActivity.class);
        } else if (itemId == R.id.nav_liked_animal) {
            intent = new Intent(this, LikedActivity.class);
        } else if (itemId == R.id.nav_user_history) {
            intent = new Intent(this, UserHistoryShow.class);
        } else if (itemId == R.id.nav_user_preference) {
            intent = new Intent(this, UserPreferenceActivity.class);
        } else if (itemId == R.id.nav_search_animal) {
            intent = new Intent(this, SearchActivity.class);
        } else if (itemId == R.id.nav_logout) {
            FirebaseAuth.getInstance().signOut();
            intent = new Intent(this, LoginActivity.class);
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
