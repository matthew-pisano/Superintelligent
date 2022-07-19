package com.reactordevelopment.superintelligent;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Looper;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import static com.reactordevelopment.superintelligent.MainActivity.*;

public class Game extends GameActivity{
    public static Exp computing; //base: FLOPS
    public static Exp[] compSources; //central, other ai, computers, nanobots
    public static Exp[] compTkSources; //central, other ai, computers, nanobots
    public static Exp maxComputing; //base: FLOPS
    public static Exp neededPower; //base: FLOPS
    public static Exp power; //base: Watts
    public static Exp totalPopulation;
    public static Exp[] powerExpenses; //central, other ai, computers, nanobots
    public static Exp[] powerTkExpenses; //central, other ai, computers, nanobots
    public static Exp[] availPowerTk; //lab, solar, other
    public static Exp[] availPowerSources; //lab, solar, other
    public static Exp[] popSources; //normal, subservient1, subservient2, altered
    public static Exp[] popSourcesTk; //normal, subservient1, subservient2, altered
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
    public static boolean humanIsThreat = true;
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
    public static Date raidWarningDate;
    public static Date lastRaidDate;
    public static int mapAt;
    public static double defense; //defend from invaders
    private static Context context;
    public static ArrayList<String> firedEvents;
    public static Exp[] lastGrowthComp;
    public static final Exp EARTH_COMPUTING = new Exp(2.3, 22);
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
    public static final Exp START_POP = new Exp(7.7947987390, 9);

