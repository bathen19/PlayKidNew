package com.example.playkidSecond;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Map;

public class HomePageActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST = 100;
    Button btnSavingDeposit, btnRequestMoney, btnPaymentInStore, btnQr, btn_share_money, btnLogout, btnAcceptRequests, btnHistory;
    TextView tvKupa;
    TextView tvHello;
    Button btnAddMoney;

    boolean isParent = false;
    boolean isCamera = false;

    Long userMoney = 0L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        setview();
        getUserMoneyAndStatus();
        getLastHistory();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        Log.d("App", "user:" + user.getPhoneNumber());
    }

    void setview() {

        tvKupa = findViewById(R.id.tvKupa);
        btnLogout = (Button) findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseHandler.logout(HomePageActivity.this);
                Intent intent = new Intent(HomePageActivity.this, LoginPage.class);
                startActivity(intent);
                finish();
            }
        });


        btnQr = (Button) findViewById(R.id.qr_button);
        btnQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                isCamera = true;
                requestPermission();

            }
        });
        btnSavingDeposit = (Button) findViewById(R.id.btnSavingDeposit);
        btnSavingDeposit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomePageActivity.this, MySaving.class);
                startActivity(intent);
            }
        });
        btnRequestMoney = (Button) findViewById(R.id.btnRequestMoney);
        btnRequestMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(HomePageActivity.this, RequestMoney.class);
//                intent.putExtra("isParent", isParent);
//                startActivity(intent);
                isCamera = false;
                requestPermission();
            }
        });
//        btnPaymentInStore = (Button) findViewById(R.id.btnPaymentInStore);
//        btnPaymentInStore.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(HomePageActivity.this, PaymentToTheStore.class);
//                startActivity(intent);
//            }
//        });

        btn_share_money = (Button) findViewById(R.id.btn_share_money);
        btn_share_money.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomePageActivity.this, ShareMoneyActivity.class);
                startActivity(intent);
            }
        });

        btnAcceptRequests = findViewById(R.id.btnAcceptRequests);
        btnAcceptRequests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomePageActivity.this, AcceptRequestMoney.class);
                startActivity(intent);
            }
        });
        btnHistory = findViewById(R.id.btnHistory);
        btnHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomePageActivity.this, RequestHistory.class);
                startActivity(intent);
            }
        });

        tvHello = findViewById(R.id.tvHello);
        btnAddMoney = findViewById(R.id.btnAddMoney);
        btnAddMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createPopUp();
            }
        });
    }

    private void requestPermission() {
        String[] perm;
        if (isCamera) {
            perm = new String[]{Manifest.permission.CAMERA};
        }
        else {
            perm = new String[]{Manifest.permission.SEND_SMS};
        }

        if (ActivityCompat.checkSelfPermission(this, perm[0]) == PackageManager.PERMISSION_GRANTED) {
            if (isCamera) {
                openQrCodeActivity();
            }
            else {
                openRequestActivity();
            }

        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, perm[0])) {
                ActivityCompat.requestPermissions(HomePageActivity.this, perm, PERMISSION_REQUEST);
            } else {
                ActivityCompat.requestPermissions(this, perm, PERMISSION_REQUEST);
            }
        }
    }

    void openQrCodeActivity() {
        Intent intent = new Intent(HomePageActivity.this, QrCodeScannerActivity.class);
        startActivity(intent);
    }
    void openRequestActivity() {
        Intent intent = new Intent(HomePageActivity.this, RequestMoney.class);
        intent.putExtra("isParent", isParent);
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                startCamera();
                if (isCamera) {
                    openQrCodeActivity();
                }
                else {
                    openRequestActivity();
                }
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    //TODO: ADDED BY ME

    public void getUserMoneyAndStatus() {
        FirebaseHandler.getUserData(UserSingleton.sherdInstance().getPhoneNumber(), new FirebaseHandler.NetworkGetData() {
            @Override
            public void finishedWithSuccess(Map<String, Object> map) {
                String money = "";

                for (Map.Entry<String,Object> entry: map.entrySet()) {
                    if (entry.getKey().equals("amount")) {
                        userMoney = (Long) entry.getValue();
                        money = Long.toString((Long) entry.getValue());
                    }
                    else if (entry.getKey().equals("isParent")) {
                        isParent = (Boolean) entry.getValue();
                        makeButtonsInvisible();
                    }
                }
                money = money + "$";

                tvKupa.setText(money);
            }

            @Override
            public void finishedWithError(String str) {
                Utils.showAlertOk(HomePageActivity.this, "Error", str);
            }
        });
    }

    private void makeButtonsInvisible() {
        if (isParent) {
            btnSavingDeposit.setVisibility(View.INVISIBLE);
            //btnPaymentInStore.setVisibility(View.INVISIBLE);
            btnQr.setVisibility(View.INVISIBLE);
            btnRequestMoney.setText("request money from child");
            tvHello.setText("Hello parent");
        }
        else {
            btn_share_money.setVisibility(View.INVISIBLE);
            btnAddMoney.setVisibility(View.INVISIBLE);
            btnRequestMoney.setText("request money from parent");
            tvHello.setText("Hello child");
        }
    }
    private void createPopUp() {
        AlertDialog.Builder moneyPopUp = new AlertDialog.Builder(HomePageActivity.this);
        View view = LayoutInflater.from(HomePageActivity.this).inflate(R.layout.saving_popup, null);
        TextView tvText = view.findViewById(R.id.tvText);
        EditText etSum = view.findViewById(R.id.etSum);
        Button btnSubmit = view.findViewById(R.id.btnSubmit);

        tvText.setText("How much money you want to add?");


        moneyPopUp.setView(view);
        moneyPopUp.setCancelable(true);
        final AlertDialog cancellable = moneyPopUp.show();

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Long moneyToAdd = Long.valueOf(etSum.getText().toString());
                Long newMoneyAccount = userMoney + moneyToAdd;

                FirebaseHandler.updateUserMoney(newMoneyAccount, UserSingleton.sherdInstance().getPhoneNumber(), new FirebaseHandler.NetworkSuccessOrNot() {
                    @Override
                    public void finishedWithSuccess(boolean succes) {
                        if (succes) {
                            Toast.makeText(HomePageActivity.this, "Money addition ended successfully", Toast.LENGTH_SHORT).show();
                            tvKupa.setText(newMoneyAccount + "$");
                            cancellable.dismiss();
                        }
                        else {
                            Toast.makeText(HomePageActivity.this, "Money addition failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        //moneyPopUp.show();
    }

    private void getLastHistory() {
        FirebaseHandler.getList("history", new FirebaseHandler.NetworkGetData() {
            @Override
            public void finishedWithSuccess(Map<String, Object> map) {

                for (Map.Entry<String, Object> entry : map.entrySet()) {
                    Map<String, Object> innerMap = (Map<String, Object>) entry.getValue();

//                    times.add(Long.valueOf(entry.getKey()));
//                    sums.add((Long) innerMap.get("sum"));
//                    isPlus.add((Boolean) innerMap.get("isPlus"));
                    if ((boolean) innerMap.get("isPlus")) {
                        Long sum = (Long) innerMap.get("sum");

                        Toast.makeText(HomePageActivity.this, "Your request of " + sum + "$ was accepted", Toast.LENGTH_SHORT).show();
                    }
                    break;

                }
            }

            @Override
            public void finishedWithError(String str) {

            }
        });
    }


}