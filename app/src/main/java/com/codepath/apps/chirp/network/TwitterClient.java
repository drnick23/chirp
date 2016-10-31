package com.codepath.apps.chirp.network;

import android.content.Context;
import android.util.Log;

import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.TwitterApi;

/*
 * 
 * This is the object responsible for communicating with a REST API. 
 * Specify the constants below to change the API being communicated with.
 * See a full list of supported API classes: 
 *   https://github.com/fernandezpablo85/scribe-java/tree/master/src/main/java/org/scribe/builder/api
 * Key and Secret are provided by the developer site for the given API i.e dev.twitter.com
 * Add methods for each relevant endpoint in the API.
 * 
 * NOTE: You may want to rename this object based on the service i.e TwitterClient or FlickrClient
 * 
 */
public class TwitterClient extends OAuthBaseClient {
	public static final Class<? extends Api> REST_API_CLASS = TwitterApi.class; // Change this
	public static final String REST_URL = "https://api.twitter.com/1.1"; // Change this, base API URL
	public static final String REST_CONSUMER_KEY = "7StXmIWWY41AlPejp7wpg";       // Change this
	public static final String REST_CONSUMER_SECRET = "B519mICav67cqWDWNzXarmArBANwf9W749zx28"; // Change this
	public static final String REST_CALLBACK_URL = "oauth://cpchirp"; // Change this (here and in manifest)

	private static int COUNT_PER_PAGE = 25;

	public TwitterClient(Context context) {
		super(context, REST_API_CLASS, REST_URL, REST_CONSUMER_KEY, REST_CONSUMER_SECRET, REST_CALLBACK_URL);
	}

	// starts at page 0
	public void getHomeTimeline(long maxId, AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/home_timeline.json");
		// Can specify query string params directly or through RequestParams.
		RequestParams params = new RequestParams();
		params.put("count", COUNT_PER_PAGE);
		if (maxId > 0) {
			params.put("max_id", maxId);
		}
		getClient().get(apiUrl, params, handler);
	}

	public void getUser(AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("account/verify_credentials.json");
		// Can specify query string params directly or through RequestParams.
		RequestParams params = new RequestParams();
		getClient().get(apiUrl, params, handler);
	}

	public void postUpdateStatus(String status, AsyncHttpResponseHandler handler) {
		Log.d("DEBUG", "updateStatus: " + status);

		String apiUrl = getApiUrl("statuses/update.json");
		RequestParams params = new RequestParams();
		params.put("status", status);

		client.post(apiUrl, params, handler);
	}

	public void postUpdateReply(String status, String id, AsyncHttpResponseHandler handler) {
		Log.d("DEBUG", "updateReply: " + status);

		String apiUrl = getApiUrl("statuses/update.json");
		RequestParams params = new RequestParams();
		params.put("status", status);
		params.put("in_reply_to_status_id", id);

		client.post(apiUrl, params, handler);
	}
}