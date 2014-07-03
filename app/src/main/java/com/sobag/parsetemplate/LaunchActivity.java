package com.sobag.parsetemplate;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.MediaController;
import android.widget.VideoView;

public class LaunchActivity extends CommonHeadlessActivity
{
    // ------------------------------------------------------------------------
    // members
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    // default stuff
    // ------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        playVideo();
    }

    @Override
    public void onResume()
    {
        super.onResume();  // Always call the superclass method first

        playVideo();
    }

    // ------------------------------------------------------------------------
    // public usage...
    // ------------------------------------------------------------------------

    public void onLogin(View view)
    {
        Intent loginActivity = new Intent(this,LoginActivity.class);
        startActivity(loginActivity);
    }

    public void onSignup(View view)
    {
        Intent signupActivity = new Intent(this,SignupActivity.class);
        startActivity(signupActivity);
    }

    // ------------------------------------------------------------------------
    // video view handling
    // ------------------------------------------------------------------------

    private void playVideo()
    {
        // display video...
        VideoView myVideoView = (VideoView)findViewById(R.id.videoView);
        myVideoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/"
                + R.raw.evolve_01));
        myVideoView.setOnPreparedListener(PreparedListener);
        myVideoView.requestFocus();
        myVideoView.start();
    }

    MediaPlayer.OnPreparedListener PreparedListener = new MediaPlayer.OnPreparedListener(){

        @Override
        public void onPrepared(MediaPlayer m) {
            try {
                if (m.isPlaying()) {
                    m.stop();
                    m.release();
                    m = new MediaPlayer();
                }
                m.setVolume(0f, 0f);
                m.setLooping(false);
                m.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
}
