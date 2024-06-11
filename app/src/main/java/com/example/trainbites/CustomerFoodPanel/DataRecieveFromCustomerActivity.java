package com.example.trainbites.CustomerFoodPanel;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.trainbites.R;
import com.squareup.picasso.Picasso;

public class DataRecieveFromCustomerActivity extends AppCompatActivity {

    TextView price,dishname;
    ImageView showimg;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_data_recieve_from_customer);

        price=findViewById(R.id.OrderPrice);
        dishname=findViewById(R.id.orderdishname);
        showimg=findViewById(R.id.loadImageforOrder);

        Picasso.get().load(getIntent().getStringExtra("loadImageforOrder"))
                .placeholder(R.drawable.loadingcircle).into(showimg);
        price.setText(getIntent().getStringExtra("OrderPrice"));
        dishname.setText(getIntent().getStringExtra("orderdishname"));

    }
}