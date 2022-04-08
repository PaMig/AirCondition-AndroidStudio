package com.example.easyaircondition;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.Locale;

public class Settings extends AppCompatActivity {
    // go back
    private ImageButton back;
    private SwitchCompat switch1;
    //sleep mode
    private ToggleButton sleep;
    private TextView time;
    private static final long TIME_IN_MS = 600000 * 6;
    private long timeInM = TIME_IN_MS;
    private long endTime;
    private boolean running;
    private CountDownTimer countDownTimer;
    private ImageView moon;
    //fan mode
    private ToggleButton fan;
    private ImageView fanphoto;
    Dialog mdialog;
    //swing mode
    private ImageButton swing;
    private TextView swing_text;
    //Global
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String SLP = "slp";
    public static final String BTN1 = "btn1";
    public static final String FAN = "fan";
    public static final String SPD = "spd";
    public static final String SWG = "swg";

    private boolean btnsleep,btnfan,btnspeed;
    private String sw_txt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //back
        back = (ImageButton) findViewById(R.id.goback);

        //sleepmode
        sleep = (ToggleButton) findViewById(R.id.sleep);
        time = (TextView) findViewById(R.id.time);
        moon = (ImageView) findViewById(R.id.moon);

        //fan mode
        fan = (ToggleButton) findViewById(R.id.fan);
        fanphoto = (ImageView) findViewById(R.id.fanphoto);
        mdialog = new Dialog(this);
        switch1 = (SwitchCompat) findViewById(R.id.speedswitch);

        //swing mode
        swing = (ImageButton) findViewById(R.id.swing);
        swing_text = (TextView) findViewById(R.id.swing_text);

        //back onclicklistener
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savedata2();
                openMainActivity();
            }
        });
        loadata2();
        updateB2();
        //sleep mode listener
        sleep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Settings.this,"os",Toast.LENGTH_LONG).show();
                if(sleep.isChecked()){
                    moon.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_moon2));
                }else{
                    moon.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_moon1));
                }
                startStop();
            }
        });
        //fan listener
        fan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mdialog.setContentView(R.layout.popup);
                if(fan.isChecked()){
                    mdialog.show();
                    mdialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    fanphoto.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.mipmap.ic_cccc));
                }else{
                    fanphoto.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.mipmap.ic_ccc));
                }
            }
        });
        //swing listener
        swing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                swingMenu(v);
            }
        });
    }
    // go back
    public void openMainActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
    //Timer
    public void startStop(){
        if(running){
            stopTimer();
        }else{
            startTimer();
        }
    }
    public void startTimer() {
        endTime = System.currentTimeMillis() + timeInM ;
        if (sleep.isChecked()) {
            countDownTimer = new CountDownTimer(timeInM, 1000) {
                @Override
                public void onTick(long l) {
                    timeInM = l;
                    updateTimer();
                }
                @Override
                public void onFinish() {
                }
            }.start();
            running = true;
        }
    }
    public void stopTimer(){
        timeInM = TIME_IN_MS;
        running = false;
        countDownTimer.cancel();
        time.setText("60:00");
    }

    private void updateTimer(){
        int min = (int) (timeInM / 1000) / 60;
        int sec = (int) (timeInM / 1000) % 60;

        String timeLeftInFormat = String.format(Locale.getDefault(),"%02d:%02d",min,sec);
        time.setText(timeLeftInFormat);
        //   close btn
        if(min == 0 & sec == 0){
            sleep.setChecked(false);
            moon.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_moon1));
            time.setText("60:00");
            SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(BTN1,sleep.isChecked());
            editor.apply();
            btnsleep = sharedPreferences.getBoolean(BTN1, false);
            timeInM = TIME_IN_MS;
            running = false;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putLong("millisLeft",timeInM);
        editor.putLong("endTime",endTime);
        editor.putBoolean("timerRunning",running);
        editor.apply();
    }

    @Override
    protected void onStart() {
        super.onStart();
        updTime();
    }
    private void updTime(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        timeInM = sharedPreferences.getLong("millisLeft",TIME_IN_MS);
        running = sharedPreferences.getBoolean("timerRunning",false );
        updateTimer();

        if(running){
            endTime = sharedPreferences.getLong("endTime",0);
            timeInM = endTime - System.currentTimeMillis();
            if(timeInM<0){
                timeInM = 0;
                running = false;
                updateTimer();
            }else{
                startTimer();
            }
        }
    }
    //Shared Preferences impl
    public void savedata2(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SWG,swing_text.getText().toString());
        editor.putBoolean(SLP,sleep.isChecked());
        editor.putBoolean(FAN, fan.isChecked());
        editor.putBoolean(SPD, switch1.isChecked());

        editor.apply();
    }
    public void loadata2(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        btnsleep = sharedPreferences.getBoolean(SLP, false);
        btnfan = sharedPreferences.getBoolean(FAN, false);
        btnspeed = sharedPreferences.getBoolean(SPD, false);
        sw_txt = sharedPreferences.getString(SWG,"ΠΑΝΩ-ΚΑΤΩ ΚΙΝΗΣΗ");
    }
    public void updateB2() {
        sleep.setChecked(btnsleep);
        fan.setChecked(btnfan);
        switch1.setChecked(btnspeed);
        swing_text.setText(sw_txt);
        if (sleep.isChecked()) {
            moon.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_moon2));
        } else {
            moon.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_moon1));
        }
        if (fan.isChecked()) {
            fanphoto.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.mipmap.ic_cccc));
        } else {
            fanphoto.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.mipmap.ic_ccc));
        }
    }
    public void swingMenu(View v){
        PopupMenu popupMenu = new PopupMenu(Settings.this,v);
        popupMenu.getMenuInflater().inflate(R.menu.swing_menu,popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId() == R.id.up){
                    swing_text.setText("ΠΟΡΕΙΑ ΑΕΡΑ: ΠΑΝΩ");
                }
                if(item.getItemId() == R.id.middle){
                    swing_text.setText("ΠΟΡΕΙΑ ΑΕΡΑ: ΣΤΗ ΜΕΣΗ");
                }
                if(item.getItemId() == R.id.down){
                    swing_text.setText("ΠΟΡΕΙΑ ΑΕΡΑ: ΚΑΤΩ");
                }
                if(item.getItemId() == R.id.up_down){
                    swing_text.setText("ΠΑΝΩ-ΚΑΤΩ ΚΙΝΗΣΗ");
                }
                return true;
            }
        });
        popupMenu.show();
    }
}