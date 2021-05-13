package com.example.hunter.ui.rewards;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hunter.Eateries2;
import com.example.hunter.R;
import com.example.hunter.RewardsItem;
import com.example.hunter.RewardsRecyclerViewAdapter;
import com.example.hunter.SpacesItemDecoration;
import com.example.hunter.ui.home.HomeFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class RewardsFragment extends Fragment {
    private OnFragmentInteractionListener mListener;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String TAG = "rewards";
    private RecyclerView recyclerView;
    private RewardsRecyclerViewAdapter adapter;
    private TextView points;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<RewardsItem> rewardsItemArrayList = new ArrayList<>();
    private String name;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private Spinner spinner;
    private ArrayAdapter<CharSequence> spinnerAdapter;
    private int point;

    public RewardsFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_rewards, container, false);
        rewardsItemArrayList = new ArrayList<>();
        recyclerView = v.findViewById(R.id.rewards_rv);
        recyclerView.setHasFixedSize(true);
        layoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new RewardsRecyclerViewAdapter(rewardsItemArrayList);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new SpacesItemDecoration(40));
        spinner = v.findViewById(R.id.rewardsSpinner);
        points = v.findViewById(R.id.userPoints);

        spinnerAdapter = ArrayAdapter.createFromResource(getContext(), R.array.rewardsSpinner, android.R.layout.simple_dropdown_item_1line);
        spinnerAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);

        db.collection("rewards")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            RewardsItem rewardsItem = document.toObject(RewardsItem.class);
                            rewardsItemArrayList.add(rewardsItem);
                            name = rewardsItem.getPlaceName();
                            Log.d(TAG, rewardsItem.getPlaceName());
                            Log.d(TAG, document.getId() + " => " + document.getData());
                            adapter.notifyDataSetChanged();
                        }
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }

                });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        break;

                    case 1:
                        sortPrice();
                        break;

                    case 2:
                        reversePrice();
                        break;

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        db.collection("users").document(firebaseAuth.getUid()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot documentSnapshot = task.getResult();
                point = (int) Math.round(documentSnapshot.getDouble("point"));
                points.setText(String.valueOf(point) + " points");
            }
        });

        adapter.setOnItemClickListener(position -> {
            RewardsItem currentItem = rewardsItemArrayList.get(position);
            RewardsDetailsFragment rewardsDetailsFragment = RewardsDetailsFragment.newInstance(currentItem, true);
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, rewardsDetailsFragment).addToBackStack("RewardsFrag").commit();
        });


        return v;
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void sortPrice() {
        Collections.sort(rewardsItemArrayList, (o1, o2) -> Integer.compare(o1.getPoints(), o2.getPoints()));
        adapter.notifyDataSetChanged();
    }

    private void reversePrice() {
        Collections.sort(rewardsItemArrayList, (o1, o2) -> Integer.compare(o2.getPoints(), o1.getPoints()));
        adapter.notifyDataSetChanged();
    }


    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
