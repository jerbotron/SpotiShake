package com.jerbotron_mac.spotishake.network.requests;

import com.jerbotron_mac.spotishake.shared.AppConstants;

public class AuthRequest {
    String grantType = AppConstants.GRANT_TYPE;
    String redirectUri = AppConstants.REDIRECT_URI;
    String code;

    public AuthRequest(String code) {
        this .code = code;
    }
}
