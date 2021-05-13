package com.example.hunter.ui.search;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hunter.Eateries2;
import com.example.hunter.R;
import com.google.android.gms.common.api.ApiException;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class SearchRecyclerViewAdapter extends RecyclerView.Adapter<SearchRecyclerViewAdapter.RecyclerViewHolder>  {

    private ArrayList<Eateries2> eateryList;
    private Context context;
    String TAG;
    private OnItemClickListener mListener;

    public SearchRecyclerViewAdapter(ArrayList<Eateries2> eateryList) {
        this.eateryList = eateryList;
    }

    public interface OnItemClickListener {
        void OnItemClick(int position);

    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    @NonNull
    @Override
    public SearchRecyclerViewAdapter.RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.eateries_list_layout,parent,false);
        context = parent.getContext();
        return new RecyclerViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchRecyclerViewAdapter.RecyclerViewHolder holder, int position) {
        holder.placeName.setText(eateryList.get(position).getPlaceName());
        holder.address.setText(eateryList.get(position).getAddress());
        holder.category.setText("Eatery\n"+eateryList.get(position).getCategory());
        holder.ratingBar.setRating(Float.parseFloat(eateryList.get(position).getRating().toString()));
        holder.ratingBar.setIsIndicator(true);
        holder.priceRateBar.setRating(Float.parseFloat(eateryList.get(position).getPrice_level().toString()));
        holder.priceRateBar.setIsIndicator(true);

        List<Place.Field> fields = Arrays.asList(Place.Field.PHOTO_METADATAS);
        FetchPlaceRequest placeRequest = FetchPlaceRequest.newInstance(eateryList.get(position).getPlaceID(), fields);
        String api = "AIzaSyDz5BGny6Lsp7gW-uJznoLVZS4riEdfnF0";
        Places.initialize(context, api);
        PlacesClient placesClient = Places.createClient(context);

        placesClient.fetchPlace(placeRequest).addOnSuccessListener((response) -> {
            Place place = response.getPlace();
            PhotoMetadata photoMetadata = place.getPhotoMetadatas().get(0);
            String attributions = photoMetadata.getAttributions();

            FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(photoMetadata)
                    .build();
            placesClient.fetchPhoto(photoRequest).addOnSuccessListener((fetchPhotoResponse) -> {
                Bitmap bitmap = fetchPhotoResponse.getBitmap();
                holder.eateryImage.setImageBitmap(bitmap);
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

    @Override
    public int getItemCount() {
        return eateryList.size();
    }

    public void filterList(ArrayList<Eateries2> filteredList){
        eateryList = filteredList;
        notifyDataSetChanged();
    }


    public class RecyclerViewHolder extends RecyclerView.ViewHolder {
        TextView placeName;
        TextView category;
        TextView address;
        ImageView eateryImage;
        RatingBar ratingBar,priceRateBar;

        public RecyclerViewHolder(@NonNull View itemView, OnItemClickListener listener) {
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
