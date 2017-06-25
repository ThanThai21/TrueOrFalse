package com.esp.trueorfalse;

import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.media.Image;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.esp.customfonttextview.CustomFontTextView;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private ImageButton soundButton;
    private ImageButton trueButton;
    private ImageButton falseButton;
    private CustomFontTextView scoreTextView;
    private CustomFontTextView expressionTextView;
    private CustomFontTextView resultTextView;
    private ProgressBar timer;
    private View gameOver;

    private CustomFontTextView gameOverScore;
    private CustomFontTextView gameOverBestScore;
    private ImageButton gameOverRestart;
    private ImageButton gameOverMenu;

//    private BackgroundSound backgroundSound;

    private boolean soundOn = true;
    private int point = 0;
    private int result;
    private int number1;
    private int number2;
    private int bestScore = 0;
    private int length = 10000;
    private CountDownTimer countDownTimer;
    private MediaPlayer mediaPlayer;
    private AssetFileDescriptor descriptor;

    private SharedPreferences sf;
    private SharedPreferences.Editor editor;

    private static final String CHANGE_SCREEN_AUDIO_FILE = "sound/changeScreen.ogg";
    private static final String CORRECT_AUDIO_FILE = "sound/correct.ogg";
    private static final String INCORRECT_AUDIO_FILE = "sound/incorrect.ogg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        init();
        mapping();
        listener();
        initGame();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        backgroundSound.execute();
    }

    @Override
    protected void onPause() {
        super.onPause();
//        backgroundSound.cancel(true);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void init() {
        sf = getSharedPreferences("tof", MODE_PRIVATE);
        editor = sf.edit();
        bestScore = sf.getInt("best", 0);
        mediaPlayer = new MediaPlayer();
        countDownTimer = new CountDownTimer(length, 1) {
            @Override
            public void onTick(long millisUntilFinished) {
                timer.setProgress((int) millisUntilFinished);
            }

            @Override
            public void onFinish() {
                if (point > bestScore) {
                    bestScore = point;
                    editor.putInt("best", bestScore);
                    editor.commit();
                }
                gameOver.setVisibility(View.VISIBLE);
                gameOverScore.setText(point + "");
                gameOverBestScore.setText(bestScore+"");
                falseButton.setEnabled(false);
                trueButton.setEnabled(false);
            }
        };
    }


    private void initGame() {
        countDownTimer.cancel();
        boolean rs;
        Random rd = new Random();
        if (point < 30) {
            number1 = rd.nextInt(8) + 1;
            number2 = rd.nextInt(8) + 1;
            rs = rd.nextBoolean();
            if (rs) {
                result = number1 + number2;
            } else {
                result = rd.nextInt(17) + 1; //1 -> 18
            }
        } else if (point < 60) {
            number1 = rd.nextInt(89) + 10;
            number2 = rd.nextInt(89) + 10;
            rs = rd.nextBoolean();
            if (rs) {
                result = number1 + number2;
            } else {
                result = rd.nextInt(178) + 20; //20 -> 198
            }
        } else {
            number1 = rd.nextInt(899) + 100;
            number2 = rd.nextInt(899) + 100;
            rs = rd.nextBoolean();
            if (rs) {
                result = number1 + number2;
            } else {
                result = rd.nextInt(1798) + 200; //20 -> 198
            }
        }
        expressionTextView.setText(number1 + "+" + number2);
        resultTextView.setText("=" + result);

        timer.setMax(length);
        timer.setProgress(length);
        countDownTimer.start();
    }

    private void mapping() {
        soundButton = (ImageButton) findViewById(R.id.soundButton);
        trueButton = (ImageButton) findViewById(R.id.trueButton);
        falseButton = (ImageButton) findViewById(R.id.falseButton);
        scoreTextView = (CustomFontTextView) findViewById(R.id.point);
        expressionTextView = (CustomFontTextView) findViewById(R.id.expression);
        resultTextView = (CustomFontTextView) findViewById(R.id.result);
        timer = (ProgressBar) findViewById(R.id.timer);
        gameOver = findViewById(R.id.game_over);

        gameOverScore = (CustomFontTextView) findViewById(R.id.game_over_score);
        gameOverBestScore = (CustomFontTextView) findViewById(R.id.game_over_best_score);
        gameOverRestart = (ImageButton) findViewById(R.id.game_over_restart);
        gameOverMenu = (ImageButton) findViewById(R.id.game_over_menu);

    }

    private void listener() {
        soundButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (soundOn) {
                        soundButton.setImageResource(R.drawable.ic_sound_on_pressed);
                    } else {
                        soundButton.setImageResource(R.drawable.ic_sound_off_pressed);
                    }
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (soundOn) {
                        soundButton.setImageResource(R.drawable.ic_sound_off);
                        IntroActivity.backgroundSound.soundOff();
                    } else {
                        soundButton.setImageResource(R.drawable.ic_sound_on);
                        IntroActivity.backgroundSound.soundOn();
                    }
                    soundOn = !soundOn;
                }
                return false;
            }
        });

        trueButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    trueButton.setImageResource(R.drawable.ic_true_pressed);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    trueButton.setImageResource(R.drawable.ic_true);
                    if (number1 + number2 == result) {
                        point += 1;
                        scoreTextView.setText(point + "");
                        try {
                            mediaPlayer.reset();
                            descriptor = getAssets().openFd(CORRECT_AUDIO_FILE);
                            mediaPlayer.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
                            descriptor.close();
                            mediaPlayer.prepare();
                            mediaPlayer.start();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        initGame();
                    } else {
                        try {
                            mediaPlayer.reset();
                            descriptor = getAssets().openFd(INCORRECT_AUDIO_FILE);
                            mediaPlayer.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
                            descriptor.close();
                            mediaPlayer.prepare();
                            mediaPlayer.start();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if (point > bestScore) {
                            bestScore = point;
                            editor.putInt("best", bestScore);
                            editor.commit();
                        }
                        gameOver.setVisibility(View.VISIBLE);
                        countDownTimer.cancel();
                        gameOverScore.setText(point + "");
                        gameOverBestScore.setText(bestScore+"");
                        soundButton.setEnabled(false);
                        falseButton.setEnabled(false);
                        trueButton.setEnabled(false);
                    }
                }
                return false;
            }
        });

        falseButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    falseButton.setImageResource(R.drawable.ic_false_pressed);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    falseButton.setImageResource(R.drawable.ic_false);
                    if (number1 + number2 != result) {
                        point += 1;
                        scoreTextView.setText(point + "");
                        try {
                            mediaPlayer.reset();
                            descriptor = getAssets().openFd(CORRECT_AUDIO_FILE);
                            mediaPlayer.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
                            descriptor.close();
                            mediaPlayer.prepare();
                            mediaPlayer.start();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        initGame();
                    } else {
                        try {
                            mediaPlayer.reset();
                            descriptor = getAssets().openFd(INCORRECT_AUDIO_FILE);
                            mediaPlayer.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
                            descriptor.close();
                            mediaPlayer.prepare();
                            mediaPlayer.start();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if (point > bestScore) {
                            bestScore = point;
                            editor.putInt("best", bestScore);
                            editor.commit();
                        }
                        gameOver.setVisibility(View.VISIBLE);
                        countDownTimer.cancel();
                        gameOverScore.setText(point + "");
                        gameOverBestScore.setText(bestScore+"");
                        soundButton.setEnabled(false);
                        trueButton.setEnabled(false);
                        falseButton.setEnabled(false);
                    }
                }
                return false;
            }
        });

        //gameOver
        gameOverRestart.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    gameOverRestart.setImageResource(R.drawable.ic_play_pressed);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    gameOverRestart.setImageResource(R.drawable.ic_play);
                    try {
                        mediaPlayer.reset();
                        descriptor = getAssets().openFd(CHANGE_SCREEN_AUDIO_FILE);
                        mediaPlayer.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
                        descriptor.close();
                        mediaPlayer.prepare();
                        mediaPlayer.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    point = 0;
                    scoreTextView.setText("0");
                    initGame();
                    trueButton.setEnabled(true);
                    falseButton.setEnabled(true);
                    soundButton.setEnabled(true);
                    gameOver.setVisibility(View.GONE);
                }
                return false;
            }
        });

        gameOverMenu.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    gameOverMenu.setImageResource(R.drawable.ic_menu_pressed);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    gameOverMenu.setImageResource(R.drawable.ic_menu);
                    try {
                        mediaPlayer.reset();
                        descriptor = getAssets().openFd(CHANGE_SCREEN_AUDIO_FILE);
                        mediaPlayer.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
                        descriptor.close();
                        mediaPlayer.prepare();
                        mediaPlayer.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    finish();
                }
                return false;
            }
        });
    }
}
