package com.example.trainbites.ChefFoodPanel;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.trainbites.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.squareup.picasso.Picasso;

public class ChefUpdateItemsActivity extends AppCompatActivity {
    TextView price, dishname, descriptionTxt, quantity;
    ImageView showimg;
    Button updateBtn, deleteBtn;
    Uri imageUri;
    ProgressDialog progressDialog;

    DatabaseReference databaseReference;
    StorageReference storageReference;

    String ItemKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chef_update_items);

        // Initialize Firebase references
        databaseReference = FirebaseDatabase.getInstance().getReference().child("product");
        storageReference = FirebaseStorage.getInstance().getReference().child("product");

        // Initialize views
        price = findViewById(R.id.PriceEditTxt);
        dishname = findViewById(R.id.DishNameEditTxt);
        descriptionTxt = findViewById(R.id.DescriptionEditTxt);
        showimg = findViewById(R.id.ImageViewer);
        quantity = findViewById(R.id.QuantityEditTxt);
        updateBtn = findViewById(R.id.UpdateItemButton);
        deleteBtn = findViewById(R.id.DeleteItemButton);

        // Progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Updating Item");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);

        // Retrieve data from intent
        Intent intent = getIntent();
        if (intent != null) {
            String orderPrice = intent.getStringExtra("ChefOrderPrice");
            String orderDishName = intent.getStringExtra("Cheforderdishname");
            String orderDescription = intent.getStringExtra("ChefOrderdiscription");
            String orderImage = intent.getStringExtra("ChefloadImageforOrder");
            String orderQuantity = intent.getStringExtra("ChefItemQuantity");
            ItemKey = intent.getStringExtra("ChefItemKey");

            // Set retrieved data to views
            price.setText(orderPrice);
            dishname.setText(orderDishName);
            descriptionTxt.setText(orderDescription);
            quantity.setText(orderQuantity);
            Picasso.get().load(orderImage).placeholder(R.drawable.loadingcircle).into(showimg);
        }

        // Set click listener for update button
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInputs()) {
                }
            }
        });

        // Set click listener for delete button
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        // Set click listener for image view to select image
        showimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestStoragePermission();
            }
        });
    }

    private void requestStoragePermission() {
        Dexter.withContext(ChefUpdateItemsActivity.this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(intent, 101);
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        Toast.makeText(ChefUpdateItemsActivity.this, "Storage permission is required to select images", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData(); // Update the imageUri when a new image is selected
            showimg.setImageURI(imageUri);
        }
    }

    private boolean validateInputs() {
        if (dishname.getText().toString().isEmpty()) {
            Toast.makeText(ChefUpdateItemsActivity.this, "Please enter the dish name", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (descriptionTxt.getText().toString().isEmpty()) {
            Toast.makeText(ChefUpdateItemsActivity.this, "Please enter the description", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (quantity.getText().toString().isEmpty()) {
            Toast.makeText(ChefUpdateItemsActivity.this, "Please enter the quantity", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (price.getText().toString().isEmpty()) {
            Toast.makeText(ChefUpdateItemsActivity.this, "Please enter the price", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }


}
