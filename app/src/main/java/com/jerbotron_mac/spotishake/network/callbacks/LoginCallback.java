package com.jerbotron_mac.spotishake.network.callbacks;

import android.app.Activity;

import com.jerbotron_mac.spotishake.shared.AppConstants;
import com.jerbotron_mac.spotishake.utils.SharedUserPrefs;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

import kaaes.spotify.webapi.android.SpotifyCallback;
import kaaes.spotify.webapi.android.SpotifyError;
import kaaes.spotify.webapi.android.models.ErrorDetails;
import kaaes.spotify.webapi.android.models.UserPrivate;
import retrofit.client.Response;

import static com.jerbotron_mac.spotishake.shared.AppConstants.CLIENT_ID;
import static com.jerbotron_mac.spotishake.shared.AppConstants.REDIRECT_URI;
import static com.jerbotron_mac.spotishake.shared.AppConstants.SETTINGS_PREF_REQUEST_CODE;

public class LoginCallback extends SpotifyCallback<UserPrivate> {

    private static final int ACCESS_EXPIRATION_STATUS_CODE = 401;
    private static final String ACCESS_EXPIRATION_MSG = "The access token expired";

    private Activity activity;
    private SharedUserPrefs sharedUserPrefs;

    public LoginCallback(Activity activity, SharedUserPrefs sharedUserPrefs) {
        this.activity = activity;
        this.sharedUserPrefs = sharedUserPrefs;
    }

    @Override
    public void failure(SpotifyError spotifyError) {
        spotifyError.printStackTrace();
        ErrorDetails errorDetails = spotifyError.getErrorDetails();
        if (errorDetails != null &&
                errorDetails.status == ACCESS_EXPIRATION_STATUS_CODE &&
                errorDetails.message.equals(ACCESS_EXPIRATION_MSG)) {
            openLoginWindow();
        }
    }

    @Override
    public void success(UserPrivate userPrivate, Response response) {
        sharedUserPrefs.saveUserInfo(userPrivate);
    }

    private void openLoginWindow() {
        // request AuthenticationResponse.Type.CODE to do refresh token/auth flow
        final AuthenticationRequest request = new AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI)
                .setScopes(AppConstants.SPOTIFY_CLIENT_SCOPES)
                .build();
        AuthenticationClient.openLoginActivity(activity, SETTINGS_PREF_REQUEST_CODE, request);
    }
}
