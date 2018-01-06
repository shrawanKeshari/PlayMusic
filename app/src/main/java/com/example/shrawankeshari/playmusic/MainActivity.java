package com.example.shrawankeshari.playmusic;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.shrawankeshari.playmusic.Database.SongDBOpenHelper;
import com.example.shrawankeshari.playmusic.Database.SongDataSource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    //Defining the variables
    List<MyTask> tasks;
    List<SongsField> songsList;
    List<SongsField> songs;
    SongDataSource dataSource;

    //variable for storing the current item selected in the list
    String current_song;

    //Objects for different views
    ProgressBar pb;
    private TextView tv_selected_track_name;
    private TextView tv_selected_track_artist;
    private ImageView im_selected_track_image;
    private ImageView im_control;
    private Toolbar tb;
    private EditText et_search_song;
    private ImageView button_search;
    private LinearLayout linearLayout;
    private ListView lv;
    private CheckBox cb;

    //MediaPlayer class instance
    private MediaPlayer mediaPlayer;

    //AudioManager class instance
    private AudioManager audioManager;

    //initializing OnAudioFocusChangeListener instance variable for getting the current focus of
    // AudioManager class
    private AudioManager.OnAudioFocusChangeListener mOnAudioFocusChangeListener =
            new AudioManager.OnAudioFocusChangeListener() {
                @Override
                public void onAudioFocusChange(int focusChange) {
                    if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
                        // Lost focus for a short time, so pause the music don't release the
                        // media player because playback is likely to resume
                        if (mediaPlayer.isPlaying()) {
                            mediaPlayer.pause();
                            im_control.setImageResource(R.drawable.ic_play);
                        }
                    } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                        // resume playback
                        if (!mediaPlayer.isPlaying()) {
                            mediaPlayer.start();
                            im_control.setImageResource(R.drawable.ic_pause);
                            mediaPlayer.setVolume(1.0f, 1.0f);
                        }
                    } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                        // Lost focus for an unbounded amount of time, pause the music
                        if (mediaPlayer.isPlaying()) {
                            mediaPlayer.pause();
                            im_control.setImageResource(R.drawable.ic_play);
                        }
                    } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
                        // Lost focus for a short time, so we can continue playing at an
                        // low volume level
                        if (mediaPlayer.isPlaying()) {
                            mediaPlayer.setVolume(0.1f, 0.1f);
                        } else {
                            mediaPlayer.pause();
                        }
                    }
                }
            };

    //API url for getting the information
    private static final String API_URL = "http://starlord.hackerearth.com/studio";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Initialize references to views and setting the visibility of progressBas to invisible
        // which shows data is fetching from web
        pb = findViewById(R.id.progress_bar1);
        pb.setVisibility(View.INVISIBLE);
        tv_selected_track_name = findViewById(R.id.select_track_name);
        tv_selected_track_artist = findViewById(R.id.select_track_artist);
        im_selected_track_image = findViewById(R.id.select_track_image);
        im_control = findViewById(R.id.control);
        et_search_song = findViewById(R.id.search_song);
        button_search = findViewById(R.id.search_button);
        linearLayout = findViewById(R.id.search_id);
        linearLayout.setVisibility(View.INVISIBLE);
        lv = findViewById(R.id.list_view);
        cb = findViewById(R.id.checkbox);

        //setting the OnClickListener on the control image responsible for showing play
        // pause button according to the state of music
        im_control.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //if music is playing then pause it otherwise start the music and
                // get the data source of the music
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    im_control.setImageResource(R.drawable.ic_play);
                } else {
                    mediaPlayer.start();
                    try {
                        mediaPlayer.setDataSource(current_song);
                        mediaPlayer.prepareAsync();
                    } catch (Exception e) {
//                        Toast.makeText(MainActivity.this, "error occured while playing song press again",
//                                Toast.LENGTH_LONG).show();
                        im_control.setImageResource(R.drawable.ic_pause);
                    }
                }
            }
        });

        //getting reference to the MediaPlayer instance
        mediaPlayer = new MediaPlayer();

        //setting the audio stream type to stream music
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);

        //initializing tasks variable for keep track of different tasks in current activity
        tasks = new ArrayList<>();

        //setting up the onPreparedListener for MediaPlayer
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                changePlayAndPause();
            }
        });

        //checking for the connectivity of internet (if true proceed for data fetching
        //otherwise display error message)
        if (isOnline()) {
            //fetching data from web
            requestData(API_URL);
        } else {
            Toolbar tb = findViewById(R.id.tool_track);
            tb.setVisibility(View.INVISIBLE);
            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_LONG).show();
        }

        dataSource = new SongDataSource(this);
        dataSource.open();

        //setting up button onClickListener
        button_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //fetching data from edittext view field
                String search_string = String.valueOf(et_search_song.getText());
                if (search_string != null) {
                    //building the query
                    String query = SongDBOpenHelper.COLUMN_NAME + " LIKE " + "\"" + "%" + search_string + "%" + "\"";
                    //searching the data into database
                    songs = dataSource.findFiltered(query);
                    //if result size is greater than zero and edittext view field is not empty
                    //display the search result otherwise display old result
                    if (songs.size() != 0 && !search_string.equals("")) {
                        displaySearchSong(songs);
                    } else {
                        //display old result
                        displaySong();
                    }
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            //listing all the available song on the web
            case R.id.all_songs:
                //make search layout visible and set the edittext to empty string
                linearLayout.setVisibility(View.VISIBLE);
                et_search_song.setText("");
                //make the search button visible
                button_search.setVisibility(View.VISIBLE);
                songsList = dataSource.findAll();
                displaySearchSong(songsList);
                if(!isOnline()){
                    Toast.makeText(this, "To listen music please turn on the internet connection",
                            Toast.LENGTH_LONG).show();
                }
                return true;

            //listing only favorite songs of the user
            case R.id.favorite:
                //make search layout visible and set the edittext to FAVORITE SONGS and make it non-focusable
                linearLayout.setVisibility(View.VISIBLE);
                et_search_song.setText("FAVORITE SONGS");
                et_search_song.setFocusable(false);
                //make search button invisible
                button_search.setVisibility(View.INVISIBLE);
                songs = dataSource.findFavorite();
                displaySearchSong(songs);
                if(!isOnline()){
                    Toast.makeText(this, "To listen music please turn on the internet connection",
                            Toast.LENGTH_LONG).show();
                }
                return true;

            //listing the history of the user
            case R.id.history:
                //make search layout visible and set the edittext to FAVORITE SONGS and make it non-focusable
                linearLayout.setVisibility(View.VISIBLE);
                et_search_song.setText("HISTORY");
                et_search_song.setFocusable(false);
                //make search button invisible
                button_search.setVisibility(View.INVISIBLE);
                songs = dataSource.findHistory();
                displaySearchSong(songs);
                if(!isOnline()){
                    Toast.makeText(this, "To listen music please turn on the internet connection",
                            Toast.LENGTH_LONG).show();
                }
                return true;

            //show the detail about me and my work
            case R.id.portfolio:
                Intent intent = new Intent(MainActivity.this, PortfolioActivity.class);
                startActivity(intent);
                return true;

            //refresh the list to display the new songs which have added to the api
            case R.id.action_refresh:
                if(mediaPlayer.isPlaying()){
                    mediaPlayer.pause();
                    im_control.setImageResource(R.drawable.ic_play);
                }
                //checking for the connectivity of internet (if true proceed for data fetching
                //otherwise display error message)
                if (isOnline()) {
                    //make layout visible & fill the edittext field with empty string
                    linearLayout.setVisibility(View.VISIBLE);
                    et_search_song.setText("");
                    button_search.setVisibility(View.VISIBLE);
                    Toolbar tb = findViewById(R.id.tool_track);
                    tb.setVisibility(View.VISIBLE);

                    //fetching data from web
                    requestData(API_URL);
                } else {
                    Toast.makeText(this, "No Internet Connection", Toast.LENGTH_LONG).show();
                }
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //This method is responsible for showing play button when song is paused and
    // pause button when song is played and obtaining the button from drawable resource directory
    private void changePlayAndPause() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            //displaying the play button
            im_control.setImageResource(R.drawable.ic_play);
        } else {
            mediaPlayer.start();
            //displaying the pause button
            im_control.setImageResource(R.drawable.ic_pause);
        }
    }

    //This method is used execute the AsyncTask for fetching the data from web
    private void requestData(String apiUrl) {
        MyTask task = new MyTask();
        //executing the asyncTask
        task.execute(apiUrl);
    }

    //Method to check the connectivity of the mobile with web
    protected boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        //checking the network information
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }

    //method to display the search result
    private void displaySearchSong(final List<SongsField> songs) {
        ArrayAdapter<SongsField> adapter = new SongAdapter(this, R.layout.item_list, songs);


        //populating the list to list view for display
        lv.setAdapter(adapter);

        //setting the onClickListener for listItem. On selecting an item it plays that song
        // along with play-pause button, which we use to play or pause the song
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                SongsField sf = songs.get(i);

                if(!dataSource.findInHistory(sf.getSongId())){
                    dataSource.addToHistory(sf, System.currentTimeMillis());
                }else{
                    dataSource.updateToHistory(sf,System.currentTimeMillis());
                }

                //displaying the item on list view
                displayList(sf);

                //getting the focus of the audioManager and playing the selected song
                getFocusAndPlayPause(sf);
            }
        });
    }

    //AsyncTask used fo fetching the data from web in a different thread so that
    // there must not be much load on main thread
    private class MyTask extends AsyncTask<String, String, List<SongsField>> {

        @Override
        protected void onPreExecute() {

            //inf number of task is zero then make progressBar visible and this task to tasks list
            if (tasks.size() == 0) {
                pb.setVisibility(View.VISIBLE);
            }
            tasks.add(this);
        }

        @Override
        protected List<SongsField> doInBackground(String... strings) {
            //getting the data from web the storing it in string variable
            String songsData = HttpManager.getData(strings[0]);

            //now parsing the json data we get from the web
            songsList = JSONParsing.parse(songsData);

            return songsList;
        }

        @Override
        protected void onPostExecute(List<SongsField> sf) {
            //remove this task from the list as this task is completed and make
            // progressBar invisible
            tasks.remove(this);
            if (tasks.size() == 0) {
                pb.setVisibility(View.INVISIBLE);
            }

            //if list obtain from the web is empty display the error msg
            if (sf == null) {
                Toast.makeText(MainActivity.this, "Error in fetching the songs",
                        Toast.LENGTH_LONG).show();

                return;
            }

            songsList = sf;

            //fetching the database entry
            songs = dataSource.findAll();
            //if number of entries in database is less than the data from web then make the
            // entry of data into database
            if (songs.size() < songsList.size()) {
                createData(songsList, songsList.size() - songs.size());
                songs = dataSource.findAll();
            }
            //make layout invisible when data is fetching from the web
            linearLayout.setVisibility(View.VISIBLE);
            //call the method to display the list
            displaySong();
        }
    }

    //This method is used to display the song lis obtain from the web
    private void displaySong() {

        ArrayAdapter<SongsField> adapter = new SongAdapter(this, R.layout.item_list, songsList);

        //populating the list to list view for display
        lv.setAdapter(adapter);

        //find the history of the user
        List<SongsField> music = dataSource.findHistory();;

        //if no history then populate the first song of the list else populate the last songs played
        //by the user
        if(music.size() <= 0){
            //initially displaying the first song with play button
            displayList(songs.get(0));
        }else{
            //display the lasted played song
            displayList(music.get(0));
        }

        //initially setting the play pause control image to play button
        im_control.setImageResource(R.drawable.ic_play);

        //setting the onClickListener for listItem. On selecting an item it plays that song
        // along with play-pause button, which we use to play or pause the song
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                SongsField sf = songsList.get(i);

                if(!dataSource.findInHistory(sf.getSongId())){
                    dataSource.addToHistory(sf, System.currentTimeMillis());
                }else{
                    dataSource.updateToHistory(sf,System.currentTimeMillis());
                }

                //displaying the item on list view
                displayList(sf);

                //getting the focus of the audioManager and playing the selected song
                getFocusAndPlayPause(sf);
            }
        });
    }

    //This method is used to display data on the list view and also responsible for
    // the showing the data of selected song at the bottom of the screen
    private void displayList(final SongsField sf) {
        tv_selected_track_name.setText(sf.getSong_name());
        tv_selected_track_artist.setText(sf.getSong_artists());

        Glide.with(im_selected_track_image.getContext())
                .load(sf.getSong_image())
                .into(im_selected_track_image);

        //get the url of the current selected song
        current_song = sf.getSong_url();

        //if song is in the favorite list the check the checkbox otherwise be it unchecked
        if (dataSource.findSongFavorite(sf.getSongId())) {
            cb.setChecked(true);
        } else {
            cb.setChecked(false);
        }

        //setting up the checkbox onClickListener
        cb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if checkbox is checked
                if (cb.isChecked()) {
                    //add the current song to favorite list and display the toast message
                    if (dataSource.addToFavorite(sf)) {
                        Toast.makeText(MainActivity.this, "Added to favorite",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "error occurred while adding",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    //remove the current song from favorite and display the toast message
                    if (dataSource.removeFavorite(sf)) {
                        Toast.makeText(MainActivity.this, "remove from favorite",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "error occurred while removing",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    //get the focus of audioManager and play the song
    private void getFocusAndPlayPause(SongsField sf) {
        int result = audioManager.requestAudioFocus(mOnAudioFocusChangeListener,
                AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);

        //if we get the requested focus we proceed for the song play
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            //if previous song is playing stop it and play the selected song otherwise
            // play the selected song directly
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
                mediaPlayer.reset();
            } else {
                mediaPlayer.reset();
            }

            //get data from the source url to the mediaPlayer object and play the song
            try {
                mediaPlayer.setDataSource(sf.getSong_url());
                mediaPlayer.prepareAsync();
            } catch (IOException e) {
//                e.printStackTrace();
                Toast.makeText(this, "error occured while playing song prees again",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    //getting focus back when activity resumes
    @Override
    protected void onResume() {
        super.onResume();

        //open the database for read write operation
        dataSource.open();

        //request for the audio focus
        int result = audioManager.requestAudioFocus(mOnAudioFocusChangeListener,
                AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);

        //check if user is online or not
        if (isOnline()) {
            //if it get the requested audio focus then start playing the music
            if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                if (mediaPlayer.isPlaying()) {
                    im_control.setImageResource(R.drawable.ic_pause);
                    mediaPlayer.start();
                }
            }
        }
    }

    //when app is destroyed by the system, release the media player
    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseMediaPlayer();
    }

    //releasing the medialPlayer object when activity is destroyed
    private void releaseMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
            audioManager.abandonAudioFocus(mOnAudioFocusChangeListener);
        }
    }

    //create the database data when new data arrive from the web
    private void createData(List<SongsField> songsList, int size) {
        List<SongsField> songList = new ArrayList<SongsField>();
        int length = songsList.size();
        for (int i = length - size; i < length; i++) {
            SongsField sf = new SongsField();
            sf.setSongId(songsList.get(i).getSongId());
            sf.setSong_name(songsList.get(i).getSong_name());
            sf.setSong_url(songsList.get(i).getSong_url());
            sf.setSong_artists(songsList.get(i).getSong_artists());
            sf.setSong_image(songsList.get(i).getSong_image());
            sf.setSong_time(songsList.get(i).getSong_time());
            songList.add(sf);
        }
        //populating the data into database
        for (SongsField sf : songList) {
            dataSource.create(sf);
        }
    }

    //making database connection close when activity pause.
    @Override
    protected void onPause() {
        super.onPause();
        dataSource.close();
    }
}
