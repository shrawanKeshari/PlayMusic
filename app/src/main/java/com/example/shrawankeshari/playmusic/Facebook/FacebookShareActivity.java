package com.example.shrawankeshari.playmusic.Facebook;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.shrawankeshari.playmusic.FbShareField;
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
    List<String> postId;
    List<String> postName;

    CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebook_share);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

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
        postName = new ArrayList<>();

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
                publishPermission(null);
            }
        });

        readPermission();
    }

    private void publishPermission(final String id) {
        Set permissions = AccessToken.getCurrentAccessToken().getPermissions();
        if (permissions.contains("publish_actions")) {
            if (id != null) {
                deleteData(id);
            } else {
                fetchData();
            }
        } else {
            LoginManager loginManager = LoginManager.getInstance();
            loginManager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    if (id != null) {
                        deleteData(id);
                    } else {
                        fetchData();
                    }
                }

                @Override
                public void onCancel() {
                    String permissionMessage = "PlayMusic requires publish_actions " +
                            "permission";
                    Toast.makeText(FacebookShareActivity.this, permissionMessage,
                            Toast.LENGTH_LONG).show();
                }

                @Override
                public void onError(FacebookException error) {

                }
            });

            loginManager.logInWithPublishPermissions(FacebookShareActivity.this,
                    Arrays.asList("publish_actions"));
        }
    }

    private void readPermission() {
        Set permissions = AccessToken.getCurrentAccessToken().getPermissions();
        if (permissions.contains("user_posts")) {
            getFbPosts();
        } else {
            LoginManager loginManager = LoginManager.getInstance();
            loginManager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    getFbPosts();
                }

                @Override
                public void onCancel() {
                    String permissionMessage = "PlayMusic requires user_posts " +
                            "permission";
                    Toast.makeText(FacebookShareActivity.this, permissionMessage,
                            Toast.LENGTH_LONG).show();
                }

                @Override
                public void onError(FacebookException error) {

                }
            });

            loginManager.logInWithReadPermissions(FacebookShareActivity.this,
                    Arrays.asList("user_posts"));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void fetchData() {

        StringBuilder field_parameter = new StringBuilder();
        field_parameter.append("/me/feed?").append("message=").append(fbShareText.getText());

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
                }
            }
        }).executeAsync();
    }

    private void getFbPosts() {
        StringBuilder field_parameter = new StringBuilder();
        field_parameter.append("/me/posts");

        new GraphRequest(AccessToken.getCurrentAccessToken(), field_parameter.toString(),
                null, HttpMethod.GET, new GraphRequest.Callback() {
            @Override
            public void onCompleted(GraphResponse response) {
                if (response.getError() != null) {
                    Toast.makeText(FacebookShareActivity.this,
                            response.getError().getErrorMessage(), Toast.LENGTH_LONG).show();
                } else {
                    JSONObject jsonObject = response.getJSONObject();

                    try {
                        JSONArray jsonData = jsonObject.getJSONArray("data");
                        int i = 0;
                        while (i < jsonData.length()) {
                            JSONObject jsonPost = jsonData.getJSONObject(i);

                            FbShareField field = new FbShareField();

                            try {
                                field.setFbShareName(jsonPost.getString("message"));
                            } catch (JSONException e) {
                                field.setFbShareName(jsonPost.getString("story"));
                            }
                            field.setFbShareId(jsonPost.getString("id"));

                            postName.add(field.getFbShareName());
                            postId.add(field.getFbShareId());
                            i++;
                        }
                    } catch (JSONException e) {
                        Toast.makeText(FacebookShareActivity.this,
                                "some error occurred", Toast.LENGTH_LONG).show();
                    }

                    displayList(postName);
                }
            }
        }).executeAsync();
    }

    private void displayList(List<String> postName) {

        if (postName == null || postName.size() == 0) {
            fbListView.setVisibility(View.INVISIBLE);
            noItemView.setVisibility(View.VISIBLE);
            noItemView.setText(R.string.noItemMsg);
        } else {
            noItemView.setVisibility(View.INVISIBLE);
            fbListView.setVisibility(View.VISIBLE);
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, android.R.id.text1, postName);
            fbListView.setAdapter(arrayAdapter);
            fbListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    String id = postId.get(i);
                    dialog(id);
                }
            });
        }
    }

    private void deleteData(String id) {
        StringBuilder field_parameter = new StringBuilder();
        field_parameter.append(id);

        new GraphRequest(AccessToken.getCurrentAccessToken(), field_parameter.toString(),
                null, HttpMethod.DELETE, new GraphRequest.Callback() {
            @Override
            public void onCompleted(GraphResponse response) {
                if (response.getError() != null) {
                    Toast.makeText(FacebookShareActivity.this,
                            response.getError().getErrorMessage(), Toast.LENGTH_LONG).show();
                } else {
                    JSONObject jsonObject = response.getJSONObject();

                    try {
                        String jsonSuccess = jsonObject.getString("success");
                        Toast.makeText(FacebookShareActivity.this,
                                "post deleted successfully", Toast.LENGTH_SHORT).show();

                    } catch (JSONException e) {
                        try {
                            JSONObject jsonError = jsonObject.getJSONObject("error");
                            Toast.makeText(FacebookShareActivity.this,
                                    jsonError.getString("message"), Toast.LENGTH_LONG).show();
                        } catch (JSONException e1) {
                            Toast.makeText(FacebookShareActivity.this,
                                    "some error occurred", Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }
        }).executeAsync();
    }

    private void dialog(final String id) {
        final AlertDialog.Builder a1 = new AlertDialog.Builder(this);
        a1.setTitle("ALERT");
        a1.setMessage("To view selected post click VIEW and to delete post press OK");
        a1.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int button1) {
                        publishPermission(id);
                        dialog.cancel();
                    }

                });
        a1.setNegativeButton("VIEW",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        Intent fbIntent = new Intent(Intent.ACTION_VIEW,
                                Uri.parse("https://www.facebook.com/" + id));
                        startActivity(fbIntent);
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = a1.create();
        a1.show();
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
