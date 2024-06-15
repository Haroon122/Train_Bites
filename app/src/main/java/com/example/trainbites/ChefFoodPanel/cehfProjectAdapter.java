package com.example.trainbites.ChefFoodPanel;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trainbites.CustomerFoodPanel.DataRecieveFromCustomerActivity;
import com.example.trainbites.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class cehfProjectAdapter extends RecyclerView.Adapter<cehfProjectAdapter.ViewHolder> {


    ArrayList<chefProjectModel>list;
    Context context;

    public cehfProjectAdapter(ArrayList<chefProjectModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        chefProjectModel model=list.get(position);

        //for image
        Picasso.get().load(model.getPostImage()).placeholder(R.drawable.loadingcircle).into(holder.postImage);
        //for text
        holder.UploadDishName.setText(model.getDishName());
        holder.UploadDescription.setText(model.getDescription());
        holder.UploadQuantity.setText(model.getQuantity());
        holder.UploadPrice.setText(model.getPrice());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, DataRecieveFromCustomerActivity.class);
                intent.putExtra("orderdishname",model.getDishName());
                intent.putExtra("OrderPrice",model.getPrice());
                intent.putExtra("loadImageforOrder",model.getPostImage());
                intent.putExtra("Orderdiscription",model.getDescription());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView UploadDishName, UploadDescription, UploadQuantity, UploadPrice;
        ImageView postImage;



        @SuppressLint("WrongViewCast")
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            UploadDishName = itemView.findViewById(R.id.UploadDishnameTxt);
            UploadDescription = itemView.findViewById(R.id.UploadDescriptionTxt);
            UploadQuantity = itemView.findViewById(R.id.UploadQuantitytxt);
            UploadPrice = itemView.findViewById(R.id.UploadPriceTxt);
            postImage = itemView.findViewById(R.id.imageView);

        }
    }}
