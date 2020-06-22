package com.norman.calculator.action;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

/**
 * @author Norman Yi
 * @version 1.0
 */
public class preConfig extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences Settings = getSharedPreferences("Settings", MODE_PRIVATE);
        String Mode = Settings.getString("Mode", "Standard");
        assert Mode != null;
        changeMode(Mode);
    }

    private void changeMode(String mode) {
        Intent intent = new Intent();
        switch (mode) {
            case "Standard":
                intent.setClass(preConfig.this, stdCalculator.class);
                startActivity(intent);  //开始跳转
                finish();
                break;
            case "Scientific": {
                intent.setClass(preConfig.this, sciCalculator.class);
                startActivity(intent);  //开始跳转
                finish();
                break;
            }
        }
    }
}