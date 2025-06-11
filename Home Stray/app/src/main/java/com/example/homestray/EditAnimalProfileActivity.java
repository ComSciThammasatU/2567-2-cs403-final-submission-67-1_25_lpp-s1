package com.example.homestray;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class EditAnimalProfileActivity extends AppCompatActivity {
    private EditText editTextName, editTextAge, editTextHabits, editTextBreed, editTextColor,
            editTextMedical, editTextSickness, editTextFoundation, editTextAddress,
            editTextContact, editTextHistory;
    private Spinner spinnerType;
    private ImageView imageView;
    private Button saveBtn;
    private Uri selectedImageUri;
    private Bitmap selectedBitmap;
    private boolean isImageChanged = false;

    private String animalId;
    private Animal currentAnimal;
    private String currentImageUrl;

    private FirebaseDatabase database;
    private DatabaseReference animalRef;
    private TextView insertProfile;
    private ArrayAdapter<CharSequence> adapter;
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_animal_profile);

        animalId = getIntent().getStringExtra("animal_id");
        if (animalId == null) {
            finish();
            return;
        }
        Log.d("DEBUG", "Received animal_id: " + animalId);


        // Init views
        editTextName = findViewById(R.id.editTextName);
        editTextAge = findViewById(R.id.editTextAge);
        editTextHabits = findViewById(R.id.editTextHabits);
        editTextBreed = findViewById(R.id.editTextBreed);
        editTextColor = findViewById(R.id.editTextColor);
        editTextMedical = findViewById(R.id.editTextMedical);
        editTextSickness = findViewById(R.id.editTextSickness);
        editTextFoundation = findViewById(R.id.editTextFoundation);
        editTextAddress = findViewById(R.id.editTextAddress);
        editTextContact = findViewById(R.id.editTextContact);
        editTextHistory = findViewById(R.id.editTextHistory);
        spinnerType = findViewById(R.id.spinnerType);
        imageView = findViewById(R.id.showChoosePhoto);
        saveBtn = findViewById(R.id.btnSubmit);
        insertProfile = findViewById(R.id.insertProfile);
        // Firebase
        database = FirebaseDatabase.getInstance();
        animalRef = database.getReference("animals").child(animalId);

        // Spinner Setup
        adapter = ArrayAdapter.createFromResource(this, R.array.animal_type, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(adapter);

        // Image picker
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null && result.getData().getData() != null) {
                        selectedImageUri = result.getData().getData();
                        try {
                            InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
                            selectedBitmap = BitmapFactory.decodeStream(inputStream);
                            imageView.setImageBitmap(selectedBitmap);
                            isImageChanged = true;
                            insertProfile.setVisibility(View.GONE);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

        imageView.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            imagePickerLauncher.launch(intent);
        });

        loadAnimalData();

        saveBtn.setOnClickListener(v -> {
            if (currentAnimal != null) {
                saveAnimalData();
                Intent intent = new Intent(EditAnimalProfileActivity.this, OpenAnimalProfileAdminActivity.class);
                intent.putExtra("animal_id", animalId);
                startActivity(intent);
            }
        });
    }

    private void loadAnimalData() {
        animalRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                currentAnimal = snapshot.getValue(Animal.class);
                if (currentAnimal == null) return;

                // Fill views
                editTextName.setText(currentAnimal.getName());
                editTextAge.setText(String.valueOf(currentAnimal.getAge()));
                editTextHabits.setText(currentAnimal.getHabits());
                editTextBreed.setText(currentAnimal.getBreed());
                editTextColor.setText(currentAnimal.getColor());
                editTextMedical.setText(currentAnimal.getMedical());
                editTextSickness.setText(currentAnimal.getSickness());
                editTextFoundation.setText(currentAnimal.getFoundation());
                editTextAddress.setText(currentAnimal.getAddress());
                editTextContact.setText(currentAnimal.getContact());
                editTextHistory.setText(currentAnimal.getHistory());

                currentImageUrl = currentAnimal.getImgURL();
                Glide.with(EditAnimalProfileActivity.this)
                        .load(currentImageUrl)
                        .into(imageView);

                // Set spinner value
                int index = adapter.getPosition(currentAnimal.getType());
                if (index >= 0) spinnerType.setSelection(index);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(EditAnimalProfileActivity.this, "Failed to load animal data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveAnimalData() {
        String name = editTextName.getText().toString().trim();
        String ageStr = editTextAge.getText().toString().trim();
        String type = spinnerType.getSelectedItem().toString();
        String habits = editTextHabits.getText().toString().trim();
        String breed = editTextBreed.getText().toString().trim();
        String color = editTextColor.getText().toString().trim();
        String medical = editTextMedical.getText().toString().trim();
        String sickness = editTextSickness.getText().toString().trim();
        String foundation = editTextFoundation.getText().toString().trim();
        String address = editTextAddress.getText().toString().trim();
        String contact = editTextContact.getText().toString().trim();
        String history = editTextHistory.getText().toString().trim();

        if (name.isEmpty() || ageStr.isEmpty() || habits.isEmpty() || breed.isEmpty() || color.isEmpty() ||
                medical.isEmpty() || sickness.isEmpty() || foundation.isEmpty() || type.isEmpty() ||
                address.isEmpty() || contact.isEmpty() || history.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int age = Integer.parseInt(ageStr);

        if (isImageChanged && selectedBitmap != null) {
            uploadImageToImgbb(selectedBitmap, newUrl -> {
                handleLatLngIfAddressChanged(name, age, type, breed, color, habits, medical, sickness, foundation, address, contact, history, newUrl);
            });
        } else {
            handleLatLngIfAddressChanged(name, age, type, breed, color, habits, medical, sickness, foundation, address, contact, history, currentImageUrl);
        }

    }

    private void handleLatLngIfAddressChanged(String name, int age, String type, String breed, String color,
                                              String habits, String medical, String sickness, String foundation,
                                              String address, String contact, String history, String imageUrl) {

        boolean isAddressChanged = !address.equals(currentAnimal.getAddress());

        if (isAddressChanged) {
            getLatLngFromAddress(address, (lat, lng) -> {
                if (lat != null && lng != null) {
                    currentAnimal.setLat(lat);
                    currentAnimal.setLng(lng);
                }
                updateAnimalInFirebase(name, age, type, breed, color, habits, medical, sickness, foundation, address, contact, history, imageUrl);
            });
        } else {
            updateAnimalInFirebase(name, age, type, breed, color, habits, medical, sickness, foundation, address, contact, history, imageUrl);
        }
    }


    private void updateAnimalInFirebase(String name, int age, String type, String breed, String color,
                                        String habits, String medical, String sickness, String foundation,
                                        String address, String contact, String history, String imageUrl) {
        currentAnimal.setName(name);
        currentAnimal.setAge(age);
        currentAnimal.setType(type);
        currentAnimal.setBreed(breed);
        currentAnimal.setColor(color);
        currentAnimal.setHabits(habits);
        currentAnimal.setMedical(medical);
        currentAnimal.setSickness(sickness);
        currentAnimal.setFoundation(foundation);
        currentAnimal.setAddress(address);
        currentAnimal.setContact(contact);
        currentAnimal.setHistory(history);
        currentAnimal.setImgURL(imageUrl);

        animalRef.setValue(currentAnimal).addOnSuccessListener(aVoid ->
                Toast.makeText(this, "Animal profile updated", Toast.LENGTH_SHORT).show()
        ).addOnFailureListener(e ->
                Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show()
        );
    }

    private void uploadImageToImgbb(Bitmap bitmap, OnImageUploaded listener) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        String encodedImage = Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);
        String url = "https://api.imgbb.com/1/upload?key=" + BuildConfig.IMAGEBB_API_KEY;

        RequestBody body = new FormBody.Builder().add("image", encodedImage).build();
        Request request = new Request.Builder().url(url).post(body).build();
        OkHttpClient client = new OkHttpClient();

        client.newCall(request).enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(EditAnimalProfileActivity.this, "Upload failed", Toast.LENGTH_SHORT).show());
            }

            @Override public void onResponse(Call call, Response response) throws IOException {
                try {
                    JSONObject json = new JSONObject(response.body().string());
                    String url = json.getJSONObject("data").getString("url");
                    runOnUiThread(() -> listener.onUploaded(url));
                } catch (JSONException e) {
                    runOnUiThread(() -> Toast.makeText(EditAnimalProfileActivity.this, "Error parsing image URL", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    private void getLatLngFromAddress(String address, OnLatLngResultListener listener) {
        String apiKey = BuildConfig.GOOGLE_CLOUD_API;
        String url = "https://maps.googleapis.com/maps/api/geocode/json?address=" +
                Uri.encode(address) + "&key=" + apiKey;

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, IOException e) {
                e.printStackTrace();
                Log.e("GeoAPI", "Request failed: " + e.getMessage());
                runOnUiThread(() -> Toast.makeText(EditAnimalProfileActivity.this, "Failed to get location", Toast.LENGTH_SHORT).show());
                listener.onResult(null, null);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    String responseBody = response.body().string();
                    try {
                        JSONObject jsonObject = new JSONObject(responseBody);
                        JSONArray results = jsonObject.getJSONArray("results");
                        if (results.length() > 0) {
                            JSONObject location = results.getJSONObject(0)
                                    .getJSONObject("geometry")
                                    .getJSONObject("location");
                            double lat = location.getDouble("lat");
                            double lng = location.getDouble("lng");
                            runOnUiThread(() -> listener.onResult(lat, lng));
                        } else {
                            Toast.makeText(EditAnimalProfileActivity.this, "Could not find location from address", Toast.LENGTH_SHORT).show();
                            editTextAddress.setError("Please enter a valid address");
                            listener.onResult(null, null);                         }
                    } catch (JSONException e) {
                        runOnUiThread(() -> listener.onResult(null, null));
                    }
                }
            }
        });
    }

    interface OnLatLngResultListener {
        void onResult(Double lat, Double lng);
    }

    interface OnImageUploaded {
        void onUploaded(String newImageUrl);
    }
}

