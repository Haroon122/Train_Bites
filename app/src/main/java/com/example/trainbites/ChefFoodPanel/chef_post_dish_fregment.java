package com.example.trainbites.ChefFoodPanel;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.trainbites.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

public class chef_post_dish_fregment extends Fragment {

    private EditText DishName, Description, Quantity, Price;
    private Button PostItem;
    private ImageButton PostImage;
    private ImageView ViewImage;
    private Uri ImageUri;

    private FirebaseDatabase database;
    private FirebaseStorage storage;
    private ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chef_post_dish_fregment, container, false);

        // Initialize the views
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        DishName = view.findViewById(R.id.DishNameEditTxt);
        Description = view.findViewById(R.id.DescriptionEditTxt);
        Quantity = view.findViewById(R.id.QuantityEditTxt);
        Price = view.findViewById(R.id.PriceEditTxt);
        ViewImage = view.findViewById(R.id.ImageViewer);
        PostItem = view.findViewById(R.id.postdishButton);
        PostImage = view.findViewById(R.id.cameraButton);


        //dialog box
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setTitle("Uploading");


        // Post item click listener
        PostItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInputs()) {
                    uploadImage();
                }
            }
        });

        // Post image click listener
        PostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestImagePermission();
            }
        });
        return view;
    }


    //check edit text entries
    private boolean validateInputs() {
        if (DishName.getText().toString().isEmpty()) {
            Toast.makeText(getContext(), "Please enter the dish name", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (Description.getText().toString().isEmpty()) {
            Toast.makeText(getContext(), "Please enter the description", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (Quantity.getText().toString().isEmpty()) {
            Toast.makeText(getContext(), "Please enter the quantity", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (Price.getText().toString().isEmpty()) {
            Toast.makeText(getContext(), "Please enter the price", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (ImageUri == null) {
            Toast.makeText(getContext(), "Please select an image", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }


    //upload data on database
    private void uploadImage() {
        progressDialog.show();
        final StorageReference reference = storage.getReference().child("product")
                .child(System.currentTimeMillis() + "");
        reference.putFile(ImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {

                    // Generate a unique key using push()
                    DatabaseReference productRef = database.getReference().child("product").push();
                    String uniqueKey = productRef.getKey();
                    @Override
                    public void onSuccess(Uri uri) {
                        chefProjectModel chefModel = new chefProjectModel();
                        chefModel.setPostImage(uri.toString());
                        chefModel.setDishName(DishName.getText().toString());
                        chefModel.setDescription(Description.getText().toString());
                        chefModel.setQuantity(Quantity.getText().toString());
                        chefModel.setPrice(Price.getText().toString());
                        chefModel.setKey(uniqueKey); // Set the unique key in the model

                        database.getReference().child("product").push().setValue(chefModel)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        progressDialog.dismiss();
                                        Toast.makeText(getContext(), "Item Upload Successful", Toast.LENGTH_SHORT).show();
                                        clearInputs();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressDialog.dismiss();
                                        Toast.makeText(getContext(), "Error Occurred", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(getContext(), "Image Upload Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void clearInputs() {
        DishName.setText("");
        Description.setText("");
        Quantity.setText("");
        Price.setText("");
        ViewImage.setImageResource(0); // Clear the ImageView
        ImageUri = null; // Reset the ImageUri
    }


    // image permission from gellery
    private void requestImagePermission() {
        Dexter.withContext(getContext()).withPermission(Manifest.permission.READ_EXTERNAL_STORAGE).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 101);
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                Toast.makeText(getContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).check();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == RESULT_OK && data != null) {
            ImageUri = data.getData();
            ViewImage.setImageURI(ImageUri);
        }
    }
}