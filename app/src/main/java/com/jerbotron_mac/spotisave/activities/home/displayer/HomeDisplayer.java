package com.jerbotron_mac.spotisave.activities.home.displayer;

import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.design.widget.MyBottomNavigationView;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.MenuView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.jerbotron_mac.spotisave.R;
import com.jerbotron_mac.spotisave.activities.home.HomePresenter;
import com.jerbotron_mac.spotisave.activities.home.custom.ViewPagerAdapter;
import com.jerbotron_mac.spotisave.activities.home.fragments.AlbumFragment;
import com.jerbotron_mac.spotisave.activities.home.fragments.DetectFragment;
import com.jerbotron_mac.spotisave.activities.home.fragments.HistoryFragment;

public class HomeDisplayer {

    private AppCompatActivity activity;
    private HomePresenter presenter;

    private MyBottomNavigationView bottomNavigationView;
    private ViewPager viewPager;
    private MenuItem prevMenuItem;

    public HomeDisplayer(AppCompatActivity activity) {
        this.activity = activity;
        bottomNavigationView = (MyBottomNavigationView) activity.findViewById(R.id.bottom_nav);
        viewPager = (ViewPager) activity.findViewById(R.id.view_pager);
    }

    public void start(final AlbumFragment albumFragment,
                      final DetectFragment detectFragment,
                      final HistoryFragment historyFragment) {
        removeTextLabel(bottomNavigationView, R.id.menu_album);
        removeTextLabel(bottomNavigationView, R.id.menu_detect);
        removeTextLabel(bottomNavigationView, R.id.menu_history);

        setupViewPager(albumFragment, detectFragment, historyFragment);

        bottomNavigationView.setOnNavigationItemSelectedListener(new MyBottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_album:
                        viewPager.setCurrentItem(0);
                        return true;
                    case R.id.menu_detect:
                        viewPager.setCurrentItem(1);
                        return true;
                    case R.id.menu_history:
                        viewPager.setCurrentItem(2);
                        return true;
                }
                return false;
            }
        });
    }

    private void setupViewPager(final AlbumFragment albumFragment,
                                final DetectFragment detectFragment,
                                final HistoryFragment historyFragment) {
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(activity.getSupportFragmentManager());
        viewPagerAdapter.addFragment(albumFragment);
        viewPagerAdapter.addFragment(detectFragment);
        viewPagerAdapter.addFragment(historyFragment);
        viewPager.setAdapter(viewPagerAdapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (prevMenuItem != null) {
                    prevMenuItem.setChecked(false);
                }
                else {
                    bottomNavigationView.getMenu().getItem(0).setChecked(false);
                }
                bottomNavigationView.getMenu().getItem(position).setChecked(true);
                prevMenuItem = bottomNavigationView.getMenu().getItem(position);

                switch (position) {
                    case HomePresenter.FragmentEnum.ALBUM: {
                        detectFragment.setIsRunning(false);
                        break;
                    }
                    case HomePresenter.FragmentEnum.DETECT: {
                        detectFragment.setIsRunning(true);
                        detectFragment.setIsAudioProcessingStarted(false);
                        break;
                    }
                    case HomePresenter.FragmentEnum.HISTORY: {
                        detectFragment.setIsRunning(false);
//                        historyFragment.getHistory();
                        break;
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        viewPager.setCurrentItem(HomePresenter.FragmentEnum.DETECT);
    }

    private void removeTextLabel(@NonNull MyBottomNavigationView bottomNavigationView, @IdRes int menuItemId) {
        View view = bottomNavigationView.findViewById(menuItemId);
        if (view == null) return;
        if (view instanceof MenuView.ItemView) {
            ViewGroup viewGroup = (ViewGroup) view;
            int padding = 0;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View v = viewGroup.getChildAt(i);
                if (v instanceof ViewGroup) {
                    padding = v.getHeight();
                    viewGroup.removeViewAt(i);
                }
            }
            viewGroup.setPadding(view.getPaddingLeft(), (viewGroup.getPaddingTop() + padding) / 2, view.getPaddingRight(), 0);
        }
    }

    public void setCurrentItem(@HomePresenter.FragmentEnum int fragmentItem) {
        viewPager.setCurrentItem(fragmentItem);
    }

    public int getCurrentItem() {
        return viewPager.getCurrentItem();
    }

    public void setPresenter(HomePresenter presenter) {
        this.presenter = presenter;
    }

    public void displayNoResults() {
        Log.d(getClass().getName(), "could not id song");
//        DeveloperUtils.showToast(activity, "Could not ID song, please try again!");
    }
}
