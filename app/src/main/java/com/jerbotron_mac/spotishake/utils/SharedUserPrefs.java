package com.jerbotron_mac.spotishake.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import kaaes.spotify.webapi.android.models.UserPrivate;

public class SharedUserPrefs {

    private static final String SHARED_PREFS_NAME = "shared_user_prefs";

    // Keys
    private static final String SPOTIFY_AUTH_TOKEN = "spotify_auth_token";
    private static final String USER_PROFILE_IMAGE_URL = "user_profile_image_url";
    private static final String USER_DISPLAY_NAME  = "user_display_name";

    SharedPreferences sharedPrefs;

    public SharedUserPrefs(Context context) {
        this.sharedPrefs = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void setSpotifyAuthToken(String authToken) {
        sharedPrefs.edit().putString(SPOTIFY_AUTH_TOKEN, authToken).apply();
        Log.d("JWDEBUG", "token = " + authToken);
    }

    public String getSpotifyAuthToken() {
        return sharedPrefs.getString(SPOTIFY_AUTH_TOKEN, null);
    }

    public void setUserProfileImageUrl(String url) {
        sharedPrefs.edit().putString(USER_PROFILE_IMAGE_URL, url).apply();
    }

    public void setUserDisplayName(String displayName) {
        sharedPrefs.edit().putString(USER_DISPLAY_NAME, displayName).apply();
    }

    public String getUserProfileImageUrl() {
        return sharedPrefs.getString(USER_PROFILE_IMAGE_URL, null);
    }

    public String getUserDisplayName() {
        return sharedPrefs.getString(USER_DISPLAY_NAME, null);
    }

    public void saveUserInfo(UserPrivate userPrivate) {
        if (userPrivate.images != null && !userPrivate.images.isEmpty()) {
            setUserProfileImageUrl(userPrivate.images.get(0).url);
        }
        setUserDisplayName(userPrivate.display_name);
    }

    public void clearUserData() {
        setSpotifyAuthToken(null);
        setUserProfileImageUrl(null);
        setUserDisplayName(null);
    }

    public boolean isUserLoggedIn() {
        String spotifyAuthToken = getSpotifyAuthToken();
        return spotifyAuthToken != null && !spotifyAuthToken.equals("");
    }
}
