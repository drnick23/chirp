package com.codepath.apps.chirp.ui.timeline.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.codepath.apps.chirp.models.Tweet;
import com.codepath.apps.chirp.network.TwitterPersistence;

import java.util.ArrayList;

/**
 * Created by nick on 11/1/16.
 */

public class MentionsTimelineFragment extends TweetsListFragment {


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public void populateTimeline(long maxId, final boolean reset) {
        Log.d("DEBUG","MentionsTimelineFragment populateTimeline maxId:"+maxId);

        TwitterPersistence.getInstance().getMentionsTimeline(maxId, 0, new TwitterPersistence.OnTimelineResults() {
            @Override
            public void onSuccess(ArrayList<Tweet> tweetList) {
                addAll(tweetList, reset);
            }

            @Override
            public void onFailure(String toastMsg) {
                //Toast.makeText(TimelineActivity.this,toastMsg,Toast.LENGTH_LONG).show();
            }
        });
    }

}
