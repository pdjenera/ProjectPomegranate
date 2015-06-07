package com.example.peterdjeneralovic.projectpomegranate;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Random;


import com.example.peterdjeneralovic.projectpomegranate.imagezoom.DynamicZoomControl;
import com.example.peterdjeneralovic.projectpomegranate.imagezoom.ImageZoomView;
import com.example.peterdjeneralovic.projectpomegranate.imagezoom.LongPressZoomListener;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector.OnGestureListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ZoomControls;

public class Graph extends Fragment {
    public static final int SMALL = 1;
    public static final int MEDIUM = 2;
    public static final int LARGE = 4;
    public static final int MIN_ZOOM = 1;
    public static final int MAX_ZOOM = 4;
//	public static final int PORTRAIT = 1;
//	public static final int LANDSCAPE = 2;

    /**
     * Constant used as menu item id for resetting zoom state
     */
    private static final int MENU_ID_RESET = 0;

    /** Image zoom view */
    //private ImageZoomView mZoomView;

    /** Zoom control */
    //private DynamicZoomControl mZoomControl;

    /**
     * Decoded bitmap image
     */
    private Bitmap mBitmap;

    /**
     * On touch listener for zoom view
     */
    //private LongPressZoomListener mZoomListener;

    //layout views
    private TextView date;
    private TextView time;
    private TextView score;
    private ImageView image;
    private drawgraph drawing;
    private TextView title;
    private LinearLayout layout1;
    private LinearLayout layout2;
    private LinearLayout scrollbody;
    //private ZoomControls zoomControls;
    private myHScrollView scrollview;

    private Cursor mDbCursor;
    private Bundle extras;

    public int eeg_data[];

    private boolean landscape = false;
    private float startX = 0;
    private float startY = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.e("Graph", "onCreate");
        super.onCreate(savedInstanceState);
        View rootView = inflater.inflate(R.layout.graph, container, false);

        setup(rootView);

        int height = drawing.getHeight();
        int width = drawing.getWidth();
        Log.d("Graph", "height/width " + height + " " + width);
        return rootView;
    }

    public void setup(View view) {
        int mOrientation = getResources().getConfiguration().orientation;
        if (mOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            landscape = true;
        } else {
            landscape = false;
        }

        Log.i("Graph", "landscape= " + landscape);

        eeg_data = new int[25000];
        Log.i("canvas array", "array size = " + eeg_data.length);

        date = (TextView) view.findViewById(R.id.DATE);
        time = (TextView) view.findViewById(R.id.TIME);
        score = (TextView) view.findViewById(R.id.Score);
        title = (TextView) view.findViewById(R.id.textview02);
        scrollview = (myHScrollView) view.findViewById(R.id.scrollView01);
        drawing = (drawgraph) view.findViewById(R.id.scrolling);
        layout1 = (LinearLayout) view.findViewById(R.id.linearLayout01);
        layout2 = (LinearLayout) view.findViewById(R.id.linearLayout02);
        scrollbody = (LinearLayout) view.findViewById(R.id.scrollBody);
        //zoomControls = (ZoomControls) findViewById(R.id.zoomControls1);

        date.setText("2015-06-07");
        time.setText("12:00:00");
        score.setText("50");

        Canvas d = new Canvas();

        //plot the graph of eeg
        Log.i("eeg", "draw eeg");
        drawing.setType("eeg");

        Random rand = new Random();
        for (int i = 0; i < 1800; i++)
            eeg_data[i] = (rand.nextInt(3000) + 0);

        drawing.addData(eeg_data, 1800);
        Log.i("Graph", "eeg-please call onDraw()");
        drawing.invalidate();
//       onDraw(d, eeg_data, num, height, width);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}