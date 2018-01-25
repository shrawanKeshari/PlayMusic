package com.example.shrawankeshari.playmusic.Facebook;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.shrawankeshari.playmusic.OfflineMusic.MusicField;
import com.example.shrawankeshari.playmusic.OfflineMusic.OfflineMusicActivity;
import com.example.shrawankeshari.playmusic.R;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class FacebookShareActivity extends AppCompatActivity {

    ProfileTracker profileTracker;
    ImageView profilePic;
    TextView profileName, facebookId, noItemView;
    EditText fbShareText;
    Button shareButton;
    MusicField musicField;
    ListView fbListView;
    String check_permission;
    List<String> postId;

    CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebook_share);

        Bundle bundle = getIntent().getExtras();
        musicField = bundle.getParcelable(OfflineMusicActivity.EXTRA_MESSAGE);

        profilePic = findViewById(R.id.profile_image);
        facebookId = findViewById(R.id.facebook_id);
        profileName = findViewById(R.id.facebook_name);
        fbShareText = findViewById(R.id.fb_share_text);
        shareButton = findViewById(R.id.share_button);
        fbListView = findViewById(R.id.fb_list_view);
        noItemView = findViewById(R.id.no_item_view);
        postId = new ArrayList<>();

        callbackManager = CallbackManager.Factory.create();

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

        StringBuilder sb = new StringBuilder();
        if (musicField != null) {
            sb.append("Listening to ").append(musicField.getSong_title()).append("\n")
                    .append(musicField.getSong_artist());
        } else {
            sb.append("first select a song from list to share");
        }

        fbShareText.setText(sb);
        fbShareText.setSelection(fbShareText.getText().length());

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                check_permission = "publish_actions";

                Set permissions = AccessToken.getCurrentAccessToken().getPermissions();
                if (permissions.contains(check_permission)) {
                    fetchData();
                } else {
                    LoginManager loginManager = LoginManager.getInstance();
                    loginManager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                        @Override
                        public void onSuccess(LoginResult loginResult) {
                            fetchData();
                        }

                        @Override
                        public void onCancel() {
                            String permissionMessage = "PlayMusic requires " + check_permission +
                                    " permission to post on your wall";
                            Toast.makeText(FacebookShareActivity.this, permissionMessage,
                                    Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onError(FacebookException error) {

                        }
                    });

                    loginManager.logInWithPublishPermissions(FacebookShareActivity.this,
                            Arrays.asList(check_permission));
                }

                Toast.makeText(FacebookShareActivity.this, "share button clicked",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void fetchData() {

        StringBuilder field_parameter = new StringBuilder();
        field_parameter.append("/me/feed?").append("message=").append(fbShareText.getText());
        Log.i("TAGG", field_parameter.toString());
        Bundle parameters = new Bundle();
        parameters.putString("fields", field_parameter.toString());

        new GraphRequest(AccessToken.getCurrentAccessToken(), field_parameter.toString(),
                null, HttpMethod.POST, new GraphRequest.Callback() {
            @Override
            public void onCompleted(GraphResponse response) {
                if (response.getError() != null) {
                    Toast.makeText(FacebookShareActivity.this,
                            response.getError().getErrorMessage(), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(FacebookShareActivity.this,
                            "shared on facebook", Toast.LENGTH_LONG).show();

                    JSONObject jsonResponse = response.getJSONObject();
                    try {
                        postId.add(jsonResponse.getString("id"));
                    } catch (JSONException je) {
                        je.printStackTrace();
                    }
                }
            }
        }).executeAsync();
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
