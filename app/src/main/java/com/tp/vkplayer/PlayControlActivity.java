package com.tp.vkplayer;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.tp.vkplayer.base.SongObject;

import java.util.ArrayList;

/**
 * Created by S.Grechkin-Pogrebnyakov on 18.05.2015.
 */

public class PlayControlActivity extends MusicControllerActivity {

    private SeekBar seekBar;
    private PlayMusicService.RepeatMode repeatMode;
    private boolean randomPlay;
    ImageButton repeatButton;
    ImageButton randomButton;

    protected void onHandleMessage(Message msg) {
        if( msg.what == seekMsg ) {
            if( playMusicService == null || !playMusicService.isPlay ) {
                seekHandler.sendEmptyMessageDelayed(seekMsg, 500);
                return;
            }
            seekBar.setProgress(getCurrentPosition());
            seekBar.setMax(getDuration());
            seekBar.setSecondaryProgress(getBufferPosition());   // for buffer progress
            songTitleView.setText(getSongName());
            artistNameView.setText(getSongArtist());
            if (isPlaying() || !isLoaded()) {
                seekHandler.sendEmptyMessageDelayed(seekMsg, 1000);
            }
        }
    }

    protected void onConnected() {
        repeatMode = getRepeatMode();
        randomPlay = isRandomPlay();
        if (randomPlay) randomButton.setImageResource(R.drawable.shuffle_button);
        else randomButton.setImageResource(R.drawable.shuffle_button_disabled);
        switch (repeatMode) {
            case DO_NOT_REPEAT:
                repeatButton.setImageResource(R.drawable.repeat_button_disabled);
                break;
            case REPEAT_ALL:
                repeatButton.setImageResource(R.drawable.repeat_button_repeat_all);
                break;
            case REPEAT_ONE:
                repeatButton.setImageResource(R.drawable.repeat_button_repeat_one);
                break;
        }
        if (isPlaying()){
            startPlay();
        } else pausePlay();

//        // TODO убрать это отсюда!!!
//        ArrayList<SongObject> songs = new ArrayList<SongObject>();
//        SongObject testSong = new SongObject("Serg", "blabla", "/sdcard/Tuneblast Music/Guns N' Roses - Knockin' On Heaven's Door.mp3");
//        songs.add(testSong);
//        testSong = new SongObject("Vasya", "qwerty", "/sdcard/Tuneblast Music/180194403.mp3");
//        songs.add(testSong);
//        testSong = new SongObject("111", "222", "https://cs1-35v4.vk-cdn.net/p19/e53261ad32f5dd.mp3?extra=emCxk1n5F8wPwQQwZ3PfyUi8Qhx_-HBCzOvbpx7E3O0cOHS7I1YbxRb9TjxmHv5ZcnURn7jYVxSqeZNJM6tlz8cf9jQ");
//        songs.add(testSong);
//        playMusicService.setSongs(songs);
//        playMusicService.setSong(0);
//        startPlay();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_play_control);

        pauseImageResource  = R.drawable.play_control_activity_pause_button;
        playImageResource   = R.drawable.play_control_activity_play_button;

        songTitleView   = (TextView)    findViewById(R.id.play_control_song_name);
        artistNameView  = (TextView)    findViewById(R.id.play_control_artist);
        playPauseButton = (ImageButton) findViewById(R.id.play_control_play_button);
        seekBar         = (SeekBar)     findViewById(R.id.play_control_seekBar);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            private boolean touched = false;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (touched) {
                    seekTo(progress);
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                pausePlay();
                touched = true;
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                startPlay();
                touched = false;
            }
        });
        playPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( playMusicService != null && musicBound )
                    if ( !isPlaying() ) {
                        startPlay();
                    }
                    else {
                        pausePlay();
                    }
                else
                    Toast.makeText(getApplicationContext(), "Пожалуйста, подождите.", Toast.LENGTH_SHORT).show();
                }
        });
        ImageButton prevButton = (ImageButton)findViewById(R.id.play_control_prev_button);
        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    playPrev();
            }
        });
        ImageButton nextButton = (ImageButton)findViewById(R.id.play_control_next_button);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    playNext();
            }
        });
        repeatButton = (ImageButton)findViewById(R.id.play_control_repeat_button);
        repeatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeRepeatMode();
            }
        });
        randomButton = (ImageButton)findViewById(R.id.play_control_random_button);
        randomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeRandomMode();
            }
        });


//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                int currentPosition = 0;
//                while (true) {
//                    try {
//                        Thread.sleep(1000);
//                    } catch (InterruptedException e) {
//                        return;
//                    }
//                    if( playMusicService == null || !playMusicService.isPlaying())
//                        continue;
//                    currentPosition = getCurrentPosition();
//                    final int total = getDuration();
//                    //final String totalTime = getAsTime(total);
//                    //final String curTime = getAsTime(currentPosition);
//
//                    seekBar.setMax(total); //song duration
//                    seekBar.setProgress(currentPosition);  //for current song progress
//                    seekBar.setSecondaryProgress(playMusicService.getBufferPosition());   // for buffer progress
////                    runOnUiThread(new Runnable() {
////                        @Override
////                        public void run() {
////                            if (isPlaying()) {
////                                if (!playPauseButton.isChecked()) {
////                                    playPauseButton.setChecked(true);
////                                }
////                            } else {
////                                if (playPauseButton.isChecked()) {
////                                    playPauseButton.setChecked(false);
////                                }
////                            }
////                            //musicDuration.setText(totalTime);
////                            //musicCurLoc.setText(curTime);
////                        }
////                    });
//                }
//            }
//        }).start();


    }


    private void changeRepeatMode() {
        if (playMusicService == null || ! musicBound )
            return;
        switch (repeatMode) {
            case DO_NOT_REPEAT:
                repeatMode = PlayMusicService.RepeatMode.REPEAT_ALL;
                playMusicService.setRepeatMode(repeatMode);
                repeatButton.setImageResource(R.drawable.repeat_button_repeat_all);
                break;
            case REPEAT_ALL:
                repeatMode = PlayMusicService.RepeatMode.REPEAT_ONE;
                playMusicService.setRepeatMode(repeatMode);
                repeatButton.setImageResource(R.drawable.repeat_button_repeat_one);
                break;
            case REPEAT_ONE:
                repeatMode = PlayMusicService.RepeatMode.DO_NOT_REPEAT;
                playMusicService.setRepeatMode(repeatMode);
                repeatButton.setImageResource(R.drawable.repeat_button_disabled);
                break;
        }

    }

    private void changeRandomMode() {
        if (playMusicService == null || ! musicBound )
            return;
        if (randomPlay) {
            randomPlay = false;
            playMusicService.setRandomPlay(false);
            randomButton.setImageResource(R.drawable.shuffle_button_disabled);
        } else {
            randomPlay = true;
            playMusicService.setRandomPlay(true);
            randomButton.setImageResource(R.drawable.shuffle_button);
        }
    }

}
