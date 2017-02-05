package com.example.fahad.neurobuds;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.Toast;

import com.physicaloid.lib.Physicaloid;
import com.physicaloid.lib.usb.driver.uart.UartConfig;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Exchanger;


/**
 * Created by renlinx on 3/2/15.
 */
public class BudService extends Service {
    static final int PREFERENCE_REV_NUM = 1;
    enum Status{ FirstAA,SecondAA,DATA,JUNK}
    Status status;

    // debug settings
    private static final boolean SHOW_DEBUG                 = false;
    private static final boolean USE_WRITE_BUTTON_FOR_DEBUG = false;

    public static final boolean isICSorHigher = ( Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB_MR2 );

    // occurs USB packet loss if TEXT_MAX_SIZE is over 6000
    private static final int TEXT_MAX_SIZE = 8192;

    private static final int MENU_ID_SETTING        = 0;
    private static final int MENU_ID_CLEARTEXT      = 1;
    private static final int MENU_ID_SENDTOEMAIL    = 2;
    private static final int MENU_ID_OPENDEVICE     = 3;
    private static final int MENU_ID_CLOSEDEVICE    = 4;
    private static final int MENU_ID_WORDLIST       = 5;

    private static final int REQUEST_PREFERENCE         = 0;
    private static final int REQUEST_WORD_LIST_ACTIVITY = 1;

    // Defines of Display Settings
    private static final int DISP_CHAR  = 0;
    private static final int DISP_DEC   = 1;
    private static final int DISP_HEX   = 2;

    // Linefeed Code Settings
    private static final int LINEFEED_CODE_CR   = 0;
    private static final int LINEFEED_CODE_CRLF = 1;
    private static final int LINEFEED_CODE_LF   = 2;

    // Load Bundle Key (for view switching)
    private static final String BUNDLEKEY_LOADTEXTVIEW = "bundlekey.LoadTextView";

    Physicaloid mSerial;

    private StringBuilder mText = new StringBuilder();
    private boolean mStop = false;

    String TAG = "AndroidSerialTerminal";

    Handler mHandler = new Handler();



    // Default settings
    private int mTextFontSize       = 12;
    private Typeface mTextTypeface  = Typeface.MONOSPACE;
    private int mDisplayType        = DISP_CHAR;
    private int mReadLinefeedCode   = LINEFEED_CODE_LF;
    private int mWriteLinefeedCode  = LINEFEED_CODE_LF;
    private int mBaudrate           = 57600;
    private int mDataBits           = UartConfig.DATA_BITS8;
    private int mParity             = UartConfig.PARITY_NONE;
    private int mStopBits           = UartConfig.STOP_BITS1;
    private int mFlowControl        = UartConfig.FLOW_CONTROL_OFF;
    private String mEmailAddress    = "@gmail.com";

    private boolean mRunningMainLoop = false;

    private static final String ACTION_USB_PERMISSION =
            "jp.ksksue.app.terminal.USB_PERMISSION";

    // Linefeed
    private final static String BR = System.getProperty("line.separator");
    private Queue<String> queue;
    private int ii;
    private int jj;

    BudData budData ;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

         queue = new LinkedList<String>();


        // get service
        mSerial = new Physicaloid(this);

        if (SHOW_DEBUG) {
            Log.d(TAG, "New instance : " + mSerial);
        }
        // listen for new devices
        IntentFilter filter = new IntentFilter();
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        registerReceiver(mUsbReceiver, filter);

        if (SHOW_DEBUG) {
            Log.d(TAG, "FTDriver beginning");
        }

        openUsbSerial();



