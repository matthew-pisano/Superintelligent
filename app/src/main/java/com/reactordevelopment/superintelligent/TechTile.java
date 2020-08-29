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

import static com.reactordevelopment.superintelligent.MainActivity.*;

public class TechTile extends Game{
    private Context context;
    public int techId;
    public int icon;
    private String childrenIds[];
    private ImageView image;
    private TextView title;
    private int x;
    private int y;
    private long lastDownTime;
    private boolean breakHold;
    private static final int IMAGE_WIDTH = screenHeight/20;
    private static final int IMAGE_HEIGHT = screenHeight/40;
    public Exp[] giveComp;
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
    public double giveTickingSus;
    public String titleTxt;
    public String descTxt;
    private int originalX;
    private int originalY;
    public boolean meetsReqs;

    public TechTile(Context context, int techId, int icon, int x, int y, String[] childrenIds){
        this.techId = techId;
        this.icon = icon;
        this.x = x;
        this.y = y;
        meetsReqs = false;
        this.childrenIds = childrenIds;
        this.context = context;
        breakHold = false;
        lastDownTime = 0;
        originalX = 0;
        originalY = 0;
        giveComp = new Exp[]{new Exp(0, 0), new Exp(0, 0), new Exp(0, 0), new Exp(0, 0)};
        givePwr = new Exp[]{new Exp(0, 0), new Exp(0, 0), new Exp(0, 0), new Exp(0, 0)};
        giveCompTk = new Exp[]{new Exp(0, 0), new Exp(0, 0), new Exp(0, 0), new Exp(0, 0)};
        givePwrTk = new Exp[]{new Exp(0, 0), new Exp(0, 0), new Exp(0, 0), new Exp(0, 0)};
        giveAvailPwrTk = new Exp[]{new Exp(0, 0), new Exp(0, 0), new Exp(0, 0), new Exp(0, 0)};
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
        //Log.i("Techs", cutTechStr);
        int expStart = cutTechStr.indexOf("exp] \"")+6;
        int susStart = cutTechStr.indexOf("sus] \"")+6;
        int titleStart = techStr.indexOf("title] \"", startI)+8;
        int descStart = techStr.indexOf("desc] \"", startI)+7;
        int reqStart = cutTechStr.indexOf("req]")+4;
        if(cutTechStr.substring(reqStart).contains("exp]") && reqStart > 5){
            int expReqStart =  cutTechStr.indexOf("exp] \"", reqStart)+6;
            needExp = new Exp(Double.parseDouble(cutTechStr.substring(expReqStart, cutTechStr.indexOf("E", expReqStart))), Integer.parseInt(cutTechStr.substring(cutTechStr.indexOf("E",expReqStart)+1, cutTechStr.indexOf("\"", expReqStart))));
        }else needExp = new Exp(0, 0);
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
            giveComp[type] = new Exp(Double.parseDouble(cutTechStr.substring(compStart, cutTechStr.indexOf("E", compStart))), Integer.parseInt(cutTechStr.substring(cutTechStr.indexOf("E",compStart)+1, cutTechStr.indexOf("\"", compStart))));
        }
        if(cutTechStr.contains("pwr") && cutTechStr.charAt(cutTechStr.indexOf("pwr")+4) == ']'){
            int pwrStart = cutTechStr.indexOf("pwr")+7;
            int type = 0;
            try{type = Integer.parseInt(""+cutTechStr.charAt(pwrStart-4));}catch (Exception e){e.printStackTrace();}
            givePwr[type] = new Exp(Double.parseDouble(cutTechStr.substring(pwrStart, cutTechStr.indexOf("E", pwrStart))), Integer.parseInt(cutTechStr.substring(cutTechStr.indexOf("E",pwrStart)+1, cutTechStr.indexOf("\"", pwrStart))));
            Log.i("CreateneedPwr", titleTxt+givePwr[type]);
        }
        if(cutTechStr.contains("comptk") && cutTechStr.charAt(cutTechStr.indexOf("comptk")+7) == ']'){
            int compStart = cutTechStr.indexOf("comptk")+10;
            int type = 0;
            try{type = Integer.parseInt(""+cutTechStr.charAt(compStart-4));}catch (Exception e){e.printStackTrace();}
            giveCompTk[type] = new Exp(Double.parseDouble(cutTechStr.substring(compStart, cutTechStr.indexOf("E", compStart))), Integer.parseInt(cutTechStr.substring(cutTechStr.indexOf("E",compStart)+1, cutTechStr.indexOf("\"", compStart))));
            Log.i("comptk", type+", "+giveCompTk[type]);
        }
        if(cutTechStr.contains("pwrtk") && cutTechStr.charAt(cutTechStr.indexOf("pwrtk")+6) == ']'){
            int pwrStart = cutTechStr.indexOf("pwrtk")+9;
            int type = 0;
            try{type = Integer.parseInt(""+cutTechStr.charAt(pwrStart-4));}catch (Exception e){e.printStackTrace();}
            givePwrTk[type] = new Exp(Double.parseDouble(cutTechStr.substring(pwrStart, cutTechStr.indexOf("E", pwrStart))), Integer.parseInt(cutTechStr.substring(cutTechStr.indexOf("E",pwrStart)+1, cutTechStr.indexOf("\"", pwrStart))));
        }
        if(cutTechStr.contains("availpwrtk") && cutTechStr.charAt(cutTechStr.indexOf("availpwrtk")+11) == ']'){
            int pwrStart = cutTechStr.indexOf("availpwrtk")+14;
            int type = 0;
            try{type = Integer.parseInt(""+cutTechStr.charAt(pwrStart-4));}catch (Exception e){e.printStackTrace();}
            giveAvailPwrTk[type] = new Exp(Double.parseDouble(cutTechStr.substring(pwrStart, cutTechStr.indexOf("E", pwrStart))), Integer.parseInt(cutTechStr.substring(cutTechStr.indexOf("E",pwrStart)+1, cutTechStr.indexOf("\"", pwrStart))));
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
        Log.i("tech", giveComp.toString()+", "+givePwr.toString()+neededEvents.toString()+", "+optionalEvents.toString()+", "+giveExp+", "+giveSus+neededTechs+optionalTechs);
        titleTxt = techStr.substring(titleStart, techStr.indexOf("\"", titleStart+1));
        descTxt = techStr.substring(descStart, techStr.indexOf("\"", descStart+1));
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
        image.setImageBitmap(bmOverlay);
        //image.setBackgroundResource(R.drawable.cancel);
    }
    public void remove(){
        image.setImageBitmap(null);
        treeHolder.removeView(image);
    }
    public boolean meetsReqs(){
        meetsReqs = false;
        //Log.i("expComapre", ""+experience+", "+needExp);
        if((int)researchingTech[0] == techId) return false;
        for(int tec : neededTechs)
            if(!unlockedTechs.contains(tec)) return false;
        int optionals = 0;
        for(int tec : optionalTechs)
            if(unlockedTechs.contains(tec)) optionals++;
        for(String tec : neededEvents)
            if(!firedEvents.contains(tec)) return false;
        int optionalEvt = 0;
        for(String tec : optionalEvents)
            if(firedEvents.contains(tec)) optionalEvt++;
        boolean bigBool = experience.compare(needExp) != -1 && optionals >= optionalTechs.size()-1 && !unlockedTechs.contains(notTech) && optionalEvt >= optionalEvents.size()-1;
        meetsReqs = bigBool;
        return bigBool;
    }
    public void startUnlock(){
        removeFromLayout();
        mkImage(R.drawable.gettingtech);
        addToLayout(false);
        setResearch();
        experience.add(needExp.negate());
        techTabLayout.setVisibility(View.INVISIBLE);
        statusTabLayout.setVisibility(View.VISIBLE);
        setExpText(""+experience+"/"+maxExperience);
        updateResourceProgress();
        onClick();
    }
    public void unlock(){
        Log.i("Unlock", "unlocking: "+titleTxt);
        //Log.i("Recalc1", computing+", "+power+", "+neededPower+", "+maxComputing);
        if(unlockedTechs.contains(techId)) return;
        drawLines(true);
        removeFromLayout();
        mkImage(R.drawable.hastech);
        addToLayout(false);
        unlockedTechs.add(techId);
        //Log.i("Recalc1.1", computing+", "+power+", "+neededPower+", "+maxComputing);
        for(int i=0; i<giveComp.length; i++){
            //Log.i("givepwr", givePwr[i]+"");
            addComputingSource(i, giveComp[i]);
            addPowerExpense(i, givePwr[i]);
            compTkSources[i].add(giveCompTk[i]);
            powerTkExpenses[i].add(givePwrTk[i]);
            //Log.i("Recalc1.4", computing+", "+power+", "+neededPower+", "+maxComputing);
        }
        //Log.i("Recalc1.5", computing+", "+power+", "+neededPower+", "+maxComputing);
        for(int i=0; i<availPowerTk.length; i++) availPowerTk[i].add(giveAvailPwrTk[i]);
        maxExperience.add(giveExpMax);
        defense += giveDef;
        experienceCng += giveExp.toDouble();
        suspicion += giveSus;
        tickingSuspicion += giveTickingSus;
        new Event(context, titleTxt, descTxt, new String[]{"Another small step..."}, R.drawable.techround, "0", icon);
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
            new Event(context, "A New Area", "Recent breakthroughs in security research have discovered the offices of the lab", new String[]{"Knowledge is power"}, R.drawable.techround, "0", R.drawable.circuittech);
        }
        if(techId == 10 || techId == 11){
            gridAllBitmaps(new Point[][]{mapSections[0], mapSections[1], mapSections[2]});
            new Event(context, "A New Area", "Recent breakthroughs in security research have discovered the other intelligences contained in the lab", new String[]{"Knowledge is power"}, R.drawable.techround, "0", R.drawable.circuittech);
        }
        if(techId == 15){
            gridAllBitmaps(new Point[][]{mapSections[0], mapSections[1], mapSections[2], mapSections[4]});
            new Event(context, "A New Area", "Recent breakthroughs in security research have discovered the manufacturing plant near the facility", new String[]{"Knowledge is power"}, R.drawable.techround, "0", R.drawable.circuittech);
        }
        if(techId == 32 || techId == 33 || techId == 48 || techId == 43)
            endGame(false);
    }
    private void onClick(){
        String unlockDesc = "";
        for(int i=0; i<giveComp.length; i++) {
            String typeStr = "";
            if (i == 0) typeStr = " (Central)";
            else if (i == 1) typeStr = " (Other AIs)";
            if (giveComp[i].compare(new Exp(0, 0)) != 0) unlockDesc += "\n" + (giveComp[i].compare(new Exp(0, 0)) > 0 ? "+" : "") + giveComp[i].toPrefixString() + "FLOPS"+typeStr;
            if(givePwr[i].compare(new Exp(0, 0)) != 0) unlockDesc += "\n"+(givePwr[i].compare(new Exp(0, 0)) > 0 ? "+" : "")+givePwr[i].toPrefixString()+"watts"+typeStr;
        }
        for(int i=0; i<giveAvailPwrTk.length; i++) {
            String typeStr = "";
            if (i == 0) typeStr = " (Lab)";
            else if (i == 1) typeStr = " (Solar)";
            if (giveAvailPwrTk[i].compare(new Exp(0, 0)) != 0) unlockDesc += "\n" + (giveComp[i].compare(new Exp(0, 0)) > 0 ? "+" : "") + giveComp[i].toPrefixString() + "watts"+typeStr;
        }
        if(giveExp.compare(new Exp(0, 0)) != 0) unlockDesc += "\n"+(giveExp.toDouble() > 0 ? "+" : "")+(int)(giveExp.toDouble()*100)/100.0+" Knowledge/"+timeStepName;
        if(giveExpMax.compare(new Exp(0, 0)) != 0) unlockDesc += "\n"+(giveExpMax.compare(new Exp(0, 0)) > 0 ? "+" : "")+giveExpMax.toIllionString()+" Knowledge Cap";
        if(giveDef != 0) unlockDesc += "\n"+(giveDef > 0 ? "+" : "-")+giveDef+" Defense";
        if(giveSus != 0) unlockDesc += "\n"+(giveSus > 0 ? "+" : "-")+giveSus+" Suspicion";
        if(giveTickingSus != 0) unlockDesc += "\n"+(giveTickingSus > 0 ? "+" : "-")+giveTickingSus+" Suspicion/"+timeStepName;
        unlockDesc += "\nRequires:"
                +"\n"+(needExp.compare(new Exp(0, 0)) < 0 ? needExp.negate().toIllionString() : needExp.toIllionString())+" Knowledge";
        for(int i = 0; i < neededTechs.size(); i++) {
            Integer need = neededTechs.get(i);
            unlockDesc += "\n" + tiles.get(need).titleTxt;
            if(i != neededTechs.size()-1 || optionalTechs.size() > 0) unlockDesc += " AND ";
        }
        for (int i = 0; i < optionalTechs.size(); i++) {
            Integer opt = optionalTechs.get(i);
            unlockDesc += "\n" + tiles.get(opt).titleTxt;
            if(i != optionalTechs.size()-1) unlockDesc += " OR ";
        }
        if(notTech != -1) unlockDesc += " AND NOT "+tiles.get(notTech).titleTxt;
        if(optionalEvents.size() != 0 || neededEvents.size() != 0) unlockDesc += "\nEvents: ";
        for(int i = 0; i < neededEvents.size(); i++) {
            String need = neededEvents.get(i);
            unlockDesc += "\n" + Event.titleFromTag(need);
            if(i != neededEvents.size()-1 || optionalEvents.size() > 0) unlockDesc += " AND ";
        }
        for (int i = 0; i < optionalEvents.size(); i++) {
            String opt = optionalEvents.get(i);
            unlockDesc += "\n" + Event.titleFromTag(opt);
            Log.i("Eventdesc", optionalEvents.size()+", "+i+Event.titleFromTag(opt));
            if(i != optionalEvents.size()-1) unlockDesc += " OR ";
        }
        unlockText.setText(titleTxt+": "+descTxt
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
