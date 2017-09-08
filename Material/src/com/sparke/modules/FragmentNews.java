package com.sparke.modules;

import java.lang.reflect.Field;
import java.util.ArrayList;

import com.example.material.R;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rey.material.widget.TabPageIndicator;
import com.sparke.util.CustomViewPager;

public class FragmentNews extends Fragment {

	private static final String SELECTED_POSITION = "selected_menu_position";

	private int mPosition = 1;
	private PagerAdapter mPagerAdapter;
	
	private CustomViewPager vp;
	private TabPageIndicator tpi;

	private Tab[] mItems = new Tab[] { Tab.NOTICE, Tab.IMPORTANT, Tab.HOT };

	public FragmentNews() {
	}

	public static FragmentNews newInstance(int number) {
		FragmentNews fragment = new FragmentNews();
		Bundle args = new Bundle();
		args.putInt(SELECTED_POSITION, number);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		int n = getArguments().getInt(SELECTED_POSITION);
		((MainActivity) activity).onSectionAttached(n);
		switch (n) {
		case 4:
			mPosition = 1;
			break;
		case 5:
			mPosition = 0;
			break;
		case 6:
			mPosition = 2;
			break;
		}
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		if (container == null)
			return null;
		View view = inflater.inflate(R.layout.fragment_news, container, false);

		vp = (CustomViewPager) view.findViewById(R.id.main_vp);
		tpi = (TabPageIndicator) view.findViewById(R.id.main_tpi);
		
		mPagerAdapter = new PagerAdapter(getChildFragmentManager(), mItems); /******/
		vp.setAdapter(mPagerAdapter);
		vp.setOffscreenPageLimit(3);
		tpi.setViewPager(vp);
		tpi.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int arg0) {	
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {}
		});
		vp.setCurrentItem(mPosition);
		
		return view;
	}
	
	public void setPosition(int n) {
		vp.setCurrentItem(n);
	}
	
	public int getPosition() {
		return vp.getCurrentItem();
	}

	public enum Tab {
		IMPORTANT("学工要闻"), NOTICE("通知公告"), HOT("热点关注");

		private final String name;

		private Tab(String s) {
			name = s;
		}

		public boolean equalsName(String otherName) {
			return (otherName != null) && name.equals(otherName);
		}

		public String toString() {
			return name;
		}
	}

	private static class PagerAdapter extends FragmentStatePagerAdapter {

		Fragment[] mFragments;
		Tab[] mTabs;

		private static final Field sActiveField;

		static {
			Field f = null;
			try {
				Class<?> c = Class
						.forName("android.support.v4.app.FragmentManagerImpl");
				f = c.getDeclaredField("mActive");
				f.setAccessible(true);
			} catch (Exception e) {
			}

			sActiveField = f;
		}

		public PagerAdapter(FragmentManager fm, Tab[] tabs) {
			super(fm);
			mTabs = tabs;
			mFragments = new Fragment[mTabs.length];

			// dirty way to get reference of cached fragment
			try {
				ArrayList<Fragment> mActive = (ArrayList<Fragment>) sActiveField.get(fm);
				if (mActive != null) {
					for (Fragment fragment : mActive) {
						if (fragment instanceof FragmentNewsImportant)
							setFragment(Tab.IMPORTANT, fragment);
						else if (fragment instanceof FragmentNewsHot)
							setFragment(Tab.HOT, fragment);
						else if (fragment instanceof FragmentNewsNotice)
							setFragment(Tab.NOTICE, fragment);
					}
				}
			} catch (Exception e) {
			}
		}

		private void setFragment(Tab tab, Fragment f) {
			for (int i = 0; i < mTabs.length; i++)
				if (mTabs[i] == tab) {
					mFragments[i] = f;
					break;
				}
		}

		@Override
		public Fragment getItem(int position) {
			if (mFragments[position] == null) {
				switch (mTabs[position]) {
				case NOTICE:
					mFragments[position] = FragmentNewsNotice.newInstance();
					break;
				case IMPORTANT:
					mFragments[position] = FragmentNewsImportant.newInstance();
					break;
				case HOT:
					mFragments[position] = FragmentNewsHot.newInstance();
					break;
				}
			}

			return mFragments[position];
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return mTabs[position].toString().toUpperCase();
		}

		@Override
		public int getCount() {
			return mFragments.length;
		}
	}
}
