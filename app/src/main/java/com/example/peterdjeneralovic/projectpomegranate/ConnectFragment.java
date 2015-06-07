package com.example.peterdjeneralovic.projectpomegranate;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Transformation;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.neurosky.thinkgear.TGDevice;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

//import com.ibm.mobile.services.core.*;

public class ConnectFragment extends Fragment {
    private final static String TAG = "ConnectFragment";
    Activity mainActivity = null;
    BluetoothAdapter bluetoothAdapter;
    View rootView;
    TextView tv;
    Button b1, b2;
    Integer previousvalue = 0;
    ProgressBar progress =null;
    TextView text = null;

    TGDevice tgDevice;
    final boolean rawEnabled = false;

    //all time is in milliseconds
    long starttime = 0, timestamp = 0;

    JSONArray joArray = new JSONArray();
    JSONObject jo = new JSONObject();
    long duration = 1 * 60000;

    /** Called when the activity is first created. */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rootView = inflater.inflate(R.layout.fragment_container, container, false);

        mainActivity = getActivity();

        tv = (TextView)rootView.findViewById(R.id.textView1);
        tv.setText("");
        tv.append("Android version: " + Integer.valueOf(android.os.Build.VERSION.SDK) + "\n" );

        b1 = (Button) rootView.findViewById(R.id.button1);
        b1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                doConnect(v);
            }
        });

        b2 = (Button) rootView.findViewById(R.id.button2);
        b2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                doDisconnect(v);
            }
        });

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(bluetoothAdapter == null) {
            // Alert user that Bluetooth is not available
            Toast.makeText(rootView.getContext(), "Bluetooth not available", Toast.LENGTH_LONG).show();
            //finish();
        }else {
        	/* create the TGDevice */
            tgDevice = new TGDevice(bluetoothAdapter, handler);
        }

        progress =  (ProgressBar) rootView.findViewById(R.id.circularProgressbar);

        ProgressBarAnimation anim = new ProgressBarAnimation(progress, 0, 20);
        text = (TextView) rootView.findViewById(R.id.numberText);
        anim.setDuration(1000);
        progress.startAnimation(anim);

        return rootView;
    }

    @Override
    public void onDestroy() {
        tgDevice.close();
        super.onDestroy();
    }

    /**
     * Handles messages from TGDevice
     */
    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case TGDevice.MSG_STATE_CHANGE:

                    switch (msg.arg1) {
                        case TGDevice.STATE_IDLE:
                            break;
                        case TGDevice.STATE_CONNECTING:
                            tv.append("Connecting...\n");
                            break;
                        case TGDevice.STATE_CONNECTED:
                            tv.append("Connected.\n");
                            tgDevice.start();
                            break;
                        case TGDevice.STATE_NOT_FOUND:
                            tv.append("Can't find\n");
                            break;
                        case TGDevice.STATE_NOT_PAIRED:
                            tv.append("not paired\n");
                            break;
                        case TGDevice.STATE_DISCONNECTED:
                            tv.append("Disconnected mang\n");

                    }

                    break;
                case TGDevice.MSG_POOR_SIGNAL:
                    //signal = msg.arg1;
                    //tv.append("PoorSignal: " + msg.arg1 + "\n");
                    break;
                case TGDevice.MSG_RAW_DATA:
                    //raw1 = msg.arg1;
                    //tv.append("Got raw: " + msg.arg1 + "\n");
                    break;
                case TGDevice.MSG_HEART_RATE:
                    //tv.append("Heart rate: " + msg.arg1 + "\n");
                    break;
                case TGDevice.MSG_ATTENTION: {
                    //att = msg.arg1;
                    tv.append("Attention: " + msg.arg1 + "\n");
                    timestamp = System.currentTimeMillis();
                    try {

                        Long tsLong = System.currentTimeMillis()/1000;
                        String ts = tsLong.toString();

                        ProgressBarAnimation anim = new ProgressBarAnimation(progress, previousvalue, msg.arg1);
                        anim.setDuration(1000);
                        progress.startAnimation(anim);
                        text.setText(String.valueOf(msg.arg1));
                        jo.put("score", msg.arg1);
                        jo.put("sessionid", 0001);
                        jo.put("timestamp", ts);

                        previousvalue = msg.arg1;


                        ((MainActivity)getActivity()).CloudPost(jo);
                        Log.v(TAG, "json: " + jo.toString() + "\n");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if(timestamp - starttime >= duration) {
                        //send json object
                        tgDevice.close();
                        jo = new JSONObject();
                        Log.v(TAG, "timeout!\n");

                    }
                    //Log.v("HelloA", "Attention: " + att + "\n");
                    break;
                }
                case TGDevice.MSG_MEDITATION:

                    break;
                case TGDevice.MSG_BLINK:
                    //tv.append("Blink: " + msg.arg1 + "\n");
                    break;
                case TGDevice.MSG_RAW_COUNT:
                    //tv.append("Raw Count: " + msg.arg1 + "\n");
                    break;
                case TGDevice.MSG_LOW_BATTERY:
                    Toast.makeText(rootView.getContext(), "Low battery!", Toast.LENGTH_SHORT).show();
                    break;
                case TGDevice.MSG_RAW_MULTI:
                    //TGRawMulti rawM = (TGRawMulti)msg.obj;
                    //tv.append("Raw1: " + rawM.ch1 + "\nRaw2: " + rawM.ch2);
                default:
                    break;
            }
        }
    };

    public void doConnect(View view) {
        if(tgDevice.getState() != TGDevice.STATE_CONNECTING && tgDevice.getState() != TGDevice.STATE_CONNECTED) {
            starttime = System.currentTimeMillis();
            tgDevice.connect(rawEnabled);
        }
        //tgDevice.ena
    }

    public void doDisconnect(View view) {
        if(tgDevice.getState() == TGDevice.STATE_CONNECTED)
            tgDevice.close();
        //tgDevice.ena
    }

    public class ProgressBarAnimation extends Animation{
        private ProgressBar progressBar;
        private float from;
        private float  to;

        public ProgressBarAnimation(ProgressBar progressBar, float from, float to) {
            super();
            this.progressBar = progressBar;
            this.from = from;
            this.to = to;
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            super.applyTransformation(interpolatedTime, t);
            float value = from + (to - from) * interpolatedTime;
            progressBar.setProgress((int) value);
        }

    }

}