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

public class AdminAdapter extends RecyclerView.Adapter<AdminAdapter.AnimalViewHolder> {
    private final Context context;
    private final List<Animal> adminAnimals;

    public AdminAdapter(Context context, List<Animal> animals) {
        this.context = context;
        this.adminAnimals = animals;
    }

    @NonNull
    @Override
    public AnimalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.liked_animal_card, parent, false);
        return new AnimalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AnimalViewHolder holder, int position) {
        Animal animal = adminAnimals.get(position);
        holder.nameTextView.setText(animal.getName() + " , " + animal.getAge() + " years old");

        Glide.with(context)
                .load(animal.getImgURL())
                .placeholder(R.drawable.placeholder_image)
                .into(holder.imageView);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, OpenAnimalProfileAdminActivity.class);
            intent.putExtra("animal_id", animal.getId());
            Log.d("Animal ID From AdminAdapter", "Animal ID: " + animal.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return adminAnimals.size();
    }

    public static class AnimalViewHolder extends  RecyclerView.ViewHolder{
        ImageView imageView;
        TextView nameTextView;

        public AnimalViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.animalImage);
            nameTextView = itemView.findViewById(R.id.animalName);
        }
    }
}
