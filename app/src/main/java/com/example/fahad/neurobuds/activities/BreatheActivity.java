package com.example.fahad.neurobuds.activities;

import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.fahad.neurobuds.R;

/**
 * @author  OBX
 * Activity to play Audio with the help of count down timer
 */
public class BreatheActivity extends AppCompatActivity {

    private MediaPlayer mPlayer;
    private long s1;
    private MyCount counter;
    private boolean isPlaying = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_breathe);

        Button mButton = (Button)findViewById(R.id.musicPlayButton);
        mPlayer = MediaPlayer.create(this,R.raw.fst);
        try
        {
            mPlayer.prepare();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        mPlayer.setLooping(true);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isPlaying)
                {
                    isPlaying = false;
                    mPlayer.pause();
                }
                else
                {
                    isPlaying = true;
                    mPlayer.start();
                    counter= new MyCount(60000,2000);
                    counter.start();
                }
            }
        });


    }

    private class MyCount extends CountDownTimer
    {
        public MyCount(long millisInFuture, long countDownInterval)
        {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            mPlayer.stop();
            mPlayer.release();
        }

        @Override
        public void onTick(long millisUntilFinished)
        {
            Log.e("TAG","VAL:"+millisUntilFinished);
            s1=millisUntilFinished;
            if(millisUntilFinished<=40000)
            {
                mPlayer.setVolume(0.2f,0.2f);
            }

            if(millisUntilFinished<=20000)
            {
                mPlayer.setVolume(1.0f,1.0f);
            }

        }
    }
}
