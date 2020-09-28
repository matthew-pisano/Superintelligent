package com.reactordevelopment.superintelligent;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import java.io.PipedOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.reactordevelopment.superintelligent.MainActivity.inchHeight;
import static com.reactordevelopment.superintelligent.MainActivity.screenHeight;
import static com.reactordevelopment.superintelligent.MainActivity.screenWidth;

public class Event extends Game{
    private Context context;
    private float dX;
    private float dY;
    private HashMap<String, Object> values;

    public Event(Context context, String tag){
        if(tag.length() > 4)
            if(tag.charAt(4) == '0')
                tag = tag.replaceFirst("0", "");
        Log.i("MkEvent", tag);
        this.context = context;
        if(firedEvents != null) firedEvents.add(tag);
        mkEvent(tag);
    }
    public Event(Context context, String title, String desc, String[] choices, int background, int type, int icon){
        this.context = context;
        values = MainActivity.parseResourceChange("");  //init blank values
        mkEvent(title, desc, choices, background, type, icon);
    }
    public static String titleFromTag(String tag){
        int startI = eventStr.indexOf("[tag] "+tag);
        int titleStart = eventStr.indexOf("title] \"", startI)+8;
        return eventStr.substring(titleStart, eventStr.indexOf("\"", titleStart));
    }
    @SuppressLint("ClickableViewAccessibility")
    public void mkEvent(String tag){
        int startI = eventStr.indexOf("[tag] "+tag);
        String cutStr = eventStr.substring(startI, eventStr.indexOf("[tag]", startI+1));
        //Log.i("EventMake", tag+", "+cutStr);
        values = MainActivity.parseResourceChange(cutStr);
        String desc = (String) values.get("descTxt");
        String title = (String) values.get("titleTxt");
        int type  = (Integer) values.get("type");
        //Log.i("EventMake", "<"+desc+"\n<"+title+"\n<"+type);
        int background = 0;
        int icon = context.getResources().getIdentifier((String) values.get("icon"), "drawable", context.getPackageName());


        if(type == 0) background = R.drawable.techround;
        else if(type == 1) background = R.drawable.emailround;
        else if (type == 2) background = R.drawable.webnewsround;
        ArrayList<String> btnTxts = new ArrayList<>(0);
        int btnStart = startI + 10;
        int cycle = 0;
        while (eventStr.indexOf("[tag]", btnStart) > eventStr.indexOf("btn]", btnStart)) {
            btnStart = eventStr.indexOf("btn]", btnStart) + 6;
            btnTxts.add(eventStr.substring(btnStart, eventStr.indexOf("\"", btnStart)));
            String cutText = "";
            try { cutText = eventStr.substring(btnStart, eventStr.indexOf("btn]", btnStart)); } catch (StringIndexOutOfBoundsException e) { continue; }
            values.put("btn" + cycle, MainActivity.parseResourceChange(cutText));
            cycle++;
            int nextTag = eventStr.indexOf("[tag]", btnStart);
            int nextBtn = eventStr.indexOf("btn]", btnStart);
            Log.i("ButtonCompare", nextTag+"("+eventStr.substring(nextTag, nextTag+10)+")");
            Log.i("ButtonCompare", nextBtn+"("+eventStr.substring(nextBtn, nextBtn+10)+")");
        }

        Log.i("Mkevent", "fromtag: "+title);
        mkEvent(title, desc, btnTxts.toArray(new String[0]), background, type, icon);
    }
    @SuppressLint("ClickableViewAccessibility")
    public void mkEvent(String title, String desc, String[] choices, int background, int type, int iconImg){
        final RelativeLayout eventLayout = new RelativeLayout(context);
        ImageView eventRound = new ImageView(context);
        eventRound.setBackgroundResource(background);
        TextView eventText = new TextView(context);
        TextView titleText = new TextView(context);
        ImageView icon = new ImageView(context);
        icon.setBackgroundResource(iconImg);
        titleText.setLayoutParams(new RelativeLayout.LayoutParams((int)(screenHeight*.6), (int)(screenWidth*.2)));
        eventText.setMovementMethod(new ScrollingMovementMethod());
        eventText.setVerticalScrollBarEnabled(true);
        eventText.setVerticalFadingEdgeEnabled(true);
        eventText.setTextSize(TypedValue.COMPLEX_UNIT_IN, inchHeight*.027f);
        eventText.setText(desc);
        titleText.setText(title);
        titleText.setTextColor(Color.BLACK);
        eventText.setTextColor(Color.BLACK);
        if(type == 0) {
            icon.setLayoutParams(new ConstraintLayout.LayoutParams((int)(screenHeight*.13), (int)(screenWidth*.2)));
            eventRound.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (screenWidth*.65)));
            eventLayout.setLayoutParams(new ConstraintLayout.LayoutParams((int)(screenHeight*.6), (int)(screenWidth*.65)));
            titleText.setTextSize(TypedValue.COMPLEX_UNIT_IN, inchHeight*.035f);
            titleText.setGravity(Gravity.CENTER);
            eventText.setLayoutParams(new RelativeLayout.LayoutParams((int)(screenHeight*.5), (int)(screenWidth*.25)));
        }
        if(type == 1){
            eventRound.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (screenWidth*.75)));
            eventLayout.setLayoutParams(new ConstraintLayout.LayoutParams((int)(screenHeight*.6), (int)(screenWidth*.75)));
            eventText.setLayoutParams(new RelativeLayout.LayoutParams((int)(screenHeight*.5), (int)(screenWidth*.35)));
            titleText.setTextSize(TypedValue.COMPLEX_UNIT_IN, inchHeight * .027f);
        }
        if(type == 2){
            eventLayout.setLayoutParams(new ConstraintLayout.LayoutParams((int)(screenHeight*.6), (int)(screenWidth*.85)));
            eventRound.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (screenWidth*.85)));
            titleText.setLayoutParams(new RelativeLayout.LayoutParams((int)(screenHeight*.6), (int)(screenWidth*.15)));
            eventText.setLayoutParams(new RelativeLayout.LayoutParams((int)(screenHeight*.5), (int)(screenWidth*.35)));
            titleText.setTextSize(TypedValue.COMPLEX_UNIT_IN, inchHeight * .04f);
            titleText.setGravity(Gravity.CENTER);
        }
        /*Log.i("Buttonchoices", "btn, "+giveComp+", "+givePwr+", "+giveExpMax);
        Log.i("Compsize", ""+giveComp.size());
        if(giveComp.size() == 0){
            giveComp.add(new Exp(0, 0));
            Log.i("Buttonchoices", "btn, "+giveComp+", "+givePwr+", "+giveExpMax);
            givePwr.add(new Exp(0, 0));
            giveAvailPwr.add(new Exp(0, 0));
            giveExp.add(new Exp(0, 0));
            giveExpMax.add(new Exp(0, 0));
            giveSus.add(0.0);
        }*/
        Log.i("ButtonChoices", title+" buttons: "+choices.length);
        if(choices.length > 1){
            eventLayout.setLayoutParams(new ConstraintLayout.LayoutParams(eventLayout.getLayoutParams().width,
                    eventLayout.getLayoutParams().height + (int)(screenWidth * .15f * choices.length-1)));
        }
        eventLayout.addView(eventRound);
        for(int i=0; i < choices.length; i++){
            ImageButton choice = new ImageButton(context);
            choice.setLayoutParams(new RelativeLayout.LayoutParams((int)(screenHeight*.6), (int)(screenWidth*.15)));
            choice.setBackgroundResource(R.drawable.buttoncutoff);
            final int finalI = i;
            choice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(values.get("btn"+finalI) != null){
                        for (int j = 0; j < Game.compSources.length; j++) {
                            Log.i("Unlockgivepwr" + values.get("titleTxt"), ((Exp[]) ((Map<String, Object>) values.get("btn" + finalI)).get("giveComp"))[j]
                                    + ", " + ((Exp[]) ((Map<String, Object>) values.get("btn" + finalI)).get("giveCompTk"))[j]
                                    + ", " + ((Exp[]) ((Map<String, Object>) values.get("btn" + finalI)).get("givePwr"))[j]
                                    + ", " + ((Exp[]) ((Map<String, Object>) values.get("btn" + finalI)).get("givePwrTk"))[j]);

                            addComputingSource(j, ((Exp[]) ((Map<String, Object>) values.get("btn" + finalI)).get("giveComp"))[j]);
                            addPowerExpense(j, ((Exp[]) ((Map<String, Object>) values.get("btn" + finalI)).get("givePwr"))[j]);
                            compTkSources[j].add(((Exp[]) ((Map<String, Object>) values.get("btn" + finalI)).get("giveCompTk"))[j]);
                            powerTkExpenses[j].add(((Exp[]) ((Map<String, Object>) values.get("btn" + finalI)).get("givePwrTk"))[j]);
                            //Log.i("Recalc1.4", computing+", "+power+", "+neededPower+", "+maxComputing);
                        }
                        //Log.i("Recalc1.5", computing+", "+power+", "+neededPower+", "+maxComputing);
                        for (int j = 0; j < availPowerTk.length; j++) availPowerTk[j].add(((Exp[]) ((Map<String, Object>) values.get("btn" + finalI)).get("giveAvailPwrTk"))[j]);
                        for (int j = 0; j < popSourcesTk.length; j++) popSourcesTk[j].add(((Exp[]) ((Map<String, Object>) values.get("btn" + finalI)).get("givePopTk"))[j]);
                        maxExperience.add(((Exp) ((Map<String, Object>) values.get("btn" + finalI)).get("giveExpMax")));
                        defense += ((Double) ((Map<String, Object>) values.get("btn" + finalI)).get("giveDef"));
                        experienceCng += ((Exp) ((Map<String, Object>) values.get("btn" + finalI)).get("giveExp")).toDouble();
                        suspicion += ((Double) ((Map<String, Object>) values.get("btn" + finalI)).get("giveSus"));
                        tickingSuspicion += ((Double) ((Map<String, Object>) values.get("btn" + finalI)).get("giveTickingSus"));
                    }
                    mainLayout.removeView(eventLayout);
                    recalcValues();
                }
            });

            eventLayout.addView(choice);
            TextView choiceText = new TextView(context);
            choiceText.setLayoutParams(new RelativeLayout.LayoutParams((int)(screenHeight*.6), (int)(screenWidth*.1)));
            choiceText.setText(choices[i]);
            choiceText.setTextSize(TypedValue.COMPLEX_UNIT_IN, inchHeight*.027f);
            choiceText.setGravity(Gravity.CENTER);
            eventLayout.addView(choiceText);
            if(type == 1) {
                choice.animate().yBy(screenWidth * .6f + screenWidth * .13f * i).setDuration(0);
                choiceText.animate().yBy(screenWidth * .6f + screenWidth * .14f * i).setDuration(0);
            }else if(type == 2) {
                choice.animate().yBy(screenWidth*.7f + screenWidth * .13f * i).setDuration(0);
                choiceText.animate().yBy(screenWidth*.7f + screenWidth * .14f * i).setDuration(0);
            }
            else{
                choice.animate().yBy(screenWidth * .5f + screenWidth * .13f * i).setDuration(0);
                choiceText.animate().yBy(screenWidth * .5f + screenWidth * .14f * i).setDuration(0);
            }
        }
        eventLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (event.getPointerCount() == 1) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            dX = view.getX() - event.getRawX();
                            dY = view.getY() - event.getRawY();
                        case MotionEvent.ACTION_MOVE:
                            if(event.getRawX() + dX > 0
                                    && event.getRawY() + dY > 0
                                    && event.getRawX() + dX < screenHeight*.45
                                    && event.getRawY() + dY  < screenWidth*.4)
                                view.animate().x(event.getRawX() + dX).y(event.getRawY() + dY).setDuration(0).start();
                            else view.animate().x(screenHeight*.2f).y(screenWidth*.2f).setDuration(500);
                            break;
                        default: return false;
                    }
                } else return false;
                return true;
            }
        });
        eventLayout.addView(icon);
        eventLayout.addView(eventText);
        eventLayout.addView(titleText);
        mainLayout.addView(eventLayout);
        eventText.animate().xBy((int)(screenHeight*.05)).setDuration(0);
        eventLayout.animate().x(screenHeight*.15f).y(screenWidth*.1f).setDuration(0);
        if(type == 1) {
            titleText.animate().xBy(screenHeight*.05f).setDuration(0);
            titleText.animate().yBy(screenWidth*.045f).setDuration(0);
            eventText.animate().y(screenWidth*.25f).setDuration(0);
        }else if(type == 2){
            eventText.animate().yBy(screenWidth*.3f).setDuration(0);
            titleText.animate().yBy(screenWidth*.1f).setDuration(0);
        } else eventText.animate().y(screenWidth*.2f).setDuration(0);
    }



}
