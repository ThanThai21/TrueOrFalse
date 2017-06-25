package com.esp.trueorfalse;

import android.app.ActionBar;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;

import java.io.IOException;

public class IntroActivity extends AppCompatActivity {

    private ImageButton playButton;
    private ImageButton rateButton;
    private ImageButton moreButton;

    private MediaPlayer mediaPlayer;
    private AssetFileDescriptor descriptor;

    public static BackgroundSound backgroundSound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        hideStatusbar();
        init();
        mapping();
        listener();

    }

    @Override
    protected void onPause() {
        super.onPause();
        backgroundSound.cancel(true);
    }

    private void hideStatusbar() {
        if (Build.VERSION.SDK_INT < 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }


    private void init() {
        mediaPlayer = new MediaPlayer();
        backgroundSound = new BackgroundSound(this);
        backgroundSound.execute();
    }

    private void mapping() {
        playButton = (ImageButton) findViewById(R.id.playButton);
        rateButton = (ImageButton) findViewById(R.id.rateButton);
        moreButton = (ImageButton) findViewById(R.id.moreButton);
    }

    private void listener() {
        playButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    playButton.setImageResource(R.drawable.ic_play_pressed);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    playButton.setImageResource(R.drawable.ic_play);
                    try {
                        mediaPlayer.reset();
                        descriptor = getAssets().openFd("sound/changeScreen.ogg");
                        mediaPlayer.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
                        descriptor.close();
                        mediaPlayer.prepare();
                        mediaPlayer.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Intent intent = new Intent(IntroActivity.this, MainActivity.class);
                    startActivity(intent);
                }
                return false;
            }
        });

        rateButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    rateButton.setImageResource(R.drawable.ic_rate_pressed);
                    return false;
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    rateButton.setImageResource(R.drawable.ic_rate);
                    AlertDialog.Builder builder = new AlertDialog.Builder(IntroActivity.this);
                    builder.setTitle("Thank you!");
                    builder.setMessage(getString(R.string.rate));
                    builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.create().show();
                    return false;
                }
                return false;
            }
        });

        moreButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    moreButton.setImageResource(R.drawable.ic_more_pressed);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    moreButton.setImageResource(R.drawable.ic_more);
                    AlertDialog.Builder builder = new AlertDialog.Builder(IntroActivity.this);
                    builder.setTitle("More game");
                    builder.setMessage(getString(R.string.more));
                    builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.create().show();
                }
                return false;
            }
        });
    }
}
