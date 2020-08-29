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
}