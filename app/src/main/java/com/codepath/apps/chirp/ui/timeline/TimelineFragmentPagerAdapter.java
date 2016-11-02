package com.codepath.apps.chirp.ui.timeline;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.codepath.apps.chirp.fragments.HomeTimelineFragment;
import com.codepath.apps.chirp.fragments.MentionsTimelineFragment;

/**
 * Created by nick on 11/2/16.
 */

public class TimelineFragmentPagerAdapter extends FragmentPagerAdapter {
    final int PAGE_COUNT = 2;
    private String tabTitles[] = new String[] { "Home", "Mentions" };
    private Context context;

    public TimelineFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new HomeTimelineFragment();
        }
        return new MentionsTimelineFragment();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];
    }

}
