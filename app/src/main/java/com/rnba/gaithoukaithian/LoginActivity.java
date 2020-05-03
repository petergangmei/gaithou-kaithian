package com.rnba.gaithoukaithian;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "ksd";
    private EditText email_field, password_field, password_field2;
    private String email_v, password_v, password_v2;
    private Button continueBtn;
    private TextView toggleBtn;
    DatabaseReference ref;
    FirebaseAuth mauth;
    ProgressDialog progressDialog;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference userRef = db.collection("Users");

    String activityStatus = "Login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mauth = FirebaseAuth.getInstance();

        email_field = findViewById(R.id.email);
        password_field = findViewById(R.id.password);
        password_field2 = findViewById(R.id.password2);
        continueBtn = findViewById(R.id.BtnContinue);

        toggleBtn = findViewById(R.id.toggleBtn);

         email_v = email_field.getText().toString();
         password_v = password_field.getText().toString();
        password_v2 = password_field2.getText().toString();

        continueBtn.setTag(activityStatus);
        continueBtn.setText(activityStatus);
        if (activityStatus.equals("Login")){
            password_field2.setVisibility(View.GONE);
        }


        //toggle
        toggleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (toggleBtn.getText().toString()){
                    case "Register":
                        password_field2.setVisibility(View.VISIBLE);
                        continueBtn.setText("Register");
                        toggleBtn.setText("Login");
                        return;

                    case "Login":
                        password_field2.setVisibility(View.GONE);
                        continueBtn.setText("Login");
                        toggleBtn.setText("Register");
                        return;

                }

            }
        });

        //textview
        textviewChange();

        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (continueBtn.getText().toString().equals("Register")){
                    if (email_field.getText().toString().isEmpty()){
                        Toast.makeText(LoginActivity.this, "Please enter your email"+email_v, Toast.LENGTH_SHORT).show();
                    }else if (password_field.getText().toString().isEmpty()){
                        Toast.makeText(LoginActivity.this, "Please enter your password", Toast.LENGTH_SHORT).show();
                    }else if (password_field2.getText().toString().isEmpty()){
                        Toast.makeText(LoginActivity.this, "Please enter your password", Toast.LENGTH_SHORT).show();
                    }else if (password_field2.getText().toString().length()>7){
                        if (password_field.getText().toString().equals(password_field2.getText().toString())){
                            continueBtn.setEnabled(false);
                            progressDialog = new ProgressDialog(LoginActivity.this);
                            progressDialog.setCancelable(false);
                            progressDialog.setMessage("Registering..");
                            progressDialog.show();

                            mauth.createUserWithEmailAndPassword(email_field.getText().toString(), password_field2.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    Intent intnt = new Intent(LoginActivity.this, ProfileActivity.class);
                                    intnt.putExtra("updating",false);
                                    intnt.putExtra("show", "editLay");
                                    intnt.putExtra("myEmail", email_field.getText().toString());
                                    startActivity(intnt);
                                    progressDialog.dismiss();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.dismiss();
                                    Toast.makeText(LoginActivity.this, "Error: "+e.getMessage().toLowerCase(), Toast.LENGTH_LONG).show();
                                }
                            });
                        }else {
                            Toast.makeText(LoginActivity.this, "Password not match.", Toast.LENGTH_SHORT).show();
                            password_field.setText("");
                            password_field2.setText("");
                            password_field.requestFocus();
                        }

                    }else {
                        Toast.makeText(LoginActivity.this, "Password required at lest 8 charactars!", Toast.LENGTH_SHORT).show();
                    }
                }else if (continueBtn.getText().toString().equals("Login")){

                    if (email_field.getText().toString().isEmpty()){
                        return;
                    }
                    if (password_field.getText().toString().isEmpty()){
                        return;
                    }
                    progressDialog = new ProgressDialog(LoginActivity.this);
                    progressDialog.setCancelable(false);
                    progressDialog.setMessage("Registering..");
                    progressDialog.show();
                    mauth.signInWithEmailAndPassword(email_field.getText().toString(), password_field.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            Toast.makeText(LoginActivity.this, "Login success!", Toast.LENGTH_SHORT).show();
                        //checkif user data avaialble
                            userRef.document(FirebaseAuth.getInstance().getCurrentUser().getUid()).get()
                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            progressDialog.dismiss();
                                         if (documentSnapshot.exists()){
                                             progressDialog.dismiss();
                                             finish();
                                         }else {
                                             progressDialog.dismiss();
                                             Intent intnt = new Intent(LoginActivity.this, ProfileActivity.class);
                                             intnt.putExtra("updating",false);
                                             startActivity(intnt);
                                         }
                                        }
                                    });

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(LoginActivity.this, "Login error:"+e.getMessage().toLowerCase(), Toast.LENGTH_LONG).show();
                        }
                    });
                }

            }
        });
    }

    private void textviewChange() {
        email_field.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                enableTaxet(s);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        password_field.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                enableTaxet(s);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        password_field2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                enableTaxet(s);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void enableTaxet(CharSequence s) {
        if(s.length()>1){
            continueBtn.setEnabled(true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onRestart() {
        super.onRestart();

    }
}
