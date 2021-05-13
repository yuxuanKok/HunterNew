package com.example.hunter;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zhihu.matisse.filter.Filter;


import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.Future;

import static android.R.style.Theme_Holo_Light_Dialog_MinWidth;

public class ProfileSettings extends AppCompatActivity implements View.OnClickListener {
    ImageView set_profile_pic;
    Button change_profpic, save_prof_set, confirm_username;
    EditText set_username_input, set_name_input, set_pswd_input, saved_dob_view;
    Spinner set_gender_spinner, set_backColor_spinner;
    TextView check_username;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    String userId = mAuth.getCurrentUser().getUid();
    ;
    private static final String TAG = "...";

    Calendar newCalendar = Calendar.getInstance();
    Calendar newDate = Calendar.getInstance();
    private DatePickerDialog birthDatePickerDialog;
    private SimpleDateFormat dateFormatter;

    String saved_displayName;
    String username;
    String saved_pswd;
    String saved_gender;
    String saved_bgColour;
    String saved_dob;
    String saved_profpic;

    private final int REQUEST_CODE_CHOOSE = 0;
    private static final int RC_PERMISSION_READ_EXTERNAL_STORAGE = 1;

    private String checkpswd = "";
    private String newpswdd = "";
    private String confirm_newpswdd = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_settings);

        //set references
        set_profile_pic = findViewById(R.id.set_profile_pic);
        change_profpic = findViewById(R.id.change_profpic);
        set_username_input = findViewById(R.id.set_username_input);
        confirm_username = findViewById(R.id.confirm_username);
        set_name_input = findViewById(R.id.set_name_input);
        set_pswd_input = findViewById(R.id.set_pswd_input);
        saved_dob_view = findViewById(R.id.saved_dob_view);
        set_gender_spinner = findViewById(R.id.set_gender_spinner);
        set_backColor_spinner = findViewById(R.id.set_backColor_spinner);
        save_prof_set = findViewById(R.id.save_prof_set);
        check_username = findViewById(R.id.check_username);

        //to set format for dob
        dateFormatter = new SimpleDateFormat("dd/MM", Locale.US);
        setDateTimeField();

        //GET data from firestore database
        DocumentReference docRef = db.collection("users").document(userId);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    //retrieve data from firestore as String
                    saved_displayName = document.getString("displayName");
                    username = document.getString("username");
                    saved_pswd = document.getString("password");
                    saved_gender = document.getString("gender");
                    saved_bgColour = document.getString("backgroundColour");
                    saved_dob = document.getString("dob");
                    saved_profpic = document.getString("photo");

                    //set saved_data as text of EditText
                    set_name_input.setText(saved_displayName);
                    set_username_input.setText(username);
                    set_pswd_input.setText(saved_pswd);
                    set_gender_spinner.setSelection(((ArrayAdapter) set_gender_spinner.getAdapter()).getPosition(saved_gender));
                    set_backColor_spinner.setSelection(((ArrayAdapter) set_backColor_spinner.getAdapter()).getPosition(saved_bgColour));
                    saved_dob_view.setText(saved_dob);
                    set_profile_pic.setImageURI(null);
                    //load profile picture into ImageView
                    if (saved_profpic != null) {
                        Uri uri = Uri.parse(saved_profpic);
                        Glide.with(ProfileSettings.this).load(uri).into(set_profile_pic);
                        //set_profile_pic.setImageResource(Integer.parseInt(saved_profpic));
                    }
                    Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                } else {
                    Log.d(TAG, "No such document");
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
            }
        });//end GET function


        //////////////////////////////////Check username existed or not//////////////////////////////////////////////////////////////
        long delay = 2000; // 1 seconds after user stops typing
        final long[] last_text_edit = {0};
        Handler handler = new Handler();

        Runnable input_finish_checker = () -> {
            if (System.currentTimeMillis() > (last_text_edit[0] + delay - 500)) {
                String set_username = set_username_input.getText().toString();
                DocumentReference docIdRef = db.collection("usernames").document(set_username);
                docIdRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Log.d(TAG, "Document exists!");
                                if (!set_username.equals(username)) {
                                    check_username.setText("Username Exist!");
                                    set_username_input.setText("");
                                }

                            } else {
                                Log.d(TAG, "Document does not exist!");
                                set_username_input.setText(set_username);
                                check_username.setText("");
                                db.collection("usernames").document(username)
                                        .delete()
                                        .addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot successfully deleted!"))
                                        .addOnFailureListener(e -> Log.w(TAG, "Error deleting document", e));
                            }
                        } else {
                            Log.d(TAG, "Failed with: ", task.getException());
                        }
                    }
                });
            }
        };

        set_username_input.addTextChangedListener(new TextWatcher() {
                                                      @Override
                                                      public void beforeTextChanged(CharSequence s, int start, int count,
                                                                                    int after) {
                                                      }

                                                      @Override
                                                      public void onTextChanged(final CharSequence s, int start, int before,
                                                                                int count) {
                                                          //You need to remove this to run only once
                                                          handler.removeCallbacks(input_finish_checker);
                                                      }

                                                      @Override
                                                      public void afterTextChanged(final Editable s) {
                                                          //avoid triggering event when text is empty
                                                          if (s.length() > 0) {
                                                              last_text_edit[0] = System.currentTimeMillis();
                                                              handler.postDelayed(input_finish_checker, delay);
                                                          }
                                                      }
                                                  }
        );

    } //end of onCreate

    ////////////////////////////////////////////////DOB SETTINGS/////////////////////////////////////////////////
    private void setDateTimeField() {
        saved_dob_view.setOnClickListener(this);
        birthDatePickerDialog = new DatePickerDialog(this, (view, year, monthOfYear, dayOfMonth) -> {
            newDate.set(year, monthOfYear, dayOfMonth);
            updateLabel();
        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
    }

    //update textView with user's birthday
    String updateLabel() {
        String dob = dateFormatter.format(newDate.getTime());
        saved_dob_view.setText(dob);
        return dob;
    }//end of updateLabel()

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////CHOOSE IMAGE//////////////////////////////////////////////////////
    List<Uri> mSelected;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("Matisse", "problem: " + mSelected);

        if (requestCode == REQUEST_CODE_CHOOSE && resultCode == RESULT_OK) {
            mSelected = Matisse.obtainResult(data);
            Log.d("Matisse", "mSelected: " + mSelected);
            if (mSelected != null) {
                Uri uri = mSelected.get(0);
                //String new_profpic = mSelected.toString();


                //update user data
                userId = mAuth.getCurrentUser().getUid();

                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReference();
                StorageReference imagesRef = storageRef.child(username + uri.getLastPathSegment());


                imagesRef.putFile(uri).addOnSuccessListener(taskSnapshot -> imagesRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri1) {
                        Log.d(TAG, "onSuccess: uri= " + uri1.toString());

                        //SET AND MERGE FUNCTION
                        Map<String, Object> undata = new HashMap<>();
                        undata.put("photo", uri1.toString());

                        db.collection("users").document(userId)
                                .set(undata, SetOptions.merge())
                                .addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot successfully written!"))
                                .addOnFailureListener(e -> Log.w(TAG, "Error writing document", e));
                        refresh();
                    }
                })).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
            }
        }
    }

