package com.example.hunter.ui.search;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hunter.Posts;
import com.example.hunter.R;
import com.example.hunter.ui.addPost.AddPostFragment;
import com.example.hunter.ui.home.CommentFragment;
import com.example.hunter.ui.home.LikesFragment;
import com.example.hunter.ui.home.PostsRecyclerViewAdapter;
import com.example.hunter.ui.profile.UserProfileFragment;
import com.google.android.gms.common.api.ApiException;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PlacesFragment extends Fragment {
    private String id;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String TAG="places";
    private Toolbar toolbar;
    private TextView placeName, address, openHours, phoneNum, category, ratingCount, reviewsCount, likesCount, comment;
    private RatingBar ratingBar, priceLvl;
    private ImageView imageView;

    String eateryPlaceName, eateryAddress,eateryPhoneNum;
    Double eateryTotalRate, eateryPriceLvl;
    List<String> eateryCategory,eateryOpenHours;
    private Context context;
    List<PhotoMetadata> eateryPhoto;
    private Button writeReview;
    private RecyclerView eateryReviewsList;
    private PostsRecyclerViewAdapter adapter;
    private String postid;
    private CheckBox like;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
//    private EateriesReviewsRecyclerViewAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    ArrayList<Posts> eateryPostArrayList = new ArrayList<>();
    private OnFragmentInteractionListener mListener;

    public PlacesFragment() {
    }


    public static PlacesFragment newInstance(String id) {
        PlacesFragment fragment = new PlacesFragment();
        Bundle args = new Bundle();
        args.putString("id",id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            id=getArguments().getString("id");

        }
    }



    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.fragment_places, container, false);
        context = container.getContext();
        imageView=v.findViewById(R.id.placeImage);
        placeName=v.findViewById(R.id.placeName);
        ratingBar=v.findViewById(R.id.placeRatingBar);
        ratingCount=v.findViewById(R.id.placeRatingCount);
        category=v.findViewById(R.id.placeCategory);
        address=v.findViewById(R.id.placeAddress);
        openHours=v.findViewById(R.id.placeOpeningHours);
        phoneNum=v.findViewById(R.id.placePhoneNumber);
        priceLvl=v.findViewById(R.id.placePriceLevel);
        reviewsCount=v.findViewById(R.id.placeReviewsCount);
        writeReview=v.findViewById(R.id.writeReview);
//        toolbar=v.findViewById(R.id.toolbarPlaces);
//        toolbar.setNavigationIcon(R.drawable.ic_keyboard_arrow_left_black_24dp);
//        toolbar.setNavigationOnClickListener(v1 -> getFragmentManager().popBackStackImmediate());

        writeReview.setOnClickListener(pos->{
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new AddPostFragment()).commit();
        });

        eateryReviewsList=v.findViewById(R.id.placeUsersReviews);
        eateryReviewsList.setHasFixedSize(true);
        eateryReviewsList.setLayoutManager(new LinearLayoutManager(getActivity()));
        layoutManager = new LinearLayoutManager(getContext());
        eateryReviewsList.setLayoutManager(layoutManager);
        adapter = new PostsRecyclerViewAdapter(eateryPostArrayList);
        eateryReviewsList.setAdapter(adapter);

///////////////////////////////////Current eatery reviews onclick////////////////////////////////////////
        adapter.setOnItemClickListener(new PostsRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(int position) {

            }

            @Override
            public void OnUsernameClick(int position) {
                String chosenUser=eateryPostArrayList.get(position).getUser_id();
                UserProfileFragment userProfileFragment= UserProfileFragment.newInstance(chosenUser, "places");
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,userProfileFragment).addToBackStack("PlacesFrag").commit();
            }
            @Override
            public void OnLikeClick(int position) {
                postid = eateryPostArrayList.get(position).getPostId();
                View view=eateryReviewsList.getLayoutManager().findViewByPosition(position);
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
///////////////////////////////////Current eatery reviews onclick////////////////////////////////////////

            @Override
            public void OnCommentClick(int position) {
                String postid = eateryPostArrayList.get(position).getPostId();
                CommentFragment commentFragment = CommentFragment.newInstance(postid);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, commentFragment).addToBackStack(null).commit();

            }

            @Override
            public void OnPlaceNameClick(int position) {

            }

            @Override
            public void OnOptionClick(int position) {
                View view=eateryReviewsList.getLayoutManager().findViewByPosition(position);
                showMenuPosts(view, position);
            }

            @Override
            public void OnLikeUserClick(int position) {
                postid = eateryPostArrayList.get(position).getPostId();
                LikesFragment likesFragment=LikesFragment.newInstance(postid);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, likesFragment).addToBackStack(null).commit();

            }
        });

