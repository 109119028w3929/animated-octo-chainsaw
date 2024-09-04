package com.srteam.expensetracker.adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.srteam.expensetracker.R;
import com.srteam.expensetracker.ui.login.WelcomePage;

public class WelcomePagerAdapter extends FragmentStatePagerAdapter {

    private static final int NUM_PAGES = 3;

    public WelcomePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        WelcomePage tp = null;
        switch (position) {
            case 0:
                tp = WelcomePage.newInstance(R.layout.layout_welcome_first);
                break;
            case 1:
                tp = WelcomePage.newInstance(R.layout.layout_welcome_second);
                break;
            case 2:
                tp = WelcomePage.newInstance(R.layout.layout_welcome_third);
                break;
        }
        return tp;
    }

    @Override
    public int getCount() {
        return NUM_PAGES;
    }

}