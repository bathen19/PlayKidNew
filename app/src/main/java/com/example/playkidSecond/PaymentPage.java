package com.example.playkidSecond;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;

public class PaymentPage extends AppCompatActivity {

    String qrCode;

    TextView tvMotivation;
    EditText etMoney;
    Button btnConfirm;

    Long userMoney;
    String motivation;

    Button btnGoHome;

    private static final String TAG = "PaymentPage";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_page);

        Intent intent = getIntent();
        qrCode = intent.getStringExtra("qrCode");

        setViews();
        getUserMoney();
        getQrMotivation();
    }


    public void setViews() {
        tvMotivation = findViewById(R.id.tvMotivation);
        etMoney = findViewById(R.id.etMoney);
        btnConfirm = findViewById(R.id.btnConfirm);

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Long moneyToPay = Long.valueOf(etMoney.getText().toString());

                if (moneyToPay != 0L) {
                    if (moneyToPay <= userMoney) {
                        Long newUserMoney = userMoney - moneyToPay;

                        updateUserMoney(newUserMoney);

                    }
                    else {
                        Utils.showAlertOk(PaymentPage.this, "Error", "Not enough money");
                    }
                }
                else {
                    Utils.showAlertOk(PaymentPage.this, "Error", "Pay minimum is 0");
                }
            }
        });
        btnGoHome = findViewById(R.id.btnGoHome);
        btnGoHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PaymentPage.this, HomePageActivity.class);
                startActivity(intent);
            }
        });
    }

    private void getUserMoney() {
        FirebaseHandler.getUserData(UserSingleton.sherdInstance().getPhoneNumber(), new FirebaseHandler.NetworkGetData() {
            @Override
            public void finishedWithSuccess(Map<String, Object> map) {
                for (Map.Entry<String,Object> entry: map.entrySet()) {
                    if (entry.getKey().equals("amount")) {
                        userMoney = (Long) entry.getValue();
                    }
                }

                Log.d(TAG, "finishedWithSuccess: money" + userMoney);

            }

            @Override
            public void finishedWithError(String str) {
                Utils.showAlertOk(PaymentPage.this, "Error", str);
            }
        });
    }
    private void getQrMotivation() {
        FirebaseHandler.getMotivation(qrCode, new FirebaseHandler.NetworkGetData() {
            @Override
            public void finishedWithSuccess(Map<String, Object> map) {
                for (Map.Entry<String,Object> entry: map.entrySet()) {
                    if (entry.getKey().equals("motivation")) {
                        motivation = (String) entry.getValue();
                        tvMotivation.setText(motivation);
                    }
                }

                Log.d(TAG, "finishedWithSuccess: motivation" + motivation);
            }

            @Override
            public void finishedWithError(String str) {
                Utils.showAlertOk(PaymentPage.this, "Error", str);
            }
        });
    }

    private void updateUserMoney(Long newMoneyUser) {
        FirebaseHandler.updateUserMoney(newMoneyUser,UserSingleton.sherdInstance().getPhoneNumber(), new FirebaseHandler.NetworkSuccessOrNot() {
            @Override
            public void finishedWithSuccess(boolean succes) {
                if (succes) {
                    Toast.makeText(PaymentPage.this, "Payment is successful", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(PaymentPage.this, HomePageActivity.class);
                    startActivity(intent);
                }
                else {

                }
            }
        });

    }
}