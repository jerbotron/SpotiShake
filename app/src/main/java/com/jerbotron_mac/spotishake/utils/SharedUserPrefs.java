package com.jerbotron_mac.spotishake.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.jerbotron_mac.spotishake.network.responses.AuthResponse;

import kaaes.spotify.webapi.android.models.UserPrivate;

public class SharedUserPrefs {

    private static final String SHARED_PREFS_NAME = "shared_user_prefs";

    // Keys
    private static final String REFRESH_TOKEN = "refresh_token";
    private static final String ACCESS_TOKEN = "access_token";
    private static final String ACCESSS_TOKEN_TTL = "access_token_ttl";
    private static final String ACCESS_CODE = "access_code";
    private static final String USER_PROFILE_IMAGE_URL = "user_profile_image_url";
    private static final String USER_DISPLAY_NAME  = "user_display_name";

    private static final String AUTO_SAVE_PREF = "auto_save_pref";

    private SharedPreferences sharedPrefs;

    public SharedUserPrefs(Context context) {
        this.sharedPrefs = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void setRefreshToken(String refreshToken) {
        sharedPrefs.edit().putString(REFRESH_TOKEN, refreshToken).apply();
    }

    public String getRefreshToken() {
        return sharedPrefs.getString(REFRESH_TOKEN, null);
    }

    public void setAccesssTokenTtl(int ttl) {
        sharedPrefs.edit().putInt(ACCESSS_TOKEN_TTL, ttl).apply();
    }

    public int getAccessTokenTtl() {
        return sharedPrefs.getInt(ACCESSS_TOKEN_TTL, 0);
    }

    public void setAccessCode(String code) {
        sharedPrefs.edit().putString(ACCESS_CODE, code).apply();
    }

    public String getAccessCode() {
        return sharedPrefs.getString(ACCESS_CODE, null);
    }

    public void setAccessToken(String authToken) {
        sharedPrefs.edit().putString(ACCESS_TOKEN, authToken).apply();
    }

    public String getAccessToken() {
        return sharedPrefs.getString(ACCESS_TOKEN, null);
    }

    public void setUserProfileImageUrl(String url) {
        sharedPrefs.edit().putString(USER_PROFILE_IMAGE_URL, url).apply();
    }

    public String getUserProfileImageUrl() {
        return sharedPrefs.getString(USER_PROFILE_IMAGE_URL, null);
    }

    public void setUserDisplayName(String displayName) {
        sharedPrefs.edit().putString(USER_DISPLAY_NAME, displayName).apply();
    }

    public String getUserDisplayName() {
        return sharedPrefs.getString(USER_DISPLAY_NAME, null);
    }

    public void setAutoSavePref(boolean autoSave) {
        sharedPrefs.edit().putBoolean(AUTO_SAVE_PREF, autoSave).apply();
    }

    public boolean getAutoSavePref() {
        return sharedPrefs.getBoolean(AUTO_SAVE_PREF, true);
    }


    public void saveUserInfo(UserPrivate userPrivate) {
        if (userPrivate.images != null && !userPrivate.images.isEmpty()) {
            setUserProfileImageUrl(userPrivate.images.get(0).url);
        }
        setUserDisplayName(userPrivate.display_name);
    }

    public void saveAuthData(AuthResponse authResponse) {

    }

    public void clearUserData() {
        setAccessToken(null);
        setUserProfileImageUrl(null);
        setUserDisplayName(null);
        setAccessCode(null);
        setRefreshToken(null);
    }

    public boolean isUserLoggedIn() {
        String spotifyAuthToken = getAccessToken();
        return spotifyAuthToken != null && !spotifyAuthToken.equals("");
    }
}
