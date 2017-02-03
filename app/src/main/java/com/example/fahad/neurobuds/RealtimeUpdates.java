
package com.example.fahad.neurobuds;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.Random;

public class RealtimeUpdates extends Fragment {
    private final Handler mHandler = new Handler();
    private Runnable mTimer1;
    private Runnable mTimer2;
    private LineGraphSeries<DataPoint> mSeries1;
    private LineGraphSeries<DataPoint> mSeries2;
    private LineGraphSeries<DataPoint> mSeries3;
    private LineGraphSeries<DataPoint> mSeries4;
    private LineGraphSeries<DataPoint> mSeries5;
    private LineGraphSeries<DataPoint> mSeries6;
    private LineGraphSeries<DataPoint> mSeries7;
    private LineGraphSeries<DataPoint> mSeries8;
    private LineGraphSeries<DataPoint> mSeries9;
    private LineGraphSeries<DataPoint> mSeries10;
    private double graph2LastXValue = 5d;
    private static final String ARG_SECTION_NUMBER = "section_number";

    public RealtimeUpdates() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static RealtimeUpdates newInstance(int sectionNumber) {
            RealtimeUpdates fragment = new RealtimeUpdates();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);


//        GraphView graph = (GraphView) rootView.findViewById(R.id.graph);
//        mSeries1 = new LineGraphSeries<>(generateData());

//        graph.addSeries(mSeries1);

        GraphView graph2 = (GraphView) rootView.findViewById(R.id.graph);

        mSeries1 = new LineGraphSeries<>();
        mSeries2 = new LineGraphSeries<>();
        mSeries3 = new LineGraphSeries<>();
        mSeries4 = new LineGraphSeries<>();
        mSeries5 = new LineGraphSeries<>();
        mSeries6 = new LineGraphSeries<>();
        mSeries7 = new LineGraphSeries<>();
        mSeries8 = new LineGraphSeries<>();
        mSeries9 = new LineGraphSeries<>();
        mSeries10 = new LineGraphSeries<>();

        mSeries1.setColor(Color.YELLOW);
        mSeries2.setColor(Color.LTGRAY);
        mSeries3.setColor(Color.GREEN);
        mSeries4.setColor(Color.CYAN);
        mSeries5.setColor(Color.BLACK);
        mSeries6.setColor(Color.CYAN);
        mSeries7.setColor(Color.BLUE);
        mSeries8.setColor(Color.MAGENTA);
        mSeries9.setColor(Color.MAGENTA);
        mSeries10.setColor(Color.MAGENTA);

        graph2.addSeries(mSeries1);
        graph2.addSeries(mSeries2);
        graph2.addSeries(mSeries3);
        graph2.addSeries(mSeries4);
        graph2.addSeries(mSeries5);
        graph2.addSeries(mSeries6);
        graph2.addSeries(mSeries7);
        graph2.addSeries(mSeries8);
        graph2.addSeries(mSeries9);
        graph2.addSeries(mSeries10);
        graph2.getViewport().setXAxisBoundsManual(true);
        graph2.getViewport().setMinX(0);
        graph2.getViewport().setMaxX(100);
        graph2.getViewport().setMaxY(8000);
        graph2.getViewport().setMinY(0);

        return rootView;
    }

//    @Override
//    public void onAttach(Activity activity) {
//        super.onAttach(activity);
//        ((MainActivity) activity).onSectionAttached(
//                getArguments().getInt(MainActivity.ARG_SECTION_NUMBER));
//    }



    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(hexBroadcastReceiver,new IntentFilter("com.neurobuds.data.hex"));


//        mTimer2 = new Runnable() {
//            @Override
//            public void run() {
//                graph2LastXValue += 1d;
//                mSeries2.appendData(new DataPoint(graph2LastXValue, getRandom()), true, 40);
//                mHandler.postDelayed(this, 200);
//            }
//        };
//        mHandler.postDelayed(mTimer2, 1000);
    }

    @Override
    public void onPause() {
//        mHandler.removeCallbacks(mTimer1);
//        mHandler.removeCallbacks(mTimer2);
        getActivity().unregisterReceiver(hexBroadcastReceiver);
        super.onPause();
    }

    BroadcastReceiver hexBroadcastReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

//            String hexdata=intent.getStringExtra("hexdata");
//            System.out.println("\n "+hexdata);


            Bundle bundle = intent.getExtras();
            BudData buddata= (BudData) bundle.getSerializable("buddata");
//            String presentationString;
//            presentationString = "Signal         :  "+buddata.signal+"\n";

            graph2LastXValue += 3d;
            mSeries1.appendData(new DataPoint(graph2LastXValue, buddata.values.get(0)/1000), true, 60);
            mSeries2.appendData(new DataPoint(graph2LastXValue, buddata.values.get(1)/1000), true, 60);
            mSeries3.appendData(new DataPoint(graph2LastXValue, buddata.values.get(2)/1000), true, 60);
            mSeries4.appendData(new DataPoint(graph2LastXValue, buddata.values.get(3)/1000), true, 60);
            mSeries5.appendData(new DataPoint(graph2LastXValue, buddata.values.get(4)/1000), true, 60);
            mSeries6.appendData(new DataPoint(graph2LastXValue, buddata.values.get(5)/1000), true, 60);
            mSeries7.appendData(new DataPoint(graph2LastXValue, buddata.values.get(6)/1000), true, 60);
            mSeries8.appendData(new DataPoint(graph2LastXValue, buddata.values.get(7)/1000), true, 60);
            mSeries9.appendData(new DataPoint(graph2LastXValue, buddata.values.get(7)/1000), true, 60);
            mSeries10.appendData(new DataPoint(graph2LastXValue, buddata.values.get(7)/1000), true, 60);


//            presentationString+=  "Attention    :  "+buddata.attention+"\n"
//                    +"Meditation :  "+buddata.meditation+"\n";
        }
    };



    double mLastRandom = 2;
    Random mRand = new Random();
//    private double getRandom() {
//        return mLastRandom += mRand.nextDouble()*0.5 - 0.25;
//    }


    @Override
    public void onDestroy() {
        super.onDestroy();

    }




}