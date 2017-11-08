package com.jerbotron_mac.spotisave.runnables;

import android.util.Log;

import com.gracenote.gnsdk.GnDescriptor;
import com.gracenote.gnsdk.GnException;
import com.gracenote.gnsdk.GnLanguage;
import com.gracenote.gnsdk.GnLocale;
import com.gracenote.gnsdk.GnLocaleGroup;
import com.gracenote.gnsdk.GnRegion;
import com.gracenote.gnsdk.GnUser;

public class LocaleLoadRunnable implements Runnable {
    private GnLocaleGroup group;
    private GnLanguage language;
    private GnRegion region;
    private GnDescriptor descriptor;
    private GnUser user;


    public LocaleLoadRunnable(GnLocaleGroup group,
                              GnLanguage language,
                              GnRegion region,
                              GnDescriptor descriptor,
                              GnUser user) {
        this.group = group;
        this.language = language;
        this.region = region;
        this.descriptor = descriptor;
        this.user = user;
    }

    @Override
    public void run() {
        try {
            GnLocale locale = new GnLocale(group, language, region, descriptor, user);
            locale.setGroupDefault();
        } catch (GnException e) {
            Log.e(getClass().getName(), e.getMessage());
            e.printStackTrace();
        }
    }
}
