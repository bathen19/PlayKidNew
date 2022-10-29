package com.example.playkidSecond;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Map;

public class AcceptRequestMoney extends AppCompatActivity {

    ListView lv;

    ArrayList<String> arrayListPhones = new ArrayList<>();
    ArrayList<Long> arrayListMoney = new ArrayList<>();
    ArrayList<String> arrayListTexts = new ArrayList<>();
    ArrayAdapter<String> adapter;

    Long currentUserMoney = 0L, askingUserMoney = 0L;
    Long newMoneyRequest;
    Long newMoneyGiver;

    Button btnGoHome;

    private static final String TAG = "AcceptRequestMoney";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accept_request_money);

        lv = findViewById(R.id.lv);

        btnGoHome = findViewById(R.id.btnGoHome);
        btnGoHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AcceptRequestMoney.this, HomePageActivity.class);
                startActivity(intent);
            }
        });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (currentUserMoney >= arrayListMoney.get(i)) {
                    //decrease this user money and increase asking user money
                    newMoneyGiver = currentUserMoney - arrayListMoney.get(i);

                    //TODO: COMPLETE
                    newMoneyRequest = arrayListMoney.get(i);

                    FirebaseHandler.updateUserMoney(newMoneyGiver, UserSingleton.sherdInstance().getPhoneNumber(), new FirebaseHandler.NetworkSuccessOrNot() {
                        @Override
                        public void finishedWithSuccess(boolean succes) {
                            if (succes) {
                                FirebaseHandler.getUserData(arrayListPhones.get(i), new FirebaseHandler.NetworkGetData() {
                                    @Override
                                    public void finishedWithSuccess(Map<String, Object> map) {
                                        for (Map.Entry<String,Object> entry: map.entrySet()) {
                                            if (entry.getKey().equals("amount")) {
                                                askingUserMoney = (Long) entry.getValue();

                                                newMoneyRequest += askingUserMoney;
                                                updateMoneyAsker(i);

                                            }
                                        }
                                    }

                                    @Override
                                    public void finishedWithError(String str) {

                                    }
                                });
                            }
                            else {

                            }
                        }
                    });

                }
                else {
                    Utils.showAlertOk(AcceptRequestMoney.this, "Error", "Not enough money");
                }
            }
        });

        getMoneyRequestList();
        getUserMoney();


    }

    private void getMoneyRequestList() {
        FirebaseHandler.getList("moneyRequest", new FirebaseHandler.NetworkGetData() {
            @Override
            public void finishedWithSuccess(Map<String, Object> map) {
                for (Map.Entry<String, Object> entry : map.entrySet()) {

                    Map<String, Object> innerMap = (Map<String, Object>) entry.getValue();

                    boolean didAccept = (boolean) innerMap.get("didAccept");
                    if (!didAccept) {
                        arrayListPhones.add((String) innerMap.get("phone"));
                        arrayListMoney.add((Long) innerMap.get("sum"));

                        Log.d(TAG, "finishedWithSuccess: " + String.valueOf(innerMap.get("phone")));

                        String textForLv =  innerMap.get("phone") + "(" + innerMap.get("description") + ")" + " requested " + innerMap.get("sum") + "$";
                        arrayListTexts.add(textForLv);
                    }

                }

                adapter = new ArrayAdapter<String>(getApplicationContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, arrayListTexts);
                lv.setAdapter(adapter);
            }

            @Override
            public void finishedWithError(String str) {

            }
        });
    }
    private void getUserMoney() {
        FirebaseHandler.getUserData(UserSingleton.sherdInstance().getPhoneNumber(), new FirebaseHandler.NetworkGetData() {
            @Override
            public void finishedWithSuccess(Map<String, Object> map) {
                for (Map.Entry<String,Object> entry: map.entrySet()) {
                    if (entry.getKey().equals("amount")) {
                        currentUserMoney = (Long) entry.getValue();

                    }
                }
            }

            @Override
            public void finishedWithError(String str) {
                Utils.showAlertOk(AcceptRequestMoney.this, "Error", str);
            }
        });
    }

    private void updateMoneyAsker(int index) {


        FirebaseHandler.updateUserMoney(newMoneyRequest, arrayListPhones.get(index), new FirebaseHandler.NetworkSuccessOrNot() {
            @Override
            public void finishedWithSuccess(boolean succes) {

                if (succes) {
                    updateRequest(index);
                }
                else {

                }
            }
        });
    }

    private void updateRequest(int index) {
        FirebaseHandler.updateRequestState(arrayListPhones.get(index), arrayListMoney.get(index), new FirebaseHandler.NetworkSuccessOrNot() {
            @Override
            public void finishedWithSuccess(boolean succes) {
                if (succes) {
                    updateHistory(index);
                }
                else {

                }
            }
        });
    }

    private void updateHistory(int index) {
        //Need to update history of accepting user and asking user

        //Start with accepting user
        FirebaseHandler.addHistoryMoneyRequest(UserSingleton.sherdInstance().getPhoneNumber(), arrayListMoney.get(index),
                false, new FirebaseHandler.NetworkSuccessOrNot() {
                    @Override
                    public void finishedWithSuccess(boolean succes) {
                        if (succes) {
                            //Now asking user
                            FirebaseHandler.addHistoryMoneyRequest(arrayListPhones.get(index), arrayListMoney.get(index),
                                    true, new FirebaseHandler.NetworkSuccessOrNot() {
                                        @Override
                                        public void finishedWithSuccess(boolean succes) {
                                            Intent intent = new Intent(AcceptRequestMoney.this, HomePageActivity.class);
                                            startActivity(intent);
                                        }
                                    });
                        }
                    }
                });
    }
}
