package com.example.hunter.ui.rewards;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.hunter.R;
import com.example.hunter.RewardsItem;
import com.example.hunter.RewardsRecyclerViewAdapter;
import com.example.hunter.SpacesItemDecoration;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;


public class RedeemedRewardsFragment extends Fragment {
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private RecyclerView recyclerView;
    private RewardsRecyclerViewAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<RewardsItem> rewardsItemArrayList = new ArrayList<>();
    private List<String> redeemedRewards = new ArrayList<>();


    private OnFragmentInteractionListener mListener;

    public RedeemedRewardsFragment() {
        // Required empty public constructor
    }

    public static RedeemedRewardsFragment newInstance(String param1, String param2) {
        RedeemedRewardsFragment fragment = new RedeemedRewardsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        redeemedRewards=new ArrayList<>();
        rewardsItemArrayList=new ArrayList<>();
        View v = inflater.inflate(R.layout.fragment_redeemed_rewards, container, false);
        recyclerView = v.findViewById(R.id.rewards_rvRedeemed);
        recyclerView.setHasFixedSize(true);
        layoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new RewardsRecyclerViewAdapter(rewardsItemArrayList);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new SpacesItemDecoration(40));

        db.collection("users").document(mAuth.getUid())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        redeemedRewards=(List<String>)documentSnapshot.get("rewards");
                        if(redeemedRewards!=null){
                            for (int i=0;i<redeemedRewards.size();i++){
                                db.collection("rewards").document(redeemedRewards.get(i)).get().addOnCompleteListener(task1 -> {
                                    if(task1.isSuccessful()){
                                        DocumentSnapshot snapshot=task1.getResult();
                                        RewardsItem rewardsItem=snapshot.toObject(RewardsItem.class);
                                        rewardsItemArrayList.add(rewardsItem);
                                        adapter.notifyDataSetChanged();
                                    }
                                });
                            }
                        }
                    }

                });

        adapter.setOnItemClickListener(position -> {
            RewardsItem currentItem=rewardsItemArrayList.get(position);
            RewardsDetailsFragment rewardsDetailsFragment=RewardsDetailsFragment.newInstance(currentItem, false);
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,rewardsDetailsFragment).addToBackStack("RedeemedFrag").commit();
        });

        return v;

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
