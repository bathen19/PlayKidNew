package com.example.playkidSecond;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;

public class SavingOperations extends AppCompatActivity {

    TextView tvSaving, tvAmount;
    Button btnDeposit, btnWithdrawal, btnDelete;

    Map<String, Object> selectedMap;
    Long userMoney;

    Long amount = 0L,goal = 0L;

    private static final String TAG = "SavingOperations";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saving_operations);

        Intent intent = getIntent();
        selectedMap = (Map<String, Object>) intent.getSerializableExtra("selectedMap");
        Log.d(TAG, "onCreate: map from intent " + selectedMap);
        Log.d(TAG, "onCreate: id from intent " + selectedMap.get("id"));

        try {
            if ( (Integer) selectedMap.get("amount") != 0) {
                amount = (Long) selectedMap.get("amount");
            }
        }
        catch (ClassCastException e) {
            if ( (Long) selectedMap.get("amount") != 0L) {
                amount = (Long) selectedMap.get("amount");
            }
        }
        Log.d(TAG, "onCreate: amount is " + amount);
        goal = (Long) selectedMap.get("goal");

        setViews();
        getUserMoney();

    }


    @SuppressLint("SetTextI18n")
    void setViews() {
        // xml to java connection
        tvSaving = findViewById(R.id.tvSaving);
        tvAmount = findViewById(R.id.tvAmount);

        btnDeposit = findViewById(R.id.btnDeposit);
        btnWithdrawal = findViewById(R.id.btnWithdrawal);
        btnDelete = findViewById(R.id.btnDelete);

        tvSaving.setText(tvSaving.getText() + " " + goal + "$");
        tvAmount.setText(tvAmount.getText() + " " + amount + "$");

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Long newMoney = userMoney + amount ;
                updateUserMoney(newMoney, 0L,true);
            }
        });

        btnDeposit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createPopUp("Enter money to deposit to saving",userMoney,true);
            }
        });

        btnWithdrawal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createPopUp("Enter money to Withdrawal from saving", amount, false);
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
                Log.d(TAG, "finishedWithSuccess: user money" + userMoney);
                String money = "Current Money: " + userMoney + "$";

                Log.d(TAG, "finishedWithSuccess: money " + money);

//                tvAmount.setText(money);
            }

            @Override
            public void finishedWithError(String str) {
                Utils.showAlertOk(SavingOperations.this, "Error", str);
            }
        });
    }

    private void updateUserMoney(Long newMoneyUser,Long newMoneySave, boolean ToDelete) {
        FirebaseHandler.updateUserMoney(newMoneyUser,UserSingleton.sherdInstance().getPhoneNumber(), new FirebaseHandler.NetworkSuccessOrNot() {
            @Override
            public void finishedWithSuccess(boolean succes) {
                if (succes) {
                    if (ToDelete) {
                        deleteUserSaving();
                    }
                    else {
                        updateSaveMoney(newMoneySave);
                    }
                }
                else {

                }
            }
        });

    }
    private void updateSaveMoney (Long newMoney) {
        FirebaseHandler.updateSavingMoney((String) selectedMap.get("id"), newMoney, selectedMap, new FirebaseHandler.NetworkSuccessOrNot() {
            @Override
            public void finishedWithSuccess(boolean succes) {
                if (succes) {
                    Intent intent = new Intent(SavingOperations.this, MySaving.class);;
                    startActivity(intent);
                }
            }
        });
    }

    private void deleteUserSaving () {
        Log.d(TAG, "finishedWithSuccess: started deleting saving");
        String idSaving = (String) selectedMap.get("id");
        FirebaseHandler.deleteSaving(idSaving, new FirebaseHandler.NetworkSuccessOrNot() {
            @Override
            public void finishedWithSuccess(boolean succes) {
                if (succes) {
                    Log.d(TAG, "finishedWithSuccess: deleting saving");
                    Intent intent = new Intent(SavingOperations.this, MySaving.class);;
                    startActivity(intent);
                }
                else {

                }
            }
        });
    }

    private void createPopUp(String text, Long maxAllowedMoney, boolean isDeposit) {
        AlertDialog.Builder moneyPopUp = new AlertDialog.Builder(SavingOperations.this);
        View view = LayoutInflater.from(SavingOperations.this).inflate(R.layout.saving_popup, null);
        TextView tvText = view.findViewById(R.id.tvText);
        EditText etSum = view.findViewById(R.id.etSum);
        Button btnSubmit = view.findViewById(R.id.btnSubmit);

        tvText.setText(text);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Long moneyTransferred = Long.valueOf(etSum.getText().toString());
                Long newMoneyAccount, newMoneySaving;

                if (moneyTransferred > maxAllowedMoney) {
                    Utils.showAlertOk(SavingOperations.this,"Too much money","Max money allowed is " + maxAllowedMoney);
                }
                else {
                    if (isDeposit) {
                        newMoneyAccount = userMoney - moneyTransferred;
                        newMoneySaving = moneyTransferred + amount;


                        if (newMoneySaving > goal) {
                            Utils.showAlertOk(SavingOperations.this,"Too much money","Max goal is " + goal);
                        }
                        else {
                            updateUserMoney(newMoneyAccount,newMoneySaving,false);
                        }
                    }
                    else {
                        newMoneyAccount = userMoney + moneyTransferred;
                        newMoneySaving = amount - moneyTransferred;


                        updateUserMoney(newMoneyAccount,newMoneySaving,false);
                    }
                }
            }
        });


        moneyPopUp.setView(view);
        moneyPopUp.setCancelable(true);
        moneyPopUp.show();
    }
}