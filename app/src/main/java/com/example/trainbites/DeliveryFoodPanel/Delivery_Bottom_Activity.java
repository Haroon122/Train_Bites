package com.example.trainbites.DeliveryFoodPanel;

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

public class Delivery_Bottom_Activity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    ViewPager2 viewPager2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_delivery_bottom);

        bottomNavigationView = findViewById(R.id.BottomNavigationBarDelivery);
        viewPager2 = findViewById(R.id.viewpagerDelivery);

        FragementAdapter adapter = new FragementAdapter(this);
        viewPager2.setAdapter(adapter);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int itmId = menuItem.getItemId();
                if (itmId == R.id.pendingordersDelivery) {
                    viewPager2.setCurrentItem(0);
                    return true;
                } else if (itmId == R.id.shipordersDelivery) {
                    viewPager2.setCurrentItem(1);
                    return true;
                } else if (itmId==R.id.ridersProfile) {
                    viewPager2.setCurrentItem(2);
                    return true;
                }
                return false;
            }
        });
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                switch (position) {
                    case 0:
                        bottomNavigationView.setSelectedItemId(R.id.pendingordersDelivery);
                        break;
                    case 1:
                        bottomNavigationView.setSelectedItemId(R.id.shipordersDelivery);
                        break;
                    case 2:
                        bottomNavigationView.setSelectedItemId(R.id.ridersProfile);
                        break;
                }

            }
        });

        // Set default fragment
        if (savedInstanceState == null) {
            bottomNavigationView.setSelectedItemId(R.id.pendingordersDelivery);
        }
    }
    @Override
    public void onBackPressed() {
        int selectedItemId = bottomNavigationView.getSelectedItemId();
        if (selectedItemId != R.id.pendingordersDelivery) {
            // Select home fragment
            bottomNavigationView.setSelectedItemId(R.id.pendingordersDelivery);
        } else {
            // Exit the app
            super.onBackPressed();
        }
    }

}
