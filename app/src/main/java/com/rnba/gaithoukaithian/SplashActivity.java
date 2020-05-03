package com.rnba.gaithoukaithian;

//import android.support.v7.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class SplashActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

//        Intent intent = new Intent();
//        intent.putExtra("from", "splash");
//        startActivity(intent);

        Intent intnt = new Intent(this,MainActivity.class);
        intnt.putExtra("from","splash");
        startActivity(intnt);
    }
}
