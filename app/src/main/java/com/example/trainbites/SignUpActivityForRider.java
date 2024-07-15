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

public class SignUpActivityForRider extends AppCompatActivity {

    EditText usernameRider,emailRider,phoneRider,passwordRider;
    ImageButton eyeBtn;
    Button signUpChef;
    TextView AlreadyAccountRider;
    FirebaseAuth m2Auth;
    FirebaseUser m2User;
    DatabaseReference db2Refrence;
    ProgressDialog progressDialogRider;

    String emailpatternRider="[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    private boolean isPasswordVisible=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up_for_rider);

        usernameRider=findViewById(R.id.edit_usernameRider);
        emailRider=findViewById(R.id.edit_emailRider);
        phoneRider=findViewById(R.id.edit_phoneRider);
        passwordRider=findViewById(R.id.edit_paswordRider);
        eyeBtn=findViewById(R.id.eyeToggleBtn);
        AlreadyAccountRider=findViewById(R.id.alreadyTxtForRider);
        AlreadyAccountRider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUpActivityForRider.this,LoginActivityForRider.class));
                finish();
            }
        });

        db2Refrence= FirebaseDatabase.getInstance().getReference();
        m2Auth=FirebaseAuth.getInstance();
        m2User=m2Auth.getCurrentUser();
        progressDialogRider=new ProgressDialog(this);
        //Button working
        signUpChef=findViewById(R.id.signupBTnRider);


        //eye toggle button working
        eyeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPasswordVisible){
                    passwordRider.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    eyeBtn.setImageResource(R.drawable.hide);
                }else {
                    passwordRider.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    eyeBtn.setImageResource(R.drawable.show);
                }
                isPasswordVisible =! isPasswordVisible;
                passwordRider.setSelection(passwordRider.length());
            }
        });
        signUpChef.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performAuthChef();
            }

            private void performAuthChef() {
                String UCname=usernameRider.getText().toString();
                String uCemail=emailRider.getText().toString();
                String uCphone=phoneRider.getText().toString();
                String uCpassword=passwordRider.getText().toString();

                if (!uCemail.matches(emailpatternRider)){
                    emailRider.setError("Enter Correct Email");
                    emailRider.requestFocus();
                } else if (uCpassword.isEmpty() || passwordRider.length()<6) {
                    passwordRider.setError("Enter proper password");
                    emailRider.requestFocus();
                } else if (UCname.isEmpty() || uCphone.isEmpty()) {
                    Toast.makeText(SignUpActivityForRider.this, "Required All Fields", Toast.LENGTH_SHORT).show();
                }else {
                    progressDialogRider.setMessage("Please wait While Registration...");
                    progressDialogRider.setTitle("Registration");
                    progressDialogRider.setCanceledOnTouchOutside(false);
                    progressDialogRider.show();

                    m2Auth.createUserWithEmailAndPassword(uCemail,uCpassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()){
                                FirebaseUser user=m2Auth.getCurrentUser();
                                if (user!=null){
                                    user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                Toast.makeText(SignUpActivityForRider.this,"Verification email sent, please verify your email",Toast.LENGTH_SHORT).show();
                                            }else {
                                                Toast.makeText(SignUpActivityForRider.this,"Failed to send verification email",Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                                //for store data in firebase database
                                // Inside onComplete() method after registration is successful
                                String userId = m2Auth.getCurrentUser().getUid();
                                Rider userData = new Rider (UCname,uCemail,uCphone,uCpassword);
                                db2Refrence.child("Rider").child(userId).setValue(userData).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            // User information stored successfully
                                        } else {
                                            // Failed to store user information
                                        }
                                    }
                                });
                                progressDialogRider.dismiss();
                                Toast.makeText(SignUpActivityForRider.this,"Successful",Toast.LENGTH_SHORT).show();
                                Intent intent=new Intent(SignUpActivityForRider.this,LoginActivityForRider.class);
                                startActivity(intent);
                                finish();
                            }else {
                                progressDialogRider.dismiss();
                                Toast.makeText(SignUpActivityForRider.this, ""+task.getException(), Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                }


            }
        });

    }
}