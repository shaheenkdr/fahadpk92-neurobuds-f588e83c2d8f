package com.example.fahad.neurobuds.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.fahad.neurobuds.BudData;
import com.example.fahad.neurobuds.R;

public class MainActivity extends AppCompatActivity {


    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        textView =(TextView) findViewById(R.id.textView);



    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(hexBroadcastReceiver,new IntentFilter("com.neurobuds.data.hex"));
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(hexBroadcastReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    BroadcastReceiver hexBroadcastReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

//            String hexdata=intent.getStringExtra("hexdata");
//            System.out.println("\n "+hexdata);


            Bundle bundle = intent.getExtras();
            BudData buddata= (BudData) bundle.getSerializable("buddata");
            String presentationString;
            presentationString = "Signal         :  "+buddata.signal+"\n";

            for(int i=0;i<8;i++)
            {
                presentationString+="Value("+i+")     : "+buddata.values.get(i)+"\n";
            }
            presentationString+=  "Attention    :  "+buddata.attention+"\n"
                                 +"Meditation :  "+buddata.meditation+"\n";

            textView.setText(presentationString);

        }
    };


}
