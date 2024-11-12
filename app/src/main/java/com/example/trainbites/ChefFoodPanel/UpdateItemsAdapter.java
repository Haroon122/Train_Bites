package com.example.trainbites.ChefFoodPanel;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trainbites.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class UpdateItemsAdapter extends RecyclerView.Adapter<UpdateItemsAdapter.ViewHolder> {
    private ArrayList<chefProjectModel> list;
    private Context context;

    public UpdateItemsAdapter(ArrayList<chefProjectModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public UpdateItemsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item, parent, false);
        return new UpdateItemsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UpdateItemsAdapter.ViewHolder holder, int position) {
        chefProjectModel model = list.get(position);

        // Load image using Picasso
        Picasso.get().load(model.getPostImage()).placeholder(R.drawable.loadingcircle).into(holder.postImage);

        // Set text views
        holder.UploadDishName.setText(model.getDishName());
        holder.UploadDescription.setText(model.getDescription());
        holder.UploadQuantity.setText(model.getQuantity());
        holder.UploadPrice.setText(model.getPrice());

        // Handle item click
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChefUpdateItemsActivity.class);
                String itemkey=model.getKey();
                Log.d("PreviousActivity", "Sending ItemKey: " + itemkey);  // Log the key being sent
                intent.putExtra("ItemKey",itemkey);
                intent.putExtra("Cheforderdishname", model.getDishName());
                intent.putExtra("ChefOrderPrice", model.getPrice());
                intent.putExtra("ChefloadImageforOrder", model.getPostImage());
                intent.putExtra("ChefOrderdiscription", model.getDescription());
                intent.putExtra("ChefItemQuantity", model.getQuantity());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView UploadDishName, UploadDescription, UploadQuantity, UploadPrice;
        ImageView postImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            UploadDishName = itemView.findViewById(R.id.UploadDishnameTxt);
            UploadDescription = itemView.findViewById(R.id.UploadDescriptionTxt);
            UploadQuantity = itemView.findViewById(R.id.UploadQuantitytxt);
            UploadPrice = itemView.findViewById(R.id.UploadPriceTxt);
            postImage = itemView.findViewById(R.id.imageView);
        }
    }
}
