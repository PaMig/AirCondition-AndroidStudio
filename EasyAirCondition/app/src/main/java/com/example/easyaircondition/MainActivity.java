package com.example.easyaircondition;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ToggleButton;


public class MainActivity extends AppCompatActivity {
    //Simple impl
    public ToggleButton togglebtn,togglebtn2;
    private TextView degrees;
    private ImageButton plusbtn;
    private ImageButton minusbtn;
    private ImageButton settings;
    private static final int cCounter = 24;
    private int counter = cCounter;
    //Global vars
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String TEXT = "text";
    public static final String BTN1 = "btn1";
    public static final String BTN2 = "btn2";
    private boolean btnonoff,btnhotcold;
    private String text;
    private View.OnClickListener clickListener =  new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            switch(v.getId()){
                case R.id.minus :
                    minusDegrees();
                    break;
                case R.id.plus :
                    plusDegrees();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Id views and listeners

        togglebtn = (ToggleButton) findViewById(R.id.togglebtn);
        togglebtn2 = (ToggleButton) findViewById(R.id.togglebtn2);
        degrees = (TextView) findViewById(R.id.degrees);
        minusbtn = (ImageButton) findViewById(R.id.minus);
        minusbtn.setOnClickListener(clickListener);
        plusbtn = (ImageButton) findViewById(R.id.plus);
        plusbtn.setOnClickListener(clickListener);
        settings = (ImageButton) findViewById(R.id.settings);
        //Go to Next Page
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savedata();
                openSettings();
            }
        });
        loadata();
        updateB();
    }
    //Degree management

    private void plusDegrees(){
        if(togglebtn.isChecked()) {
            if (counter < 33) {
                counter++;
                degrees.setText(counter + "");
            }
        }
    }
    private void minusDegrees(){
        if(togglebtn.isChecked()) {
            if (counter > 16) {
                counter--;
                degrees.setText(counter + "");
            }
        }
    }
    //Next page
    public void openSettings(){
        Intent intent = new Intent(this, Settings.class);
        startActivity(intent);
    }
    //Shared Preferences impl
    public void savedata(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(TEXT,degrees.getText().toString());
        editor.putBoolean(BTN1, togglebtn.isChecked());
        editor.putBoolean(BTN2, togglebtn2.isChecked());
        editor.apply();
    }
    public void loadata(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        text = sharedPreferences.getString(TEXT,cCounter + "");
        btnonoff = sharedPreferences.getBoolean(BTN1, false);
        btnhotcold = sharedPreferences.getBoolean(BTN2, false);
    }
    public void updateB(){
        degrees.setText(text);
        togglebtn.setChecked(btnonoff);
        togglebtn2.setChecked(btnhotcold);
        try {
            counter = Integer.parseInt(text);
        }
        catch (NumberFormatException e)
        {
            counter = 24;
        }
    }
}
