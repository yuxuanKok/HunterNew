package com.example.hunter.ui.search;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SearchView;
import android.widget.Spinner;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hunter.Eateries2;
import com.example.hunter.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class JapaneseFragment extends Fragment {
    private SearchView japaneseSearchView;
    private RecyclerView japaneseRecyclerView;
    private Spinner japaneseSpinner;
    private ArrayList<Eateries2> japaneseArray = new ArrayList<Eateries2>();
    private SearchRecyclerViewAdapter adapter;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private ArrayAdapter<CharSequence> spinnerAdapter ;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_japanese, container, false);

        japaneseSearchView = view.findViewById(R.id.japaneseSearchView);
        japaneseRecyclerView= view.findViewById(R.id.japaneseRecyclerView);
        japaneseSpinner = view.findViewById(R.id.japaneseSpinner);
        japaneseRecyclerView.setHasFixedSize(true);
        japaneseRecyclerView.setLayoutManager(new LinearLayoutManager(getContext().getApplicationContext()));
        adapter = new SearchRecyclerViewAdapter(japaneseArray);

        spinnerAdapter = ArrayAdapter.createFromResource(getContext(), R.array.searchSpinner,android.R.layout.simple_dropdown_item_1line);
        spinnerAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        japaneseSpinner.setAdapter(spinnerAdapter);


        db.collection("eateries")
                .whereArrayContains("category","Japanese")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(DocumentChange document :queryDocumentSnapshots.getDocumentChanges()){
                            String placeId = document.getDocument().getString("placeID");
                            String placeName = document.getDocument().getString("placeName");
                            List<String> category = (List<String>)document.getDocument().get("category");
                            String address = document.getDocument().getString("address");
                            Double rating = document.getDocument().getDouble("rating");
                            Double price_level = document.getDocument().getDouble("price_level");

                            Eateries2 eateries = new Eateries2(address, category, placeId, placeName, rating,price_level);
                            japaneseArray.add(eateries);
                            adapter.notifyDataSetChanged();
                        }
                    }
                });


        japaneseRecyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new SearchRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(int position) {
                String chosenEatery=japaneseArray.get(position).getPlaceID();
                PlacesFragment placesFragment = PlacesFragment.newInstance(chosenEatery);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,placesFragment).commit();
            }
        });



        japaneseSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return false;
            }
        });

        japaneseSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        //Do nothing
                        break;

                    case 1:
                        sortPrice();
                        break;

                    case 2:
                        reversePrice();
                        break;

                    case 3:
                        sortArrayList();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return view;
    }

    private  void  sortArrayList(){
        Collections.sort(japaneseArray, new Comparator<Eateries2>() {
            @Override
            public int compare(Eateries2 o1, Eateries2 o2) {
                return Double.compare(o2.getRating(),o1.getRating());
            }
        });
        adapter.notifyDataSetChanged();
    }

    private  void sortPrice(){
        Collections.sort(japaneseArray, new Comparator<Eateries2>() {
            @Override
            public int compare(Eateries2 o1, Eateries2 o2) {
                return o1.getPrice_level().compareTo(o2.getPrice_level());
            }
        });
        adapter.notifyDataSetChanged();
    }

    private  void reversePrice(){
        Collections.sort(japaneseArray, new Comparator<Eateries2>() {
            @Override
            public int compare(Eateries2 o1, Eateries2 o2) {
                return o2.getPrice_level().compareTo(o1.getPrice_level());
            }
        });
        adapter.notifyDataSetChanged();
    }

    private void filter(String text){
        ArrayList<Eateries2> filteredList = new ArrayList<>();
        for(Eateries2 eatery: japaneseArray){
            if(eatery.getPlaceName().toLowerCase().contains(text.toLowerCase())){
                filteredList.add(eatery);
            }
        }
        adapter.filterList(filteredList);
    }



}