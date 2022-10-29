package com.example.playkidSecond;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CreateNewSaving extends AppCompatActivity {
    Button btnConfirmation;
    EditText txtSavingName,txtSavingGoal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_new_saving);
        setview();
    }

    void setview(){

        txtSavingGoal = (EditText) findViewById(R.id.txtSavingGoal);
        txtSavingName = (EditText) findViewById(R.id.txtSavingName);

        btnConfirmation = (Button) findViewById(R.id.btnConfirmation);
        btnConfirmation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(validateFields()){
                    String uniquId = Utils.getRandomString(14);
                    Map<String,Object> savingData = new HashMap<>();
                    savingData.put("createdDate",new Date());
                    savingData.put("description",txtSavingName.getText().toString());
                    savingData.put("goal",Long.parseLong(txtSavingGoal.getText().toString()));
                    savingData.put("amount",0);
                    savingData.put("id",uniquId);

                    Map<String,Object> mapID = new HashMap<>();
                    mapID.put(uniquId,savingData);

                    FirebaseHandler.createNewSaving(mapID, new FirebaseHandler.NetworkSuccessOrNot() {
                        @Override
                        public void finishedWithSuccess(boolean succes) {
                            if (succes){

                                Intent intent = new Intent(CreateNewSaving.this, SavingOperations.class);
                                intent.putExtra("selectedMap", (Serializable) savingData);
                                startActivity(intent);
                            }
                            else{
                                Utils.showAlertOk(CreateNewSaving.this,"Save Error",
                                        "Could not save new saving info in server.\nplease try again later.");
                            }
                        }
                    });
                }
//               finish();
            }
        });
    }

    Boolean validateFields(){

        if (!txtSavingName.getText().toString().trim().isEmpty() && !txtSavingGoal.getText().toString().trim().isEmpty()
                && Integer.parseInt(txtSavingGoal.getText().toString()) > 0){
            return true;
        }
        else{
            Utils.showAlertOk(this,"Missing data","Must add data to all fields");
            return  false;
        }
    }
}