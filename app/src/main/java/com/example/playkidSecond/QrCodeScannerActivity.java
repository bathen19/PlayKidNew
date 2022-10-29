package com.example.playkidSecond;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.zxing.Result;

public class QrCodeScannerActivity extends AppCompatActivity {
  //  private PreviewView previewView;
  //  private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;

//    private Button qrCodeFoundButton;

    //New approach:
    private CodeScanner mCodeScanner;


    CodeScannerView scannerView;

    private static final String TAG = "QrCodeScannerActivity";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code_scanner);
        setView();
    }

    void setView(){
    //    previewView = findViewById(R.id.activity_main_previewView);
    //    cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        //startCamera();

        scannerView = findViewById(R.id.scanner_view);
        mCodeScanner = new CodeScanner(this, scannerView);


        //from library

        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String qrCode = result.getText();
                        Log.d(TAG, "run: qr code from camera " + qrCode);

                        Intent intent = new Intent(QrCodeScannerActivity.this, PaymentPage.class);
                        intent.putExtra("qrCode" , qrCode);
                        startActivity(intent);
                    }
                });
            }
        });

        scannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCodeScanner.startPreview();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCodeScanner.startPreview();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mCodeScanner.releaseResources();
    }
}
