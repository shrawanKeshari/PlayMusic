package com.example.shrawankeshari.playmusic;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

public class PortfolioActivity extends AppCompatActivity {

    //objects for different views
    TextView technical_detail, educational_detail, android_work, linkedin, github,
            interviewbit, hackerrank;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_portfolio);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Initialize references to views
        android_work = findViewById(R.id.android_work);

        educational_detail = findViewById(R.id.educational_detail);

        technical_detail = findViewById(R.id.technical_detail);

        linkedin = findViewById(R.id.linkedin);

        github = findViewById(R.id.github);

        interviewbit = findViewById(R.id.interviewbit);

        hackerrank = findViewById(R.id.hackerrank);

        //setting up the onClickListener which on click popup the dialog box containing the details
        //about android work and experience
        android_work.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = "Developing Android application since May 2016 from " +
                        "learning perspective.Currently i'm working on this music application which " +
                        "display the songs fetched from the web in the listview and on selecting the " +
                        "song, it play the music and also give the play-pause option to the user. " +
                        "I have added an checkbox and when user checked the box the song added to the " +
                        "favorite list and when user unchecked the box the song remove from the favorite list. " +
                        "User can also view the history of the songs played. User can also search for the " +
                        "song and play the searched song.I have added a portfolio activity which display the " +
                        "educational, technical detail and past android applications i have made." +
                        " I enjoy making android application and like to try new functionality in the " +
                        "app. I have made several android application over the past year and detail about " +
                        "the applications are given below:\n" +
                        "\n1) Travel Guide: Android application having firebase login. Displaying " +
                        "   information about some tourist places and a review section for tourist places. (Present)\n\n" +
                        "\n2) Surf: Surf has passwordless login using facebook account kit. Uses phone " +
                        "   number and email for login into account and also provide login using facebook. (August 2017)\n\n" +
                        "\n3) Notifier: Android application which calculates the aggregate of student " +
                        "   up-to specified year and fetches notices from the college website and " +
                        "   display it in list view. (March 2017)\n\n" +
                        "\n4) Coffee Counter: Android application for displaying the number of coffee's " +
                        "   ordered and the the total price. Email the order Summary to the user Email. (July 2016)\n\n" +
                        "\n5) Court Counter: Application for displaying score of a basketball match " +
                        "   with reset button. (June 2016)";

                dialog("Android Work", message);
            }
        });

        //setting up the onClickListener which on click popup the dialog box containing the
        // educational detail
        educational_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = "2013-2017: B.Tech in Electronics and Communication Engineering (ECE) " +
                        "from Bundelkhand Institute of Engineering and Technology (BIET), Jhansi " +
                        "with an aggregate of 77.04%.\n\n" +
                        "2011-2012: Completed XII with Physics, Chemistry and Mathematics as majors " +
                        "from KV AFS Ojhar, Nasik with an aggregate of 88.40%.\n\n" +
                        "2009-2010: Completed X with Science and Mathematics as majors from KV AFS " +
                        "Ojhar, Nasik with an aggregate of 89.30%.";

                dialog("Educational Qualification", message);
            }
        });

        //setting up the onClickListener which on click popup the dialog box containing the
        // technical detail
        technical_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = "Languages: C, CORE JAVA.\n" +
                        "\nField of Interests: Data Structures Using C.\n" +
                        "\nIDE: Eclipse, Android Studio.\n" +
                        "\nOperating System: Android OS & LINUX.\n" +
                        "\nVersion Control: GIT\n" +
                        "\n280th rank on INTERVIEWBIT.\n" +
                        "\n929th rank on HACKERRANK.";

                dialog("Technical Details", message);
            }
        });

        //setting up the onClickListener which on click open the linkedin profile on the web
        linkedin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://www.linkedin.com/in/shrawan-kumar-keshari-747b73125/"));
                startActivity(intent);
            }
        });

        //setting up the onClickListener which on click open the github profile on the web
        github.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://github.com/shrawanKeshari"));
                startActivity(intent);
            }
        });

        //setting up the onClickListener which on click open the interviewBit profile on the web
        interviewbit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://www.interviewbit.com/profile/shrawan_keshari"));
                startActivity(intent);
            }
        });

        //setting up the onClickListener which on click open the hackerrank profile on the web
        hackerrank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://www.hackerrank.com/Shrawankeshari?hr_r=1"));
                startActivity(intent);
            }
        });
    }

    //method to popup the dialog box with message to display
    private void dialog(String title, String message) {
        AlertDialog.Builder a1 = new AlertDialog.Builder(this);
        a1.setTitle(title);
        a1.setMessage(message);
        a1.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int button1) {
                        dialog.cancel();
                    }

                });
        AlertDialog alertDialog = a1.create();
        a1.show();
    }
}
