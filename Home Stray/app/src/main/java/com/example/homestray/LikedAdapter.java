package com.example.homestray;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class LikedAdapter  extends RecyclerView.Adapter<LikedAdapter.LikedViewHolder> {
    private final Context context;
    private final List<Animal> likedAnimals;

    public LikedAdapter(Context context, List<Animal> likedAnimals) {
        this.context = context;
        this.likedAnimals = likedAnimals;
    }

    @NonNull
    @Override
    public LikedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.liked_animal_card, parent, false);
        return new LikedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LikedViewHolder holder, int position) {
        Animal animal = likedAnimals.get(position);
        holder.nameTextView.setText(animal.getName() + " , " + animal.getAge() + " years old");
        Log.d("AdapterCheck", "Binding animal: " + animal.getName());

        Glide.with(context)
                .load(animal.getImgURL())
                .placeholder(R.drawable.placeholder_image)
                .into(holder.imageView);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, OpenAnimalProfileActivity.class);
            intent.putExtra("animal_id", animal.getId());
            Log.d("Animal ID", "Animal ID: " + animal.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        Log.d("AdapterDebug", "Items in list: " + likedAnimals.size());
        return likedAnimals.size();
    }

    public static class LikedViewHolder extends  RecyclerView.ViewHolder{
        ImageView imageView;
        TextView nameTextView;

        public LikedViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.animalImage);
            nameTextView = itemView.findViewById(R.id.animalName);
        }
    }
}
