package com.example.trainbites.CustomerFoodPanel;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.trainbites.ChefFoodPanel.cehfProjectAdapter;
import com.example.trainbites.R;
import com.example.trainbites.ChefFoodPanel.chefProjectModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

public class CustomerHomeFregment extends Fragment {

    RecyclerView recyclerView;
    ArrayList<chefProjectModel> recycleList;
    FirebaseDatabase firebaseDatabase;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_customer_home_fregment, container, false);

        // Initialization
        recyclerView = view.findViewById(R.id.MYrecyclerView);
        recycleList = new ArrayList<>();
        firebaseDatabase = FirebaseDatabase.getInstance();

        // Set up RecyclerView
        cehfProjectAdapter recyclerAdapter = new cehfProjectAdapter(recycleList, requireContext());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(recyclerAdapter);

        // Fetch data from Firebase database
        firebaseDatabase.getReference().child("product").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    chefProjectModel chefProjectModel = dataSnapshot.getValue(chefProjectModel.class);
                    recycleList.add(chefProjectModel);
                }

                recyclerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database error
            }
        });

        return view;
    }
}
