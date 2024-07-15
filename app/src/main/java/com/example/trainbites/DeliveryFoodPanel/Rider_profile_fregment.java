package com.example.trainbites.DeliveryFoodPanel;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.trainbites.Chef;
import com.example.trainbites.MainActivity;
import com.example.trainbites.R;
import com.example.trainbites.Rider;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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


public class Rider_profile_fregment extends Fragment {
    private static final String TAG = "RiderProfileFregment";
    ImageButton riderLogoutBtn,riderChangeProfileBtn,eyeBtn;
    Button riderEbitBtn;
    EditText riderEmail,riderPhone,riderPassword,riderUserName;
    ImageView riderProfileView;
    String currentImageurl;
    Uri RiderImageUri;
    FirebaseUser currentRider;
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    FirebaseStorage storage;
    DatabaseReference databaseReference;
    ProgressDialog progressDialog1;
    ProgressDialog progressDialog2;
    private boolean isPasswordVisible=false;




    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_rider_profile_fregment, container, false);

        //iniliazation
        riderPassword=view.findViewById(R.id.RiderPasswordEdittext);
        riderPhone=view.findViewById(R.id.RiderPhoneEdittext);
        riderEmail=view.findViewById(R.id.RiderEmailEdittext);
        riderUserName=view.findViewById(R.id.RidernameId);
        riderLogoutBtn=view.findViewById(R.id.RiderProfileLogoutBtn);
        riderChangeProfileBtn=view.findViewById(R.id.Riderchangeprofile);
        riderEbitBtn=view.findViewById(R.id.RiderProfileEditBtn);
        riderProfileView=view.findViewById(R.id.RiderImageView);
        eyeBtn=view.findViewById(R.id.eyeToggleBtn);

        //firebase inilazation

        storage=FirebaseStorage.getInstance();
        database=FirebaseDatabase.getInstance();
        mAuth=FirebaseAuth.getInstance();
        currentRider=mAuth.getCurrentUser();

        //progressDialog box 1 working
        progressDialog1=new ProgressDialog(getContext());
        progressDialog1.setTitle("Uploading");
        progressDialog1.setMessage("Please wait while your image is being uploaded...");
        progressDialog1.setCanceledOnTouchOutside(false);

        //progressDialog box 2 working
        progressDialog2=new ProgressDialog(getContext());
        progressDialog2.setTitle("Update");
        progressDialog2.setMessage("Please wait while Chef Profile is being updated...");
        progressDialog2.setCanceledOnTouchOutside(false);


        // Check current user and load profile details
        if (currentRider != null) {
            loadChefProfileImage(currentRider.getUid());
            loadChefProfileDetails(currentRider.getUid());
        }

        //eye toggle button working
        eyeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPasswordVisible){
                    riderPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    eyeBtn.setImageResource(R.drawable.hide);
                }else {
                    riderPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    eyeBtn.setImageResource(R.drawable.show);
                }
                isPasswordVisible =! isPasswordVisible;
                riderPassword.setSelection(riderPassword.length());
            }
        });




        //logout button working
        riderLogoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getContext())
                        .setTitle("LogOut")
                        .setMessage("Are you sure you want to Logout...?")
                        .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mAuth.signOut();
                                Toast.makeText(getContext(), "LogOut Successfully", Toast.LENGTH_SHORT).show();
                                Intent intent=new Intent(getContext(), MainActivity.class);
                                startActivity(intent);
                                if (getActivity() !=null){
                                    getActivity().finish();

                                }
                            }
                        }).setNegativeButton("No",null).show();

            }
        });


        //select image from gallery button
        riderChangeProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmDialog();
            }
        });

        //edit button working
        riderEbitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditChefProfileMethod();

            }
        });

        return view;
    }

    //edit chef profile details
    private void EditChefProfileMethod(){
        progressDialog2.show();
        String Uname = riderUserName.getText().toString().trim();
        String mail = riderEmail.getText().toString().trim();
        String passw = riderPassword.getText().toString().trim();
        String phonee = riderPhone.getText().toString().trim();

        if (Uname.isEmpty() || mail.isEmpty() || phonee.isEmpty()) {
            Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
        } else if (passw.isEmpty()|| passw.length()<6){
            Toast.makeText(getContext(), "please enter minimum 6 digits password", Toast.LENGTH_SHORT).show();
            progressDialog2.dismiss();
        } else {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                // Update email and password
                user.updateEmail(mail)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                user.updatePassword(passw)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                // Update other user profile details in Realtime Database
                                                DatabaseReference userRef = database.getReference().child("Rider").child(currentRider.getUid());
                                                userRef.child("phone").setValue(phonee);
                                                userRef.child("password").setValue(passw);
                                                userRef.child("username").setValue(Uname).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        Toast.makeText(getContext(), "Profile Updated Successfully", Toast.LENGTH_SHORT).show();
                                                        progressDialog2.dismiss();
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        progressDialog2.dismiss();
                                                        Toast.makeText(getContext(), "Failed to update profile", Toast.LENGTH_SHORT).show();
                                                        Log.e(TAG, "Failed to update profile: " + e.getMessage());
                                                    }
                                                });
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                progressDialog2.dismiss();
                                                Toast.makeText(getContext(), "Failed to update password", Toast.LENGTH_SHORT).show();
                                                Log.e(TAG, "Failed to update password: " + e.getMessage());
                                            }
                                        });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog2.dismiss();
                                Toast.makeText(getContext(), "Failed to update email", Toast.LENGTH_SHORT).show();
                                Log.e(TAG, "Failed to update email: " + e.getMessage());
                            }
                        });
            } else {
                progressDialog2.dismiss();
                Toast.makeText(getContext(), "User not authenticated", Toast.LENGTH_SHORT).show();
            }
        }
    }


    //upload image from firebase
    private void postImage(){
        if (RiderImageUri != null && currentRider != null){
            progressDialog1.show();
            final String uid=currentRider.getUid();
            final StorageReference reference=storage.getReference().child("Rider_image").child(uid + ".jpg");
            reference.putFile(RiderImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String imageUrl=uri.toString();
                            database.getReference().child("Rider").child(uid).child("RiderImage").setValue(imageUrl)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            progressDialog1.dismiss(); // Dismiss progress dialog
                                            showUploadSuccessDialog();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            progressDialog1.dismiss(); // Dismiss progress dialog
                                            Log.e(TAG, "Failed to update image URL in database: " + e.getMessage());
                                            Toast.makeText(getContext(), "Error Occurred", Toast.LENGTH_SHORT).show();
                                        }
                                    });


                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog1.dismiss(); // Dismiss progress dialog
                            Log.e(TAG, "Failed to get download URL: " + e.getMessage());

                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog1.dismiss(); // Dismiss progress dialog
                    Log.e(TAG, "Image upload failed: " + e.getMessage());

                }
            });
        }

    }
    // confirm dialog show after upload image
    private void showUploadSuccessDialog() {
        new AlertDialog.Builder(getContext())
                .setTitle("Upload Successful")
                .setMessage("Your profile picture has been successfully uploaded.")
                .setPositiveButton("OK", null)
                .show();
    }

    //chow confirm dialoug choosw image from gallery

    private void showConfirmDialog() {
        new AlertDialog.Builder(getContext())
                .setTitle("Change Profile Picture")
                .setMessage("Are you sure you want to change your profile picture?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        requestStoragePermission();
                    }
                }).setNegativeButton("No", null).show();
    }


    //request to gallery
    private void requestStoragePermission() {
        Dexter.withContext(getContext()).withPermission(Manifest.permission.READ_EXTERNAL_STORAGE).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse response) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 101);
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse response) {
                Toast.makeText(getContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest request, PermissionToken token) {
                token.continuePermissionRequest();
            }
        }).check();
    }

    // Handle the result of image selection
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == RESULT_OK && data != null) {
            RiderImageUri = data.getData();
            riderProfileView.setImageURI(RiderImageUri);
            deletePreviousImageAndUploadNewImage();

        }
    }

    private void deletePreviousImageAndUploadNewImage() {
        if (currentImageurl != null && !currentImageurl.isEmpty()) {
            StorageReference previousImageRef = storage.getReferenceFromUrl(currentImageurl);
            previousImageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    // Successfully deleted previous image
                    postImage();                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Failed to delete previous image
                    Log.e(TAG, "Failed to delete previous image: " + e.getMessage());
                    Toast.makeText(getContext(), "Failed to delete previous image", Toast.LENGTH_SHORT).show();
                    postImage(); // Attempt to upload new image anyway
                }
            });
        } else {
            postImage(); // No previous image, proceed with uploading new image
        }
    }

    //load chef details from firebase and show on app
    private void loadChefProfileDetails(String uid) {

        DatabaseReference cRef=database.getReference().child("Rider").child(uid);
        cRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    Rider riderModel=snapshot.getValue(Rider.class);
                    if (riderModel != null){
                        riderEmail.setText(riderModel.getEmail());
                        riderPhone.setText(riderModel.getPhone());
                        riderPassword.setText(riderModel.getPassword());
                        riderUserName.setText(riderModel.getUsername());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load Rider details", Toast.LENGTH_SHORT).show();

            }
        });


    }
    //load chef image from firebase and show on app

    private void loadChefProfileImage(String uid){

        DatabaseReference uRef = database.getReference().child("Rider").child(uid).child("RiderImage");
        uRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    currentImageurl = snapshot.getValue(String.class);
                    if (currentImageurl != null && !currentImageurl.isEmpty()) {
                        Glide.with(getContext()).load(currentImageurl).into(riderProfileView);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load profile image", Toast.LENGTH_SHORT).show();
            }
        });

    }





}