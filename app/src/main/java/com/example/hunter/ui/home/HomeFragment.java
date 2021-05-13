package com.example.hunter.ui.home;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.example.hunter.Posts;
import com.example.hunter.R;
import com.example.hunter.ui.addPost.EditPostActivity;
import com.example.hunter.ui.profile.UserProfileFragment;
import com.example.hunter.ui.search.PlacesFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class HomeFragment extends Fragment implements View.OnClickListener{

    private RecyclerView allUsersPostList;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private TextView likesCount, comment;
    private PostsRecyclerViewAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private CheckBox like;
    private Context context;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String TAG = "posts", postid, placeid;
    ArrayList<Posts> allPostsArrayList = new ArrayList<>();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        allPostsArrayList=new ArrayList<>();

        View v = inflater.inflate(R.layout.fragment_home, container, false);
        like = v.findViewById(R.id.post_like_icon);
        likesCount = v.findViewById(R.id.likesCount);
        allUsersPostList = v.findViewById(R.id.all_users_post_list);
        comment = v.findViewById(R.id.view_comments);
        allUsersPostList.setHasFixedSize(true);
        allUsersPostList.setLayoutManager(new LinearLayoutManager(getActivity()));
        layoutManager = new LinearLayoutManager(getContext());
        allUsersPostList.setLayoutManager(layoutManager);
        adapter = new PostsRecyclerViewAdapter(allPostsArrayList);
        allUsersPostList.setAdapter(adapter);
        context=v.getContext();


        db.collection("posts").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Posts postsList = document.toObject(Posts.class);
                    allPostsArrayList.add(postsList);
                    sortArrayList();
                    adapter.notifyDataSetChanged();
                }
            } else {
                Log.d(TAG, "Error getting documents: ", task.getException());
            }

        });

        adapter.setOnItemClickListener(new PostsRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(int position) {

            }

            @Override
            public void OnUsernameClick(int position) {
                String chosenUser = allPostsArrayList.get(position).getUser_id();
                UserProfileFragment userProfileFragment = UserProfileFragment.newInstance(chosenUser,"home");
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, userProfileFragment).addToBackStack("HomeFrag").commit();
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
            public void OnOptionClick(int position) {
                View view=allUsersPostList.getLayoutManager().findViewByPosition(position);
                showMenuPosts(view, position);
            }

            @Override
            public void OnLikeUserClick(int position) {
                postid = allPostsArrayList.get(position).getPostId();
                LikesFragment likesFragment=LikesFragment.newInstance(postid);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, likesFragment).addToBackStack("HomeFrag").commit();

            }

            @Override
            public void OnCommentClick(int position) {
                postid = allPostsArrayList.get(position).getPostId();
                CommentFragment commentFragment = CommentFragment.newInstance(postid);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, commentFragment).addToBackStack("HomeFrag").commit();
            }

            @Override
            public void OnPlaceNameClick(int position) {
                placeid=allPostsArrayList.get(position).getPlace_id();
                PlacesFragment placesFragment = PlacesFragment.newInstance(placeid);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,placesFragment).addToBackStack("HomeFrag").commit();
            }
        });





        return v;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.post_like_icon:
                Log.d(TAG, "OK LIKE");


        }
    }

    public void showMenuPosts(View v, int pos) {
        PopupMenu popup = new PopupMenu(getContext(), v, Gravity.CENTER | Gravity.RIGHT);
        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.deletePost:
                    new AlertDialog.Builder(getActivity(), R.style.AlertDialogStyle)
                            .setMessage("Are you sure you want to delete your post?")
                            .setTitle("Delete Post")
                            .setCancelable(true)
                            .setPositiveButton("Delete", (dialog, which) -> {
                                allPostsArrayList.remove(pos);
                                String postid = allPostsArrayList.get(pos).getPostId();
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
                    Intent intent=new Intent(getActivity(), EditPostActivity.class);
                    intent.putExtra("postid",postid);
                    startActivity(intent);

                default:
                    return false;
            }
        });
        popup.inflate(R.menu.posts);
        popup.show();
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