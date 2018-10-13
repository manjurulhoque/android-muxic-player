package com.manjurulhoque.musicx;

import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSeekBar;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class PlayerActivity extends AppCompatActivity implements View.OnClickListener, MediaPlayer.OnCompletionListener {

    private CircleImageView mSongImageView;
    private int position;
    static MediaPlayer mMediaPlayer;
    @BindView(R.id.btn_play)
    FloatingActionButton btn_play;
    @BindView(R.id.btn_prev)
    ImageButton btn_prev;
    @BindView(R.id.btn_next)
    ImageButton btn_next;
    List<File> mySongs = new ArrayList<>();
    RotateAnimation rotate;
    @BindView(R.id.textViewSongName)
    TextView textViewSongName;
    @BindView(R.id.tv_song_current_duration)
    TextView mTextViewCurrentDuration;
    @BindView(R.id.tv_song_total_duration)
    TextView mTextViewTotoalDuration;
    @BindView(R.id.seek_song_progressbar)
    AppCompatSeekBar mSeekBar;
    @BindView(R.id.textViewSongArtist)
    TextView textViewSongArtist;

    Thread updateSeekBar;
    int currentPosition = 0;
    Timer timer;
    File songFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        ButterKnife.bind(this);

        mSongImageView = findViewById(R.id.song_image);

        rotate = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(10000);
        rotate.setRepeatCount(Animation.INFINITE);
        rotate.setInterpolator(new LinearInterpolator());
        mSongImageView.setAnimation(rotate);

        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
        }
        updateSeekBar = new Thread() {
            @Override
            public void run() {
                int totalDuration = mMediaPlayer.getDuration();
                while (currentPosition < totalDuration) {
                    try {
                        sleep(500);
                        currentPosition = mMediaPlayer.getCurrentPosition();
                        mSeekBar.setProgress(currentPosition);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        Intent i = getIntent();
        Bundle b = i.getExtras();
        mySongs = Constants.mySongs;
        songFile = (File) getIntent().getExtras().get("song");
        position = b.getInt("position", 0);

        changeSongName(mySongs.get(position));

        Uri u = Uri.parse(mySongs.get(position).toString());
        mMediaPlayer = MediaPlayer.create(getApplicationContext(), u);
        mMediaPlayer.start();
        mSeekBar.setMax(mMediaPlayer.getDuration());
        updateSeekBar.start();
        mTextViewTotoalDuration.setText(getDuration(mySongs.get(position)));
        if (mMediaPlayer.isPlaying()) {
            btn_play.setImageResource(R.drawable.ic_pause);
        }
        updateDuration();

        btn_play.setOnClickListener(this);
        btn_next.setOnClickListener(this);
        btn_prev.setOnClickListener(this);

        mSeekBar.setOnSeekBarChangeListener(new AppCompatSeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mMediaPlayer.seekTo(seekBar.getProgress());
            }
        });

        mMediaPlayer.setOnCompletionListener(this);
    }

    private void updateDuration() {
        if (mMediaPlayer != null) {
            timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                                mTextViewCurrentDuration.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        mTextViewCurrentDuration.setText(getCurrentDuration(mMediaPlayer.getCurrentPosition()));
                                    }
                                });
                            } else {
                                timer.cancel();
                                timer.purge();
                            }
                        }
                    });
                }
            }, 0, 1000);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_play:
                playMusic();
                break;
            case R.id.btn_next:
                playNext();
                break;
            case R.id.btn_prev:
                playPrevious();
                break;
            default:
                break;
        }
    }

    private void changeSongName(File file) {
        textViewSongName.setText(file.getName().replace(".mp3", ""));
        textViewSongArtist.setText(Utils.getSongArtist(file));
    }

    private String getCurrentDuration(long duration) {
        return Utils.formateMilliSeccond(duration);
    }

    private String getDuration(File file) {
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(file.getAbsolutePath());
        String durationStr = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        return Utils.formateMilliSeccond(Long.parseLong(durationStr));
    }

    private void playPrevious() {
        mMediaPlayer.stop();
        mMediaPlayer.release();
        position = position - 1;
        if (position <= 0) {
            position = mySongs.size() - 1;
        }
        Uri u = Uri.parse(mySongs.get((position) % mySongs.size()).toString());
        mMediaPlayer = MediaPlayer.create(getApplicationContext(), u);
        mMediaPlayer.start();
        changeSongName(mySongs.get(position));
    }

    private void playNext() {
        mMediaPlayer.stop();
        mMediaPlayer.release();
        position = position + 1;
        if (position >= mySongs.size() - 1) {
            position = 0;
        }
        Uri u = Uri.parse(mySongs.get((position) % mySongs.size()).toString());
        mMediaPlayer = MediaPlayer.create(getApplicationContext(), u);
        mMediaPlayer.start();
        changeSongName(mySongs.get(position));
    }

    private void playMusic() {
        if (mMediaPlayer.isPlaying()) {
            mSongImageView.clearAnimation();
            mMediaPlayer.pause();
            btn_play.setImageResource(R.drawable.ic_play);
        } else {
            mSongImageView.startAnimation(rotate);
            mMediaPlayer.start();
            btn_play.setImageResource(R.drawable.ic_pause);
        }
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        playNext();
    }
}
