package com.example.playkidSecond;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ShareMoneyActivity extends AppCompatActivity {
    EditText txtPhone, txtDescriptionChild,txtDescriptionParent;
    Button btnShare;
    ListView listView;
    ArrayList<String> arr = new ArrayList<>();

    Button btnGoHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_money);
        setview();
        getChildrenListData();
    }

    void getChildrenListData() {
        FirebaseHandler.getList("children", new FirebaseHandler.NetworkGetData() {
            @Override
            public void finishedWithSuccess(Map<String, Object> map) {
                for (Map.Entry<String, Object> entry : map.entrySet()) {
                    System.out.println("Key = " + entry.getKey() +
                            ", Value = " + entry.getValue());

                    Map<String, Object> innerMap = (Map<String, Object>) entry.getValue();
                    arr.add((String) innerMap.get("descriptionChild"));

                }
                setListViewAdapter();

            }

            @Override
            public void finishedWithError(String str) {
                //Utils.showAlertOk(ShareMoneyActivity.this, "Error", str);
            }
        });
    }

    void setview() {
        listView = (ListView) findViewById(R.id.list);

//        // Defined Array values to show in ListView
//        String[] values = new String[] { "Android List View",
//                "Adapter implementation",
//                "Simple List View In Android",
//                "Create List View Android",
//                "Android Example",
//                "List View Source Code",
//                "List View Array Adapter",
//                "Android Example List View"
//        };
//
//
//
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
//                android.R.layout.simple_list_item_1, android.R.id.text1, values);
//
//        listView.setAdapter(adapter);

        // ListView Item Click Listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                // ListView Clicked item index
                int itemPosition = position;

                // ListView Clicked item value
                String itemValue = (String) listView.getItemAtPosition(position);

                // Show Alert
                Toast.makeText(getApplicationContext(),
                                "Position :" + itemPosition + "  ListItem : " + itemValue, Toast.LENGTH_LONG)
                        .show();

            }

        });


        txtDescriptionChild = (EditText) findViewById(R.id.txtDescriptionChild);
        txtDescriptionParent = findViewById(R.id.txtDescriptionParent);
        txtPhone = (EditText) findViewById(R.id.txtPhone);

        btnShare = (Button) findViewById(R.id.btnShare);
        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Map<String, Object> mapIn = new HashMap<>();
                mapIn.put("phone", txtPhone.getText().toString());
                mapIn.put("descriptionChild", txtDescriptionChild.getText().toString());
                mapIn.put("descriptionParent", txtDescriptionParent.getText().toString());

                Map<String, Object> mapID = new HashMap<>();
                mapID.put(txtPhone.getText().toString(), mapIn);

                FirebaseHandler.createNewUserToShareMoneyWith(mapID, new FirebaseHandler.NetworkSuccessOrNot() {
                    @Override
                    public void finishedWithSuccess(boolean succes) {
                        if (!succes) {
                            Utils.showAlertOk(ShareMoneyActivity.this, "Error", "Could not share money with this user");
                        } else {
                            // create new info in child
                            Map<String, Object> mapIn = new HashMap<>();
                            mapIn.put("phone", UserSingleton.sherdInstance().getPhoneNumber());
                            mapIn.put("descriptionChild", txtDescriptionChild.getText().toString());
                            mapIn.put("descriptionParent", txtDescriptionParent.getText().toString());


                            Map<String, Object> mapIDChild = new HashMap<>();
                            mapIDChild.put(UserSingleton.sherdInstance().getPhoneNumber(), mapIn);

                            FirebaseHandler.createNewSponserForChild(txtPhone.getText().toString(), mapIDChild, new FirebaseHandler.NetworkSuccessOrNot() {
                                @Override
                                public void finishedWithSuccess(boolean succes) {
                                    if (!succes) {
                                        Utils.showAlertOk(ShareMoneyActivity.this, "Error", "Could not update child with share description");
                                    } else {
                                        finish();
                                    }
                                }
                            });
//
                        }
                    }
                });
            }
        });
        btnGoHome = findViewById(R.id.btnGoHome);
        btnGoHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ShareMoneyActivity.this, HomePageActivity.class);
                startActivity(intent);
            }
        });
    }

    void setListViewAdapter() {
        // Defined Array values to show in ListView
//        String[] values = new String[] { "Android List View",
//                "Adapter implementation",
//                "Simple List View In Android",
//                "Create List View Android",
//                "Android Example",
//                "List View Source Code",
//                "List View Array Adapter",
//                "Android Example List View"
//        };


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, arr);

        listView.setAdapter(adapter);
    }

}