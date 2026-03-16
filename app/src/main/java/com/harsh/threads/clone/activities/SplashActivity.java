package com.harsh.threads.clone.activities;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.harsh.threads.clone.BaseActivity;
import com.harsh.threads.clone.R;
import com.harsh.threads.clone.interfaces.profile.onProfileUpdate;
import com.harsh.threads.clone.interfaces.profile.onProfileUpdateImpl;
import com.harsh.threads.clone.model.UserModel;

public class SplashActivity extends BaseActivity implements onProfileUpdate {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if (!isUserLoggedIn()) {
            nextScreen();
        } else {
            getUsersDatabase(new AuthListener() {
                @Override
                public void onAuthTaskStart() {

                }

                @Override
                public void onAuthSuccess(DataSnapshot snapshot) {
                     for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                         UserModel user = dataSnapshot.getValue(UserModel.class);
                         if (user != null && mAuth != null && mAuth.getCurrentUser() != null && user.getUid().equals(mAuth.getCurrentUser().getUid())) {
                             mUser = user;
                             break;
                         }
                     }
                     nextScreen();
                }

                @Override
                public void onAuthFail(DatabaseError error) {
                    showToast(error != null ? error.getMessage() : "Database connection failed");
                    nextScreen(); // Proceed anyway
                }
            });
        }

    }

    private void nextScreen(){
        new Handler().postDelayed(() -> {
            View imageView = findViewById(R.id.imageView);
            Intent intent = new Intent(SplashActivity.this, isUserLoggedIn() ? MainActivity.class : AuthActivity.class);
            
            if (imageView != null) {
                Pair<View, String> p1 = Pair.create(imageView, "splash_image");
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SplashActivity.this, p1);
                startActivity(intent, options.toBundle());
            } else {
                startActivity(intent);
            }
        }, 3000);

        new Handler().postDelayed(this::finish, 6000);
    }

    @Override
    public void setup() {

    }

    @Override
    public void onProfileUpdate(UserModel userModel) {

    }
}