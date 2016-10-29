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

    public static final String FAVORITE_COUNT = "favorite_count";
    public static final String RETWEET_COUNT = "retweet_count";

    private long uid;
    private String text;
    private String createdAt;
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

            // drill down for memdia
            if (jsonObject.has("extended_entities")) {
                JSONObject jsonExtendedEntitiesObject = jsonObject.getJSONObject("extended_entities");

                if (jsonExtendedEntitiesObject.has("media")) {
                    JSONArray jsonMediaArray = jsonExtendedEntitiesObject.getJSONArray("media");

                    if (jsonMediaArray.length() > 0) {
                        JSONObject jsonMediaObject = jsonMediaArray.getJSONObject(0);

                        if (jsonMediaObject.has("media_url")) {

                        }
/*
                        if (jsonMediaObject != null && jsonMediaObject.has(VIDEO_INFO)) {
                            tweet.setMedia_type(jsonMediaObject.get(TYPE).getAsString());
                            JsonObject jsonVideoInfoObject = jsonMediaObject.get(VIDEO_INFO)
                                    .getAsJsonObject();

                            if (jsonVideoInfoObject != null && jsonVideoInfoObject.has(VARIANTS)) {
                                JsonArray jsonVariantsArray = jsonVideoInfoObject.get(VARIANTS)
                                        .getAsJsonArray();

                                if (jsonVariantsArray != null && jsonVariantsArray.size() > 0) {

                                    for (int i = 0; i < jsonVariantsArray.size() ; i++) {
                                        JsonObject jsonVariantObject = jsonVariantsArray.get(i)
                                                .getAsJsonObject();

                                        if (jsonVariantObject != null &&
                                                jsonVariantObject.has(CONTENT_TYPE)) {
                                            if (jsonVariantObject.get(CONTENT_TYPE).getAsString()
                                                    .equals(VIDEO_MP4)){
                                                tweet.setVideo_url(jsonVariantObject.get(URL)
                                                        .getAsString());
                                                tweet.setMedia_content_type(
                                                        jsonVariantObject.get(CONTENT_TYPE)
                                                                .getAsString());
                                            }
                                        }
                                    }
                                }
                            }
                        }*/
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
