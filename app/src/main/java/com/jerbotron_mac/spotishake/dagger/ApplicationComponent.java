package com.jerbotron_mac.spotishake.dagger;

import com.jerbotron_mac.spotishake.application.SpotiShakeApplication;
import com.jerbotron_mac.spotishake.dagger.scopes.ApplicationScope;
import com.jerbotron_mac.spotishake.data.DatabaseAdapter;
import com.jerbotron_mac.spotishake.network.SpotifyServiceWrapper;
import com.jerbotron_mac.spotishake.utils.AppUtils;
import com.jerbotron_mac.spotishake.utils.SharedUserPrefs;

import dagger.Component;
import dagger.Module;
import dagger.Provides;

@ApplicationScope
@Component(modules = {ApplicationComponent.ApplicationModule.class})
public interface ApplicationComponent {

    void inject(SpotiShakeApplication application);

    @Module
    class ApplicationModule {
        private SpotiShakeApplication application;

        public ApplicationModule(SpotiShakeApplication application) {
            this.application = application;
        }

        @Provides
        @ApplicationScope
        public AppUtils appUtils() {
            return new AppUtils(application.getApplicationContext());
        }

        @Provides
        @ApplicationScope
        public DatabaseAdapter databaseAdapter(AppUtils appUtils) {
            return new DatabaseAdapter(application.getApplicationContext(), appUtils);
        }

        @Provides
        @ApplicationScope
        public SharedUserPrefs sharedUserPrefs() {
            return new SharedUserPrefs(application.getApplicationContext());
        }

        @Provides
        @ApplicationScope
        public SpotifyServiceWrapper spotifyServiceWrapper(SharedUserPrefs sharedUserPrefs,
                                                           AppUtils appUtils) {
            return new SpotifyServiceWrapper(sharedUserPrefs, appUtils);
        }
    }

    AppUtils provideAppUtils();
    DatabaseAdapter provideDatabaseAdapter();
    SharedUserPrefs provideSharedUserPrefs();
    SpotifyServiceWrapper provideSpotifyServiceWrapper();
}
