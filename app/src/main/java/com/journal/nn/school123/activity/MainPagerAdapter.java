package com.journal.nn.school123.activity;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

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
