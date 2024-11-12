package com.example.trainbites.CustomerFoodPanel;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.example.trainbites.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class CustomerOrderFregment extends Fragment {

    RecyclerView recyclerView;
    CustomerOrderFragmentAdapter orderAdapter;
    List<Object> ordersList; // Changed to handle both models
    DatabaseReference singleOrderRef;
    DatabaseReference multipleOrderRef;
    SwipeRefreshLayout swipeRefreshLayout;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_customer_order_fregment, container, false);


        // Initialize RecyclerView and List
        recyclerView = view.findViewById(R.id.orderRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        ordersList = new ArrayList<>();

        // Firebase references for both types of orders
        singleOrderRef = FirebaseDatabase.getInstance().getReference("Single_Confirm_order");
        multipleOrderRef = FirebaseDatabase.getInstance().getReference("Multiple_Confirm_order");

        // Adapter setup
        orderAdapter = new CustomerOrderFragmentAdapter(ordersList);
        recyclerView.setAdapter(orderAdapter);

        //fetch data from firebase
        fetchOrderData();

        //swipe to refresh
        swipeRefreshLayout=view.findViewById(R.id.swiperForCustomerOrder);
        swipeRefreshLayout.setColorSchemeColors(Color.parseColor("#028352"));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchOrderData();

            }
        });

        return view;
    }

    private void fetchOrderData(){
        // Fetch data for Single_Confirm_order
        singleOrderRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ordersList.clear(); // Clear list to avoid duplication
                swipeRefreshLayout.setRefreshing(true);
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        ConfirmOrderForSingleScreen_Model orderModel = dataSnapshot.getValue(ConfirmOrderForSingleScreen_Model.class);
                        if (orderModel != null) {
                            ordersList.add(orderModel);
                        }
                    }
                    orderAdapter.notifyDataSetChanged();
                } else {
                    Log.e("DataError", "No data found in Single_Confirm_order");
                }
                swipeRefreshLayout.setRefreshing(false);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", "Database Error: " + error.getMessage());
                Toast.makeText(getContext(), "Failed to load single orders. Please try again later.", Toast.LENGTH_SHORT).show();
                swipeRefreshLayout.setRefreshing(false);

            }
        });

        // Fetch data for Multiple_Confirm_order
        multipleOrderRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                swipeRefreshLayout.setRefreshing(true);
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        MultipleConfirmOrderModel multipleOrderModel = dataSnapshot.getValue(MultipleConfirmOrderModel.class);
                        if (multipleOrderModel != null) {
                            ordersList.add(multipleOrderModel);
                        }
                    }
                    orderAdapter.notifyDataSetChanged();
                } else {
                    Log.e("DataError", "No data found in Multiple_Confirm_order");
                }
                swipeRefreshLayout.setRefreshing(false);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", "Database Error: " + error.getMessage());
                Toast.makeText(getContext(), "Failed to load multiple orders. Please try again later.", Toast.LENGTH_SHORT).show();
                swipeRefreshLayout.setRefreshing(false);

            }
        });
    }
}
