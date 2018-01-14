package com.example.shrawankeshari.playmusic.Facebook;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.shrawankeshari.playmusic.R;
import com.facebook.AccessToken;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;

public class FacebookShareActivity extends AppCompatActivity {

    ProfileTracker profileTracker;
    ImageView profilePic;
    TextView profileName, facebookId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebook_share);

        profilePic = findViewById(R.id.profile_image);
        facebookId = findViewById(R.id.facebook_id);
        profileName = findViewById(R.id.facebook_name);

        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                if (currentProfile != null) {
                    displayProfileInfo(currentProfile);
                }
            }
        };

        if (AccessToken.getCurrentAccessToken() != null) {
            Profile currentProfile = Profile.getCurrentProfile();
            if (currentProfile != null) {
                displayProfileInfo(currentProfile);
            } else {
                Profile.fetchProfileForCurrentAccessToken();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_facebook, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {

            case R.id.facebook_logout_option:
                LoginManager.getInstance().logOut();
                launchLoginActivity();
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        profileTracker.stopTracking();
    }

    private void displayProfileInfo(Profile profile) {

        String profileId = profile.getId();
        facebookId.setText(profileId);

        String name = profile.getName();
        profileName.setText(name);

        Uri profilePicUri = profile.getProfilePictureUri(100, 100);
        Glide.with(profilePic.getContext())
                .load(profilePicUri)
                .into(profilePic);
    }

    private void launchLoginActivity() {
        Intent intent = new Intent(FacebookShareActivity.this,
                FacebookLoginActivity.class);
        startActivity(intent);
        finish();
    }
}
