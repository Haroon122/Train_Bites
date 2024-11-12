package com.example.trainbites.ChefFoodPanel;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class ChefUpdateItemsActivity extends AppCompatActivity {
    TextView price, dishname, descriptionTxt, quantity;
    ImageView showimg;
    Button updateBtn, deleteBtn;
    Uri imageUri;
    ProgressDialog progressDialog;
    ProgressDialog progressDialog1;

    DatabaseReference databaseReference;
    StorageReference storageReference;

    String key;
    String imageUrl;

    private static final String TAG = "ChefUpdateItemsActivity";

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

        // Progress dialog for update items
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Updating Item");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);

        // Progress dialog for delete items
        progressDialog1= new ProgressDialog(this);
        progressDialog1.setTitle("Delete Item");
        progressDialog1.setMessage("Please wait...");
        progressDialog1.setCanceledOnTouchOutside(false);

        // Retrieve data from intent
        Intent intent = getIntent();
        if (intent != null) {
            key = intent.getStringExtra("ItemKey");
            Log.d("ChefUpdateItemsActivity", "Received ItemKey: " + key);  // Log the received key
            String orderPrice = intent.getStringExtra("ChefOrderPrice");
            String orderDishName = intent.getStringExtra("Cheforderdishname");
            String orderDescription = intent.getStringExtra("ChefOrderdiscription");
            imageUrl = intent.getStringExtra("ChefloadImageforOrder");
            String orderQuantity = intent.getStringExtra("ChefItemQuantity");

            // Set retrieved data to views
            price.setText(orderPrice);
            dishname.setText(orderDishName);
            descriptionTxt.setText(orderDescription);
            quantity.setText(orderQuantity);
            Picasso.get().load(imageUrl).placeholder(R.drawable.loadingcircle).into(showimg);
        }

        // Set click listener for update button
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInputs() && key != null) {
                    updateItemMethod();
                } else {
                    Toast.makeText(ChefUpdateItemsActivity.this, "Item key is null", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Item key is null");
                }
            }
        });

        // Set click listener for delete button
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (key != null) {
                    new AlertDialog.Builder(ChefUpdateItemsActivity.this)
                            .setTitle("Delete")
                            .setMessage("Are you sure you want to delete the item?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    deleteItemMethod();
                                }
                            }).setNegativeButton("No", null).show();
                } else {
                    Toast.makeText(ChefUpdateItemsActivity.this, "Item key is null", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Item key is null");
                }
            }
        });

        showimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestStoragePermission();
            }
        });
    }

    //update image method
    private void updateItemMethod() {
        progressDialog.show();

        if (imageUri != null) {
            // Upload the new image
            StorageReference fileRef = storageReference.child(key + ".jpg");
            fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String newImageUrl = uri.toString();
                            updateDatabase(newImageUrl);
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(ChefUpdateItemsActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        } else {
            // No new image selected, update database with existing imageUrl
            updateDatabase(imageUrl);
        }
    }
// update data method
    private void updateDatabase(String newImageUrl) {
        Map<String, Object> itemUpdates = new HashMap<>();
        itemUpdates.put("price", price.getText().toString());
        itemUpdates.put("dishName", dishname.getText().toString());
        itemUpdates.put("description", descriptionTxt.getText().toString());
        itemUpdates.put("quantity", quantity.getText().toString());
        itemUpdates.put("postImage", newImageUrl);

        databaseReference.child(key).updateChildren(itemUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressDialog.dismiss();
                if (task.isSuccessful()) {
                    Toast.makeText(ChefUpdateItemsActivity.this, "Item updated successfully", Toast.LENGTH_SHORT).show();
                    finish();
                    fetchUpdatedData();
                } else {
                    Toast.makeText(ChefUpdateItemsActivity.this, "Failed to update item", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(ChefUpdateItemsActivity.this, "Failed to update item", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //fetch data from firebase method
    private void fetchUpdatedData() {
        databaseReference.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String updatedPrice = dataSnapshot.child("price").getValue(String.class);
                    String updatedDishName = dataSnapshot.child("dishname").getValue(String.class);
                    String updatedDescription = dataSnapshot.child("description").getValue(String.class);
                    String updatedQuantity = dataSnapshot.child("quantity").getValue(String.class);
                    String updatedImageUrl = dataSnapshot.child("imageUrl").getValue(String.class);

                    // Update UI with the new values
                    price.setText(updatedPrice);
                    dishname.setText(updatedDishName);
                    descriptionTxt.setText(updatedDescription);
                    quantity.setText(updatedQuantity);
                    Picasso.get().load(updatedImageUrl).placeholder(R.drawable.loadingcircle).memoryPolicy(MemoryPolicy.NO_CACHE).into(showimg);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ChefUpdateItemsActivity.this, "Failed to fetch updated data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //delete data method
    private void deleteItemMethod() {
        progressDialog1.show();
        databaseReference.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Log.d(TAG, "Item found: " + snapshot.getKey());
                    snapshot.getRef().removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            progressDialog1.dismiss();
                            if (task.isSuccessful()) {
                                Toast.makeText(ChefUpdateItemsActivity.this, "Item deleted successfully", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(ChefUpdateItemsActivity.this, "Failed to delete item", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    progressDialog1.dismiss();
                    Toast.makeText(ChefUpdateItemsActivity.this, "Item does not exist", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog1.dismiss();
                Toast.makeText(ChefUpdateItemsActivity.this, "Failed to delete item", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //check validity method
    private boolean validateInputs() {
        if (price.getText().toString().isEmpty()) {
            price.setError("Price is required");
            return false;
        }
        if (dishname.getText().toString().isEmpty()) {
            dishname.setError("Dish name is required");
            return false;
        }
        if (descriptionTxt.getText().toString().isEmpty()) {
            descriptionTxt.setError("Description is required");
            return false;
        }
        if (quantity.getText().toString().isEmpty()) {
            quantity.setError("Quantity is required");
            return false;
        }
        return true;
    }

    //request image from gallery method
    private void requestStoragePermission() {
        Dexter.withContext(this).withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        Intent intent = new Intent(Intent.ACTION_PICK);
                        intent.setType("image/*");
                        startActivityForResult(Intent.createChooser(intent, "Select Image"), 1);
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        Toast.makeText(ChefUpdateItemsActivity.this, "Storage permission is required to select an image", Toast.LENGTH_SHORT).show();
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
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            showimg.setImageURI(imageUri);
        }
    }
}
