package com.example.peterdjeneralovic.projectpomegranate;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

public class drawgraph extends View{
    public static final int SMALL = 1;
    public static final int MEDIUM = 2;
    public static final int LARGE = 4;

    private float view_width;
    private float view_height;
    private int eeg_data[];
    private int num;
    private String type;
    public int zoom_level;
    private LayoutParams params;


    public drawgraph(Context context) {
        super(context);
//		setHorizontalScrollBarEnabled(true);
        Log.d("Draw Graph","Create 1");
        num = 0;
        eeg_data = new int[25000];
        type = "";
        zoom_level = MEDIUM;
    }

    public drawgraph(Context paramContext, AttributeSet paramAttributeSet) {
        super(paramContext, paramAttributeSet);
//	    setHorizontalScrollBarEnabled(true);
        Log.d("Draw Graph","Create 2");
        num = 0;
        eeg_data = new int[25000];
        type = "";
        zoom_level = MEDIUM;
    }

    public drawgraph(Context paramContext, AttributeSet paramAttributeSet, int paramInt) {
        super(paramContext, paramAttributeSet, paramInt);
//	    setHorizontalScrollBarEnabled(true);
        Log.d("Draw Graph","Create 3");
        num = 0;
        eeg_data = new int[25000];
        type = "";
        zoom_level = MEDIUM;
    }

    public void addData(int data1[], int size) {
        Log.d("Draw Graph","eeg: " + data1);
        for (int i = 0; i < size; i++) {
            eeg_data[i] = data1[i];
        }
        num = size;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float []pts = new float[25000*2];
        int i;

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(3);

        //Draw EEG
        paint.setColor(Color.RED);
        for(i=0;i<num;++i) {
            pts[i*2] = i/320.0F*(view_width);
            pts[i*2+1] = view_height - eeg_data[i]/4096.0F*(view_height);
        }

        for(i=0;i<num-1;++i) {
            canvas.drawLine(pts[2*i],pts[2*i+1],pts[2*(i+1)],pts[2*(i+1)+1],paint);
        }
    }

    protected void onSizeChanged(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
        super.onSizeChanged(paramInt1, paramInt2, paramInt3, paramInt4);
        Log.d("Draw Graph","" + paramInt1 + "," + paramInt2 + "," + paramInt3 + "," + paramInt4);
        view_width = paramInt1;
        view_height = paramInt2;
    }

    public void setType(String newType) {
        type = newType;
    }

}

