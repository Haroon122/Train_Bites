package com.example.trainbites.CustomerFoodPanel;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
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

import com.example.trainbites.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DecimalFormat;

public class ConfirmOrderForSingleScreen extends AppCompatActivity {
    EditText name,phone,seat,coach;
    TextView totalAmount,dishName;
    Button nxt;
    DatabaseReference dbRefrence;
    ProgressDialog progressDialog;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_confirm_order_for_single_screen);
        name=findViewById(R.id.usernameid);
        phone=findViewById(R.id.userphoneid);
        seat=findViewById(R.id.userSeatid);
        coach=findViewById(R.id.userCoachid);
        nxt=findViewById(R.id.nextBtn);
        totalAmount=findViewById(R.id.totalAmountId);
        dishName=findViewById(R.id.Getdishname);
        progressDialog=new ProgressDialog(this);
        dbRefrence= FirebaseDatabase.getInstance().getReference("Single_Confirm_order");

        //show data in totalamout text view box
        String totalAmountValue=getIntent().getStringExtra("Total Amount");
        totalAmount.setText("Total Amount: " + totalAmountValue);

        //show data in dishname text view box
        String FetchDishName=getIntent().getStringExtra("DishName");
        dishName.setText(FetchDishName);


        nxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ButtonFunctionality();
            }

            private void ButtonFunctionality() {

                String Uname=name.getText().toString().trim();
                String Uphone=phone.getText().toString().trim();
                String Useat=seat.getText().toString().trim();
                String Ucoach=coach.getText().toString().trim();
                String UtotalAmount=totalAmount.getText().toString().trim();
                String Udish=dishName.getText().toString().trim();


                if (Uname.isEmpty()||Uphone.isEmpty()||Useat.isEmpty()||Ucoach.isEmpty()||UtotalAmount.isEmpty()||Udish.isEmpty())
                {
                    Toast.makeText(ConfirmOrderForSingleScreen.this,"All Fields are Required",Toast.LENGTH_SHORT).show();
                    return;
                }else {
                    progressDialog.setTitle("Loading...");
                    progressDialog.setMessage("Please wait...");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();

                    String SingleOrderId=dbRefrence.push().getKey();
                    if (SingleOrderId != null){
                        ConfirmOrderForSingleScreen_Model singleOrder=new ConfirmOrderForSingleScreen_Model(Uname,Uphone,Useat,Ucoach,UtotalAmount,Udish);
                        dbRefrence.child(SingleOrderId).setValue(singleOrder).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    Toast.makeText(ConfirmOrderForSingleScreen.this, "Order placed successfully", Toast.LENGTH_SHORT).show();
                                    finish();
                                }else{
                                    Toast.makeText(ConfirmOrderForSingleScreen.this, "Failed to place order", Toast.LENGTH_SHORT).show();
                                }
                                progressDialog.dismiss();

                            }
                        });
                    }
                }

            }
        });


    }
}