/////////////////////////////////Display current eatery details/////////////////////////////////////////
        db.collection("eateries").document(id)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Log.d(TAG, document.getId() + " => " + document.getData());
                            eateryPlaceName = (String) document.get("placeName");
                            eateryTotalRate = (Double) document.get("totalRate");
                            eateryCategory = (List<String>)document.get("category");
                            eateryAddress = (String) document.get("address");
                            eateryPhoneNum = (String) document.get("phoneNum");
                            eateryPriceLvl = (Double) document.get("totalPrice");
                            eateryOpenHours = (List<String>)document.get("weekdayText");

                            placeName.setText(eateryPlaceName);
                            ratingBar.setRating(Float.parseFloat(eateryTotalRate.toString()));
                            ratingCount.setText(eateryTotalRate.toString());
                            category.setText(eateryCategory.toString());
                            address.setText(eateryAddress);
                            if(eateryPhoneNum!=null){
                            phoneNum.setText(eateryPhoneNum);
                            }else{
                                phoneNum.setText("none");
                            }
                            priceLvl.setRating(Float.parseFloat(eateryPriceLvl.toString()));
//                            openHours.setText(eateryOpenHours.toString());

                            List<Place.Field> fields = Arrays.asList(Place.Field.PHOTO_METADATAS, Place.Field.OPENING_HOURS);
                            FetchPlaceRequest placeRequest = FetchPlaceRequest.newInstance(id, fields);
                            String api = "AIzaSyDz5BGny6Lsp7gW-uJznoLVZS4riEdfnF0";
                            Places.initialize(context, api);
                            PlacesClient placesClient = Places.createClient(context);

                            placesClient.fetchPlace(placeRequest).addOnSuccessListener((response) -> {
                                Place place = response.getPlace();
                                PhotoMetadata photoMetadata = place.getPhotoMetadatas().get(0);
                                String attributions = photoMetadata.getAttributions();
                                eateryOpenHours = place.getOpeningHours().getWeekdayText();
                                openHours.setText(eateryOpenHours.toString());

                                FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(photoMetadata)
                                        .build();
                                placesClient.fetchPhoto(photoRequest).addOnSuccessListener((fetchPhotoResponse) -> {
                                    Bitmap bitmap = fetchPhotoResponse.getBitmap();
                                    imageView.setImageBitmap(bitmap);
                                    // imageView.setImageBitmap(bitmap);
                                }).addOnFailureListener((exception) -> {
                                    if (exception instanceof ApiException) {
                                        ApiException apiException = (ApiException) exception;
                                        int statusCode = apiException.getStatusCode();
                                        // Handle error with given status code.
                                        Log.e(TAG, "Place not found: " + exception.getMessage());
                                    }
                                });
                            });

                        } else {
                            Log.d(TAG, "No such document");
                        }
                    } else {
                        Log.w(TAG, "Error getting documents.", task.getException());
                    }
                });
/////////////////////////////////End Display current eatery details/////////////////////////////////////

/////////////////////////////////Display current eatery reviews/////////////////////////////////////////
            db.collection("eateries").document(id).collection("posts").get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (task.getResult().size() > 0) {
                            String postId = (String) document.get("postID");
                            db.collection("posts").document(postId).get().addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    DocumentSnapshot documentSnapshot = task1.getResult();
                                    Posts post = documentSnapshot.toObject(Posts.class);
                                    eateryPostArrayList.add(post);
                                    adapter.notifyDataSetChanged();
                                    reviewsCount.setText(eateryPostArrayList.size() + "");
                                }
                            });
                        }
                    }
                    }
            });
/////////////////////////////////////////////END Display////////////////////////////////////////////////
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

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
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
                                String postid = eateryPostArrayList.get(pos).getPostId();
                                eateryPostArrayList.remove(pos);
                                db.collection("posts").document(postid).delete();
                                db.collection("users").document(mAuth.getUid()).collection("posts").document(postid).delete();
                                db.collection("eateries").document(eateryPostArrayList.get(pos).getPlace_id()).collection("posts").document(postid).delete();
                                db.collection("eateries").document(eateryPostArrayList.get(pos).getPlace_id()).update("postNum",FieldValue.increment(-1));
                                adapter.notifyDataSetChanged();
                            })
                            .create()
                            .show();
                    return true;
                case R.id.editPost:
                    String postid = eateryPostArrayList.get(pos).getPostId();
                    Intent intent=new Intent();
                    intent.putExtra("postid",postid);
                    startActivity(intent);

                default:
                    return false;
            }
        });
        popup.inflate(R.menu.posts);
        popup.show();
    }
}
