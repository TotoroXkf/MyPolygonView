package com.totoro.xkf.sample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import com.totoro.xkf.polygonview.PolygonView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PolygonView polygonView = findViewById(R.id.pv_polygon_view);
        List<Float> pointValue = new ArrayList<>();
        for (int i = 0; i < polygonView.getEageCount(); i++) {
            pointValue.add((float) (Math.random() * 1));
        }
        polygonView.setPointValue(pointValue);
        List<String> pointName = new ArrayList<>();
        pointName.add("Activity");
        pointName.add("BroadcastReceiver");
        pointName.add("ContentProvider");
        pointName.add("Service");
        pointName.add("View");
        pointName.add("Handle");
        polygonView.setPointName(pointName);
        polygonView.draw();
    }
}
