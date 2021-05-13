package com.example.hunter.ui.search;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.hunter.Eateries2;
import com.example.hunter.R;
import com.example.hunter.User;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class SearchingFragment extends Fragment {

    private SearchView searchView;
    private RecyclerView resultList;

    private RecyclerView.Adapter<RecyclerView.ViewHolder> adapter;
    final int VIEW_TYPE_USER = 0;
    final int VIEW_TYPE_EATERIES = 1;

    private ArrayList<User> userArrayList = new ArrayList<User>();
    private ArrayList<Eateries2> eateriesArrayList = new ArrayList<Eateries2>();

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String TAG;

    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void OnItemClick(int position);

    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_searching, container, false);
        //return inflater.inflate(R.layout.fragment_searching, container, false);

        searchView = root.findViewById(R.id.searchView);

        resultList = root.findViewById(R.id.resultList);
        resultList.setHasFixedSize(true);
        resultList.setLayoutManager(new LinearLayoutManager(getActivity()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText.trim().isEmpty()){
                    userArrayList = new ArrayList<>();
                    eateriesArrayList = new ArrayList<>();
                }
                else {
                    userArrayList = new ArrayList<>();
                    eateriesArrayList = new ArrayList<>();
                    FirestoreUserSearch(newText);
                }
                return false;
            }
        });

//        adapter.setOnItemClickListener(new SearchingFragment.OnItemClickListener() {
//            @Override
//            public void OnItemClick(int position) {
//                String chosenEatery=resultList.get(position).getPlaceID();
//                PlacesFragment placesFragment = PlacesFragment.newInstance(chosenEatery);
//                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,placesFragment).commit();
//            }
//        });

        return root;
    }


    private void FirestoreUserSearch(String searchText) {

        db.collection("users")
                .orderBy("username")
                .startAt(searchText)
                .endAt(searchText + "\uf8ff")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(DocumentChange document :queryDocumentSnapshots.getDocumentChanges()){
                            userArrayList.add(document.getDocument().toObject(User.class));
                            adapter.notifyDataSetChanged();
                        }
                    }
                });


        db.collection("eateries")
                .orderBy("placeName")
                .startAt(searchText)
                .endAt(searchText + "\uf8ff")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()){
                            String placeId = doc.getDocument().getString("placeID");
                            String placeName = doc.getDocument().getString("placeName");
                            List<String> category = (List<String>)doc.getDocument().get("category");
                            String address = doc.getDocument().getString("address");
                            Double rating = doc.getDocument().getDouble("rating");
                            Double price_level = doc.getDocument().getDouble("price_level");

                            Eateries2 eateries = new Eateries2(address, category, placeId, placeName, rating, price_level);
                            eateriesArrayList.add(eateries);
                            adapter.notifyDataSetChanged();
                        }
                    }
                });



        adapter = new RecyclerView.Adapter<RecyclerView.ViewHolder>() {

            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view;

                if (viewType==VIEW_TYPE_USER){
                    view = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.user_list_layout,parent,false);
                    return  new SearchingFragment.UserViewHolder(view);

                }
                if(viewType==VIEW_TYPE_EATERIES){
                    view = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.eateries_list_layout,parent,false);
                    return new SearchingFragment.EateriesViewHolder(view,mListener);
                }
                return null;

            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                if(holder instanceof SearchingFragment.UserViewHolder){
                    ((SearchingFragment.UserViewHolder) holder).userName.setText(userArrayList.get(position).getUsername());
                    ((SearchingFragment.UserViewHolder) holder).category.setText("User");
                    ((SearchingFragment.UserViewHolder) holder).userImage.setImageURI(null);
                    if(userArrayList.get(position).getPhoto()!=null){
                        Uri uri=Uri.parse(userArrayList.get(position).getPhoto());
                        Glide.with(getContext()).load(uri).into(((SearchingFragment.UserViewHolder) holder).userImage);
                    }

                }

                if(holder instanceof SearchingFragment.EateriesViewHolder){
                    ((SearchingFragment.EateriesViewHolder) holder).placeName.setText(eateriesArrayList.get(position-userArrayList.size()).getPlaceName());
                    ((SearchingFragment.EateriesViewHolder) holder).category.setText("Eatery\n"+eateriesArrayList.get(position-userArrayList.size()).getCategory());
                    ((SearchingFragment.EateriesViewHolder) holder).address.setText(Html.fromHtml(eateriesArrayList.get(position-userArrayList.size()).getAddress()));
                    ((EateriesViewHolder) holder).ratingBar.setRating(Float.parseFloat(eateriesArrayList.get(position-userArrayList.size()).getRating().toString()));
                    ((EateriesViewHolder) holder).ratingBar.setIsIndicator(true);
                    ((EateriesViewHolder) holder).priceRateBar.setRating(Float.parseFloat(eateriesArrayList.get(position-userArrayList.size()).getPrice_level().toString()));

                    List<Place.Field> fields = Arrays.asList(Place.Field.PHOTO_METADATAS);
                    FetchPlaceRequest placeRequest = FetchPlaceRequest.newInstance(eateriesArrayList.get(position-userArrayList.size()).getPlaceID(), fields);
                    String api = "AIzaSyDz5BGny6Lsp7gW-uJznoLVZS4riEdfnF0";
                    Places.initialize(getContext(), api);
                    PlacesClient placesClient = Places.createClient(getContext());

                    placesClient.fetchPlace(placeRequest).addOnSuccessListener((response) -> {
                        Place place = response.getPlace();
                        PhotoMetadata photoMetadata = place.getPhotoMetadatas().get(0);
                        String attributions = photoMetadata.getAttributions();

                        FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(photoMetadata)
                                .build();
                        placesClient.fetchPhoto(photoRequest).addOnSuccessListener((fetchPhotoResponse) -> {
                            Bitmap bitmap = fetchPhotoResponse.getBitmap();
                            ((SearchingFragment.EateriesViewHolder) holder).eateryImage.setImageBitmap(bitmap);
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

                }
            }

            @Override
            public int getItemCount() {
                return userArrayList.size()+eateriesArrayList.size();
            }

            @Override
            public int getItemViewType(int position){
                if(position<userArrayList.size()){
                    return VIEW_TYPE_USER;
                }
                if(position-userArrayList.size()<eateriesArrayList.size()){
                    return VIEW_TYPE_EATERIES;
                }
                return -1;
            }
        };
        resultList.setAdapter(adapter);

    }

    private class UserViewHolder extends RecyclerView.ViewHolder {
        TextView userName;
        ImageView userImage;
        TextView category;

        UserViewHolder(View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.userName);
            userImage = itemView.findViewById(R.id.userImage);
            category = itemView.findViewById(R.id.catagory);
        }
    }

    private class EateriesViewHolder extends RecyclerView.ViewHolder{
        TextView placeName;
        TextView category;
        TextView address;
        ImageView eateryImage;
        RatingBar ratingBar,priceRateBar;

        EateriesViewHolder(View itemView,OnItemClickListener listener){
            super(itemView);
            placeName = itemView.findViewById(R.id.placeName);
            category = itemView.findViewById(R.id.catagory);
            address = itemView.findViewById(R.id.address);
            eateryImage = itemView.findViewById(R.id.eateryImage);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            priceRateBar = itemView.findViewById(R.id.priceRateBar);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.OnItemClick(position);
                    }
                }
            });
        }
    }
}
