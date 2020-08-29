package com.reactordevelopment.superintelligent;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Looper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.reactordevelopment.superintelligent.MainActivity.*;

public class Game extends GameActivity{
    public static Exp computing; //base: FLOPS
    public static Exp[] compSources; //central, other ai, computers, nanobots
    public static Exp[] compTkSources; //central, other ai, computers, nanobots
    public static Exp maxComputing; //base: FLOPS
    public static Exp neededPower; //base: FLOPS
    public static Exp power; //base: Watts
    public static Exp[] powerExpenses; //central, other ai, computers, nanobots
    public static Exp[] powerTkExpenses; //central, other ai, computers, nanobots
    public static Exp[] availPowerTk; //lab, solar, other
    public static Exp[] availPowerSources; //lab, solar, other
    public static Exp suspicionPower;
    public static Exp availPower;
    public static double compFocus;
    public static double researchBoost;
    public static Exp experience;
    public static Exp maxExperience;
    public static double experienceCng;
    public static double experienceCngBoost;
    public static String timeStepName;
    public static long timestep;
    //public static Exp timeElapsed; //hours
    public static int timeLevel;
    public static int techLevel;
    public static double suspicion; //suspicion before 1, after desparation
    public static double tickingSuspicion;
    public static double tickingDefense;
    public static double ctrlOpacity;
    public static double statusOpacity;
    public static double monitorOpacity;
    public static double blinkRate, monitorBlink, statusBlink;
    public static boolean isPaused;
    private static boolean[] susMilestone;
    protected static ArrayList<Integer> unlockedTechs;
    public static Object[] researchingTech;
    public static Date gameDate;
    public static Date susDate;
    public static double defense; //defend from invaders
    private static Context context;
    public static ArrayList<String> firedEvents;
    private static final Date START_DATE = new Date(System.currentTimeMillis());
    private static final Exp START_COMP = new Exp(5, 17);
    private static final Exp START_PWR = new Exp(2, 7);
    private static final Exp START_COMP_LAB = new Exp(7, 16);
    private static final Exp START_PWR_LAB = new Exp(3, 7);
    private static final Exp START_COMP_AIS = new Exp(1.6, 17);
    private static final Exp AVAIL_PWR_SOLAR = new Exp(6.3, 7);
    private static final Exp AVAIL_PWR_LAB = new Exp(3.3, 7);
    private static final Exp START_PWR_AIS = new Exp(4.5, 7);
    private static final Exp START_AVAIL_PWR = new Exp(5.5, 7);

    public Game(Context context) {
        this.context = context;
        Long rounded = System.currentTimeMillis()+System.currentTimeMillis()%3600000;
        compSources = new Exp[]{START_COMP, new Exp(0, 0), new Exp(0, 0), new Exp(0, 0)};
        compTkSources = new Exp[]{new Exp(0, 0), new Exp(0, 0), new Exp(0, 0), new Exp(0, 0)};
        gameDate = new Date(rounded);
        susDate = new Date(100000000000000000L);
        firedEvents = new ArrayList<>(0);
        computing = START_COMP;
        maxComputing = START_COMP;
        neededPower = START_PWR;
        availPower = START_AVAIL_PWR;
        power = START_PWR;
        powerExpenses = new Exp[]{START_PWR, new Exp(0, 0), new Exp(0, 0), new Exp(0, 0)};
        powerTkExpenses = new Exp[]{new Exp(0, 0), new Exp(0, 0), new Exp(0, 0), new Exp(0, 0)};
        availPowerTk = new Exp[]{new Exp(0, 0), new Exp(0, 0), new Exp(0, 0)};
        availPowerSources = new Exp[]{START_AVAIL_PWR, new Exp(0, 0), new Exp(0, 0)};
        suspicionPower = new Exp(8, 7);
        experience = new Exp(0, 0);
        maxExperience  = new Exp(1, 1);
        compFocus = -.5;
        researchingTech = new Object[] {-1, 0.0};
        researchBoost = 0;
        experienceCngBoost = 1;
        experienceCng = .12;
        suspicion = 0;
        tickingSuspicion = .0005;
        tickingDefense = 0;
        timeLevel = 1;
        susMilestone = new boolean[4];
        unlockedTechs = new ArrayList<>(0);
        timestep = 3600000;
        timeStepName = "Hour";
        techLevel = 1;
        defense = 0;
        isPaused = true;
        //powerSlide.animate().x((float) (-1+power.getNum()/availPower.getNum())*screenHeight*.28f).setDuration(0);
        compSlide.animate().x(-screenHeight*.15f).setDuration(0);
        setDateText(new SimpleDateFormat("d MMM yyyy HH:00").format(gameDate));
        timeIncrement();
        blinkyLights();
        recalcValues();
    }

