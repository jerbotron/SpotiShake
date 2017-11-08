package com.jerbotron_mac.spotisave.activities.home.custom;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;

/**
 * Created by jerbotron-mac on 10/17/17.
 */

public class MyBottomNavigationView extends BottomNavigationView {

    public MyBottomNavigationView(Context context) {
        super(context);
    }

    @Override
    public void setOnNavigationItemSelectedListener(@Nullable OnNavigationItemSelectedListener listener) {
        super.setOnNavigationItemSelectedListener(listener);
    }

}
