package com.example.hunter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    TextView forgotPassword;
    TextView signup;
    Button login;
    EditText username, password;
    String name, pw;
    String TAG;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        forgotPassword=findViewById(R.id.forgotPassword);
        signup=findViewById(R.id.signup);
        login=findViewById(R.id.login);
        username=findViewById(R.id.username);
        password=findViewById(R.id.password);

        forgotPassword.setOnClickListener(this);
        signup.setOnClickListener(this);
        login.setOnClickListener(this);

        db=FirebaseFirestore.getInstance();

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.signup:
//                Intent intent= new Intent(MainActivity.this,SignUpActivity.class);
//                startActivity(intent);
                break;
            case R.id.login:
                Intent intent = new Intent(this, FeedActivity.class);
                startActivity(intent);
                User user = new User();
                name = username.getText().toString();
                if(password.getText().toString()!=""){
                    pw=password.getText().toString();
                }
                else {
                    pw=null;
                }
                user.setUsername(name);
                user.setPassword(pw);
                CollectionReference collectionReference=db.collection("users");
                Query query=collectionReference.whereEqualTo("username",user.getUsername() );
                db.collection("users")
                        .whereEqualTo("username",user.getUsername())
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
//                                        Intent intent2 = new Intent(getApplicationContext(),ProfileActivity.class);
//                                        startActivity(intent2);
                                        Log.d(TAG, document.getId() + " => " + document.getData());
                                    }
                                } else {
                                    Log.d(TAG, "Error getting documents: ", task.getException());
                                }
                            }
                        });

            default:
                break;
        }
    }
}

