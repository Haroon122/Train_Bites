package com.example.trainbites.ChefFoodPanel;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class FragementAdapter extends FragmentStateAdapter{
    public FragementAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new chef_Home_Fregment();
            case 1:
                return new chef_pending_orders_fegment();
            case 2:
                return new cehf_order_fregmant();
            case 3:
                return new chef_post_dish_fregment();
            case 4:
                return new Chef_profileID_fregment();
            default:
                return new chef_Home_Fregment();
        }
    }

    @Override
    public int getItemCount() {
        return 5;
    }
}
