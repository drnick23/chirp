package com.codepath.apps.chirp.models;

import android.text.format.DateUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by nick on 10/26/16.
 */

@Parcel
public class Tweet {

    public static final String CREATED_AT = "created_at";
    public static final String ID = "id";
    public static final String TEXT = "text";
    public static final String USER = "user";
    public static final int MEDIA_TYPE_PHOTO = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    public static final String FAVORITE_COUNT = "favorite_count";
    public static final String RETWEET_COUNT = "retweet_count";

    private long uid;
    private String text;
    private int mediaType = 0;
    private String createdAt;
    private String mediaUrl;
    private User user;
    private int favoriteCount = 0;
    private int retweetCount = 0;

    public Tweet() {}

    public long getUid() {
        return uid;
    }

    public String getText() {
        return text;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getTimeAgoCreatedAt() {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(createdAt).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                        System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return relativeDate;
    }

    public User getUser() {
        return user;
    }

    public int getMediaType() {
        return mediaType;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public int getFavoriteCount() {
        return favoriteCount;
    }

    public int getRetweetCount() {
        return retweetCount;
    }

    public static Tweet fromJSON(JSONObject jsonObject) {
        Tweet tweet = new Tweet();

        try {
            tweet.text = jsonObject.getString(TEXT);
            tweet.uid = jsonObject.getLong(ID);
            tweet.createdAt = jsonObject.getString(CREATED_AT);
            tweet.user = User.fromJSON(jsonObject.getJSONObject(USER));

            if (jsonObject.has(RETWEET_COUNT)) {
                tweet.retweetCount = jsonObject.getInt(RETWEET_COUNT);
            }
            if (jsonObject.has(FAVORITE_COUNT)) {
                tweet.retweetCount = jsonObject.getInt(FAVORITE_COUNT);
            }

            // drill down for media
            if (jsonObject.has("extended_entities")) {
                JSONObject jsonExtendedEntitiesObject = jsonObject.getJSONObject("extended_entities");

                if (jsonExtendedEntitiesObject.has("media")) {
                    JSONArray jsonMediaArray = jsonExtendedEntitiesObject.getJSONArray("media");

                    if (jsonMediaArray.length() > 0) {
                        JSONObject jsonMediaObject = jsonMediaArray.getJSONObject(0);

                        if (jsonMediaObject.has("type") && jsonMediaObject.has("media_url")) {
                            String type = jsonMediaObject.getString("type");
                            if (type.equals("photo")) {
                                tweet.mediaType = MEDIA_TYPE_PHOTO;

                            }
                            else if (type.equals("video")) {
                                tweet.mediaType = MEDIA_TYPE_VIDEO;
                            }
                            tweet.mediaUrl = jsonMediaObject.getString("media_url");
                        }


                    }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return tweet;
    }

    public static ArrayList<Tweet> fromJSONArray(JSONArray jsonArray) {
        ArrayList<Tweet> tweets = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject tweetJson = null;
            try {
                tweetJson = jsonArray.getJSONObject(i);
                Tweet tweet = Tweet.fromJSON(tweetJson);
                if (tweet != null) {
                    tweets.add(tweet);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                // if a tweet doesn't work, continue through the others
                continue;
            }

        }
        return tweets;
    }
}
