package com.example.fahad.neurobuds;

import android.app.Application;
import android.content.Intent;

/**
 * Created by fahad on 10/17/2016.
 */

public class NeuroBuds extends Application
{

    @Override
    public void onCreate() {
        super.onCreate();

        Intent i = new Intent(getApplicationContext(),BudService.class);
        startService(i);
    }
}
