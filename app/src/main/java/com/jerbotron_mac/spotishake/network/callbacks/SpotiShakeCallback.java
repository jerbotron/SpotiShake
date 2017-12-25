package com.jerbotron_mac.spotishake.network.callbacks;

import kaaes.spotify.webapi.android.SpotifyCallback;
import kaaes.spotify.webapi.android.SpotifyError;
import retrofit.RetrofitError;

public abstract class SpotiShakeCallback extends SpotifyCallback {
    @Override
    public void failure(SpotifyError spotifyError) {
        RetrofitError retrofitError = spotifyError.getRetrofitError();
        if (retrofitError != null ) {

        }
    }
}
