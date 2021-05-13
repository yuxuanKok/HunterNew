package com.example.hunter.ui.profile;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.hunter.Posts;
import com.example.hunter.R;
import com.example.hunter.User;
import com.example.hunter.ui.home.CommentFragment;
import com.example.hunter.ui.home.LikesFragment;
import com.example.hunter.ui.home.PostsRecyclerViewAdapter;
import com.example.hunter.ui.search.PlacesFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class UserProfileFragment extends Fragment implements View.OnClickListener {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CircleImageView proPic;
    private TextView username, postNum, followNum, followingNum, likesCount, comment;
    private CheckBox follow, like;
    private User user;
    private Context context;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private Toolbar toolbar;
    private String userId, usernameUser, postid, frag, placeid;
    private OnFragmentInteractionListener mListener;
    ArrayList<Posts> allPostsArrayList = new ArrayList<>();
    private RecyclerView allUsersPostList;
    private PostsRecyclerViewAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;


    public UserProfileFragment() {
        // Required empty public constructor
    }

    public static UserProfileFragment newInstance(String userid, String frag) {
        UserProfileFragment fragment = new UserProfileFragment();
        Bundle args = new Bundle();
        args.putString("userID", userid);
        args.putString("frag",frag);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userId = getArguments().getString("userID");
            frag=getArguments().getString("frag");
            if (userId.equals(mAuth.getUid())) {
                ProfileFragment profileFragment = new ProfileFragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, profileFragment).commit();
            } else {
                db.collection("users").document(userId).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        user = documentSnapshot.toObject(User.class);
                        Glide.with(context).load(user.getPhoto()).into(proPic);
                        username.setText(user.getUsername());
                        List<String> followingList = (List<String>) documentSnapshot.get("following");
                        List<String> followerList = (List<String>) documentSnapshot.get("followers");
                        if (followerList != null) {
                            followNum.setText(followerList.size() + "");
                            for (String fol : followerList) {
                                if (fol.equals(mAuth.getUid())) {
                                    follow.setChecked(true);
                                    follow.setText(getResources().getText(R.string.followingButton));
                                    follow.setTextColor(Color.BLACK);
                                    follow.setBackgroundTintList(ContextCompat.getColorStateList(context,R.color.places_text_white_alpha_26));
                                }
                            }
                        }
                        if (followingList != null) {
                            followingNum.setText(followingList.size() + "");
                        }

                    }
                });
            }

        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_user_profile, container, false);
        allPostsArrayList=new ArrayList<>();
        proPic = v.findViewById(R.id.profilePicUser);
        username = v.findViewById(R.id.usernameProfileUser);
        postNum = v.findViewById(R.id.postNumCountUser);
        followNum = v.findViewById(R.id.followerNumCountUser);
        followingNum = v.findViewById(R.id.followingNumCountUser);
        follow = v.findViewById(R.id.followButton);
        toolbar = v.findViewById(R.id.toolbar5);
        comment = v.findViewById(R.id.view_comments);
        like = v.findViewById(R.id.post_like_icon);
        likesCount = v.findViewById(R.id.likesCount);
        context = v.getContext();

        toolbar.setNavigationIcon(R.drawable.ic_keyboard_arrow_left_black_24dp);
        db.collection("users").document(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot documentSnapshot = task.getResult();
                usernameUser = (String) documentSnapshot.get("displayName");
                toolbar.setTitle(usernameUser);
            }
        });

        if(frag.equals("comment")){
            toolbar.setNavigationOnClickListener(v1 -> getFragmentManager().popBackStack("CommentFrag", FragmentManager.POP_BACK_STACK_INCLUSIVE));
        }else if(frag.equals("home")) {
            toolbar.setNavigationOnClickListener(v1 -> getFragmentManager().popBackStack("HomeFrag", FragmentManager.POP_BACK_STACK_INCLUSIVE));
        }
        else if(frag.equals("like")){
            toolbar.setNavigationOnClickListener(v1 -> getFragmentManager().popBackStack("LikeFrag", FragmentManager.POP_BACK_STACK_INCLUSIVE));
        }
        else if(frag.equals("places")){
            toolbar.setNavigationOnClickListener(v1 -> getFragmentManager().popBackStack("PlacesFrag", FragmentManager.POP_BACK_STACK_INCLUSIVE));
        }

        follow.setOnClickListener(this::onClick);

        allUsersPostList = v.findViewById(R.id.userPostsUser);
        allUsersPostList.setHasFixedSize(true);
        allUsersPostList.setLayoutManager(new LinearLayoutManager(getActivity()));
        layoutManager = new LinearLayoutManager(getContext());
        allUsersPostList.setLayoutManager(layoutManager);
        adapter = new PostsRecyclerViewAdapter(allPostsArrayList);
        allUsersPostList.setAdapter(adapter);
        adapter.setOnItemClickListener(new PostsRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(int position) {

            }

            @Override
            public void OnUsernameClick(int position) {

            }

            @Override
            public void OnLikeClick(int position) {
                postid = allPostsArrayList.get(position).getPostId();
                View view = allUsersPostList.getLayoutManager().findViewByPosition(position);
                like = view.findViewById(R.id.post_like_icon);
                likesCount = view.findViewById(R.id.likesCount);
                if (like.isChecked()) {
                    db.collection("posts").document(postid).update("likes", FieldValue.arrayUnion(mAuth.getUid()));
                    likesCount.setText(Integer.parseInt(likesCount.getText().toString()) + 1 + "");

                } else {
                    db.collection("posts").document(postid).update("likes", FieldValue.arrayRemove(mAuth.getUid()));
                    likesCount.setText(Integer.parseInt(likesCount.getText().toString()) - 1 + "");

                }

            }

            @Override
            public void OnCommentClick(int position) {
                String postid = allPostsArrayList.get(position).getPostId();
                CommentFragment commentFragment = CommentFragment.newInstance(postid);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, commentFragment).addToBackStack("UserFrag").commit();

            }

            @Override
            public void OnPlaceNameClick(int position) {
                placeid=allPostsArrayList.get(position).getPlace_id();
                PlacesFragment placesFragment = PlacesFragment.newInstance(placeid);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,placesFragment).addToBackStack("UserFrag").commit();
            }

            @Override
            public void OnOptionClick(int position) {

            }

            @Override
            public void OnLikeUserClick(int position) {
                postid = allPostsArrayList.get(position).getPostId();
                LikesFragment likesFragment = LikesFragment.newInstance(postid);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, likesFragment).addToBackStack(null).commit();

            }


        });

        db.collection("users").document(userId).collection("posts").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String postid = (String) document.get("postID");
                    db.collection("posts").document(postid).get().addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            DocumentSnapshot documentSnapshot = task1.getResult();
                            Posts post = documentSnapshot.toObject(Posts.class);
                            allPostsArrayList.add(post);
                            sortArrayList();
                            adapter.notifyDataSetChanged();
                            postNum.setText(allPostsArrayList.size() + "");
                        }
                    });
                }
            }
        });

        return v;
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.followButton:
                if (!follow.isChecked()) {
                    db.collection("users").document(mAuth.getUid()).update("following", FieldValue.arrayRemove(user.getUid())).addOnCompleteListener(task -> {
                        db.collection("users").document(user.getUid()).update("followers", FieldValue.arrayRemove(mAuth.getUid()));
                        follow.setText(R.string.follow);
                        followNum.setText(Integer.parseInt(followNum.getText().toString()) - 1 +"");
                        follow.setTextColor(ContextCompat.getColorStateList(context,R.color.colorPrimaryDark));
                        follow.setBackgroundTintList(ContextCompat.getColorStateList(context,R.color.colorPrimary));
                    });
                } else {
                    db.collection("users").document(mAuth.getUid()).update("following", FieldValue.arrayUnion(user.getUid())).addOnCompleteListener(task -> {
                        db.collection("users").document(user.getUid()).update("followers", FieldValue.arrayUnion(mAuth.getUid()));
                        follow.setText(R.string.followingButton);
                        followNum.setText(Integer.parseInt(followNum.getText().toString()) + 1 +"");
                        follow.setTextColor(Color.BLACK);
                        follow.setBackgroundTintList(ContextCompat.getColorStateList(context,R.color.places_text_white_alpha_26));
                    });
                }
                break;

        }
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    private  void  sortArrayList(){
        Collections.sort(allPostsArrayList, new Comparator<Posts>() {
            @Override
            public int compare(Posts o1, Posts o2) {
                return o2.getDate_created().compareTo(o1.getDate_created());
            }
        });
    }
}
