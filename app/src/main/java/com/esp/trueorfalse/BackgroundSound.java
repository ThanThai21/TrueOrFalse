package com.esp.trueorfalse;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.AsyncTask;

import java.io.IOException;

public class BackgroundSound extends AsyncTask<Void, Void, Void> {

    private Context context;
    MediaPlayer mediaPlayer;
    AssetFileDescriptor descriptor;

    public BackgroundSound(Context context) {
        this.context = context;
    }

    @Override
    protected Void doInBackground(Void... params) {
        mediaPlayer = new MediaPlayer();

        try {
            mediaPlayer.reset();
            descriptor = context.getAssets().openFd("sound/background_sound_game.mp3");
            mediaPlayer.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
            descriptor.close();
            mediaPlayer.prepare();
            mediaPlayer.setVolume(1, 1);
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mediaPlayer.start();
                }
            });
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void soundOn() {
        mediaPlayer.setVolume(1, 1);
    }

    public void soundOff() {
        mediaPlayer.setVolume(0, 0);
    }
}
