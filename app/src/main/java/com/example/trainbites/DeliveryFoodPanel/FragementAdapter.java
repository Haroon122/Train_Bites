package com.example.trainbites.DeliveryFoodPanel;

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
                return new DeliveryPendingFregment();
            case 1:
                return new DeliveryTrackOrderFregment();
            default:
                return new chef_Home_Fregment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
