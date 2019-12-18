/*
 * MainViewPagerAdapter
 * D:/course/geochat-client-master/app/src/main/java/co/siarhei/apps/android/geochat/UI/Adapters/MainViewPagerAdapter.java
 *
 * Project: geochat-client-master
 * Module: app
 * Last Modified: 17.12.19 20:41 <dexte>
 * Last Compilation: 17.12.19 20:41
 *
 * Copyright (c) 2019. Martin David Shaw. All rights reserved.
 */

package co.siarhei.apps.android.geochat.UI.Adapters;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class MainViewPagerAdapter extends FragmentPagerAdapter {
    private ArrayList<Fragment> fragments;
    private ArrayList<String>   titles;

    public MainViewPagerAdapter(FragmentManager fm){
        super(fm);
        this.fragments = new ArrayList<>();
        this.titles = new ArrayList<>();
    }


    @Override
    public Fragment getItem(int i) {
        return fragments.get(i);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    public void addFragment(Fragment  fragment,String title){
        fragments.add(fragment);
        titles.add(title);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return titles.get(position);
    }
}