/////////////////////////////////////////////////////////////////////////////////////////////

    /////////////////////////////////SET PASSWORD /////////////////////////////////////////////////
    public void checkOldPswd1() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.oldpswd_customdialog, null);
        dialogBuilder.setView(dialogView);

        final EditText old_pswd = dialogView.findViewById(R.id.old_pswd);

        dialogBuilder.setTitle("Please Retype Old Password");
        dialogBuilder.setNeutralButton("Done", (dialog, whichButton) -> {
            ////////////////get saved pswd//////////////////////
            checkpswd = old_pswd.getText().toString();
            if (!checkpswd.equals(saved_pswd)) {
                checkOldPswd2();
            } else {
                setNewPswd1();
            }
        });
        dialogBuilder.setNegativeButton("Cancel", (dialog, whichButton) -> dialog.cancel());
        AlertDialog b = dialogBuilder.create();
        b.show();
    }

    public void checkOldPswd2() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.oldpswd_customdialog, null);
        dialogBuilder.setView(dialogView);

        final EditText old_pswd = dialogView.findViewById(R.id.old_pswd);

        dialogBuilder.setTitle("Old Password Incorrect");
        dialogBuilder.setNeutralButton("Done", (dialog, whichButton) -> {
            ////////////////get saved pswd//////////////////////
            checkpswd = old_pswd.getText().toString();
            if (!checkpswd.equals(saved_pswd)) {
                checkOldPswd2();
            } else {
                setNewPswd1();
            }
        });
        dialogBuilder.setNegativeButton("Cancel", (dialog, whichButton) -> dialog.cancel());
        AlertDialog b = dialogBuilder.create();
        b.show();
    }

    public void setNewPswd1() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.newpswd_customdialog, null);
        dialogBuilder.setView(dialogView);

        final EditText newpswd = dialogView.findViewById(R.id.newpswd);
        final EditText confirm_newpswd = dialogView.findViewById(R.id.confirm_newpswd);

        dialogBuilder.setTitle("Please Type New Password");
        dialogBuilder.setNeutralButton("Done", (dialog, whichButton) -> {

            newpswdd = newpswd.getText().toString();
            confirm_newpswdd = confirm_newpswd.getText().toString();

            //Check whether both password match
            //IF match, save new password to firestore
            if (newpswdd.equals(confirm_newpswdd)) {
                userId = mAuth.getCurrentUser().getUid();
                DocumentReference userDoc = db.collection("users").document(userId);

                //SET AND MERGE FUNCTION
                Map<String, Object> udata = new HashMap<>();
                udata.put("password", newpswdd);
                db.collection("users").document(userId)
                        .set(udata, SetOptions.merge())
                        .addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot successfully written!"))
                        .addOnFailureListener(e -> Log.w(TAG, "Error writing document", e));
                dialog.dismiss();
                refresh();
            } else { //IF not, call the dialog box to let user key in again.
                setNewPswd2();
            }
        });
        dialogBuilder.setNegativeButton("Cancel", (dialog, whichButton) -> dialog.cancel());
        AlertDialog b = dialogBuilder.create();
        b.show();
    }

    public void setNewPswd2() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.newpswd_customdialog, null);
        dialogBuilder.setView(dialogView);

        final EditText newpswd = dialogView.findViewById(R.id.newpswd);
        final EditText confirm_newpswd = dialogView.findViewById(R.id.confirm_newpswd);

        dialogBuilder.setTitle("Password Not Match");
        dialogBuilder.setNeutralButton("Done", (dialog, whichButton) -> {

            newpswdd = newpswd.getText().toString();
            confirm_newpswdd = confirm_newpswd.getText().toString();

            //Check whether both password match
            //IF match, save new password to firestore
            if (newpswdd.equals(confirm_newpswdd)) {
                userId = mAuth.getCurrentUser().getUid();
                DocumentReference userDoc = db.collection("users").document(userId);

                //SET AND MERGE FUNCTION
                Map<String, Object> udata = new HashMap<>();
                udata.put("password", newpswdd);
                db.collection("users").document(userId)
                        .set(udata, SetOptions.merge())
                        .addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot successfully written!"))
                        .addOnFailureListener(e -> Log.w(TAG, "Error writing document", e));
                dialog.dismiss();
                refresh();
            } else { //IF not, call the dialog box to let user key in again.
                setNewPswd2();
            }
        });
        dialogBuilder.setNegativeButton("Cancel", (dialog, whichButton) -> dialog.cancel());
        AlertDialog b = dialogBuilder.create();
        b.show();
    }
