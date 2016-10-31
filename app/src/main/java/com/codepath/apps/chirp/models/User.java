package com.codepath.apps.chirp.models;

import com.codepath.apps.chirp.db.MyDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

/**
 * Created by nick on 10/26/16.
 */
@Table(database = MyDatabase.class)
@Parcel(analyze={User.class})
public class User extends BaseModel {
    @PrimaryKey
    @Column
    long uid;

    @Column
    String name;

    @Column
    String screenName;

    @Column
    String profileImageUrl;

    public User() {}

    public static User fromJSON(JSONObject jsonObject) {
        User user = new User();
        try {
            user.name = jsonObject.getString("name");
            user.uid = jsonObject.getLong("id");
            user.screenName = jsonObject.getString("screen_name");
            user.profileImageUrl = jsonObject.getString("profile_image_url");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return user;
    }

    public String getName() {
        return name;
    }

    public long getUid() {
        return uid;
    }

    public String getScreenName() {
        return screenName;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }
}
