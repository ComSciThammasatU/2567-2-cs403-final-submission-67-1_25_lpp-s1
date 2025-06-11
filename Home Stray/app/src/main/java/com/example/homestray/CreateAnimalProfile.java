package com.example.homestray;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

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


public class CreateAnimalProfile extends MainActivity {
    FusedLocationProviderClient fusedLocationClient;

    private

    EditText editTextName, editTextAge, editTextHabits, editTextBreed, editTextColor, editTextMedical,
            editTextSickness, editTextFoundation, editTextAddress, editTextContact, editTextHistory;
    Spinner spinnerType;
    CardView card;
    FirebaseAuth auth;
    FirebaseUser user;
    Button submitBtn;
    private String uploadedImageUrl = null;
    private Uri selectedImageUri = null;
    private Bitmap selectedBitmap = null;
    private boolean isImageSelected = false;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    TextView insertProfile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_animal_profile);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        //Declaration
        card = findViewById(R.id.insertPhoto);
        editTextName = findViewById(R.id.editTextName);
        editTextHabits = findViewById(R.id.editTextHabits);
        editTextBreed = findViewById(R.id.editTextBreed);
        editTextColor = findViewById(R.id.editTextColor);
        editTextMedical = findViewById(R.id.editTextMedical);
        editTextSickness = findViewById(R.id.editTextSickness);
        editTextFoundation = findViewById(R.id.editTextFoundation);
        editTextAddress = findViewById(R.id.editTextAddress);
        spinnerType = findViewById(R.id.spinnerType);
        editTextContact = findViewById(R.id.editTextContact);
        editTextHistory = findViewById(R.id.editTextHistory);
        editTextAge = findViewById(R.id.editTextAge);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        insertProfile = findViewById(R.id.insertProfile);

        submitBtn = findViewById(R.id.btnSubmit);

        //Set so that user won't exist app when press back button
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent(CreateAnimalProfile.this, AdminActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

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

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if(result.getResultCode() == RESULT_OK){
                        Intent data = result.getData();
                        if (data != null && data.getData() != null){
                            selectedImageUri = data.getData();
                            isImageSelected = true;
                            insertProfile.setVisibility(View.GONE);
                            try {
                                InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
                                selectedBitmap = BitmapFactory.decodeStream(inputStream);

                                //Show ImageView
                                ImageView selectImage = findViewById(R.id.showChoosePhoto);
                                selectImage.setImageBitmap(selectedBitmap);
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    }
                }
        );

        //Input Image
        card.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            imagePickerLauncher.launch(Intent.createChooser(intent, "Select Picture"));
        });

        // Block digits (0-9) for the fields below
        editTextName.setFilters(new InputFilter[]{noDigitsFilter});
        editTextHabits.setFilters(new InputFilter[]{noDigitsFilter});
        editTextBreed.setFilters(new InputFilter[]{noDigitsFilter});
        editTextColor.setFilters(new InputFilter[]{noDigitsFilter});
        editTextContact.setFilters(new InputFilter[]{
                new InputFilter.LengthFilter(10)
        });
        editTextAge.setFilters(new InputFilter[]{
                new InputFilter.LengthFilter(2)
        });

        //Input Fields to Firebase
        if(user != null){
            submitBtn.setOnClickListener(v -> {
                //Get Input
                String name = editTextName.getText().toString();
                String age = editTextAge.getText().toString();
                String habits = editTextHabits.getText().toString();
                String breed = editTextBreed.getText().toString();
                String color = editTextColor.getText().toString();
                String medical = editTextMedical.getText().toString();
                String sickness = editTextSickness.getText().toString();
                String foundation = editTextFoundation.getText().toString();
                String address = editTextAddress.getText().toString();
                String type = selectedType[0];
                String contact = editTextContact.getText().toString();
                String history = editTextHistory.getText().toString();

                if(selectedType[0].isEmpty()){
                    Toast.makeText(this, "Please Select Animal Type", Toast.LENGTH_SHORT).show();
                }
                if (!isImageSelected || selectedBitmap == null) {
                    Toast.makeText(CreateAnimalProfile.this, "Please select an image", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!contact.matches("^0\\d{9}$")) {
                    Toast.makeText(this, "Please enter a correct 10-digit phone number.\n", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (name.isEmpty() || history.isEmpty() || age.isEmpty() || habits.isEmpty() || breed.isEmpty() || color.isEmpty() ||
                        medical.isEmpty() || sickness.isEmpty() || foundation.isEmpty() ||
                        type.isEmpty() || contact.isEmpty() || address.isEmpty()) {
                    Toast.makeText(CreateAnimalProfile.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                uploadImageToImgbb(selectedBitmap, new OnImageUploadListener() {
                    @Override
                    public void onUploadSuccess(String imageUrl) {
                        uploadedImageUrl = imageUrl;
                        getLatLngFromAddress(address, (lat, lng) -> {
                            if(lat == null || lng == null){
                                Toast.makeText(CreateAnimalProfile.this, "Failed to get location", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("animals");
                            String animalId = myRef.push().getKey();
                            int ageInt = Integer.parseInt(age);
                            Animal animal = new Animal(animalId, name, ageInt, breed, color, habits, medical, sickness, foundation, type, contact, history, address);
                            animal.setImgURL(uploadedImageUrl);
                            animal.setLat(lat);
                            animal.setLng(lng);

                            if(animalId != null){
                                myRef.child(animalId).setValue(animal).addOnSuccessListener(unused ->
                                {
                                    Toast.makeText(CreateAnimalProfile.this, "Success Create Animal Profile", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(CreateAnimalProfile.this, AdminActivity.class);
                                    startActivity(intent);
                                    finish();
                                }).addOnFailureListener(e ->
                                        Toast.makeText(CreateAnimalProfile.this, "Failed To Create Animal Profile", Toast.LENGTH_SHORT).show()
                                );
                            }
                        });
                    }

                    @Override
                    public void onUploadFailed(String errorMessage) {
                        Toast.makeText(CreateAnimalProfile.this, "Image upload failed: " + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });

            });
        }
    }

    private void uploadImageToImgbb(Bitmap bitmap, OnImageUploadListener listener) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);

        String apiKey = BuildConfig.IMAGEBB_API_KEY;
        String url = "https://api.imgbb.com/1/upload?key=" + apiKey;

        RequestBody requestBody = new FormBody.Builder()
                .add("image", encodedImage)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> listener.onUploadFailed("Upload failed: " + e.getMessage()));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String responseBody = response.body().string();
                        JSONObject json = new JSONObject(responseBody);
                        String imageUrl = json.getJSONObject("data").getString("url");
                        runOnUiThread(() -> listener.onUploadSuccess(imageUrl));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        runOnUiThread(() -> listener.onUploadFailed("Failed to parse response"));
                    }
                } else {
                    runOnUiThread(() -> listener.onUploadFailed("Upload failed"));
                }
            }
        });
    }

    interface OnImageUploadListener {
        void onUploadSuccess(String imageUrl);
        void onUploadFailed(String errorMessage);
    }


    public InputFilter noDigitsFilter = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            for (int i = start; i < end; i++) {
                if (Character.isDigit(source.charAt(i))) {
                    return "";
                }
            }
            return null;
        }
    };

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
                runOnUiThread(() -> Toast.makeText(CreateAnimalProfile.this, "Failed to get location", Toast.LENGTH_SHORT).show());
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
                            runOnUiThread(() -> listener.onResult(null, null));
                        }
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


    @Override
    protected void onPause() {
        super.onPause();
        ActivityStateHelper.saveLastActivity(this, this.getClass().getName());
    }
}