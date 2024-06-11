package com.example.trainbites.ChefFoodPanel;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.trainbites.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Chef_Bottom_Activity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    ViewPager2 viewPager2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chef_bottom);

        bottomNavigationView=findViewById(R.id.BottomNavigationBar);
        viewPager2=findViewById(R.id.viewpager);

        FragementAdapter adapter =new FragementAdapter(this);
        viewPager2.setAdapter(adapter);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int itmId=menuItem.getItemId();
                if (itmId==R.id.chefHome){
                    viewPager2.setCurrentItem(0);
                    return true;
                } else if (itmId==R.id.PendingOrders) {
                    viewPager2.setCurrentItem(1);
                    return true;
                } else if (itmId==R.id.Orders) {
                    viewPager2.setCurrentItem(2);
                    return true;
                } else if (itmId==R.id.chefProfile) {
                    viewPager2.setCurrentItem(3);
                    return true;
                }
                return false;


            }
        });

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                switch (position){
                    case 0:
                        bottomNavigationView.setSelectedItemId(R.id.chefHome);
                        break;
                    case 1:
                        bottomNavigationView.setSelectedItemId(R.id.PendingOrders);

                        break;
                    case 2:
                        bottomNavigationView.setSelectedItemId(R.id.Orders);

                        break;
                    case 3:
                        bottomNavigationView.setSelectedItemId(R.id.chefProfile);

                        break;
                }
            }
        });
        // Set default fragment
        if (savedInstanceState == null) {
            bottomNavigationView.setSelectedItemId(R.id.chefHome);
        }
    }
    @Override
    public void onBackPressed() {
        int selectedItemId = bottomNavigationView.getSelectedItemId();
        if (selectedItemId != R.id.chefHome) {
            // Select home fragment
            bottomNavigationView.setSelectedItemId(R.id.chefHome);
        } else {
            // Exit the app
            super.onBackPressed();
        }
    }
}