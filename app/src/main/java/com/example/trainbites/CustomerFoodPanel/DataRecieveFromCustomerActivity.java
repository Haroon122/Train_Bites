package com.example.trainbites.CustomerFoodPanel;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.trainbites.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class DataRecieveFromCustomerActivity extends AppCompatActivity {

    TextView price, dishname, discriptiontxt;
    ImageView showimg;
    Button cartButton, confirmOrderButton;
    DatabaseReference databaseReference;

    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_data_recieve_from_customer);

        price = findViewById(R.id.CartPrice);
        dishname = findViewById(R.id.Cartdishname);
        showimg = findViewById(R.id.loadImageforOrder);
        cartButton = findViewById(R.id.cartBtn);
        discriptiontxt = findViewById(R.id.Cartiscription);
        confirmOrderButton = findViewById(R.id.confirmOrdeerBtn);

        // Retrieve data from intent extras
        String orderPrice = getIntent().getStringExtra("OrderPrice");
        String orderDishName = getIntent().getStringExtra("orderdishname");
        String orderDescription = getIntent().getStringExtra("Orderdiscription");
        String orderImage = getIntent().getStringExtra("loadImageforOrder");

        // Set text views and image using Picasso
        price.setText(orderPrice);
        dishname.setText(orderDishName);
        discriptiontxt.setText(orderDescription);
        Picasso.get().load(orderImage)
                .placeholder(R.drawable.loadingcircle).into(showimg);

        // Set click listener for confirm order button
        confirmOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Pass data to ConfirmOrderScreen
                Intent intent = new Intent(DataRecieveFromCustomerActivity.this, ConfirmOrderForSingleScreen.class);
                intent.putExtra("Total Amount", orderPrice); // Pass the order price as total amount
                intent.putExtra("DishName",orderDishName);
                startActivity(intent);
            }
        });

        // Set click listener for cart button (if needed)
        cartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle adding item to cart
                addToCart(orderPrice, orderDishName, orderImage);
            }
        });
    }

    // Method to add item to cart
    private void addToCart(String price, String dishName, String image) {
        databaseReference = FirebaseDatabase.getInstance().getReference("Cart_item");
        String key = databaseReference.push().getKey(); // Generate a unique key for the item
        CartItemModel cartItem = new CartItemModel(dishName, price, image, key);
        databaseReference.child(key).setValue(cartItem);
        Toast.makeText(DataRecieveFromCustomerActivity.this, "Item added to Cart", Toast.LENGTH_SHORT).show();
    }
}
