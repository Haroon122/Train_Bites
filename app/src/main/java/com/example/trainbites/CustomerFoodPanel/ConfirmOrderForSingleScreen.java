package com.example.trainbites.CustomerFoodPanel;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.trainbites.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.paymentsheet.PaymentSheetResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ConfirmOrderForSingleScreen extends AppCompatActivity {

    EditText name, phone, seat, coach;
    TextView totalAmount, dishName;
    Button nxt;
    DatabaseReference dbRefrence;
    ProgressDialog progressDialog;

    // Stripe variables
    String publishableKey = "pk_test_51Pg4coRuzQz82CpEQpG8FMy8XD6YAz9TxW9C4aaKruNz4Z02QFtgYEr5WNmMtgy1RX6Meehf3XU4qrpckBxqRavv00Mmyzd4Ax";
    String secretKey = "sk_test_51Pg4coRuzQz82CpEwInq4dxvtcQ5yesb6Dk8nhfjOKLv8EL6g8OqiGqcHypq9ZPcGaYPXUqPtXKM4CDPq4Mds7he00NjMYtrPm";
    String customerId;
    String ephemeralKey;
    String clientSecret;
    PaymentSheet paymentSheet;
    int amount;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_order_for_single_screen);

        name = findViewById(R.id.usernameid);
        phone = findViewById(R.id.userphoneid);
        seat = findViewById(R.id.userSeatid);
        coach = findViewById(R.id.userCoachid);
        nxt = findViewById(R.id.nextBtn);
        totalAmount = findViewById(R.id.totalAmountId);
        dishName = findViewById(R.id.Getdishname);
        progressDialog = new ProgressDialog(this);
        dbRefrence = FirebaseDatabase.getInstance().getReference("Single_Confirm_order");

        // Initialize Stripe
        PaymentConfiguration.init(this, publishableKey);
        paymentSheet = new PaymentSheet(this, this::onPaymentResult);

        // Show data in totalAmount TextView
        String totalAmountValue = getIntent().getStringExtra("Total Amount");
        totalAmount.setText("Total Amount: " + totalAmountValue);

        // Show data in dishName TextView
        String FetchDishName = getIntent().getStringExtra("DishName");
        dishName.setText(FetchDishName);

        // Set click listener for order confirmation and payment
        nxt.setOnClickListener(v -> ButtonFunctionality());
    }

    private void ButtonFunctionality() {
        String Uname = name.getText().toString().trim();
        String Uphone = phone.getText().toString().trim();
        String Useat = seat.getText().toString().trim();
        String Ucoach = coach.getText().toString().trim();
        String UtotalAmount = totalAmount.getText().toString().replace("Total Amount: ", "").trim();
        String Udish = dishName.getText().toString().trim();

        if (Uname.isEmpty() || Uphone.isEmpty() || Useat.isEmpty() || Ucoach.isEmpty() || UtotalAmount.isEmpty() || Udish.isEmpty()) {
            Toast.makeText(ConfirmOrderForSingleScreen.this, "All Fields are Required", Toast.LENGTH_SHORT).show();
        } else {
            progressDialog.setTitle("Processing...");
            progressDialog.setMessage("Please wait...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            // Convert amount to integer for Stripe (cents)
            amount = Integer.parseInt(UtotalAmount) * 100; // Convert to cents

            // Start Stripe payment process
            if (clientSecret == null) {
                createCustomer();
            } else {
                paymentFlow();
            }
        }
    }

    private void createCustomer() {
        StringRequest request = new StringRequest(Request.Method.POST, "https://api.stripe.com/v1/customers", response -> {
            try {
                JSONObject object = new JSONObject(response);
                customerId = object.getString("id");
                getEphemeralKey();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> Toast.makeText(this, "Error: " + error.getLocalizedMessage(), Toast.LENGTH_SHORT).show()) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> header = new HashMap<>();
                header.put("Authorization", "Bearer " + secretKey);
                return header;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }

    private void getEphemeralKey() {
        StringRequest request = new StringRequest(Request.Method.POST, "https://api.stripe.com/v1/ephemeral_keys", response -> {
            try {
                JSONObject object = new JSONObject(response);
                ephemeralKey = object.getString("id");
                getClientSecret();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> Toast.makeText(this, "Error: " + error.getLocalizedMessage(), Toast.LENGTH_SHORT).show()) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> header = new HashMap<>();
                header.put("Authorization", "Bearer " + secretKey);
                header.put("Stripe-Version", "2024-06-20");
                return header;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("customer", customerId);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }

    private void getClientSecret() {
        StringRequest request = new StringRequest(Request.Method.POST, "https://api.stripe.com/v1/payment_intents", response -> {
            try {
                JSONObject object = new JSONObject(response);
                clientSecret = object.getString("client_secret");
                paymentFlow();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> Toast.makeText(this, "Error: " + error.getLocalizedMessage(), Toast.LENGTH_SHORT).show()) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> header = new HashMap<>();
                header.put("Authorization", "Bearer " + secretKey);
                return header;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("customer", customerId);
                params.put("amount", String.valueOf(amount)); // Send the amount here
                params.put("currency", "usd");
                params.put("automatic_payment_methods[enabled]", "true");
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }

    private void paymentFlow() {
        paymentSheet.presentWithPaymentIntent(clientSecret, new PaymentSheet.Configuration("Train Bites",
                new PaymentSheet.CustomerConfiguration(customerId, ephemeralKey)));
    }

    private void onPaymentResult(PaymentSheetResult paymentSheetResult) {
        if (paymentSheetResult instanceof PaymentSheetResult.Completed) {
            placeOrder("paid");
        } else if (paymentSheetResult instanceof PaymentSheetResult.Canceled) {
            placeOrder("Unpaid");
            Toast.makeText(this, "Payment Canceled", Toast.LENGTH_SHORT).show();
        } else if (paymentSheetResult instanceof PaymentSheetResult.Failed) {
            placeOrder("Unpaid");
            Toast.makeText(this, "Payment Failed", Toast.LENGTH_LONG).show();
        }
        progressDialog.dismiss();
    }

    private void placeOrder(String paymentstatus) {
        String Uname = name.getText().toString().trim();
        String Uphone = phone.getText().toString().trim();
        String Useat = seat.getText().toString().trim();
        String Ucoach = coach.getText().toString().trim();
        String UtotalAmount = totalAmount.getText().toString().replace("Total Amount: ", "").trim();
        String Udish = dishName.getText().toString().trim();
        String SingleItemOrMultipleItem="Single Item";


        // Get current date
        String currentDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault()).format(new Date());

        // Generate a unique order number
        String uniqueOrderId = String.valueOf(System.currentTimeMillis()); // Use current timestamp for uniqueness

            String SingleOrderId = dbRefrence.push().getKey();
            if (SingleOrderId != null) {
                ConfirmOrderForSingleScreen_Model singleOrder = new ConfirmOrderForSingleScreen_Model(Uname, Uphone, Useat, Ucoach, UtotalAmount, Udish,currentDateTime, uniqueOrderId,paymentstatus,SingleItemOrMultipleItem );
                dbRefrence.child(SingleOrderId).setValue(singleOrder).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            if (paymentstatus.equals("paid")){
                                ShowConfirmDialog();
                            }else {
                            }



                            // Clear fields after successful order placement
                            name.setText("");
                            phone.setText("");
                            seat.setText("");
                            coach.setText("");
                            totalAmount.setText("Total Amount: ");
                            dishName.setText("");

                        } else {
                            Toast.makeText(ConfirmOrderForSingleScreen.this, "Failed to place order", Toast.LENGTH_SHORT).show();
                        }
                        progressDialog.dismiss();


                    }
                });
            }

    }
    private void ShowConfirmDialog(){
        Dialog dialog=new Dialog(ConfirmOrderForSingleScreen.this);
        dialog.setContentView(R.layout.custom_dialogbox_layout);

        //back to home button id
        Button BkBtn = dialog.findViewById(R.id.back_to_home_id);
        BkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //dialog.dismiss();

                Intent intent = new Intent(ConfirmOrderForSingleScreen.this,Customer_Bottom_Activity.class);
                startActivity(intent);
                finish();
            }
        });

        //move to checking order
        Button chkBtn=dialog.findViewById(R.id.check_order_id);
        chkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ConfirmOrderForSingleScreen.this,Customer_Bottom_Activity.class);
                startActivity(intent);
                finish();
            }
        });
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();

    }
}


