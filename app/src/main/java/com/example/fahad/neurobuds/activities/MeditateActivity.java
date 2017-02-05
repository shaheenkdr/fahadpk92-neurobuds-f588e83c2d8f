package com.example.fahad.neurobuds.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.fahad.neurobuds.R;

import java.util.Random;

/**
 * Activity for Meditation with MediaPlayer and Timer
 */
public class MeditateActivity extends AppCompatActivity
{

    private boolean isTurnedOn;
    private MediaPlayer mPlayer;
    private ImageButton playButton;
    private ImageButton timerButton;
    private static int length;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        if(getSupportActionBar()!=null)
            getSupportActionBar().setTitle("Meditate");
        setContentView(R.layout.activity_meditate);

       /* ImageButton im1 = (ImageButton)findViewById(R.id.backMeditate);
        im1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });*/
        isTurnedOn = false;
        mPlayer = MediaPlayer.create(this,R.raw.medx);
        length = 0;
        try{mPlayer.prepare();}catch (Exception e){}
        playButton = (ImageButton)findViewById(R.id.playButton);
        timerButton = (ImageButton)findViewById(R.id.timerButton);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {


                if(!isTurnedOn)
                {
                    playButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.pause));
                    isTurnedOn = true;
                    mPlayer.start();
                    mPlayer.seekTo(length);


                }
                else
                {
                    playButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.play2));
                    isTurnedOn = false;
                    mPlayer.pause();
                    length = mPlayer.getCurrentPosition();
                }

            }
        });

        timerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                final CharSequence[] items = {"1 min", "2 min", "3 min", "5 min", "10 min","15 min"};
                AlertDialog.Builder builder =
                        new AlertDialog.Builder(MeditateActivity.this, R.style.AppCompatAlertDialogStyle);
                builder.setTitle("Set timer");
                builder.setItems(items, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which)
                    {
                        int x=1;

                        switch (which)
                        {
                            case 0: x = 1;
                                break;
                            case 1: x = 2;
                                break;
                            case 2: x = 3;
                                break;
                            case 3: x = 5;
                                break;
                            case 4: x = 10;
                                break;
                            case 5: x = 15;
                                break;
                        }

                        int interval = 60000 * x;
                        Handler handler = new Handler();
                        Runnable runnable = new Runnable()
                        {
                            public void run()
                            {
                                try {
                                    if (mPlayer.isPlaying())
                                    {
                                        mPlayer.stop();
                                    }


                                }
                                catch (Exception e){}
                            }
                        };

                        handler.postAtTime(runnable, System.currentTimeMillis()+interval);
                        handler.postDelayed(runnable, interval);
                        Toast.makeText(MeditateActivity.this, "timer set for "+items[which], Toast.LENGTH_SHORT).show();

                    }

                });
                builder.create().show();

            }
        });



    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            if (mPlayer.isPlaying()) {
                mPlayer.stop();
            }

            mPlayer.release();
        }
        catch (Exception e){}
    }


}
