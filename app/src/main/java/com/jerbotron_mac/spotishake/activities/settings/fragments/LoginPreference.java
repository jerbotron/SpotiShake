package com.jerbotron_mac.spotishake.activities.settings.fragments;

import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jerbotron_mac.spotishake.R;
import com.jerbotron_mac.spotishake.activities.settings.SettingsPresenter;
import com.jerbotron_mac.spotishake.utils.SharedUserPrefs;
import com.squareup.picasso.Picasso;


public class LoginPreference extends Preference {

    private ImageView profileImage;
    private TextView displayName;
    private TextView loginButton;

    private SettingsPresenter presenter;
    private MainPreferencesFragment preferencesFragment;

    public LoginPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public LoginPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public LoginPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected View onCreateView(ViewGroup parent) {
        super.onCreateView(parent);
        LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return layoutInflater.inflate(R.layout.pref_account, parent, false);
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
        profileImage = (ImageView) view.findViewById(R.id.profile_image);
        displayName = (TextView) view.findViewById(R.id.account_display_name);
        loginButton = (TextView) view.findViewById(R.id.account_login);

        loginButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if (presenter.isLoggedIn()) {
                    presenter.logoutSpotify();
                    setLoggedOut();
                } else {
                    preferencesFragment.openLoginWindow();
                }
            }
        });

        if (presenter.isLoggedIn()) {
            setLoggedIn();
        } else {
            setLoggedOut();
        }
    }

    void init(MainPreferencesFragment fragment,
              SettingsPresenter presenter) {
        this.preferencesFragment = fragment;
        this.presenter = presenter;
    }

    void setLoggedIn() {
        setupUserProfile(getContext());
        loginButton.setText(R.string.pref_account_logout);
    }

    private void setLoggedOut() {
        Picasso.with(getContext())
                .load(R.drawable.default_profile_image)
                .into(profileImage);
        displayName.setText("");
        loginButton.setText(R.string.pref_account_login);
    }

    private void setupUserProfile(Context context) {
        SharedUserPrefs sharedUserPrefs = presenter.getSharedUserPrefs();
        String imageUrl = sharedUserPrefs.getUserProfileImageUrl();
        if (imageUrl != null) {
            Picasso.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.default_profile_image)
                    .into(profileImage);
        }
        displayName.setText("DJ Khaled");
//        displayName.setText(sharedUserPrefs.getUserDisplayName());
    }
}
