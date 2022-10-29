package com.example.playkidSecond;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

public class SmsAuthenticationActivity extends AppCompatActivity {

    Button btnSend;
    EditText editTxtCode;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
String verificationId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication_page2);
        getIntentData();
        setView();
    }

    void getIntentData(){
        String value;
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            verificationId = bundle.getString("verificationId");
            Log.d("App","verification code:"+verificationId);
        }
    }

    void setView() {
        editTxtCode = (EditText) findViewById(R.id.editTxtCode);
        btnSend = (Button) findViewById(R.id.btnSend);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String code = editTxtCode.getText().toString();
                try {
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
                    signInWithPhoneAuthCredential(credential);
                }
                catch (Exception ex){
                    Log.d("App",ex.getMessage());
                }

            }
        });
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("App", "signInWithCredential:success");

                            FirebaseUser user = task.getResult().getUser();
                            Log.d("App",user.getPhoneNumber());


                            Intent returnIntent = new Intent();
//                            returnIntent.putExtra("result",result);
                            setResult(Activity.RESULT_OK,returnIntent);
                            finish();

                            // Update UI
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w("App", "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }

                            Utils.showAlertOk(SmsAuthenticationActivity.this,"Exception",task.getException().getMessage());
                        }
                    }
                });
    }

}