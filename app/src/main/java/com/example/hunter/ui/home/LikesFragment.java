package com.example.hunter.ui.home;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.hunter.R;
import com.example.hunter.ui.profile.UserProfileFragment;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;


public class LikesFragment extends Fragment {

    private String postid;
    private OnFragmentInteractionListener mListener;
    private Toolbar toolbar;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private LikesAdapter adapter;
    private ArrayList<String> arrayList= new ArrayList<>();

    public LikesFragment() {
        // Required empty public constructor
    }

    public static LikesFragment newInstance(String param1) {
        LikesFragment fragment = new LikesFragment();
        Bundle args = new Bundle();
        args.putString("postid",param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            postid=getArguments().getString("postid");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_likes, container, false);
        toolbar=v.findViewById(R.id.toolbarLikes);
        toolbar.setNavigationIcon(R.drawable.ic_keyboard_arrow_left_black_24dp);
        toolbar.setNavigationOnClickListener(v1 -> getFragmentManager().popBackStack("HomeFrag", FragmentManager.POP_BACK_STACK_INCLUSIVE));

        recyclerView=v.findViewById(R.id.rvLikes);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new LikesAdapter(arrayList);
        recyclerView.setAdapter(adapter);

        db.collection("posts").document(postid).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                DocumentSnapshot documentSnapshot=task.getResult();
                List<String> likesList=(List<String>)documentSnapshot.get("likes");
                if(likesList!=null){
                    for (int i=0;i<likesList.size();i++){
                        arrayList.add(likesList.get(i));
                        adapter.notifyDataSetChanged();
                    }
                }
                Log.d("likes", likesList.get(0));
            }
        });

        adapter.setOnItemClickListener(position -> {
            String chosenUser=arrayList.get(position);
            UserProfileFragment userProfileFragment = UserProfileFragment.newInstance(chosenUser, "like");
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, userProfileFragment).addToBackStack("LikeFrag").commit();
        });
        return  v;
    }


//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
