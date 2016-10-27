package com.codepath.apps.chirp.ui.timeline;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.codepath.apps.chirp.R;
import com.codepath.apps.chirp.TwitterApplication;
import com.codepath.apps.chirp.helpers.EndlessRecyclerViewScrollListener;
import com.codepath.apps.chirp.models.Tweet;
import com.codepath.apps.chirp.network.TwitterClient;
import com.codepath.apps.chirp.ui.compose.ComposeFragment;
import com.codepath.apps.chirp.ui.detail.DetailActivity;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class TimelineActivity extends AppCompatActivity implements TweetsAdapter.OnTweetsAdapterListener, ComposeFragment.OnComposeListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.rvTweets)
    RecyclerView rvTweets;

    @BindView(R.id.abCompose)
    FloatingActionButton abCompose;

    private TwitterClient client;
    private ArrayList<Tweet> tweets;
    private TweetsAdapter aTweets;

    // stores the oldest id for our fetched tweets
    private long currentMaxId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        tweets = new ArrayList<>();
        aTweets = new TweetsAdapter(this,tweets);

        aTweets.setOnTweetsAdapterListener(this);

        rvTweets.setAdapter(aTweets);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);

        rvTweets.setLayoutManager(linearLayoutManager);

        //rvArticles.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacing, includeEdge));

        rvTweets.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                // add 1 to currentMaxId so we don't fetch the same tweet again since it's inclusive.
                Tweet oldestTweet = tweets.get(tweets.size()-1);
                currentMaxId = oldestTweet.getUid();
                populateTimeline(currentMaxId+1);
                Log.d("DEBUG", "load more");
            }
        });

        abCompose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onComposeButton();
            }
        });

        client = TwitterApplication.getRestClient();
        populateTimeline(0);
    }

    // get the twitter timeline json and fill our list view
    private void populateTimeline(long maxId) {
        Log.d("DEBUG","populateTimeline maxId:"+maxId);
        client.getHomeTimeline(maxId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.d("DEBUG",response.toString());
                // after we get our list of tweets, find the oldest one and remember that
                // in case we need to fetch again.
                tweets.addAll(Tweet.fromJSONArray(response));
                aTweets.notifyDataSetChanged();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEUBG", errorResponse.toString());
            }
        });
    }

    // launchers
    public void onComposeButton() {
        Log.d("DEBUG","onComposeButton");
        FragmentManager fm = getSupportFragmentManager();
        ComposeFragment fragment = ComposeFragment.newInstance("Compose");
        fragment.show(fm, "compose");
    }

    @Override
    public void onTweetClick(Tweet tweet) {
        Log.d("DEBUG","CLICKED TWEET");
        Intent i = new Intent(getApplicationContext(), DetailActivity.class);
        i.putExtra("tweet", Parcels.wrap(tweet));
        startActivity(i);
    }

    // results from fragments


    @Override
    public void onSendTweet(String body) {
        Log.d("DEBUG","send tweet");
    }
}
