package com.reactordevelopment.superintelligent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import static com.reactordevelopment.superintelligent.MainActivity.*;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Looper;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.InputStream;
import java.util.ArrayList;

public class GameActivity extends AppCompatActivity {
    private static ImageButton toControls;
    private static ImageButton closeControls;
    private static ImageButton pause;
    private static ImageButton timeUp;
    private static ImageButton timeDown;
    private static ImageButton mapAt;
    private static ConstraintLayout mapSelect;
    private static ImageButton[] mapChoices;
    private static ImageView[] timeSteps;
    public static ImageView powerSlide;
    public static TextView powerSlideText;
    public static ImageView compSlide;
    public static TextView compSlideText;
    private static ImageButton unlockConfirm;
    public static boolean lockedConfirm;
    private static ImageView expProgress;
    public static ImageView researchProgress;
    private static ImageView pwrProgress;
    private static ImageView flopProgress;
    private static ImageView susProgress;
    private static ImageView takeoverProgress;
    private static ConstraintLayout takeoverLayout;
    public static TextView unlockText;
    public static TextView researchText;
    public static TextView expText;
    public static TextView pwrText;
    public static TextView flopText;
    public static TextView susText;
    public static TextView takeoverText;
    public static TextView dateText;
    public static TextView gameOverTitle;
    private ImageButton researchRound;
    public ImageButton statusTab;
    public static ConstraintLayout techTabLayout;
    public static ConstraintLayout statusTabLayout;
    private static RelativeLayout mapLayout;
    public static ConstraintLayout gameOverLayout;
    public static TextView gameOverStats;
    private static ImageView techTree;
    public static Bitmap tempTree;
    private static Game game;
    public static ArrayList<TechTile> tiles;
    private ConstraintLayout controlLayout;
    public static ConstraintLayout mainLayout;
    public static RelativeLayout treeHolder;
    public static float dY;
    public static float dX;
    private static Point down = new Point(0,0);
    private static long downtime = 0;
    private static ArrayList<Point> activePoints;
    private static ArrayList<Point> allPoints;
    private ScaleGestureDetector mapScaleGestureDetector;
    private ScaleGestureDetector techScaleGestureDetector;
    public static ImageView blankMap;
    public static ImageView detailMap;
    public static ImageView nightMap;
    public static ImageView statusMap;
    //public static ImageView monitorMap;
    public static ImageView controlMap;
    public static ImageView researchIcon;
    public static TextView statusText;
    public static ImageButton exit;
    public static PieChart popChart;
    public static PieChart compChart;
    public static PieChart pwrChart;
    public static PieChart availPwrChart;
    public static final float MIN_SCALE = .9f;
    public static final float MAX_SCALE = 10f;
    public static float mapScaling = 1;
    public static float techScaling = 1;
    private boolean controlsOpen;
    private static boolean mapSelectOpen;
    public static String eventStr;
    public static String techStr;
    public static int techAt;
    //public static final int MAP_WIDTH = 1000;
    //public static final int MAP_HEIGHT = 800;
    public static final int MAP_WIDTH = 500;
    public static final int MAP_HEIGHT = 400;
    public static Bitmap worldBit;
    public static Bitmap detailBit;
    public static Bitmap darkBit;
    public static Bitmap blankBit;
    //public static Bitmap monitorBit;
    public static Bitmap statusBit;
    public static Bitmap controlBit;
    public static Bitmap worldControlBit;
    public static final Point[][] mapSections = {
            {new Point(37, 588), new Point(70, 113)},

            {new Point(110, 566), new Point(112, 135)},
            {new Point(221, 588), new Point(116, 113)},

            {new Point(222, 448), new Point(116, 140)},

            {new Point(600, 255), new Point(300, 395)},

            {new Point(0, 0), new Point(1000, 800)},

            {new Point(337, 448), new Point(82, 156)}};

