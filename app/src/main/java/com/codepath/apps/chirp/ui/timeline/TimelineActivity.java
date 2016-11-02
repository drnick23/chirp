package com.codepath.apps.chirp.ui.timeline;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.codepath.apps.chirp.R;
import com.codepath.apps.chirp.fragments.HomeTimelineFragment;
import com.codepath.apps.chirp.models.Tweet;
import com.codepath.apps.chirp.ui.compose.ComposeFragment;
import com.codepath.apps.chirp.ui.detail.DetailActivity;

import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TimelineActivity extends AppCompatActivity implements TweetsAdapter.OnTweetsAdapterListener, ComposeFragment.OnComposeListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.abCompose)
    FloatingActionButton abCompose;

    private HomeTimelineFragment fragmentTweetsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Timeline");

        if (savedInstanceState == null) {
            fragmentTweetsList = (HomeTimelineFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_timeline);
        }

        abCompose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onComposeButton();
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
            fragmentTweetsList.addToTop(tweet);
            /*tweets.add(tweet);
            aTweets.notifyDataSetChanged();

            linearLayoutManager.scrollToPositionWithOffset(0, 0);



            populateTimeline(0, true);*/

            Toast.makeText(this,"Sent Tweet!",Toast.LENGTH_LONG).show();
            Log.d("DEBUG","send tweet");
        }
    }
}
