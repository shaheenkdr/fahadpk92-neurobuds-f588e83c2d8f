package com.example.fahad.neurobuds.activities;

import android.app.ProgressDialog;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

import com.example.fahad.neurobuds.R;

import co.mobiwise.library.InteractivePlayerView;
import co.mobiwise.library.OnActionClickedListener;

import static android.provider.ContactsContract.CommonDataKinds.Website.URL;

public class MusicActivity extends AppCompatActivity implements OnActionClickedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if(getSupportActionBar()!=null)
            getSupportActionBar().setTitle("Music");
        setContentView(R.layout.activity_music);

        final MediaPlayer mPlayer = MediaPlayer.create(this,R.raw.thd);
        try
        {
            mPlayer.prepare();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        final InteractivePlayerView ipv = (InteractivePlayerView) findViewById(R.id.ipv);
        ipv.setMax(123);
        ipv.setProgress(0);
        ipv.setOnActionClickedListener(this);


        final ImageView control = (ImageView) findViewById(R.id.control);
        control.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!ipv.isPlaying()){
                    ipv.start();
                    mPlayer.start();
                    control.setBackgroundResource(R.drawable.pause);
                }
                else{
                    ipv.stop();
                    mPlayer.pause();
                    control.setBackgroundResource(R.drawable.play);
                }
            }
        });
    }

    @Override
    public void onActionClicked(int id) {
        switch (id){
            case 1:
                //Called when 1. action is clicked.
                break;
            case 2:
                //Called when 2. action is clicked.
                break;
            case 3:
                //Called when 3. action is clicked.
                break;
            default:
                break;
        }
    }
}
