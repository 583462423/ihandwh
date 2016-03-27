/*
 * Copyright 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.xinqi.ihandwh.ui_components;

//import com.xinqi.ihandwh.R;
import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xinqi.ihandwh.ConfigCenter.ConfigCenterContentPage;
import com.xinqi.ihandwh.OrderSeats.BookSeatsContentPage;
import com.xinqi.ihandwh.R;
import com.xinqi.ihandwh.SearchBook.SearchBookContentPage;

import java.util.ArrayList;
import java.util.List;

/**
 * A basic sample which shows how to use {@link SlidingTabLayout}
 * to display a custom {@link ViewPager} title strip which gives continuous feedback to the user
 * when scrolling.
 */
public class SlidingTabsIconsFragment extends Fragment {
    public static final String TAG=SlidingTabsIconsFragment.class.getSimpleName();
    /**
     * This class represents a tab to be displayed by {@link ViewPager} and it's associated
     * {@link SlidingTabLayout}.
     */

    static class SamplePagerItem {
        private final CharSequence mTitle;
        private final int mIndicatorColor;
        private final int mDividerColor;
        private final int mIconRes;
        private final int m2ndIconRes;
        private Fragment mFragment;

        SamplePagerItem(Fragment fragment, CharSequence title, int indicatorColor, int dividerColor, int iconRes, int iconRes2) {
            mFragment=fragment;
            mTitle = title;
            mIndicatorColor = indicatorColor;
            mDividerColor = dividerColor;
            mIconRes=iconRes;
            m2ndIconRes=iconRes2;
        }

        /**
         * @return A new {@link Fragment} to be displayed by a {@link ViewPager}
         */
        Fragment getFragment() {
            return mFragment;
        }

        /**
         * @return the title which represents this tab. In this sample this is used directly by
         * {@link android.support.v4.view.PagerAdapter#getPageTitle(int)}
         */
        CharSequence getTitle() {
            return mTitle;
        }

        /**
         * @return the color to be used for indicator on the {@link SlidingTabLayout}
         */
        int getIndicatorColor() {
            return mIndicatorColor;
        }

        /**
         * @return the color to be used for right divider on the {@link SlidingTabLayout}
         */
        int getDividerColor() {
            return mDividerColor;
        }

        int getIconRes(){
            return mIconRes;
        }

        int get2ndIconRes(){
            return m2ndIconRes;
        }
    }

    /**
     * A custom {@link ViewPager} title strip which looks much like Tabs present in Android v4.0 and
     * above, but is designed to give continuous feedback to the user when scrolling.
     */
    private SlidingTabLayout mSlidingTabLayout;

    /**
     * A {@link ViewPager} which will be used in conjunction with the {@link SlidingTabLayout} above.
     */
    private ViewPager mViewPager;

