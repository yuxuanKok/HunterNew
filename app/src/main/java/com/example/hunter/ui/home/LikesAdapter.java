package com.example.hunter.ui.home;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.hunter.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class LikesAdapter extends RecyclerView.Adapter<LikesAdapter.LikesViewHolder> {
    private LikesAdapter.OnItemClickListener mListener;
    private Context context;
    private List<String> likesList;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public LikesAdapter(List<String> arrayList) {
        likesList = arrayList;
    }

    public interface OnItemClickListener {
        void OnUsernameClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public static class LikesViewHolder extends RecyclerView.ViewHolder {
        public CircleImageView circleImageView;
        public TextView userName;

        public LikesViewHolder(@NonNull View itemView, LikesAdapter.OnItemClickListener mListener) {
            super(itemView);
            circleImageView = itemView.findViewById(R.id.likesPic);
            userName = itemView.findViewById(R.id.likesname);

            userName.setOnClickListener(v -> {
                if (mListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        mListener.OnUsernameClick(position);
                    }
                }
            });
        }
    }


    @NonNull
    @Override
    public LikesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.likes_row, parent, false);
        LikesAdapter.LikesViewHolder likesViewHolder = new LikesViewHolder(v, mListener);
        context = parent.getContext();
        return likesViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull LikesViewHolder holder, int position) {
        String currentId = likesList.get(position);
        db.collection("users").document(currentId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot documentSnapshot = task.getResult();
                holder.circleImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                Glide.with(context).load(documentSnapshot.getString("photo")).into(holder.circleImageView);
                holder.userName.setText(documentSnapshot.getString("username"));
                Log.d("likes", "great");

            }
        });
    }

    @Override
    public int getItemCount() {
        return likesList.size();
    }


}
