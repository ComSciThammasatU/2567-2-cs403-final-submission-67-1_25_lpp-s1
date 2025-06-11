package com.example.homestray;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.MenuItem;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

public class UserHistoryFormActivity extends MainActivity {
    EditText editTextFirstName, editTextLastName, editTextPhone, editTextLine,
            editTextSalary, editTextFree, editTextConsent, editTextAddress;
    Button submitBtn;
    CardView homePicture01, homePicture02, homePicture03, profilePicture;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private int selectedImageSlot = -1;
    Uri[] imageUris = new Uri[4];
    Bitmap[] imageBitmaps = new Bitmap[4];
    ImageView[] imageViews = new ImageView[4];
    TextView textProfilePicture, textHomePicture01, textHomePicture02, textHomePicture03;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_history_form);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        //Declaration
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

        textProfilePicture = findViewById(R.id.textProfilePicture);
        textHomePicture01 = findViewById(R.id.textHomePicture01);
        textHomePicture02 = findViewById(R.id.textHomePicture02);
        textHomePicture03 = findViewById(R.id.textHomePicture03);

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

                                switch (selectedImageSlot) {
                                    case 0:
                                        textProfilePicture.setVisibility(View.GONE);
                                        break;
                                    case 1:
                                        textHomePicture01.setVisibility(View.GONE);
                                        break;
                                    case 2:
                                        textHomePicture02.setVisibility(View.GONE);
                                        break;
                                    case 3:
                                        textHomePicture03.setVisibility(View.GONE);
                                        break;
                                }
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
                //This is for the first time user entering the app and have to fill information
                if(doubleBackToExitPressedOnce){
                    ActivityStateHelper.clearLastActivity(UserHistoryFormActivity.this);
                    FirebaseAuth.getInstance().signOut();

                    Intent intent = new Intent(UserHistoryFormActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    doubleBackToExitPressedOnce = true;
                    Toast.makeText(UserHistoryFormActivity.this, "Press BACK again to exit", Toast.LENGTH_SHORT).show();

                    //Reset doubleBackToExitPressedOnce after 2 seconds
                    new android.os.Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
                }
            }
        });

        editTextFirstName.setFilters(new InputFilter[]{noDigitsFilter});
        editTextLastName.setFilters(new InputFilter[]{noDigitsFilter});
        editTextPhone.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
        editTextFree.setFilters(new InputFilter[]{new InputFilter.LengthFilter(2)});

        if(user != null){
            submitBtn.setOnClickListener(v -> {
                boolean valid = true;

                String fName = editTextFirstName.getText().toString();
                String lName = editTextLastName.getText().toString();
                String phone = editTextPhone.getText().toString();
                String line = editTextLine.getText().toString();
                String salary = editTextSalary.getText().toString();
                String free = editTextFree.getText().toString();
                String consent = editTextConsent.getText().toString();
                String address = editTextAddress.getText().toString();

                editTextFirstName.setError(null);
                editTextLastName.setError(null);
                editTextPhone.setError(null);
                editTextLine.setError(null);
                editTextSalary.setError(null);
                editTextFree.setError(null);
                editTextConsent.setError(null);
                editTextAddress.setError(null);

                // Validate input fields
                if (fName.isEmpty()) {
                    editTextFirstName.setError("First name is required");
                    valid = false;
                }
                if (lName.isEmpty()) {
                    editTextLastName.setError("Last name is required");
                    valid = false;
                }
                if (phone.length() != 10) {
                    editTextPhone.setError("Enter a valid 10-digit phone number");
                    valid = false;
                }
                if (line.isEmpty()) {
                    editTextLine.setError("LINE ID is required");
                    valid = false;
                }
                if (salary.isEmpty()) {
                    editTextSalary.setError("Salary is required");
                    valid = false;
                }
                if (free.isEmpty()) {
                    editTextFree.setError("Enter valid free time (e.g. 2)");
                    valid = false;
                }
                if(!free.isEmpty()){
                    int freeInt = Integer.parseInt(free);
                    if (freeInt < 0 || freeInt > 24) {
                        editTextFree.setError("Enter valid free time (0–24)");
                        valid = false;
                    }
                }
                if (consent.isEmpty()) {
                    editTextConsent.setError("Consent field is required");
                    valid = false;
                }
                if (address.isEmpty()) {
                    editTextAddress.setError("Address is required");
                    valid = false;
                }
                if(imageUris[0] == null || imageUris[1] == null || imageUris[2] == null || imageUris[3] == null){
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Picture Are Not Selected");
                    builder.setMessage("Please Upload All Photos Before Continue");

                    builder.setPositiveButton("Close", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();
                    valid = false;
                }
                if (!valid) {
                    Toast.makeText(this, "Please fix the errors above", Toast.LENGTH_SHORT).show();
                    return;
                }
                getLatLngFromAddress(address, ((lat, lng) -> {
                    if(lat == null || lng == null){
                        Toast.makeText(UserHistoryFormActivity.this, "Failed to get location", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    UsersHistory usersHistory = new UsersHistory(fName, lName, phone, line, salary, free, consent, address);
                    usersHistory.setLat(lat);
                    usersHistory.setLng(lng);

                    FirebaseDatabase.getInstance().getReference("users").child(user.getUid())
                            .child("History").setValue(usersHistory)
                            .addOnSuccessListener(unused -> {
                                Toast.makeText(this, "Saved form data", Toast.LENGTH_SHORT).show();
                                uploadAllImages(user.getUid());
                                goNextPage(user.getUid());
                            }).addOnFailureListener(e -> Toast.makeText(this, "Failed to save form data", Toast.LENGTH_SHORT).show());
                }));
        });
        }
    }

    private void uploadAllImages(String uid) {
        DatabaseReference imageRef = FirebaseDatabase.getInstance().getReference("users").child(uid).child("Images");
        int totalImages = 0;
        for (Uri uri : imageUris) {
            if (uri != null) totalImages++;
        }

        if (totalImages == 0) {
            goNextPage(uid);
            return;
        }

        int[] uploadedCount = {0};
        for (int i = 0; i < imageUris.length; i++) {
            final int index = i;
            if (imageUris[index] != null) {
                int finalTotalImages = totalImages;
                uploadImageToImgBB(imageUris[index], index, new UploadCallback() {
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
                        imageRef.child(key).setValue(imageUrl);
                        uploadedCount[0]++;
                        if (uploadedCount[0] == finalTotalImages) {
                            Toast.makeText(UserHistoryFormActivity.this, "All images uploaded", Toast.LENGTH_SHORT).show();
                            goNextPage(uid);
                        }
                    }

                    @Override
                    public void onUploadFailure(Exception e) {
                        Toast.makeText(UserHistoryFormActivity.this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    private void goNextPage(String uid) {
        DatabaseReference checkPreference = FirebaseDatabase.getInstance().getReference("users")
                .child(uid).child("Preference");
        checkPreference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Toast.makeText(UserHistoryFormActivity.this, "Sent to Home Page", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(UserHistoryFormActivity.this, MainActivity.class));
                } else {
                    Toast.makeText(UserHistoryFormActivity.this, "Sent to Preference Page", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(UserHistoryFormActivity.this, UserPreferenceActivity.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void uploadImageToImgBB(Uri imageUri, int slot, UploadCallback callback) {
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

    private void openImagePicker(int slot){
        selectedImageSlot = slot;
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        imagePickerLauncher.launch(Intent.createChooser(intent, "Select Picture"));
    }

    public interface UploadCallback {
        void onUploadSuccess(String imageUrl, int slot);
        void onUploadFailure(Exception e);
    }

    public InputFilter noDigitsFilter = (source, start, end, dest, dStart, dEnd) -> {
        for (int i = start; i < end; i++) {
            if (Character.isDigit(source.charAt(i))) {
                return "";
            }
        }
        return null;
    };

    private void getLatLngFromAddress(String address, CreateAnimalProfile.OnLatLngResultListener listener) {
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
                runOnUiThread(() -> Toast.makeText(UserHistoryFormActivity.this, "Failed to get location", Toast.LENGTH_SHORT).show());
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
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(UserHistoryFormActivity.this, "Could not find location from address", Toast.LENGTH_SHORT).show();
                                    editTextAddress.setError("Please enter a valid address");
                                    listener.onResult(null, null);
                                }
                            });
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
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return super.onNavigationItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        ActivityStateHelper.saveLastActivity(this, this.getClass().getName());
    }
}
