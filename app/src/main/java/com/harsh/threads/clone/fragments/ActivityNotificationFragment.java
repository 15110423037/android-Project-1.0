package com.harsh.threads.clone.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.TextViewCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.harsh.threads.clone.R;
import com.harsh.threads.clone.utils.Utils;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ActivityNotificationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ActivityNotificationFragment extends Fragment {

    private  static  ActivityNotificationFragment instance;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    TextView chip_all;
    TextView chip_requests;
    TextView chip_replies;
    TextView chip_mentions;
    TextView chip_quotes;
    TextView chip_reposts;

    TextView no_data_text;

    int selectedPosition = 0;

    RecyclerView recyclerView;

    public ActivityNotificationFragment() {
        // Required empty public constructor
    }

    public static ActivityNotificationFragment newInstance(String param1, String param2) {
        ActivityNotificationFragment fragment = new ActivityNotificationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static ActivityNotificationFragment getInstance() {
        if (instance == null) {
            instance = new ActivityNotificationFragment();
        }
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
        return inflater.inflate(R.layout.fragment_activity_notification, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        chip_all = view.findViewById(R.id.chip_all);
        chip_requests = view.findViewById(R.id.chip_requests);
        chip_replies = view.findViewById(R.id.chip_replies);
        chip_mentions = view.findViewById(R.id.chip_mentions);
        chip_quotes = view.findViewById(R.id.chip_quotes);
        chip_reposts = view.findViewById(R.id.chip_reposts);

        no_data_text = view.findViewById(R.id.no_data_text);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        chip_all.setOnClickListener(view1 -> setPosition(0));
        chip_requests.setOnClickListener(view1 -> setPosition(1));
        chip_replies.setOnClickListener(view1 -> setPosition(2));
        chip_mentions.setOnClickListener(view1 -> setPosition(3));
        chip_quotes.setOnClickListener(view1 -> setPosition(4));
        chip_reposts.setOnClickListener(view1 -> setPosition(5));

        setPosition(0);

    }

    private void setPosition(int position){
        setHeaderPos(chip_all, position==0);
        setHeaderPos(chip_requests, position==1);
        setHeaderPos(chip_replies, position==2);
        setHeaderPos(chip_mentions, position==3);
        setHeaderPos(chip_quotes, position==4);
        setHeaderPos(chip_reposts, position==5);
        selectedPosition = position;

        if(position == 0 || position == 1) {
            no_data_text.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            recyclerView.setAdapter(new DataAdapter(position == 0));
        }
        else{
            no_data_text.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
    }

    private void setHeaderPos(TextView view, boolean isActivated){
        if (isActivated){
            TextViewCompat.setTextAppearance(view, R.style.Base_Widget_AppCompat_TextView_ButtonFilled);
            view.setBackgroundResource(R.drawable.button_background_filled);
        }
        else{
            TextViewCompat.setTextAppearance(view, R.style.Base_Widget_AppCompat_TextView_ButtonOutlined);
            view.setBackgroundResource(R.drawable.button_background_outlined);
        }
    }


    static class DataAdapter extends RecyclerView.Adapter<DataAdapter.ViewHolder>{

        boolean rand;

        public DataAdapter(boolean rand){
            this.rand = rand;
        }

        @NonNull
        @Override
        public DataAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = View.inflate(parent.getContext(), R.layout.notification_activity_follow_req_layout,null);
            view.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull DataAdapter.ViewHolder holder, int position) {
            if (rand){
                if(Utils.getRandomNumber(0,1)==0){
                    holder.itemView.findViewById(R.id.follow_button).setVisibility(View.GONE);
                }
            }
        }

        @Override
        public int getItemCount() {
            return 100;
        }

        static class ViewHolder extends RecyclerView.ViewHolder{

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
            }
        }
    }

}