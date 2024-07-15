package com.example.trainbites.CustomerFoodPanel;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import com.example.trainbites.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private ArrayList<CartItemModel> list;
    private Context context;
    private DatabaseReference databaseReference;
    private CartAdapterListener listener;

    public interface CartAdapterListener {
        void onCartUpdated();

    }

    public CartAdapter(ArrayList<CartItemModel> list, Context context, CartAdapterListener listener) {
        this.list = list;
        this.context = context;
        this.listener = listener;
        this.databaseReference = FirebaseDatabase.getInstance().getReference("Cart_item");
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cart_item, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, @SuppressLint("RecyclerView") int position) {
        CartItemModel cartItemModel = list.get(position);

        Picasso.get().load(cartItemModel.getImageUrl()).placeholder(R.drawable.loadingcircle).into(holder.cartImage);
        holder.cartDishName.setText(cartItemModel.getDishName());
        holder.cartPrice.setText(cartItemModel.getPrice());
        holder.dumyPrice.setText(cartItemModel.getPrice());
        holder.itm_count=1;

        holder.itm_count = cartItemModel.getQuantity();
        // Set default value to 1 if itm_count is 0
        if (holder.itm_count == 0) {
            holder.itm_count = 1;
            cartItemModel.setQuantity(1); // Set quantity in cartItemModel to 1 as well
            listener.onCartUpdated();
        }
        holder.showItm.setText(String.valueOf(holder.itm_count));
        holder.dmyItem.setText(String.valueOf(holder.itm_count));
        holder.mainTotal.setText(calculateTotal(holder.dumyPrice.getText().toString(), holder.itm_count));

        holder.addBtn.setOnClickListener(v -> {
            if (holder.itm_count < 10) {
                holder.itm_count++;
                holder.showItm.setText(String.valueOf(holder.itm_count));
                holder.dmyItem.setText(String.valueOf(holder.itm_count));
                holder.mainTotal.setText(calculateTotal(holder.dumyPrice.getText().toString(), holder.itm_count));
                cartItemModel.setQuantity(holder.itm_count);
                listener.onCartUpdated();
            }
        });

        holder.removeBtn.setOnClickListener(v -> {
            if (holder.itm_count > 1) {
                holder.itm_count--;
                holder.showItm.setText(String.valueOf(holder.itm_count));
                holder.dmyItem.setText(String.valueOf(holder.itm_count));
                holder.mainTotal.setText(calculateTotal(holder.dumyPrice.getText().toString(), holder.itm_count));
                cartItemModel.setQuantity(holder.itm_count);
                listener.onCartUpdated();
            }else {
                Toast.makeText(context, "Minimum item count is 1", Toast.LENGTH_SHORT).show();
            }
        });

        holder.deleteBtn.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Delete Item")
                    .setMessage("Are you sure you want to delete this item?")
                    .setPositiveButton("OK", (dialog, which) -> {
                        String key = cartItemModel.getKey();
                        if (key != null) {
                            databaseReference.child(key).removeValue().addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    list.remove(position);
                                    notifyItemRemoved(position);
                                    notifyItemRangeChanged(position, list.size());
                                    Toast.makeText(context, "Item deleted successfully", Toast.LENGTH_SHORT).show();
                                    listener.onCartUpdated();
                                } else {
                                    Toast.makeText(context, "Failed to delete item", Toast.LENGTH_SHORT).show();
                                    Log.e("CartAdapter", "Failed to delete item: " + task.getException().getMessage());
                                }
                            });
                        } else {
                            Toast.makeText(context, "Item key is null", Toast.LENGTH_SHORT).show();
                            Log.e("CartAdapter", "Item key is null");
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class CartViewHolder extends RecyclerView.ViewHolder {

        TextView cartDishName, cartPrice, showItm, dumyPrice, dmyItem, mainTotal;
        ImageView cartImage;
        ImageButton deleteBtn, addBtn, removeBtn;
        int itm_count = 1;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            cartDishName = itemView.findViewById(R.id.CartDishName);
            cartPrice = itemView.findViewById(R.id.UploadPriceTxt);
            cartImage = itemView.findViewById(R.id.imageView);
            deleteBtn = itemView.findViewById(R.id.deleteitemBtn);
            addBtn = itemView.findViewById(R.id.additemBtn);
            removeBtn = itemView.findViewById(R.id.removeitemBtn);
            showItm = itemView.findViewById(R.id.showitem);
            dumyPrice = itemView.findViewById(R.id.dumytotalprice);
            dmyItem = itemView.findViewById(R.id.dumyitmNumber);
            mainTotal = itemView.findViewById(R.id.maintotal);
        }
    }

    private String calculateTotal(String price, int count) {
        double itemPrice = Double.parseDouble(price);
        double total = itemPrice * count;
        DecimalFormat decimalFormat = new DecimalFormat("0.##");
        return decimalFormat.format(total);
    }
}
