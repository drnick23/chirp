package com.codepath.apps.chirp.ui.compose;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.chirp.R;
import com.codepath.apps.chirp.TwitterApplication;
import com.codepath.apps.chirp.models.Tweet;
import com.codepath.apps.chirp.network.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnComposeListener} interface
 * to handle interaction events.
 * Use the {@link ComposeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ComposeFragment extends DialogFragment implements TextWatcher {

    @BindView(R.id.tvName)
    TextView tvName;

    @BindView(R.id.ivProfileImage)
    ImageView ivProfileImage;

    @BindView(R.id.tvScreenName)
    TextView tvScreenName;

    @BindView(R.id.etBody)
    EditText etBody;

    @BindView(R.id.tvCountRemaining)
    TextView tvCountRemaining;

    @BindView(R.id.btSend)
    Button btSend;

    private String title;

    private OnComposeListener mListener;

    public ComposeFragment() {
        // Required empty public constructor
    }

    public static ComposeFragment newInstance(String title) {
        ComposeFragment fragment = new ComposeFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_compose, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        // Fetch arguments from bundle and set title
        String title = getArguments().getString("title", "Search Filters");
        getDialog().setTitle(title);

        // TODO: use the actual logged in user
        tvName.setText("@drnicolas23");
        tvScreenName.setText("Nicolas Halper");
        Glide.with(this).load("https://pbs.twimg.com/profile_images/449220684317609986/yfCBIt8t_400x400.png")
                //.placeholder(R.id.?)
                .fitCenter()
                .into(ivProfileImage);

        etBody.addTextChangedListener(this);

    }

    @OnClick(R.id.btSend)
    public void onSendButton() {

        String body = etBody.getText().toString();
        TwitterClient client = TwitterApplication.getRestClient();

        client.postUpdateStatus(body, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Tweet tweet = Tweet.fromJSON(response);
                mListener.onSendTweet(tweet);
                dismiss();

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                // listener should actually handle error...
                mListener.onSendTweet(null);
                dismiss();
            }

        });

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnComposeListener) {
            mListener = (OnComposeListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnComposeListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onResume() {
        // Get existing layout params for the window
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        // Assign window properties to fill the parent
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
        // Call super onResume after sizing
        super.onResume();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnComposeListener {
        // TODO: Update argument type and name
        void onSendTweet(Tweet tweet);
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        long currentLength = charSequence.length();
        long charactersLeft = 140 - currentLength;

        if (charactersLeft < 0) {
            etBody.setTextColor(Color.RED);
            btSend.setEnabled(false);
        } else {
            etBody.setTextColor(Color.BLACK);
            btSend.setEnabled(true);
        }
        tvCountRemaining.setText("" + charactersLeft);
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }
}
