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

public class LoginActivityForRider extends AppCompatActivity {

    Button signinForRider,loginBUTTONRider;
    EditText emailRider,passwordRider;
    String emailpatternRider="[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    CheckBox cBoxRider;
    TextView forgetTxtRider;
    FirebaseAuth m2Auth;
    FirebaseUser m2User;
    ProgressDialog progressDialogRider;
    SharedPreferences spRider;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_for_rider);

            //Initilazation
        cBoxRider=findViewById(R.id.checkboxIDForRider);
        forgetTxtRider=findViewById(R.id.forgetIdForRider);
        emailRider=findViewById(R.id.enter_emailRider);
        passwordRider=findViewById(R.id.enter_paswordRider);
        loginBUTTONRider=findViewById(R.id.loginBTnRider);
        m2Auth=FirebaseAuth.getInstance();
        m2User=m2Auth.getCurrentUser();
        progressDialogRider=new ProgressDialog(this);

        signinForRider=findViewById(R.id.signupBTnRider);
        signinForRider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(LoginActivityForRider.this, SignUpActivityForRider.class);
                startActivity(intent);
                finish();
            }
        });
        // Initialize SharedPreferences
        spRider=getSharedPreferences("loginPrefsRider",MODE_PRIVATE);
        String savedEmailRider=spRider.getString("email","");
        String savedPasswordRider=spRider.getString("password","");
        emailRider.setText(savedEmailRider);
        passwordRider.setText(savedPasswordRider);

        loginBUTTONRider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                perofmAuth();
            }

            private void perofmAuth() {

                String LRmail=emailRider.getText().toString();
                String LRpass=passwordRider.getText().toString();

                if (!LRmail.matches(emailpatternRider)){
                    emailRider.setError("Enter Correct Email");
                    emailRider.requestFocus();
                } else if (LRpass.isEmpty() || passwordRider.length()<6) {
                    passwordRider.setError("Enter proper password");
                    passwordRider.requestFocus();
                }else{
                    progressDialogRider.setMessage("Please wait While Login...");
                    progressDialogRider.setTitle("Login");
                    progressDialogRider.setCanceledOnTouchOutside(false);
                    progressDialogRider.show();

                    m2Auth.signInWithEmailAndPassword(LRmail,LRpass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressDialogRider.dismiss();
                            if (task.isSuccessful()) {
                                if (FirebaseAuth.getInstance().getCurrentUser().isEmailVerified()){
                                    Toast.makeText(LoginActivityForRider.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(LoginActivityForRider.this, chooseRiderActivity.class);
                                    startActivity(intent);
                                    finish();
                                }else {
                                    Toast.makeText(LoginActivityForRider.this, "please Verify Email", Toast.LENGTH_SHORT).show();

                                }
                            }else {
                                progressDialogRider.dismiss();
                                Toast.makeText(LoginActivityForRider.this,""+task.getException(),Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
                    // Save email and password if checkbox is checked
                    if (cBoxRider.isChecked()) {
                        SharedPreferences.Editor editor = spRider.edit();
                        editor.putString("email", LRmail);
                        editor.putString("password", LRpass);
                        editor.apply();
                    } else {
                        // Clear saved email and password
                        SharedPreferences.Editor editor = spRider.edit();
                        editor.remove("email");
                        editor.remove("password");
                        editor.apply();
                    }

                }





            }
        });

        forgetTxtRider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Lemail=emailRider.getText().toString().trim();
                if (TextUtils.isEmpty(Lemail)){
                    emailRider.setError("Enter your registered email");
                    emailRider.requestFocus();
                }else {
                    progressDialogRider.setMessage("Sending reset password email...");
                    progressDialogRider.show();
                    m2Auth.sendPasswordResetEmail(Lemail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            progressDialogRider.dismiss();
                            if (task.isSuccessful()) {
                                Toast.makeText(LoginActivityForRider.this, "Reset password email sent, check your inbox", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(LoginActivityForRider.this, "Failed to send reset password email. Please try again later.", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                }


            }
        });






    }
}