    /**
     * List of {@link Fragment} which represent this sample's tabs.
     */
    private List<SamplePagerItem> mTabs = new ArrayList<SamplePagerItem>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // BEGIN_INCLUDE (populate_tabs)
        /**
         * Populate our tab list with tabs. Each item contains a title, indicator color and divider
         * color, which are used by {@link SlidingTabLayout}.
         */
        //添加预约座位Fragment
        mTabs.add(new SamplePagerItem(BookSeatsContentPage.newInstance(),
                getResources().getString(R.string.book_seats_title), // Title
                Color.RED, // Indicator color
                Color.GRAY,// Divider color
                R.drawable.tabs_title_icon1,
                R.drawable.tabs_title_selected1));
        //添加查找书籍Fragment
        mTabs.add(new SamplePagerItem(
                SearchBookContentPage.newInstance(),
                getResources().getString(R.string.search_book_title),
                getResources().getColor(R.color.search_book_stripe_color),
                Color.GRAY,
                R.drawable.tabs_title_icon2,
                R.drawable.tabs_title_selected2));
        //添加个人中心Fragment
        mTabs.add(new SamplePagerItem(
                ConfigCenterContentPage.newInstance(),
                getResources().getString(R.string.config_center_title),
                getResources().getColor(R.color.config_center_stripe_color),
                Color.GRAY,
                R.drawable.tabs_title_icon3,
                R.drawable.tabs_title_selected3));
        // END_INCLUDE (populate_tabs)
    }
    /**
     * Inflates the {@link View} which will be displayed by this {@link Fragment}, from the app's
     * resources.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.sliding_tabs_icons_main_layout, container, false);
    }

    // BEGIN_INCLUDE (fragment_onviewcreated)
    /**
     * This is called after the {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)} has finished.
     * Here we can pick out the {@link View}s we need to configure from the content view.
     *
     * We set the {@link ViewPager}'s adapter to be an instance of
     * {@link SampleFragmentPagerAdapter}. The {@link SlidingTabLayout} is then given the
     * {@link ViewPager} so that it can populate itself.
     *
     * @param view View created in {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}
     */

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // BEGIN_INCLUDE (setup_viewpager)
        // Get the ViewPager and set it's PagerAdapter so that it can display items
        mViewPager = (ViewPager) view.findViewById(R.id.content_viewpager);
        mViewPager.setAdapter(new SampleFragmentPagerAdapter(getChildFragmentManager()));
        // END_INCLUDE (setup_viewpager)

        // BEGIN_INCLUDE (setup_slidingtablayout)
        // Give the SlidingTabLayout the ViewPager, this must be done AFTER the ViewPager has had
        // it's PagerAdapter set.
        mSlidingTabLayout = (SlidingTabLayout) view.findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setCustomTabView(R.layout.sliding_tabs_icons_selector_bar_item, R.id.selectorText, R.id.selectorIcon);
        // BEGIN_INCLUDE (tab_colorizer)
        // Set a TabColorizer to customize the indicator and divider colors. Here we just retrieve
        // the tab at the position, and return it's set color
        mSlidingTabLayout.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {

            @Override
            public int getIndicatorColor(int position) {
                return mTabs.get(position).getIndicatorColor();
            }

            @Override
            public int getDividerColor(int position) {
                return mTabs.get(position).getDividerColor();
            }
        });

        mSlidingTabLayout.setCustomIconsResSet(new SlidingTabLayout.IconsRes() {
            @Override
            public List<Integer> getIconsSet() {
                return null;
            }

            @Override
            public int getIconAt(int position) {
                return mTabs.get(position).getIconRes();
            }

            @Override
            public int get2ndIconAt(int position) {
                return mTabs.get(position).get2ndIconRes();
            }
        });

        mSlidingTabLayout.setActionBarInterface(new SlidingTabLayout.ActionBarInterface() {
            @Override
            public ActionBar getCustomActionBar() {
                return getActivity().getActionBar();
            }

            @Override
            public TextView getTitle() {
                return (TextView)(getActivity().getActionBar().getCustomView().findViewById(R.id.textViewTitle));
            }
        });

        mSlidingTabLayout.setDistributeEvenly(true);

        mSlidingTabLayout.setViewPager(mViewPager);


        // END_INCLUDE (tab_colorizer)
        // END_INCLUDE (setup_slidingtablayout)
    }
    // END_INCLUDE (fragment_onviewcreated)

    /**
     * The {@link FragmentPagerAdapter} used to display pages in this sample. The individual pages
     * are instances of {@link Fragment} which just display three lines of text. Each page is
     * created by the relevant {@link SamplePagerItem} for the requested position.
     * <p>
     * The important section of this class is the {@link #getPageTitle(int)} method which controls
     * what is displayed in the {@link SlidingTabLayout}.
     */
    class SampleFragmentPagerAdapter extends FragmentPagerAdapter {

        SampleFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        /**
         * Return the {@link android.support.v4.app.Fragment} to be displayed at {@code position}.
         * <p>
         * Here we return the value returned from {@link SamplePagerItem#getFragment()}.
         */
        @Override
        public Fragment getItem(int i) {
            return mTabs.get(i).getFragment();
        }

        @Override
        public int getCount() {
            return mTabs.size();
        }

        // BEGIN_INCLUDE (pageradapter_getpagetitle)
        /**
         * Return the title of the item at {@code position}. This is important as what this method
         * returns is what is displayed in the {@link SlidingTabLayout}.
         * <p>
         * Here we return the value returned from {@link SamplePagerItem#getTitle()}.
         */
        @Override
        public CharSequence getPageTitle(int position) {
            return mTabs.get(position).getTitle();
        }
        // END_INCLUDE (pageradapter_getpagetitle)

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("bac","12232341");
        for(int i=0;i<mTabs.size();++i)
            mTabs.get(i).mFragment.onActivityResult(requestCode, resultCode, data);
    }
}