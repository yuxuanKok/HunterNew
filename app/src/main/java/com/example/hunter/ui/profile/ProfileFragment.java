package com.example.hunter.ui.profile;

import androidx.lifecycle.ViewModelProviders;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.hunter.MainActivity;
import com.example.hunter.Posts;
import com.example.hunter.ProfileSettings;
import com.example.hunter.R;
import com.example.hunter.User;
import com.example.hunter.ui.addPost.EditPostActivity;
import com.example.hunter.ui.home.CommentFragment;
import com.example.hunter.ui.home.LikesFragment;
import com.example.hunter.ui.home.PostsRecyclerViewAdapter;
import com.example.hunter.ui.rewards.RedeemedRewardsFragment;
import com.example.hunter.ui.rewards.RewardsDetailsFragment;
import com.example.hunter.ui.search.PlacesFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

public class ProfileFragment extends Fragment implements View.OnClickListener {

    private TextView username, postNum, following, followers, likesCount, comment;
    private CircleImageView profilePic;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String TAG, postid;
    private CheckBox like;
    private  Button options;
    private MenuItem signOut;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String userId, placeid;
    ArrayList<Posts> allPostsArrayList = new ArrayList<>();
    private RecyclerView allUsersPostList;
    private PostsRecyclerViewAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private Context context;

