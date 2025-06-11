package com.example.homestray;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class EditUserHistoryActivity extends AppCompatActivity {
    EditText editTextFirstName, editTextLastName, editTextPhone, editTextLine,
            editTextSalary, editTextFree, editTextConsent, editTextAddress;

    Button submitBtn;
    CardView homePicture01, homePicture02, homePicture03, profilePicture;
    ActivityResultLauncher<Intent> imagePickerLauncher;
    String userId;
    FirebaseDatabase database;
    DatabaseReference userRef, imgRef;
    UsersHistory currentUser;
    boolean isImageChanged = false;
    int selectedImageSlot = -1;
    Uri[] imageUris = new Uri[4];
    Bitmap[] imageBitmaps = new Bitmap[4];
    ImageView[] imageViews = new ImageView[4];
    String[] originalImageUrls = new String[4];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_history);

        // Firebase
        userId = getIntent().getStringExtra("user_id");
        database = FirebaseDatabase.getInstance();
        userRef = database.getReference("users").child(userId).child("History");
        imgRef = database.getReference("users").child(userId).child("Images");


        editTextFirstName = findViewById(R.id.editTextFirstName);
        editTextLastName = findViewById(R.id.editTextLastName);
        editTextPhone = findViewById(R.id.editTextPhone);
        editTextLine = findViewById(R.id.editTextLine);
        editTextSalary = findViewById(R.id.editTextSalary);
        editTextFree = findViewById(R.id.editTextFree);
        editTextConsent = findViewById(R.id.editTextConsent);
        editTextAddress = findViewById(R.id.editTextAddress);

        profilePicture = findViewById(R.id.profilePicture);
        homePicture01 = findViewById(R.id.homePicture01);
        homePicture02 = findViewById(R.id.homePicture02);
        homePicture03 = findViewById(R.id.homePicture03);

        imageViews[0] = findViewById(R.id.showChoosePhoto);
        imageViews[1] = findViewById(R.id.showChoosePhoto01);
        imageViews[2] = findViewById(R.id.showChoosePhoto02);
        imageViews[3] = findViewById(R.id.showChoosePhoto03);

        submitBtn = findViewById(R.id.btnSubmit);

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if(result.getResultCode() == RESULT_OK){
                        Intent data = result.getData();
                        if (data != null && data.getData() != null){
                            Uri uri = data.getData();
                            try {
                                InputStream inputStream = getContentResolver().openInputStream(uri);
                                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                                imageUris[selectedImageSlot] = uri;
                                imageBitmaps[selectedImageSlot] = bitmap;
                                imageViews[selectedImageSlot].setImageBitmap(bitmap);
                                isImageChanged = true;
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    }
                }
        );

        profilePicture.setOnClickListener(v -> openImagePicker(0));
        homePicture01.setOnClickListener(v -> openImagePicker(1));
        homePicture02.setOnClickListener(v -> openImagePicker(2));
        homePicture03.setOnClickListener(v -> openImagePicker(3));

        //Set so that user won't exist app when press back button
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent(EditUserHistoryActivity.this, UserHistoryShow.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        editTextFirstName.setFilters(new InputFilter[]{noDigitsFilter});
        editTextLastName.setFilters(new InputFilter[]{noDigitsFilter});
        editTextPhone.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
        editTextFree.setFilters(new InputFilter[]{new InputFilter.LengthFilter(2)});

        loadUserData();

        submitBtn.setOnClickListener(v -> {
            if (currentUser != null) {
                saveUserData();
                Intent intent = new Intent(EditUserHistoryActivity.this, UserHistoryShow.class);
                startActivity(intent);
            }
        });

    }

    private void saveUserData() {
        String firstName = editTextFirstName.getText().toString().trim();
        String lastName = editTextLastName.getText().toString().trim();
        String phone = editTextPhone.getText().toString().trim();
        String line = editTextLine.getText().toString().trim();
        String salary = editTextSalary.getText().toString().trim();
        String free = editTextFree.getText().toString().trim();
        String consent = editTextConsent.getText().toString().trim();
        String address = editTextAddress.getText().toString().trim();

        Log.d("DEBUG Save User Data", "Free time : " + free);


        if(firstName.isEmpty() || lastName.isEmpty() || phone.isEmpty() || line.isEmpty() || salary.isEmpty() ||
                free.isEmpty() || consent.isEmpty() || address.isEmpty()){
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (isImageChanged) {
            uploadAllImages(userId);
            handleLatLngIfAddressChanged(firstName, lastName, phone, line, salary, free, consent, address);
        } else {
            handleLatLngIfAddressChanged(firstName, lastName, phone, line, salary, free, consent, address);
        }
    }

    private void loadUserData() {
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                currentUser = snapshot.getValue(UsersHistory.class);
                if(currentUser == null) return;

                // Fill views
                editTextFirstName.setText(currentUser.getFirstName());
                editTextLastName.setText(currentUser.getLastName());
                editTextPhone.setText(currentUser.getPhone());
                editTextLine.setText(currentUser.getLine());
                editTextSalary.setText(currentUser.getSalary());
                editTextFree.setText(currentUser.getFreeTime());
                editTextConsent.setText(currentUser.getConsent());
                editTextAddress.setText(currentUser.getAddress());

                Log.d("DEBUG Load User Data", "Free time : " + currentUser.getFreeTime());

                imgRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            originalImageUrls[0] = snapshot.child("profile").getValue(String.class);
                            originalImageUrls[1] = snapshot.child("home1").getValue(String.class);
                            originalImageUrls[2] = snapshot.child("home2").getValue(String.class);
                            originalImageUrls[3] = snapshot.child("home3").getValue(String.class);

                            for (int i = 0; i < 4; i++) {
                                Glide.with(EditUserHistoryActivity.this)
                                        .load(originalImageUrls[i])
                                        .into(imageViews[i]);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(EditUserHistoryActivity.this, "Failed to load image urls", Toast.LENGTH_SHORT).show();
                    }
                });


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(EditUserHistoryActivity.this, "Failed to load user data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public InputFilter noDigitsFilter = (source, start, end, dest, dstart, dend) -> {
        for (int i = start; i < end; i++) {
            if (Character.isDigit(source.charAt(i))) {
                return "";
            }
        }
        return null;
    };

    private void uploadAllImages(String uid) {
        DatabaseReference imageRef = FirebaseDatabase.getInstance().getReference("users").child(uid).child("Images");
        int totalImages = 0;
        for (Uri uri : imageUris) {
            if (uri != null) totalImages++;
        }

        if (totalImages == 0) {
            return;
        }

        int[] uploadedCount = {0};
        for (int i = 0; i < imageUris.length; i++) {
            final int index = i;
            if (imageUris[index] != null) {
                int finalTotalImages = totalImages;
                uploadImageToImgBB(imageUris[index], index, new UserHistoryFormActivity.UploadCallback() {
                    @Override
                    public void onUploadSuccess(String imageUrl, int slot) {
                        String key;
                        switch (slot) {
                            case 0:
                                key = "profile";
                                break;
                            case 1:
                                key = "home1";
                                break;
                            case 2:
                                key = "home2";
                                break;
                            case 3:
                                key = "home3";
                                break;
                            default:
                                key = "unknown";
                                break;
                        }
                        imageRef.child(key).setValue(imageUrl).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                setResult(RESULT_OK);
                                finish();
                            } else {
                                Toast.makeText(EditUserHistoryActivity.this, "Failed to update profile image", Toast.LENGTH_SHORT).show();
                            }
                        });
                        uploadedCount[0]++;
                        if (uploadedCount[0] == finalTotalImages) {
                            Toast.makeText(EditUserHistoryActivity.this, "All images uploaded", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onUploadFailure(Exception e) {
                        Toast.makeText(EditUserHistoryActivity.this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    private void uploadImageToImgBB(Uri imageUri, int slot, UserHistoryFormActivity.UploadCallback callback) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            byte[] imageBytes = new byte[inputStream.available()];
            inputStream.read(imageBytes);
            String base64Image = android.util.Base64.encodeToString(imageBytes, android.util.Base64.DEFAULT);

            String API_KEY = BuildConfig.IMAGEBB_API_KEY;
            String url = "https://api.imgbb.com/1/upload?key=" + API_KEY;

            okhttp3.RequestBody requestBody = new okhttp3.FormBody.Builder()
                    .add("image", base64Image)
                    .build();

            okhttp3.Request request = new okhttp3.Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build();

            new okhttp3.OkHttpClient().newCall(request).enqueue(new okhttp3.Callback() {
                @Override
                public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {
                    runOnUiThread(() -> callback.onUploadFailure(e));
                }

                @Override
                public void onResponse(@NonNull okhttp3.Call call, @NonNull okhttp3.Response response) throws IOException {
                    if (response.isSuccessful()) {
                        try {
                            String responseBody = response.body().string();
                            JSONObject json = new JSONObject(responseBody);
                            String imageUrl = json.getJSONObject("data").getString("url");
                            runOnUiThread(() -> callback.onUploadSuccess(imageUrl, slot));
                        } catch (Exception e) {
                            runOnUiThread(() -> callback.onUploadFailure(e));
                        }
                    } else {
                        runOnUiThread(() -> callback.onUploadFailure(new Exception("Response not successful")));
                    }
                }
            });
            inputStream.close();
        } catch (Exception e) {
            callback.onUploadFailure(e);
        }
    }


    private void openImagePicker(int slot) {
        selectedImageSlot = slot;
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        imagePickerLauncher.launch(Intent.createChooser(intent, "Select Picture"));
    }

    private void handleLatLngIfAddressChanged(String firstName, String lastName, String phone, String line,
                                              String salary, String free, String consent, String address) {

        boolean isAddressChanged = !address.equals(currentUser.getAddress());

        if (isAddressChanged) {
            getLatLngFromAddress(address, (lat, lng) -> {
                if (lat != null && lng != null) {
                    currentUser.setLat(lat);
                    currentUser.setLng(lng);
                }
                updateUserInFirebase(firstName, lastName, phone, line, salary, free, consent, address);
            });
        }else {
            updateUserInFirebase(firstName, lastName, phone, line, salary, free, consent, address);
        }
    }


    private void updateUserInFirebase(String firstName, String lastName, String phone, String line,
                                      String salary, String free, String consent, String address) {
        currentUser.setFirstName(firstName);
        currentUser.setLastName(lastName);
        currentUser.setPhone(phone);
        currentUser.setLine(line);
        currentUser.setSalary(salary);
        currentUser.setFreeTime(free);
        currentUser.setConsent(consent);
        currentUser.setAddress(address);

        userRef.setValue(currentUser).addOnSuccessListener(aVoid -> {
                Toast.makeText(this, "User profile updated", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
        }).addOnFailureListener(e ->
                Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show()
        );

    }

    private void getLatLngFromAddress(String address, OnLatLngResultListener listener) {
        String apiKey = BuildConfig.GOOGLE_CLOUD_API;
        String url = "https://maps.googleapis.com/maps/api/geocode/json?address=" +
                Uri.encode(address) + "&key=" + apiKey;

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Log.e("GeoAPI", "Request failed: " + e.getMessage());
                runOnUiThread(() -> Toast.makeText(EditUserHistoryActivity.this, "Failed to get location", Toast.LENGTH_SHORT).show());
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
                            Toast.makeText(EditUserHistoryActivity.this, "Could not find location from address", Toast.LENGTH_SHORT).show();
                            editTextAddress.setError("Please enter a valid address");
                            listener.onResult(null, null);                        }
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