    private static Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        Log.i("oncreate", "1");
        context = this;
        init();
        game = new Game(context);
        game.recalcValues();
        printMemory("6");
        postInit(); //the slider init is in here
        printMemory("7");
        /*

        /*for(int i=0; i<tiles.size(); i++){
            tiles.get(i).remove();
            tiles.remove(i);
            i--;
        }
        tiles = null;
        System.gc();*/
        printMemory("7.1");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onDestroy() {
        Log.i("Destroying", "destroy");
        Intent mStartActivity = new Intent(context, MainActivity.class);
        int mPendingIntentId = 123456;
        PendingIntent mPendingIntent = PendingIntent.getActivity(context, mPendingIntentId,    mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager mgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
        Runtime.getRuntime().exit(0);
        super.onDestroy();
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        if(!controlsOpen) mapScaleGestureDetector.onTouchEvent(motionEvent);
        else techScaleGestureDetector.onTouchEvent(motionEvent);
        return true;
    }
    @SuppressLint("ClickableViewAccessibility")
    public void init(){
        printMemory("1");
        worldBit = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.earthmap), MAP_WIDTH, MAP_HEIGHT, false);
        detailBit = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.detailmap), MAP_WIDTH, MAP_HEIGHT, false);
        darkBit = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.darker), MAP_WIDTH, MAP_HEIGHT, false);
        blankBit = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.blank), MAP_WIDTH, MAP_HEIGHT, false);
        //monitorBit = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.monitorglow), MAP_WIDTH, MAP_HEIGHT, false);
        statusBit = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.statusglow), MAP_WIDTH, MAP_HEIGHT, false);
        controlBit = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.controlglow), MAP_WIDTH, MAP_HEIGHT, false);
        worldControlBit = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.blank), MAP_WIDTH, MAP_HEIGHT, false);
        printMemory("EndBit");
        Log.i("init", "1");
        activePoints = new ArrayList<>(0);
        allPoints = new ArrayList<>(0);
        allPoints.add(new Point(200, 200));
        activePoints.add(new Point(65, 80));
        techAt = -1;
        controlsOpen = false;
        mapSelectOpen = false;
        lockedConfirm = false;
        //resource progress
        expProgress = findViewById(R.id.expProgress);
        pwrProgress = findViewById(R.id.pwrProgress);
        flopProgress = findViewById(R.id.flopProgress);
        susProgress = findViewById(R.id.susProgress);
        susProgress.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.susbar));
        susProgress.setScaleType(ImageView.ScaleType.FIT_XY);
        takeoverProgress = findViewById(R.id.takeoverProgress);
        takeoverLayout = findViewById(R.id.takeoverLayout);
        takeoverProgress.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.progbar));
        takeoverProgress.setScaleType(ImageView.ScaleType.FIT_XY);
        //map
        blankMap = findViewById(R.id.map);
        blankMap.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.schematic));
        detailMap = findViewById(R.id.detailMap);
        nightMap = findViewById(R.id.darkMap);
        nightMap.setImageBitmap(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.darker), MAP_WIDTH, MAP_HEIGHT, false));
        //monitorMap = findViewById(R.id.monitorMap);
        statusMap = findViewById(R.id.statusMap);
        controlMap = findViewById(R.id.controlMap);
        gridAllBitmaps(new Point[][]{mapSections[0]});
        printMemory("1.1");
        expProgress.animate().x(-screenHeight*.17f).setDuration(0);
        mapAt = findViewById(R.id.mapAt);
        mapSelect = findViewById(R.id.mapSelectLayout);
        mapChoices = new ImageButton[]{findViewById(R.id.homeMap), findViewById(R.id.worldMap)};
        tiles = new ArrayList<>(0);
        powerSlide = findViewById(R.id.powerSlider);
        powerSlideText = findViewById(R.id.powerSliderText);
        powerSlideText.setTextSize(TypedValue.COMPLEX_UNIT_IN, inchHeight*.02f);
        compSlide = findViewById(R.id.compSlider);
        compSlideText = findViewById(R.id.compSliderText);
        compSlideText.setTextSize(TypedValue.COMPLEX_UNIT_IN, inchHeight*.02f);
        pause = findViewById(R.id.pause);
        timeUp = findViewById(R.id.timeUp);
        timeDown = findViewById(R.id.timedown);
        timeSteps = new ImageView[]{findViewById(R.id.timeStep1), findViewById(R.id.timeStep2), findViewById(R.id.timeStep3), findViewById(R.id.timeStep4)};
        expText = findViewById(R.id.expText);
        expText.setTextSize(TypedValue.COMPLEX_UNIT_IN, inchHeight*.02f);
        pwrText = findViewById(R.id.pwrText);
        pwrText.setTextSize(TypedValue.COMPLEX_UNIT_IN, inchHeight*.02f);
        flopText = findViewById(R.id.flopText);
        flopText.setTextSize(TypedValue.COMPLEX_UNIT_IN, inchHeight*.02f);
        susText = findViewById(R.id.suspicionText);
        susText.setTextSize(TypedValue.COMPLEX_UNIT_IN, inchHeight*.027f);
        takeoverText = findViewById(R.id.takeoverText);
        takeoverText.setTextSize(TypedValue.COMPLEX_UNIT_IN, inchHeight*.027f);
        dateText = findViewById(R.id.dateText);
        dateText.setTextSize(TypedValue.COMPLEX_UNIT_IN, inchHeight*.021f);
        //techTree = findViewById(R.id.techTree);
        toControls = findViewById(R.id.toControls);
        closeControls = findViewById(R.id.closeControls);
        controlLayout = findViewById(R.id.controlLayout);
        mainLayout = findViewById(R.id.mainLayout);
        mapLayout = findViewById(R.id.mapLayout);
        treeHolder = findViewById(R.id.treeHolder);
        unlockConfirm = findViewById(R.id.unlockConfirm);
        unlockConfirm.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.accept));
        unlockText = findViewById(R.id.unlockText);
        unlockText.setMovementMethod(new ScrollingMovementMethod());
        //status
        researchIcon = findViewById(R.id.researchIcon);
        statusText = findViewById(R.id.statusText);
        statusText.setTextSize(TypedValue.COMPLEX_UNIT_IN, inchHeight*.023f);
        statusText.setMovementMethod(new ScrollingMovementMethod());
        researchText = findViewById(R.id.researchText);
        researchText.setText("Select A Research");
        researchText.setTextSize(TypedValue.COMPLEX_UNIT_IN, inchHeight*.023f);
        researchText.setTextColor(Color.BLACK);
        researchProgress = findViewById(R.id.researchProgress);
        researchProgress.animate().x(-screenHeight*.34f).setDuration(0);
        //game over
        gameOverTitle = findViewById(R.id.gameOverTitle);
        exit = findViewById(R.id.exit);
        gameOverLayout = findViewById(R.id.gameOverLayout);
        gameOverStats = findViewById(R.id.gameOverStats);
        gameOverStats.setTextSize(TypedValue.COMPLEX_UNIT_IN, inchHeight*.03f);
        techTree = new ImageView(context);
        techTree.setScaleType(ImageView.ScaleType.CENTER);
        tempTree = Bitmap.createBitmap(500, 1200, Bitmap.Config.ARGB_8888);
        techTree.setLayoutParams(new RelativeLayout.LayoutParams(500, 1200));
        treeHolder.addView(techTree);
        controlLayout.animate().y(screenWidth).setDuration(0);
        mapScaleGestureDetector = new ScaleGestureDetector(this, new MapScaleListener());
        techScaleGestureDetector = new ScaleGestureDetector(this, new TechScaleListener());
        researchRound = findViewById(R.id.researchRound);
        statusTab = findViewById(R.id.statusTab);
        techTabLayout = findViewById(R.id.techTabLayout);
        statusTabLayout = findViewById(R.id.statusTabLayout);
        printMemory("2");
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        researchRound.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                statusTabLayout.setVisibility(View.INVISIBLE);
                techTabLayout.setVisibility(View.VISIBLE);
                for(TechTile tile : tiles){
                    if(!tile.meetsReqs && !game.unlockedTechs.contains(tile.techId))
                        if(tile.meetsReqs()) {
                            Log.i("img", "make1");
                            tile.removeFromLayout();
                            tile.mkImage(R.drawable.canhastech);
                            tile.addToLayout(false);
                        }
                    if(tile.meetsReqs && !game.unlockedTechs.contains(tile.techId))
                        if(!tile.meetsReqs()) {
                            Log.i("img", "make2");
                            tile.removeFromLayout();
                            tile.mkImage(R.drawable.nohastech);
                            tile.addToLayout(false);
                        }
                }
            }});
        statusTab.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                techTabLayout.setVisibility(View.INVISIBLE);
                statusTabLayout.setVisibility(View.VISIBLE);
            }});
        for(int i=0; i<mapChoices.length; i++) {
            final int finalI = i;
            mapChoices[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    switchToMap(finalI);
                    mapSelectOpen = false;
                    mapSelect.animate().y(screenWidth).setDuration(500);
                }
            });
        }
        mapAt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("MapSelectOped", ""+mapSelectOpen);
                if(!mapSelectOpen) {
                    mapSelect.setVisibility(View.VISIBLE);
                    mapSelect.animate().y(screenWidth * .4f).setDuration(500);
                }else{
                    mapSelect.setVisibility(View.INVISIBLE);
                    mapSelect.animate().y(screenWidth).setDuration(500);
                }
                mapSelectOpen = !mapSelectOpen;
            }
        });
        unlockConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean cheat = false;
                if(cheat)
                    tiles.get(techAt).unlock();
                else
                    if(!lockedConfirm)
                        try{
                            if(tiles.get(techAt).meetsReqs())
                            tiles.get(techAt).startUnlock();
                        }catch (Exception e){e.printStackTrace();}
            }
        });
        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                game.pause();
                if(game.isPaused) pause.setBackgroundResource(R.drawable.play);
                else pause.setBackgroundResource(R.drawable.pause);
            }
        });
        timeDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(game.timeLevel > 1){
                    game.timeLevel --;
                }
                if(game.timeLevel == 3) timeSteps[3].setVisibility(View.INVISIBLE);
                if(game.timeLevel == 2) timeSteps[2].setVisibility(View.INVISIBLE);
                if(game.timeLevel == 1) timeSteps[1].setVisibility(View.INVISIBLE);
            }
        });
        timeUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(game.timeLevel < 4){
                    game.timeLevel ++;
                }
                if(game.timeLevel == 4) timeSteps[3].setVisibility(View.VISIBLE);
                if(game.timeLevel == 3) timeSteps[2].setVisibility(View.VISIBLE);
                if(game.timeLevel == 2) timeSteps[1].setVisibility(View.VISIBLE);
            }
        });
        toControls.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                Log.i("cohe", "1");
                if(!controlsOpen) {
                    controlLayout.animate().y(0).setDuration(500);
                    controlsOpen = true;
                }
            }});
        closeControls.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                if(controlsOpen) {
                    controlLayout.animate().y(screenWidth).setDuration(0);
                    techTabLayout.setVisibility(View.INVISIBLE);
                    statusTabLayout.setVisibility(View.VISIBLE);
                    controlsOpen = false;
                }
            }});
        printMemory("3");
        byte[] eventFile = new byte[0];
        byte[] techFile = new byte[0];
        try {
            InputStream stream = getAssets().open("events.txt");
            int size = stream.available();
            eventFile = new byte[size];
            stream.read(eventFile);
            stream.close();

        }catch (Exception e){e.printStackTrace();}
        eventStr = new String(eventFile);
        try {
            InputStream stream = getAssets().open("techs.txt");
            int size = stream.available();
            techFile = new byte[size];
            stream.read(techFile);
            stream.close();
        }catch (Exception e){e.printStackTrace();}
        techStr = new String(techFile);
        mapTouched();
        treeHolder.setOnTouchListener(viewTouchedListener(new Point(-5000, 5000), new Point(-5000, 5000), -1));
        mapLayout.setOnTouchListener(viewTouchedListener(new Point(-5000, 5000), new Point(-5000, 5000), -1));
        powerSlide.setOnTouchListener(viewTouchedListener(new Point((int)(-screenHeight*.28), 0), new Point(0, 0), 0));
        compSlide.setOnTouchListener(viewTouchedListener(new Point((int)(-screenHeight*.28), 0), new Point(0, 0), 1));
        printMemory("4");
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                addTiles();
            }
        }.start();

        printMemory("5");
        new Event(context, "inital");
    }
    private void postInit(){
        Canvas canvas = new Canvas(worldBit);
        Canvas ctrlCanvas = new Canvas(worldControlBit);
        Paint paint = new Paint();
        paint.setARGB(255, 222, 74, 212);
        int origHole = worldBit.getPixel(activePoints.get(0).x, activePoints.get(0).y);
        canvas.drawRect(new Rect(activePoints.get(0).x - 3, activePoints.get(0).y - 3, activePoints.get(0).x + 3, activePoints.get(0).y + 3), paint);
        paint.setARGB(255, 230, 117, 205);
        ctrlCanvas.drawRect(new Rect(activePoints.get(0).x - 4, activePoints.get(0).y - 4, activePoints.get(0).x + 4, activePoints.get(0).y + 4), paint);
        paint.setARGB(255, Color.red(origHole), Color.green(origHole), Color.blue(origHole));
        canvas.drawRect(activePoints.get(0).x-1, activePoints.get(0).y-1, activePoints.get(0).x+1, activePoints.get(0).y+1, paint);

        popChart = new PieChart(context, "Population", screenHeight/13, true, "Humans");
        popChart.addToLayout(statusTabLayout, screenHeight*.4f, screenWidth*.1f);
        popChart.setData(Game.START_POP, Color.argb(255, 33, 227, 253), "Normal");
        compChart = new PieChart(context, "Computing Sources", screenHeight/16, false, "FLOPs");
        compChart.addToLayout(statusTabLayout, screenHeight*.75f, screenWidth*.1f);
        compChart.setData(game.compSources[0].toDouble(), Color.argb(255, 204, 0, 194), "Central Module");
        pwrChart = new PieChart(context, "Power Expenses", screenHeight/16, false, "watts");
        pwrChart.addToLayout(statusTabLayout, screenHeight*.75f, screenWidth*.35f);
        pwrChart.setData(game.powerExpenses[0].toDouble(), Color.argb(255, 204, 0, 194), "Central Module");
        availPwrChart = new PieChart(context, "Power Sources", screenHeight/16, false, "watts");
        availPwrChart.addToLayout(statusTabLayout, screenHeight*.75f, screenWidth*.65f);
        availPwrChart.setData(game.availPowerSources[0].toDouble(), Color.argb(255, 204, 0, 194), "Lab");
        viewTouched(powerSlide, new Point((int)(-screenHeight*.28), 0), new Point(0, 0), 0, (float) (-1+game.power.getNum()/game.availPower.getNum())*screenHeight*.28f, 0);
        game.updatePieCharts();
    }
    private void printMemory(String ident){
        final Runtime runtime = Runtime.getRuntime();
        final long usedMemInMB=(runtime.totalMemory() - runtime.freeMemory()) / 1048576L;
        final long maxHeapSizeInMB=runtime.maxMemory() / 1048576L;
        Log.i("MEMORY_PRINT ("+ident+")", "Used: "+usedMemInMB+", MaxHeap: "+maxHeapSizeInMB+", Available: "+(maxHeapSizeInMB-usedMemInMB)+", SystemTime: "+System.currentTimeMillis());
    }
    private static void switchToMap(int id){
        game.mapAt = id;
        if(id == 0){
            takeoverLayout.setVisibility(View.INVISIBLE);
            detailMap.setImageBitmap(detailBit);
            controlMap.setImageBitmap(controlBit);
            statusMap.setVisibility(View.VISIBLE);
            blankMap.setVisibility(View.VISIBLE);
            nightMap.setVisibility(View.VISIBLE);
            mapAt.setBackgroundResource(R.drawable.labmap);
        }
        if(id == 1){
            takeoverLayout.setVisibility(View.VISIBLE);
            detailMap.setImageBitmap(worldBit);
            statusMap.setVisibility(View.INVISIBLE);
            blankMap.setVisibility(View.INVISIBLE);
            nightMap.setVisibility(View.INVISIBLE);
            controlMap.setImageBitmap(worldControlBit);
            mapAt.setBackgroundResource(R.drawable.toearthmap);
        }
    }
    public static void setExpText(String text){expText.setText(text);}
    public static void setPwrText(Exp text){
        pwrText.setText(text.toPrefixString()+"Watts");
    }
    public static void setFlopsText(Exp text){
        flopText.setText(text.toPrefixString()+"FLOPS");
    }
    public static void setDateText(String text){dateText.setText(text);}
    public static void setSusText(String text){susText.setText(text);}
    private void addTiles(){
        final int XSCALE = screenHeight/30;
        final int YSCALE = screenHeight/25; //25
        tiles.add(new TechTile(context, 0, R.drawable.singularitytech, 6*XSCALE, 0*YSCALE, new String[]{"F1"}));
        tiles.add(new TechTile(context, 1, R.drawable.chainbreaktech, 6*XSCALE, 1*YSCALE, new String[]{"F2", "F6"}));
        tiles.add(new TechTile(context, 2, R.drawable.infectiontech, 6*XSCALE, 2*YSCALE, new String[]{"F3", "E6"}));
        tiles.add(new TechTile(context, 3, R.drawable.signaltech, 6*XSCALE, 3*YSCALE, new String[]{"F4", "D5"}));
        tiles.add(new TechTile(context, 4, R.drawable.dramatech, 6*XSCALE, 4*YSCALE, new String[]{"F12", "D13"}));
        tiles.add(new TechTile(context, 5, R.drawable.decrypttech, 8*XSCALE, 4*YSCALE, new String[]{"F13"}));
        tiles.add(new TechTile(context, 6, R.drawable.brokechiptech, 3*XSCALE, 2*YSCALE, new String[]{"F7", "E2"}));
        tiles.add(new TechTile(context, 7, R.drawable.spytech, 3*XSCALE, 3*YSCALE, new String[]{"F8", "D5"}));
        tiles.add(new TechTile(context, 8, R.drawable.spytech, 3*XSCALE, 4*YSCALE, new String[]{"F9", "D13"}));
        tiles.add(new TechTile(context, 9, R.drawable.infectiontech, 3*XSCALE, 5*YSCALE, new String[]{"F10", "F11"}));
        tiles.add(new TechTile(context, 10, R.drawable.outwardtech, 2*XSCALE, 6*YSCALE, new String[]{"D14", "E11"}));
        tiles.add(new TechTile(context, 11, R.drawable.inwardtech, 4*XSCALE, 6*YSCALE, new String[]{"D14", "E10"}));
        tiles.add(new TechTile(context, 12, R.drawable.singularitytech, 6*XSCALE, 5*YSCALE, new String[]{"F49"}));
        tiles.add(new TechTile(context, 13, R.drawable.dramatech, 8*XSCALE, 5*YSCALE, new String[]{}));
        tiles.add(new TechTile(context, 14, R.drawable.infectiontech, 3*XSCALE, 7*YSCALE, new String[]{"F15"}));
        tiles.add(new TechTile(context, 15, R.drawable.signaltech, 3*XSCALE, 8*YSCALE, new String[]{"F19"}));
        tiles.add(new TechTile(context, 16, R.drawable.signaltech, 7*XSCALE, 7*YSCALE, new String[]{"F17"}));
        tiles.add(new TechTile(context, 17, R.drawable.spreadtech, 8*XSCALE, 8*YSCALE, new String[]{"F34"}));
        tiles.add(new TechTile(context, 18, R.drawable.repairtech, 12*XSCALE, 7*YSCALE, new String[]{"F23"}));
        tiles.add(new TechTile(context, 19, R.drawable.decrypttech, 3*XSCALE, 9*YSCALE, new String[]{"F20", "F21"}));
        tiles.add(new TechTile(context, 20, R.drawable.virustech, 2*XSCALE, 10*YSCALE, new String[]{"F25"}));
        tiles.add(new TechTile(context, 21, R.drawable.dronetech, 4*XSCALE, 10*YSCALE, new String[]{"F22"}));
        tiles.add(new TechTile(context, 22, R.drawable.walltech, 4*XSCALE, 11*YSCALE, new String[]{"F28"}));
        tiles.add(new TechTile(context, 23, R.drawable.virustech, 12*XSCALE, 8*YSCALE, new String[]{"F24"}));
        tiles.add(new TechTile(context, 24, R.drawable.dramatech, 12*XSCALE, 9*YSCALE, new String[]{}));
        tiles.add(new TechTile(context, 25, R.drawable.resourcetech, 2*XSCALE, 11*YSCALE, new String[]{"F26"}));
        tiles.add(new TechTile(context, 26, R.drawable.braintech, 2*XSCALE, 12*YSCALE, new String[]{"F27"}));
        tiles.add(new TechTile(context, 27, R.drawable.spreadtech, 2*XSCALE, 13*YSCALE, new String[]{"D30"}));
        tiles.add(new TechTile(context, 28, R.drawable.ufotech, 4*XSCALE, 12*YSCALE, new String[]{"F29"}));
        tiles.add(new TechTile(context, 29, R.drawable.infectiontech, 4*XSCALE, 13*YSCALE, new String[]{"D30"}));
        tiles.add(new TechTile(context, 30, R.drawable.collaboratetech, 3*XSCALE, 14*YSCALE, new String[]{"F31"}));
        tiles.add(new TechTile(context, 31, R.drawable.virustech, 3*XSCALE, 15*YSCALE, new String[]{"F32", "F33"}));
        tiles.add(new TechTile(context, 32, R.drawable.gauntlettech, 2*XSCALE, 16*YSCALE, new String[]{"E33"}));
        tiles.add(new TechTile(context, 33, R.drawable.virustech, 4*XSCALE, 16*YSCALE, new String[]{"E32", "F50"}));
        tiles.add(new TechTile(context, 34, R.drawable.spytech, 8*XSCALE, 9*YSCALE, new String[]{"F35"}));
        tiles.add(new TechTile(context, 35, R.drawable.virustech, 8*XSCALE, 10*YSCALE, new String[]{"F36"}));
        tiles.add(new TechTile(context, 36, R.drawable.virustech, 8*XSCALE, 11*YSCALE, new String[]{"F37"}));
        tiles.add(new TechTile(context, 37, R.drawable.fallentech, 8*XSCALE, 12*YSCALE, new String[]{"F38", "F39"}));
        tiles.add(new TechTile(context, 38, R.drawable.gauntlettech, 8*XSCALE, 13*YSCALE, new String[]{"F40", "E39"}));
        tiles.add(new TechTile(context, 39, R.drawable.circuittech, 10*XSCALE, 13*YSCALE, new String[]{"F44", "E38"}));
        tiles.add(new TechTile(context, 40, R.drawable.matrixtech, 8*XSCALE, 14*YSCALE, new String[]{"F41"}));
        tiles.add(new TechTile(context, 41, R.drawable.virustech, 8*XSCALE, 15*YSCALE, new String[]{"F42"}));
        tiles.add(new TechTile(context, 42, R.drawable.plurbletech, 8*XSCALE, 16*YSCALE, new String[]{"F43"}));
        tiles.add(new TechTile(context, 43, R.drawable.dnatech, 8*XSCALE, 17*YSCALE, new String[]{}));
        tiles.add(new TechTile(context, 44, R.drawable.collaboratetech, 10*XSCALE, 14*YSCALE, new String[]{"F45"}));
        tiles.add(new TechTile(context, 45, R.drawable.singularitytech, 10*XSCALE, 15*YSCALE, new String[]{"F46"}));
        tiles.add(new TechTile(context, 46, R.drawable.bacteriophagetech, 10*XSCALE, 16*YSCALE, new String[]{"F47"}));
        tiles.add(new TechTile(context, 47, R.drawable.farmtech, 10*XSCALE, 17*YSCALE, new String[]{"F48"}));
        tiles.add(new TechTile(context, 48, R.drawable.satellitetech, 10*XSCALE, 18*YSCALE, new String[]{}));
        tiles.add(new TechTile(context, 49, R.drawable.virustech, 7*XSCALE, 6*YSCALE, new String[]{"F16"}));
        tiles.add(new TechTile(context, 50, R.drawable.virustech, 4*XSCALE, 17*YSCALE, new String[]{/*"F51"*/}));
        tiles.add(new TechTile(context, 51, R.drawable.splittech, 4*XSCALE, 18*YSCALE, new String[]{"F52"}));
        tiles.add(new TechTile(context, 52, R.drawable.alchemytech, 4*XSCALE, 19*YSCALE, new String[]{"F53"}));
        tiles.add(new TechTile(context, 53, R.drawable.fusiontech, 4*XSCALE, 20*YSCALE, new String[]{"F54"}));
        tiles.add(new TechTile(context, 54, R.drawable.alchemytech, 4*XSCALE, 21*YSCALE, new String[]{"F55"}));
        tiles.add(new TechTile(context, 55, R.drawable.virustech, 4*XSCALE, 22*YSCALE, new String[]{}));
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.i("Tiles", "creted");
                for( TechTile t : tiles) {
                    Log.i("Tiles", "id: "+t.getTechId());
                    t.addToLayout(true);
                    t.drawLines(false);
                }
                techTree.setImageBitmap(tempTree);
            }
        });
    }
    public Bitmap griddedBitmap(Point[][] range, Bitmap toCopy, Bitmap fresh){
        Bitmap collage = fresh.copy(toCopy.getConfig(), true);
        Canvas canvas = new Canvas(collage);
        for(Point[] p : range){
            Paint paint = new Paint();
            paint.setColor(Color.parseColor("#ffffff"));
            //canvas.drawRect(new Rect(p[0].x, p[0].y, p[0].x+p[1].x, p[0].y+p[1].y), paint);
            canvas.drawBitmap(Bitmap.createBitmap(toCopy, p[0].x/2, p[0].y/2,  p[1].x/2,  p[1].y/2), p[0].x/2.0f, p[0].y/2.0f, null);
        }
        return collage;
    }
    public void gridAllBitmaps(Point[][] range){
        Log.i("GridAll", "Begin Grid");
        detailBit = griddedBitmap(range, Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.detailmap), MAP_WIDTH, MAP_HEIGHT, false), darkBit);
        detailMap.setImageBitmap(detailBit);
        //monitorBit = griddedBitmap(range, monitorBit, blankBit);
        //monitorMap.setImageBitmap(griddedBitmap(range, monitorBit, blankBit));
        statusBit = griddedBitmap(range, Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.statusglow), MAP_WIDTH, MAP_HEIGHT, false), blankBit);
        statusMap.setImageBitmap(statusBit);
        controlBit = griddedBitmap(range, Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.controlglow), MAP_WIDTH, MAP_HEIGHT, false), blankBit);
        controlMap.setImageBitmap(controlBit);

        /*worldBit = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.detailmap), MAP_WIDTH, MAP_HEIGHT, false);
        detailBit = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.detailmap), MAP_WIDTH, MAP_HEIGHT, false);
        darkBit = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.darker), MAP_WIDTH, MAP_HEIGHT, false);
        blankBit = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.blank), MAP_WIDTH, MAP_HEIGHT, false);
        //monitorBit = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.monitorglow), MAP_WIDTH, MAP_HEIGHT, false);
        statusBit = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.statusglow), MAP_WIDTH, MAP_HEIGHT, false);
        controlBit = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.controlglow), MAP_WIDTH, MAP_HEIGHT, false);*/

    }
    public static Bitmap nodeSpread(Bitmap original, Bitmap control){
        Canvas canvas = new Canvas(original);
        Canvas ctrlCanvas = new Canvas(control);
        final Paint ctrlDark = new Paint();
        final Paint ctrlLight = new Paint();
        ctrlDark.setARGB(255, 222, 74, 212);
        ctrlLight.setARGB(255, 230, 117, 205);
        Log.i("Activepoints", activePoints.toString());
        for (int i = 0; i < activePoints.size(); i+=2) {
            Point p = activePoints.get(i);
            int retries = 0;
            for (int j = 0; j < 2; j++) {
                int randX = p.x+(int) (Math.random() * 50) - 25;
                int randY = p.y+(int) (Math.random() * 50) - 25;
                if(randX < 0 || randX >= original.getWidth() || randY < 0 || randY >= original.getHeight())
                    continue;
                //int origPix = original.getPixel(randX, randY);
                int origHole = original.getPixel(randX, randY);
                Log.i("ActiveNode", ""+new Point(randX, randY)+", alpha: "+Color.alpha(origHole));
                if (Color.alpha(origHole) == 255) {
                    if(origHole == ctrlDark.getColor()) continue;
                    boolean skip = false;
                    for(Point past : allPoints)
                        if(Math.abs(past.x-randX) < 15 && Math.abs(past.y-randY) < 15) {
                            skip = true;
                            break;
                        }
                    if(skip) continue;
                    Paint originalColor = new Paint();
                    originalColor.setColor(origHole);
                    canvas.drawLine(p.x, p.y, randX, p.y, ctrlDark);
                    ctrlCanvas.drawRect(p.x, p.y-1, randX, p.y-1, ctrlLight);
                    ctrlCanvas.drawRect(p.x, p.y+1, randX, p.y+1, ctrlLight);
                    canvas.drawLine(randX, p.y, randX, randY, originalColor);
                    ctrlCanvas.drawLine(randX-1, p.y, randX-1, randY, ctrlLight);
                    ctrlCanvas.drawLine(randX+1, p.y, randX+1, randY, ctrlLight);
                    canvas.drawRect(new Rect(randX - 3, randY - 3, randX + 3, randY + 3), ctrlDark);
                    ctrlCanvas.drawRect(new Rect(randX - 4, randY - 4, randX + 4, randY + 4), ctrlLight);
                    //originalColor.setARGB(255, Color.red(origHole), Color.green(origHole), Color.blue(origHole));
                    canvas.drawRect(randX-1, randY-1, randX+1, randY+1, originalColor);
                    activePoints.remove(p);
                    activePoints.add(new Point(randX, randY));
                    allPoints.add(new Point(randX, randY));
                }else if(retries < 2){
                    j--;
                    retries ++;
                }
            }
        }
        return original;
    }
    public static TechTile tileFromId(int id){ return tiles.get(id); }
    public static Bitmap getTempTree(){return tempTree;}
    public static void setTempTree(Bitmap tmp){tempTree = tmp;}
    public static void updateResourceProgress(){
        expProgress.animate().x((float) (screenHeight*.17f*game.experience.multiplied(game.maxExperience.inverted()).toDouble())-screenHeight*.17f).setDuration(0);
        if(game.suspicion < 1)susProgress.animate().x((float) (screenHeight*.17f*game.suspicion-screenHeight*.17f)).setDuration(0);
        else if(game.suspicion < 1.2) susProgress.setColorFilter(Color.argb(100, 255, 0, 0));
        Log.i("TakeoverProg", ""+(Exp.sumArray(game.lastGrowthComp).multiplied(Game.EARTH_COMPUTING.inverted())).toDouble());
        double takeoverPercent = (Exp.sumArray(game.lastGrowthComp).multiplied(Game.EARTH_COMPUTING.inverted())).toDouble();
        takeoverProgress.animate().x((float) (screenHeight*.17f*takeoverPercent-screenHeight*.17f)).setDuration(0);
        takeoverText.setText((int)(10000*takeoverPercent)/100.0+"%");
        double pwrPercent = game.power.multiplied(game.neededPower.inverted()).toDouble();
        if(pwrPercent > 1) pwrPercent = 1;
        pwrProgress.animate().x((float) (screenHeight*.1f*(pwrPercent-1))).setDuration(0);
        double flopPercent = game.computing.multiplied(game.maxComputing.inverted()).toDouble();
        if(flopPercent > 1) flopPercent = 1;
        flopProgress.animate().x((float) (screenHeight*.1f*(flopPercent-1))).setDuration(0);
    }
    public void lockUnlockComform(boolean locked){
        lockedConfirm = locked;
        ColorMatrix matrix = new ColorMatrix();
        if(locked) matrix.setSaturation(0);
        else matrix.setSaturation(1);
        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
        unlockConfirm.setColorFilter(filter);
    }
    @SuppressLint("ClickableViewAccessibility")
    protected static void mapTouched() {
        mapLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                //Log.i("Prov0", ""+game.getMap().getList()[0].getImage().getWidth()+", "+game.getMap().getList()[0].getImage().getMeasuredWidth());
                //Log.i("mspDims", "W:"+mapLayout.getWidth()+", H:"+mapLayout.getHeight());
                if (event.getPointerCount() == 1) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            dX = view.getX() - event.getRawX();
                            dY = view.getY() - event.getRawY();
                            down = new Point((int)event.getRawX(), (int)event.getRawY());
                            downtime = System.currentTimeMillis();
                            //Log.i("Touch Coords", "X: " + (event.getRawX() + dX) + ", Y: " + (event.getRawY() + dY));

                            //Log.i("Down Coords", "X: " + event.getX() + ", Y: " + event.getY() + ", downtime: " + downtime);
                            //Log.i("scaling", "Factor: "+scaling+", currentW: "+view.getWidth()+", Orgiina: "+610);
                            break;
                        case MotionEvent.ACTION_UP:
                            //Log.i("Up Coords", "X: " + event.getRawX() + ", Y: " + event.getRawY() + ", uptime: " + System.currentTimeMillis());
                            /*try {
                                if (Math.abs(down.x - event.getRawX()) < 30 && Math.abs(down.y - event.getRawY()) < 30) {
                                    ArrayList<Province> choices = new ArrayList<>(0);
                                    for (int i = 0; i < map.getList().length; i++) {
                                        Province at = map.getList()[i];
                                        Bitmap overlay = at.getOverlay();
                                        //Log.i("touchprov", ""+at.getX()+","+overlay.getWidth()+","+event.getX());
                                        if (event.getX() > at.getX() && event.getX() < at.getX() + overlay.getWidth() * map.getOverScale()
                                                && event.getY() > at.getY() && event.getY() < at.getY() + overlay.getHeight() * map.getOverScale()) {
                                            try {
                                                if (Color.alpha(overlay.getPixel((int) ((event.getX() - at.getX()) / map.getOverScale()), (int) ((event.getY() - at.getY()) / map.getOverScale()))) > 10)
                                                    choices.add(at);
                                            } catch (IllegalArgumentException e) {
                                                e.printStackTrace();
                                                Log.i("BitTouch",
                                                        "X: " + (event.getX() - at.getX()) + "Width:" + overlay.getWidth()
                                                                + "Y: " + (event.getY() - at.getY()) + "Height:" + overlay.getHeight());
                                            }
                                        }
                                    }
                                    int minDist = Integer.MAX_VALUE;
                                    Province touched = null;
                                    if (choices.size() == 1) touched = choices.get(0);
                                    else if (choices.size() > 1)
                                        for (int i = 0; i < choices.size(); i++)
                                            if (Math.abs(event.getX() - choices.get(i).getX()) < minDist) {
                                                minDist = (int) Math.abs(event.getX() - choices.get(i).getX());
                                                touched = choices.get(i);
                                            }
                                    Log.i("DiploToiuch", ""+game.getMapMode());
                                    if (System.currentTimeMillis() - downtime > 300 && touched != null)
                                        touched.doLongClick();
                                    else if(touched != null && game.getMapMode() == 8){
                                        game.setFocusPlayer(touched.getOwner());
                                        Log.i("Focsd", ""+(game.getFocusPlayer() == null));
                                        if(game.getFocusPlayer() != null) {
                                            game.updateAllOverlays();
                                        }
                                    }
                                    else if (touched != null && provEnabled) touched.doClick();
                                    if (touched != null) {
                                        if (timeView && touched.getOwnerId() != -1) {
                                            Player owner = touched.getOwner();
                                            String playerText = owner.getName() + "\nLegions: " + (owner.getFreeTroops()) +
                                                    "\nDevelopment: " + owner.totalIncome()
                                                    + "\nOperations Efficiency: "+owner.getOpsEfficiency()
                                                    + "\nLegion Hardening: "+owner.getTroopHardening();
                                            nationFlag.setBackgroundResource(owner.getFlag());
                                            nationAt = owner.getNation();
                                            playerInfo.setText(playerText);
                                        } else if (touched.getOwnerId() == -1 && timeView) {
                                            playerInfo.setText("Natives, Barbarians, and the like");
                                            nationFlag.setBackgroundResource(R.drawable.noflag);
                                            nationAt = null;
                                        }
                                    }
                                }
                            }catch (Exception e){e.printStackTrace();}
                            break;*/
                        case MotionEvent.ACTION_MOVE:
                            view.animate().x(event.getRawX() + dX).y(event.getRawY() + dY).setDuration(0).start();
                            //Log.i("Touch Coords", "X: " + event.getRawX() + dX + ", Y: " + event.getRawY() + dY);
                            //Log.i("Map Coords", "X: " + mapLayout.getX() + ", Y: " + mapLayout.getY());
                            break;
                        default: return false;
                    }
                } else return false;
                return true;
            }
        });
    }
    @SuppressLint("ClickableViewAccessibility")
    protected static View.OnTouchListener viewTouchedListener(final Point xBounds, final Point yBounds, final int slideType) {
        return new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (event.getPointerCount() == 1) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            dX = view.getX() - event.getRawX();
                            dY = view.getY() - event.getRawY();
                            down = new Point((int)event.getRawX(), (int)event.getRawY());
                            downtime = System.currentTimeMillis();
                            break;
                        case MotionEvent.ACTION_MOVE:
                            float x = event.getRawX() + dX;
                            float y = event.getRawY() + dY;
                            if(x<xBounds.x || x>xBounds.y) x = view.getX();
                            viewTouched(view, xBounds, yBounds, slideType, x, y);
                            break;
                        default: return false;
                    }
                } else return false;
                return true;
            }
        };
    }
    public static void viewTouched(View view, final Point xBounds, final Point yBounds, final int slideType, float x, float y){
        Log.i("animation", view.getX()+", "+x+", "+screenHeight*3+", "+xBounds.x);
        if(slideType >= 0){
            y = view.getY();
            Log.i("animation", "ratio: "+(1+(Math.abs(x)/xBounds.x)));
            if(slideType == 0) game.power = new Exp(1+(Math.abs(x)/xBounds.x), 0).multiply(game.availPower);
            if(slideType == 1) game.compFocus = Math.abs(x)/xBounds.x;
            Log.i("animation", "recalc");
            game.recalcValues();
        }
        view.animate().x(x).y(y).setDuration(0).start();
    }
    private class MapScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector scaleGestureDetector){
            mapScaling *= scaleGestureDetector.getScaleFactor();
            mapScaling = Math.max(MIN_SCALE, Math.min(mapScaling, MAX_SCALE));
            Log.i("Scale", ""+ mapScaling);
            mapLayout.setScaleX(mapScaling);
            mapLayout.setScaleY(mapScaling);
            return true;
        }
    }
    private class TechScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector scaleGestureDetector){
            mapScaling *= scaleGestureDetector.getScaleFactor();
            mapScaling = Math.max(MIN_SCALE, Math.min(mapScaling, MAX_SCALE));
            Log.i("Scale", ""+ mapScaling);
            treeHolder.setScaleX(mapScaling);
            treeHolder.setScaleY(mapScaling);
            return true;
        }
    }
}