package com.example.playkidSecond;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class FirebaseHandler {

    private static final String TAG = "FirebaseHandler";


    interface NetworkSuccessOrNot{
        void finishedWithSuccess(boolean succes);
    }

    interface NetworkGetData{
        void finishedWithSuccess(Map<String,Object> map);
        void finishedWithError(String str);
    }

    public static void logout(Context context) {
        FirebaseAuth.getInstance().signOut();
    }
    
public static void createNewSaving(Map<String, Object> data,NetworkSuccessOrNot callback) {
    Log.d("App","user phone:"+UserSingleton.sherdInstance().getPhoneNumber());
    CollectionReference usersCollection = FirebaseFirestore.getInstance().collection(UserSingleton.sherdInstance().getPhoneNumber());
    usersCollection.document("savings")
            .set(data, SetOptions.merge())
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d("Debug", "DocumentSnapshot successfully written!");
                    callback.finishedWithSuccess(true);
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w("Debug", "Error writing document", e);
                    callback.finishedWithSuccess(false);
                }
            });
}


    public static void createNewUserToShareMoneyWith(Map<String, Object> data,NetworkSuccessOrNot callback) {
        Log.d("App","user phone:"+UserSingleton.sherdInstance().getPhoneNumber());
        CollectionReference usersCollection = FirebaseFirestore.getInstance().collection(UserSingleton.sherdInstance().getPhoneNumber());
        usersCollection.document("children")
                .set(data, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Debug", "DocumentSnapshot successfully written!");
                        callback.finishedWithSuccess(true);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Debug", "Error writing document", e);
                        callback.finishedWithSuccess(false);
                    }
                });
    }

    public static void createNewSponserForChild(String childPhone, Map<String, Object> data,NetworkSuccessOrNot callback) {
//
        CollectionReference usersCollection = FirebaseFirestore.getInstance().collection(childPhone);
        usersCollection.document("getMoneyFrom")
                .set(data, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Debug", "DocumentSnapshot successfully written!");
                        callback.finishedWithSuccess(true);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Debug", "Error writing document", e);
                        callback.finishedWithSuccess(false);
                    }
                });
    }
    public static void getList(String documentPath, NetworkGetData callBack ) {
        CollectionReference usersCollection = FirebaseFirestore.getInstance().collection(UserSingleton.sherdInstance().getPhoneNumber());

        //String documentPath = isParent ? "children" : "getMoneyFrom";

        FirebaseFirestore.getInstance().collection(UserSingleton.sherdInstance().getPhoneNumber()).document(documentPath).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (!documentSnapshot.exists()) {
                            Log.d("App", "onSuccess: LIST EMPTY");
                            callBack.finishedWithError("Document data empty");
                            return;
                        } else {
                            Log.d("App", "onSuccess: " );
                            callBack.finishedWithSuccess(documentSnapshot.getData());
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("App", "onFailure : " );
                        callBack.finishedWithError(e.getMessage());
                    }
                });
    }

    //TODO: Added by me

    public static void addUser(boolean isParent, NetworkSuccessOrNot callback) {
        CollectionReference collectionReference = FirebaseFirestore.getInstance().collection(UserSingleton.sherdInstance().getPhoneNumber());

        Map<String, Object> userData = new HashMap<>();
        userData.put("amount",100);
        userData.put("isParent",isParent);

        collectionReference.document("data")
                .set(userData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        callback.finishedWithSuccess(true);

                        Log.d(TAG, "onSuccess: user added successfully");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callback.finishedWithSuccess(false);

                        Log.d(TAG, "onFailure: User adding failed");
                    }
                });
    }
    public static void getUserData(String userPhone, NetworkGetData callback) {
        CollectionReference collectionReference = FirebaseFirestore.getInstance().collection(userPhone);

        collectionReference.document("data").get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (!documentSnapshot.exists()) {
                            Log.d(TAG, "onSuccess: Getting user kupa failed");
                            callback.finishedWithError("Getting user kupa failed");
                        } else {
                            Log.d(TAG, "onSuccess: Getting user kupa succeeded");
                            callback.finishedWithSuccess(documentSnapshot.getData());
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: Getting user kupa failed");
                        callback.finishedWithError(e.getMessage());
                    }
                });

    }
    public static void getUserSavings(NetworkGetData callback) {
        DocumentReference documentReference = FirebaseFirestore.getInstance().collection(UserSingleton.sherdInstance().getPhoneNumber()).document("savings");

        documentReference.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        if (!documentSnapshot.exists()) {
                            Log.d(TAG, "onSuccess: No user savings found");
                            callback.finishedWithError("No user savings found");
                        }
                        else {
                            Log.d(TAG, "onSuccess: Getting user savings succeeded");
                            Log.d(TAG, "onSuccess: " + Objects.requireNonNull(documentSnapshot.getData()).size());

                            Map<String, Object> map = documentSnapshot.getData();

                            //testing
//                            for (Map.Entry<String, Object> entry : map.entrySet()) {
//                                Map<String, Object> innerMap = (Map<String, Object>) entry.getValue();
//                                Log.d(TAG, "onSuccess: map test " + innerMap);
//                                Log.d(TAG, "onSuccess: map test fields " + innerMap.get("id"));
//                            }

                            callback.finishedWithSuccess(map);
                        }


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: Getting user savings failed");
                        callback.finishedWithError(e.getMessage());
                    }
                });

    }
    public static void updateUserMoney(Long newMoney,String phone, NetworkSuccessOrNot callback) {
        CollectionReference collectionReference = FirebaseFirestore.getInstance().collection(phone);

        Map<String, Object> userData = new HashMap<>();
        userData.put("amount",newMoney);
        //userData.put("isParent",isParent);

        collectionReference.document("data")
                        .update(userData)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        callback.finishedWithSuccess(true);
                                    }
                                })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                callback.finishedWithSuccess(false);
                                            }
                                        });

    }
    public static void updateSavingMoney(String idSaving, Long newMoney,Map<String,Object> oldMap, NetworkSuccessOrNot callback) {
        CollectionReference collectionReference = FirebaseFirestore.getInstance().collection(UserSingleton.sherdInstance().getPhoneNumber());

        oldMap.put("amount",newMoney);

        Map<String,Object> updates = new HashMap<>();
        updates.put(idSaving,oldMap);

        collectionReference.document("savings")
                .update(updates)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        callback.finishedWithSuccess(true);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callback.finishedWithSuccess(false);
                    }
                });
    }

    public static void deleteSaving (String idSaving, NetworkSuccessOrNot callback) {
        CollectionReference collectionReference = FirebaseFirestore.getInstance().collection(UserSingleton.sherdInstance().getPhoneNumber());

        Map<String,Object> updates = new HashMap<>();
        updates.put(idSaving, FieldValue.delete());

//        collectionReference.document("savings")
//                .collection(idSaving)
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        for (QueryDocumentSnapshot document : task.getResult()) {
//                            document.getReference().delete();
//                        }
//                    }
//                });

        collectionReference.document("savings")
                .update(updates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        callback.finishedWithSuccess(true);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callback.finishedWithSuccess(false);
                    }
                });

    }

    public static void getMotivation(String category, NetworkGetData callback) {
        CollectionReference collectionReference = FirebaseFirestore.getInstance().collection("stores");

        collectionReference.document(category).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (!documentSnapshot.exists()) {
                            Log.d(TAG, "onSuccess: Getting category failed");
                            callback.finishedWithError("Getting category failed");
                        } else {
                            Log.d(TAG, "onSuccess: Getting category succeeded");
                            callback.finishedWithSuccess(documentSnapshot.getData());
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: Getting category failed");
                        callback.finishedWithError(e.getMessage());
                    }
                });

    }
    public static void changeMoneyRequest (String telWanted, Long sumWanting,String description, NetworkSuccessOrNot callback) {
        CollectionReference collectionReference = FirebaseFirestore.getInstance().collection(telWanted);

        String telWanting = UserSingleton.sherdInstance().getPhoneNumber();

        Map<String,Object> map = new HashMap<>();
        map.put("phone",telWanting);
        map.put("sum",sumWanting);
        map.put("description",description);
        map.put("didAccept",false);

        Map<String,Object> inputs = new HashMap<>();
        inputs.put(telWanting,map);

        collectionReference.document("moneyRequest")
                .set(inputs, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        callback.finishedWithSuccess(true);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callback.finishedWithSuccess(false);
                    }
                });

    }
    public static void updateRequestState (String telWanting, Long sumWanting, NetworkSuccessOrNot callback) {

        CollectionReference collectionReference = FirebaseFirestore.getInstance().collection(UserSingleton.sherdInstance().getPhoneNumber());

        Map<String,Object> map = new HashMap<>();
        map.put("phone",telWanting);
        map.put("sum",sumWanting);
        map.put("didAccept",true);

        Map<String,Object> inputs = new HashMap<>();
        inputs.put(telWanting,map);

        collectionReference.document("moneyRequest")
                .set(inputs, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        callback.finishedWithSuccess(true);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callback.finishedWithSuccess(false);
                    }
                });
    }
    public static void addHistoryMoneyRequest (String phone, Long sum, boolean isPlus, NetworkSuccessOrNot callback) {
        CollectionReference collectionReference = FirebaseFirestore.getInstance().collection(phone);


        Map<String,Object> map = new HashMap<>();
        map.put("sum",sum);
        map.put("isPlus",isPlus);

        Map<String,Object> inputs = new HashMap<>();
        inputs.put(String.valueOf(System.currentTimeMillis()),map);


        collectionReference.document("history")
                .set(inputs, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        callback.finishedWithSuccess(true);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callback.finishedWithSuccess(false);
                    }
                });

    }
}
