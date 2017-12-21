package com.jerbotron_mac.spotishake.network.subscribers;

import com.jerbotron_mac.spotishake.network.responses.AuthResponse;
import com.jerbotron_mac.spotishake.utils.SharedUserPrefs;

import io.reactivex.observers.DisposableObserver;

public class AuthTokenSubscriber extends DisposableObserver<AuthResponse> {

    private SharedUserPrefs sharedUserPrefs;

    public AuthTokenSubscriber(SharedUserPrefs sharedUserPrefs) {
        this.sharedUserPrefs = sharedUserPrefs;
    }


    @Override
    public void onNext(AuthResponse authResponse) {
        sharedUserPrefs.saveAuthData(authResponse);
    }

    @Override
    public void onError(Throwable e) {
        e.printStackTrace();
    }

    @Override
    public void onComplete() {

    }
}
