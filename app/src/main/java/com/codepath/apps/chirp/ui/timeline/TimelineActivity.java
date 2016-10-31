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
import android.widget.Toast;

import com.codepath.apps.chirp.R;
import com.codepath.apps.chirp.helpers.EndlessRecyclerViewScrollListener;
import com.codepath.apps.chirp.models.Tweet;
import com.codepath.apps.chirp.network.TwitterPersistence;
import com.codepath.apps.chirp.ui.compose.ComposeFragment;
import com.codepath.apps.chirp.ui.detail.DetailActivity;

import org.parceler.Parcels;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TimelineActivity extends AppCompatActivity implements TweetsAdapter.OnTweetsAdapterListener, ComposeFragment.OnComposeListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.rvTweets)
    RecyclerView rvTweets;

    @BindView(R.id.abCompose)
    FloatingActionButton abCompose;

    private TwitterPersistence clientPersistence;
    private ArrayList<Tweet> tweets;
    private TweetsAdapter aTweets;
    private LinearLayoutManager linearLayoutManager;

    // stores the oldest id for our fetched tweets
    private long currentMaxId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Timeline");

        tweets = new ArrayList<>();
        aTweets = new TweetsAdapter(this,tweets);

        aTweets.setOnTweetsAdapterListener(this);
        rvTweets.setAdapter(aTweets);

        linearLayoutManager = new LinearLayoutManager(this);
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

        abCompose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onComposeButton();
            }
        });

        clientPersistence = TwitterPersistence.getInstance();
        populateTimeline(0, true);
    }

    // get the twitter timeline json and fill our list view
    private void populateTimeline(long maxId, final boolean reset) {
        Log.d("DEBUG","populateTimeline maxId:"+maxId);

        TwitterPersistence.getInstance().getHomeTimeline(maxId, 0, new TwitterPersistence.OnTimelineResults() {
            @Override
            public void onSuccess(ArrayList<Tweet> tweetList) {
                if (reset) {
                    tweets.clear();
                }
                tweets.addAll(tweetList);
                aTweets.notifyDataSetChanged();
            }

            @Override
            public void onFailure(String toastMsg) {
                Toast.makeText(TimelineActivity.this,toastMsg,Toast.LENGTH_LONG).show();
            }
        });

    }

    // launchers
    public void onComposeButton() {
        Log.d("DEBUG","onComposeButton");
        FragmentManager fm = getSupportFragmentManager();
        ComposeFragment fragment = ComposeFragment.newInstance("Compose");

        //fragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.Dialog_FullScreenSmallInset);

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
    public void onSendTweet(Tweet tweet) {
        if (tweet == null) {
            // display error
            Toast.makeText(this,"Could not post tweet",Toast.LENGTH_LONG).show();
        } else {
            // TODO: could be more efficient and fetch tweets and prepend
            // rather than refresh all.
            // show immediate result, then refresh.

            // TODO: scroll to top
            tweets.add(tweet);
            aTweets.notifyDataSetChanged();

            linearLayoutManager.scrollToPositionWithOffset(0, 0);

            Toast.makeText(this,"Sent Tweet!",Toast.LENGTH_LONG).show();
            Log.d("DEBUG","send tweet");

            populateTimeline(0, true);


        }
    }
}
