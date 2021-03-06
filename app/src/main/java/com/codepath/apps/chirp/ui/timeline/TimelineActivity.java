package com.codepath.apps.chirp.ui.timeline;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.codepath.apps.chirp.R;
import com.codepath.apps.chirp.models.Tweet;
import com.codepath.apps.chirp.ui.compose.ComposeFragment;
import com.codepath.apps.chirp.ui.profile.ProfileActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TimelineActivity extends AppCompatActivity implements ComposeFragment.OnComposeListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.abCompose)
    FloatingActionButton abCompose;

    private TimelineFragmentPagerAdapter timelineFragmentPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Timeline");

        //if (savedInstanceState == null) {
        //    fragmentTweetsList = (HomeTimelineFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_timeline);
        //}

        // Get the ViewPager and set it's PagerAdapter so that it can display items
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        timelineFragmentPagerAdapter = new TimelineFragmentPagerAdapter(getSupportFragmentManager(),
                TimelineActivity.this);
        viewPager.setAdapter(timelineFragmentPagerAdapter);

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);

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
            //fragmentTweetsList.addToTop(tweet);
            timelineFragmentPagerAdapter.userSentTweet(tweet);

            Toast.makeText(this,"Sent Tweet!",Toast.LENGTH_LONG).show();
            Log.d("DEBUG","send tweet");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_timeline, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (item.getItemId()) {
            case R.id.miProfile:
                // launch the filter settings activity
                Log.d("DEBUG","TODO: launch filter settings");
                launchProfileActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    void launchProfileActivity() {
        Log.d("DEBUG","launch profile");
        Intent i = new Intent(this, ProfileActivity.class);
        startActivity(i);
    }
}
