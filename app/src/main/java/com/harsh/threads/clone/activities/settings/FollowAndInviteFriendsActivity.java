package com.harsh.threads.clone.activities.settings;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.harsh.threads.clone.BaseActivity;
import com.harsh.threads.clone.R;
import com.harsh.threads.clone.databinding.ActivityFollowAndInviteFriendsBinding;

public class FollowAndInviteFriendsActivity extends BaseActivity {

    ActivityFollowAndInviteFriendsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFollowAndInviteFriendsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}