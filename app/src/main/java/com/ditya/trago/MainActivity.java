package com.ditya.trago;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    TextView tv;
    Button New;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv = findViewById(R.id.textView3);
        SharedPreferences dis = getSharedPreferences("dist", MODE_PRIVATE);
        SharedPreferences.Editor editor3 = dis.edit();
        String delta = dis.getString("dis", "");
        tv.setText("Total distance travelled : " + delta);
        New = findViewById(R.id.button3);
        New.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MapsActivity.class));
            }
        });
    }
}