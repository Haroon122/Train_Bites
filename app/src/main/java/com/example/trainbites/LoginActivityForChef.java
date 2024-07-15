package com.example.trainbites;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.trainbites.ChefFoodPanel.Chef_Bottom_Activity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivityForChef extends AppCompatActivity {

    Button siginForChef,loginBUTTONchef;
    ImageButton eyeBtn;
    EditText emailchef,passwordchef;
    String emailpatternchef="[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    CheckBox cBoxChef;
    TextView forgetTxtChef;
    FirebaseAuth m1Auth;
    FirebaseUser m1User;
    ProgressDialog progressDialogChef;
    SharedPreferences spChef;
    private boolean isPasswordVisible=false;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_for_chef);

        //Initilazation
        cBoxChef=findViewById(R.id.checkboxIDForChef);
        forgetTxtChef=findViewById(R.id.forgetIdForChef);
        emailchef=findViewById(R.id.enter_emailChef);
        passwordchef=findViewById(R.id.enter_paswordChef);
        loginBUTTONchef=findViewById(R.id.loginBTnChef);
        eyeBtn=findViewById(R.id.eyeToggleBtn);
        m1Auth=FirebaseAuth.getInstance();
        m1User=m1Auth.getCurrentUser();
        progressDialogChef=new ProgressDialog(this);


        siginForChef=findViewById(R.id.signupBTnChef);
        siginForChef.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(LoginActivityForChef.this,SignUpActivityForChef.class);
                startActivity(intent);
                finish();
            }
        });

        //eye toggle button working
        eyeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPasswordVisible){
                    passwordchef.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    eyeBtn.setImageResource(R.drawable.hide);// Set to closed eye icon
                }else {
                    passwordchef.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    eyeBtn.setImageResource(R.drawable.show);// Set to open eye icon
                }
                 isPasswordVisible =! isPasswordVisible;
                // Move cursor to the end of the text
                passwordchef.setSelection(passwordchef.length());
            }

        });


        // Initialize SharedPreferences
        spChef=getSharedPreferences("loginPrefsChef",MODE_PRIVATE);
        String savedEmailChef=spChef.getString("email","");
        String savedPasswordChef=spChef.getString("password","");
        emailchef.setText(savedEmailChef);
        passwordchef.setText(savedPasswordChef);

        loginBUTTONchef.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performAuth();
            }

            private void performAuth() {
                String LCmail=emailchef.getText().toString();
                String LCpass=passwordchef.getText().toString();

                if (!LCmail.matches(emailpatternchef)){
                    emailchef.setError("Enter Correct Email");
                    emailchef.requestFocus();
                } else if (LCpass.isEmpty() || passwordchef.length()<6) {
                    passwordchef.setError("Enter proper password");
                    passwordchef.requestFocus();
                }else{
                    progressDialogChef.setMessage("Please wait While Login...");
                    progressDialogChef.setTitle("Login");
                    progressDialogChef.setCanceledOnTouchOutside(false);
                    progressDialogChef.show();

                    m1Auth.signInWithEmailAndPassword(LCmail,LCpass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressDialogChef.dismiss();
                            if (task.isSuccessful()) {
                                if (FirebaseAuth.getInstance().getCurrentUser().isEmailVerified()){
                                    Toast.makeText(LoginActivityForChef.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(LoginActivityForChef.this, Chef_Bottom_Activity.class);
                                    startActivity(intent);
                                    finish();
                                }else {
                                    Toast.makeText(LoginActivityForChef.this, "please Verify Email", Toast.LENGTH_SHORT).show();

                                }
                            }else {
                                progressDialogChef.dismiss();
                                Toast.makeText(LoginActivityForChef.this,""+task.getException(),Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
                    // Save email and password if checkbox is checked
                    if (cBoxChef.isChecked()) {
                        SharedPreferences.Editor editor = spChef.edit();
                        editor.putString("email", LCmail);
                        editor.putString("password", LCpass);
                        editor.apply();
                    } else {
                        // Clear saved email and password
                        SharedPreferences.Editor editor = spChef.edit();
                        editor.remove("email");
                        editor.remove("password");
                        editor.apply();
                    }

                }



            }
        });
        forgetTxtChef.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Lemail=emailchef.getText().toString().trim();
                if (TextUtils.isEmpty(Lemail)){
                    emailchef.setError("Enter your registered email");
                    emailchef.requestFocus();
                }else {
                    progressDialogChef.setMessage("Sending reset password email...");
                    progressDialogChef.show();
                    m1Auth.sendPasswordResetEmail(Lemail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            progressDialogChef.dismiss();
                            if (task.isSuccessful()) {
                                Toast.makeText(LoginActivityForChef.this, "Reset password email sent, check your inbox", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(LoginActivityForChef.this, "Failed to send reset password email. Please try again later.", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                }


            }
        });


    }
}