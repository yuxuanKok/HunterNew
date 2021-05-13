package com.example.hunter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.libraries.places.api.model.Place;

import java.util.List;

public class PlacesRecyclerViewAdapter extends
        RecyclerView.Adapter<PlacesRecyclerViewAdapter.ViewHolder> {

    private List<Place> placesList;
    private Context context;

    public PlacesRecyclerViewAdapter(List<Place> list, Context ctx) {
        placesList = list;
        context = ctx;
    }
    @Override
    public int getItemCount() {
        return placesList.size();
    }

    @Override
    public PlacesRecyclerViewAdapter.ViewHolder
    onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.eateries_list_layout, parent, false);

        PlacesRecyclerViewAdapter.ViewHolder viewHolder =
                new PlacesRecyclerViewAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(PlacesRecyclerViewAdapter.ViewHolder holder, int position) {
        final Place place = placesList.get(position);
        holder.placeName.setText(place.getName());
        holder.address.setText(place.getAddress());


//        if(place.getRating() > -1){
//            holder.ratingBar.setNumStars((int)place.getRating());
//        }else{
//            holder.ratingBar.setVisibility(View.GONE);
//        }

//        holder.viewOnMap.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                showOnMap(place);
//            }
//        });

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView placeName;
        TextView category;
        TextView address;
        ImageView eateryImage;
        RatingBar ratingBar,priceRateBar;

        public ViewHolder(View itemView) {

            super(itemView);

            placeName = itemView.findViewById(R.id.placeName);
            category = itemView.findViewById(R.id.catagory);
            address = itemView.findViewById(R.id.address);
            eateryImage = itemView.findViewById(R.id.eateryImage);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            priceRateBar = itemView.findViewById(R.id.priceRateBar);
        }
    }

//    private void showOnMap(Place place){
//        FragmentManager fm = ((CurrentLocationNearByPlacesActivity)context)
//                .getSupportFragmentManager();
//
//        Bundle bundle=new Bundle();
//        bundle.putString("name", (String)place.getName());
//        bundle.putString("address", (String)place.getAddress());
//        bundle.putDouble("lat", place.getLatLng().latitude);
//        bundle.putDouble("lng", place.getLatLng().longitude);
//
//        PlaceOnMapFragment placeFragment = new PlaceOnMapFragment();
//        placeFragment.setArguments(bundle);
//
//        fm.beginTransaction().replace(R.id.map_frame, placeFragment).commit();
//    }
}
