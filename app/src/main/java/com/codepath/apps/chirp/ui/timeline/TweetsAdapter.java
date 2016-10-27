package com.codepath.apps.chirp.ui.timeline;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.codepath.apps.chirp.R;
import com.codepath.apps.chirp.models.Tweet;

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

        @BindView(R.id.tvDisplayName)
        TextView tvDisplayName;

        @BindView(R.id.tvTimeAgo)
        TextView tvTimeAgo;
        //@BindView(R.id.ivThumbnail)
        //ImageView ivThumbnail;

        public StandardTweetViewHolder(View itemView) {
            super(itemView);
        }

        public void configure(Tweet tweet) {
            tvDisplayName.setText(tweet.getUser().getScreenName());
            tvBody.setText(tweet.getBody());
            tvTimeAgo.setText(tweet.getTimeAgoCreatedAt());
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
