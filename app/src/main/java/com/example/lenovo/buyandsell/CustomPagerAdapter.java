package com.example.lenovo.buyandsell;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class CustomPagerAdapter extends FragmentPagerAdapter {
    public CustomPagerAdapter(FragmentManager fm) {
        super( fm );
    }

    @Override
    public Fragment getItem(int position) {

        switch(position){

            case 0:
                InboxFragment inboxFragment=new InboxFragment();
                return inboxFragment;
            case 1:
                BuyFragment buyFragment=new BuyFragment();
                return buyFragment;
            case 2:
                SellFragment sellFragment=new SellFragment();
                return sellFragment;
            default:
                return null;

        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {

        switch(position){
            case 0:
                return "INBOX";
            case 1:
                return "BUY";
            case 2:
                return "SELL";
            default:
                return null;
        }

    }
}
