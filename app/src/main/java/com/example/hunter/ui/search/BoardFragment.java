package com.example.hunter.ui.search;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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


public class BoardFragment extends Fragment {
    private RecyclerView boardRecyclerView;
    private ArrayList<Eateries2> boardArray = new ArrayList<Eateries2>();
    private BoardRecyclerViewAdapter adapter;
    FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_board, container, false);

        boardRecyclerView= view.findViewById(R.id.boardRecyclerView);
        boardRecyclerView.setHasFixedSize(true);
        boardRecyclerView.setLayoutManager(new LinearLayoutManager(getContext().getApplicationContext()));
        adapter = new BoardRecyclerViewAdapter(boardArray);



        db.collection("eateries")
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
                            boardArray.add(eateries);
                            sortArrayList();
                            adapter.notifyDataSetChanged();
                        }
                    }
                });


        boardRecyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new BoardRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(int position) {
                String chosenEatery=boardArray.get(position).getPlaceID();
                PlacesFragment placesFragment = PlacesFragment.newInstance(chosenEatery);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,placesFragment).commit();
            }
        });

        return view;
    }

    private  void  sortArrayList(){
        Collections.sort(boardArray, new Comparator<Eateries2>() {
            @Override
            public int compare(Eateries2 o1, Eateries2 o2) {
                return Double.compare(o2.getRating(),o1.getRating());
            }
        });
        //adapter.notifyDataSetChanged();
    }

}