        return START_STICKY;
    }



    void setSerialDataToTextView(int disp, byte[] rbuf, int len, String sCr, String sLf) {
        int tmpbuf;
        for (int i = 0; i < len; ++i) {
            if (SHOW_DEBUG) {
                Log.d(TAG, "Read  Data[" + i + "] : " + rbuf[i]);
            }

            // "\r":CR(0x0D) "\n":LF(0x0A)
            if ((mReadLinefeedCode == LINEFEED_CODE_CR) && (rbuf[i] == 0x0D)) {
                mText.append(sCr);
                mText.append(BR);
            } else if ((mReadLinefeedCode == LINEFEED_CODE_LF) && (rbuf[i] == 0x0A)) {
                mText.append(sLf);
                mText.append(BR);
            } else if ((mReadLinefeedCode == LINEFEED_CODE_CRLF) && (rbuf[i] == 0x0D)
                    && (rbuf[i + 1] == 0x0A)) {
                mText.append(sCr);
                if (disp != DISP_CHAR) {
                    mText.append("");
                }
                mText.append(sLf);
                mText.append(BR);
                ++i;
            } else if ((mReadLinefeedCode == LINEFEED_CODE_CRLF) && (rbuf[i] == 0x0D)) {
                // case of rbuf[last] == 0x0D and rbuf[0] == 0x0A
                mText.append(sCr);
                lastDataIs0x0D = true;
            } else if (lastDataIs0x0D && (rbuf[0] == 0x0A)) {
                if (disp != DISP_CHAR) {
                    mText.append("");
                }
                mText.append(sLf);
                mText.append(BR);
                lastDataIs0x0D = false;
            } else if (lastDataIs0x0D && (i != 0)) {
                // only disable flag
                lastDataIs0x0D = false;
                --i;
            } else {
//                switch (disp) {
//                    case DISP_CHAR:
//                        mText.append((char) rbuf[i]);
//                        break;
//                    case DISP_DEC:
//                        tmpbuf = rbuf[i];
//                        if (tmpbuf < 0) {
//                            tmpbuf += 256;
//                        }
//                        mText.append(String.format("%1$03d", tmpbuf));
//                        mText.append(" ");
//                        break;
//                    case DISP_HEX:
                       mText.append(IntToHex2((int) rbuf[i]));
                       mText.append("");



//                        break;
//                    default:
//                        break;
//                }
            }
        }
    }

    private String IntToHex2(int Value) {
        char HEX2[] = {
                Character.forDigit((Value >> 4) & 0x0F, 16),
                Character.forDigit(Value & 0x0F, 16)
        };
        String Hex2Str = new String(HEX2);
        return Hex2Str;
    }

    boolean lastDataIs0x0D = false;


    private Runnable mLoop = new Runnable() {
        @Override
        public void run() {
            int len;
            byte[] rbuf = new byte[4096];

            for (;;) {// this is the main loop for transferring

                // ////////////////////////////////////////////////////////
                // Read and Display to Terminal
                // ////////////////////////////////////////////////////////
                len = mSerial.read(rbuf);
                rbuf[len] = 0;

                if (len > 0) {
                    if (SHOW_DEBUG) {
                        Log.d(TAG, "Read  Length : " + len);
                    }

//                    switch (mDisplayType) {
//                        case DISP_CHAR:
//                            setSerialDataToTextView(mDisplayType, rbuf, len, "", "");
//                            break;
//                        case DISP_DEC:
//                            setSerialDataToTextView(mDisplayType, rbuf, len, "013", "010");
//                            break;
//                        case DISP_HEX:
                            setSerialDataToTextView(mDisplayType, rbuf, len, "0d", "0a");
//                            break;
//                    }

                    mHandler.post(new Runnable() {
                        public void run()
                        {
                            String data=mText.toString();
                            System.out.println((ii++)+"-data :"+data);
                            for(int i=0;i<data.length();i=i+2)
                            {
                                String sub=data.substring(i,i+2);
                                System.out.print(sub+" ");
                                queue.add(sub.replace(" ",""));
                            }
                            System.out.println("");
                            mText.setLength(0);

//                            if (mText.toString().replace(" ","").length() ==72)
//                            {
////                                String parsedData = parseData(mText.toString());
////
//                                String parsedData = mText.toString();
////                                if (parsedData != null) {
//                                    Intent bIntent = new Intent("com.neurobuds.data.hex");
////                            System.out.println("\n On Service ---- "+ mText.toString());
//                                    bIntent.putExtra("hexdata", parsedData);
//                                    sendBroadcast(bIntent);
////                                }
//                                mText.setLength(0);
//
//                            }
////                            else
////                            {
////                                System.out.println(mText.toString().replace(" ","").length());
////                                mText.setLength(0);
////                            }

                        }
                    });

                    mHandler.post(new Runnable() {
                        public void run() {
                            parse();
                        }
                    });
                }

                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (mStop) {
                    mRunningMainLoop = false;
                    return;
                }
            }
        }
    };

    private void parse() {




       while(queue.peek()!=null) {
               switch (status) {
                   case JUNK:
                       if (queue.peek().equals("aa")) {
                           status = Status.FirstAA;
                           System.out.println(Status.FirstAA);
                           queue.remove();
                       }
                       else
                        queue.remove();
                       break;
                   case FirstAA:
                       if (queue.peek().equals("aa")) {
                           status = Status.SecondAA;
                           System.out.println(Status.SecondAA);
                           queue.remove();
                       }
                       else {
                           status = Status.JUNK;
                           System.out.println(Status.JUNK);
                       }
                       break;
                   case SecondAA:
                       if (queue.peek().equals("20")) {
                           status = Status.DATA;
                           System.out.println(Status.DATA);
                           queue.remove();
                       }
                       else {
                           status = Status.JUNK;
                           System.out.println(Status.JUNK);
                       }
                       break;
                   case DATA:
                       String parsedData="";
                       if (queue.size() >= 32) {
                           for (int i = 0; i < 32; i++)
                               parsedData += queue.remove();
                           System.out.println("parsedData" + parsedData);
                           PacketConversion(parsedData);
                       }
                       status=Status.JUNK;
                       System.out.println(Status.JUNK);
                       break;
               }

       }


    }

    private void PacketConversion(String packet) {
        System.out.println("packet "+ packet);
        int tempInt2;
        try {
            while (packet.length() >= 2) {

                switch (Integer.parseInt(packet.substring(0, 2))) {
                    case 2:

                        tempInt2 = (int) Long.parseLong(packet.substring(2, 4), 16);
                        System.out.println("Signal :" + tempInt2);
                        budData.signal = tempInt2;
                        packet = packet.substring(4);
                        break;
                    case 4:
                        tempInt2 = (int) Long.parseLong(packet.substring(2, 4), 16);
                        System.out.println("Attention :" + tempInt2);
                        budData.attention = tempInt2;
                        packet = packet.substring(4);
                        break;
                    case 5:
                        tempInt2 = (int) Long.parseLong(packet.substring(2, 4), 16);
                        System.out.println("Meditation :" + tempInt2);
                        budData.meditation = tempInt2;
                        packet = packet.substring(4);
                        break;
                    case 83:
                        tempInt2 = (int) Long.parseLong(packet.substring(2, 4), 16);
                        System.out.println("Length :" + tempInt2);
                        budData.values = new ArrayList<>();
                        packet = packet.substring(4);
                        for (int i = 0; i < 8; i++) {
                            budData.values.add(Long.parseLong(packet.substring(0, 6), 16));
                            System.out.println("Value (" + i + ") :" + Long.parseLong(packet.substring(0, 6), 16));
                            packet = packet.substring(6);
                        }
                        break;
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();;
        }

        brodacastBudData(budData);

    }

    private void brodacastBudData(BudData budData) {
        Intent bIntent = new Intent("com.neurobuds.data.hex");
        Bundle bundle= new Bundle();
        bundle.putSerializable("buddata",budData);
        bIntent.putExtras(bundle);
        sendBroadcast(bIntent);
    }


    private void openUsbSerial() {

        ii=0;
        jj=0;

        budData = new BudData();

        status=Status.JUNK;
        if(mSerial == null) {
            Toast.makeText(this, "cannot open", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!mSerial.isOpened()) {
            if (SHOW_DEBUG) {
                Log.d(TAG, "onNewIntent begin");
            }
            if (!mSerial.open()) {
                Toast.makeText(this, "cannot open", Toast.LENGTH_SHORT).show();
                return;
            } else {
                loadDefaultSettingValues();

                boolean dtrOn=false;
                boolean rtsOn=false;
                if(mFlowControl == UartConfig.FLOW_CONTROL_ON) {
                    dtrOn = true;
                    rtsOn = true;
                }
                mSerial.setConfig(new UartConfig(mBaudrate, mDataBits, mStopBits, mParity, dtrOn, rtsOn));

                if(SHOW_DEBUG) {
                    Log.d(TAG, "setConfig : baud : "+mBaudrate+", DataBits : "+mDataBits+", StopBits : "+mStopBits+", Parity : "+mParity+", dtr : "+dtrOn+", rts : "+rtsOn);
                }

//                mTvSerial.setTextSize(mTextFontSize);

                Toast.makeText(this, "connected", Toast.LENGTH_SHORT).show();
            }
        }

        if (!mRunningMainLoop) {
            mainloop();
        }

    }

    private void mainloop() {
        mStop = false;
        mRunningMainLoop = true;
        Toast.makeText(this, "connected", Toast.LENGTH_SHORT).show();
        if (SHOW_DEBUG) {
            Log.d(TAG, "start mainloop");
        }
        new Thread(mLoop).start();
    }

    BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
                if (SHOW_DEBUG) {
                    Log.d(TAG, "Device attached");
                }
                if (!mSerial.isOpened()) {
                    if (SHOW_DEBUG) {
                        Log.d(TAG, "Device attached begin");
                    }
                    openUsbSerial();
                }
                if (!mRunningMainLoop) {
                    if (SHOW_DEBUG) {
                        Log.d(TAG, "Device attached mainloop");
                    }
                    mainloop();
                }
            } else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                if (SHOW_DEBUG) {
                    Log.d(TAG, "Device detached");
                }
                mStop = true;
//                detachedUi();
//                mSerial.usbDetached(intent);
                mSerial.close();
            } else if (ACTION_USB_PERMISSION.equals(action)) {
                if (SHOW_DEBUG) {
                    Log.d(TAG, "Request permission");
                }
                synchronized (this) {
                    if (!mSerial.isOpened()) {
                        if (SHOW_DEBUG) {
                            Log.d(TAG, "Request permission begin");
                        }
                        openUsbSerial();
                    }
                }
                if (!mRunningMainLoop) {
                    if (SHOW_DEBUG) {
                        Log.d(TAG, "Request permission mainloop");
                    }
                    mainloop();
                }
            }
        }
    };


    void loadDefaultSettingValues() {

        String res =  Integer.toString(DISP_CHAR);
        mDisplayType = Integer.valueOf(res);

        res =  Integer.toString(12);
        mTextFontSize = Integer.valueOf(res);

        res = Integer.toString(3);
        switch(Integer.valueOf(res)){
            case 0:
                mTextTypeface = Typeface.DEFAULT;
                break;
            case 1:
                mTextTypeface = Typeface.SANS_SERIF;
                break;
            case 2:
                mTextTypeface = Typeface.SERIF;
                break;
            case 3:
                mTextTypeface = Typeface.MONOSPACE;
                break;
        }

        res = Integer.toString(LINEFEED_CODE_CRLF);
        mReadLinefeedCode = Integer.valueOf(res);

        res =  Integer.toString(LINEFEED_CODE_CRLF);
        mWriteLinefeedCode = Integer.valueOf(res);

        res =  "asd@gmail.com";
        mEmailAddress = res;

        res =  Integer.toString(9600);
        mBaudrate = Integer.valueOf(res);

        res =  Integer.toString(UartConfig.DATA_BITS8);
        mDataBits = Integer.valueOf(res);

        res = Integer.toString(UartConfig.PARITY_NONE);
        mParity = Integer.valueOf(res);

        res =  Integer.toString(UartConfig.STOP_BITS1);
        mStopBits = Integer.valueOf(res);

        res = Integer.toString(UartConfig.FLOW_CONTROL_OFF);
        mFlowControl = Integer.valueOf(res);
    }




}
