package com.example.trainbites;

import android.annotation.SuppressLint;
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

public class SignUpActivityForChef extends AppCompatActivity {

    EditText usernameChef,emailChef,phoneChef,passwordChef;
    ImageButton eyeBtn;
    Button signUpChef;
    TextView AlreadyAccountChef;
    FirebaseAuth m1Auth;
    FirebaseUser m1User;
    DatabaseReference db1Refrence;
    ProgressDialog progressDialogChef;

    String emailpatternChef="[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    private boolean isPasswordVisible=false;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up_for_chef);

        usernameChef=findViewById(R.id.edit_usernameChef);
        emailChef=findViewById(R.id.edit_emailChef);
        phoneChef=findViewById(R.id.edit_phoneChef);
        passwordChef=findViewById(R.id.edit_paswordChef);
        AlreadyAccountChef=findViewById(R.id.alreadyTxtForChef);
        eyeBtn=findViewById(R.id.eyeToggleBtn);
        AlreadyAccountChef.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUpActivityForChef.this,LoginActivityForChef.class));
                finish();
            }
        });

        db1Refrence= FirebaseDatabase.getInstance().getReference();
        m1Auth=FirebaseAuth.getInstance();
        m1User=m1Auth.getCurrentUser();
        progressDialogChef=new ProgressDialog(this);
        //Button working
        signUpChef=findViewById(R.id.signupBTnChef);



        //eye toggle button working
        eyeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPasswordVisible){
                    passwordChef.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    eyeBtn.setImageResource(R.drawable.hide);
                }else {
                    passwordChef.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    eyeBtn.setImageResource(R.drawable.show);
                }
                isPasswordVisible =! isPasswordVisible;
                passwordChef.setSelection(passwordChef.length());
            }
        });
        signUpChef.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performAuthChef();
            }

            private void performAuthChef() {
                String UCname=usernameChef.getText().toString();
                String uCemail=emailChef.getText().toString();
                String uCphone=phoneChef.getText().toString();
                String uCpassword=passwordChef.getText().toString();

                if (!uCemail.matches(emailpatternChef)){
                    emailChef.setError("Enter Correct Email");
                    emailChef.requestFocus();
                } else if (uCpassword.isEmpty() || passwordChef.length()<6) {
                    passwordChef.setError("Enter proper password");
                    passwordChef.requestFocus();
                } else if (UCname.isEmpty() || uCphone.isEmpty()) {
                    Toast.makeText(SignUpActivityForChef.this, "Required All Fields", Toast.LENGTH_SHORT).show();
                }else {
                    progressDialogChef.setMessage("Please wait While Registration...");
                    progressDialogChef.setTitle("Registration");
                    progressDialogChef.setCanceledOnTouchOutside(false);
                    progressDialogChef.show();

                    m1Auth.createUserWithEmailAndPassword(uCemail,uCpassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()){
                                FirebaseUser user=m1Auth.getCurrentUser();
                                if (user!=null){
                                    user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                Toast.makeText(SignUpActivityForChef.this,"Verification email sent, please verify your email",Toast.LENGTH_SHORT).show();
                                            }else {
                                                Toast.makeText(SignUpActivityForChef.this,"Failed to send verification email",Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                                //for store data in firebase database
                                // Inside onComplete() method after registration is successful
                                String userId = m1Auth.getCurrentUser().getUid();
                                Chef userData = new Chef(UCname,uCemail,uCphone,uCpassword);
                                db1Refrence.child("Chef").child(userId).setValue(userData).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            // User information stored successfully
                                        } else {
                                            // Failed to store user information
                                        }
                                    }
                                });
                                progressDialogChef.dismiss();
                                Toast.makeText(SignUpActivityForChef.this,"Successful",Toast.LENGTH_SHORT).show();
                                Intent intent=new Intent(SignUpActivityForChef.this,LoginActivityForChef.class);
                                startActivity(intent);
                                finish();
                            }else {
                                progressDialogChef.dismiss();
                                Toast.makeText(SignUpActivityForChef.this, ""+task.getException(), Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                }


            }
        });

    }
}