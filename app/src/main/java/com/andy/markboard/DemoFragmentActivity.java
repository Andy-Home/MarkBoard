package com.andy.markboard;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;

import com.andy.markboard.adapter.AndyPagerAdapter;

import java.util.ArrayList;

public class DemoFragmentActivity extends FragmentActivity {

    FragmentManager mFragmentManager;
    FragmentTransaction mFragmentTransaction;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);

        ViewPager viewPager = findViewById(R.id.viewpager);
        mFragmentManager = getSupportFragmentManager();
        mFragmentTransaction = mFragmentManager.beginTransaction();
        DemoFragment f1 = new DemoFragment();
        DemoFragment f2 = new DemoFragment();
        ArrayList<Fragment> mData = new ArrayList<>();
        mData.add(f1);
        mData.add(f2);

        viewPager.setAdapter(new AndyPagerAdapter(mFragmentManager, mData));
        viewPager.setCurrentItem(0);
    }
}
