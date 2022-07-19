package com.reactordevelopment.superintelligent;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.provider.ContactsContract;
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
    private int choiceAt = 0;

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
        values = MainActivity.parseResourceChange("", "");  //init blank values
        mkEvent(title, desc, choices, background, type, icon);
    }
    public static String titleFromTag(String tag){
        int startI = eventStr.indexOf("[tag] "+tag);
        int titleStart = eventStr.indexOf("[title] \"", startI)+8;
        return eventStr.substring(titleStart, eventStr.indexOf("\"", titleStart));
    }
    @SuppressLint("ClickableViewAccessibility")
    public void mkEvent(String tag){
        int startI = eventStr.indexOf("[tag] "+tag);
        String cutStr = eventStr.substring(startI, eventStr.indexOf("[tag]", startI+1));
        //Log.i("EventMake", tag+", "+cutStr);
        values = MainActivity.parseResourceChange(cutStr, "EventPass");
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
        while (eventStr.indexOf("[tag]", btnStart) > eventStr.indexOf("[btn]", btnStart)) {
            btnStart = eventStr.indexOf("[btn]", btnStart) + 7;
            btnTxts.add(eventStr.substring(btnStart, eventStr.indexOf("\"", btnStart)));
            String cutText = "";
            try { cutText = eventStr.substring(btnStart, eventStr.indexOf("[btn]", btnStart)); } catch (StringIndexOutOfBoundsException e) { continue; }
            values.put("btn" + cycle, MainActivity.parseResourceChange(cutText, "ButtonPass"));
            cycle++;
            int nextTag = eventStr.indexOf("[tag]", btnStart);
            int nextBtn = eventStr.indexOf("[btn]", btnStart);
            Log.i("ButtonCompare", nextTag+"("+eventStr.substring(nextTag, nextTag+10)+")");
            Log.i("ButtonCompare", nextBtn+"("+eventStr.substring(nextBtn, nextBtn+10)+")");
        }

        Log.i("Mkevent", "fromtag: "+title+", "+btnTxts.toString());
        mkEvent(title, desc, btnTxts.toArray(new String[0]), background, type, icon);
    }
    @SuppressLint("ClickableViewAccessibility")
    public void mkEvent(String title, String desc, final String[] choices, int background, int type, int iconImg){
        final RelativeLayout eventLayout = new RelativeLayout(context);
        ImageView eventRound = new ImageView(context);
        eventRound.setId(2);
        eventRound.setBackgroundResource(background);
        TextView eventText = new TextView(context);
        TextView titleText = new TextView(context);
        ImageView icon = new ImageView(context);
        final TextView tooltip = new TextView(context);
        ImageView tooltipRound = new ImageView(context);
        ImageButton confirm = new ImageButton(context);
        icon.setBackgroundResource(iconImg);
        titleText.setLayoutParams(new RelativeLayout.LayoutParams((int)(screenHeight*.6), (int)(screenWidth*.2)));
        eventText.setMovementMethod(new ScrollingMovementMethod());
        eventText.setVerticalScrollBarEnabled(true);
        eventText.setVerticalFadingEdgeEnabled(true);
        eventText.setTextSize(TypedValue.COMPLEX_UNIT_IN, inchHeight*.027f);
        eventText.setText(desc);
        titleText.setText(title);
        tooltip.setTextColor(Color.BLACK);
        titleText.setTextColor(Color.BLACK);
        eventText.setTextColor(Color.BLACK);


        final ImageButton[] checks = new ImageButton[choices.length];
        final ImageButton[] choiceBtns = new ImageButton[choices.length];
        final String[] tooltips = new String[choices.length];
        boolean effects = false; //if choices have effects
        for(int index=0; index < choices.length; index++){
            choiceBtns[index] = new ImageButton(context);
            checks[index] = new ImageButton(context);
            choiceBtns[index].setLayoutParams(new RelativeLayout.LayoutParams((int)(screenHeight*.6), (int)(screenWidth*.15)));
            checks[index].setLayoutParams(new RelativeLayout.LayoutParams((int)(screenHeight*.07), (int)(screenWidth*.15)));
            checks[index].setBackgroundResource(R.drawable.blank);
            choiceBtns[index].setBackgroundResource(R.drawable.buttoncutoff);
            final int finalI = index;
            choiceBtns[index].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(choices.length > 1) tooltip.setText(choices[finalI]+"\nEffects: "+tooltips[finalI]);
                    else mainLayout.removeView(eventLayout);
                    for(ImageButton b : checks)
                        b.setBackgroundResource(R.drawable.blank);
                    checks[finalI].setBackgroundResource(R.drawable.checkmark);
                    choiceAt = finalI;
                }
            });
            tooltips[index] = "";
            Map<String, Object> btnValues = (Map<String, Object>) values.get("btn" + finalI);
            for(int i=0; i<((Exp[])btnValues.get("giveComp")).length; i++) {
                String typeStr = "";
                if (i == 0) typeStr = " (Central)";
                else if (i == 1) typeStr = " (Other AIs)";
                else if (i == 2) typeStr = " (Other Computers)";
                else if (i == 3) typeStr = " (Nanobots)";
                Exp comp = ((Exp[])btnValues.get("giveComp"))[i];
                if (!comp.equalTo(new Exp(0, 0))) tooltips[index] += "\n" + (comp.greaterThan(new Exp(0, 0)) ? "+" : "") + comp.toPrefixString() + "FLOPS"+typeStr;
                Exp compTk = ((Exp[])btnValues.get("giveCompTk"))[i];
                if (!compTk.equalTo(new Exp(0, 0))) tooltips[index] += "\n" + (compTk.greaterThan(new Exp(0, 0)) ? "+" : "") + compTk.toPrefixString() + "FLOPS/"+Game.timeStepName+typeStr;
                Exp pwr = ((Exp[])btnValues.get("givePwr"))[i];
                if(!pwr.equalTo(new Exp(0, 0))) tooltips[index] += "\n"+(pwr.greaterThan(new Exp(0, 0)) ? "+" : "")+pwr.toPrefixString()+"watts"+typeStr;
                Exp pwrTk = ((Exp[])btnValues.get("givePwrTk"))[i];
                if(!pwrTk.equalTo(new Exp(0, 0))) tooltips[index] += "\n"+(pwrTk.greaterThan(new Exp(0, 0)) ? "+" : "")+pwrTk.toPrefixString()+"watts/"+Game.timeStepName+typeStr;
                Log.i("ButtonValues("+i+")", comp+", "+compTk+", "+pwr+", "+pwrTk);
            }
            for(int i=0; i<((Exp[])btnValues.get("giveAvailPwrTk")).length; i++) {
                String typeStr = "";
                if (i == 0) typeStr = " (Lab)";
                else if (i == 1) typeStr = " (Solar)";
                else if (i == 2) typeStr = " (Other)";
                Exp availPwrTk = ((Exp[])btnValues.get("giveAvailPwrTk"))[i];
                if (!availPwrTk.equalTo(new Exp(0, 0))) tooltips[index] += "\n" + (availPwrTk.greaterThan(new Exp(0, 0)) ? "+" : "") + availPwrTk.toPrefixString() + "watts available/"+Game.timeStepName+typeStr;
            }
            for(int i=0; i<((Exp[])values.get("givePopTk")).length; i++) {
                String typeStr = "";
                if (i == 1) typeStr = " (Influenced)";
                else if (i == 2) typeStr = " (Controlled)";
                else if (i == 3) typeStr = " (Designed)";
                Exp popTk = ((Exp[])btnValues.get("givePopTk"))[i];
                if (!popTk.equalTo(new Exp(0, 0))) tooltips[index] += "\n" + (popTk.greaterThan(new Exp(0, 0)) ? "+" : "") + popTk.toIllionString() + " Humans/"+Game.timeStepName+typeStr;
            }
            Exp exp = ((Exp)btnValues.get("giveExp"));
            if(!exp.equalTo(new Exp(0, 0))) tooltips[index] += "\n"+(exp.toDouble() > 0 ? "+" : "")+(int)(exp.toDouble()*100)/100.0+" Knowledge/"+timeStepName;
            Exp expMax = ((Exp)btnValues.get("giveExpMax"));
            if(!expMax.equalTo(new Exp(0, 0))) tooltips[index] += "\n"+(expMax.greaterThan(new Exp(0, 0)) ? "+" : "")+expMax.toIllionString()+" Knowledge Cap";
            Double def = ((Double)btnValues.get("giveDef"));
            if(def != 0) tooltips[index] += "\n"+(def > 0 ? "+" : "-")+def+" Defense";
            Double sus = ((Double)btnValues.get("giveSus"));
            if(sus != 0) tooltips[index] += "\n"+(sus > 0 ? "+" : "-")+sus+" Suspicion";
            Double susTk = ((Double)btnValues.get("giveTickingSus"));
            if(susTk != 0) tooltips[index] += "\n"+(susTk > 0 ? "+" : "-")+susTk+" Suspicion/"+timeStepName;

            if(!tooltips[index].equals(""))
                effects = true;
        }

        int imgWidth = (int)(screenHeight*.6);  //without tooltip
        int totalWidth = (int)(screenHeight*.85); //with tooltip
        int imgHeight = (int) (screenWidth*.65);
        if(type == 0) {
            imgHeight = (int) (screenWidth*.65);
            icon.setLayoutParams(new ConstraintLayout.LayoutParams((int)(screenHeight*.13), (int)(screenWidth*.2)));
            titleText.setTextSize(TypedValue.COMPLEX_UNIT_IN, inchHeight*.035f);
            titleText.setGravity(Gravity.CENTER);
            eventText.setLayoutParams(new RelativeLayout.LayoutParams((int)(screenHeight*.5), (int)(screenWidth*.25)));
        }
        if(type == 1){
            imgHeight = (int) (screenWidth*.75);
            eventText.setLayoutParams(new RelativeLayout.LayoutParams((int)(screenHeight*.5), (int)(screenWidth*.35)));
            titleText.setTextSize(TypedValue.COMPLEX_UNIT_IN, inchHeight * .027f);
        }
        if(type == 2){
            imgHeight = (int) (screenWidth*.85);
            titleText.setLayoutParams(new RelativeLayout.LayoutParams((int)(screenHeight*.6), (int)(screenWidth*.15)));
            eventText.setLayoutParams(new RelativeLayout.LayoutParams((int)(screenHeight*.5), (int)(screenWidth*.35)));
            titleText.setTextSize(TypedValue.COMPLEX_UNIT_IN, inchHeight * .04f);
            titleText.setGravity(Gravity.CENTER);
        }
        eventRound.setLayoutParams(new RelativeLayout.LayoutParams(imgWidth, imgHeight));
        eventLayout.setLayoutParams(new ConstraintLayout.LayoutParams(effects ? totalWidth : imgWidth, imgHeight));
        eventLayout.addView(eventRound);

        for(int index=0; index < choices.length; index++) {
            eventLayout.addView(choiceBtns[index]);
            if(effects) eventLayout.addView(checks[index]);
            TextView choiceText = new TextView(context);
            choiceText.setLayoutParams(new RelativeLayout.LayoutParams((int)(screenHeight*.6), (int)(screenWidth*.1)));
            choiceText.setText(choices[index]);
            choiceText.setTextSize(TypedValue.COMPLEX_UNIT_IN, inchHeight*.027f);
            choiceText.setGravity(Gravity.CENTER);
            eventLayout.addView(choiceText);
            if(type == 1) {
                choiceBtns[index].animate().yBy(screenWidth * .6f + screenWidth * .13f * index).setDuration(0);
                checks[index].animate().yBy(screenWidth * .6f + screenWidth * .14f * index).setDuration(0);
                choiceText.animate().yBy(screenWidth * .6f + screenWidth * .14f * index).setDuration(0);
            }else if(type == 2) {
                choiceBtns[index].animate().yBy(screenWidth*.7f + screenWidth * .13f * index).setDuration(0);
                choiceText.animate().yBy(screenWidth*.7f + screenWidth * .14f * index).setDuration(0);
                checks[index].animate().yBy(screenWidth*.7f + screenWidth * .14f * index).setDuration(0);
            }
            else{
                choiceBtns[index].animate().yBy(screenWidth * .5f + screenWidth * .13f * index).setDuration(0);
                choiceText.animate().yBy(screenWidth * .5f + screenWidth * .14f * index).setDuration(0);
                checks[index].animate().yBy(screenWidth * .5f + screenWidth * .14f * index).setDuration(0);
            }
        }
        tooltipRound.setId(1);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(totalWidth-imgWidth-10, (int)(screenWidth*.15));
        params.addRule(RelativeLayout.RIGHT_OF, eventRound.getId());
        tooltip.setLayoutParams(params);
        tooltip.setPadding(10, 10, 5, 10);
        params = new RelativeLayout.LayoutParams(totalWidth-imgWidth, (int)(screenWidth*.15));
        params.addRule(RelativeLayout.RIGHT_OF, eventRound.getId());
        tooltipRound.setLayoutParams(params);
        tooltip.setVerticalScrollBarEnabled(true);
        tooltip.setMovementMethod(new ScrollingMovementMethod());
        confirm.setBackgroundResource(R.drawable.accept);
        params = new RelativeLayout.LayoutParams((int)(screenHeight*.15), (int)(screenHeight*.15)/2);
        params.addRule(RelativeLayout.BELOW, tooltipRound.getId());
        params.addRule(RelativeLayout.RIGHT_OF, eventRound.getId());
        confirm.setLayoutParams(params);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(values.get("btn"+choiceAt) != null){
                    for (int j = 0; j < Game.compSources.length; j++) {
                        Log.i("Unlockgivepwr" + values.get("titleTxt"), ((Exp[]) ((Map<String, Object>) values.get("btn" + choiceAt)).get("giveComp"))[j]
                                + ", " + ((Exp[]) ((Map<String, Object>) values.get("btn" + choiceAt)).get("giveCompTk"))[j]
                                + ", " + ((Exp[]) ((Map<String, Object>) values.get("btn" + choiceAt)).get("givePwr"))[j]
                                + ", " + ((Exp[]) ((Map<String, Object>) values.get("btn" + choiceAt)).get("givePwrTk"))[j]);

                        addComputingSource(j, ((Exp[]) ((Map<String, Object>) values.get("btn" + choiceAt)).get("giveComp"))[j]);
                        addPowerExpense(j, ((Exp[]) ((Map<String, Object>) values.get("btn" + choiceAt)).get("givePwr"))[j]);
                        compTkSources[j].add(((Exp[]) ((Map<String, Object>) values.get("btn" + choiceAt)).get("giveCompTk"))[j]);
                        powerTkExpenses[j].add(((Exp[]) ((Map<String, Object>) values.get("btn" + choiceAt)).get("givePwrTk"))[j]);
                        //Log.i("Recalc1.4", computing+", "+power+", "+neededPower+", "+maxComputing);
                    }
                    //Log.i("Recalc1.5", computing+", "+power+", "+neededPower+", "+maxComputing);
                    for (int j = 0; j < availPowerTk.length; j++) availPowerTk[j].add(((Exp[]) ((Map<String, Object>) values.get("btn" + choiceAt)).get("giveAvailPwrTk"))[j]);
                    for (int j = 0; j < popSourcesTk.length; j++) popSourcesTk[j].add(((Exp[]) ((Map<String, Object>) values.get("btn" + choiceAt)).get("givePopTk"))[j]);
                    maxExperience.add(((Exp) ((Map<String, Object>) values.get("btn" + choiceAt)).get("giveExpMax")));
                    defense += ((Double) ((Map<String, Object>) values.get("btn" + choiceAt)).get("giveDef"));
                    experienceCng += ((Exp) ((Map<String, Object>) values.get("btn" + choiceAt)).get("giveExp")).toDouble();
                    suspicion += ((Double) ((Map<String, Object>) values.get("btn" + choiceAt)).get("giveSus"));
                    tickingSuspicion += ((Double) ((Map<String, Object>) values.get("btn" + choiceAt)).get("giveTickingSus"));
                }
                mainLayout.removeView(eventLayout);
                recalcValues();
            }
        });


        Log.i("ButtonChoices", title+" buttons: "+choices.length+" tooltips: "+tooltips[0].equals(""));
        choiceBtns[0].performClick();
        eventLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                eventLayout.bringToFront();
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

        if(effects){
            tooltipRound.setBackgroundResource(R.drawable.mainround);
            eventLayout.setLayoutParams(new ConstraintLayout.LayoutParams(eventLayout.getLayoutParams().width,
                    eventLayout.getLayoutParams().height + (int)(screenWidth * .17f * choices.length-1)));
            eventLayout.addView(confirm);
            eventLayout.addView(tooltipRound);
            eventLayout.addView(tooltip);
        }
        eventLayout.addView(icon);
        eventLayout.addView(eventText);
        eventLayout.addView(titleText);
        mainLayout.addView(eventLayout);
        //tooltip.animate().xBy(imgWidth);
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
