package com.example.trainbites;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
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

public class LoginActivity extends AppCompatActivity {
    Button signUpForUser,loginBtn;
    EditText email,password;
    String emailpattern="[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    CheckBox cBox;
    TextView forgetTxt;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    ProgressDialog progressDialog;
    SharedPreferences sp;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        //Initilazation
        cBox=findViewById(R.id.checkboxIDForUser);
        forgetTxt=findViewById(R.id.forgetIdForUser);
        email=findViewById(R.id.enter_email);
        password=findViewById(R.id.enter_pasword);
        loginBtn=findViewById(R.id.loginBTn);
        signUpForUser=findViewById(R.id.signupBTnUser);
        mAuth=FirebaseAuth.getInstance();
        mUser=mAuth.getCurrentUser();
        progressDialog=new ProgressDialog(this);

        //signup Button
        signUpForUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(LoginActivity.this,SignUpActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Initialize SharedPreferences
        sp=getSharedPreferences("loginPrefs",MODE_PRIVATE);
        String savedEmail=sp.getString("email","");
        String savedPassword=sp.getString("password","");
        email.setText(savedEmail);
        password.setText(savedPassword);

        //Login Button Working
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performAuth();
            }
            private void performAuth() {
                String Lmail=email.getText().toString();
                String Lpass=password.getText().toString();

                if (!Lmail.matches(emailpattern)){
                    email.setError("Enter Correct Email");
                    email.requestFocus();
                } else if (Lpass.isEmpty() || password.length()<6) {
                    password.setError("Enter proper password");
                    password.requestFocus();
                }else {
                    progressDialog.setMessage("Please wait While Login...");
                    progressDialog.setTitle("Login");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();

                    mAuth.signInWithEmailAndPassword(Lmail,Lpass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressDialog.dismiss();
                            if (task.isSuccessful()) {
                                if (FirebaseAuth.getInstance().getCurrentUser().isEmailVerified()){
                                    Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(LoginActivity.this, chooseUserActivity.class);
                                    startActivity(intent);
                                    finish();
                                }else {
                                    Toast.makeText(LoginActivity.this, "please Verify Email", Toast.LENGTH_SHORT).show();

                                }
                            }else {
                                progressDialog.dismiss();
                                Toast.makeText(LoginActivity.this,""+task.getException(),Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
                    // Save email and password if checkbox is checked
                    if (cBox.isChecked()) {
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("email", Lmail);
                        editor.putString("password", Lpass);
                        editor.apply();
                    } else {
                        // Clear saved email and password
                        SharedPreferences.Editor editor = sp.edit();
                        editor.remove("email");
                        editor.remove("password");
                        editor.apply();
                    }

                }


            }
        });
        forgetTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Lemail=email.getText().toString().trim();
                if (TextUtils.isEmpty(Lemail)){
                    email.setError("Enter your registered email");
                    email.requestFocus();
                }else {
                    progressDialog.setMessage("Sending reset password email...");
                    progressDialog.show();
                    mAuth.sendPasswordResetEmail(Lemail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            progressDialog.dismiss();
                            if (task.isSuccessful()) {
                                Toast.makeText(LoginActivity.this, "Reset password email sent, check your inbox", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(LoginActivity.this, "Failed to send reset password email. Please try again later.", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                }


            }
        });




    }

}