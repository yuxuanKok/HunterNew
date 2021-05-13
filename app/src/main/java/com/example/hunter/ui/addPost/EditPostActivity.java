package com.example.hunter.ui.addPost;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.hunter.Category;
import com.example.hunter.ImageAdapter;
import com.example.hunter.MultiSelectionSpinner;
import com.example.hunter.Posts;
import com.example.hunter.R;
import com.example.hunter.ui.home.HomeFragment;
import com.example.hunter.ui.profile.ProfileFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditPostActivity extends FragmentActivity implements View.OnClickListener {
    private String postid, placeID;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private Button done, back;
    private Double priceRate,rate,totalRate,totalPrice,postNum;
    private ViewPager viewPager;
    private CircleImageView img;
    private TextView username, placeName;
    private EditText exp;
    private RatingBar price, ratingBar;
    private MultiSelectionSpinner spinner;
    private ArrayList<Category> category = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_post);
        postid = getIntent().getStringExtra("postid");
        placeName = findViewById(R.id.placeEdit);
        done = findViewById(R.id.doneOptions);
        back = findViewById(R.id.backOptions);
        img = findViewById(R.id.optionsPic);
        username = findViewById(R.id.optionsUsername);
        exp = findViewById(R.id.expEdit);
        price = findViewById(R.id.priceBarEdit);
        ratingBar = findViewById(R.id.ratingBarEdit);
        viewPager = findViewById(R.id.viewpagerOpt);
        spinner = findViewById(R.id.spinnerEdit);

        String[] catName = getResources().getStringArray(R.array.category);

        for (int i = 0; i < catName.length; i++) {
            category.add(new Category(catName[i], false,i));
        }

        spinner.setItems(category);

        db.collection("users").document(mAuth.getUid()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot documentSnapshot = task.getResult();
                String imgId = documentSnapshot.getString("photo");
                String name = documentSnapshot.getString("username");
                Glide.with(getApplicationContext()).load(imgId).into(img);
                username.setText(name);
            }
        });

        db.collection("posts").document(postid).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot documentSnapshot = task.getResult();
                String capt = documentSnapshot.getString("caption");
                priceRate = documentSnapshot.getDouble("priceLvl");
                rate = documentSnapshot.getDouble("rating");
                placeID = documentSnapshot.getString("place_id");
                db.collection("eateries").document(placeID).get().addOnCompleteListener(task1 -> {
                    DocumentSnapshot documentSnapshot1 = task1.getResult();
                    String place = documentSnapshot1.getString("placeName");
                    totalPrice=documentSnapshot1.getDouble("totalPrice");
                    totalRate=documentSnapshot1.getDouble("totalRate");
                    postNum=documentSnapshot1.getDouble("postNum");
                    placeName.setText(place);
                });
                List<String> photoID = (List<String>) documentSnapshot.get("photo_id");
                List<Uri> photoUri = new ArrayList<>();
                for (int i = 0; i < photoID.size(); i++) {
                    photoUri.add(Uri.parse(photoID.get(i)));
                }
                ImageAdapter imageAdapter = new ImageAdapter(getApplicationContext(), photoUri);
                viewPager.setAdapter(imageAdapter);
                price.setRating(Float.parseFloat(priceRate.toString()));
                ratingBar.setRating(Float.parseFloat(rate.toString()));
                exp.setText(capt);
            }
        });

        done.setOnClickListener(this::onClick);
        back.setOnClickListener(this::onClick);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.doneOptions:
                String capt = exp.getText().toString();
                Double rating = Double.parseDouble(String.valueOf(ratingBar.getRating()));
                Double priceBar = Double.parseDouble(String.valueOf(price.getRating()));
                ArrayList<Category> selectedItem = spinner.getSelectedItems();
                List<String> cat = new ArrayList<>();
                for (int i = 0; i < selectedItem.size(); i++) {
                    cat.add(selectedItem.get(i).getName());
                }
                db.collection("posts").document(postid).update("caption", capt, "rating", rating, "priceLvl", priceBar);
                db.collection("eateries").document(placeID).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot documentSnapshot = task.getResult();

                        List<String> group = (List<String>) documentSnapshot.get("category");
                        for (int i = 0; i < cat.size(); i++) {
                            for (int j = 0; j < group.size(); j++) {
                                if (!cat.get(i).equals(group.get(j))) {
                                    db.collection("eateries").document(placeID).update("category", FieldValue.arrayUnion(cat.get(i)));
                                }
                            }
                        }
                        Double newtotalrate=totalRate+(rating-rate);
                        Double newtotalprice=totalPrice+(priceBar-priceRate);
                        Double newRate=newtotalrate/postNum;
                        Double newPrice=newtotalprice/postNum;
                        db.collection("eateries").document(placeID).update("price_level",newPrice,"rating",newRate,
                        "totalPrice",newtotalprice,"totalRate",newtotalrate);
                        Toast.makeText(this, "Post Updated", Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
                break;
            case R.id.backOptions:
                finish();
                break;
        }
    }
}
