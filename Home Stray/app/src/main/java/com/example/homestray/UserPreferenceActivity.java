package com.example.homestray;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class UserPreferenceActivity extends MainActivity {
    SeekBar rangeSeekBar;
    TextView range;
    CheckBox cbBaby, cbYoung, cbAdult, cbOld, cbCrippled, cbNeutered, cbSick;
    Button btnSubmit, btnBreed;
    List<String> selectedBreedGroup = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preference);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        //Declaration
        drawerLayout = findViewById(R.id.drawerLayout_user_preference);
        Spinner spinnerType = findViewById(R.id.spinnerType);
        rangeSeekBar = findViewById(R.id.rangeSeekBar);
        range = findViewById(R.id.rangeText);
        cbBaby = findViewById(R.id.cbBaby);
        cbYoung = findViewById(R.id.cbYoung);
        cbAdult = findViewById(R.id.cbAdult);
        cbOld = findViewById(R.id.ctOld);
        cbCrippled = findViewById(R.id.cbCrippled);
        cbNeutered = findViewById(R.id.cbNeutered);
        cbSick = findViewById(R.id.cbSick);
        btnSubmit = findViewById(R.id.btnSubmitPreference);
        btnBreed = findViewById(R.id.btnBreed);

        //Setting Spinner Adapter
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.animal_type,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(adapter);

        //Spinner on selected
        final String[] selectedType = {""};
        spinnerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedType[0] = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        //Seekbar Range of search
        rangeSeekBar.setProgress(10);
        rangeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                range.setText(progress + " km");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        btnBreed.setOnClickListener(v -> {
            if(selectedType[0].isEmpty()){
                Toast.makeText(this, "Please Select Animal Type", Toast.LENGTH_SHORT).show();
            }else{
                showBreedDialog(selectedType[0]);
            }
        });

        btnSubmit.setOnClickListener(v -> {
            List<String> ageGroup = new ArrayList<>();
            if(cbBaby.isChecked()) ageGroup.add("Baby");
            if(cbYoung.isChecked()) ageGroup.add("Young");
            if(cbAdult.isChecked()) ageGroup.add("Adult");
            if(cbOld.isChecked()) ageGroup.add("Old");

            boolean crippled = cbCrippled.isChecked();
            boolean neutered = cbNeutered.isChecked();
            boolean sick = cbSick.isChecked();

            int range = rangeSeekBar.getProgress();
            String type = selectedType[0];

            UserPreference preference = new UserPreference(type, selectedBreedGroup, ageGroup, crippled, neutered, sick, range);

            if(user != null){
                String uid = user.getUid();
                DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("users").child(uid).child("Preference");

                myRef.setValue(preference).addOnSuccessListener(unused -> {
                    Toast.makeText(UserPreferenceActivity.this, "Preference Saved", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(UserPreferenceActivity.this, MainActivity.class));
                    finish();
                }).addOnFailureListener(e -> {
                    Toast.makeText(UserPreferenceActivity.this, "Failed to save Preference", Toast.LENGTH_SHORT).show();
                });
            }else{
                Toast.makeText(UserPreferenceActivity.this, "User not logged in", Toast.LENGTH_SHORT).show();
            }
        });

        //Set so that user won't exist app when press back button
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users");
            @Override
            public void handleOnBackPressed() {
                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.child("Preference").exists()){
                            if(doubleBackToExitPressedOnce){
                                ActivityStateHelper.clearLastActivity(UserPreferenceActivity.this);
                                FirebaseAuth.getInstance().signOut();

                                Intent intent = new Intent(UserPreferenceActivity.this, LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            } else {
                                doubleBackToExitPressedOnce = true;
                                Toast.makeText(UserPreferenceActivity.this, "Press BACK again to exit", Toast.LENGTH_SHORT).show();

                                //Reset doubleBackToExitPressedOnce after 2 seconds
                                new android.os.Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
                            }
                        }else {
                            Intent intent = new Intent(UserPreferenceActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(UserPreferenceActivity.this, "Couldn't find Preference", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void showBreedDialog(String type) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_breed_selection, null);
        builder.setView(dialogView);

        TextView title = dialogView.findViewById(R.id.dialogTitle);
        title.setText("Select Breed (" + type + ")");

        LinearLayout layoutContainer = dialogView.findViewById(R.id.layoutBreedContainer);

        List<String> breedOption = new ArrayList<>(getBreedForType(type));
        Collections.sort(breedOption);

        Map<Character, List<String>> grouped = new TreeMap<>();
        for (String breed : breedOption) {
            char firstChar = Character.toUpperCase(breed.charAt(0));
            if (!grouped.containsKey(firstChar)) {
                grouped.put(firstChar, new ArrayList<>());
            }
            grouped.get(firstChar).add(breed);
        }

        List<Chip> allChips = new ArrayList<>();

        for (Map.Entry<Character, List<String>> entry : grouped.entrySet()) {
            TextView sectionTitle = new TextView(this);
            sectionTitle.setText(String.valueOf(entry.getKey()));
            sectionTitle.setTextSize(16);
            sectionTitle.setTypeface(null, Typeface.BOLD);
            sectionTitle.setPadding(0, 16, 0, 8);
            layoutContainer.addView(sectionTitle);

            ChipGroup chipGroup = new ChipGroup(this);
            chipGroup.setChipSpacing(8);
            chipGroup.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            chipGroup.setSingleSelection(false);

            for (String breed : entry.getValue()) {
                Chip chip = new Chip(this, null, com.google.android.material.R.style.Widget_MaterialComponents_Chip_Choice);
                chip.setText(breed);
                chip.setCheckable(true);
                chip.setChipBackgroundColorResource(R.color.white);
                chip.setTextColor(ContextCompat.getColor(this, R.color.black));
                chip.setChipStrokeColorResource(R.color.black);
                chip.setChipStrokeWidth(1f);
                chip.setChipCornerRadius(16f);
                chip.setFocusable(true);
                chip.setClickable(true);

                chip.setChecked(selectedBreedGroup.contains(breed));
                chipGroup.addView(chip);
                allChips.add(chip);
            }

            layoutContainer.addView(chipGroup);
        }

        builder.setPositiveButton("Submit", (dialog, which) -> {
            selectedBreedGroup.clear();
            for (Chip chip : allChips) {
                if (chip.isChecked()) {
                    selectedBreedGroup.add(chip.getText().toString());
                    Log.d("SelectedBreed", chip.getText().toString());
                }
            }
            Log.d("Selected Breed", "Selected: " + selectedBreedGroup.size() + " breeds");
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }



    private List<String> getBreedForType(String type) {
        if(type.equalsIgnoreCase("Dog")){
            return Arrays.asList("Thai Ridgeback", "Thai Bangkaew", "Persian", "Scottish Fold",
                    "Labrador Retriever", "White Siamese", "Chihuahua", "Pitbull", "Terrier", "Siamese",
                    "Pug", "Golden Retriever", "German Shepherd", "Poodle", "Beagle", "Shih Tzu", "Pomeranian",
                    "Siberian Husky", "French Bulldog", "Bulldog", "Dachshund", "Border Collie", "Rottweiler",
                    "Doberman Pinscher", "Yorkshire Terrier", "Maltese", "Cavalier King Charles Spaniel", "Samoyed",
                    "Akita", "Shiba Inu", "Great Dane", "Boxer", "Australian Shepherd", "Bernese Mountain Dog",
                    "Bichon Frise", "Boston Terrier", "Bull Terrier", "Chow Chow", "Cocker Spaniel", "Collie", "Corgi",
                    "Dalmatian", "English Bulldog", "Greyhound", "Havanese", "Jack Russell Terrier", "Lhasa Apso",
                    "Miniature Schnauzer", "Newfoundland", "Old English Sheepdog", "Papillon", "Pekingese", "Pointer",
                    "Saint Bernard", "Shar Pei", "Shetland Sheepdog", "Springer Spaniel", "Weimaraner",
                    "West Highland White Terrier", "Whippet");
        }else if (type.equalsIgnoreCase("Cat")){
            return Arrays.asList("Siamese", "Persian", "Scottish Fold", "Maine Coon", "Bengal", "British Shorthair",
                    "Ragdoll", "Sphynx", "Abyssinian", "Burmese", "American Shorthair", "Devon Rex", "Cornish Rex",
                    "Russian Blue", "Norwegian Forest Cat", "Oriental Shorthair", "Birman", "Exotic Shorthair",
                    "Himalayan", "Manx", "Turkish Angora", "Siberian", "Bombay", "Tonkinese", "Chartreux", "Ocicat",
                    "Savannah", "Toyger", "Pixie-bob", "Cymric", "Japanese Bobtail", "American Bobtail", "American Curl",
                    "Balinese", "Colorpoint Shorthair", "Egyptian Mau", "European Burmese", "Havana Brown", "Korat", "LaPerm",
                    "Lykoi", "Munchkin", "Nebelung", "Peterbald", "Ragamuffin", "Selkirk Rex", "Snowshoe", "Sokoke", "Somali",
                    "Thai", "Turkish Van", "Chausie", "Burmilla", "California Spangled", "Javanese", "Khao Manee", "Kinkalow",
                    "Napoleon (Minuet)", "Serengeti", "Singapura", "Tiffanie", "Ukrainian Levkoy");
        }else{
            return new ArrayList<>();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        ActivityStateHelper.saveLastActivity(this, this.getClass().getName());
    }
}
