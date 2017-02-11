package com.mylesspencertyler.project3activities;

import android.content.Context;
import android.media.MediaPlayer;

/**
 * Created by tyler on 2/10/2017.
 */

public class AudioActionPlayer {

    private MediaPlayer mPlayer;

    public void stop() {
        if(mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
    }

    public void play(Context c) {
        if(mPlayer == null){
            mPlayer = MediaPlayer.create(c, R.raw.beat_02);
            mPlayer.start();
        }
    }
}
