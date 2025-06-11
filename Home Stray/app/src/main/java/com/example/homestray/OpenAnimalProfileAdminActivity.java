package com.example.homestray;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class OpenAnimalProfileAdminActivity extends AppCompatActivity {
    TextView textViewName, textViewDes;
    ImageView imageView;
    DatabaseReference animalRef;
    ImageButton editButton, deleteButton;
    ValueEventListener animalValueEvent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_animal_profile_admin);

        textViewName = findViewById(R.id.textViewName);
        textViewDes = findViewById(R.id.textViewDescription);
        imageView = findViewById(R.id.imageView);
        editButton = findViewById(R.id.imageButtonEdit);
        deleteButton = findViewById(R.id.imageButtonDelete);

        String animal_id = getIntent().getStringExtra("animal_id");
        Log.d("Animal ID Received", "Animal ID: " + animal_id);
        if(animal_id == null){
            Toast.makeText(OpenAnimalProfileAdminActivity.this, "Animal ID Not Found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent(OpenAnimalProfileAdminActivity.this, AdminActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        editButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, EditAnimalProfileActivity.class);
            intent.putExtra("animal_id", animal_id);
            startActivity(intent);
        });

        deleteButton.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Delete Animal")
                    .setMessage("Are you sure you want to delete this animal?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        if (animal_id == null || animal_id.trim().isEmpty()) {
                            Toast.makeText(this, "Animal ID is invalid. Cannot delete.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Log.d("DEBUG_DELETE", "Deleting animal with ID: " + animal_id);

                        DatabaseReference dislikedRef = FirebaseDatabase.getInstance().getReference("dislikedAnimal").child(animal_id);
                        DatabaseReference likedRef = FirebaseDatabase.getInstance().getReference("likedAnimal").child(animal_id);
                        DatabaseReference deleteAnimalRef = FirebaseDatabase.getInstance().getReference("animals").child(animal_id);
                        dislikedRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                                    String userId = userSnapshot.getKey();
                                    if (userSnapshot.hasChild(animal_id)){
                                        dislikedRef.child(userId).child(animal_id).removeValue();
                                        Log.d("DeleteLikedAnimal", "Delete Animal ID : " + animal_id + " From User : " + userId);
                                    }
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.d("Firebase", "Failed : " + error.getMessage());
                            }
                        });
                        likedRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                                    String userId = userSnapshot.getKey();
                                    if (userSnapshot.hasChild(animal_id)){
                                        likedRef.child(userId).child(animal_id).removeValue();
                                        Log.d("DeleteLikedAnimal", "Delete Animal ID : " + animal_id + " From User : " + userId);
                                    }
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.d("Firebase", "Failed : " + error.getMessage());
                            }
                        });
                        deleteAnimalRef.removeValue().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(this, "Deleted Animal Profile", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(this, AdminActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(this, "Failed to Delete Animal Profile", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }).setNegativeButton("No", null).show();
        });

        animalRef = FirebaseDatabase.getInstance().getReference("animals");

        animalValueEvent = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot animalSnapshot : snapshot.getChildren()) {
                        String animalID = animalSnapshot.child("id").getValue(String.class);
                        if(animalID != null){
                            if(animalID.equals(animal_id)){
                                String name = animalSnapshot.child("name").getValue(String.class) + " , " + animalSnapshot.child("age").getValue(Integer.class) + " years old";
                                String imgURL = animalSnapshot.child("imgURL").getValue(String.class);
                                String desc = "Breed : " + animalSnapshot.child("breed").getValue(String.class) + "\n" +
                                        "Type : " + animalSnapshot.child("type").getValue(String.class) + "\n" +
                                        "Color : " + animalSnapshot.child("color").getValue(String.class) + "\n" +
                                        "Habits : " + animalSnapshot.child("habits").getValue(String.class) + "\n" +
                                        "Medical : " + animalSnapshot.child("medical").getValue(String.class) + "\n" +
                                        "Sickness : " + animalSnapshot.child("sickness").getValue(String.class) + "\n" +
                                        "Foundation : " + animalSnapshot.child("foundation").getValue(String.class) + "\n" +
                                        "Address : " + animalSnapshot.child("address").getValue(String.class) + "\n" +
                                        "Contact : " + animalSnapshot.child("contact").getValue(String.class) + "\n" +
                                        "History : " + animalSnapshot.child("history").getValue(String.class) + "\n";

                                textViewName.setText(name);
                                textViewDes.setText(desc);

                                Glide.with(OpenAnimalProfileAdminActivity.this)
                                        .load(imgURL)
                                        .placeholder(R.drawable.placeholder_image)
                                        .into(imageView);
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(OpenAnimalProfileAdminActivity.this, "Failed to load animal data", Toast.LENGTH_SHORT).show();
            }
        };

        animalRef.addValueEventListener(animalValueEvent);
    }

    @Override
    protected void onStop() {
        super.onStop();
        animalRef.removeEventListener(animalValueEvent);
    }
}
