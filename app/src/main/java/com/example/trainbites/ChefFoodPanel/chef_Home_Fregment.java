package com.example.trainbites.ChefFoodPanel;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.trainbites.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class chef_Home_Fregment extends Fragment {

    private RecyclerView recyclerView;
    private ArrayList<chefProjectModel> recycleList;
    private FirebaseDatabase firebaseDatabase;
    private UpdateItemsAdapter recyclerAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private static final String TAG = "ChefHomeFragment";


    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_chef__home__fregment, container, false);


        // Initialization
        recyclerView = view.findViewById(R.id.ChefrecyclerView);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout); // Initialize the SwipeRefreshLayout
        recycleList = new ArrayList<>();
        firebaseDatabase = FirebaseDatabase.getInstance();

        // Set up RecyclerView
        recyclerAdapter = new UpdateItemsAdapter(recycleList, requireContext());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(recyclerAdapter);

        // Set the color scheme for the SwipeRefreshLayout
        swipeRefreshLayout.setColorSchemeColors(Color.parseColor("#028352"));

        // Fetch data from Firebase database
        fetchProductItems();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchProductItems();
            }
        });


        return view;
    }

    private void fetchProductItems() {
        swipeRefreshLayout.setRefreshing(true);  // Show the refresh indicator
        firebaseDatabase.getReference().child("product").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                recycleList.clear(); // Clear the list before adding new items
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    chefProjectModel chefProjectModel = dataSnapshot.getValue(chefProjectModel.class);
                    if (chefProjectModel != null) {
                        chefProjectModel.setKey(dataSnapshot.getKey()); // Set the key
                        recycleList.add(chefProjectModel);
                    } else {
                        Log.e(TAG, "chefProjectModel is null for dataSnapshot: " + dataSnapshot.getKey());
                    }
                }
                recyclerAdapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);  // Hide the refresh indicator
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Database error: " + error.getMessage());
                swipeRefreshLayout.setRefreshing(false);  // Hide the refresh indicator
            }
        });
    }
}