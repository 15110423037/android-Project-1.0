package com.harsh.threads.clone.activities;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.harsh.threads.clone.BaseActivity;
import com.harsh.threads.clone.R;
import com.harsh.threads.clone.databinding.ActivityReplyToThreadBinding;
import com.harsh.threads.clone.fragments.HomeFragment;
import com.harsh.threads.clone.model.CommentsModel;
import com.harsh.threads.clone.model.ThreadModel;
import com.harsh.threads.clone.utils.MDialogUtil;
import com.harsh.threads.clone.utils.Utils;

import java.util.ArrayList;

public class ReplyToThreadActivity extends BaseActivity {

    ActivityReplyToThreadBinding binding;

    ThreadModel threadModel;

    ArrayList<String> data = new ArrayList<>();
    NewThreadActivity.ImagesListAdapter adapter = new NewThreadActivity.ImagesListAdapter(data, (listener, dataList) -> {
        data = dataList;
    });
    ActivityResultLauncher<PickVisualMediaRequest> launcher = registerForActivityResult(new ActivityResultContracts.PickMultipleVisualMedia(5), o -> {
        if (o == null) {
            Toast.makeText(ReplyToThreadActivity.this, "No image Selected", Toast.LENGTH_SHORT).show();
        } else {
            for (Uri uri : o) {
//                adapter.addData(uri.toString());
                if (data.size() < 6) {
                    //adapter.addData(uri.toString());
                    data.add(uri.toString());
                    binding.imagesRecyclerView.setAdapter(new NewThreadActivity.ImagesListAdapter(data));
                }
            }
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReplyToThreadBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.edittext.requestFocus();

        binding.imagesRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.imagesRecyclerView.setAdapter(new HomeFragment.PostImagesListAdapter(false));

        if (getIntent().getExtras() == null || (getIntent().getExtras().getString("thread") == null))
            finish();

        mThreadsDatabaseReference.child(getIntent().getExtras().getString("thread")).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                threadModel = snapshot.getValue(ThreadModel.class);
                if (threadModel == null) {
                    finish();
                }
                setUpThreadView(threadModel == null ? new ThreadModel() : threadModel);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        binding.postButton.setOnClickListener(view -> {
            if (binding.edittext.getText().toString().trim().isEmpty() && data.isEmpty())
                return;

            if(threadModel == null) return;

            ArrayList<CommentsModel> comments = threadModel.getComments();
            if(comments == null) comments = new ArrayList<>();

            comments.add(new CommentsModel(
                    mUser.getUid(),
                    data,
                    new ArrayList<>(),
                    1,
                    (comments.size())+"",
                    binding.edittext.getText().toString(),
                    Utils.getNowInMillis()+"",
                    mUser.getUsername(),
                    new ArrayList<>()
            ));

            threadModel.setComments(comments);

            showProgressDialog();
            mThreadsDatabaseReference.child(threadModel.getID()).setValue(threadModel).addOnCompleteListener(task -> {
                hideProgressDialog();
                finish();
            });

        });

        binding.insertImage.setOnClickListener(view -> {
            if (data.size() == 5)
                return;
            launcher.launch(new PickVisualMediaRequest.Builder()
                    .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                    .build());
        });

    }

    private void setUpThreadView(ThreadModel threadModel){
        binding.text.setText(threadModel.getText());
        binding.username.setText(threadModel.getUsername());
        binding.time.setText(Utils.calculateTimeDiff(Long.parseLong(threadModel.getTime())));

        binding.imagesRecyclerView.setAdapter(new HomeFragment.PostImagesListAdapter(threadModel.getImages(), false));

        if(threadModel.isIsPoll() && false) {
            binding.pollLayout.setVisibility(View.VISIBLE);
            binding.imagesRecyclerView.setVisibility(View.GONE);
        }
        else {
            binding.pollLayout.setVisibility(View.GONE);
            binding.imagesRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    public void askAndPressback(View view) {
        if (binding.edittext.getText().toString().trim().isEmpty())
            //if ((adapter.getData() == null || adapter.getData().isEmpty())) {
        {
            onBackPressed();
            return;
        }

        MDialogUtil mDialogUtil = new MDialogUtil(this)
                .setTitle("Are you sure you want to exit?")
                .setMessage("", false);
        AlertDialog dialog = mDialogUtil.create();
        mDialogUtil.setB1("Exit", v -> {
            dialog.dismiss();
            onBackPressed();
        });
        mDialogUtil.setB2("Cancel", v -> dialog.dismiss());
        dialog.show();
    }
}