package com.codepath.apps.chirp.ui.timeline.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.apps.chirp.R;
import com.codepath.apps.chirp.TwitterApplication;
import com.codepath.apps.chirp.helpers.EndlessRecyclerViewScrollListener;
import com.codepath.apps.chirp.models.Tweet;
import com.codepath.apps.chirp.network.TwitterClient;
import com.codepath.apps.chirp.ui.timeline.TweetsAdapter;

import java.util.ArrayList;

/**
 * Created by nick on 11/1/16.
 */

public class TweetsListFragment extends Fragment implements TweetsAdapter.OnTweetsAdapterListener {

    //@BindView(R.id.rvTweets)
    RecyclerView rvTweets;

    protected TwitterClient twitterClient;

    protected ArrayList<Tweet> tweets;
    protected TweetsAdapter aTweets;
    protected LinearLayoutManager linearLayoutManager;

    private TweetsAdapter.OnTweetsAdapterListener listener;

    // stores the oldest id for our fetched tweets
    private long currentMaxId;

    // Store the listener (activity) that will have events fired once the fragment is attached
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof TweetsAdapter.OnTweetsAdapterListener) {
            listener = (TweetsAdapter.OnTweetsAdapterListener) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement TweetsAdapter.OnTweetsAdapterListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tweets_list, container, false);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvTweets = (RecyclerView) view.findViewById(R.id.rvTweets);
        aTweets.setOnTweetsAdapterListener(this);
        rvTweets.setAdapter(aTweets);

        linearLayoutManager = new LinearLayoutManager(getActivity());
        rvTweets.setLayoutManager(linearLayoutManager);

        rvTweets.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                // add 1 to currentMaxId so we don't fetch the same tweet again since it's inclusive.
                Tweet oldestTweet = tweets.get(tweets.size()-1);
                currentMaxId = oldestTweet.getUid();
                populateTimeline(currentMaxId+1,false);
                Log.d("DEBUG", "load more");
            }
        });

        populateTimeline(0,true);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tweets = new ArrayList<>();
        aTweets = new TweetsAdapter(getActivity(),tweets);
        // todo: either this needs to implement listener or activity does?

        twitterClient = TwitterApplication.getRestClient();
    }

    public void populateTimeline(long maxId, final boolean reset) {

    }

    public void addAll(ArrayList<Tweet> tweetList, boolean reset) {
        if (reset) {
            tweets.clear();
        }
        tweets.addAll(tweetList);
        aTweets.notifyDataSetChanged();
    }

    public void addToTop(Tweet tweet) {
        tweets.add(tweet);
        aTweets.notifyDataSetChanged();

        linearLayoutManager.scrollToPositionWithOffset(0, 0);

        // TODO: call protected override class?
        // populateTimeline(0, true);
    }

    @Override
    public void onTweetClick(Tweet tweet) {
        listener.onTweetClick(tweet);
    }

    @Override
    public void onTweetProfileImageClick(Tweet tweet) {
       listener.onTweetProfileImageClick(tweet);
    }
}
