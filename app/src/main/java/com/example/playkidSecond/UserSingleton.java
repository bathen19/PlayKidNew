package com.example.playkidSecond;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UserSingleton {
    private static final UserSingleton ourInstance = new UserSingleton();
    public static UserSingleton sherdInstance() {
        return ourInstance;
    }
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser user;

    public UserSingleton(){
     user = mAuth.getCurrentUser();
    }

    String getPhoneNumber(){
        return user.getPhoneNumber();
    }
}
