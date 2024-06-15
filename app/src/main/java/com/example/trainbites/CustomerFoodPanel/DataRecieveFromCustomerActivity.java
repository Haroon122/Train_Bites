package com.example.trainbites.CustomerFoodPanel;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.example.trainbites.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class DataRecieveFromCustomerActivity extends AppCompatActivity {

    TextView price, dishname,discriptiontxt;
    ImageView showimg;
    Button cartButton;
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
        cartButton=findViewById(R.id.cartBtn);
        discriptiontxt=findViewById(R.id.Cartiscription);

        cartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                databaseReference= FirebaseDatabase.getInstance().getReference("Cart_item");
                String key = databaseReference.push().getKey(); // Generate a unique key for the item
                CartItemModel cartItem = new CartItemModel(dishname.getText().toString(), price.getText().toString(), getIntent().getStringExtra("loadImageforOrder"),key);
                databaseReference.child(key).setValue(cartItem);
                Toast.makeText(DataRecieveFromCustomerActivity.this, "Item add to Cart", Toast.LENGTH_SHORT).show();

            }
        });

        price.setText(getIntent().getStringExtra("OrderPrice"));
        dishname.setText(getIntent().getStringExtra("orderdishname"));
        discriptiontxt.setText(getIntent().getStringExtra("Orderdiscription"));
        Picasso.get().load(getIntent().getStringExtra("loadImageforOrder"))
                .placeholder(R.drawable.loadingcircle).into(showimg);
    }
}
