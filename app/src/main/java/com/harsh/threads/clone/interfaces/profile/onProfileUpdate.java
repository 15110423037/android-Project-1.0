package com.harsh.threads.clone.interfaces.profile;

import androidx.annotation.NonNull;

import com.google.api.client.util.Value;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.harsh.threads.clone.BaseActivity;
import com.harsh.threads.clone.Constants;
import com.harsh.threads.clone.model.UserModel;

public interface onProfileUpdate {

    void setup();
    void onProfileUpdate(UserModel userModel);

}

