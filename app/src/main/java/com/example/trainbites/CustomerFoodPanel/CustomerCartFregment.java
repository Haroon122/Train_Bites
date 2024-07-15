package com.example.trainbites.CustomerFoodPanel;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.trainbites.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class CustomerCartFregment extends Fragment implements CartAdapter.CartAdapterListener {

    private RecyclerView recyclerView;
    private Button confirmorderbtn;
    private ArrayList<CartItemModel> recycleList;
    private FirebaseDatabase firebaseDatabase;
    private CartAdapter recyclerAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView grandTotal;
    private static final String TAG = "CustomerCartFragment";

    @SuppressLint({"WrongViewCast", "MissingInflatedId"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_customer_cart_fregment, container, false);

        confirmorderbtn=view.findViewById(R.id.confirmorderbtn);
        confirmorderbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double totalAmount=calculateGrandTotal();
                Intent intent=new Intent(getActivity(),ConfirmOrderScreen.class);
                //this intent for move multiple cart items to next activity
                intent.putParcelableArrayListExtra("cartItems", recycleList);
                //this intent for move single amount move in next activity
                intent.putExtra("Total Amount",totalAmount);
                startActivity(intent);
            }


        });


        recyclerView = view.findViewById(R.id.CartrecyclerView);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        grandTotal = view.findViewById(R.id.grandtotal);
        recycleList = new ArrayList<>();
        firebaseDatabase = FirebaseDatabase.getInstance();

        recyclerAdapter = new CartAdapter(recycleList, requireContext(), this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(recyclerAdapter);

        fetchCartItems();
        updateGrandTotal();

        swipeRefreshLayout.setColorSchemeColors(Color.parseColor("#028352"));
        swipeRefreshLayout.setOnRefreshListener(this::fetchCartItems);

        return view;
    }

    private void fetchCartItems() {
        swipeRefreshLayout.setRefreshing(true);
        firebaseDatabase.getReference().child("Cart_item").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                recycleList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    CartItemModel cartItemModel = dataSnapshot.getValue(CartItemModel.class);
                    if (cartItemModel != null) {
                        recycleList.add(cartItemModel);
                    } else {
                        Log.e(TAG, "CartItemModel is null for dataSnapshot: " + dataSnapshot.getKey());
                    }
                }
                recyclerAdapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
                updateGrandTotal();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Database error: " + error.getMessage());
                Toast.makeText(requireContext(), "Failed to retrieve data from Firebase", Toast.LENGTH_SHORT).show();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void onCartUpdated() {
        updateGrandTotal();
    }

    private void updateGrandTotal() {
        double total = 0;
        for (CartItemModel item : recycleList) {
            total += item.getPriceAsDouble() * item.getQuantity();
        }

        DecimalFormat decimalFormat = new DecimalFormat("0.##");
        grandTotal.setText(decimalFormat.format(total));
    }


    //this show in next confirm order screen activity
    private double calculateGrandTotal() {
        double total = 0;
        for (CartItemModel item : recycleList) {
            total += item.getPriceAsDouble() * item.getQuantity();
        }
        return total;
    }

}
