package com.securityKenya.co.ke.securitysystem;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private CardView dashboardAct;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        dashboardAct = (CardView) findViewById(R.id.action_emergency_alert);
        dashboardAct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(MainActivity.this, DashboardActivity.class);
                MainActivity.this.startActivity(myIntent);
            }
        });
    }
}
