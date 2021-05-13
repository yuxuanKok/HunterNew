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


public class BoardRecyclerViewAdapter extends RecyclerView.Adapter<BoardRecyclerViewAdapter.RecyclerViewHolder>  {

    private ArrayList<Eateries2> eateryList;
    private Context context;
    String TAG;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void OnItemClick(int position);

    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public BoardRecyclerViewAdapter(ArrayList<Eateries2> eateryList) {
        this.eateryList = eateryList;
    }

    @NonNull
    @Override
    public BoardRecyclerViewAdapter.RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.board_list_layout,parent,false);
        context = parent.getContext();
        return new RecyclerViewHolder(view,mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull BoardRecyclerViewAdapter.RecyclerViewHolder holder, int position) {
        holder.boardName.setText(eateryList.get(position).getPlaceName());
        holder.number.setText(String.valueOf(position+1)); //(String.valueOf(counter));
        holder.boardCategory.setText("Eatery\n"+eateryList.get(position).getCategory());
        holder.boardRating.setRating(Float.parseFloat(eateryList.get(position).getRating().toString()));
        holder.boardRating.setIsIndicator(true);

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
                holder.boardImage.setImageBitmap(bitmap);
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
        if (eateryList.size()>10){
            return 10;
        }

        return eateryList.size();
    }



    public class RecyclerViewHolder extends RecyclerView.ViewHolder {
        TextView boardName;
        TextView boardCategory;
        TextView number;
        ImageView boardImage;
        RatingBar boardRating;

        public RecyclerViewHolder(@NonNull View itemView,OnItemClickListener listener) {
            super(itemView);
            boardName = itemView.findViewById(R.id.boardName);
            boardCategory = itemView.findViewById(R.id.boardCategory);
            number = itemView.findViewById(R.id.number);
            boardImage = itemView.findViewById(R.id.boardImage);
            boardRating = itemView.findViewById(R.id.boardRating);

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
