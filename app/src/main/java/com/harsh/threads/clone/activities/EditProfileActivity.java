package com.harsh.threads.clone.activities;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.snackbar.SnackbarContentLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.harsh.threads.clone.BaseActivity;
import com.harsh.threads.clone.R;
import com.harsh.threads.clone.databinding.ActivityEditProfileBinding;
import com.harsh.threads.clone.utils.Utils;
import com.suke.widget.SwitchButton;

public class EditProfileActivity extends BaseActivity {

    ActivityEditProfileBinding binding;
    String bio, link;
    boolean isPublicAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.visibilityLayout.setOnClickListener(v -> binding.switchButton.toggle());
        binding.switchButton.setOnCheckedChangeListener((view, isChecked) -> {
            if (isChecked) {
                binding.textView13.setText("Private profiles can only reply to their followers. Switch to public to reply to anyone.");
            }else{
                binding.textView13.setText("If you switch to private, you won't be able to reply to others unless they follow you.");
            }
        });

        binding.textView7.setOnClickListener(view -> {
            Snackbar.make(binding.getRoot(), "Cannot change the username until 30 days.", Snackbar.LENGTH_SHORT).show();
        });

        bio = mUser.getBio();
        link = mUser.getInfoLink();
        isPublicAccount = mUser.isPublicAccount();

        binding.name.setText(String.format("%s ( %s )", mUser.getName(), mUser.getUsername()));
        binding.bio.setText(mUser.getBio());
        if(!mUser.getInfoLink().isEmpty())binding.link.setText(mUser.getInfoLink());
        binding.switchButton.setChecked(mUser.isPublicAccount());

        binding.done.setOnClickListener(view -> {
            Utils.hideKeyboard(EditProfileActivity.this);
           if(binding.bio.getText().toString().trim().equals(bio.trim())
                   && binding.link.getText().toString().trim().equals(link.trim())
                   && binding.switchButton.isChecked() == isPublicAccount){
               return;
           }

           mUser.setBio(binding.bio.getText().toString().trim());
           mUser.setInfoLink(binding.link.getText().toString().trim());
           mUser.setPublicAccount(binding.switchButton.isChecked());

           updateProfileInfo(mUser, new AuthListener(){
               @Override
               public void onAuthTaskStart() {
                   showProgressDialog();
                   finish();
               }

               @Override
               public void onAuthSuccess(DataSnapshot snapshot) {
                   hideProgressDialog();
                   finish();
               }

               @Override
               public void onAuthFail(DatabaseError error) {
                   hideProgressDialog();
                   finish();
               }
           });
        });
    }
}