package thegame.thegame;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

public class HomeScreen extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen);

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
                    startActivity(map);
                    finish();
                }
            }
        });




    }

}
