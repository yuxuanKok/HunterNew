package com.example.hunter.ui.home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.view.GestureDetectorCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.example.hunter.DoubleClickListener;
import com.example.hunter.ImageAdapter;
import com.example.hunter.Posts;
import com.example.hunter.R;
import com.example.hunter.TimeAgo2;
import com.example.hunter.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostsRecyclerViewAdapter extends RecyclerView.Adapter<PostsRecyclerViewAdapter.RecyclerViewHolder> implements GestureDetector.OnDoubleTapListener {
    private ArrayList<Posts> postsList;
    private static Context context;
    private OnItemClickListener mListener;
    private static FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private GestureDetector.OnGestureListener gestureListener;

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        Log.d("Posts", "onDoubleTap: " + e.toString()+"needed");

        return true;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        return false;
    }


    public interface OnItemClickListener {
        void OnItemClick(int position);
        void OnUsernameClick(int position);
        void OnLikeClick(int position);
        void OnCommentClick(int position);
        void OnPlaceNameClick(int position);
        void OnOptionClick (int position);
        void OnLikeUserClick(int position);
    }





    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }



    public static class RecyclerViewHolder extends RecyclerView.ViewHolder {
        CircleImageView postProfileImage, captImg;
        TextView postProfileUsername;
        TextView postPlaceId;
        RatingBar postRatingBar;
        RatingBar priceRateBar;
        TextView postDateCreated;
        ViewPager postImage;
        CheckBox postLikeIcon;
        TextView likesCount;
        TextView likesText;
        TextView postDescription;
        TextView postComments;
        Button postOption;


        public RecyclerViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            postProfileImage = itemView.findViewById(R.id.post_profile_image);
            postProfileUsername = itemView.findViewById(R.id.post_profile_username);
            postPlaceId = itemView.findViewById(R.id.post_place_id);
            postRatingBar = itemView.findViewById(R.id.post_rating_bar);
            postDateCreated = itemView.findViewById(R.id.post_date_created);
            postImage = itemView.findViewById(R.id.post_image);
            postLikeIcon = itemView.findViewById(R.id.post_like_icon);
            likesCount = itemView.findViewById(R.id.likesCount);
            likesText = itemView.findViewById(R.id.likesText);
            postDescription = itemView.findViewById(R.id.post_description);
            postComments = itemView.findViewById(R.id.view_comments);
            priceRateBar = itemView.findViewById(R.id.priceRateBar);
            captImg=itemView.findViewById(R.id.captImg);
            postOption=itemView.findViewById(R.id.postOption);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.OnItemClick(position);
                    }
                }
            });

            postProfileUsername.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.OnUsernameClick(position);
                    }
                }
            });

            postLikeIcon.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.OnLikeClick(position);
                    }
                }
            });

            postComments.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.OnCommentClick(position);
                    }
                }
            });

            postPlaceId.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.OnPlaceNameClick(position);
                    }
                }
            });

            likesCount.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.OnLikeUserClick(position);
                    }
                }
            });

            postOption.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.OnOptionClick(position);
                    }
                }
            });


        }

    }

    public PostsRecyclerViewAdapter(ArrayList<Posts> postsList) {
        this.postsList = postsList;

    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_users_post, parent, false);
        RecyclerViewHolder recyclerViewHolder = new RecyclerViewHolder(v, mListener);
        context = parent.getContext();
        return recyclerViewHolder;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull PostsRecyclerViewAdapter.RecyclerViewHolder holder, int position) {
        Posts currentPost = postsList.get(position);
        db.collection("users").document(currentPost.getUser_id()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot documentSnapshot = task.getResult();
                if (documentSnapshot.exists()) {
                    User user = documentSnapshot.toObject(User.class);

                    holder.postProfileImage.setScaleType(CircleImageView.ScaleType.CENTER_CROP);
                    Glide.with(context).load(user.getPhoto()).into(holder.postProfileImage);
                    Glide.with(context).load(user.getPhoto()).into(holder.captImg);

                    db.collection("posts").document(currentPost.getPostId()).get().addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            DocumentSnapshot documentSnapshot1 = task1.getResult();
                            List<String> photoID = (List<String>) documentSnapshot1.get("photo_id");
                            List<String> likesList = (List<String>) documentSnapshot1.get("likes");
                            List<Uri> photoUri = new ArrayList<>();
                            if (photoID!=null){
                                for (int i = 0; i < photoID.size(); i++) {
                                    photoUri.add(Uri.parse(photoID.get(i)));
                                }
                                ImageAdapter imageAdapter = new ImageAdapter(context, photoUri);
                                holder.postImage.setAdapter(imageAdapter);
                                imageAdapter.notifyDataSetChanged();
                            }
                            if (likesList != null) {
                                holder.likesCount.setText(likesList.size() + "");
                                for (int i = 0; i < likesList.size(); i++) {
                                    if (mAuth.getUid().equals(likesList.get(i))) {
                                        holder.postLikeIcon.setChecked(true);
                                    }
                                }
                            } else {
                                holder.likesCount.setText("0");
                            }
                            db.collection("posts").document(currentPost.getPostId()).collection("comments").get().addOnCompleteListener(task2 -> {
                                if (task2.isSuccessful()) {
                                    if (task2.getResult().size() > 0) {
                                        holder.postComments.setText("View all " + task2.getResult().size() + " comments...");
                                    } else {
                                        holder.postComments.setText(R.string.addComment);

                                    }
                                } else {
                                    holder.postComments.setText(R.string.addComment);
                                }
                            });
                            if(!mAuth.getUid().equals(currentPost.getUser_id())){
                                holder.postOption.setVisibility(View.INVISIBLE);
                            }

                        }
                    });

                    holder.postProfileUsername.setText(user.getUsername());
                    db.collection("eateries").document(currentPost.getPlace_id()).get().addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            DocumentSnapshot documentSnapshot1 = task1.getResult();
                            try {
                                String placeName = documentSnapshot1.get("placeName").toString();
                                holder.postPlaceId.setText(placeName);
                            }catch (Exception e){

                            }

                        }
                    });
                    holder.postRatingBar.setRating(Float.parseFloat(currentPost.getRating().toString()));
                    holder.priceRateBar.setRating(Float.parseFloat(currentPost.getPriceLvl().toString()));
                    TimeAgo2 timeAgo2 = new TimeAgo2();
                    holder.postDateCreated.setText(timeAgo2.covertTimeToText(currentPost.getDate_created()));
                    holder.postDescription.setText(currentPost.getCaption());
                    holder.likesText.setText("Likes");
                }

            }

        });

    }

    @Override
    public int getItemCount() {
        return postsList.size();
    }
}