    public static ProfileFragment newInstance(String userid) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString("userID", userid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments()!=null){
            userId=getArguments().getString("userID");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        allPostsArrayList=new ArrayList<>();
        View myLayout = inflater.inflate(R.layout.profile_fragment, container, false);
        profilePic = myLayout.findViewById(R.id.profilePic);
        username = myLayout.findViewById(R.id.usernameProfile);
        options = myLayout.findViewById(R.id.options);
        signOut = myLayout.findViewById(R.id.signOut);
        postNum=myLayout.findViewById(R.id.postNumCount);
        like = myLayout.findViewById(R.id.post_like_icon);
        likesCount=myLayout.findViewById(R.id.likesCount);
        followers=myLayout.findViewById(R.id.followerNumCount);
        following=myLayout.findViewById(R.id.followingNumCount);
        context=myLayout.getContext();
        comment=myLayout.findViewById(R.id.view_comments);
        allUsersPostList=myLayout.findViewById(R.id.userPosts);
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
//                String chosenUser=allPostsArrayList.get(position).getUser_id();
//                UserProfileFragment userProfileFragment=UserProfileFragment.newInstance(chosenUser);
//                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,userProfileFragment).commit();
            }
            @Override
            public void OnLikeClick(int position) {
                postid = allPostsArrayList.get(position).getPostId();
                View view=allUsersPostList.getLayoutManager().findViewByPosition(position);
                like=view.findViewById(R.id.post_like_icon);
                likesCount=view.findViewById(R.id.likesCount);
                if (like.isChecked()) {
                    db.collection("posts").document(postid).update("likes", FieldValue.arrayUnion(mAuth.getUid()));
                    likesCount.setText(Integer.parseInt(likesCount.getText().toString())+1+"");

                } else {
                    db.collection("posts").document(postid).update("likes", FieldValue.arrayRemove(mAuth.getUid()));
                    likesCount.setText(Integer.parseInt(likesCount.getText().toString())-1+"");

                }

            }
            @Override
            public void OnPlaceNameClick(int position) {
                placeid=allPostsArrayList.get(position).getPlace_id();
                PlacesFragment placesFragment = PlacesFragment.newInstance(placeid);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,placesFragment).addToBackStack("ProfileFrag").commit();
            }

            @Override
            public void OnOptionClick(int position) {
                View view=allUsersPostList.getLayoutManager().findViewByPosition(position);
                showMenuPosts(view, position);
            }

            @Override
            public void OnLikeUserClick(int position) {
                postid = allPostsArrayList.get(position).getPostId();
                LikesFragment likesFragment=LikesFragment.newInstance(postid);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, likesFragment).addToBackStack(null).commit();

            }

            @Override
            public void OnCommentClick(int position) {
                String postid = allPostsArrayList.get(position).getPostId();
                CommentFragment commentFragment = CommentFragment.newInstance(postid);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, commentFragment).addToBackStack(null).commit();

            }
        });

        userId = mAuth.getCurrentUser().getUid();

        db.collection("users").document(userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Log.d(TAG, document.getId() + " => " + document.getData());
                            username.setText(document.toObject(User.class).getUsername());
                            String photoString = (String)document.get("photo");
                            List<String> followingList=(List<String>)document.get("following");
                            List<String> followerList=(List<String>)document.get("followers");
                            if(followerList!=null){
                                followers.setText(followerList.size()+"");
                            }
                            if(followingList!=null){
                                following.setText(followingList.size()+"");
                            }
                            profilePic.setImageURI(null);
                            if (photoString != null) {
                                Uri uri = Uri.parse(photoString);
                                Glide.with(context).load(uri).into(profilePic);
                            }
                            Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        } else {
                            Log.d(TAG, "No such document");
                        }
                    } else {
                        Log.w(TAG, "Error getting documents.", task.getException());
                    }
                });

        db.collection("users").document(mAuth.getUid()).collection("posts").get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String postid=(String)document.get("postID");
                    db.collection("posts").document(postid).get().addOnCompleteListener(task1 -> {
                        if(task1.isSuccessful()){
                            DocumentSnapshot documentSnapshot=task1.getResult();
                            Posts post=documentSnapshot.toObject(Posts.class);
                            allPostsArrayList.add(post);
                            sortArrayList();
                            adapter.notifyDataSetChanged();
                            postNum.setText(allPostsArrayList.size()+"");
                        }
                    });
                }
            }
        });

        options.setOnClickListener(this);

        return myLayout;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        mViewModel = ViewModelProviders.of(this).get(ProfileViewModel.class);
    }

    public void showMenu(View v) {
        PopupMenu popup = new PopupMenu(getContext(), v);
        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.signOut:
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(getContext(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    return true;

                case R.id.settings:
                    Intent intent1 = new Intent(getContext(), ProfileSettings.class);
                    startActivity(intent1);

                case R.id.redeemedRewards:
                    RedeemedRewardsFragment redeemedRewardsFragment = new RedeemedRewardsFragment();
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, redeemedRewardsFragment).commit();

                default:
                    return false;
            }
        });
        popup.inflate(R.menu.profile);
        popup.show();
    }
    public void showMenuPosts(View v, int pos) {
        PopupMenu popup = new PopupMenu(getContext(), v);
        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.deletePost:
                    new AlertDialog.Builder(getActivity(), R.style.AlertDialogStyle)
                            .setMessage("Are you sure you want to delete your post?")
                            .setTitle("Delete Post")
                            .setCancelable(true)
                            .setPositiveButton("Delete", (dialog, which) -> {
                                String postid = allPostsArrayList.get(pos).getPostId();
                                allPostsArrayList.remove(pos);
                                db.collection("posts").document(postid).delete();
                                db.collection("users").document(mAuth.getUid()).collection("posts").document(postid).delete();
                                db.collection("eateries").document(allPostsArrayList.get(pos).getPlace_id()).collection("posts").document(postid).delete();
                                db.collection("eateries").document(allPostsArrayList.get(pos).getPlace_id()).update("postNum",FieldValue.increment(-1));
                                adapter.notifyDataSetChanged();
                            })
                            .create()
                            .show();
                    return true;
                case R.id.editPost:
                    String postid = allPostsArrayList.get(pos).getPostId();
                    Intent intent=new Intent(getActivity(),EditPostActivity.class);
                    intent.putExtra("postid",postid);
                    startActivity(intent);

                default:
                    return false;
            }
        });
        popup.inflate(R.menu.posts);
        popup.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.options:
                showMenu(v);
        }
    }

    private  void  sortArrayList(){
        Collections.sort(allPostsArrayList, new Comparator<Posts>() {
            @Override
            public int compare(Posts o1, Posts o2) {
                return o2.getDate_created().compareTo(o1.getDate_created());
            }
        });
        //adapter.notifyDataSetChanged();
    }

}
