package com.rnba.gaithoukaithian;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rnba.gaithoukaithian.model.Product;
import com.rnba.gaithoukaithian.model.User;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ProfileActivity extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference userRef = db.collection("Users");

    EditText fullName, phone, email, address;
    Button btnContinue, myOrderBtn, editBtn, logOutBtn;
    private TextView myName;
    private LinearLayout profileEditLay, profileLay;

    Boolean updating= true;
    String show = "null", myEmail="null";
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        final Intent intent = getIntent();
        show = intent.getExtras().getString("show");
        myEmail = intent.getExtras().getString("myEmail", "null");
        updating = intent.getExtras().getBoolean("updating", true);


        fullName = findViewById(R.id.fullName);
        phone = findViewById(R.id.phone);
        email = findViewById(R.id.email);
        address = findViewById(R.id.address);
        profileEditLay = findViewById(R.id.profileEditLay);
        profileLay = findViewById(R.id.profileLay);
        btnContinue = findViewById(R.id.btnContinue);
        myOrderBtn = findViewById(R.id.myOrderBtn);
        editBtn = findViewById(R.id.editBtn);
        logOutBtn = findViewById(R.id.logOutBtn);
        myName = findViewById(R.id.myName);

        email.setText(myEmail);


       onClicks();
    }

    private void onClicks() {

        //btn myOrder click
        myOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),MyorderActivity.class));
            }
        });

        myOrderBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                userRef.document(FirebaseAuth.getInstance().getCurrentUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        User u = documentSnapshot.toObject(User.class);
                        if (u.getAdmin()){
                                startActivity(new Intent(getApplicationContext(), AdminActivity.class));
                        }else {
                            if (u.getRole().equals("employee")){
                                startActivity(new Intent(getApplicationContext(), EmployeeActivity.class));
                            }
                            Toast.makeText(ProfileActivity.this, "Invalid activity!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                return false;
            }
        });
        
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               profileEditLay.setVisibility(View.VISIBLE);
               profileLay.setVisibility(View.GONE);
            }
        });
        //btn logout click
        logOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ProfileActivity.this, "Logout", Toast.LENGTH_SHORT).show();
                FirebaseAuth.getInstance().signOut();
                finish();
            }
        });

        //btn continue click
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fnameV, phoneV, emailV, addressV;
                fnameV = fullName.getText().toString();
                phoneV = phone.getText().toString();
                emailV = email.getText().toString();
                addressV = address.getText().toString();

                if (fnameV.length()<1 | phoneV.length()<1 | addressV.length()<1 ){
                    Toast.makeText(ProfileActivity.this, "Enter all your info", Toast.LENGTH_SHORT).show();
                    return;
                }
                Calendar calForTime = Calendar.getInstance();
                SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh:mm a");
                final String currentTime =currentTimeFormat.format(calForTime.getTime());
                Calendar calForDate = Calendar.getInstance();
                SimpleDateFormat currentDateFormat = new SimpleDateFormat("dd-mm-yyyy");
                String currentDate = currentDateFormat.format(calForDate.getTime());
                String timeNdateV = currentTime+currentDate;

                User user = new User(FirebaseAuth.getInstance().getCurrentUser().getUid(), fnameV,phoneV, emailV, addressV, timeNdateV,"customer", false, System.currentTimeMillis());
                progressDialog = new ProgressDialog(ProfileActivity.this);
                progressDialog.setMessage("Progress..");
                progressDialog.show();
                if (updating){
                    Toast.makeText(ProfileActivity.this, "Updating", Toast.LENGTH_SHORT).show();
                    userRef.document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .update("fullName", fnameV, "phone", phoneV, "email", emailV, "address", addressV ).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(ProfileActivity.this, "Updated!", Toast.LENGTH_SHORT).show();
                            finish();
                            progressDialog.dismiss();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(ProfileActivity.this, "error: "+e.getMessage().toLowerCase(), Toast.LENGTH_LONG).show();
                        }
                    });
                }else {
                    userRef.document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(ProfileActivity.this, "Info updated!", Toast.LENGTH_SHORT).show();
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.dismiss();
                                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    intent.putExtra("from", "profile");
                                    startActivity(intent);
                                }
                            },500);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(ProfileActivity.this, "error: "+e.getMessage().toLowerCase(), Toast.LENGTH_LONG).show();
                        }
                    });

                }

            }
        });

    }

    private void checkContitions() {

        //checkuser login
        if (FirebaseAuth.getInstance().getCurrentUser()!=null){
            PerformTaskWithUID();
            myOrderBtn.setVisibility(View.VISIBLE);
            logOutBtn.setVisibility(View.VISIBLE);
            editBtn.setVisibility(View.VISIBLE);
        }else {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            Toast.makeText(this, "Required login first.", Toast.LENGTH_SHORT).show();
        }
        if (updating){
            btnContinue.setText("Update");
        }else {
            btnContinue.setText("Continue");
            email.setEnabled(true);
        }

        if (show.equals("userProfile")){
            profileLay.setVisibility(View.VISIBLE);
        }else if(show.equals("editLay")) {
            profileLay.setVisibility(View.GONE);
            profileEditLay.setVisibility(View.VISIBLE);
        }

    }

    private void PerformTaskWithUID() {
        userRef.document(FirebaseAuth.getInstance().getCurrentUser().getUid()).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()){
                            User user = documentSnapshot.toObject(User.class);
                            fullName.setText(user.getFullName());
                            phone.setText(user.getPhone());
                            email.setText(user.getEmail());
                            address.setText(user.getAddress());
                            myName.setText(user.getFullName());

                        }
                        btnContinue.setEnabled(true);
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (FirebaseAuth.getInstance().getCurrentUser() ==null){
            finish();
        }


    }

    @Override
    protected void onStart() {
        super.onStart();
        checkContitions();
//        profileLay.setVisibility(View.VISIBLE);
//        profileEditLay.setVisibility(View.GONE);
    }
}


