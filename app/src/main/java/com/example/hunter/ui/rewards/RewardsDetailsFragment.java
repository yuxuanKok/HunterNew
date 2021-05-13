package com.example.hunter.ui.rewards;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.hunter.R;
import com.example.hunter.RewardsItem;
import com.example.hunter.User;
import com.example.hunter.ui.search.PlacesFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class RewardsDetailsFragment extends Fragment implements View.OnClickListener {

    private OnFragmentInteractionListener mListener;
    private RewardsItem rewardsItem;
    private ImageView imageView;
    private TextView placeName;
    private TextView title;
    private TextView desc;
    private TextView points;
    private TextView terms;
    private TextView availableUntil;
    private Button redeem;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private User user;
    private Context context;
    private Boolean boo;
    private List<String> rewardsid = new ArrayList<>();
    private Toolbar toolbar;


    public RewardsDetailsFragment() {
        // Required empty public constructor
    }


    public static RewardsDetailsFragment newInstance(RewardsItem rewardsItem, Boolean boo) {
        RewardsDetailsFragment fragment = new RewardsDetailsFragment();
        Bundle args = new Bundle();
        args.putParcelable("rewardsItem", rewardsItem);
        args.putBoolean("boo", boo);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            rewardsItem = getArguments().getParcelable("rewardsItem");
            boo = getArguments().getBoolean("boo");
            Log.d("rewardsfrag", rewardsItem.getImages());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_rewards_details, container, false);
        toolbar = v.findViewById(R.id.toolbar2);
        imageView = v.findViewById(R.id.image);
        placeName = v.findViewById(R.id.placeName);
        title = v.findViewById(R.id.title);
        desc = v.findViewById(R.id.description);
        points = v.findViewById(R.id.points);
        terms = v.findViewById(R.id.terms);
        redeem = v.findViewById(R.id.redeem);
        availableUntil = v.findViewById(R.id.availableUntil);
        redeem.setOnClickListener(this::onClick);
        context = v.getContext();

        toolbar.setNavigationIcon(R.drawable.ic_keyboard_arrow_left_black_24dp);
        if(boo){
            toolbar.setNavigationOnClickListener(v1 -> getFragmentManager().popBackStack("RewardsFrag", FragmentManager.POP_BACK_STACK_INCLUSIVE));
        }

        if (!boo) {
            redeem.setText("Use");
            toolbar.setNavigationOnClickListener(v1 -> getFragmentManager().popBackStack("RedeemedFrag", FragmentManager.POP_BACK_STACK_INCLUSIVE));
        }

        placeName.setText(rewardsItem.getPlaceName());
        title.setText(rewardsItem.getTitle());
        desc.setText(rewardsItem.getDesc());
        points.setText("Points Needed:\t" + rewardsItem.getPoints());
        terms.setText(rewardsItem.getTerms());
        SimpleDateFormat curFormater = new SimpleDateFormat("dd/MM/yyyy");
        Date date = rewardsItem.getAvailableUntil();
        String date2 = curFormater.format(date);
        availableUntil.setText("Available Until:\t" + date2);
        Glide.with(v.getContext()).load(Uri.parse(rewardsItem.getImages())).into(imageView);

        if (!boo) {
            db.collection("users").document(mAuth.getUid()).get().addOnCompleteListener(task -> {
                DocumentSnapshot documentSnapshot = task.getResult();
                rewardsid = (List<String>) documentSnapshot.get("rewards");
                for (int i = 0; i < rewardsid.size(); i++) {
                    if (rewardsItem.getRewardsID().equals(rewardsid.get(i))) {
//                        redeem.setText("Use");
                        redeem.setClickable(true);
                    }

                }
            });
        } else {
            db.collection("users").document(mAuth.getUid()).get().addOnCompleteListener(task -> {
                DocumentSnapshot documentSnapshot = task.getResult();

                rewardsid = (List<String>) documentSnapshot.get("rewards");
                if (rewardsid != null) {
                    for (int i = 0; i < rewardsid.size(); i++) {
                        if (rewardsItem.getRewardsID().equals(rewardsid.get(i))) {
                            redeem.setText("Redeemed");
                            redeem.setClickable(false);
                            redeem.setBackgroundColor(getResources().getColor(R.color.fishMain));
                        }

                    }

                }


            });
        }


        return v;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.placeName:
                PlacesFragment placesFragment = PlacesFragment.newInstance(rewardsItem.getPlaceID());
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, placesFragment).commit();
                break;
            case R.id.redeem:
                if (!boo) {
                    db.collection("users").document(mAuth.getUid()).update("rewards", FieldValue.arrayRemove(rewardsItem.getRewardsID())).addOnCompleteListener(task -> {
                        redeem.setText("Used");
                        redeem.setClickable(false);
                    });
                } else {
                    db.collection("users").document(mAuth.getUid()).get().addOnCompleteListener(task -> {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        user = documentSnapshot.toObject(User.class);
                        if (user.getPoint() >= rewardsItem.getPoints()) {
                            redeem.setText("Redeemed");
                            redeem.setClickable(false);
                            db.collection("users").document(mAuth.getUid()).update("point", FieldValue.increment(-1 * rewardsItem.getPoints()),
                                    "rewards", FieldValue.arrayUnion(rewardsItem.getRewardsID()));
                        } else
                            Toast.makeText(context, "You don't have enough points!", Toast.LENGTH_LONG).show();
                    });
                }

                break;
        }
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
