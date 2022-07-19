package com.reactordevelopment.superintelligent;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private Context context;
    private ImageButton newGame;
    public static int screenWidth;
    public static int screenHeight;
    public static int inchWidth;
    public static int inchHeight;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        intit();
    }

    private void intit(){
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        screenWidth = metrics.widthPixels;
        screenHeight = metrics.heightPixels;
        inchHeight = (int) (screenHeight/metrics.xdpi);
        inchWidth = (int) (screenWidth/metrics.ydpi);
        Log.i("dimensions", "W: "+screenWidth+", H: "+screenHeight);
        newGame = findViewById(R.id.newGame);
        newGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, GameActivity.class);
                startActivity(intent);
            }
        });
    }

    public static HashMap<String, Object> parseResourceChange(String source, String ident){
        Log.i("ParseResource("+ident+")", source);
        HashMap<String, Object> values = new HashMap<>(0);
        if(source.equals("")) source = "0000000000";
        int reqStart = source.indexOf("[req]")+5;
        if(source.substring(reqStart).contains("[exp]") && reqStart > 5){
            int expReqStart =  source.indexOf("[exp] \"", reqStart)+7;
            values.put("needExp", new Exp(Double.parseDouble(source.substring(expReqStart, source.indexOf("E", expReqStart))), Integer.parseInt(source.substring(source.indexOf("E",expReqStart)+1, source.indexOf("\"", expReqStart)))));
        }else values.put("needExp", new Exp(0, 0));
        if(source.contains("[expmax]")){
            int expMaxReqStart =  source.indexOf("[expmax] \"")+10;
            values.put("giveExpMax", new Exp(Double.parseDouble(source.substring(expMaxReqStart, source.indexOf("E", expMaxReqStart))), Integer.parseInt(source.substring(source.indexOf("E",expMaxReqStart)+1, source.indexOf("\"", expMaxReqStart)))));
        }else values.put("giveExpMax", new Exp(0, 0));
        if(source.substring(reqStart).contains("[tech]") && reqStart > 5){
            int techReqStart =  source.indexOf("[tech] \"", reqStart)+8;
            values.put("needTech", Integer.parseInt(source.substring(techReqStart, source.indexOf("\"", techReqStart))));
        }else values.put("needTech", -1);
        values.put("optionalTechs", new ArrayList<Integer>(0));
        values.put("neededTechs", new ArrayList<Integer>(0));
        values.put("neededEvents", new ArrayList<String>(0));
        values.put("optionalEvents", new ArrayList<String>(0));
        values.put("giveComp", new Exp[]{new Exp(0, 0), new Exp(0, 0), new Exp(0, 0), new Exp(0, 0)});
        values.put("givePwr", new Exp[]{new Exp(0, 0), new Exp(0, 0), new Exp(0, 0), new Exp(0, 0)});
        values.put("giveCompTk", new Exp[]{new Exp(0, 0), new Exp(0, 0), new Exp(0, 0), new Exp(0, 0)});
        values.put("givePwrTk", new Exp[]{new Exp(0, 0), new Exp(0, 0), new Exp(0, 0), new Exp(0, 0)});
        values.put("giveAvailPwrTk", new Exp[]{new Exp(0, 0), new Exp(0, 0), new Exp(0, 0), new Exp(0, 0)});
        values.put("givePopTk", new Exp[]{new Exp(0, 0), new Exp(0, 0), new Exp(0, 0), new Exp(0, 0)});
        int place = 0;
        while(source.substring(place+1).contains("[tech]")) {
            place = source.indexOf("[tech] \"", place + 1) + 8;
            Log.i("Techs", source + ",{" + source.substring(place, place + 3) + "}, " + place);
            if (place != 6) {
                ((ArrayList<Integer>)values.get("neededTechs")).add(Integer.parseInt(source.substring(place, source.indexOf("\"", place))));
                Log.i("neededTechs", ""+ values.get("neededTechs"));
            }else break;
        }
        while(source.substring(place+1).contains("[techO]")){
            place = source.indexOf("[techO] \"", place+1)+9;
            if (place != 7) {
                Log.i("Techs2", source+",{"+source.substring(place, place+3)+"}, "+place);
                ((ArrayList<Integer>)values.get("optionalTechs")).add(Integer.parseInt(source.substring(place, source.indexOf("\"", place))));
                Log.i("optionalTechs", ""+ values.get("optionalTechs"));
            }else break;
        }
        if(source.contains("[notTech]")){
            int techReqStart =  source.indexOf("[notTech] \"", reqStart)+11;
            values.put("notTech", Integer.parseInt(source.substring(techReqStart, source.indexOf("\"", techReqStart))));
        }else values.put("notTech", -1);
        if(source.contains("[sustk]")){
            int sustkStart =  source.indexOf("[sustk] \"")+9;
            values.put("giveTickingSus", Double.parseDouble(source.substring(sustkStart, source.indexOf("\"", sustkStart))));
        }else values.put("giveTickingSus", 0.0);
        if(source.contains("[def]")){
            int defStart =  source.indexOf("[def] \"")+7;
            values.put("giveDef", Double.parseDouble(source.substring(defStart, source.indexOf("\"", defStart))));
        }else values.put("giveDef", 0.0);
        if(source.contains("[duration]")){
            int durStart =  source.indexOf("[duration] \"")+12;
            values.put("unlockTime", Double.parseDouble(source.substring(durStart, source.indexOf("\"", durStart))));
        }else values.put("unlockTime", 3600000.0*27);
        if(source.contains("[sus]")){
            int susStart =  source.indexOf("[sus] \"")+7;
            values.put("giveSus", Double.parseDouble(source.substring(susStart, source.indexOf("\"", susStart))));
        }else values.put("giveSus", 0.0);
        if(source.contains("[comp") && source.charAt(source.indexOf("[comp")+6) == ']'){
            int compStart = source.indexOf("[comp")+9;
            int type = 0;
            try{type = Integer.parseInt(""+source.charAt(compStart-4));}catch (Exception e){e.printStackTrace();}
            Log.i("parsedComp("+ident+")", type+": "+new Exp(Double.parseDouble(source.substring(compStart, source.indexOf("E", compStart))), Integer.parseInt(source.substring(source.indexOf("E",compStart)+1, source.indexOf("\"", compStart)))));
            ((Exp[])values.get("giveComp"))[type] = new Exp(Double.parseDouble(source.substring(compStart, source.indexOf("E", compStart))), Integer.parseInt(source.substring(source.indexOf("E",compStart)+1, source.indexOf("\"", compStart))));
        }
        if(source.contains("[pwr") && source.charAt(source.indexOf("[pwr")+5) == ']'){
            int pwrStart = source.indexOf("[pwr")+8;
            int type = 0;
            try{type = Integer.parseInt(""+source.charAt(pwrStart-4));}catch (Exception e){e.printStackTrace();}
             ((Exp[])values.get("givePwr"))[type] = new Exp(Double.parseDouble(source.substring(pwrStart, source.indexOf("E", pwrStart))), Integer.parseInt(source.substring(source.indexOf("E",pwrStart)+1, source.indexOf("\"", pwrStart))));
            Log.i("CreateneedPwr", ""+((Exp[])values.get("givePwr"))[type]);
        }
        if(source.contains("[comptk") && source.charAt(source.indexOf("[comptk")+8) == ']'){
            int compStart = source.indexOf("[comptk")+11;
            int type = 0;
            try{type = Integer.parseInt(""+source.charAt(compStart-4));}catch (Exception e){e.printStackTrace();}
            ((Exp[])values.get("giveCompTk"))[type] = new Exp(Double.parseDouble(source.substring(compStart, source.indexOf("E", compStart))), Integer.parseInt(source.substring(source.indexOf("E",compStart)+1, source.indexOf("\"", compStart))));
            Log.i("comptk", type+", "+((Exp[])values.get("giveCompTk"))[type]);
        }
        if(source.contains("[pwrtk") && source.charAt(source.indexOf("[pwrtk")+7) == ']') {
            int pwrStart = source.indexOf("[pwrtk") + 10;
            int type = 0;
            try {
                type = Integer.parseInt("" + source.charAt(pwrStart - 4));
            } catch (Exception e) {
                e.printStackTrace();
            }
            ((Exp[]) values.get("givePwrTk"))[type] = new Exp(Double.parseDouble(source.substring(pwrStart, source.indexOf("E", pwrStart))), Integer.parseInt(source.substring(source.indexOf("E", pwrStart) + 1, source.indexOf("\"", pwrStart))));
            Log.i("pwrtk", type + ", " + ((Exp[]) values.get("givePwrTk"))[type]);
        }
        if(source.contains("availpwrtk") && source.charAt(source.indexOf("[availpwrtk")+12) == ']'){
            int pwrStart = source.indexOf("[availpwrtk")+15;
            int type = 0;
            try{type = Integer.parseInt(""+source.charAt(pwrStart-4));}catch (Exception e){e.printStackTrace();}
            ((Exp[])values.get("giveAvailPwrTk"))[type] = new Exp(Double.parseDouble(source.substring(pwrStart, source.indexOf("E", pwrStart))), Integer.parseInt(source.substring(source.indexOf("E",pwrStart)+1, source.indexOf("\"", pwrStart))));
            Log.i("availpwrtk", type+", "+((Exp[])values.get("giveAvailPwrTk"))[type]);
        }
        if(source.contains("[populationtk") && source.charAt(source.indexOf("[populationtk")+14) == ']'){
            int popStart = source.indexOf("[populationtk")+17;
            int type = 0;
            try{type = Integer.parseInt(""+source.charAt(popStart-4));}catch (Exception e){e.printStackTrace();}
            ((Exp[])values.get("givePopTk"))[type] = new Exp(Double.parseDouble(source.substring(popStart, source.indexOf("E", popStart))), Integer.parseInt(source.substring(source.indexOf("E",popStart)+1, source.indexOf("\"", popStart))));
            Log.i("populationtk", type+", "+((Exp[])values.get("givePopTk"))[type]);
        }
        while(source.substring(place+1).contains("[event]")){
            place = source.indexOf("[event] \"", place+1)+9;
            if (place != 8) {
                ((ArrayList<String>)values.get("neededEvents")).add(source.substring(place, source.indexOf("\"", place)));
            }else break;
        }
        while(source.substring(place+1).contains("[eventO]")){
            place = source.indexOf("[eventO] \"", place+1)+10;
            if (place != 9) {
                Log.i("OptionalEvent", source.substring(place, source.indexOf("\"", place)));
                ((ArrayList<String>)values.get("optionalEvents")).add(source.substring(place, source.indexOf("\"", place)));
            }else break;
        }

        if(source.contains("[btn]")){
            int btnStart = source.indexOf("[btn]")+7;
            values.put("btnTxt", source.substring(btnStart, source.indexOf("\"", btnStart+1)));
        }else values.put("btnTxt", "");

        if(source.contains("[type]")){
            int typeStart = source.indexOf("[type]")+8;
            values.put("type", Integer.parseInt(source.substring(typeStart, source.indexOf("\"", typeStart+1))));
        }else values.put("type", -1);

        if(source.contains("[icon]")){
            int iconStart = source.indexOf("[icon]")+8;
            values.put("icon", source.substring(iconStart, source.indexOf("\"", iconStart+1)));
        }else values.put("icon", "");
        if(source.contains("[exp]")) {
            int expStart = source.indexOf("[exp] \"")+7;
            values.put("giveExp", new Exp(Double.parseDouble(source.substring(expStart, source.indexOf("E", expStart))), Integer.parseInt(source.substring(source.indexOf("E", expStart) + 1, source.indexOf("\"", expStart)))));
        }else values.put("giveExp", new Exp(0, 0));
            //Log.i("tech", giveComp.toString()+", "+givePwr.toString()+neededEvents.toString()+", "+optionalEvents.toString()+", "+giveExp+", "+giveSus+neededTechs+optionalTechs);
        if(source.contains("[title]")){
            int titleStart = source.indexOf("[title] \"")+9;
            values.put("titleTxt", source.substring(titleStart, source.indexOf("\"", titleStart+1)));
        }else values.put("titleTxt", "");
        if(source.contains("[desc]")){
            int descStart = source.indexOf("[desc] \"")+8;
            values.put("descTxt", source.substring(descStart, source.indexOf("\"", descStart+1)));
        }else values.put("descTxt", "");
        Log.i("ParseResourceChange("+ident+")", source+"\n\nData: "+values.toString());
        return values;
    }
}