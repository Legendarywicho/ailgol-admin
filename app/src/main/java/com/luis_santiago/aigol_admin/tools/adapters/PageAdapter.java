package com.luis_santiago.aigol_admin.tools.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by legendarywicho on 9/6/17.
 */

public class PageAdapter extends FragmentStatePagerAdapter{

    private int numberOfTabs = 2;
    private Fragment mFragmentStanding;
    private Fragment mFragmentScores;
    private String tittleArray [] = {"Tables", "Score"};

    public PageAdapter(FragmentManager fm, Fragment fragment, Fragment scores) {
        super(fm);
        this.mFragmentStanding = fragment;
        this.mFragmentScores = scores;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:{
                return mFragmentStanding;
            }
            case 1:{
                return mFragmentScores;
            }

        }

        return null;
    }

    @Override
    public int getCount() {
        return numberOfTabs;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tittleArray[position];
    }
}
