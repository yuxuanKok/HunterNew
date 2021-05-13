package com.example.hunter.ui.search;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.hunter.Eateries2;
import com.example.hunter.R;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Random;

@RequiresApi(api = Build.VERSION_CODES.O)
public class RandomFragment extends Fragment implements LocationListener, View.OnClickListener {
    private ImageView randomImage;
    private TextView randomName;
    private RatingBar randomRatingBar;
    private TextView randomCategory;
    private TextView address;
    private Button repick, review, map;

    String TAG;
    int random ;
    private ArrayList<Eateries2> eateriesArrayList = new ArrayList<Eateries2>();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private LocationManager locationManager;
    Geocoder coder ;
    private List<Address> logLng;
    private ArrayList<Eateries2> nearBy = new ArrayList<>();
    double lat, mLat;
    double lng, mLng;
    String placeid;
    boolean open;
    Calendar calendar = Calendar.getInstance();
    int day = calendar.get(Calendar.DAY_OF_WEEK);
    int APM = calendar.get(Calendar.AM_PM);
    int hour = calendar.get(Calendar.HOUR);
    int min = calendar.get(Calendar.MINUTE);



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_random, container, false);

        address= view.findViewById(R.id.randomAddress);
        randomImage = view.findViewById(R.id.randomImageView);
        randomName = view.findViewById(R.id.randomName);
        randomRatingBar = view.findViewById(R.id.randomRatingBar);
        randomCategory = view.findViewById(R.id.randomCategory);
        repick = view.findViewById(R.id.repick);
        review = view.findViewById(R.id.show);
        map = view.findViewById(R.id.map);


        repick.setOnClickListener(this);
        review.setOnClickListener(this);
        map.setOnClickListener(this);

///////GET CURRENT LOCATION//////
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {


            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return null;
        }
        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        onLocationChanged(location);
////////END GET CURRENT LOCATION///////


//////GET ADDRESS FROM FIRESTORE//////
        db.collection("eateries")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()){
                            eateriesArrayList.add(doc.getDocument().toObject(Eateries2.class));
                        }


                        ////////COMPARE TWO ADDRESS LOG AND LNG/////////

                        coder = new Geocoder(getContext(), Locale.getDefault());
                        try {
                            for (int i = 0; i < eateriesArrayList.size(); i++) {
                                logLng = coder.getFromLocationName(eateriesArrayList.get(i).getAddress(), 5);//return an array of addresses that are known to describe the named location
                                Address mlocation = logLng.get(0);
                                mLat = mlocation.getLatitude();
                                mLng = mlocation.getLongitude();
                                if (distance(mLat,mLng, lat, lng) < 0.8) {
                                    //nearBy.add(eateriesArrayList.get(i));
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                                    List placefields = Arrays.asList(Place.Field.OPENING_HOURS, Place.Field.UTC_OFFSET, Place.Field.NAME,Place.Field.PHOTO_METADATAS);
                                    FetchPlaceRequest placeRequest = FetchPlaceRequest.newInstance(eateriesArrayList.get(i).getPlaceID(),placefields);
                                    String api = "AIzaSyDz5BGny6Lsp7gW-uJznoLVZS4riEdfnF0";
                                    Places.initialize(getContext(), api);
                                    PlacesClient placesClient = Places.createClient(getContext());

                                    int finalI = i;
                                    placesClient.fetchPlace(placeRequest).addOnSuccessListener((response) -> {
                                        Place place = response.getPlace();
                                        if(place!=null) {
                                            //open= place.isOpen();
                                            Log.d("place", "PLACE :" + place.getName());
                                            if (place.isOpen()) {
                                                Log.d("placeisopen: ", "isopen?" + place.isOpen().toString() + "i :" + finalI);
                                                nearBy.add(eateriesArrayList.get(finalI));

                                            } else {
                                                open = false;
                                                Log.d("placeNOTopen: ", "place NOT open" + place.getName() + place.isOpen().toString());
                                            }
                                            if (!nearBy.isEmpty()) {

                                                random = new Random().nextInt(nearBy.size());
                                                randomName.setText(nearBy.get(random).getPlaceName());
                                                randomRatingBar.setRating(Float.parseFloat(nearBy.get(random).getRating().toString()));
                                                randomRatingBar.setIsIndicator(true);
                                                randomCategory.setText(nearBy.get(random).getCategory().toString());
                                                address.setText(nearBy.get(random).getAddress());
                                                placeid = nearBy.get(random).getPlaceID();

                                                // FetchPlaceRequest placeRequest2 = FetchPlaceRequest.newInstance(nearBy.get(random).getPlaceID(), placefields);
                                                PhotoMetadata photoMetadata = place.getPhotoMetadatas().get(0);
                                                String attributions = photoMetadata.getAttributions();
                                                FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(photoMetadata)
                                                        .build();
                                                placesClient.fetchPhoto(photoRequest).addOnSuccessListener((fetchPhotoResponse) -> {
                                                    Bitmap bitmap = fetchPhotoResponse.getBitmap();
                                                    randomImage.setImageBitmap(bitmap);
                                                }).addOnFailureListener((exception) -> {
                                                    if (exception instanceof ApiException) {
                                                        ApiException apiException = (ApiException) exception;
                                                        int statusCode = apiException.getStatusCode();
                                                        // Handle error with given status code.
                                                        Log.e(TAG, "Place not found: " + exception.getMessage());
                                                    }
                                                });
                                            } else{
                                                Toast.makeText(getContext(),"no result",Toast.LENGTH_LONG).show();
//
                                            }
                                        }
                                    });

                                    //String placeidd = eateriesArrayList.get(i).getPlaceID();

//                                    if(open) {
//                                        Log.d("place(i)","Place i"+ i);
//                                        nearBy.add(eateriesArrayList.get(i));
//                                    }
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        finally {

                        }

//                        if(!nearBy.isEmpty()) {
//
//                            random = new Random().nextInt(nearBy.size());
//                            randomName.setText(nearBy.get(random).getPlaceName());
//                            randomRatingBar.setRating(Float.parseFloat(nearBy.get(random).getRating().toString()));
//                            randomRatingBar.setIsIndicator(true);
//                            randomCategory.setText(nearBy.get(random).getCategory().toString());
//                            address.setText(nearBy.get(random).getAddress());
//                            placeid=nearBy.get(random).getPlaceID();
//
//                            List<Place.Field> fields = Arrays.asList(Place.Field.PHOTO_METADATAS);
//                            FetchPlaceRequest placeRequest = FetchPlaceRequest.newInstance(nearBy.get(random).getPlaceID(), fields);
//                            String api = "AIzaSyDz5BGny6Lsp7gW-uJznoLVZS4riEdfnF0";
//                            Places.initialize(getContext(), api);
//                            PlacesClient placesClient = Places.createClient(getContext());
//
////                            placesClient.fetchPlace(placeRequest).addOnSuccessListener((response) -> {
//   //                             Place place = response.getPlace();
//                                PhotoMetadata photoMetadata = place.getPhotoMetadatas().get(0);
//                                String attributions = photoMetadata.getAttributions();
//
//                                FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(photoMetadata)
//                                        .build();
//                                placesClient.fetchPhoto(photoRequest).addOnSuccessListener((fetchPhotoResponse) -> {
//                                    Bitmap bitmap = fetchPhotoResponse.getBitmap();
//                                    randomImage.setImageBitmap(bitmap);
//                                }).addOnFailureListener((exception) -> {
//                                    if (exception instanceof ApiException) {
//                                        ApiException apiException = (ApiException) exception;
//                                        int statusCode = apiException.getStatusCode();
//                                        // Handle error with given status code.
//                                        Log.e(TAG, "Place not found: " + exception.getMessage());
//                                    }
//                                });
//                            });
////            }
//                        }
//                        else{
//                            Toast.makeText(getContext(),"no result",Toast.LENGTH_LONG).show();
////
//                        }



////////END COMPARE////////
                    }
                });

