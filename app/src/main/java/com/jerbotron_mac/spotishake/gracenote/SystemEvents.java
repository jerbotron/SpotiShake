package com.jerbotron_mac.spotishake.gracenote;

import android.util.Log;

import com.gracenote.gnsdk.GnException;
import com.gracenote.gnsdk.GnList;
import com.gracenote.gnsdk.GnLocale;
import com.gracenote.gnsdk.GnUser;
import com.gracenote.gnsdk.IGnSystemEvents;

public class SystemEvents implements IGnSystemEvents {

    private GnUser gnUser;

    public SystemEvents(GnUser gnUser) {
        this.gnUser = gnUser;
    }

    @Override
    public void localeUpdateNeeded(final GnLocale locale) {
        // Locale update is detected
        Thread localeUpdateThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    locale.update(gnUser);
                } catch (GnException e) {
                    Log.e(SystemEvents.class.getName(), e.getMessage());
                    e.printStackTrace();
                }
            }
        });
        localeUpdateThread.start();
    }

    @Override
    public void listUpdateNeeded(final GnList list) {
        // List update is detected
        Thread listUpdateThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    list.update(gnUser);
                } catch (GnException e) {
                    Log.e(SystemEvents.class.getName(), e.getMessage());
                    e.printStackTrace();
                }
            }
        });
        listUpdateThread.start();
    }

    @Override
    public void systemMemoryWarning(long currentMemorySize, long warningMemorySize) {
        // only invoked if a memory warning limit is configured
    }
}
