package com.codepath.apps.chirp.ui.detail;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.codepath.apps.chirp.R;
import com.codepath.apps.chirp.models.Tweet;

import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.ivProfileImage)
    ImageView ivProfileImage;

    @BindView(R.id.tvName)
    TextView tvName;

    @BindView(R.id.tvScreenName)
    TextView tvScreenName;

    @BindView(R.id.tvBody)
    TextView tvBody;

    @BindView(R.id.ivPhoto)
    ImageView ivPhoto;

    @BindView(R.id.ivVideo)
    VideoView ivVideo;

    @BindView(R.id.tvTimeAgo)
    TextView tvTimeAgo;

    Tweet tweet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Detail");

        tweet = (Tweet) Parcels.unwrap(getIntent().getParcelableExtra("tweet"));
        Log.d("DEBUG",tweet.toString());

        tvScreenName.setText(tweet.getUser().getScreenName());
        tvBody.setText(tweet.getText());
        tvTimeAgo.setText(tweet.getTimeAgoCreatedAt());
        tvName.setText(tweet.getUser().getName());
        if (!TextUtils.isEmpty(tweet.getUser().getProfileImageUrl())) {
            Glide.with(this).load(tweet.getUser().getProfileImageUrl())
                    //.placeholder(R.id.?)
                    .fitCenter()
                    .into(ivProfileImage);
        }
        if (tweet.getMediaType() == Tweet.MEDIA_TYPE_PHOTO) {
            Glide.with(this).load(tweet.getMediaUrl())
                    //.placeholder(R.id.?)
                    .fitCenter()
                    .into(ivPhoto);
            ivPhoto.setVisibility(View.VISIBLE);
            ivVideo.setVisibility(View.GONE);
            //tvBody.setBackgroundColor(Color.parseColor("#00ff00"));
        }
        else if ((tweet.getMediaType() == Tweet.MEDIA_TYPE_VIDEO) && (tweet.getVideoUrl() != null)){
            ivVideo.setVideoPath(tweet.getVideoUrl());
            MediaController mediaController = new MediaController(this);
            mediaController.setAnchorView(ivVideo);
            ivVideo.setMediaController(mediaController);
            //ivVideo.setMediaController(null);
            ivVideo.requestFocus();
            ivVideo.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                // Close the progress bar and play the video
                public void onPrepared(MediaPlayer mp) {
                    ivVideo.start();
                }
            });
            ivPhoto.setVisibility(View.GONE);
            ivVideo.setVisibility(View.VISIBLE);
            //tvBody.setBackgroundColor(Color.parseColor("#ff0000"));
        } else {
            ivPhoto.setVisibility(View.GONE);
            ivVideo.setVisibility(View.GONE);
            //tvBody.setBackgroundColor(Color.parseColor("#0000ff"));
        }
    }

}
