package com.example.lab5_3;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {
    Button bt1, bt2;
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
            intent = new Intent(this, MusicService.class);
            bt1 = (Button)findViewById(R.id.btn1);
            bt2 = (Button)findViewById(R.id.btn2);

            bt1.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view){
                    startService(intent);
                    Toast.makeText(getApplicationContext(), "Started", Toast.LENGTH_SHORT).show();
                    finish();
            }
        });
            bt2.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view){
                    stopService(intent);
                    Toast.makeText(getApplicationContext(), "Service Stopped", Toast.LENGTH_SHORT).show();
                }
            });
    }
}