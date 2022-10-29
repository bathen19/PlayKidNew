package com.example.playkidSecond;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Switch;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class LoginPage extends AppCompatActivity {
    Button btnSendNumber;
    EditText editTextPhone;
    //Switch switchParentChild;
    RadioButton rbParent,rbChild;
    boolean isParent = false;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);
        getUserIfExist();
        setFirebaseAuthCallbackParam();
        setView();
    }

    void getUserIfExist() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            openHomepageActivity();
        }

    }

    void setView() {

        //btnTest = (Button) findViewById(R.id.btnTest);
//        btnTest.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                openAuthenticationActivity("stam");
//            }
//        });

        btnSendNumber = (Button) findViewById(R.id.btnSendNumber);
        editTextPhone = (EditText) findViewById(R.id.editTextPhone);
        btnSendNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (rbChild.isChecked() || rbParent.isChecked()) {
                    sendUserPhoneNumber(editTextPhone.getText().toString());
                }
            }
        });
        //switchParentChild = findViewById(R.id.switchParentChild);
        rbParent = findViewById(R.id.rbParent);
        rbChild = findViewById(R.id.rbChild);
    }

    void sendUserPhoneNumber(String phoneNumber) {
        try {
            PhoneAuthOptions options =
                    PhoneAuthOptions.newBuilder(mAuth)
                            .setPhoneNumber(phoneNumber)       // Phone number to verify
                            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                            .setActivity(this)                 // Activity (for callback binding)
                            .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                            .build();
            PhoneAuthProvider.verifyPhoneNumber(options);
        } catch (Exception ex) {
            Log.d("App", ex.getMessage().toString());
        }
    }

    void setFirebaseAuthCallbackParam() {
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.d("app", "onVerificationCompleted:" + credential);

//                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w("app", "onVerificationFailed", e);

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                }
                Utils.showAlertOk(LoginPage.this, "Exception", e.getMessage());
                // Show a message and update the UI
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d("App", "onCodeSent:" + verificationId);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        openAuthenticationActivity(verificationId);
                    }
                });
            }
        };
    }

    void openAuthenticationActivity(String verificationId) {
        Intent intent = new Intent(LoginPage.this, SmsAuthenticationActivity.class);
        intent.putExtra("verificationId", verificationId);
        authenticationActivityResultLauncher.launch(intent);
    }

    // You can do the assignment inside onAttach or onCreate, i.e, before the activity is displayed
    ActivityResultLauncher<Intent> authenticationActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {

                        isParent = rbParent.isChecked();
                        addUserToFireStore();
                    }
                }
            });

    void openHomepageActivity() {
        Intent intent = new Intent(LoginPage.this, HomePageActivity.class);
        startActivity(intent);
        finish();
    }

    //TODO: Added by me

    private void addUserToFireStore() {
        FirebaseHandler.addUser(isParent, new FirebaseHandler.NetworkSuccessOrNot() {
            @Override
            public void finishedWithSuccess(boolean succes) {
                if (succes) {
                    openHomepageActivity();
                }
                else{
                    Utils.showAlertOk(LoginPage.this,"Save Error",
                            "Could not save new saving info in server.\nplease try again later.");
                }
            }
        });
    }
}