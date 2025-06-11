package com.example.homestray;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardViewHolder> {
    private List<Animal> animalList;
    private Context context;
    private DatabaseReference historyRef, animalRef;

    public CardAdapter(List<Animal> animals, Context context) {
        this.animalList = animals;
        this.context = context;
    }

    public void setAnimalList(List<Animal> animalList) {
        this.animalList = animalList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.animal_profile_card, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        Animal animal = animalList.get(position);

        if (animalList == null || animalList.isEmpty()) {
            Log.e("CardAdapter", "animalList is empty or null. Skipping binding.");
            return;
        }

        if (animal == null || animal.getId() == null) {
            Log.e("CardAdapter", "Animal or animal ID is null at position: " + position);
            return;
        }

        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) {
            Log.e("CardAdapter", "User is not logged in. Skipping distance calculation.");
            holder.cardDistance.setText("Unknown distance");
            return;
        }

        historyRef = FirebaseDatabase.getInstance().getReference("users").child(uid).child("History");
        animalRef = FirebaseDatabase.getInstance().getReference("animals").child(animal.getId());

        historyRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                animalRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            Double userLat = userSnapshot.child("lat").getValue(Double.class);
                            Double userLng = userSnapshot.child("lng").getValue(Double.class);
                            Double animalLat = snapshot.child("lat").getValue(Double.class);
                            Double animalLng = snapshot.child("lng").getValue(Double.class);

                            if (userLat != null && userLng != null && animalLat != null && animalLng != null) {
                                double distance = Haversine.calculateDistance(userLat, userLng, animalLat, animalLng);
                                holder.cardDistance.setText(String.format("Distance : %.2f km", distance));
                            } else {
                                holder.cardDistance.setText("Unknown distance");
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(context, "Failed to get animal location", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "Failed to get user location", Toast.LENGTH_SHORT).show();
            }
        });

        holder.cardTitle.setText(animal.getName() + " , " + animal.getAge() + " Years Old");

        String description = "Breed : " + animal.getBreed() + "\n" +
                "Type : " + animal.getType() + "\n" +
                "Color : " + animal.getColor() + "\n" +
                "Habits : " + animal.getHabits() + "\n";

        holder.cardDes.setText(description);

        if (animal.getImgURL() != null && !animal.getImgURL().isEmpty()) {
            Glide.with(context)
                    .load(animal.getImgURL())
                    .placeholder(R.drawable.placeholder_image)
                    .into(holder.cardImage);
        } else {
            holder.cardImage.setImageResource(R.drawable.placeholder_image);
        }
    }


    @Override
    public int getItemCount() {
        return animalList.size();
    }

    public static class CardViewHolder extends RecyclerView.ViewHolder {
        TextView cardDes;
        ImageView cardImage;
        TextView cardTitle;
        TextView cardDistance;

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            cardImage = itemView.findViewById(R.id.cardImage);
            cardTitle = itemView.findViewById(R.id.cardTitle);
            cardDes = itemView.findViewById(R.id.cardDes);
            cardDistance = itemView.findViewById(R.id.cardDistance);
        }
    }
}
