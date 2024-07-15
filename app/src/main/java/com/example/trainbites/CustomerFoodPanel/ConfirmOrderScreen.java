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
import java.util.ArrayList;

public class ConfirmOrderScreen extends AppCompatActivity {
    EditText name,phone,seat,coach;
    TextView totalAmount,dishname;
    Button nxt;
    private ArrayList<CartItemModel> cartItems;

    DatabaseReference dbRefrence;
    ProgressDialog progressDialog;


    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_confirm_order_screen);

        name=findViewById(R.id.usernameid);
        phone=findViewById(R.id.userphoneid);
        seat=findViewById(R.id.userSeatid);
        coach=findViewById(R.id.userCoachid);
        nxt=findViewById(R.id.nextBtn);
        totalAmount=findViewById(R.id.totalAmountId);
        dishname=findViewById(R.id.multipleCartItems);
        progressDialog=new ProgressDialog(this);

        dbRefrence=FirebaseDatabase.getInstance().getReference("Multiple_Confirm_order");


        double totalAmountValue=getIntent().getDoubleExtra("Total Amount",0.0);
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        totalAmount.setText("Total Amount: " + decimalFormat.format(totalAmountValue));

        //fetch multiple items for cart activity and show in this ativity
        cartItems = getIntent().<CartItemModel>getParcelableArrayListExtra("cartItems");
        StringBuilder orderDetails = new StringBuilder();
        for (CartItemModel item : cartItems) {
            orderDetails.append("Dish: ").append(item.getDishName())
                    .append("\nQuantity: ").append(item.getQuantity())
                    .append("\nPrice: ").append(item.getPrice())
                    .append("\n\n");  // Adding new line for better readability
        }
        dishname.setText(orderDetails.toString());

        //Confirm order button implementatoin
        nxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConfirmOrderWorking();
            }

            private void ConfirmOrderWorking() {
                //data store in realtime database
                String Uname=name.getText().toString().trim();
                String Uphone=phone.getText().toString().trim();
                String Useat=seat.getText().toString().trim();
                String Ucoach=coach.getText().toString().trim();
                String Utotal=totalAmount.getText().toString().trim();
                String Udish=dishname.getText().toString().trim();

                if (Uname.isEmpty()||Uphone.isEmpty()||Useat.isEmpty()||Ucoach.isEmpty()||Utotal.isEmpty()||Udish.isEmpty()){
                    Toast.makeText(ConfirmOrderScreen.this, "All Fields are Required", Toast.LENGTH_SHORT).show();
                    return;
                }else {
                    progressDialog.setTitle("Loading");
                    progressDialog.setMessage("Please Wait...");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();


                    String UserId=dbRefrence.push().getKey();
                    MultipleConfirmOrderModel multipleOrders=new MultipleConfirmOrderModel(Uname,Uphone,Useat,Ucoach,Udish,Utotal);
                    dbRefrence.child(UserId).setValue(multipleOrders).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(ConfirmOrderScreen.this, "Order placed successfully", Toast.LENGTH_SHORT).show();
                                finish();
                            }else {
                                Toast.makeText(ConfirmOrderScreen.this, "Failed to place order", Toast.LENGTH_SHORT).show();
                            }
                            progressDialog.dismiss();
                        }
                    });

                }

            }
        });

    }
}