package com.example.trainbites.ChefFoodPanel;

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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.trainbites.Chef;
import com.example.trainbites.MainActivity;
import com.example.trainbites.R;
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

public class Chef_profileID_fregment extends Fragment {

    private static final String TAG = "ChefProfileFregment";
    ImageButton eyeBtn;
    FirebaseStorage storage;
    FirebaseAuth mAuth;
    FirebaseUser currentChef;
    Uri ChefImageUri;
    FirebaseDatabase database;
    ImageView imageView,chefImageBtn;
    String currentImageUrl;
    EditText Chefmail, ChefPassword, ChefPhone,Chefname;
    ProgressDialog progressDialog1,progressDialog2;
    Button editBtn,logoutBtn;
    private boolean isPasswordVisible=false;


    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_chef_profile_i_d_fregment, container, false);

        //inilization
        chefImageBtn=view.findViewById(R.id.chefImageBtn);
        logoutBtn=view.findViewById(R.id.CheflogoutBtn);
        eyeBtn=view.findViewById(R.id.eyeToggleBtn);
        editBtn=view.findViewById(R.id.ChefProfileEditBtn);
        Chefmail=view.findViewById(R.id.ChefEmailEdittext);
        ChefPhone=view.findViewById(R.id.ChefPhoneEdittext);
        ChefPassword=view.findViewById(R.id.ChefPasswordEdittext);
        Chefname=view.findViewById(R.id.ChefnameId);
        imageView=view.findViewById(R.id.ChefImageView);

        //database inilization
        storage = FirebaseStorage.getInstance();
        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentChef = mAuth.getCurrentUser();

        progressDialog1 = new ProgressDialog(getContext());
        progressDialog2=new ProgressDialog(getContext());

        //progressbar1 for uploding image
        progressDialog1.setTitle("Uploading");
        progressDialog1.setMessage("Please wait while your image is being uploaded...");
        progressDialog1.setCanceledOnTouchOutside(false);

        //progressbar2 for update user detail
        progressDialog2.setTitle("Update");
        progressDialog2.setMessage("Please wait while Chef Profile is being updated...");
        progressDialog2.setCanceledOnTouchOutside(false);

        // Check current user and load profile details
        if (currentChef != null) {
           loadChefProfileImage(currentChef.getUid());
            loadChefProfileDetails(currentChef.getUid());
        }

        //eye toggle button working
        eyeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPasswordVisible){
                    ChefPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    eyeBtn.setImageResource(R.drawable.hide);// Set to closed eye icon
                }else {
                    ChefPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    eyeBtn.setImageResource(R.drawable.show);// Set to open eye icon
                }
                isPasswordVisible = !isPasswordVisible;
                // Move cursor to the end of the text
                ChefPassword.setSelection(ChefPassword.length());
            }
        });


        //logout button working
        logoutBtn.setOnClickListener(new View.OnClickListener() {
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
        chefImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmDialog();
            }
        });

        //edit button working
        editBtn.setOnClickListener(new View.OnClickListener() {
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
        String Uname = Chefname.getText().toString().trim();
        String mail = Chefmail.getText().toString().trim();
        String passw = ChefPassword.getText().toString().trim();
        String phonee = ChefPhone.getText().toString().trim();

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
                                                DatabaseReference userRef = database.getReference().child("Chef").child(currentChef.getUid());
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
        if (ChefImageUri != null && currentChef != null){
            progressDialog1.show();
            final String uid=currentChef.getUid();
            final StorageReference reference=storage.getReference().child("Chef_image").child(uid + ".jpg");
            reference.putFile(ChefImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String imageUrl=uri.toString();
                            database.getReference().child("Chef").child(uid).child("ChefImage").setValue(imageUrl)
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
            ChefImageUri = data.getData();
            imageView.setImageURI(ChefImageUri);
            deletePreviousImageAndUploadNewImage();

        }
    }

    private void deletePreviousImageAndUploadNewImage() {
        if (currentImageUrl != null && !currentImageUrl.isEmpty()) {
            StorageReference previousImageRef = storage.getReferenceFromUrl(currentImageUrl);
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

        DatabaseReference cRef=database.getReference().child("Chef").child(uid);
        cRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    Chef chefModel=snapshot.getValue(Chef.class);
                    if (chefModel != null){
                        Chefmail.setText(chefModel.getEmail());
                        ChefPhone.setText(chefModel.getPhone());
                        ChefPassword.setText(chefModel.getPassword());
                        Chefname.setText(chefModel.getUsername());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load Chef details", Toast.LENGTH_SHORT).show();

            }
        });


    }
    //load chef image from firebase and show on app

    private void loadChefProfileImage(String uid){

        DatabaseReference uRef = database.getReference().child("Chef").child(uid).child("ChefImage");
        uRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    currentImageUrl = snapshot.getValue(String.class);
                    if (currentImageUrl != null && !currentImageUrl.isEmpty()) {
                        Glide.with(getContext()).load(currentImageUrl).into(imageView);
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