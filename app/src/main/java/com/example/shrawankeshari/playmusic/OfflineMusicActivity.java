package com.example.shrawankeshari.playmusic;

import android.Manifest;
import android.app.DownloadManager;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.shrawankeshari.playmusic.Facebook.FacebookLoginActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class OfflineMusicActivity extends AppCompatActivity {

    //    private Button download;
    private ListView lv;
    private ImageView im_selected_track_image;
    private TextView tv_selected_track_title;
    private TextView tv_selected_track_artist;
    private ImageView im_song_control;
    private ImageView im_song_previous;
    private ImageView im_song_next;
    private int music_index;

    private MusicField shareMusic;

    MediaMetadataRetriever mediaMetadataRetriever;

    private List<MusicField> musicList = new ArrayList<>();
    private String current_song;

    private Uri file_uri;
    private DownloadManager downloadManager;
    private ArrayList<Long> list = new ArrayList<>();
    private long referenceId;

    private MediaPlayer mediaPlayer;
    private AudioManager audioManager;

    private MediaPlayer.OnCompletionListener onCompletionListener =
            new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    if (music_index == musicList.size() - 1) {
                        music_index = 0;

                        PlaySongOnNextPrevious(music_index);

                    } else {

                        PlaySongOnNextPrevious(++music_index);

                    }

                    displayCurrentPlayingSong(musicList.get(music_index));
                }
            };

    private AudioManager.OnAudioFocusChangeListener mOnAudioFocusChangeListener =
            new AudioManager.OnAudioFocusChangeListener() {
                @Override
                public void onAudioFocusChange(int focusChange) {
                    if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
                        // Lost focus for a short time, so pause the music don't release the
                        // media player because playback is likely to resume
                        if (mediaPlayer.isPlaying()) {
                            mediaPlayer.pause();
                        }
                    } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                        // resume playback
                        if (!mediaPlayer.isPlaying()) {
                            mediaPlayer.start();
                            mediaPlayer.setVolume(1.0f, 1.0f);
                        }
                    } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                        // Lost focus for an unbounded amount of time, pause the music
                        if (mediaPlayer.isPlaying()) {
                            mediaPlayer.pause();
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isStoragePermissionGranted()) {
            AfterPermissionGranted();
        }
    }

    public void AfterPermissionGranted() {
        setContentView(R.layout.activity_offline);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);

        mediaMetadataRetriever = new MediaMetadataRetriever();

