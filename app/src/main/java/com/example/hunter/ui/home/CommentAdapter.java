package com.example.hunter.ui.home;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.hunter.Comment;
import com.example.hunter.Posts;
import com.example.hunter.R;
import com.example.hunter.TimeAgo2;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private Context context;
    private ArrayList<Comment> commentList;
    private static FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CommentAdapter.OnItemClickListener mListener;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    public interface OnItemClickListener {
        void OnUsernameClick(int position);

        void OnButtonClick(int position);

    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public CommentAdapter(ArrayList<Comment> comments) {
        this.commentList = comments;
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        public CircleImageView circleImageView;
        public TextView userName, comment, date;
        public Button button;


        public CommentViewHolder(@NonNull View itemView, CommentAdapter.OnItemClickListener mListener) {
            super(itemView);
            circleImageView = itemView.findViewById(R.id.commentPic);
            userName = itemView.findViewById(R.id.commentName);
            comment = itemView.findViewById(R.id.commentCapt);
            button = itemView.findViewById(R.id.commentDelete);
            date = itemView.findViewById(R.id.dateComment);

            userName.setOnClickListener(v -> {
                if (mListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        mListener.OnUsernameClick(position);
                    }
                }
            });
            button.setOnClickListener(v -> {
                if (mListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        mListener.OnButtonClick(position);
                    }
                }
            });
        }
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_row, parent, false);
        CommentViewHolder commentViewHolder = new CommentViewHolder(v, mListener);
        context = parent.getContext();
        return commentViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment currentcomment = commentList.get(position);
        db.collection("users").document(currentcomment.getUser_id()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot documentSnapshot = task.getResult();
                Log.d("DATE", "what");
                String image = documentSnapshot.getString("photo");
                String name = documentSnapshot.getString("username");
                holder.circleImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                Glide.with(context).load(image).into(holder.circleImageView);
                holder.userName.setText(name);
                holder.comment.setText(currentcomment.getComment());
                TimeAgo2 timeAgo2 = new TimeAgo2();
                holder.date.setText(timeAgo2.covertTimeToText(currentcomment.getDate_created()));
                if (!firebaseAuth.getUid().equals(currentcomment.getUser_id())){
                    holder.button.setVisibility(View.INVISIBLE);
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return commentList.size();
    }
}
