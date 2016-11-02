package com.codepath.apps.chirp.network;

import android.util.Log;

import com.codepath.apps.chirp.TwitterApplication;
import com.codepath.apps.chirp.models.Tweet;
import com.codepath.apps.chirp.models.Tweet_Table;
import com.codepath.apps.chirp.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by nick on 10/30/16.
 */

// db wrapper for twitter client
public class TwitterPersistence {

    private static final TwitterPersistence instance = new TwitterPersistence();

    //private constructor to avoid client applications to use constructor
    private TwitterPersistence(){}

    public static TwitterPersistence getInstance(){
        return instance;
    }

    public void getMentionsTimeline(long maxId, int sinceId, final OnTimelineResults results) {
        TwitterClient client = TwitterApplication.getRestClient();

        /*
        // if neither maxId or sinceId are specified and we have no network, see if we have results from disk
        if ((maxId == 0) && (sinceId == 0) && !NetworkUtils.isOnline()) {
            ArrayList<Tweet> tweets = loadHomeTimelineFromDisk(maxId);
            if (tweets.size() > 0) {
                // if we have disk results, return those already.
                Log.d("DEBUG", "Returned " + (tweets.size()) + " results from disk");
                results.onSuccess(tweets);
                return;
            }
        }*/

        if (!NetworkUtils.isOnline()) {
            results.onFailure("Offline mode. Connect internet for more.");
            return;
        }

        client.getMentionsTimeline(maxId, sinceId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.d("DEBUG",response.toString());
                // after we get our list of tweets, find the oldest one and remember that
                // in case we need to fetch again.
                ArrayList<Tweet> tweets = Tweet.fromJSONArray(response);
                saveHomeTimelineToDisk(tweets);
                results.onSuccess(tweets);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", errorResponse.toString());
                results.onFailure(errorResponse.toString());
            }
        });
    }

    public interface OnTimelineResults {
        void onSuccess(ArrayList<Tweet> tweetList);
        void onFailure(String toastMsg);
    }

    private ArrayList<Tweet> loadHomeTimelineFromDisk(long maxId) {
        List<Tweet> tweetList = SQLite.select().from(Tweet.class).where(Tweet_Table.uid.greaterThanOrEq(maxId)).orderBy(Tweet_Table.createdAt,false).queryList();
        ArrayList<Tweet> tweets = new ArrayList<>();
        int length = tweetList.size();
        for (int i=0;i<length;i++) {
            tweets.add(tweetList.get(i));
        }
        return tweets;
    }

    private void saveHomeTimelineToDisk(ArrayList<Tweet> tweets) {
        // save all tweets
        int length = tweets.size();
        for (int i=0;i<length;i++) {
            tweets.get(i).save();
        }
    }

    public void flushTimelineToDisk(ArrayList<Tweet> tweets) {
        SQLite.delete().from(User.class).execute();
        SQLite.delete().from(Tweet.class).execute();
        saveHomeTimelineToDisk(tweets);
    }

    // starts at page 0
    public void getHomeTimeline(long maxId, long sinceId, final OnTimelineResults results) {
        TwitterClient client = TwitterApplication.getRestClient();


        // if neither maxId or sinceId are specified and we have no network, see if we have results from disk
        if ((maxId == 0) && (sinceId == 0) && !NetworkUtils.isOnline()) {
            ArrayList<Tweet> tweets = loadHomeTimelineFromDisk(maxId);
            if (tweets.size() > 0) {
                // if we have disk results, return those already.
                Log.d("DEBUG", "Returned " + (tweets.size()) + " results from disk");
                results.onSuccess(tweets);
                return;
            }
        }

        if (!NetworkUtils.isOnline()) {
            results.onFailure("Offline mode. Connect internet for more.");
            return;
        }

        client.getHomeTimeline(maxId, sinceId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.d("DEBUG",response.toString());
                // after we get our list of tweets, find the oldest one and remember that
                // in case we need to fetch again.
                ArrayList<Tweet> tweets = Tweet.fromJSONArray(response);
                saveHomeTimelineToDisk(tweets);
                results.onSuccess(tweets);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", errorResponse.toString());
                results.onFailure(errorResponse.toString());
            }
        });
    }

}
