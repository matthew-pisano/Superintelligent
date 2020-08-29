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

import static com.reactordevelopment.superintelligent.MainActivity.inchHeight;
import static com.reactordevelopment.superintelligent.MainActivity.screenHeight;
import static com.reactordevelopment.superintelligent.MainActivity.screenWidth;

public class Event extends Game{
    private Context context;
    private float dX;
    private float dY;
    private ArrayList<Exp> giveComp;
    private ArrayList<Exp> givePwr;
    private ArrayList<Exp> giveAvailPwr;
    private ArrayList<Exp> giveExp;
    private ArrayList<Exp> giveExpMax;
    private ArrayList<Double> giveSus;

    public Event(Context context, String tag){
        this.context = context;
        if(firedEvents != null) firedEvents.add(tag);
        giveComp = new ArrayList<>(0);
        givePwr = new ArrayList<>(0);
        giveAvailPwr = new ArrayList<>(0);
        giveExp = new ArrayList<>(0);
        giveExpMax = new ArrayList<>(0);
        giveSus = new ArrayList<>(0);
        mkEvent(tag);
    }
    public Event(Context context, String title, String desc, String[] choices, int background, String type, int icon){
        this.context = context;
        giveComp = new ArrayList<>(0);
        givePwr = new ArrayList<>(0);
        giveAvailPwr = new ArrayList<>(0);
        giveExp = new ArrayList<>(0);
        giveExpMax = new ArrayList<>(0);
        giveSus = new ArrayList<>(0);
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
        int msgStart = eventStr.indexOf("msg] \"", startI)+6;
        int typeStart = eventStr.indexOf("type] \"", startI)+7;
        int titleStart = eventStr.indexOf("title] \"", startI)+8;
        String desc = eventStr.substring(msgStart, eventStr.indexOf("\"", msgStart));
        String title = eventStr.substring(titleStart, eventStr.indexOf("\"", titleStart));
        String type = eventStr.substring(typeStart, eventStr.indexOf("\"", typeStart));
        int background;
        int icon = R.drawable.blank;
        if(eventStr.substring(startI+5, eventStr.indexOf("[tag]", startI+5)).contains("icon]")){
            int iconStart = eventStr.indexOf("icon] \"", startI) + 7;
            String name = eventStr.substring(iconStart, eventStr.indexOf("\"", iconStart));
            icon = context.getResources().getIdentifier(name, "drawable", context.getPackageName());
            Log.i("Icon", "name: "+name+", icon: "+icon+", Blank: "+R.drawable.blank);
        }
        if(type.equals("0")) background = R.drawable.techround;
        else if(type.equals("1")) background = R.drawable.emailround;
        else background = R.drawable.webnewsround;
        ArrayList<String> btnTxts = new ArrayList<>(0);
        int btnStart = eventStr.indexOf("btn", startI)-5;
        while (btnStart != -1) {
            if (eventStr.substring(eventStr.indexOf("]", btnStart) - 3, eventStr.indexOf("]", btnStart)).equals("btn")) {
                btnStart = eventStr.indexOf("]", btnStart) + 3;
                btnTxts.add(eventStr.substring(btnStart, eventStr.indexOf("\"", btnStart)));
                String cutText = "";
                try{cutText = eventStr.substring(btnStart, eventStr.indexOf("btn]", btnStart));}catch (StringIndexOutOfBoundsException e){Log.i("Ctttext", "exception");continue;}
                if(cutText.contains("comp]")) {
                    int compStart = cutText.indexOf("comp] \"") + 7;
                    Log.i("EventCom", ""+giveComp);
                    giveComp.add(new Exp(Double.parseDouble(cutText.substring(compStart, cutText.indexOf("E", compStart))), Integer.parseInt(cutText.substring(cutText.indexOf("E", compStart) + 1, cutText.indexOf("\"", compStart)))));
                    Log.i("EventCom", ""+giveComp);
                }else giveComp.add(new Exp(0, 0));
                if(cutText.contains("pwr]")) {
                    int pwrStart = cutText.indexOf("pwr] \"") + 6;
                    givePwr.add(new Exp(Double.parseDouble(cutText.substring(pwrStart, cutText.indexOf("E", pwrStart))), Integer.parseInt(cutText.substring(cutText.indexOf("E", pwrStart) + 1, cutText.indexOf("\"", pwrStart)))));
                }else givePwr.add(new Exp(0, 0));
                if(cutText.contains("availpwr]")) {
                    int pwrStart = cutText.indexOf("availpwr] \"") + 11;
                    giveAvailPwr.add(new Exp(Double.parseDouble(cutText.substring(pwrStart, cutText.indexOf("E", pwrStart))), Integer.parseInt(cutText.substring(cutText.indexOf("E", pwrStart) + 1, cutText.indexOf("\"", pwrStart)))));
                }else giveAvailPwr.add(new Exp(0, 0));
                if(cutText.contains("exp]")) {
                    int expStart = cutText.indexOf("exp] \"") + 6;
                    giveExp.add(new Exp(Double.parseDouble(cutText.substring(expStart, cutText.indexOf("E", expStart))), Integer.parseInt(cutText.substring(cutText.indexOf("E", expStart) + 1, cutText.indexOf("\"", expStart)))));
                }else giveExp.add(new Exp(0, 0));
                if(cutText.contains("expmax]")) {
                    int expStart = cutText.indexOf("expmax] \"") + 9;
                    giveExpMax.add(new Exp(Double.parseDouble(cutText.substring(expStart, cutText.indexOf("E", expStart))), Integer.parseInt(cutText.substring(cutText.indexOf("E", expStart) + 1, cutText.indexOf("\"", expStart)))));
                }else giveExpMax.add(new Exp(0, 0));
                if(cutText.contains("sus]")) {
                    int susStart = cutText.indexOf("sus] \"") + 6;
                    giveSus.add(Double.parseDouble(cutText.substring(susStart, cutText.indexOf("\"", susStart))));
                }else giveSus.add(0.0);
            } else btnStart = -1;
        }
        Log.i("Mkevent", "fromtag: "+title);
        mkEvent(title, desc, btnTxts.toArray(new String[0]), background, type, icon);
    }
    @SuppressLint("ClickableViewAccessibility")
    public void mkEvent(String title, String desc, String[] choices, int background, String type, int iconImg){
        final RelativeLayout eventLayout = new RelativeLayout(context);
        ImageView eventRound = new ImageView(context);
        eventRound.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        eventRound.setBackgroundResource(background);
        eventLayout.addView(eventRound);
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
        if(type.equals("0")) {
            icon.setLayoutParams(new ConstraintLayout.LayoutParams((int)(screenHeight*.13), (int)(screenWidth*.2)));
            eventLayout.setLayoutParams(new ConstraintLayout.LayoutParams((int)(screenHeight*.6), (int)(screenWidth*.65)));
            titleText.setTextSize(TypedValue.COMPLEX_UNIT_IN, inchHeight*.035f);
            titleText.setGravity(Gravity.CENTER);
            eventText.setLayoutParams(new RelativeLayout.LayoutParams((int)(screenHeight*.5), (int)(screenWidth*.25)));
        }
        if(type.equals("1")){
            eventLayout.setLayoutParams(new ConstraintLayout.LayoutParams((int)(screenHeight*.6), (int)(screenWidth*.75)));
            eventText.setLayoutParams(new RelativeLayout.LayoutParams((int)(screenHeight*.5), (int)(screenWidth*.35)));
            titleText.setTextSize(TypedValue.COMPLEX_UNIT_IN, inchHeight * .027f);
        }
        if(type.equals("2")){
            eventLayout.setLayoutParams(new ConstraintLayout.LayoutParams((int)(screenHeight*.6), (int)(screenWidth*.85)));
            titleText.setLayoutParams(new RelativeLayout.LayoutParams((int)(screenHeight*.6), (int)(screenWidth*.15)));
            eventText.setLayoutParams(new RelativeLayout.LayoutParams((int)(screenHeight*.5), (int)(screenWidth*.35)));
            titleText.setTextSize(TypedValue.COMPLEX_UNIT_IN, inchHeight * .04f);
            titleText.setGravity(Gravity.CENTER);
        }
        Log.i("Buttonchoices", "btn, "+giveComp+", "+givePwr+", "+giveExpMax);
        Log.i("Compsize", ""+giveComp.size());
        if(giveComp.size() == 0){
            giveComp.add(new Exp(0, 0));
            Log.i("Buttonchoices", "btn, "+giveComp+", "+givePwr+", "+giveExpMax);
            givePwr.add(new Exp(0, 0));
            giveAvailPwr.add(new Exp(0, 0));
            giveExp.add(new Exp(0, 0));
            giveExpMax.add(new Exp(0, 0));
            giveSus.add(0.0);
        }
        Log.i("Buttonchoices", ""+choices.length);
        Log.i("Buttonchoices", "btn, "+giveComp+", "+givePwr+", "+giveExpMax+", "+new Exp(0, 0));
        for(int i=0; i < choices.length; i++){
            ImageButton choice = new ImageButton(context);
            choice.setLayoutParams(new RelativeLayout.LayoutParams((int)(screenHeight*.5), (int)(screenWidth*.1)));
            choice.setBackgroundResource(R.drawable.widebutton);
            final int finalI = i;
            choice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.i("Remove", "rem, "+giveComp+", "+givePwr+", "+giveExpMax);
                    addComputingSource(0, giveComp.get(finalI));
                    addPowerExpense(0, givePwr.get(finalI));
                    experience.add(giveExp.get(finalI));
                    suspicion += giveSus.get(finalI);
                    availPower.add(giveAvailPwr.get(finalI));
                    maxExperience.add(giveExpMax.get(finalI));
                    mainLayout.removeView(eventLayout);
                    updateResourceProgress();

                }
            });
            eventLayout.addView(choice);
            TextView choiceText = new TextView(context);
            choiceText.setLayoutParams(new RelativeLayout.LayoutParams((int)(screenHeight*.6), (int)(screenWidth*.1)));
            choiceText.setText(choices[0]);
            choiceText.setTextSize(TypedValue.COMPLEX_UNIT_IN, inchHeight*.027f);
            choiceText.setGravity(Gravity.CENTER);
            eventLayout.addView(choiceText);
            if(type.equals("1")) {
                choice.animate().yBy(screenWidth * .6f).setDuration(0);
                choiceText.animate().yBy(screenWidth * .6f).setDuration(0);
            }else if(type.equals("2")) {
                choice.animate().yBy(screenWidth*.7f).setDuration(0);
                choiceText.animate().yBy(screenWidth*.7f).setDuration(0);
            }
            else{
                choice.animate().yBy(screenWidth * .5f).setDuration(0);
                choiceText.animate().yBy(screenWidth * .5f).setDuration(0);
            }
            choice.animate().xBy(screenWidth*.06f).setDuration(0);
        }
        //eventText.animate().yBy(-screenWidth*.05f).setDuration(0);
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
        if(type.equals("1")) {
            titleText.animate().xBy(screenHeight*.05f).setDuration(0);
            titleText.animate().yBy(screenWidth*.045f).setDuration(0);
            eventText.animate().y(screenWidth*.25f).setDuration(0);
        }else if(type.equals("2")){
            eventText.animate().yBy(screenWidth*.3f).setDuration(0);
            titleText.animate().yBy(screenWidth*.1f).setDuration(0);
        } else eventText.animate().y(screenWidth*.2f).setDuration(0);
    }



}
