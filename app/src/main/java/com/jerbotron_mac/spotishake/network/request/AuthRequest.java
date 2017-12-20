package com.jerbotron_mac.spotishake.network.request;

import com.jerbotron_mac.spotishake.shared.AppConstants;

public class AuthRequest {

    String grantType = "authorization_code";
    String code;
    String redirectUri;
}
