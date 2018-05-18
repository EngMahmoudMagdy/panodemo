package com.magdy.panodemo;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.asha.vrlib.MDVRLibrary;

public class MainActivity extends AppCompatActivity {

    private MDVRLibrary mVRLibrary;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button b = findViewById(R.id.bitmap);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MD360PlayerActivity.startBitmap(MainActivity.this, Uri.parse("file:///android_asset/images/vr_cinema.jpg"));
            }
        });
        Button bb = findViewById(R.id.places);
        bb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(),ListActivity.class);
                startActivity(i);
            }
        });
    }
}
