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
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.trainbites.MainActivity;
import com.example.trainbites.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.paymentsheet.PaymentSheetResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ConfirmOrderScreen extends AppCompatActivity {
    private EditText name, phone, seat, coach;
    private TextView totalAmount, dishname;
    private Button nxt;
    private ArrayList<CartItemModel> cartItems;

    private DatabaseReference dbReference;
    private ProgressDialog progressDialog;

    // Stripe variables
    private String publishableKey = "pk_test_51Pg4coRuzQz82CpEQpG8FMy8XD6YAz9TxW9C4aaKruNz4Z02QFtgYEr5WNmMtgy1RX6Meehf3XU4qrpckBxqRavv00Mmyzd4Ax";
    private String secretKey = "sk_test_51Pg4coRuzQz82CpEwInq4dxvtcQ5yesb6Dk8nhfjOKLv8EL6g8OqiGqcHypq9ZPcGaYPXUqPtXKM4CDPq4Mds7he00NjMYtrPm";
    private String customerId;
    private String ephemeralKey;
    private String clientSecret;
    private PaymentSheet paymentSheet;
    private int amount;

    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_order_screen);


        // Initialize views
        name = findViewById(R.id.usernameid);
        phone = findViewById(R.id.userphoneid);
        seat = findViewById(R.id.userSeatid);
        coach = findViewById(R.id.userCoachid);
        nxt = findViewById(R.id.nextBtn);
        totalAmount = findViewById(R.id.totalAmountId);
        dishname = findViewById(R.id.multipleCartItems);
        progressDialog = new ProgressDialog(this);

        dbReference = FirebaseDatabase.getInstance().getReference("Multiple_Confirm_order");

        // Get total amount and cart items
        double totalAmountValue = getIntent().getDoubleExtra("Total Amount", 0.0);
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        totalAmount.setText("Total Amount: " + decimalFormat.format(totalAmountValue));

        cartItems = getIntent().getParcelableArrayListExtra("cartItems");
        StringBuilder orderDetails = new StringBuilder();
        for (CartItemModel item : cartItems) {
            orderDetails.append("Dish: ").append(item.getDishName())
                    .append("\nQuantity: ").append(item.getQuantity())
                    .append("\nPrice: ").append(item.getPrice())
                    .append("\n\n");
        }
        dishname.setText(orderDetails.toString());

        // Initialize Stripe
        PaymentConfiguration.init(this, publishableKey);
        paymentSheet = new PaymentSheet(this, this::onPaymentResult);

        // Confirm order button
        nxt.setOnClickListener(v -> confirmOrderWorking());
    }

    private void confirmOrderWorking() {
        // Data validation
        String Uname = name.getText().toString().trim();
        String Uphone = phone.getText().toString().trim();
        String Useat = seat.getText().toString().trim();
        String Ucoach = coach.getText().toString().trim();
        String Utotal = totalAmount.getText().toString().trim();
        String Udish = dishname.getText().toString().trim();

        if (Uname.isEmpty() || Uphone.isEmpty() || Useat.isEmpty() || Ucoach.isEmpty() || Utotal.isEmpty() || Udish.isEmpty()) {
            Toast.makeText(ConfirmOrderScreen.this, "All Fields are Required", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.setTitle("Processing...");
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        try {
            // Parse amount for Stripe (convert to cents)
            amount = (int) (Double.parseDouble(Utotal.replace("Total Amount: ", "").trim()) * 100); // Convert to cents
        } catch (NumberFormatException e) {
            progressDialog.dismiss();
            Toast.makeText(this, "Invalid total amount", Toast.LENGTH_SHORT).show();
            return;
        }

        if (clientSecret == null) {
            createCustomer();
        } else {
            paymentFlow();
        }
    }

    private void createCustomer() {
        StringRequest request = new StringRequest(Request.Method.POST, "https://api.stripe.com/v1/customers",
                response -> {
                    try {
                        JSONObject object = new JSONObject(response);
                        customerId = object.getString("id");
                        getEphemeralKey();
                    } catch (JSONException e) {
                        handleStripeError(e.getMessage());
                    }
                },
                error -> handleStripeError(error.getLocalizedMessage())) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> header = new HashMap<>();
                header.put("Authorization", "Bearer " + secretKey);
                return header;
            }
        };
        Volley.newRequestQueue(this).add(request);
    }

    private void getEphemeralKey() {
        StringRequest request = new StringRequest(Request.Method.POST, "https://api.stripe.com/v1/ephemeral_keys",
                response -> {
                    try {
                        JSONObject object = new JSONObject(response);
                        ephemeralKey = object.getString("id");
                        getClientSecret();
                    } catch (JSONException e) {
                        handleStripeError(e.getMessage());
                    }
                },
                error -> handleStripeError(error.getLocalizedMessage())) {
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
        Volley.newRequestQueue(this).add(request);
    }

    private void getClientSecret() {
        StringRequest request = new StringRequest(Request.Method.POST, "https://api.stripe.com/v1/payment_intents",
                response -> {
                    try {
                        JSONObject object = new JSONObject(response);
                        clientSecret = object.getString("client_secret");
                        paymentFlow();
                    } catch (JSONException e) {
                        handleStripeError(e.getMessage());
                    }
                },
                error -> handleStripeError(error.getLocalizedMessage())) {
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
        Volley.newRequestQueue(this).add(request);
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
        String Utotal = totalAmount.getText().toString().trim();
        String Udish = dishname.getText().toString().trim();
        String SingleItemOrMultipleItem="Multiple Items";

        // Get current date
        String currentDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault()).format(new Date());

        // Generate a unique order number
        String uniqueOrderId = String.valueOf(System.currentTimeMillis()); // Use current timestamp for uniqueness


        String MultipleOrderId = dbReference.push().getKey();
        if (MultipleOrderId != null) {
            MultipleConfirmOrderModel singleOrder = new MultipleConfirmOrderModel(Uname, Uphone, Useat, Ucoach, Utotal, Udish, currentDateTime, uniqueOrderId,SingleItemOrMultipleItem,paymentstatus);
            dbReference.child(MultipleOrderId).setValue(singleOrder).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        if (paymentstatus.equals("paid")){
                            confirmDialogBox();
                        }else {
                        }

                        // Clear fields after successful order placement
                        name.setText("");
                        phone.setText("");
                        seat.setText("");
                        coach.setText("");
                        totalAmount.setText("Total Amount: ");
                        dishname.setText("");

                    } else {
                        Toast.makeText(ConfirmOrderScreen.this, "Failed to place order", Toast.LENGTH_SHORT).show();
                    }
                    progressDialog.dismiss();

                }
            });
        }
    }

    private void handleStripeError(String message) {
        progressDialog.dismiss();
        Toast.makeText(this, "Stripe Error: " + message, Toast.LENGTH_SHORT).show();
    }

    private void confirmDialogBox(){
        Dialog dialog=new Dialog(ConfirmOrderScreen.this);
        dialog.setContentView(R.layout.custom_dialogbox_layout);

        //back to home button id
        Button BkBtn = dialog.findViewById(R.id.back_to_home_id);
        BkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();

            }
        });
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();
    }
}
