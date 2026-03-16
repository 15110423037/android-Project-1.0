package com.harsh.threads.clone.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.widget.TextViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.harsh.threads.clone.BaseActivity;
import com.harsh.threads.clone.R;
import com.harsh.threads.clone.databinding.ActivityThreadViewBinding;
import com.harsh.threads.clone.fragments.HomeFragment;
import com.harsh.threads.clone.model.CommentsModel;
import com.harsh.threads.clone.model.ThreadModel;
import com.harsh.threads.clone.utils.Utils;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class ThreadViewActivity extends BaseActivity {

    ActivityThreadViewBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityThreadViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.postRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.postRecyclerView.setAdapter(new HomeFragment.PostImagesListAdapter(false));

        binding.commentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        //binding.commentsRecyclerView.setAdapter(new CommentsImagesListAdapter());

        if (getIntent().getExtras() == null || (getIntent().getExtras().getString("thread") == null))
            finish();

        mThreadsDatabaseReference.child(getIntent().getExtras().getString("thread")).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ThreadModel threadModel = snapshot.getValue(ThreadModel.class);
                if (threadModel == null) {
                    finish();
                }
                setUpThreadView(threadModel == null ? new ThreadModel() : threadModel);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        binding.addNewCommentLayout.setOnClickListener(view -> {
            if (!mUser.isPublicAccount()) {
                showNeedPublicProfileDialog();
                return;
            }
            addNewComment();
        });
    }

    private void addNewComment() {
        startActivity(new Intent(this, ReplyToThreadActivity.class).putExtra("thread", getIntent().getExtras().getString("thread")));
    }

    private void showNeedPublicProfileDialog(){
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(ThreadViewActivity.this);
        final View bottomSheetView = LayoutInflater.from(ThreadViewActivity.this).inflate(R.layout.public_account_needed_dialog_layout, null);
        bottomSheetDialog.setContentView(bottomSheetView);
        final LinearLayout privateProfileButton, publicProfileButton;
        final TextView cancel_button;
        cancel_button = bottomSheetView.findViewById(R.id.cancel_button);
        privateProfileButton = bottomSheetView.findViewById(R.id.privateProfileButton);
        publicProfileButton = bottomSheetView.findViewById(R.id.publicProfileBtn);

        final boolean publicAccountStatus = mUser.isPublicAccount();
        if(publicAccountStatus){
            publicProfileButton.setBackgroundResource(R.drawable.button_background_outlined_filled);
            privateProfileButton.setBackgroundResource(R.drawable.button_background_outlined);
        }else{
            publicProfileButton.setBackgroundResource(R.drawable.button_background_outlined);
            privateProfileButton.setBackgroundResource(R.drawable.button_background_outlined_filled);
        }

        privateProfileButton.setOnClickListener(view1 -> {
            publicProfileButton.setBackgroundResource(R.drawable.button_background_outlined);
            privateProfileButton.setBackgroundResource(R.drawable.button_background_outlined_filled);
            setHeaderPos(cancel_button, publicAccountStatus);
            cancel_button.setText(publicAccountStatus?"Done":"Cancel");
            cancel_button.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        });
        publicProfileButton.setOnClickListener(view1 -> {
            publicProfileButton.setBackgroundResource(R.drawable.button_background_outlined_filled);
            privateProfileButton.setBackgroundResource(R.drawable.button_background_outlined);
            setHeaderPos(cancel_button, !publicAccountStatus);
            cancel_button.setText(!publicAccountStatus?"Done":"Cancel");
            cancel_button.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        });

        cancel_button.setOnClickListener(view1 -> {
            bottomSheetDialog.dismiss();
            if(cancel_button.getText().toString().equals("Cancel"))
                return;
            showProgressDialog();
            mUser.setPublicAccount(!publicAccountStatus);
            mUsersDatabaseReference.child(mUser.getUsername()).setValue(mUser).addOnCompleteListener(task -> hideProgressDialog());
        });

        bottomSheetDialog.create();
        bottomSheetDialog.show();
    }

    private void setUpThreadView(ThreadModel threadModel){
        binding.textPara.setText(threadModel.getText());
        binding.username.setText(threadModel.getUsername());
        binding.time.setText(Utils.calculateTimeDiff(Long.parseLong(threadModel.getTime())));

        binding.likes.setText(String.valueOf(threadModel.getLikes() == null?0:threadModel.getLikes().size()));
        binding.comments.setText(String.valueOf(threadModel.getComments() == null?0:threadModel.getComments().size()));
        binding.reposts.setText(String.valueOf(threadModel.getReposts() == null?0:threadModel.getReposts().size()));

        binding.postRecyclerView.setAdapter(new HomeFragment.PostImagesListAdapter(threadModel.getImages(), false));
        binding.commentsRecyclerView.setAdapter(new CommentsImagesListAdapter());

        if(threadModel.isIsPoll() && false) {
            binding.pollLayout.setVisibility(View.VISIBLE);
            binding.postRecyclerView.setVisibility(View.GONE);
        }
        else {
            binding.pollLayout.setVisibility(View.GONE);
            binding.postRecyclerView.setVisibility(View.VISIBLE);
        }

        if(threadModel.getPollOptions()!=null){
            binding.pollOption1.setText(threadModel.getPollOptions().getOption1().getText());
            binding.pollOption2.setText(threadModel.getPollOptions().getOption2().getText());
            if (threadModel.getPollOptions().getOption3().getVisibility()) {
                binding.pollOption3.setVisibility(View.VISIBLE);
                binding.pollOption3.setText(threadModel.getPollOptions().getOption3().getText());
            }
            if (threadModel.getPollOptions().getOption4().getVisibility()) {
                binding.pollOption4.setVisibility(View.VISIBLE);
                binding.pollOption4.setText(threadModel.getPollOptions().getOption4().getText());
            }

            binding.pollOption1.setOnClickListener(view -> {
                setHeaderPos((TextView) view, true);
                setHeaderPos((TextView) binding.pollOption2, false);
                setHeaderPos((TextView) binding.pollOption3, false);
                setHeaderPos((TextView) binding.pollOption4, false);
            });
            binding.pollOption2.setOnClickListener(view -> {
                setHeaderPos((TextView) view, true);
                setHeaderPos((TextView) binding.pollOption1, false);
                setHeaderPos((TextView) binding.pollOption3, false);
                setHeaderPos((TextView) binding.pollOption4, false);
            });
            binding.pollOption3.setOnClickListener(view -> {
                setHeaderPos((TextView) view, true);
                setHeaderPos((TextView) binding.pollOption1, false);
                setHeaderPos((TextView) binding.pollOption2, false);
                setHeaderPos((TextView) binding.pollOption4, false);
            });
            binding.pollOption4.setOnClickListener(view -> {
                setHeaderPos((TextView) view, true);
                setHeaderPos((TextView) binding.pollOption1, false);
                setHeaderPos((TextView) binding.pollOption2, false);
                setHeaderPos((TextView) binding.pollOption3, false);
            });
        }

        if(threadModel.getComments()!=null){
            binding.commentsRecyclerView.setAdapter(new CommentsImagesListAdapter(threadModel.getComments()));
        }

        binding.likesLayout.setOnClickListener(view -> {
            if (threadModel.getLikes().contains(BaseActivity.mUser.getUid())) {
                threadModel.getLikes().remove(BaseActivity.mUser.getUid());
            } else {
                threadModel.getLikes().add(BaseActivity.mUser.getUid());
            }
            setLikeStatus(binding.likes, threadModel);
            BaseActivity.mThreadsDatabaseReference.child(threadModel.getID()).setValue(threadModel);
        });
        setLikeStatus(binding.likes, threadModel);
    }

    private void setLikeStatus(TextView likes, ThreadModel threadModel) {
        if (threadModel.getLikes().contains(BaseActivity.mUser.getUid())) {
            binding.likeImage.setImageResource(R.drawable.favorite_24px);
        } else {
            binding.likeImage.setImageResource(R.drawable.favorite_outline_24px);
            binding.likeImage.setColorFilter(R.color.red);
        }
    }

    private void setHeaderPos(TextView view, boolean isActivated) {
        if (isActivated) {
            TextViewCompat.setTextAppearance(view, R.style.Base_Widget_AppCompat_TextView_ButtonFilled);
            view.setBackgroundResource(R.drawable.button_background_filled);
        } else {
            TextViewCompat.setTextAppearance(view, R.style.Base_Widget_AppCompat_TextView_ButtonOutlined);
            view.setBackgroundResource(R.drawable.button_background_outlined);
        }
    }

    public static class CommentsImagesListAdapter extends RecyclerView.Adapter<CommentsImagesListAdapter.ViewHolder> {

        public ArrayList<CommentsModel> data;

        public CommentsImagesListAdapter(){}

        public CommentsImagesListAdapter(ArrayList<CommentsModel> data){
            this.data = data;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comments_layout, null);
            view.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            return new CommentsImagesListAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) holder.itemView.getLayoutParams();
//            int dp = ((int) (holder.itemView.getContext().getResources().getDisplayMetrics().density));
//            params.setMargins(dp*12,0,dp*12,0);
//            holder.itemView.setLayoutParams(params);
            ((TextView)holder.itemView.findViewById(R.id.username)).setText(data.get(position).getUsername());
            ((TextView)holder.itemView.findViewById(R.id.title)).setText(data.get(position).getText());
            TextView time = holder.itemView.findViewById(R.id.time);
            time.setText(Utils.calculateTimeDiff(Long.parseLong(data.get(position).getTime())));
            TextView likes = holder.itemView.findViewById(R.id.likes);
            likes.setText(String.valueOf(data.get(position).getLikes() == null?0:data.get(position).getLikes().size()));

        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
            }
        }
    }
}
