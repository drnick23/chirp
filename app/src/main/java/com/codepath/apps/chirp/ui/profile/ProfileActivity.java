package com.codepath.apps.chirp.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.chirp.R;
import com.codepath.apps.chirp.TwitterApplication;
import com.codepath.apps.chirp.models.Tweet;
import com.codepath.apps.chirp.ui.detail.DetailActivity;
import com.codepath.apps.chirp.ui.timeline.TweetsAdapter;
import com.codepath.apps.chirp.ui.timeline.fragments.UserTimelineFragment;
import com.codepath.apps.chirp.models.User;
import com.codepath.apps.chirp.network.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;
import org.parceler.Parcels;

import cz.msebera.android.httpclient.Header;


public class ProfileActivity extends AppCompatActivity implements TweetsAdapter.OnTweetsAdapterListener {

    TwitterClient client;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // get the screen name from activity
        final String screenName = getIntent().getStringExtra("screen_name");
        getSupportActionBar().setTitle(screenName);

        if (savedInstanceState == null) {
            // create the user timeline fragment
            UserTimelineFragment fragmentUserTimeline = UserTimelineFragment.newInstance(screenName);

            // display the user fragment dynamically
            // insert framelayout into timeline activty then insert user timeline
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.flContainer, fragmentUserTimeline);
            ft.commit();
        }

        client = TwitterApplication.getRestClient();
        client.getUserInfo(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                user = User.fromJSON(response);
                getSupportActionBar().setTitle(user.getScreenName());
                setupProfileHeader(user);
            }

        });

    }

    private void setupProfileHeader(User user) {
        TextView tvName = (TextView) findViewById(R.id.tvName);
        TextView tagLine = (TextView) findViewById(R.id.tvTagLine);
        TextView tvFollowers = (TextView) findViewById(R.id.tvFollowers);
        TextView tvFollowing = (TextView) findViewById(R.id.tvFollowing);
        ImageView ivProfileImage = (ImageView) findViewById(R.id.ivProfileImage);

        tvName.setText(user.getName());
        tagLine.setText(user.getDescription());
        tvFollowers.setText(user.getFollowersCount()+" Followers");
        tvFollowing.setText(user.getFollowingCount()+" Following");

        Glide.with(this).load(user.getProfileImageUrl())
                .fitCenter()
                .into(ivProfileImage);

    }

    @Override
    public void onTweetClick(Tweet tweet) {
        Log.d("DEBUG","CLICKED TWEET");
        Intent i = new Intent(getApplicationContext(), DetailActivity.class);
        i.putExtra("tweet", Parcels.wrap(tweet));
        startActivity(i);
    }

    @Override
    public void onTweetProfileImageClick(Tweet tweet) {
        Log.d("DEBUG","CLICKED TWEET PROFILE IMAGE");
        Intent i = new Intent(getApplicationContext(), ProfileActivity.class);
        i.putExtra("user", Parcels.wrap(tweet.getUser()));
        startActivity(i);
    }

}