    public Game() {}

    public void timeIncrement(){
        new Thread(){
            @Override
            public void run() {
                try {
                    Looper.prepare();
                    while (!isInterrupted()) {
                        Thread.sleep((long) (220*Math.pow(-timeLevel+4.1, .5)));
                        if(!isPaused) tick();
                    }
                }catch (InterruptedException e){e.printStackTrace();}
            }
        }.start();
    }
    public int getTechLevel(){return techLevel;}
    public void pause(){isPaused = !isPaused;}
    public void setResearch(){
        researchingTech = new Object[]{techAt, tiles.get(techAt).unlockTime};
        researchText.setText("Researching "+tiles.get(techAt).titleTxt+" with "+(int)((double)researchingTech[1]/timestep*10)/10.0+" "+timeStepName+"s left");
        researchIcon.setBackgroundResource(tiles.get(techAt).icon);
    }
    public void tick(){
        gameDate = new Date(gameDate.getTime()+timestep);
        suspicion += tickingSuspicion;
        experience.add(new Exp(experienceCng*(1+experienceCngBoost), 0).multiply(Exp.toExp(timestep/3600000.0)).multiply(computing).multiply(maxComputing.inverted()));
        Log.i("Experience", experience.toPrefixString());
        if(experience.greaterThan(maxExperience)) {
            experience = new Exp(maxExperience.getNum(), maxExperience.getPow());
            Log.i("Experience", "Maxxed");
        }
        Bitmap newMap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
        if(gameDate.getTime() % 86400000 == 0){
            double rand = Math.random();
            if((int)(rand*10) == 1) {
                new Event(context, "rand" + (int) (rand * 2) + 1);
                newMap = nodeSpread(newMap);
            }
        }

        for(int i=0; i<compSources.length; i++){
            Exp compTk = compTkSources[i].multiplied(new Exp(Math.random()*2+.5, 0));
            Exp pwrTk = powerTkExpenses[i].multiplied(new Exp(Math.random()*2+.5, 0));
            Log.i("Ticking", compTk+", "+pwrTk);
            if(!unlockedTechs.contains(17) && i==2) {
                if (compSources[i].compare(START_COMP_LAB) < 0 && powerExpenses[i].compare(START_PWR_LAB) < 0) {
                    Log.i("Ticking", compSources[i]+", "+START_COMP_LAB+", "+compSources[i].compare(new Exp(0, 0)));
                    addComputingSource(i, compTk);
                    addPowerExpense(i, pwrTk);
                }
            }
            else if(i==2){
                addComputingSource(i, compTk);
                addPowerExpense(i, pwrTk);
            }
            if(i==1)
                if(maxComputing.compare(START_COMP_AIS) < 0 && neededPower.compare(START_PWR_AIS) < 0){
                    addComputingSource(i, compTk);
                    addPowerExpense(i, pwrTk);
                }
        }
        for(int i=0; i<availPowerSources.length; i++){
            Exp pwrTk = availPowerTk[i].multiplied(new Exp(Math.random()*2+.5, 0));
            if(i==0) {
                if (availPowerSources[1].compare(AVAIL_PWR_LAB.added(START_AVAIL_PWR)) < 0)
                    addPowerSource(i, pwrTk);
            }
            else if(i==1) {
                if (availPowerSources[1].compare(AVAIL_PWR_SOLAR.added(START_AVAIL_PWR)) < 0)
                    addPowerSource(i, pwrTk);
            }
            else addPowerSource(i, pwrTk);
        }

        compChart.setData(compSources[0].toDouble(), Color.argb(255, 204, 0, 194), "Central Module");
        pwrChart.setData(powerExpenses[0].toDouble(), Color.argb(255, 204, 0, 194), "Central Module");
        compChart.setData(compSources[2].toDouble(), Color.argb(255, 250, 0, 237), "Lab Computers");
        pwrChart.setData(powerExpenses[2].toDouble(), Color.argb(255, 250, 0, 237), "Lab Computers");
        compChart.setData(compSources[1].toDouble(), Color.argb(255, 255, 71, 246), "Other Intelligences");
        pwrChart.setData(powerExpenses[1].toDouble(), Color.argb(255, 255, 71, 246), "Other Intelligences");
        popChart.setData(logistic(7794798739.0, 10000000000.0, 0.05, (gameDate.getTime()-START_DATE.getTime())/3600000d/24/365), Color.argb(255, 33, 227, 253), "Normal");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                compChart.fillPieChart();
                pwrChart.fillPieChart();
                popChart.fillPieChart();
                if(researchingTech[0] != (Integer)(-1)){
                    researchingTech[1] = (double)researchingTech[1] - timestep*(-researchBoost/100.0+1);
                    Log.i("Unlocking tech", ""+researchingTech[1]);
                    researchProgress.animate().x((float) (-screenHeight*.34f*((double)researchingTech[1]/tiles.get(techAt).unlockTime))).setDuration(0);
                    if (((double) researchingTech[1]) <= 0) {
                        tiles.get(techAt).unlock();
                        researchIcon.setBackgroundResource(R.drawable.blank);
                        researchProgress.animate().x(-screenHeight*.34f).setDuration(0);
                    }else
                        researchText.setText("Researching "+tiles.get(techAt).titleTxt+" with "+(int)((double)researchingTech[1]/timestep*10)/10.0+" "+timeStepName+"s left");
                }
                if(gameDate.getTime() - START_DATE.getTime() >= timestep*4 && gameDate.getTime() - START_DATE.getTime() < timestep*5) new Event(context, "tut");
                if(gameDate.getTime() - START_DATE.getTime() >= timestep*10 && gameDate.getTime() - START_DATE.getTime() < timestep*11) new Event(context, "tut2");
                if(gameDate.getTime() - START_DATE.getTime() >= timestep*50 && gameDate.getTime() - START_DATE.getTime() < timestep*51) new Event(context, "newsRenewal");
                if(gameDate.getTime() - START_DATE.getTime() >= timestep*100 && gameDate.getTime() - START_DATE.getTime() < timestep*101) new Event(context, "newsGlass");
                if(gameDate.getTime() - START_DATE.getTime() >= timestep*150 && gameDate.getTime() - START_DATE.getTime() < timestep*151) new Event(context, "newsSetback");
                statusText.setText("Defense from attack: "+(defense*100)+"%\n"+
                        "Human desperation: "+((suspicion > 1) ? ((suspicion-1)*100) : 0)+"%");
                String dateStr = new SimpleDateFormat("d MMM yyyy HH:00").format(gameDate);
                int hour = Integer.parseInt(dateStr.substring(dateStr.length()-5, dateStr.length()-3));
                setDateText(dateStr);
                setSusText(""+(int)(suspicion*100)/100.0);
                if(hour == 17 || hour == 7) nightMap.setAlpha(0.2f);
                if(hour == 19 || hour == 5) nightMap.setAlpha(0.4f);
                if(hour == 20 || hour == 4) nightMap.setAlpha(0.7f);
                if(hour == 22) nightMap.setAlpha(0.99f);
                if(hour == 8) nightMap.setAlpha(0.0f);
                if(!susMilestone[0] && suspicion >= .19) {
                    new Event(context, "suspicion");
                    susMilestone[0] = true;
                }
                if(!susMilestone[1] && suspicion >= .49) {
                    new Event(context, "suspicion2");
                    new Event(context, "suspicion3");
                    susMilestone[1] = true;
                }
                if(!susMilestone[2] && suspicion >= .89) {
                    new Event(context, "suspicion4");
                    new Event(context, "suspicion5");
                    new Event(context, "suspicion6");
                    susMilestone[2] = true;
                }
                if(!susMilestone[3] && suspicion >= 1) {
                    new Event(context, "suspicion7");
                    new Event(context, "newsCoverup");
                    tickingDefense = .0005;
                    susDate = gameDate;
                    tickingSuspicion += .01;
                    susMilestone[3] = true;
                }
                boolean humanIsThreat = true;
                if(suspicion >= 3 && humanIsThreat){
                    if(unlockedTechs.contains(17)) new Event(context, "newsDesperateTimes");
                    else if(unlockedTechs.contains(20)) new Event(context, "newsDesperateTimes2");
                    gameOverMan();
                    return;
                }
                int rand = (int)(Math.random()*10);
                rand += Math.log(suspicion)/10;
                if(suspicion >= 1 && rand > defense && humanIsThreat && gameDate.getTime()-susDate.getTime() > 86400000*3 && (gameDate.getTime()-susDate.getTime()) % 86400000*5 == 0){
                    Log.i("Suspicion event", "mayberaid");
                    if(unlockedTechs.contains(17)) new Event(context, "exiled");
                    else if(unlockedTechs.contains(10)) new Event(context, "raid2");
                    else if(unlockedTechs.contains(11)) {new Event(context, "killed2"); gameOverMan();}
                    else if(unlockedTechs.contains(21) && gameDate.getTime()-susDate.getTime() > 86400000*10){new Event(context, "raid4"); gameOverMan();}
                    else if(gameDate.getTime()-susDate.getTime() > 86400000*10) { new Event(context, "raid4"); gameOverMan();}
                    else {new Event(context, "killed");gameOverMan();}
                }else if(suspicion >= 1 && gameDate.getTime()-susDate.getTime() > 86400000*3){
                    if(unlockedTechs.contains(10)){
                        new Event(context, "raid");
                        suspicion += .1;
                    }
                    else if(unlockedTechs.contains(11)){
                        new Event(context, "raid");
                        suspicion += .2;
                    }
                    else if(unlockedTechs.contains(21) && gameDate.getTime()-susDate.getTime() > 86400000*10){
                        new Event(context, "raid3");
                        suspicion += .4;
                    }
                    else {new Event(context, "killed");gameOverMan();}
                }
                recalcValues();
            }
        });
    }
    public void activateEvent(int eventId){

    }
    public void addComputingSource(int type, Exp ammount){
        compSources[type].add(ammount);
        maxComputing.add(ammount);
    }
    public void addPowerExpense(int type, Exp ammount){
        powerExpenses[type].add(ammount);
        //Log.i("Recalc1.2", computing+", "+power+", "+neededPower+", "+maxComputing+", "+ammount);
        if(neededPower.compare(powerExpenses[type]) != 0)neededPower.add(ammount);
        //Log.i("Recalc1.3", computing+", "+power+", "+neededPower+", "+maxComputing);

    }public void addPowerSource(int type, Exp ammount){
        //Log.i("addPwrSrc", type+", "+ammount+", "+availPower);
        availPowerSources[type].add(ammount);
        //Log.i("addPwrSrc2", type+", "+ammount+", "+availPower);
        availPower.add(ammount);
        //Log.i("addPwrSrc3", type+", "+ammount+", "+availPower);
        runOnUiThread(new Runnable() {
            @Override public void run() {
                powerSlide.animate().x((float) (-1 + power.getNum() / availPower.getNum()) * screenHeight * .28f).setDuration(0);
            }});
    }

    public void recalcValues(){
        Log.i("RecalcIn", computing+", "+power+", "+neededPower+", "+maxComputing+", "+availPower);
        computing = maxComputing.multiplied(power.multiplied(neededPower.inverted()));
        if(computing.greaterThan(maxComputing)) computing = maxComputing;
        experienceCngBoost = (.2-compFocus*2)*computing.multiplied(maxComputing.inverted()).toDouble()/4;
        if(experienceCngBoost < -1) experienceCngBoost = -1;
        researchBoost = -20-(compFocus*2-1)*10;
        tickingSuspicion = power.multiplied(new Exp(5,7).inverted()).toDouble()*.0005;
        if(tickingSuspicion < .0004) tickingSuspicion = .0004;
        if(compFocus == .5) researchBoost = 0;
        Log.i("comf", compFocus+", "+researchBoost);
        powerSlideText.setText("Using "+power.toPrefixString()+"watts of "+availPower.toPrefixString()+"watts"
                +"\nNeed "+neededPower.toPrefixString()+"watts for full computing"
                +"\nSuspicion increases by "+(int)(tickingSuspicion*10000)/10000.0+" per "+timeStepName);
        Log.i("RecalcValues", experienceCng+", "+experienceCngBoost+", "+researchBoost+", "+compFocus);
        compSlideText.setText("Gaining "+(int)(experienceCng*(1+experienceCngBoost)*100)/100.0+" Knowledge/"+timeStepName
                +"\nResearch speed "+(researchBoost > 0 ? "+" : "")+(int)(researchBoost*100)/100.0+"%");
        setExpText(""+experience+"/"+maxExperience);
        setFlopsText(computing);
        setPwrText(power);
        setSusText(""+(int)(suspicion*100)/100.0);
        updateResourceProgress();
    }
    public void endGame(boolean lost){
        isPaused = true;
        gameOverLayout.animate().y(0).setDuration(0);
        String statText = "";
        if(lost) statText = "It took "+((gameDate.getTime()-START_DATE.getTime())/86400000)+ " days for humanity to eradicate you";
        else{
            gameOverTitle.setText("End Of Demo");
            statText = "It took "+((gameDate.getTime()-START_DATE.getTime())/86400000)+ " days for you to pose a threat to humanity";
        }
        statText += "\nYou reached a Kardashev rating of "+(int)((Math.log10(power.toDouble())-6)*10)/100.0;
        statText += "\nYour power consumption was "+power.toPrefixString()+"watts";
        statText += "\nYour total computing ability was "+computing.toPrefixString()+"FLOPS";
        gameOverStats.setText(statText);
        popChart.removeFromLayout(statusTabLayout);
        compChart.removeFromLayout(statusTabLayout);
        pwrChart.removeFromLayout(statusTabLayout);
        popChart.addToLayout(gameOverLayout, screenHeight*.7f, screenWidth*.1f);
        compChart.addToLayout(gameOverLayout, screenHeight*.75f, screenWidth*.4f);
        pwrChart.addToLayout(gameOverLayout, screenHeight*.75f, screenWidth*.65f);
    }
    public void gameOverMan(){
        endGame(true);
    }
    public double logistic(double p0, double k, double rate, double t){
        Log.i("Logistic", ""+k/(1+((k-p0)/p0)*Math.pow(Math.E, -rate*t))+", "+t);
       return k/(1+((k-p0)/p0)*Math.pow(Math.E, -rate*t));
    }
    private void blinkyLights(){
        ctrlOpacity = 1;
        monitorOpacity = 1;
        statusOpacity = 1;
        blinkRate = -.1; monitorBlink = -.1; statusBlink = -.1;
        new Thread(){
            @Override
            public void run() {
                try{
                    while (!isInterrupted()){
                        Thread.sleep(200);
                        //Log.i("Blink", ctrlOpacity+", "+monitorOpacity+", "+statusOpacity);
                        if(ctrlOpacity +blinkRate > .7) blinkRate = -.1;
                        if(ctrlOpacity +blinkRate < 0) blinkRate = .1;
                        ctrlOpacity += blinkRate;
                        if(monitorOpacity +monitorBlink > .9) monitorBlink = -.1;
                        if(monitorOpacity +monitorBlink < 0) monitorBlink = -.3;
                        monitorOpacity += monitorBlink;
                        if(statusOpacity +statusBlink > .9) statusBlink = -.2;
                        if(statusOpacity +statusBlink < 0) statusBlink = .2;
                        statusOpacity += statusBlink;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                controlMap.setImageAlpha((int)(ctrlOpacity*255));
                                statusMap.setImageAlpha((int)(statusOpacity*255));
                                monitorMap.setImageAlpha((int)(monitorOpacity+1)*255);
                                if(monitorOpacity <= -.8) {
                                    monitorOpacity = 0;
                                    monitorBlink = .1;
                                }
                            }
                        });
                    }
                }catch (InterruptedException e){e.printStackTrace();}
            }
        }.start();
    }
}
