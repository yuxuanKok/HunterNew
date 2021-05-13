package com.example.hunter.ui.search;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.hunter.R;


public class SearchFragment extends Fragment implements View.OnClickListener {


    private Button searchButton;
    private CardView board, top, chinese, malay, indian, western, japanese, korean, random;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_search, container, false);

        searchButton = root.findViewById(R.id.searchButton);
        board = root.findViewById(R.id.board);
        top = root.findViewById(R.id.top);
        chinese = root.findViewById(R.id.chinese);
        malay = root.findViewById(R.id.malay);
        indian = root.findViewById(R.id.indian);
        western = root.findViewById(R.id.western);
        japanese = root.findViewById(R.id.japanese);
        korean = root.findViewById(R.id.korean);
        random = root.findViewById(R.id.random);

        searchButton.setOnClickListener(this);
        board.setOnClickListener(this);
        top.setOnClickListener(this);
        chinese.setOnClickListener(this);
        malay.setOnClickListener(this);
        indian.setOnClickListener(this);
        western.setOnClickListener(this);
        japanese.setOnClickListener(this);
        korean.setOnClickListener(this);
        random.setOnClickListener(this);

        return root;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onClick(View v) {
        Fragment fragment = null;
        switch (v.getId()) {
            case R.id.searchButton:
                fragment = new SearchingFragment();
                break;

            case R.id.board:
                fragment = new BoardFragment();
                break;
//
            case R.id.top:
                fragment = new TopFragment();
                break;

            case R.id.chinese:
                fragment = new ChineseFragment();
                break;

            case R.id.malay:
                fragment = new MalayFragment();
                break;
//
//            case R.id.indian:
//                fragment = new IndianFragment();
//                break;
//
            case R.id.western:
                fragment = new WesternFragment();
                break;
//
//            case R.id.korean:
//                fragment = new KoreanFragment();
//                break;
//
            case R.id.japanese:
                fragment = new JapaneseFragment();
                break;
//
            case R.id.random:
                fragment = new RandomFragment();
                break;

        }
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}