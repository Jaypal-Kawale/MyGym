package com.example.mygym;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class check extends AppCompatActivity {
Button client,gymowner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check);
        client=findViewById(R.id.reg_client);
        gymowner=findViewById(R.id.reg_gymowner);
        client.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(check.this,signup.class);
                intent.putExtra("mode",0);
                startActivity(intent);
            }
        });
        gymowner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(check.this,signup.class);
                intent.putExtra("mode",1);
                startActivity(intent);
            }
        });
    }
}