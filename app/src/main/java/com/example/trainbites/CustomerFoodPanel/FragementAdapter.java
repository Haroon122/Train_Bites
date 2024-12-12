package com.example.trainbites.CustomerFoodPanel;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.trainbites.ChefFoodPanel.cehf_order_fregmant;
import com.example.trainbites.ChefFoodPanel.chef_Home_Fregment;
import com.example.trainbites.ChefFoodPanel.chef_pending_orders_fegment;
import com.example.trainbites.ChefFoodPanel.chef_post_dish_fregment;

public class FragementAdapter extends FragmentStateAdapter{
    public FragementAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new CustomerHomeFregment();
            case 1:
                return new CustomerOrderFregment();
            case 2:
                return new CustomerCartFregment();
            case 3:
                return new CustomerTrackOrderFregment();
            case 4:
                return new CustomerProfileFregment();
            default:
                return new CustomerHomeFregment();
        }
    }

    @Override
    public int getItemCount() {
        return 5;
    }
}