//        download = findViewById(R.id.download);
        lv = findViewById(R.id.listView_offline);
        im_selected_track_image = findViewById(R.id.song_image_offline);
        im_song_control = findViewById(R.id.song_control_offline);
        tv_selected_track_title = findViewById(R.id.song_title_offline);
        tv_selected_track_artist = findViewById(R.id.song_artist_offline);
        im_song_previous = findViewById(R.id.song_previous_offline);
        im_song_next = findViewById(R.id.song_next_offline);

        mediaPlayer = new MediaPlayer();

        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);

        file_uri = Uri.parse("http://hck.re/Rh8KTk");

        retrieveSong();

        displaySong();

        im_song_control.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    im_song_control.setImageResource(R.drawable.ic_play);
                } else {
                    mediaPlayer.start();
                    try {
                        mediaPlayer.setDataSource(current_song);
                        mediaPlayer.prepareAsync();
                    } catch (Exception e) {
                        im_song_control.setImageResource(R.drawable.ic_pause);
                    }
                }
            }
        });

        im_song_previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (music_index == 0) {
                    music_index = musicList.size() - 1;

                    PlaySongOnNextPrevious(music_index);

                } else {

                    PlaySongOnNextPrevious(--music_index);

                }

                displayCurrentPlayingSong(musicList.get(music_index));
            }
        });

        im_song_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (music_index == musicList.size() - 1) {
                    music_index = 0;

                    PlaySongOnNextPrevious(music_index);

                } else {

                    PlaySongOnNextPrevious(++music_index);

                }

                displayCurrentPlayingSong(musicList.get(music_index));
            }
        });


        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    im_song_control.setImageResource(R.drawable.ic_play);
                } else {
                    mediaPlayer.start();
                    im_song_control.setImageResource(R.drawable.ic_pause);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_offline, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.share:
                String songName = shareMusic.getSong_path();
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("audio/*");
                intent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:///" + songName));
                startActivity(Intent.createChooser(intent, "Share Via"));
                return true;

            case R.id.fb_share:
                Intent facebookIntent = new Intent(OfflineMusicActivity.this,
                        FacebookLoginActivity.class);
                startActivity(facebookIntent);
                return true;

            case R.id.online_music:
                Intent offlineIntent = new Intent(OfflineMusicActivity.this, StreamMusicActivity.class);
                startActivity(offlineIntent);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
            audioManager.abandonAudioFocus(mOnAudioFocusChangeListener);
        }
    }

    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            AfterPermissionGranted();
        }
    }

    public void retrieveSong() {
        ContentResolver contentResolver = getContentResolver();

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        String selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0";
        String sortedOrder = MediaStore.Audio.Media.TITLE + " ASC";
        Cursor cursor = contentResolver.query(uri, null, selection, null,
                sortedOrder);
        if (cursor != null && cursor.getCount() > 0) {

            while (cursor.moveToNext()) {

                MusicField mf = new MusicField();

                mf.setSong_title(cursor.getString(cursor
                        .getColumnIndex(MediaStore.Audio.Media.TITLE)));

                mf.setSong_artist(cursor.getString(cursor
                        .getColumnIndex(MediaStore.Audio.Media.ARTIST)));

                String data = cursor.getString(cursor
                        .getColumnIndex(MediaStore.Audio.Media.DATA));

                mediaMetadataRetriever.setDataSource(data);

                byte[] image = mediaMetadataRetriever.getEmbeddedPicture();

                if (image != null) {
                    mf.setSong_image(image);
                } else {
                    mf.setWhenNoImage(R.drawable.ic_music);
                }


                mf.setSong_path(data);

                long length = Long.parseLong(mediaMetadataRetriever
                        .extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));

                length = length / 1000;

                long minute = length / 60;
                long second = length % 60;

                StringBuilder duration = new StringBuilder();
                duration.append(" - ").append(String.valueOf(minute))
                        .append(":").append(String.valueOf(second));


                mf.setSong_duration(duration.toString());

                musicList.add(mf);

            }
        } else {
            tv_selected_track_title.setText("Nothing To Display");
        }
    }

    private void displaySong() {
        ArrayAdapter<MusicField> adapter = new MusicAdapter(this,
                R.layout.item_list_offline, musicList);

        lv.setAdapter(adapter);

        displayCurrentPlayingSong(musicList.get(0));

        im_song_control.setImageResource(R.drawable.ic_play);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                MusicField mf = musicList.get(i);

                displayCurrentPlayingSong(mf);

                music_index = i;

                int result = audioManager.requestAudioFocus(mOnAudioFocusChangeListener,
                        AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);

                if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.stop();
                        mediaPlayer.reset();
                    } else {
                        mediaPlayer.reset();
                    }

                    try {
                        mediaPlayer.setDataSource(mf.getSong_path());
                        mediaPlayer.prepareAsync();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    mediaPlayer.setOnCompletionListener(onCompletionListener);
                }
            }
        });
    }

    private void displayCurrentPlayingSong(MusicField mf) {
        shareMusic = mf;
        tv_selected_track_title.setText(mf.getSong_title());
        tv_selected_track_artist.setText(mf.getSong_artist());

        if (mf.getSong_image() != null) {
            Glide.with(im_selected_track_image.getContext())
                    .load(mf.getSong_image())
                    .into(im_selected_track_image);
        } else {
            Glide.with(im_selected_track_image.getContext())
                    .load(mf.getWhenNoImage())
                    .into(im_selected_track_image);
        }

        current_song = mf.getSong_path();
    }

    private void PlaySongOnNextPrevious(int song_index) {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.reset();
        } else {
            mediaPlayer.reset();
        }
        try {
            mediaPlayer.setDataSource(musicList.get(song_index).getSong_path());
            mediaPlayer.prepareAsync();
        } catch (Exception e) {
            im_song_control.setImageResource(R.drawable.ic_pause);
        }
    }

}
