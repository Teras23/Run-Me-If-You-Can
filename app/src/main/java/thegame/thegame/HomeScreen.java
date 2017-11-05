package thegame.thegame;


import android.Manifest;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

public class HomeScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen);

        final List<String> requiredSDKPermissions = new ArrayList<String>();
        requiredSDKPermissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        requiredSDKPermissions.add(Manifest.permission.INTERNET);
        requiredSDKPermissions.add(Manifest.permission.ACCESS_WIFI_STATE);
        requiredSDKPermissions.add(Manifest.permission.ACCESS_NETWORK_STATE);
        requiredSDKPermissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        ActivityCompat.requestPermissions(this,
                requiredSDKPermissions.toArray(new String[requiredSDKPermissions.size()]),
                1);

        final RelativeLayout dialog = findViewById(R.id.dialogview);
        dialog.setVisibility(View.INVISIBLE);

        Button button = findViewById(R.id.reg);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.setVisibility(View.VISIBLE);
            }
        });

        final EditText username = findViewById(R.id.idUsername);

        Button cancel = findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.setVisibility(View.INVISIBLE);
            }
        });

        Button play = findViewById(R.id.play);
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            Intent map = new Intent(HomeScreen.this, MainActivity.class);
            if (username.getText().toString().isEmpty()){
                username.setHint("Put in username");
            } else {
                map.putExtra("Name", username.getText().toString());
                startActivity(map);
                finish();
            }
            }
        });
    }
}
