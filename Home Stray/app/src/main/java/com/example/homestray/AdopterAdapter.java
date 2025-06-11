package com.example.homestray;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class AdopterAdapter extends RecyclerView.Adapter<AdopterAdapter.AdopterViewHolder> {
    private List<Adopter> adopters;
    private Context context;

    public AdopterAdapter(List<Adopter> adopters, Context context) {
        this.adopters = adopters;
        this.context = context;
    }

    @NonNull
    @Override
    public AdopterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adoptor_card, parent, false);
        return new AdopterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdopterViewHolder holder, int position) {
        Adopter adopter = adopters.get(position);
        Glide.with(context).load(adopter.profileImageUrl).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return adopters.size();
    }

    public static class AdopterViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public AdopterViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageViewAdopter);
        }
    }
}

