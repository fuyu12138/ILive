package com.graduation.fuyu.ilive.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class LabelFragmentAdapter extends FragmentPagerAdapter {

	List<Fragment> fragmentList = new ArrayList<Fragment>();
	public LabelFragmentAdapter(FragmentManager fm, List<Fragment> fragmentList) {
		super(fm);
		this.fragmentList = fragmentList;
	}

	@Override
	public Fragment getItem(int position) {
		return fragmentList.get(position);
	}

	@Override
	public int getCount() {
		return fragmentList.size();
	}

	@Override
	public CharSequence getPageTitle(int position) {
		switch (position) {
			case 0:
				return "娱乐";
			case 1:
				return "游戏";
			case 2:
				return "手游";
			case 3:
				return "绘画";
			default:
				return null;
		}
	}

}
