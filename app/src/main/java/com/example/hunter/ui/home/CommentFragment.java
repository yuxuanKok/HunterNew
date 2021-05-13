package com.example.hunter.ui.home;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.example.hunter.Comment;
import com.example.hunter.Posts;
import com.example.hunter.R;
import com.example.hunter.ui.profile.UserProfileFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class CommentFragment extends Fragment implements View.OnClickListener {
    private String postid;
    private Toolbar toolbar;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private OnFragmentInteractionListener mListener;
    private TextView addComment;
    private EditText comment;
    private ArrayList<Comment> commentArrayList=new ArrayList<>();
    private RecyclerView recyclerView;
    private CommentAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;


    public CommentFragment() {
        // Required empty public constructor
    }

    public static CommentFragment newInstance(String param1) {
        CommentFragment fragment = new CommentFragment();
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

        commentArrayList=new ArrayList<>();
        View v= inflater.inflate(R.layout.fragment_comment, container, false);
        toolbar=v.findViewById(R.id.toolbarComment);
        toolbar.setNavigationIcon(R.drawable.ic_keyboard_arrow_left_black_24dp);
        toolbar.setNavigationOnClickListener(v1 -> getFragmentManager().popBackStack("HomeFrag", FragmentManager.POP_BACK_STACK_INCLUSIVE));
        addComment=v.findViewById(R.id.commentButton);
        comment=v.findViewById(R.id.commentText);
        recyclerView=v.findViewById(R.id.comments);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new CommentAdapter(commentArrayList);
        recyclerView.setAdapter(adapter);

        db.collection("posts").document(postid).collection("comments").get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                for (QueryDocumentSnapshot documentSnapshot:task.getResult()){
                    Comment comment=documentSnapshot.toObject(Comment.class);
                    commentArrayList.add(comment);
                    sortArrayList();
                    adapter.notifyDataSetChanged();
                    Log.d("CommentFrag", "Good");
                    Log.d("CommentFrag", commentArrayList.get(0).getComment());

                }
            }
            else {
                Log.d("CommentFrag", "WHY");
            }
        });
        Log.d("CommentFrag", postid);


        adapter.setOnItemClickListener(new CommentAdapter.OnItemClickListener() {
            @Override
            public void OnUsernameClick(int position) {
                String chosenUser = commentArrayList.get(position).getUser_id();
                UserProfileFragment userProfileFragment = UserProfileFragment.newInstance(chosenUser, "comment");
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, userProfileFragment, "CommentFrag").addToBackStack("CommentFrag").commit();
            }

            @Override
            public void OnButtonClick(int position) {
                String commentid=commentArrayList.get(position).getCommentid();
                new AlertDialog.Builder(getActivity(),R.style.AlertDialogStyle)
                        .setMessage("Are you sure you want to delete your comment?")
                        .setTitle("Delete Comment")
                        .setCancelable(true)
                        .setPositiveButton("Delete", (dialog, which) -> {
                            commentArrayList.remove(position);
                            db.collection("posts").document(postid).collection("comments").document(commentid).delete();
                            adapter.notifyDataSetChanged();
                        })
                        .create()
                        .show();
            }
        });

        addComment.setOnClickListener(this::onClick );

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
        if(!comment.getText().toString().trim().isEmpty()){
            Date date =Calendar.getInstance().getTime();
            Log.d("CommentFrag", date.getTime()+"");

            Comment newComment=new Comment(mAuth.getUid()+date+"comment",comment.getText().toString(),mAuth.getUid(),date);
            db.collection("posts").document(postid).collection("comments").document(mAuth.getUid()+date+"comment").set(newComment).addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    commentArrayList.add(newComment);
                    adapter.notifyDataSetChanged();
                }
            });
            comment.setText("");
        }
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    private  void  sortArrayList(){
        Collections.sort(commentArrayList, new Comparator<Comment>() {
            @Override
            public int compare(Comment o1, Comment o2) {
                return o1.getDate_created().compareTo(o2.getDate_created());
            }
        });
        //adapter.notifyDataSetChanged();
    }


}
