package com.jerbotron_mac.spotishake.activities.home.dagger;

import com.jerbotron_mac.spotishake.activities.home.HomeActivity;
import com.jerbotron_mac.spotishake.activities.home.HomePresenter;
import com.jerbotron_mac.spotishake.activities.home.fragments.HistoryFragment;
import com.jerbotron_mac.spotishake.dagger.ApplicationComponent;
import com.jerbotron_mac.spotishake.dagger.scopes.ActivityScope;

import dagger.Component;

@ActivityScope
@Component(dependencies = ApplicationComponent.class)
public interface HomeComponent {
    void inject(HomeActivity target);
    void inject(HomePresenter target);
    void inject(HistoryFragment target);
}
