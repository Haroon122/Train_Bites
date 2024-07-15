package com.example.trainbites;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {

    EditText username, email, phone, password;
    ImageButton eyeBtn;
    Button signUp;
    TextView AlreadyAccount;
    FirebaseAuth mAuth;
    DatabaseReference dbReference;
    ProgressDialog progressDialog;

    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    private boolean isPasswordVisible=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        username = findViewById(R.id.edit_usernameUser);
        email = findViewById(R.id.edit_emailUser);
        phone = findViewById(R.id.edit_phoneUser);
        password = findViewById(R.id.edit_paswordUser);
        AlreadyAccount = findViewById(R.id.alreadyTxtForUser);
        eyeBtn=findViewById(R.id.eyeToggleBtn);

        mAuth = FirebaseAuth.getInstance();
        dbReference = FirebaseDatabase.getInstance().getReference("User");
        progressDialog = new ProgressDialog(this);

        AlreadyAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                finish();
            }
        });


        //eye toggle button working
        eyeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPasswordVisible){
                    password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    eyeBtn.setImageResource(R.drawable.hide);
                }else {
                    password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    eyeBtn.setImageResource(R.drawable.show);
                }
                isPasswordVisible =! isPasswordVisible;
                password.setSelection(password.length());
            }
        });

        signUp = findViewById(R.id.signupBTnUser);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performAuth();
            }

            private void performAuth() {
                String Uname = username.getText().toString();
                String uemail = email.getText().toString();
                String uphone = phone.getText().toString();
                String upassword = password.getText().toString();

                if (!uemail.matches(emailPattern)) {
                    email.setError("Enter Correct Email");
                    email.requestFocus();
                } else if (upassword.isEmpty() || upassword.length() < 6) {
                    password.setError("Enter proper password");
                    password.requestFocus();
                } else if (Uname.isEmpty() || uphone.isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "All Fields are Required", Toast.LENGTH_SHORT).show();
                } else {
                    progressDialog.setMessage("Please wait while Registration...");
                    progressDialog.setTitle("Registration");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();

                    mAuth.createUserWithEmailAndPassword(uemail, upassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                if (user != null) {
                                    user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(SignUpActivity.this, "Verification email sent, please verify your email", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(SignUpActivity.this, "Failed to send verification email", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                                // Store data in Firebase Database
                                String userId = user.getUid();
                                User userData = new User(userId, Uname, uemail, uphone, upassword, "");
                                dbReference.child(userId).setValue(userData).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            progressDialog.dismiss();
                                            Toast.makeText(SignUpActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            progressDialog.dismiss();
                                            Toast.makeText(SignUpActivity.this, "Failed to store user data", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(SignUpActivity.this, "Registration Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
}
