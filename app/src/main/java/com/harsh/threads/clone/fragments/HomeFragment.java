package com.harsh.threads.clone.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.TextViewCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.harsh.threads.clone.BaseActivity;
import com.harsh.threads.clone.R;
import com.harsh.threads.clone.activities.NewThreadActivity;
import com.harsh.threads.clone.activities.ThreadViewActivity;
import com.harsh.threads.clone.model.CommentsModel;
import com.harsh.threads.clone.model.ThreadModel;
import com.harsh.threads.clone.utils.Utils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import io.getstream.photoview.dialog.PhotoViewDialog;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String  TAG = "HomeFragment";
    private static HomeFragment instance;
    RecyclerView recyclerView;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    ArrayList<ThreadModel> data = new ArrayList<>();
    private final Adapter dataAdapter = new Adapter(data);

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    public static Fragment getInstance() {
        if (instance == null)
            instance = new HomeFragment();
        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(dataAdapter);
        refreshList();

        SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            //new Handler().postDelayed(() -> swipeRefreshLayout.setRefreshing(false), 3000);
            data.clear();
            refreshList();
            swipeRefreshLayout.setRefreshing(false);
        });
    }

    private void refreshList(){
        data.clear();
        dataAdapter.clearData();
//        BaseActivity.mThreadsDatabaseReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
//                    ThreadModel threadModel = dataSnapshot.getValue(ThreadModel.class);
//                    //Log.i("HomeFragment", "onDataChange: " + threadModel.profileImage());
//                    if (threadModel != null) {
//                        data.add(threadModel);
//                        recyclerView.setAdapter(new Adapter(data));
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//
        BaseActivity.mThreadsDatabaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                //Log.i(TAG, "onChildAdded: " + snapshot);
                dataAdapter.addData(snapshot.getValue(ThreadModel.class));
                ArrayList<CommentsModel> comments = new ArrayList<>();
                comments.add(new CommentsModel(
                        BaseActivity.mUser != null ? BaseActivity.mUser.getUid() : "",
                        new ArrayList<>(),
                        new ArrayList<>(),
                        1,
                        "cid",
                        "comment",
                        Utils.getNowInMillis()+"",
                        BaseActivity.mUser != null ? BaseActivity.mUser.getUsername() : "",
                        new ArrayList<>()
                ));
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                ThreadModel model = snapshot.getValue(ThreadModel.class);
                if (model != null) {
                    for (int i = 0; i < data.size(); i++) {
                        if (data.get(i).getID().equals(model.getID())) {
                            dataAdapter.updateData(i, model);
                            break;
                        }
                    }
                }
                Log.i(TAG, "onChildChanged: " + snapshot);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                ThreadModel model = snapshot.getValue(ThreadModel.class);
                if (model != null)
                    dataAdapter.removeData(model);

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setHeaderPos(TextView view, boolean isActivated) {
        if (isActivated) {
            TextViewCompat.setTextAppearance(view, R.style.Base_Widget_AppCompat_TextView_ButtonFilled);
            view.setBackgroundResource(R.drawable.button_background_filled);
        } else {
            TextViewCompat.setTextAppearance(view, R.style.Base_Widget_AppCompat_TextView_ButtonOutlined);
            view.setBackgroundResource(R.drawable.button_background_outlined);
            view.setEnabled(false);
        }
    }

    public static class PostImagesListAdapter extends RecyclerView.Adapter<PostImagesListAdapter.ViewHolder> {

        private boolean shouldHaveLeftPadding = true;
        private List<String> data = new ArrayList<>();

        public PostImagesListAdapter() {

        }

        public PostImagesListAdapter(List<String> data) {
            this.data = data;
        }

        public PostImagesListAdapter(List<String> data, boolean leftPadding) {
            this.data = data;
            this.shouldHaveLeftPadding = leftPadding;
        }

        public PostImagesListAdapter(boolean leftPadding) {
            this.shouldHaveLeftPadding = leftPadding;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_list_item_image_item, null);
            view.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            return new ViewHolder(view, getItemCount());
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) holder.itemView.getLayoutParams();
            int dp = ((int) (holder.itemView.getContext().getResources().getDisplayMetrics().density));
            params.setMargins((position == 0 && shouldHaveLeftPadding) ? dp * 60 : dp * 4, 0, dp * 4, dp*4);
            holder.itemView.setLayoutParams(params);
            ImageView imageView = holder.itemView.findViewById(R.id.imageView);
            if (!data.get(position).isEmpty()) {
                //Picasso.get().load(data.get(position)).placeholder(R.drawable.photo_library_24px).into(imageView);
                if(data.get(position).contains("gif"))Glide.with(holder.itemView.getContext()).asGif().load(data.get(position)).into(imageView);
                else Glide.with(holder.itemView.getContext()).load(data.get(position)).into(imageView);
                //setImage(Utils.getBitmapFromURL(data.get(position)), imageView);
            }

            imageView.setOnClickListener(view -> {
                new PhotoViewDialog.Builder(holder.itemView.getContext(), data, (imageView1, url) -> Glide.with(holder.itemView.getContext()).load(url).into(imageView1)).withTransitionFrom(imageView).withStartPosition(position).build().show(true);
            });

        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        private void setImage(Bitmap bitmap, ImageView imageView) {
            if (bitmap != null) {
                int imageWidth = bitmap.getWidth();
                int imageHeight = bitmap.getHeight();
                int maxHeight = 200;

                float aspectRatio = (float) imageWidth / imageHeight;

                if (aspectRatio >= 1.5) {
                    imageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, maxHeight));
                    imageView.setAdjustViewBounds(false);
                } else if (aspectRatio <= 0.6) {
                    imageView.setLayoutParams(new LinearLayout.LayoutParams(150, maxHeight));
                    imageView.setAdjustViewBounds(false);
                } else {
                    imageView.setLayoutParams(new LinearLayout.LayoutParams(maxHeight, maxHeight));
                    imageView.setAdjustViewBounds(false);
                }

                imageView.setImageDrawable(new BitmapDrawable(imageView.getResources(), bitmap));
            }
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {

            public ViewHolder(@NonNull View itemView, int itemCount) {
                super(itemView);
            }
        }
    }

    class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
        private ArrayList<ThreadModel> data = new ArrayList<>();

        public Adapter(ArrayList<ThreadModel> data) {
            this.data = data;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = viewType == 1 ? LayoutInflater.from(getContext()).inflate(R.layout.main_head_layout, null) : LayoutInflater.from(getContext()).inflate(R.layout.home_list_item, null);
            view.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            if (getItemViewType(position) == 1) {
                holder.itemView.setOnClickListener(view -> startActivity(new Intent(getContext(), NewThreadActivity.class)));
                if(BaseActivity.mUser != null && BaseActivity.mUser.getUsername()!=null)((TextView) holder.itemView.findViewById(R.id.username)).setText(BaseActivity.mUser.getUsername());
            }
            if (position == 0) return;
            final int newPosition = position - 1;

            ((TextView) holder.itemView.findViewById(R.id.username)).setText(data.get(newPosition).getUsername());

            final ShapeableImageView profileImage = holder.itemView.findViewById(R.id.profileImage);
            if (!data.get(newPosition).profileImage().isEmpty())
                Picasso.get().load(data.get(newPosition).profileImage()).placeholder(R.drawable.person_outline_24px).into(profileImage);

            ((TextView) holder.itemView.findViewById(R.id.time)).setText(Utils.calculateTimeDiff(Long.parseLong(data.get(newPosition).getTime())));
            ((TextView) holder.itemView.findViewById(R.id.title)).setText(data.get(newPosition).getText());
            ((TextView) holder.itemView.findViewById(R.id.likes)).setText(String.valueOf(data.get(newPosition).getLikes().size()));
            ((TextView) holder.itemView.findViewById(R.id.comments)).setText(String.valueOf(data.get(newPosition).getComments().size()));
            ((TextView) holder.itemView.findViewById(R.id.reposts)).setText(String.valueOf(data.get(newPosition).getReposts().size()));

            //((RecyclerView) holder.itemView.findViewById(R.id.imagesListRecyclerView)).addItemDecoration(new MarginDecoration(holder.itemView.getContext(), 90, 24));
            ((RecyclerView) holder.itemView.findViewById(R.id.imagesListRecyclerView)).setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
            if (!data.get(newPosition).getImages().isEmpty())
                ((RecyclerView) holder.itemView.findViewById(R.id.imagesListRecyclerView)).setAdapter(new PostImagesListAdapter(data.get(newPosition).getImages()));

            if (data.get(newPosition).isIsPoll()) {
                holder.itemView.findViewById(R.id.poll_layout).setVisibility(View.VISIBLE);
                holder.itemView.findViewById(R.id.imagesListRecyclerView).setVisibility(View.GONE);

                ((TextView) holder.itemView.findViewById(R.id.poll_option_1)).setText(data.get(newPosition).getPollOptions().getOption1().getText());
                ((TextView) holder.itemView.findViewById(R.id.poll_option_2)).setText(data.get(newPosition).getPollOptions().getOption2().getText());
                if (data.get(newPosition).getPollOptions().getOption3().getVisibility()) {
                    ((TextView) holder.itemView.findViewById(R.id.poll_option_3)).setVisibility(View.VISIBLE);
                    ((TextView) holder.itemView.findViewById(R.id.poll_option_3)).setText(data.get(newPosition).getPollOptions().getOption3().getText());
                }
                if (data.get(newPosition).getPollOptions().getOption4().getVisibility()) {
                    ((TextView) holder.itemView.findViewById(R.id.poll_option_4)).setVisibility(View.VISIBLE);
                    ((TextView) holder.itemView.findViewById(R.id.poll_option_4)).setText(data.get(newPosition).getPollOptions().getOption4().getText());
                }
            } else
                holder.itemView.findViewById(R.id.poll_layout).setVisibility(View.GONE);

            holder.itemView.setOnClickListener(view -> startActivity(new Intent(getContext(), ThreadViewActivity.class).putExtra("thread", data.get(newPosition).getID())));

            holder.itemView.findViewById(R.id.poll_option_1).setOnClickListener(view -> {
                if(BaseActivity.mUser == null) return;
                setHeaderPos((TextView) view, true);
                setHeaderPos((TextView) holder.itemView.findViewById(R.id.poll_option_2), false);
                setHeaderPos((TextView) holder.itemView.findViewById(R.id.poll_option_3), false);
                setHeaderPos((TextView) holder.itemView.findViewById(R.id.poll_option_4), false);
                ArrayList<String> list = data.get(newPosition).getPollOptions().getOption1().getVotes();
                ArrayList<String> list1 = data.get(newPosition).getPollOptions().getOption2().getVotes();
                ArrayList<String> list2 = data.get(newPosition).getPollOptions().getOption3().getVotes();
                ArrayList<String> list3 = data.get(newPosition).getPollOptions().getOption4().getVotes();
                if(!(list.contains(BaseActivity.mUser.getUid()) && list1.contains(BaseActivity.mUser.getUid()) && list2.contains(BaseActivity.mUser.getUid()) && list3.contains(BaseActivity.mUser.getUid()))){
                    list.add(BaseActivity.mUser.getUid());
                    data.get(newPosition).getPollOptions().getOption1().setVotes(list);
                    updateVote(data.get(newPosition).getID(), data.get(newPosition));
                }

            });
            holder.itemView.findViewById(R.id.poll_option_2).setOnClickListener(view -> {
                if(BaseActivity.mUser == null) return;
                setHeaderPos((TextView) view, true);
                setHeaderPos((TextView) holder.itemView.findViewById(R.id.poll_option_1), false);
                setHeaderPos((TextView) holder.itemView.findViewById(R.id.poll_option_3), false);
                setHeaderPos((TextView) holder.itemView.findViewById(R.id.poll_option_4), false);
                ArrayList<String> list = data.get(newPosition).getPollOptions().getOption1().getVotes();
                ArrayList<String> list1 = data.get(newPosition).getPollOptions().getOption2().getVotes();
                ArrayList<String> list2 = data.get(newPosition).getPollOptions().getOption3().getVotes();
                ArrayList<String> list3 = data.get(newPosition).getPollOptions().getOption4().getVotes();
                if(!(list.contains(BaseActivity.mUser.getUid()) && list1.contains(BaseActivity.mUser.getUid()) && list2.contains(BaseActivity.mUser.getUid()) && list3.contains(BaseActivity.mUser.getUid()))){
                    list1.add(BaseActivity.mUser.getUid());
                    data.get(newPosition).getPollOptions().getOption2().setVotes(list1);
                    updateVote(data.get(newPosition).getID(), data.get(newPosition));
                }
            });
            holder.itemView.findViewById(R.id.poll_option_3).setOnClickListener(view -> {
                if(BaseActivity.mUser == null) return;
                setHeaderPos((TextView) view, true);
                setHeaderPos((TextView) holder.itemView.findViewById(R.id.poll_option_1), false);
                setHeaderPos((TextView) holder.itemView.findViewById(R.id.poll_option_2), false);
                setHeaderPos((TextView) holder.itemView.findViewById(R.id.poll_option_4), false);
                ArrayList<String> list = data.get(newPosition).getPollOptions().getOption1().getVotes();
                ArrayList<String> list1 = data.get(newPosition).getPollOptions().getOption2().getVotes();
                ArrayList<String> list2 = data.get(newPosition).getPollOptions().getOption3().getVotes();
                ArrayList<String> list3 = data.get(newPosition).getPollOptions().getOption4().getVotes();
                if(!(list.contains(BaseActivity.mUser.getUid()) && list1.contains(BaseActivity.mUser.getUid()) && list2.contains(BaseActivity.mUser.getUid()) && list3.contains(BaseActivity.mUser.getUid()))){
                    list2.add(BaseActivity.mUser.getUid());
                    data.get(newPosition).getPollOptions().getOption3().setVotes(list2);
                    updateVote(data.get(newPosition).getID(), data.get(newPosition));
                }
            });
            holder.itemView.findViewById(R.id.poll_option_4).setOnClickListener(view -> {
                if(BaseActivity.mUser == null) return;
                setHeaderPos((TextView) view, true);
                setHeaderPos((TextView) holder.itemView.findViewById(R.id.poll_option_1), false);
                setHeaderPos((TextView) holder.itemView.findViewById(R.id.poll_option_2), false);
                setHeaderPos((TextView) holder.itemView.findViewById(R.id.poll_option_3), false);
                ArrayList<String> list = data.get(newPosition).getPollOptions().getOption1().getVotes();
                ArrayList<String> list1 = data.get(newPosition).getPollOptions().getOption2().getVotes();
                ArrayList<String> list2 = data.get(newPosition).getPollOptions().getOption3().getVotes();
                ArrayList<String> list3 = data.get(newPosition).getPollOptions().getOption4().getVotes();
                if(!(list.contains(BaseActivity.mUser.getUid()) && list1.contains(BaseActivity.mUser.getUid()) && list2.contains(BaseActivity.mUser.getUid()) && list3.contains(BaseActivity.mUser.getUid()))){
                    list3.add(BaseActivity.mUser.getUid());
                    data.get(newPosition).getPollOptions().getOption4().setVotes(list3);
                    updateVote(data.get(newPosition).getID(), data.get(newPosition));
                }
            });
//
//            setHeaderPos((TextView) holder.itemView.findViewById(R.id.poll_option_1), false);
//            setHeaderPos((TextView) holder.itemView.findViewById(R.id.poll_option_2), false);
//            setHeaderPos((TextView) holder.itemView.findViewById(R.id.poll_option_3), false);
//            setHeaderPos((TextView) holder.itemView.findViewById(R.id.poll_option_4), false);
//
//            holder.itemView.findViewById(R.id.poll_option_1).setEnabled(true);
//            holder.itemView.findViewById(R.id.poll_option_2).setEnabled(true);
//            holder.itemView.findViewById(R.id.poll_option_3).setEnabled(true);
//            holder.itemView.findViewById(R.id.poll_option_4).setEnabled(true);

            if(BaseActivity.mUser != null && data.get(newPosition).getPollOptions().getOption1().getVotes().contains(BaseActivity.mUser.getUid()))
                setHeaderPos((TextView) holder.itemView.findViewById(R.id.poll_option_1), true);
            else if (BaseActivity.mUser != null && data.get(newPosition).getPollOptions().getOption2().getVotes().contains(BaseActivity.mUser.getUid()))
                setHeaderPos((TextView) holder.itemView.findViewById(R.id.poll_option_2), true);
            else if (BaseActivity.mUser != null && data.get(newPosition).getPollOptions().getOption3().getVotes().contains(BaseActivity.mUser.getUid()))
                setHeaderPos((TextView) holder.itemView.findViewById(R.id.poll_option_3), true);
            else if (BaseActivity.mUser != null && data.get(newPosition).getPollOptions().getOption4().getVotes().contains(BaseActivity.mUser.getUid()))
                setHeaderPos((TextView) holder.itemView.findViewById(R.id.poll_option_4), true);

            holder.itemView.findViewById(R.id.likeThreadLayout).setOnClickListener(view -> {
                if (BaseActivity.mUser == null) return;
                if (data.get(newPosition).getLikes().contains(BaseActivity.mUser.getUid())) {
                    data.get(newPosition).getLikes().remove(BaseActivity.mUser.getUid());
                } else {
                    data.get(newPosition).getLikes().add(BaseActivity.mUser.getUid());
                }
                setLikeStatus(holder, newPosition);
                BaseActivity.mThreadsDatabaseReference.child(data.get(newPosition).getID()).setValue(data.get(newPosition));
                notifyDataSetChanged();
            });
            setLikeStatus(holder, newPosition);
        }

        @Override
        public int getItemCount() {
            return data.size() + 1;
        }

        @Override
        public int getItemViewType(int position) {
            return position == 0 ? 1 : 0;
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
            }
        }

        private void updateVote(String id, ThreadModel dataModel){
            BaseActivity.mThreadsDatabaseReference.child(id).setValue(dataModel);
        }

        public void updateData(int position, ThreadModel data) {
            this.data.set(position, data);
            notifyDataSetChanged();
            notifyItemChanged(position);
        }

        public void addData(ThreadModel data) {
            this.data.add(0,data);
            notifyDataSetChanged();
            notifyItemInserted(this.data.size());
        }

        public void removeData(int position) {
            this.data.remove(position);
            notifyDataSetChanged();
            notifyItemRemoved(position+1);
        }

        public void removeData(ThreadModel model) {
            int i = this.data.indexOf(model);
            this.data.remove(model);
            notifyDataSetChanged();
            notifyItemRemoved(i);
        }

        public void clearData() {
            this.data.clear();
            notifyDataSetChanged();
        }

        private void setLikeStatus(ViewHolder holder, int newPosition){
            if (BaseActivity.mUser != null && data.get(newPosition).getLikes().contains(BaseActivity.mUser.getUid())) {
                ((ImageView)holder.itemView.findViewById(R.id.likeImage)).setImageResource(R.drawable.favorite_24px);
            } else {
                ((ImageView)holder.itemView.findViewById(R.id.likeImage)).setImageResource(R.drawable.favorite_outline_24px);
                ((ImageView)holder.itemView.findViewById(R.id.likeImage)).setColorFilter(R.color.red);
            }
        }
    }
}