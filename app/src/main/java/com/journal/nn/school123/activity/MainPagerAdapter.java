package com.journal.nn.school123.activity;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.journal.nn.school123.fragment.SwipeFragment;

import java.util.List;

class MainPagerAdapter extends FragmentStatePagerAdapter {
    private List<SwipeFragment> fragments;

    MainPagerAdapter(@NonNull FragmentManager fragmentManager,
                     @NonNull List<SwipeFragment> fragments) {
        super(fragmentManager);
        this.fragments = fragments;
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return fragments.get(position).getTitle();
    }
}
