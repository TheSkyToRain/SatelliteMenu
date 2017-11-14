package com.ebaryice.satellitemenu;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    SatelliteMenu satelliteMenu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        satelliteMenu = findViewById(R.id.satelliteMenu);
        satelliteMenu.setToggleMenuDuration(3000);
    }
}
