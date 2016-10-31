package com.codepath.apps.chirp.ui.timeline;

import android.content.Context;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.chirp.R;
import com.codepath.apps.chirp.models.Tweet;
import com.yqritc.scalablevideoview.ScalableVideoView;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by nick on 10/26/16.
 */

public class TweetsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Tweet> tweets;
    private Context mContext;

    public static int TWEET_TYPE_STANDARD = 0;
    public static int TWEET_TYPE_MEDIA = 1;

    // Define listener member variable
    private OnTweetsAdapterListener aaListener;
    public void setOnTweetsAdapterListener(OnTweetsAdapterListener listener) {
        this.aaListener = listener;
    }

    // Define the listener interface
    public interface OnTweetsAdapterListener {
        void onTweetClick(Tweet tweet);
    }

    // Pass in the tweets array into the constructor
    public TweetsAdapter(Context context, ArrayList<Tweet> tweetList) {
        tweets = tweetList;
        mContext = context;
    }

    // Easy access to the context object in the recyclerview
    private Context getContext() {
        return mContext;
    }

    // base class for our different article views that setups up the click listeners
    // and also provides a configure function
    public abstract class BaseTweetViewHolder extends RecyclerView.ViewHolder {

        public BaseTweetViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Triggers click upwards to the adapter on click
                    if (aaListener != null) {
                        int position = getAdapterPosition();
                        if (aaListener != null && position != RecyclerView.NO_POSITION) {
                            aaListener.onTweetClick(tweets.get(position));
                        }
                    }
                }
            });
        }

        public abstract void configure(Tweet tweet);
    }

    public class StandardTweetViewHolder extends BaseTweetViewHolder {
        @BindView(R.id.tvBody)
        TextView tvBody;

        @BindView(R.id.tvName)
        TextView tvName;

        @BindView(R.id.tvDisplayName)
        TextView tvDisplayName;

        @BindView(R.id.tvTimeAgo)
        TextView tvTimeAgo;

        @BindView(R.id.ivProfileImage)
        ImageView ivProfileImage;

        @BindView(R.id.ivPhoto)
        ImageView ivPhoto;

        @BindView(R.id.ivVideo)
        ScalableVideoView ivVideo;

        public StandardTweetViewHolder(View itemView) {
            super(itemView);
        }

        public void configure(Tweet tweet) {
            tvDisplayName.setText(tweet.getUser().getScreenName());
            tvBody.setText(tweet.getText());
            tvTimeAgo.setText(tweet.getTimeAgoCreatedAt());
            tvName.setText(tweet.getUser().getName());
            if (!TextUtils.isEmpty(tweet.getUser().getProfileImageUrl())) {
                Glide.with(mContext).load(tweet.getUser().getProfileImageUrl())
                        //.placeholder(R.id.?)
                        .fitCenter()
                        .into(ivProfileImage);
            }
            if (tweet.getMediaType() == Tweet.MEDIA_TYPE_PHOTO) {
                Glide.with(mContext).load(tweet.getMediaUrl())
                        //.placeholder(R.id.?)
                        .fitCenter()
                        .into(ivPhoto);
                ivPhoto.setVisibility(View.VISIBLE);
                ivVideo.setVisibility(View.GONE);
                tvBody.setBackgroundColor(Color.parseColor("#00ff00"));
            }
            else if ((tweet.getMediaType() == Tweet.MEDIA_TYPE_VIDEO) && (tweet.getVideoUrl() != null)){

                //new DownloadFileFromURL().execute(tweet.getVideoUrl());

                /*ivVideo.setVideoPath(tweet.getVideoUrl());
                MediaController mediaController = new MediaController(mContext);
                mediaController.setAnchorView(ivVideo);
                ivVideo.setMediaController(mediaController);
                ivVideo.requestFocus();
                ivVideo.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    // Close the progress bar and play the video
                    public void onPrepared(MediaPlayer mp) {
                        ivVideo.start();
                    }
                });*/
                Uri uri = Uri.parse(tweet.getVideoUrl());
                //new DownloadFileFromURL().execute("https://video.twimg.com/ext_tw_video/790898321124630528/pu/vid/634x360/7lICFv5qRDLZo92C.mp4");
                try {
                    ivVideo.setDataSource(mContext, uri);
                    ivVideo.setVolume(0, 0);
                    ivVideo.setLooping(true);
                    ivVideo.prepare(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            ivVideo.start();
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //mVideoView.setRawData(R.raw.landscape_sample);

                /*
                Uri url = Uri.parse(tweet.getVideoUrl());
                try {
                    ivVideo.setDataSource(mContext, url);
                    ivVideo.setLooping(true);
                    ivVideo.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }*/

                //ivVideo.setVideo(tweet.getVideoUrl());

                ivPhoto.setVisibility(View.GONE);
                ivVideo.setVisibility(View.VISIBLE);
                tvBody.setBackgroundColor(Color.parseColor("#ff0000"));
            } else {
                ivPhoto.setVisibility(View.GONE);
                ivVideo.setVisibility(View.GONE);
                tvBody.setBackgroundColor(Color.parseColor("#0000ff"));
            }
        }
    }


    /**
     * Background Async Task to download file
     * */
    class DownloadFileFromURL extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Bar Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        /**
         * Downloading file in background thread
         * */
        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                URL url = new URL(f_url[0]);
                URLConnection connection = url.openConnection();
                connection.connect();

                // this will be useful so that you can show a tipical 0-100%
                // progress bar
                int lengthOfFile = connection.getContentLength();

                // download the file
                InputStream input = new BufferedInputStream(url.openStream(),
                        8192);

                // Output stream
                OutputStream output = new FileOutputStream(Environment
                        .getExternalStorageDirectory().toString()
                        + "/2011.kml");

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    //publishProgress("" + (int) ((total * 100) / lengthOfFile));

                    // writing data to file
                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }

            return null;
        }

        /**
         * Updating progress bar
         * */
        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            //pDialog.setProgress(Integer.parseInt(progress[0]));
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        @Override
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after the file was downloaded
            //dismissDialog(progress_bar_type);

        }

    }

    public class MediaTweetViewHolder extends BaseTweetViewHolder {
        //@BindView(R.id.tvTitle)
        //TextView tvTitle;

        public MediaTweetViewHolder(View itemView) {
            super(itemView);
        }

        public void configure(Tweet tweet) {
            //tvTitle.setText(article.getHeadline());
            //if (!TextUtils.isEmpty(article.getThumbnail())) {
            //    Glide.with(getContext()).load(article.getThumbnail()).into(ivThumbnail);
            //}
        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        RecyclerView.ViewHolder viewHolder;

        // Inflate the custom layout
        if (viewType == TWEET_TYPE_STANDARD) {
            View standardTweetResult = inflater.inflate(R.layout.item_tweet_standard, parent, false);
            viewHolder = new StandardTweetViewHolder(standardTweetResult);
        }
        else { //if (viewType == TWEET_TYPE_MEDIA) {
            View mediaTweetResult = inflater.inflate(R.layout.item_tweet_media, parent, false);
            viewHolder = new MediaTweetViewHolder(mediaTweetResult);
        }

        // Return a new holder instance
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        // simply get the tweet and call the base class configure for said article
        Tweet tweet = tweets.get(position);
        ((BaseTweetViewHolder) holder).configure(tweet);
    }

    @Override
    public int getItemViewType(int position) {
        Tweet tweet = tweets.get(position);
        return TWEET_TYPE_STANDARD;
    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }
}
