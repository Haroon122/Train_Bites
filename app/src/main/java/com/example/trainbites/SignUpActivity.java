package com.example.trainbites;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class SignUpActivity extends AppCompatActivity {

    EditText username,email,phone,password;
    Button signUp;
    TextView AlreadyAccount;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    DatabaseReference dbRefrence;
    ProgressDialog progressDialog;

    String emailpattern="[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);

        username=findViewById(R.id.edit_usernameUser);
        email=findViewById(R.id.edit_emailUser);
        phone=findViewById(R.id.edit_phoneUser);
        password=findViewById(R.id.edit_paswordUser);
        AlreadyAccount=findViewById(R.id.alreadyTxtForUser);
        AlreadyAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUpActivity.this,LoginActivity.class));
                finish();
            }
        });

        //Initilization
        dbRefrence=FirebaseDatabase.getInstance().getReference();
        mAuth=FirebaseAuth.getInstance();
        mUser=mAuth.getCurrentUser();
        progressDialog=new ProgressDialog(this);
        //Button working
        signUp=findViewById(R.id.signupBTnUser);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performAuth();

            }
            private void performAuth() {
                String Uname=username.getText().toString();
                String uemail=email.getText().toString();
                String uphone=phone.getText().toString();
                String upassword=password.getText().toString();

                if (!uemail.matches(emailpattern)){
                    email.setError("Enter Correct Email");
                    email.requestFocus();
                } else if (upassword.isEmpty() || password.length()<6) {
                    password.setError("Enter proper password");
                    password.requestFocus();
                } else if (Uname.isEmpty() || uphone.isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "Required All Fields", Toast.LENGTH_SHORT).show();
                }else {
                    progressDialog.setMessage("Please wait While Registration...");
                    progressDialog.setTitle("Registration");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();

                    mAuth.createUserWithEmailAndPassword(uemail,upassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                FirebaseUser user=mAuth.getCurrentUser();
                                if (user!=null){
                                    user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                Toast.makeText(SignUpActivity.this,"Verification email sent, please verify your email",Toast.LENGTH_SHORT).show();
                                            }else {
                                                Toast.makeText(SignUpActivity.this,"Failed to send verification email",Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                                //for store data in firebase database
                                // Inside onComplete() method after registration is successful
                                String userId = mAuth.getCurrentUser().getUid();
                                User userData = new User(Uname,uemail,uphone);
                                dbRefrence.child("User").child(userId).setValue(userData).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            // User information stored successfully
                                        } else {
                                            // Failed to store user information
                                        }
                                    }
                                });
                                progressDialog.dismiss();
                                Toast.makeText(SignUpActivity.this,"Successful",Toast.LENGTH_SHORT).show();
                                Intent intent=new Intent(SignUpActivity.this,LoginActivity.class);
                                startActivity(intent);
                                finish();
                            }else {
                                progressDialog.dismiss();
                                Toast.makeText(SignUpActivity.this, ""+task.getException(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                 }
            }
        });
    }
}

