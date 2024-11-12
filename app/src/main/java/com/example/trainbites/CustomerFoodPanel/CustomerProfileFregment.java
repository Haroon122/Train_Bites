package com.example.trainbites.CustomerFoodPanel;

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
import com.example.trainbites.MainActivity;
import com.example.trainbites.R;
import com.example.trainbites.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
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

public class CustomerProfileFregment extends Fragment {
    private static final String TAG = "CustomerProfileFregment";
    ImageButton eyeBtn;
    private FirebaseStorage storage;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private Uri UserImageUri;
    private FirebaseDatabase database;
    private ImageView imageView,userAddImage;
    private String currentImageUrl;
    EditText email, password, phone, username;
    ProgressDialog progressDialog1,progressDialog2;
    Button editBtn,logoutBtn;
    private boolean isPasswordVisible=false;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_customer_profile_fregment, container, false);

        // Initialization
        email = view.findViewById(R.id.userEmailEdittext);
        password = view.findViewById(R.id.userPasswordEdittext);
        phone = view.findViewById(R.id.userPhoneEdittext);
        username=view.findViewById(R.id.userNAmeId);
        imageView = view.findViewById(R.id.userImageView);
        eyeBtn=view.findViewById(R.id.eyeToggleBtn);
        userAddImage = view.findViewById(R.id.userAddImageBtn);
        logoutBtn=view.findViewById(R.id.LogoutCustomerId);
        editBtn=view.findViewById(R.id.EditUserProfileId);

        //database inilization
        storage = FirebaseStorage.getInstance();
        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        progressDialog1 = new ProgressDialog(getContext());
        progressDialog2=new ProgressDialog(getContext());

        //progressbar1 for uploding image
        progressDialog1.setTitle("Uploading");
        progressDialog1.setMessage("Please wait while your image is being uploaded...");
        progressDialog1.setCanceledOnTouchOutside(false);

        //progressbar2 for update user detail
        progressDialog2.setTitle("Update");
        progressDialog2.setMessage("Please wait while User Profile is being updated...");
        progressDialog2.setCanceledOnTouchOutside(false);

        // Check current user and load profile details
        if (currentUser != null) {
            loadUserProfileImage(currentUser.getUid());
            loadUserProfileDetails(currentUser.getUid());
        }

        // Change profile image button
        userAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmDialog();
            }
        });

        //eye toggle button working
        eyeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPasswordVisible){
                    password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    eyeBtn.setImageResource(R.drawable.hide);// Set to closed eye icon
                }else {
                    password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    eyeBtn.setImageResource(R.drawable.show);// Set to open eye icon
                }
                isPasswordVisible = !isPasswordVisible;
                // Move cursor to the end of the text
                password.setSelection(password.length());
            }
        });

        //user profile log out button

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

        //Edit user profile if Button working
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditUserProfileMethod();
            }


            private void EditUserProfileMethod() {
                progressDialog2.show();
                String Uname = username.getText().toString().trim();
                String mail = email.getText().toString().trim();
                String passw = password.getText().toString().trim();
                String phonee = phone.getText().toString().trim();

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
                                                        DatabaseReference userRef = database.getReference().child("User").child(currentUser.getUid());
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


        });

        return view;
    }



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

    // Request storage permission to access gallery
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
            UserImageUri = data.getData();
            imageView.setImageURI(UserImageUri);
            deletePreviousImageAndUploadNewImage();
        }
    }

    // Load user profile image from Firebase Database
    private void loadUserProfileImage(String uid) {
        DatabaseReference uRef = database.getReference().child("User").child(uid).child("userImage");
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

    // Load user profile details from Firebase Database
    private void loadUserProfileDetails(String uid) {
        DatabaseReference uRef=database.getReference().child("User").child(uid);
        uRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    User userModel=snapshot.getValue(User.class);
                    if (userModel !=null){
                        email.setText(userModel.getEmail());
                        password.setText(userModel.getPassword());
                        phone.setText(userModel.getPhone());
                        username.setText(userModel.getUsername());
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load user details", Toast.LENGTH_SHORT).show();

            }
        });
    }

    // Delete previous image and upload new one
    private void deletePreviousImageAndUploadNewImage() {
        if (currentImageUrl != null && !currentImageUrl.isEmpty()) {
            StorageReference previousImageRef = storage.getReferenceFromUrl(currentImageUrl);
            previousImageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    // Successfully deleted previous image
                    postNewImage();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Failed to delete previous image
                    Log.e(TAG, "Failed to delete previous image: " + e.getMessage());
                    Toast.makeText(getContext(), "Failed to delete previous image", Toast.LENGTH_SHORT).show();
                    postNewImage(); // Attempt to upload new image anyway
                }
            });
        } else {
            postNewImage(); // No previous image, proceed with uploading new image
        }
    }

    // Upload new image to Firebase Storage and update Database
    private void postNewImage() {
        if (UserImageUri != null && currentUser != null) {
            progressDialog1.show(); // Show progress dialog
            final String uid = currentUser.getUid();
            final StorageReference reference = storage.getReference().child("User_image").child(uid + ".jpg");
            reference.putFile(UserImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String imageUrl = uri.toString();
                            database.getReference().child("User").child(uid).child("userImage").setValue(imageUrl)
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

    private void showUploadSuccessDialog() {
        new AlertDialog.Builder(getContext())
                .setTitle("Upload Successful")
                .setMessage("Your profile picture has been successfully uploaded.")
                .setPositiveButton("OK", null)
                .show();
    }
}
