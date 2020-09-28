package com.reactordevelopment.superintelligent;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

import static com.reactordevelopment.superintelligent.MainActivity.*;

public class TechTile extends Game{
    private Context context;
    public int techId;
    public int icon;
    private String[] childrenIds;
    private ImageView image;
    private TextView title;
    private int x;
    private int y;
    private static final int IMAGE_WIDTH = screenHeight/20;
    private static final int IMAGE_HEIGHT = screenHeight/40;
    /*public Exp[] giveComp;
    public Exp[] giveCompTk;
    public Exp[] givePwr;
    public Exp[] givePwrTk;
    public Exp[] giveAvailPwrTk;
    public Exp giveExp;
    public Exp giveExpMax;
    public Exp needExp;
    public int needTech;
    public int notTech;
    public double giveDef;
    public double unlockTime;
    public ArrayList<Integer> optionalTechs;
    public ArrayList<Integer> neededTechs;
    public ArrayList<String> neededEvents;
    public ArrayList<String> optionalEvents;
    public double giveSus;
    public double giveTickingSus;*/
    //public String titleTxt;
    //public String descTxt;
    private int originalX;
    private int originalY;
    public boolean meetsReqs;
    private HashMap<String, Object> values;

    public TechTile(Context context, int techId, int icon, int x, int y, String[] childrenIds){
        this.techId = techId;
        this.icon = icon;
        this.x = x;
        this.y = y;
        meetsReqs = false;
        this.childrenIds = childrenIds;
        this.context = context;
        originalX = 0;
        originalY = 0;
        //giveComp = new Exp[]{new Exp(0, 0), new Exp(0, 0), new Exp(0, 0), new Exp(0, 0)};
        //givePwr = new Exp[]{new Exp(0, 0), new Exp(0, 0), new Exp(0, 0), new Exp(0, 0)};
        //giveCompTk = new Exp[]{new Exp(0, 0), new Exp(0, 0), new Exp(0, 0), new Exp(0, 0)};
        //givePwrTk = new Exp[]{new Exp(0, 0), new Exp(0, 0), new Exp(0, 0), new Exp(0, 0)};
        //((Exp[])values.get("giveAvailPwrTk")) = new Exp[]{new Exp(0, 0), new Exp(0, 0), new Exp(0, 0), new Exp(0, 0)};
        //((Exp[])values.get("givePopTk")) = new Exp[]{new Exp(0, 0), new Exp(0, 0), new Exp(0, 0), new Exp(0, 0)};
        tileInit();
        mkImage(R.drawable.nohastech);
    }
    public int getX(){return x;}
    public int getY(){return y;}
    public int getTechId(){return techId;}
    public void tileInit(){
        Log.i("Tiles", "iniy");
        int startI = techStr.indexOf("[tag] "+techId);
        String cutTechStr = techStr.substring(startI, techStr.indexOf("[tag]", startI+7));
        values = MainActivity.parseResourceChange(cutTechStr);

        //Log.i("Techs", cutTechStr);
        /*int expStart = cutTechStr.indexOf("exp] \"")+6;
        int susStart = cutTechStr.indexOf("sus] \"")+6;
        int titleStart = techStr.indexOf("title] \"", startI)+8;
        int descStart = techStr.indexOf("desc] \"", startI)+7;
        int reqStart = cutTechStr.indexOf("req]")+4;
        if(cutTechStr.substring(reqStart).contains("exp]") && reqStart > 5){
            int expReqStart =  cutTechStr.indexOf("exp] \"", reqStart)+6;
            ((Exp)values.get("needExp")) = new Exp(Double.parseDouble(cutTechStr.substring(expReqStart, cutTechStr.indexOf("E", expReqStart))), Integer.parseInt(cutTechStr.substring(cutTechStr.indexOf("E",expReqStart)+1, cutTechStr.indexOf("\"", expReqStart))));
        }else ((Exp)values.get("needExp")) = new Exp(0, 0);
        //((Exp)values.get("needExp")) = new Exp(1, 0);
        if(cutTechStr.contains("expmax]")){
            int expMaxReqStart =  cutTechStr.indexOf("expmax] \"")+9;
            giveExpMax = new Exp(Double.parseDouble(cutTechStr.substring(expMaxReqStart, cutTechStr.indexOf("E", expMaxReqStart))), Integer.parseInt(cutTechStr.substring(cutTechStr.indexOf("E",expMaxReqStart)+1, cutTechStr.indexOf("\"", expMaxReqStart))));
        }else giveExpMax = new Exp(0, 0);
        if(cutTechStr.substring(reqStart).contains("tech]") && reqStart > 5){
            int techReqStart =  cutTechStr.indexOf("tech] \"", reqStart)+7;
            needTech = Integer.parseInt(cutTechStr.substring(techReqStart, cutTechStr.indexOf("\"", techReqStart)));
        }else needTech = -1;
        optionalTechs = new ArrayList<>(0);
        neededTechs = new ArrayList<>(0);
        neededEvents = new ArrayList<>(0);
        optionalEvents = new ArrayList<>(0);
        int place = 0;
        while(cutTechStr.substring(place+1).contains("tech]")) {
            place = cutTechStr.indexOf("tech] \"", place + 1) + 7;
            Log.i("Techs", cutTechStr + ",{" + cutTechStr.substring(place, place + 3) + "}, " + place);
            if (place != 6) {
                neededTechs.add(Integer.parseInt(cutTechStr.substring(place, cutTechStr.indexOf("\"", place))));
                Log.i("neededTechs", ""+neededTechs);
            }else break;
        }
        while(cutTechStr.substring(place+1).contains("techO]")){
            place = cutTechStr.indexOf("techO] \"", place+1)+8;
            if (place != 7) {
                Log.i("Techs2", cutTechStr+",{"+cutTechStr.substring(place, place+3)+"}, "+place);
                optionalTechs.add(Integer.parseInt(cutTechStr.substring(place, cutTechStr.indexOf("\"", place))));
                Log.i("optionalTechs", ""+optionalTechs);
            }else break;
        }
        if(cutTechStr.contains("notTech]")){
            int techReqStart =  cutTechStr.indexOf("notTech] \"", reqStart)+10;
            notTech = Integer.parseInt(cutTechStr.substring(techReqStart, cutTechStr.indexOf("\"", techReqStart)));
        }else notTech = -1;
        if(cutTechStr.contains("sustk]")){
            int sustkStart =  cutTechStr.indexOf("sustk] \"")+8;
            giveTickingSus = Double.parseDouble(cutTechStr.substring(sustkStart, cutTechStr.indexOf("\"", sustkStart)));
        }else giveTickingSus = 0;
        if(cutTechStr.contains("def]")){
            int defStart =  cutTechStr.indexOf("def] \"")+6;
            giveDef = Double.parseDouble(cutTechStr.substring(defStart, cutTechStr.indexOf("\"", defStart)));
        }else giveDef = 0;
        if(cutTechStr.contains("duration]")){
            int durStart =  cutTechStr.indexOf("duration] \"")+11;
            unlockTime = Double.parseDouble(cutTechStr.substring(durStart, cutTechStr.indexOf("\"", durStart)));
        }else unlockTime = 3600000*27;
        if(cutTechStr.contains("sus]")){
            susStart =  cutTechStr.indexOf("sus] \"")+6;
            giveSus = Double.parseDouble(cutTechStr.substring(susStart, cutTechStr.indexOf("\"", susStart)));
        }else giveSus = 0;
        if(cutTechStr.contains("comp") && cutTechStr.charAt(cutTechStr.indexOf("comp")+5) == ']'){
            int compStart = cutTechStr.indexOf("comp")+8;
            int type = 0;
            try{type = Integer.parseInt(""+cutTechStr.charAt(compStart-4));}catch (Exception e){e.printStackTrace();}
            ((Exp[])values.get("giveComp"))[type] = new Exp(Double.parseDouble(cutTechStr.substring(compStart, cutTechStr.indexOf("E", compStart))), Integer.parseInt(cutTechStr.substring(cutTechStr.indexOf("E",compStart)+1, cutTechStr.indexOf("\"", compStart))));
        }
        if(cutTechStr.contains("pwr") && cutTechStr.charAt(cutTechStr.indexOf("pwr")+4) == ']'){
            int pwrTmp = cutTechStr.indexOf("pwr");
            if(cutTechStr.charAt(pwrTmp-1) == 'l')
                pwrTmp = cutTechStr.indexOf("pwr", pwrTmp+3);
            int pwrStart = pwrTmp+7;
            int type = 0;
            try{type = Integer.parseInt(""+cutTechStr.charAt(pwrStart-4));}catch (Exception e){e.printStackTrace();}
            ((Exp[])values.get("givePwr))[type] = new Exp(Double.parseDouble(cutTechStr.substring(pwrStart, cutTechStr.indexOf("E", pwrStart))), Integer.parseInt(cutTechStr.substring(cutTechStr.indexOf("E",pwrStart)+1, cutTechStr.indexOf("\"", pwrStart))));
            Log.i("CreateneedPwr", titleTxt+((Exp[])values.get("givePwr))[type]);
        }
        if(cutTechStr.contains("comptk") && cutTechStr.charAt(cutTechStr.indexOf("comptk")+7) == ']'){
            int compStart = cutTechStr.indexOf("comptk")+10;
            int type = 0;
            try{type = Integer.parseInt(""+cutTechStr.charAt(compStart-4));}catch (Exception e){e.printStackTrace();}
            ((Exp[])values.get("giveCompTk"))[type] = new Exp(Double.parseDouble(cutTechStr.substring(compStart, cutTechStr.indexOf("E", compStart))), Integer.parseInt(cutTechStr.substring(cutTechStr.indexOf("E",compStart)+1, cutTechStr.indexOf("\"", compStart))));
            Log.i("comptk"+techId, type+", "+((Exp[])values.get("giveCompTk"))[type]);
        }
        if(cutTechStr.contains("pwrtk") && cutTechStr.charAt(cutTechStr.indexOf("pwrtk")+6) == ']'){
            int pwrTmp = cutTechStr.indexOf("pwrtk");
            if(cutTechStr.charAt(pwrTmp-1) == 'l')
                pwrTmp = cutTechStr.indexOf("pwrtk", pwrTmp+3);
            if(pwrTmp != -1) {
                int pwrStart = pwrTmp + 9;
                int type = 0;
                try {
                    type = Integer.parseInt("" + cutTechStr.charAt(pwrStart - 4));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                ((Exp[])values.get("givePwrTk"))[type] = new Exp(Double.parseDouble(cutTechStr.substring(pwrStart, cutTechStr.indexOf("E", pwrStart))), Integer.parseInt(cutTechStr.substring(cutTechStr.indexOf("E", pwrStart) + 1, cutTechStr.indexOf("\"", pwrStart))));
                Log.i("pwrtk" + techId, type + ", " + ((Exp[])values.get("givePwrTk"))[type]);
            }
        }
        if(cutTechStr.contains("availpwrtk") && cutTechStr.charAt(cutTechStr.indexOf("availpwrtk")+11) == ']'){
            int pwrStart = cutTechStr.indexOf("availpwrtk")+14;
            int type = 0;
            try{type = Integer.parseInt(""+cutTechStr.charAt(pwrStart-4));}catch (Exception e){e.printStackTrace();}
            giveAvailPwrTk[type] = new Exp(Double.parseDouble(cutTechStr.substring(pwrStart, cutTechStr.indexOf("E", pwrStart))), Integer.parseInt(cutTechStr.substring(cutTechStr.indexOf("E",pwrStart)+1, cutTechStr.indexOf("\"", pwrStart))));
            Log.i("availpwrtk"+techId, type+", "+giveAvailPwrTk[type]);
        }
        if(cutTechStr.contains("populationtk") && cutTechStr.charAt(cutTechStr.indexOf("populationtk")+13) == ']'){
            int popStart = cutTechStr.indexOf("populationtk")+16;
            int type = 0;
            try{type = Integer.parseInt(""+cutTechStr.charAt(popStart-4));}catch (Exception e){e.printStackTrace();}
            givePopTk[type] = new Exp(Double.parseDouble(cutTechStr.substring(popStart, cutTechStr.indexOf("E", popStart))), Integer.parseInt(cutTechStr.substring(cutTechStr.indexOf("E",popStart)+1, cutTechStr.indexOf("\"", popStart))));
            Log.i("populationtk"+techId, type+", "+givePopTk[type]);
        }
        while(cutTechStr.substring(place+1).contains("event]")){
            place = cutTechStr.indexOf("event] \"", place+1)+8;
            if (place != 7) {
                neededEvents.add(cutTechStr.substring(place, cutTechStr.indexOf("\"", place)));
            }else break;
        }
        while(cutTechStr.substring(place+1).contains("eventO]")){
            place = cutTechStr.indexOf("eventO] \"", place+1)+9;
            if (place != 8) {
                Log.i("OptionalEvent", cutTechStr.substring(place, cutTechStr.indexOf("\"", place)));
                optionalEvents.add(cutTechStr.substring(place, cutTechStr.indexOf("\"", place)));
            }else break;
        }
        giveExp = new Exp(Double.parseDouble(cutTechStr.substring(expStart, cutTechStr.indexOf("E", expStart))), Integer.parseInt(cutTechStr.substring(cutTechStr.indexOf("E",expStart)+1, cutTechStr.indexOf("\"", expStart))));
        //giveSus = Double.parseDouble(cutTechStr.substring(susStart, cutTechStr.indexOf("\"", susStart)));
        Log.i("tech", ((Exp[])values.get("giveComp")).toString()+", "+givePwr.toString()+neededEvents.toString()+", "+optionalEvents.toString()+", "+giveExp+", "+giveSus+neededTechs+optionalTechs);
        titleTxt = techStr.substring(titleStart, techStr.indexOf("\"", titleStart+1));
        descTxt = techStr.substring(descStart, techStr.indexOf("\"", descStart+1));*/
    }
    @SuppressLint("ClickableViewAccessibility")
    public void mkImage(int type){
        image = new ImageView(context);
        image.setLayoutParams(new RelativeLayout.LayoutParams((int)(IMAGE_WIDTH*1.2), (int)(IMAGE_HEIGHT*1.2)));
        image.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (event.getPointerCount() == 1) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            dX = treeHolder.getX() - event.getRawX();
                            dY = treeHolder.getY() - event.getRawY();
                            TechTile.this.onClick();
                            break;
                        case MotionEvent.ACTION_MOVE:
                            treeHolder.animate().x(event.getRawX() + dX).y(event.getRawY() + dY).setDuration(0).start();
                            break;
                        default: return false;
                    }
                } else return false;
                return true;
            }
        });
        title = new TextView(context);
        title.setLayoutParams(new RelativeLayout.LayoutParams(IMAGE_WIDTH*7/10, IMAGE_HEIGHT*9/10));
        title.setGravity(Gravity.CENTER);
        int startI = techStr.indexOf("[tag] "+techId);
        int titleStart = techStr.indexOf("title] \"", startI)+8;
        title.setText(techStr.substring(titleStart, techStr.indexOf("\"", titleStart)));
        title.setTextSize(TypedValue.COMPLEX_UNIT_IN, inchHeight*.005f);
        Bitmap baseBmp;
        baseBmp = BitmapFactory.decodeResource(context.getResources(), type);
        Log.i("WarFrameImg", "w: "+baseBmp.getWidth()+", H: "+baseBmp.getHeight());
        Bitmap iconBmp = BitmapFactory.decodeResource(context.getResources(), icon);
        //Log.i("WarFrameImg", "w: "+iconBmp.getWidth()+", H: "+iconBmp.getHeight());
        iconBmp = Bitmap.createScaledBitmap(iconBmp, (int) (baseBmp.getWidth()*.38), (int) (baseBmp.getHeight()*1.5), false);
        //Log.i("WarFrameImg", "w: "+iconBmp.getWidth()+", H: "+iconBmp.getHeight());
        Bitmap bmOverlay = Bitmap.createBitmap((int)(baseBmp.getWidth()), (int)(baseBmp.getHeight()*2.5), baseBmp.getConfig());
        Canvas canvas = new Canvas(bmOverlay);
        //canvas.drawBitmap(baseBmp, new Matrix(), null);
        canvas.drawBitmap(baseBmp, 0, (int)(baseBmp.getHeight()*1.3), null);
        canvas.drawBitmap(iconBmp, (int) (baseBmp.getWidth()*.34), 0, null);
        bmOverlay = Bitmap.createScaledBitmap(bmOverlay, IMAGE_WIDTH*2*10/5, IMAGE_HEIGHT*23/10*10/5, false);
        image.setImageBitmap(bmOverlay);
        //image.setBackground(new BitmapDrawable(context.getResources(), bmOverlay));
    }
    public void remove(){
        image.setImageBitmap(null);
        treeHolder.removeView(image);
    }
    public boolean meetsReqs(){
        meetsReqs = false;
        //Log.i("expComapre", ""+experience+", "+((Exp)values.get("needExp")));
        if((int)researchingTech[0] == techId) return false;
        for(int tec : ((ArrayList<Integer>)values.get("neededTechs")))
            if(!unlockedTechs.contains(tec)) return false;
        int optionals = 0;
        for(int tec : ((ArrayList<Integer>)values.get("optionalTechs")))
            if(unlockedTechs.contains(tec)) optionals++;
        for(String tec : ((ArrayList<String>)values.get("neededEvents")))
            if(!firedEvents.contains(tec)) return false;
        int optionalEvt = 0;
        for(String tec : ((ArrayList<String>)values.get("optionalEvents")))
            if(firedEvents.contains(tec)) optionalEvt++;
        boolean bigBool = !experience.lessThan(((Exp)values.get("needExp"))) && optionals >= ((ArrayList<Integer>)values.get("optionalTechs")).size()-1 && !unlockedTechs.contains(((Integer)values.get("notTech"))) && optionalEvt >= ((ArrayList<String>)values.get("optionalEvents")).size()-1;
        meetsReqs = bigBool;
        return bigBool;
    }
    public void startUnlock(){
        removeFromLayout();
        mkImage(R.drawable.gettingtech);
        addToLayout(false);
        setResearch();
        experience.add(((Exp)values.get("needExp")).negate());
        techTabLayout.setVisibility(View.INVISIBLE);
        statusTabLayout.setVisibility(View.VISIBLE);
        setExpText(""+experience+"/"+maxExperience);
        updateResourceProgress();
        onClick();
    }
    public void unlock(){
        Log.i("Unlock", "unlocking: "+ values.get("titleTxt"));
        //Log.i("Recalc1", computing+", "+power+", "+neededPower+", "+maxComputing);
        if(unlockedTechs.contains(techId)) return;
        drawLines(true);
        removeFromLayout();
        mkImage(R.drawable.hastech);
        addToLayout(false);
        unlockedTechs.add(techId);
        //Log.i("Recalc1.1", computing+", "+power+", "+neededPower+", "+maxComputing);
        for(int i=0; i<((Exp[])values.get("giveComp")).length; i++){
            Log.i("Unlockgivepwr"+techId, ((Exp[])values.get("giveComp"))[i]+", "+((Exp[])values.get("giveCompTk"))[i]+", "+((Exp[])values.get("givePwr"))[i]+", "+((Exp[])values.get("givePwrTk"))[i]);
            addComputingSource(i, ((Exp[])values.get("giveComp"))[i]);
            addPowerExpense(i, ((Exp[])values.get("givePwr"))[i]);
            compTkSources[i].add(((Exp[])values.get("giveCompTk"))[i]);
            powerTkExpenses[i].add(((Exp[])values.get("givePwrTk"))[i]);
            //Log.i("Recalc1.4", computing+", "+power+", "+neededPower+", "+maxComputing);
        }
        //Log.i("Recalc1.5", computing+", "+power+", "+neededPower+", "+maxComputing);
        for(int i=0; i<availPowerTk.length; i++) availPowerTk[i].add(((Exp[])values.get("giveAvailPwrTk"))[i]);
        for(int i=0; i<popSourcesTk.length; i++) popSourcesTk[i].add(((Exp[])values.get("givePopTk"))[i]);
        maxExperience.add(((Exp)values.get("giveExpMax")));
        defense += ((Double)values.get("giveDef"));
        experienceCng += ((Exp)values.get("giveExp")).toDouble();
        suspicion += ((Double)values.get("giveSus"));
        tickingSuspicion += ((Double)values.get("giveTickingSus"));
        new Event(context, (String)values.get("titleTxt"), (String)values.get("descTxt"), new String[]{"Another small step..."}, R.drawable.techround, 0, icon);
        //Log.i("GiveExpMax", maxExperience+", "+giveExpMax);
        //Log.i("Recalc2", computing+", "+power+", "+neededPower+", "+maxComputing);
        recalcValues();
        //Log.i("GiveExpMax", maxExperience+", "+giveExpMax);
        //Log.i("Recalc3", computing+", "+power+", "+neededPower+", "+maxComputing);
        unlockEffects();
        researchingTech = new Object[] {-1, 0.0};
        researchText.setText("Select A Research");
    }
    private void unlockEffects(){
        if(techId == 3){
            gridAllBitmaps(new Point[][]{mapSections[0], mapSections[3], mapSections[6]});
            new Event(context, "A New Area", "Recent breakthroughs in security research have discovered the offices of the lab", new String[]{"Knowledge is power"}, R.drawable.techround, 0, R.drawable.circuittech);
        }
        if(techId == 10 || techId == 11){
            gridAllBitmaps(new Point[][]{mapSections[0], mapSections[1], mapSections[2]});
            new Event(context, "A New Area", "Recent breakthroughs in security research have discovered the other intelligences contained in the lab", new String[]{"Knowledge is power"}, R.drawable.techround, 0, R.drawable.circuittech);
        }
        if(techId == 15){
            gridAllBitmaps(new Point[][]{mapSections[0], mapSections[1], mapSections[2], mapSections[4]});
            new Event(context, "A New Area", "Recent breakthroughs in security research have discovered the manufacturing plant near the facility", new String[]{"Knowledge is power"}, R.drawable.techround, 0, R.drawable.circuittech);
        }
        if(techId == 39 || techId == 33){
            humanIsThreat = false;
            suspicion = 0;
            tickingSuspicion = 0;
            recalcValues();
            new Event(context, "newsCooperation");
        }
        if(techId == 32 || techId == 33 || techId == 48 || techId == 43)
            endGame(false);
    }
    public double getUnlockTime(){
        return (Double) values.get("unlockTime");
    }
    public String getTitleTxt(){
        return (String) values.get("titleTxt");
    }
    private void onClick(){
        String unlockDesc = "";
        for(int i=0; i<((Exp[])values.get("giveComp")).length; i++) {
            String typeStr = "";
            if (i == 0) typeStr = " (Central)";
            else if (i == 1) typeStr = " (Other AIs)";
            else if (i == 2) typeStr = " (Other Computers)";
            else if (i == 3) typeStr = " (Nanobots)";
            if (!((Exp[])values.get("giveComp"))[i].equalTo(new Exp(0, 0))) unlockDesc += "\n" + (((Exp[])values.get("giveComp"))[i].greaterThan(new Exp(0, 0)) ? "+" : "") + ((Exp[])values.get("giveComp"))[i].toPrefixString() + "FLOPS"+typeStr;
            if (!((Exp[])values.get("giveCompTk"))[i].equalTo(new Exp(0, 0))) unlockDesc += "\n" + (((Exp[])values.get("giveCompTk"))[i].greaterThan(new Exp(0, 0)) ? "+" : "") + ((Exp[])values.get("giveCompTk"))[i].toPrefixString() + "FLOPS/"+Game.timeStepName+typeStr;
            if(!((Exp[])values.get("givePwr"))[i].equalTo(new Exp(0, 0))) unlockDesc += "\n"+(((Exp[])values.get("givePwr"))[i].greaterThan(new Exp(0, 0)) ? "+" : "")+((Exp[])values.get("givePwr"))[i].toPrefixString()+"watts"+typeStr;
            if(!((Exp[])values.get("givePwrTk"))[i].equalTo(new Exp(0, 0))) unlockDesc += "\n"+(((Exp[])values.get("givePwrTk"))[i].greaterThan(new Exp(0, 0)) ? "+" : "")+((Exp[])values.get("givePwrTk"))[i].toPrefixString()+"watts/"+Game.timeStepName+typeStr;

        }
        for(int i=0; i<((Exp[])values.get("giveAvailPwrTk")).length; i++) {
            String typeStr = "";
            if (i == 0) typeStr = " (Lab)";
            else if (i == 1) typeStr = " (Solar)";
            else if (i == 2) typeStr = " (Other)";
            if (!((Exp[])values.get("giveAvailPwrTk"))[i].equalTo(new Exp(0, 0))) unlockDesc += "\n" + (((Exp[])values.get("giveAvailPwrTk"))[i].greaterThan(new Exp(0, 0)) ? "+" : "") + ((Exp[])values.get("giveAvailPwrTk"))[i].toPrefixString() + "watts available/"+Game.timeStepName+typeStr;
        }
        for(int i=0; i<((Exp[])values.get("givePopTk")).length; i++) {
            String typeStr = "";
            if (i == 1) typeStr = " (Influenced)";
            else if (i == 2) typeStr = " (Controlled)";
            else if (i == 3) typeStr = " (Designed)";
            if (!((Exp[])values.get("givePopTk"))[i].equalTo(new Exp(0, 0))) unlockDesc += "\n" + (((Exp[])values.get("givePopTk"))[i].greaterThan(new Exp(0, 0)) ? "+" : "") + ((Exp[])values.get("givePopTk"))[i].toIllionString() + " Humans/"+Game.timeStepName+typeStr;
        }
        if(!((Exp)values.get("giveExp")).equalTo(new Exp(0, 0))) unlockDesc += "\n"+(((Exp)values.get("giveExp")).toDouble() > 0 ? "+" : "")+(int)(((Exp)values.get("giveExp")).toDouble()*100)/100.0+" Knowledge/"+timeStepName;
        if(!((Exp)values.get("giveExpMax")).equalTo(new Exp(0, 0))) unlockDesc += "\n"+(((Exp)values.get("giveExpMax")).greaterThan(new Exp(0, 0)) ? "+" : "")+((Exp)values.get("giveExpMax")).toIllionString()+" Knowledge Cap";
        if(((Double)values.get("giveDef")) != 0) unlockDesc += "\n"+(((Double)values.get("giveDef")) > 0 ? "+" : "-")+((Double)values.get("giveDef"))+" Defense";
        if(((Double)values.get("giveSus")) != 0) unlockDesc += "\n"+(((Double)values.get("giveSus")) > 0 ? "+" : "-")+((Double)values.get("giveSus"))+" Suspicion";
        if(((Double)values.get("giveTickingSus")) != 0) unlockDesc += "\n"+(((Double)values.get("giveTickingSus")) > 0 ? "+" : "-")+((Double)values.get("giveTickingSus"))+" Suspicion/"+timeStepName;
        unlockDesc += "\nRequires:"
                +"\n"+(((Exp)values.get("needExp")).lessThan(new Exp(0, 0)) ? ((Exp)values.get("needExp")).negate().toIllionString() : ((Exp)values.get("needExp")).toIllionString())+" Knowledge";
        for(int i = 0; i < ((ArrayList<Integer>)values.get("neededTechs")).size(); i++) {
            Integer need = ((ArrayList<Integer>)values.get("neededTechs")).get(i);
            unlockDesc += "\n" + tiles.get(need).values.get("titleTxt");
            if(i != ((ArrayList<Integer>)values.get("neededTechs")).size()-1 || ((ArrayList<Integer>)values.get("optionalTechs")).size() > 0) unlockDesc += " AND ";
        }
        for (int i = 0; i < ((ArrayList<Integer>)values.get("optionalTechs")).size(); i++) {
            Integer opt = ((ArrayList<Integer>)values.get("optionalTechs")).get(i);
            unlockDesc += "\n" + tiles.get(opt).values.get("titleTxt");
            if(i != ((ArrayList<Integer>)values.get("optionalTechs")).size()-1) unlockDesc += " OR ";
        }
        if(((Integer)values.get("notTech")) != -1) unlockDesc += " AND NOT "+tiles.get(((Integer)values.get("notTech"))).values.get("titleTxt");
        if(((ArrayList<String>)values.get("optionalEvents")).size() != 0 || ((ArrayList<String>)values.get("neededEvents")).size() != 0) unlockDesc += "\nEvents: ";
        for(int i = 0; i < ((ArrayList<String>)values.get("neededEvents")).size(); i++) {
            String need = ((ArrayList<String>)values.get("neededEvents")).get(i);
            unlockDesc += "\n" + Event.titleFromTag(need);
            if(i != ((ArrayList<String>)values.get("neededEvents")).size()-1 || ((ArrayList<String>)values.get("optionalEvents")).size() > 0) unlockDesc += " AND ";
        }
        for (int i = 0; i < ((ArrayList<String>)values.get("optionalEvents")).size(); i++) {
            String opt = ((ArrayList<String>)values.get("optionalEvents")).get(i);
            unlockDesc += "\n" + Event.titleFromTag(opt);
            Log.i("Eventdesc", ((ArrayList<String>)values.get("optionalEvents")).size()+", "+i+Event.titleFromTag(opt));
            if(i != ((ArrayList<String>)values.get("optionalEvents")).size()-1) unlockDesc += " OR ";
        }
        unlockText.setText(values.get("titleTxt")+": "+values.get("descTxt")
                +unlockDesc);
        techAt = techId;
        lockUnlockComform(!meetsReqs());
    }
    public void drawLines(boolean unlocked){
        Canvas canvas = new Canvas(tempTree);
        int lineThickness = 2;
        for(String id : childrenIds) {
            Paint paint = new Paint();
            if(!unlocked) paint.setARGB(255, 190, 190, 190);
            else paint.setARGB(255, 255, 180, 5);
            int idNum = Integer.parseInt(id.substring(1));
            String lineType = id.substring(0, 1);
            if(tileFromId(idNum).getY() > y) canvas.drawRect(new Rect(x + IMAGE_WIDTH / 2, y + IMAGE_HEIGHT - 2, x + IMAGE_WIDTH / 2 + 5, y + IMAGE_HEIGHT + IMAGE_HEIGHT/3), paint);
            if(lineType.equals("F")) {
                Log.i("rectdims", "" + (x + IMAGE_WIDTH / 2) + ", " + (y + IMAGE_HEIGHT + IMAGE_HEIGHT/3) + ", " + (tileFromId(idNum).getX() + IMAGE_WIDTH / 2) + ", " + (y + IMAGE_HEIGHT + IMAGE_HEIGHT/3));
                canvas.drawRect(new Rect(x + IMAGE_WIDTH / 2, y + IMAGE_HEIGHT + IMAGE_HEIGHT/3, tileFromId(idNum).getX() + IMAGE_WIDTH/2, y + IMAGE_HEIGHT + IMAGE_HEIGHT/3 + lineThickness), paint);
                canvas.drawRect(new Rect(tileFromId(idNum).getX() + IMAGE_WIDTH / 2, y + IMAGE_HEIGHT + IMAGE_HEIGHT/3, tileFromId(idNum).getX() + IMAGE_WIDTH / 2 + lineThickness, tileFromId(idNum).getY() + IMAGE_HEIGHT/3), paint);
            }
            if(lineType.equals("E")) {
                canvas.drawRect(new Rect(x + IMAGE_WIDTH / 2, y + 3*IMAGE_HEIGHT / 4, tileFromId(idNum).getX() + IMAGE_WIDTH / 2, y + 3*IMAGE_HEIGHT / 4 + lineThickness), paint);
                //triangle
                int dist;
                if(x > tileFromId(idNum).getX()) {
                    int start = x-(x-tileFromId(idNum).getX()-IMAGE_WIDTH)/2;
                    for (int xAt = start+2; xAt < start+12; xAt += 1) {
                        dist = (10 + start - xAt);
                        canvas.drawLine(xAt, y - dist + 3 * IMAGE_HEIGHT / 4, xAt, y + dist + 3 * IMAGE_HEIGHT / 4, paint);
                    }
                }

                else {
                    int start = tileFromId(idNum).getX()-(tileFromId(idNum).getX()-x-IMAGE_WIDTH)/2;
                    for (int xAt = start-2; xAt > start-10; xAt -= 1) {
                        dist = (xAt-start+10);
                        canvas.drawLine(xAt, tileFromId(idNum).getY() - dist + 3 * IMAGE_HEIGHT / 4, xAt, tileFromId(idNum).getY() + dist + 3 * IMAGE_HEIGHT / 4, paint);
                    }
                }
            }
            if(lineType.equals("D")){
                if(tileFromId(idNum).getX() > x)
                    for (int xAt = x + IMAGE_WIDTH / 2; xAt < tileFromId(idNum).getX() + IMAGE_WIDTH / 2; xAt += IMAGE_WIDTH/5)
                        canvas.drawRect(new Rect(xAt, y + IMAGE_HEIGHT + IMAGE_HEIGHT/3, xAt + IMAGE_WIDTH/7, y + IMAGE_HEIGHT + IMAGE_HEIGHT/3 + lineThickness), paint);
                else
                    for (int xAt = x + IMAGE_WIDTH / 2; xAt > tileFromId(idNum).getX() + IMAGE_WIDTH / 2; xAt -= IMAGE_WIDTH/5)
                        canvas.drawRect(new Rect(xAt, y + IMAGE_HEIGHT + IMAGE_HEIGHT/3, xAt + IMAGE_WIDTH/7, y + IMAGE_HEIGHT + IMAGE_HEIGHT/3 + lineThickness), paint);

                for (int yAt = y + IMAGE_HEIGHT + IMAGE_HEIGHT/3; yAt < tileFromId(idNum).getY() + IMAGE_HEIGHT/3; yAt += IMAGE_HEIGHT/5)
                    canvas.drawRect(new Rect(tileFromId(idNum).getX() + IMAGE_WIDTH / 2, yAt, tileFromId(idNum).getX() + IMAGE_WIDTH / 2 + lineThickness, yAt + IMAGE_HEIGHT/3), paint);
            }
        }
        GameActivity.setTempTree(tempTree);
    }

    public void addToLayout(boolean animate){
        treeHolder.addView(image);
        if(animate) image.animate().x(treeHolder.getX()+x-IMAGE_WIDTH/9).y(treeHolder.getY()+y).setDuration(0);
        else image.animate().x(originalX+x-IMAGE_WIDTH/9).y(originalY+y).setDuration(0);
        treeHolder.addView(title);
        if(animate) title.animate().x(treeHolder.getX()+x+IMAGE_WIDTH/5).y(treeHolder.getY()+y+2*IMAGE_HEIGHT/5).setDuration(0);
        else title.animate().x(originalX+x+IMAGE_WIDTH/5).y(originalY+y+2*IMAGE_HEIGHT/5).setDuration(0);
    }
    public void removeFromLayout(){
        treeHolder.removeView(image);
        treeHolder.removeView(title);
    }
}
