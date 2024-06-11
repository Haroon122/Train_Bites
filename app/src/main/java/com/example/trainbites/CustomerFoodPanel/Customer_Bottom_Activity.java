package com.example.trainbites.CustomerFoodPanel;

import android.annotation.SuppressLint;
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

public class Customer_Bottom_Activity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    ViewPager2 viewPager2;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_customer_bottom);

        bottomNavigationView=findViewById(R.id.BottomNavigationBarCustomer);
        viewPager2=findViewById(R.id.viewpagerCustomer);

        FragementAdapter adapter=new FragementAdapter(this);
        viewPager2.setAdapter(adapter);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int itmId=menuItem.getItemId();
                if (itmId==R.id.customerHome){
                    viewPager2.setCurrentItem(0);
                    return true;
                } else if (itmId==R.id.orderCustomer) {
                    viewPager2.setCurrentItem(1);
                    return true;
                } else if (itmId==R.id.cart) {
                    viewPager2.setCurrentItem(2);
                    return true;
                } else if (itmId==R.id.TrackOrderCustomer) {
                    viewPager2.setCurrentItem(3);
                    return true;
                } else if (itmId==R.id.Customerprofile) {
                    viewPager2.setCurrentItem(4);
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
                        bottomNavigationView.setSelectedItemId(R.id.customerHome);
                        break;
                    case 1:
                        bottomNavigationView.setSelectedItemId(R.id.orderCustomer);

                        break;
                    case 2:
                        bottomNavigationView.setSelectedItemId(R.id.cart);

                        break;
                    case 3:
                        bottomNavigationView.setSelectedItemId(R.id.TrackOrderCustomer);

                        break;
                    case 4:
                        bottomNavigationView.setSelectedItemId(R.id.Customerprofile);

                        break;

                }

            }
        });

        if (savedInstanceState == null) {
            bottomNavigationView.setSelectedItemId(R.id.customerHome);
        }
    }
    @Override
    public void onBackPressed() {
        int selectedItemId = bottomNavigationView.getSelectedItemId();
        if (selectedItemId != R.id.customerHome) {
            // Select home fragment
            bottomNavigationView.setSelectedItemId(R.id.customerHome);
        } else {
            // Exit the app
            super.onBackPressed();
        }
    }

}