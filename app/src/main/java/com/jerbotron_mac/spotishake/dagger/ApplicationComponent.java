package com.jerbotron_mac.spotishake.dagger;

import com.jerbotron_mac.spotishake.application.SpotiShakeApplication;
import com.jerbotron_mac.spotishake.dagger.scopes.ApplicationScope;
import com.jerbotron_mac.spotishake.data.DatabaseAdapter;
import com.jerbotron_mac.spotishake.network.RestAdapterModule;
import com.jerbotron_mac.spotishake.network.SpotifyAuthService;
import com.jerbotron_mac.spotishake.network.SpotifyServiceWrapper;
import com.jerbotron_mac.spotishake.utils.AppUtils;
import com.jerbotron_mac.spotishake.utils.SharedUserPrefs;

import javax.inject.Named;

import dagger.Component;
import dagger.Module;
import dagger.Provides;
import kaaes.spotify.webapi.android.SpotifyService;
import retrofit.RestAdapter;

@ApplicationScope
@Component(modules = {ApplicationComponent.ApplicationModule.class,
        RestAdapterModule.class,
        SpotifyAuthService.Module.class})
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
        public SpotifyServiceWrapper spotifyServiceWrapper(SpotifyService spotifyService,
                                                           AppUtils appUtils) {
            return new SpotifyServiceWrapper(spotifyService, appUtils);
        }

        @Provides
        @ApplicationScope
        public SpotifyService spotifyService(@Named("spotify") RestAdapter restAdapter) {
            return restAdapter.create(SpotifyService.class);
        }
    }

    AppUtils provideAppUtils();
    DatabaseAdapter provideDatabaseAdapter();
    SharedUserPrefs provideSharedUserPrefs();
    SpotifyServiceWrapper provideSpotifyServiceWrapper();
    SpotifyAuthService provideSpotifyAuthService();
}
