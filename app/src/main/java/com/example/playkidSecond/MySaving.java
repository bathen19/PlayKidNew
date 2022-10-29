package com.example.playkidSecond;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;

public class MySaving extends AppCompatActivity {
    Button btnCreateNewSaving;
//    TextView tvSavings;
    ListView lvSavings;

    ArrayList<String> lvTexts = new ArrayList<>();
    ArrayList<Map<String, Object>> lvMaps = new ArrayList<>();
    ArrayAdapter<String> lvAdapter;


    private static final String TAG = "MySaving";

    Button btnGoHome;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mysaving);
        setview();
        setSavinglistener();
    }

    void setSavinglistener(){
        //FirebaseHandler.createSavingsListener();

        FirebaseHandler.getUserSavings(new FirebaseHandler.NetworkGetData() {
            @Override
            public void finishedWithSuccess(Map<String, Object> map) {

                for (Map.Entry<String, Object> entry : map.entrySet()) {
                    Map<String, Object> innerMap = (Map<String, Object>) entry.getValue();

                    lvMaps.add(innerMap);
//                    String txt = "Saving name: " + innerMap.get("id") + " saving amount: " + innerMap.get("amount") + " saving goal: " + innerMap.get("goal");

                    for (Map.Entry<String, Object> sub_entry : innerMap.entrySet()) {
                        if (sub_entry.getKey().equals("description")) {
                            lvTexts.add(String.valueOf(sub_entry.getValue()));
                        }
                    }

//                    lvTexts.add(String.valueOf(innerMap));

                }


                lvAdapter = new ArrayAdapter<String>(getApplicationContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, lvTexts);
                lvSavings.setAdapter(lvAdapter);

//                tvSavings.setText(stringBuilder.toString());
            }

            @Override
            public void finishedWithError(String str) {
                if (str.equals("No user savings found")) {
                    Utils.showAlertOk(MySaving.this, "Warning", str);
                }
                else {
                    Utils.showAlertOk(MySaving.this, "Error", str);
                }
            }
        });
    }
    void setview(){
//        tvSavings = findViewById(R.id.tvSavings);
        lvSavings = findViewById(R.id.lvSavings);
        btnCreateNewSaving = (Button) findViewById(R.id.btnCreateNewSaving);
        btnCreateNewSaving.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MySaving.this, CreateNewSaving.class);
                startActivity(intent);
//               finish();
            }
        });
        lvSavings.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Map<String, Object> selectedMap = lvMaps.get(i);

                Intent intent = new Intent(MySaving.this, SavingOperations.class);
                intent.putExtra("selectedMap", (Serializable) selectedMap);
                startActivity(intent);
            }
        });

        btnGoHome = findViewById(R.id.btnGoHome);
        btnGoHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MySaving.this, HomePageActivity.class);
                startActivity(intent);
            }
        });
    }

}