//////END GET ADDRESS//////


        return view;
    }


    ///////FUNCTION FOR COMPARE THE DISTANCE//////
    private double distance(double lat1, double lng1, double lat2, double lng2) {

        double earthRadius = 3958.75; // in miles, change to 6371 for kilometers

        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);

        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);

        double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        double dist = earthRadius * c;

        return dist;
    }
////////END FUNCTION COMPARE DISTANCE/////

    ///////FOR THE LocationListener///////
    @Override
    public void onLocationChanged(Location location) {
        lat = location.getLatitude();
        lng = location.getLongitude();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onClick(View v) {

        switch(v.getId()){
            case R.id.repick:
                random = new Random().nextInt(nearBy.size());
                randomName.setText(nearBy.get(random).getPlaceName());
                randomRatingBar.setRating(Float.parseFloat(nearBy.get(random).getRating().toString()));
                randomRatingBar.setIsIndicator(true);
                randomCategory.setText(nearBy.get(random).getCategory().toString());
                address.setText(nearBy.get(random).getAddress());
                placeid=nearBy.get(random).getPlaceID();


                List<Place.Field> fields = Arrays.asList(Place.Field.PHOTO_METADATAS);
                FetchPlaceRequest placeRequest = FetchPlaceRequest.newInstance(nearBy.get(random).getPlaceID(), fields);
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
                        randomImage.setImageBitmap(bitmap);
                    }).addOnFailureListener((exception) -> {
                        if (exception instanceof ApiException) {
                            ApiException apiException = (ApiException) exception;
                            int statusCode = apiException.getStatusCode();
                            // Handle error with given status code.
                            Log.e(TAG, "Place not found: " + exception.getMessage());
                        }
                    });
                });
                break;

            case R.id.map:
                break;

            case R.id.show:
                PlacesFragment placesFragment = PlacesFragment.newInstance(placeid);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,placesFragment).commit();
                break;
        }

    }
//////END LocationListener//////


}