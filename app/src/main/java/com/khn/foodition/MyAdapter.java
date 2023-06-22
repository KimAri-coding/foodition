package com.khn.foodition;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class MyAdapter extends FragmentStateAdapter {

    int mCount;

    public MyAdapter(Foodition_Intro_Activity foodition, int count) {
        super(foodition);
        mCount = count;
    }
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        int index = getRealPosition(position);

        if(index == 0) return new Fragment_1p();
        else if(index == 1) return new Fragment_2p();
        else if(index == 2) return new Fragment_3p();
        else return new Fragment_4p();
    }
    @Override
    public int getItemCount() {
        return 5000;
    }
    public int getRealPosition(int position) { return position % mCount;}
}
