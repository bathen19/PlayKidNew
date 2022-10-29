package com.example.playkidSecond;

import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;

public class RequestMoney extends AppCompatActivity {

    ListView lv;
    TextView tvPhone;
    EditText etMoney;
    Button btnRequest;
    Button btnGoHome;

    boolean isParent = false;
    ArrayList<String> allPhoneNumbers = new ArrayList<>();
    ArrayList<String> allDescriptions = new ArrayList<>();
    ArrayList<String> allTexts = new ArrayList<>();
    ArrayAdapter<String> adapter;
    String chosenPhone = "";
    String chosenDescription = "";
    Long money = 0L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_money);


        //Get parent / child
        Intent intent = getIntent();
        isParent = intent.getBooleanExtra("isParent",false);

        setview();
        getListData();



    }

    private void setview() {
        lv = findViewById(R.id.lv);
        tvPhone = findViewById(R.id.tvPhone);
        etMoney = findViewById(R.id.etMoney);
        btnRequest = findViewById(R.id.btnRequest);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                chosenPhone = allPhoneNumbers.get(i);
                chosenDescription = allDescriptions.get(i);
                tvPhone.setText(chosenPhone);
            }
        });

        btnRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etMoney.getText().toString().equals("")) {
                    Utils.showAlertOk(RequestMoney.this, "Error", "Enter more than 0 money");
                } else {
                    money = Long.valueOf(etMoney.getText().toString());

                    if (money > 0) {
                        if (!chosenPhone.isEmpty()) {
                            requestMoney();
                        } else {
                            Utils.showAlertOk(RequestMoney.this, "Error", "Enter phone");
                        }
                    } else {
                        Utils.showAlertOk(RequestMoney.this, "Error", "Enter more than 0 money");
                    }
                }
            }
        });
        btnGoHome = findViewById(R.id.btnGoHome);
        btnGoHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RequestMoney.this, HomePageActivity.class);
                startActivity(intent);
            }
        });
    }
    void getListData() {
        String documentPath = isParent ? "children" : "getMoneyFrom";

        FirebaseHandler.getList(documentPath, new FirebaseHandler.NetworkGetData() {
            @Override
            public void finishedWithSuccess(Map<String, Object> map) {
                for (Map.Entry<String, Object> entry : map.entrySet()) {

                    Map<String, Object> innerMap = (Map<String, Object>) entry.getValue();
                    String phone = (String) innerMap.get("phone");
                    String descriptionChild = (String) innerMap.get("descriptionChild");
                    String descriptionParent = (String) innerMap.get("descriptionParent");

                    allPhoneNumbers.add(phone);

                    if(isParent) {
                        allDescriptions.add(descriptionParent);
                        allTexts.add(phone + " " + descriptionChild);
                    }
                    else {
                        allDescriptions.add(descriptionChild);
                        allTexts.add(phone + " " + descriptionParent);
                    }

                }

                adapter = new ArrayAdapter<String>(getApplicationContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, allTexts);
                lv.setAdapter(adapter);

            }

            @Override
            public void finishedWithError(String str) {
                Utils.showAlertOk(RequestMoney.this, "Error", str);
            }
        });
    }
    void requestMoney() {
        FirebaseHandler.changeMoneyRequest(chosenPhone, money, chosenDescription, new FirebaseHandler.NetworkSuccessOrNot() {
            @Override
            public void finishedWithSuccess(boolean succes) {
                if (succes) {
                    sendSms();

                    Toast.makeText(RequestMoney.this, "Sended request successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RequestMoney.this, HomePageActivity.class);
                    startActivity(intent);
                }
                else {

                }
            }
        });
    }
    private void sendSms() {
        try {
            String textSms = UserSingleton.sherdInstance().getPhoneNumber() + "has requested " + money + "$" + " in order to approve it, open tha page 'see money requests'";

            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(chosenPhone, null, textSms, null, null);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}