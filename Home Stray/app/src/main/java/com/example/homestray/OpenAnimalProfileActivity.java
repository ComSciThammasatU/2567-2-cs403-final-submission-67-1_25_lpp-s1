package com.example.homestray;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class OpenAnimalProfileActivity extends AppCompatActivity {
    TextView textViewName, textViewDes;
    ImageView imageView;
    DatabaseReference animalRef;

    ValueEventListener animalValueListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_animal_profile);

        textViewName = findViewById(R.id.textViewName);
        textViewDes = findViewById(R.id.textViewDescription);
        imageView = findViewById(R.id.imageView);

        String animalId = getIntent().getStringExtra("animal_id");
        Log.d("Debug Open Animal Profile", "Received animal_id: " + animalId);

        if(animalId == null){
            Toast.makeText(this, "Invalid animal ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        animalRef = FirebaseDatabase.getInstance().getReference("animals").child(animalId);

        animalValueListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String name = snapshot.child("name").getValue(String.class) + " , " + snapshot.child("age").getValue(Integer.class) + " years old";
                    String imgURL = snapshot.child("imgURL").getValue(String.class);
                    String desc = "Breed : " + snapshot.child("breed").getValue(String.class) + "\n" +
                            "Type : " + snapshot.child("type").getValue(String.class) + "\n" +
                            "Color : " + snapshot.child("color").getValue(String.class) + "\n" +
                            "Habits : " + snapshot.child("habits").getValue(String.class) + "\n" +
                            "Medical : " + snapshot.child("medical").getValue(String.class) + "\n" +
                            "Sickness : " + snapshot.child("sickness").getValue(String.class) + "\n" +
                            "Foundation : " + snapshot.child("foundation").getValue(String.class) + "\n" +
                            "Contact : " + snapshot.child("contact").getValue(String.class) + "\n" +
                            "History : " + snapshot.child("history").getValue(String.class) + "\n";

                    textViewName.setText(name);
                    textViewDes.setText(desc);

                    Glide.with(OpenAnimalProfileActivity.this)
                            .load(imgURL)
                            .placeholder(R.drawable.placeholder_image)
                            .into(imageView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(OpenAnimalProfileActivity.this, "Failed to load animal data", Toast.LENGTH_SHORT).show();
            }
        };

        animalRef.addValueEventListener(animalValueListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (animalValueListener != null) {
            animalRef.removeEventListener(animalValueListener);
        }
    }

}
