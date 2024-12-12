package com.example.trainbites.ChefFoodPanel;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
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
import java.util.Map;

public class chef_pending_orders_fegment extends Fragment {

    RecyclerView recyclerView;
    ChefPendingOrderFragmentAdapter pendingOrderAdapter;
    ArrayList<Object> pendingOrderList;
    DatabaseReference singleItemDB, multiItemDB;

    SwipeRefreshLayout swipeRefreshLayout;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chef_pending_orders_fegment, container, false);

        // Initialization
        recyclerView = view.findViewById(R.id.PendingOrderRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        pendingOrderList = new ArrayList<>();

        swipeRefreshLayout = view.findViewById(R.id.swiperForPendingOrder);
        swipeRefreshLayout.setColorSchemeColors(Color.parseColor("#028352"));
        swipeRefreshLayout.setOnRefreshListener(this::fetchData);

        // Firebase references
        singleItemDB = FirebaseDatabase.getInstance().getReference("Pending_order_chef_db_singleOrder");
        multiItemDB = FirebaseDatabase.getInstance().getReference("Pending_order_chef_db_MultiOrder");

        // Adapter setup
        pendingOrderAdapter = new ChefPendingOrderFragmentAdapter(pendingOrderList, getContext());
        recyclerView.setAdapter(pendingOrderAdapter);

        fetchData();
        return view;
    }

    private void fetchData() {
        singleItemDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                pendingOrderList.clear();
                swipeRefreshLayout.setRefreshing(true);

                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Log.d("FirebaseData", "Data: " + dataSnapshot.getValue()); // Log fetched data
                        ChefPendingSingleOrderModel orderModel=dataSnapshot.getValue(ChefPendingSingleOrderModel.class);

                        if (orderModel != null) {
                            pendingOrderList.add(orderModel);
                        } else {
                            Log.e("ModelError", "Failed to parse ChefPendingSingleOrderModel");
                        }
                    }
                    pendingOrderAdapter.notifyDataSetChanged();
                } else {
                    Log.e("DataError", "No data found in Single_Confirm_order");
                }

                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", "Database Error: " + error.getMessage());
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        // Multi items fetch
        multiItemDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        ChefPendingMultipleOrderModel orderModel= dataSnapshot.getValue(ChefPendingMultipleOrderModel.class);
                        if (orderModel != null) {
                            pendingOrderList.add(orderModel);
                        }
                    }
                    pendingOrderAdapter.notifyDataSetChanged();
                } else {
                    Log.e("DataError", "No data found in Multi_Confirm_order");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", "Database Error: " + error.getMessage());
                Toast.makeText(getContext(), "Failed to load multi orders. Please try again later.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}