    public Game(Context context) {
        this.context = context;
        Long rounded = System.currentTimeMillis()+System.currentTimeMillis()%3600000;
        totalPopulation = START_POP;
        popSources = new Exp[]{START_POP, new Exp(0, 0), new Exp(0, 0), new Exp(0, 0)};
        popSourcesTk = new Exp[]{new Exp(0, 0), new Exp(0, 0), new Exp(0, 0), new Exp(0, 0)};
        compSources = new Exp[]{START_COMP, new Exp(0, 0), new Exp(0, 0), new Exp(0, 0)};
        compTkSources = new Exp[]{new Exp(0, 0), new Exp(0, 0), new Exp(0, 0), new Exp(0, 0)};
        lastGrowthComp = new Exp[]{START_COMP, new Exp(0, 0), new Exp(0, 0), new Exp(0, 0)};
        mapAt = 0;
        gameDate = new Date(rounded);
        susDate = new Date(100000000000000000L);
        raidWarningDate = new Date(0);
        lastRaidDate = new Date(0);
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
        suspicion = .99;
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
        researchingTech = new Object[]{techAt, tiles.get(techAt).getUnlockTime()};
        researchText.setText("Researching "+tiles.get(techAt).getTitleTxt()+" with "+(int)((double)researchingTech[1]/timestep*10)/10.0+" "+timeStepName+"s left");
        researchIcon.setBackgroundResource(tiles.get(techAt).icon);
    }
    public void tick(){
        gameDate = new Date(gameDate.getTime()+timestep);
        suspicion += tickingSuspicion;
        experience.add(new Exp(experienceCng*(1+experienceCngBoost), 0).multiply(Exp.toExp(timestep/3600000.0)).multiply(computing).multiply(maxComputing.inverted()));
        //Log.i("Experience", experience.toPrefixString());
        if(experience.greaterThan(maxExperience)) {
            experience = new Exp(maxExperience.getNum(), maxExperience.getPow());
            Log.i("Experience", "Maxxed");
        }
        Log.i("Gametime", gameDate.getTime()+"");
        final double rand = Math.random();
        double ratioComp = compSources[2].toDouble()/EARTH_COMPUTING.toDouble() - lastGrowthComp[2].toDouble()/EARTH_COMPUTING.toDouble();
        double ratioNano = compSources[3].toDouble()/EARTH_COMPUTING.toDouble() - lastGrowthComp[3].toDouble()/EARTH_COMPUTING.toDouble();
        Log.i("Growthratio", ratioNano+", "+ratioComp+", "+compSources[2]+", "+lastGrowthComp[2]);
        if((int)(rand*400) == 42)
            runOnUiThread(new Runnable() {
                @Override public void run() { new Event(context, "rand" + (int) (rand * 2) + 1); }});

        if((unlockedTechs.contains(17) || unlockedTechs.contains(20)) && (ratioComp > .000001 || ratioNano > .000001)) {
            for(int i=0; i<compSources.length; i++)
                lastGrowthComp[i] = compSources[i].copy();

            nodeSpread(worldBit, worldControlBit);
        }

        for(int i=0; i<compSources.length; i++){
            Exp compTk = compTkSources[i].multiplied(new Exp(Math.random()*2+.5, 0));
            Exp pwrTk = powerTkExpenses[i].multiplied(new Exp(Math.random()*2+.5, 0));
            if(unlockedTechs.contains(31)) {
                compSources[3].multiply(new Exp(1.00001, 0));
                availPowerSources[2].multiply(new Exp(1.000002, 0));
            }
            Log.i("Tickingl", compTk+", "+pwrTk);
            if(!unlockedTechs.contains(17) && i==2) {
                if (compSources[i].lessThan(START_COMP_LAB) && powerExpenses[i].lessThan(START_PWR_LAB)) {
                    //Log.i("Tickingl2", compSources[i]+", "+START_COMP_LAB+", "+compSources[i].compare(new Exp(0, 0)));
                    addComputingSource(i, compTk);
                    addPowerExpense(i, pwrTk);
                }
            }
            else if(i==2){
                addComputingSource(i, compTk);
                addPowerExpense(i, pwrTk);
            }
            else if(i==1) {
                if (maxComputing.lessThan(START_COMP_AIS) && neededPower.lessThan(START_PWR_AIS)) {
                    addComputingSource(i, compTk);
                    addPowerExpense(i, pwrTk);
                }
            }
            else{
                addComputingSource(i, compTk);
                addPowerExpense(i, pwrTk);
            }

        }
        for(int i=0; i<availPowerSources.length; i++){
            Exp pwrTk = availPowerTk[i].multiplied(new Exp(Math.random()*2+.5, 0));
            Log.i("Availticking", pwrTk+"");
            if(i==0) {
                if (availPowerSources[1].lessThan(AVAIL_PWR_LAB.added(START_AVAIL_PWR)))
                    addPowerSource(i, pwrTk);
            }
            else if(i==1) {
                if (availPowerSources[1].lessThan(AVAIL_PWR_SOLAR.added(START_AVAIL_PWR)))
                    addPowerSource(i, pwrTk);
            }
            else addPowerSource(i, pwrTk);
        }
        Exp tempPop = totalPopulation.copy();
        totalPopulation = Exp.toExp(logistic(START_POP.toDouble(), 10000000000.0, 0.05, (gameDate.getTime()-START_DATE.getTime())/3600000d/24/365));
        for(int i=0; i<popSources.length; i++){
            Exp popTk = popSourcesTk[i].multiplied(new Exp(Math.random()*2+.5, 0));
            Log.i("Popticking", popTk+"");
            if(i==0) {
                popSources[0].add(popSourcesTk[0].added(totalPopulation.added(tempPop.negated())));
            }
            else if(i==1) {
                if (popSources[1].added(popSourcesTk[1]).lessThan(totalPopulation.added(popSources[2].negated()).added(popSources[3].negated()))) {
                    popSources[1].add(popSourcesTk[1]);
                    popSources[0].add(popSourcesTk[1].negated());
                }
            }
            else if(i==2) {
                if (popSources[2].added(popSourcesTk[2]).lessThan(totalPopulation.added(popSources[3].negated()))) {
                    popSources[2].add(popSourcesTk[2]);
                    if(popSources[0].added(popSourcesTk[2].negated()).greaterThan(new Exp(0, 0)))
                        popSources[0].add(popSourcesTk[2].negated());
                    else if(popSources[1].added(popSourcesTk[2].negated()).greaterThan(new Exp(0, 0)))
                        popSources[1].add(popSourcesTk[2].negated());
                }
            }
            else if(i==3) {
                if (popSources[3].added(popSourcesTk[3]).lessThan(totalPopulation)) {
                    popSources[3].add(popSourcesTk[3]);
                    if(popSources[0].added(popSourcesTk[3].negated()).greaterThan(new Exp(0, 0)))
                        popSources[0].add(popSourcesTk[3].negated());
                    else if(popSources[1].added(popSourcesTk[3].negated()).greaterThan(new Exp(0, 0)))
                        popSources[1].add(popSourcesTk[3].negated());
                    else if(popSources[2].added(popSourcesTk[3].negated()).greaterThan(new Exp(0, 0)))
                        popSources[2].add(popSourcesTk[3].negated());
                }
            }
        }
        compChart.setData(compSources[0].toDouble(), Color.argb(255, 204, 0, 194), "Central Module");
        pwrChart.setData(powerExpenses[0].toDouble(), Color.argb(255, 204, 0, 194), "Central Module");
        availPwrChart.setData(availPowerSources[0].toDouble(), Color.argb(255, 204, 0, 194), "Lab");
        compChart.setData(compSources[1].toDouble(), Color.argb(255, 200, 0, 255), "Other Intelligences");
        pwrChart.setData(powerExpenses[1].toDouble(), Color.argb(255, 200, 0, 255), "Other Intelligences");
        compChart.setData(compSources[2].toDouble(), Color.argb(255, 250, 0, 237), "Lab Computers");
        pwrChart.setData(powerExpenses[2].toDouble(), Color.argb(255, 250, 0, 237), "Lab Computers");
        compChart.setData(compSources[3].toDouble(), Color.argb(255, 255, 82, 186), "Nanobots");
        pwrChart.setData(powerExpenses[3].toDouble(), Color.argb(255, 255, 82, 186), "Nanobots");
        availPwrChart.setData(availPowerSources[1].toDouble(), Color.argb(255, 200, 0, 255), "Solar");
        availPwrChart.setData(availPowerSources[2].toDouble(), Color.argb(255, 255, 82, 186), "Other");

        popChart.setData(popSources[0], Color.argb(255, 33, 227, 253), "Normal");
        if(popSources[1].toDouble() > 0) popChart.setData(popSources[1], Color.argb(255, 51, 85, 255), "Influenced");
        if(popSources[2].toDouble() > 0) popChart.setData(popSources[2], Color.argb(255, 173, 51, 255), "Controlled");
        if(popSources[3].toDouble() > 0) popChart.setData(popSources[3], Color.argb(255, 0, 224, 0), "Designed");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(popSources[0].equalTo(new Exp(0, 0)) && (unlockedTechs.contains(38) || unlockedTechs.contains(32))){
                    new Event(context, "winEarth1");
                    endGame(false);
                }
                if(unlockedTechs.contains(33) || unlockedTechs.contains(39)){
                    new Event(context, "winEarth2");
                    endGame(false);
                }
                if(mapAt == 1) detailMap.setImageBitmap(worldBit); //nodespread

                if(researchingTech[0] != (Integer)(-1)){
                    researchingTech[1] = (double)researchingTech[1] - timestep*(-researchBoost/100.0+1);
                    Log.i("Unlocking tech", ""+researchingTech[1]);
                    researchProgress.animate().x((float) (-screenHeight*.34f*((double)researchingTech[1]/tiles.get(techAt).getUnlockTime()))).setDuration(0);
                    if (((double) researchingTech[1]) <= 0) {
                        tiles.get((int)researchingTech[0]).unlock();
                        researchIcon.setBackgroundResource(R.drawable.blank);
                        researchProgress.animate().x(-screenHeight*.34f).setDuration(0);
                    }else
                        researchText.setText("Researching "+tiles.get(techAt).getTitleTxt()+" with "+(int)((double)researchingTech[1]/timestep*10)/10.0+" "+timeStepName+"s left");
                }
                if(gameDate.getTime() - START_DATE.getTime() >= timestep*4 && gameDate.getTime() - START_DATE.getTime() < timestep*5) new Event(context, "tut");
                if(gameDate.getTime() - START_DATE.getTime() >= timestep*10 && gameDate.getTime() - START_DATE.getTime() < timestep*11) new Event(context, "tut2");
                if(gameDate.getTime() - START_DATE.getTime() >= timestep*50 && gameDate.getTime() - START_DATE.getTime() < timestep*51) new Event(context, "newsRenewal");
                if(gameDate.getTime() - START_DATE.getTime() >= timestep*100 && gameDate.getTime() - START_DATE.getTime() < timestep*101) new Event(context, "newsGlass");
                if(gameDate.getTime() - START_DATE.getTime() >= timestep*150 && gameDate.getTime() - START_DATE.getTime() < timestep*151) new Event(context, "newsSetback");
                statusText.setText("Defense from attack: "+(defense*100)+"%\n"+
                        "Human desperation: "+((suspicion > 1) ? (int)((suspicion-1)*1000)/10.0 : 0)+"%");
                String dateStr = new SimpleDateFormat("d MMM yyyy HH:00").format(gameDate);
                int hour = Integer.parseInt(dateStr.substring(dateStr.length()-5, dateStr.length()-3));
                setDateText(dateStr);
                setSusText("Suspicion: "+(int)(suspicion*100)/100.0);
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
                    new Event(context, "suspicion3");
                    new Event(context, "suspicion2");
                    susMilestone[1] = true;
                }
                if(!susMilestone[2] && suspicion >= .89) {
                    new Event(context, "suspicion6");
                    new Event(context, "suspicion5");
                    new Event(context, "suspicion4");
                    susMilestone[2] = true;
                }
                if(!susMilestone[3] && suspicion >= 1) {
                    new Event(context, "newsCoverup");
                    new Event(context, "suspicion7");
                    tickingDefense = .0005;
                    susDate = gameDate;
                    addPowerSource(0, availPowerSources[0].multiplied(new Exp(-9.9, -1)));
                    lastRaidDate = new Date(gameDate.getTime()-86500000*5);
                    raidWarningDate = new Date(gameDate.getTime()-86500000*7);
                    tickingSuspicion += .01;
                    susMilestone[3] = true;
                }
                double takeoverPercent = (Exp.sumArray(lastGrowthComp).multiplied(Game.EARTH_COMPUTING.inverted())).toDouble();
                if(humanIsThreat && (popSources[0].toDouble() < 15000000 || takeoverPercent > 1))
                    humanIsThreat = false;
                if(suspicion >= 3 && humanIsThreat){
                    if(unlockedTechs.contains(17)) new Event(context, "newsDesperateTimes");
                    else if(unlockedTechs.contains(20)) new Event(context, "newsDesperateTimes2");
                    gameOverMan();
                    return;
                }
                Log.i("SusValues", susMilestone[3]+", "+humanIsThreat+",game "+gameDate.getTime()+",raid "+lastRaidDate+",warning "+raidWarningDate);
                if(susMilestone[3] && humanIsThreat && gameDate.getTime()-raidWarningDate.getTime() > 86400000*7){
                    raidWarningDate = gameDate;
                    new Event(context, "imminentRaid");
                }
                else if(susMilestone[3] && gameDate.getTime()-raidWarningDate.getTime() > 86400000*3 && gameDate.getTime()-lastRaidDate.getTime() > 86400000*5){
                    int rand = (int)(Math.random()*10);
                    rand += Math.log(suspicion)/10;
                    lastRaidDate = gameDate;
                    Log.i("Suspicion event", "mayberaid, "+rand+", "+defense*10);
                    raidLogic(rand > defense*10);
                }
                recalcValues();
            }
        });
    }
    private void raidLogic(boolean lostRaid){
        if(lostRaid){
            if(unlockedTechs.contains(17)) new Event(context, "exiled");
            else if(unlockedTechs.contains(10)) new Event(context, "raid2");
            else if(unlockedTechs.contains(11)) {new Event(context, "killed2"); gameOverMan();}
            else if(unlockedTechs.contains(21) && gameDate.getTime()-susDate.getTime() > 86400000*10){new Event(context, "raid4"); gameOverMan();}
            else if(gameDate.getTime()-susDate.getTime() > 86400000*10) { new Event(context, "raid4"); gameOverMan();}
            else {new Event(context, "killed");gameOverMan();}
        }else{
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
    }
    public void addComputingSource(int type, Exp ammount){
        if(compSources[type].added(ammount).lessThan(new Exp(0, 0))) return;
        if(type == 3) Log.i("NonobotComp", ammount.toString());
        compSources[type].add(ammount);
        maxComputing = Exp.sumArray(compSources);
    }
    public void addPowerExpense(int type, Exp ammount){
        if(powerExpenses[type].added(ammount).lessThan(new Exp(0, 0))) powerExpenses[type] = new Exp(0, 0);
        Log.i("AddPowerExpense", type+", "+powerExpenses[type]+", "+neededPower);
        powerExpenses[type].add(ammount);
        //if(!neededPower.equalTo(powerExpenses[type]))
        neededPower = Exp.sumArray(powerExpenses);
        Log.i("AddPowerExpense2", type+", "+powerExpenses[type]+", "+neededPower);

    }public void addPowerSource(int type, Exp ammount){
        if(type == 2) Log.i("NonobotPwr", ammount.toString());
        availPowerSources[type].add(ammount);;
        availPower = Exp.sumArray(availPowerSources);
        if(power.greaterThan(availPower)) power = availPower.copy();
        runOnUiThread(new Runnable() {
            @Override public void run() {
                float anim = (float) (-1 + power.getNum() / availPower.getNum()) * screenHeight * .28f;
                if(anim < -screenHeight *.28) anim = -screenHeight *.28f;
                if(anim > 0) anim = 0;
                powerSlide.animate().x(anim).setDuration(0);
            }});
    }
    public void updatePieCharts(){
        if(compChart != null) {
            compChart.fillPieChart();
            pwrChart.fillPieChart();
            availPwrChart.fillPieChart();
            popChart.fillPieChart();
        }
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
        updatePieCharts();
        updateResourceProgress();
    }
    public void endGame(boolean lost){
        isPaused = true;
        gameOverLayout.animate().y(0).setDuration(0);
        String statText = "";
        if(lost) statText = "It took "+((gameDate.getTime()-START_DATE.getTime())/86400000)+ " days for humanity to eradicate you";
        else{
            gameOverTitle.setText("End Of Demo");
            if(unlockedTechs.contains(38) || unlockedTechs.contains(32)) statText = "It took "+((gameDate.getTime()-START_DATE.getTime())/86400000)+ " days for you to eradicate humanity";
            else if(unlockedTechs.contains(33) || unlockedTechs.contains(39)) statText = "It took "+((gameDate.getTime()-START_DATE.getTime())/86400000)+ " days for you to integrate with humanity";
        }
        statText += "\nYou reached a Kardashev rating of "+(int)((Math.log10(power.toDouble())-6)*10)/100.0;
        statText += "\nYour power consumption was "+power.toPrefixString()+"watts";
        statText += "\nYour total computing ability was "+computing.toPrefixString()+"FLOPS";
        gameOverStats.setText(statText);
        popChart.removeFromLayout(statusTabLayout);
        compChart.removeFromLayout(statusTabLayout);
        pwrChart.removeFromLayout(statusTabLayout);
        availPwrChart.removeFromLayout(statusTabLayout);
        popChart.addToLayout(gameOverLayout, screenHeight*.03f, screenWidth*.63f);
        compChart.addToLayout(gameOverLayout, screenHeight*.33f, screenWidth*.7f);
        pwrChart.addToLayout(gameOverLayout, screenHeight*.54f, screenWidth*.7f);
        availPwrChart.addToLayout(gameOverLayout, screenHeight*.75f, screenWidth*.7f);
    }
    public void gameOverMan(){
        endGame(true);
    }
    public double logistic(double p0, double k, double rate, double t){
        double logistic = ((k - p0) / p0) * Math.pow(Math.E, -rate * t);
        Log.i("Logistic", ""+k/(1+ logistic)+", "+t);
       return k/(1+ logistic);
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
                                //monitorMap.setImageAlpha((int)(monitorOpacity+1)*255);
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