////////////////////////////////////END PSWD SETTINGS///////////////////////////////////////////////

    //////////////////////////////////PAGE REFRESH////////////////////////////
    public void refresh() {
        Intent intent = getIntent();
        overridePendingTransition(0, 0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
        overridePendingTransition(0, 0);
        startActivity(intent);
    }

    /////////////////////////////////////////////////////////////////////////////
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.change_profpic:
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "perm", Toast.LENGTH_LONG).show();

                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, RC_PERMISSION_READ_EXTERNAL_STORAGE);
                } else {
                    Matisse.from(ProfileSettings.this)
                            .choose(MimeType.ofAll())
                            .countable(true)
                            .maxSelectable(1)
                            .theme(R.style.Matisse_Dracula)
                            // .addFilter(new GifSizeFilter(320, 320, 5 * Filter.K * Filter.K))
                            .gridExpectedSize(getResources().getDimensionPixelSize(R.dimen.grid_expected_size))
                            .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                            .thumbnailScale(0.85f)
                            .imageEngine(new GlideEngine())
                            .showPreview(false) // Default is `true`
                            .forResult(REQUEST_CODE_CHOOSE);
                }
                break;

            case R.id.set_pswd_input:
                if (!saved_pswd.matches("")) {
//                   pswd = set_pswd_input.getText().toString();
//                    DocumentReference usernameDoc =db.collection("users").document(userId);
//
//                    //SET AND MERGE FUNCTION
//                    Map<String, Object> udata = new HashMap<>();
//                    udata.put("password", pswd);
//
//                    db.collection("users").document(userId)
//                            .set(udata, SetOptions.merge())
//                            .addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot successfully written!"))
//                            .addOnFailureListener(e -> Log.w(TAG, "Error writing document", e));

                    //dont allow user to edit
                    //*STILL CAN EDIT ***////////
                    set_pswd_input.setFocusable(false);
                    set_pswd_input.setClickable(true);
                    checkOldPswd1();
                }
                break;

            case R.id.saved_dob_view:
                //to hide year field in DatePickerDialog
                birthDatePickerDialog.getDatePicker().findViewById(Resources.getSystem().getIdentifier("year", "id", "android")).setVisibility(View.GONE);
                birthDatePickerDialog.show();
                break;

            case R.id.save_prof_set:
                if (saved_dob_view.getText().toString().matches("")) {
                    Toast.makeText(this, "Please choose your birthday",
                            Toast.LENGTH_LONG).show();
                } else {
                    //get input from users

                    String set_name = set_name_input.getText().toString();
                    String gender = set_gender_spinner.getSelectedItem().toString();
                    String backgroundColour = set_backColor_spinner.getSelectedItem().toString();
                    String pswd = set_pswd_input.getText().toString();
                    String set_username = set_username_input.getText().toString();

                    //update user data
                    DocumentReference userDoc = db.collection("users").document(userId);

                    //SET AND MERGE FUNCTION
                    Map<String, Object> udata = new HashMap<>();
                    udata.put("displayName", set_name);
                    udata.put("gender", gender);
                    udata.put("backgroundColour", backgroundColour);
                    udata.put("dob", updateLabel());
                    udata.put("password", pswd);
                    udata.put("username", set_username);

                    db.collection("users").document(userId)
                            .set(udata, SetOptions.merge())
                            .addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot successfully written!"))
                            .addOnFailureListener(e -> Log.w(TAG, "Error writing document", e));

                    //////////////////////////////////Save to usernames collection//////////////////////////////////////////////
                    //SET AND MERGE FUNCTION
                    Map<String, Object> undata = new HashMap<>();
                    undata.put("password", pswd);
                    undata.put("uid", userId);

                    db.collection("usernames").document(set_username)
                            .set(undata, SetOptions.merge())
                            .addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot successfully written!"))
                            .addOnFailureListener(e -> Log.w(TAG, "Error writing document", e));

                    //go back profile page
                    onBackPressed();
                    break;
                }
        }
    }
}