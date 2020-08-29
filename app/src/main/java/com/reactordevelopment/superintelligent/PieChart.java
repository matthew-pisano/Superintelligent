package com.reactordevelopment.superintelligent;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.provider.ContactsContract;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.ArrayList;

import static com.reactordevelopment.superintelligent.MainActivity.inchHeight;
import static com.reactordevelopment.superintelligent.MainActivity.screenHeight;
import static com.reactordevelopment.superintelligent.MainActivity.screenWidth;

public class PieChart {
    private ArrayList<String[]> data;
    private Bitmap chart;
    private ImageView pieChart;
    private ImageView shine;
    private TextView title;
    private RelativeLayout pieChartLayout;
    private LinearLayout labelLayout;
    private LinearLayout colorLayout;
    private Context context;
    private String titleTxt;
    private boolean illion;
    private String unit;
    private int radius;
    public PieChart(Context context, String titleTxt, int radius, boolean illion, String unit){
        this.illion = illion;
        this.unit = unit;
        this.titleTxt = titleTxt;
        this.radius = radius;
        this.context = context;
        title = new TextView(context);
        title.setText(titleTxt);
        title.setTextSize(TypedValue.COMPLEX_UNIT_PX, radius/5);
        title.setTextColor(Color.BLACK);
        title.setLayoutParams(new ConstraintLayout.LayoutParams(radius*2, radius/4));
        title.setGravity(Gravity.CENTER);
        shine = new ImageView(context);
        pieChart = new ImageView(context);
        pieChartLayout = new RelativeLayout(context);
        labelLayout = new LinearLayout(context);
        colorLayout = new LinearLayout(context);
        data = new ArrayList<>(0);
        shine.setBackgroundResource(R.drawable.circleshine);
        chart = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.braintech), radius*2, radius*2, false);
        pieChart.setLayoutParams(new ConstraintLayout.LayoutParams(radius*2, radius*2));
        shine.setLayoutParams(pieChart.getLayoutParams());
        pieChartLayout.setLayoutParams(new ConstraintLayout.LayoutParams(radius*4, 5*radius/2));
        labelLayout.setLayoutParams(new ConstraintLayout.LayoutParams(radius*2, radius*2));
        colorLayout.setLayoutParams(new ConstraintLayout.LayoutParams(radius/6, radius*2));
        labelLayout.setOrientation(LinearLayout.VERTICAL);
        colorLayout.setOrientation(LinearLayout.VERTICAL);
        pieChart.setScaleType(ImageView.ScaleType.FIT_XY);
        data.add(new String[]{"1", ""+Color.argb(255, 100, 100, 100), "No Data"});
        Log.i("ChartMake", ""+System.currentTimeMillis());
        fillPieChart();
        Log.i("ChartMake", ""+System.currentTimeMillis());
        pieChartLayout.addView(pieChart);
        pieChartLayout.addView(title);
        pieChartLayout.addView(labelLayout);
        pieChartLayout.addView(colorLayout);
        pieChartLayout.addView(shine);
        shine.setAlpha(.25f);
        labelLayout.animate().x(radius*100/42).y(radius/4).setDuration(0);
        colorLayout.animate().x(radius*100/48).y(radius/4).setDuration(0);
        pieChart.animate().y(radius/4).setDuration(0);
        shine.animate().y(radius/4).setDuration(0);
    }
    public void addToLayout(ConstraintLayout layout, float x, float y){
        layout.addView(pieChartLayout);
        pieChartLayout.animate().x(x).y(y).setDuration(0);
    }
    public void removeFromLayout(ConstraintLayout layout){
        layout.removeView(pieChartLayout);
    }
    public void setData(Object ammount, int color, String title){
        try {
            removeData(title);
            addData(ammount, color, title);
            calcFillPieChart();
        }catch(Exception e){e.printStackTrace();}
    }
    private void addData(Object ammount, int color, String title){
        if(data.size() == 1)
            if(data.get(0)[2].equals("No Data"))
                data.remove(0);
        data.add(new String[]{""+ammount, ""+color, title});
    }
    private void removeData(String title){
        for (int i = 0; i < data.size(); i++) {
            String[] arr = data.get(i);
            if (arr[2].equals(title)) {
                data.remove(arr);
                i--;
            }
        }
        if(data.size() == 0)
            data.add(new String[]{"1", ""+Color.argb(255, 100, 100, 100), "No Data"});
    }
    private void calcFillPieChart(){
        double rad = 0;
        double sumAmmounts = 0;
        Canvas canvas = new Canvas(chart);
        for(String[] arr: data)
            sumAmmounts += Double.parseDouble(arr[0]);

        for(String[] arr: data){
            double addRad = 2*Math.PI*(Double.parseDouble(arr[0])/sumAmmounts);
            Paint paint = new Paint();
            paint.setColor(Integer.parseInt(arr[1]));
            for(double i = rad; i<rad+addRad; i+=.006){
                canvas.drawLine(radius, radius, (float)(radius*Math.sin(i))+radius, (float)(radius*Math.cos(i))+radius, paint);
            }
            rad += addRad;
        }
    }
    public void fillPieChart(){
        double sumAmmounts = 0;
        labelLayout.removeAllViews();
        colorLayout.removeAllViews();
        for(String[] arr: data)
            sumAmmounts += Double.parseDouble(arr[0]);

        for (int i = 0; i < data.size(); i++) {
            String[] arr = data.get(i);
            Bitmap bitColor = Bitmap.createBitmap(radius / 5, (int) (radius / 2.8), Bitmap.Config.ARGB_8888);
            Paint paint = new Paint();
            paint.setColor(Integer.parseInt(arr[1]));
            Canvas colorCanvas = new Canvas(bitColor);
            colorCanvas.drawRect(new Rect(0, 0, radius / 5, radius / 5), paint);

            TextView label = new TextView(context);
            if (illion)
                label.setText(arr[2] + ":\n" + new Exp(Double.parseDouble(arr[0]), 0).toIllionString() + " " + unit);
            else
                label.setText(arr[2] + ":\n" + new Exp(Double.parseDouble(arr[0]), 0).toPrefixString() + unit);
            label.setTextSize(TypedValue.COMPLEX_UNIT_PX, radius / 7);
            label.setTextColor(Color.BLACK);
            labelLayout.addView(label);
            ImageView colorView = new ImageView(context);
            colorView.setImageBitmap(bitColor);
            colorLayout.addView(colorView);
        }
        TextView total = new TextView(context);
        if(illion)total.setText("Total:\n"+new Exp(sumAmmounts, 0).toIllionString()+" "+unit);
        else total.setText("Total:\n"+new Exp(sumAmmounts, 0).toPrefixString()+unit);
        total.setTextSize(TypedValue.COMPLEX_UNIT_PX, radius/7);
        total.setTextColor(Color.BLACK);
        labelLayout.addView(total);
        pieChart.setImageBitmap(chart);
    }
}
