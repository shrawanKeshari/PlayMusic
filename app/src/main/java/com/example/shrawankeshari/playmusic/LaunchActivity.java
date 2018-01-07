package com.example.shrawankeshari.playmusic;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by shrawankeshari on 7/1/18.
 * Class for implementing splash screen and after that open the activity displaying song's fetched
 * from the web.
 */

public class LaunchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        //handler for holding the current activity for sometime and then launching the next
        //activity send in the intent
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(LaunchActivity.this,
                        StreamMusicActivity.class);
                startActivity(intent);
                finish();
            }
        }, 2000);
    }
}
