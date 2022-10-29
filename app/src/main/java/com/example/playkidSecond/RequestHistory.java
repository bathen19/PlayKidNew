package com.example.playkidSecond;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Map;

public class RequestHistory extends AppCompatActivity {

    ListView lv;
//    ArrayList<Long> times = new ArrayList<>();
//    ArrayList<Long> sums = new ArrayList<>();
//    ArrayList<Boolean> isPlus = new ArrayList<>();
    ArrayList<String> textsForLv = new ArrayList<>();
    ArrayAdapter<String> adapter;


    private static final String TAG = "RequestHistory";

    Button btnGoHome;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_history);

        lv = findViewById(R.id.lv);

        btnGoHome = findViewById(R.id.btnGoHome);
        btnGoHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RequestHistory.this, HomePageActivity.class);
                startActivity(intent);
            }
        });


        getHistoryList();
    }

    private void getHistoryList() {
        FirebaseHandler.getList("history", new FirebaseHandler.NetworkGetData() {
            @Override
            public void finishedWithSuccess(Map<String, Object> map) {
                for (Map.Entry<String, Object> entry : map.entrySet()) {
                    Map<String, Object> innerMap = (Map<String, Object>) entry.getValue();

//                    times.add(Long.valueOf(entry.getKey()));
//                    sums.add((Long) innerMap.get("sum"));
//                    isPlus.add((Boolean) innerMap.get("isPlus"));

                    String text = ((Boolean) innerMap.get("isPlus")) ? "+" : "-" + " ";

                    text += innerMap.get("sum") + "$";
                    Log.d(TAG, "finishedWithSuccess: " + text);
                    Log.d(TAG, "finishedWithSuccess: " + innerMap.get("sum"));
                    textsForLv.add(text);

                }
                adapter = new ArrayAdapter<String>(getApplicationContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, textsForLv);
                lv.setAdapter(adapter);
            }

            @Override
            public void finishedWithError(String str) {

            }
        });
    }
}