package com.example.fahad.neurobuds.activities;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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
    private TextView clockText;
    private Button mButton;
    private Vibrator v;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_breathe);

        clockText = (TextView)findViewById(R.id.breatheText);
        mButton = (Button)findViewById(R.id.musicPlayButton);
        v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
        mButton.setText("Start");
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
        counter= new MyCount(13000,1000);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isPlaying)
                {
                    mButton.setText("Start");
                    isPlaying = false;
                    counter.cancel();
                    mPlayer.pause();
                }
                else
                {
                    mButton.setText("Stop");
                    clockText.setText("deep breathe in");
                    v.vibrate(100);
                    isPlaying = true;
                    mPlayer.start();
                    counter= new MyCount(s1,1000);
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
            counter= new MyCount(13000,1000);
            counter.start();
            clockText.setText("deep breathe in");
            v.vibrate(100);

        }

        @Override
        public void onTick(long millisUntilFinished)
        {
            Log.e("TAG","VAL:"+millisUntilFinished);
            s1=millisUntilFinished;
            if(millisUntilFinished<=8000 && millisUntilFinished>=7000)
            {
                mPlayer.setVolume(0.2f,0.2f);
                clockText.setText("hold");
                v.vibrate(100);
            }

            if(millisUntilFinished<=5000 && millisUntilFinished>=4000)
            {
                clockText.setText("deep breathe out");
                mPlayer.setVolume(1.0f,1.0f);
                v.vibrate(100);
            }

        }
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        try
        {
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
        }
        catch (Exception e){e.printStackTrace();}
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        try
        {
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
        }
        catch (Exception e){e.printStackTrace();}

    }
}
