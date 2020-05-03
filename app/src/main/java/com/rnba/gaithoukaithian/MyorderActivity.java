package com.rnba.gaithoukaithian;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.nfc.Tag;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.rnba.gaithoukaithian.adapter.MyorderAdapter;
import com.rnba.gaithoukaithian.model.Order;

import java.util.ArrayList;
import java.util.List;

public class MyorderActivity extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference orderRef = db.collection("Orders");

    private MyorderAdapter myorderAdapter;
    private List<Order> orderList, lastOrderList;
    private RecyclerView recyclerView,recyclerView2;
    private TextView orderQuantity,orderQuantity2;
    SwipeRefreshLayout refresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myorder);

        orderQuantity = findViewById(R.id.orderQuantity);
        orderQuantity2 = findViewById(R.id.orderQuantity2);
        refresh = findViewById(R.id.refresh);

        loadOrderDetails();

        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadOrderDetails();
                refreshContent();
            }
        });

    }


    private void refreshContent(){
        new Handler().postDelayed(new Runnable() {
            @Override public void run() {
                refresh.setRefreshing(false);
            }
        }, 2000);

    }

    public void loadOrderDetails() {
        recyclerView = findViewById(R.id.recyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        orderList = new ArrayList<>();
        orderRef.orderBy("timestamp", Query.Direction.DESCENDING).whereEqualTo("orderBy", FirebaseAuth.getInstance().getCurrentUser().getUid()).whereEqualTo("status", "In Progress").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()){
                            orderList.clear();
                            for (DocumentSnapshot snapshot:queryDocumentSnapshots){
                                orderQuantity.setText("Recent Order: "+queryDocumentSnapshots.size());
                                Order order = snapshot.toObject(Order.class);
                                orderList.add(order);
                            }
                            myorderAdapter = new MyorderAdapter(getApplicationContext(), orderList);
                            recyclerView.setAdapter(myorderAdapter);
                        }
                    }
                });


        //last order
        recyclerView2 = findViewById(R.id.recyclerview2);
        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(this);
        linearLayoutManager2.setReverseLayout(true);
        recyclerView2.setLayoutManager(new LinearLayoutManager(this));
        lastOrderList = new ArrayList<>();
        orderRef.orderBy("timestamp", Query.Direction.DESCENDING).whereEqualTo("orderBy", FirebaseAuth.getInstance().getCurrentUser().getUid()).whereEqualTo("status", "Delivered").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()){
                            lastOrderList.clear();
                            orderQuantity2.setVisibility(View.VISIBLE);
                            for (DocumentSnapshot snapshot:queryDocumentSnapshots){
                                orderQuantity2.setText("Last Order: "+queryDocumentSnapshots.size());
                                Order order = snapshot.toObject(Order.class);
                                lastOrderList.add(order);
                            }
                            myorderAdapter = new MyorderAdapter(getApplicationContext(), lastOrderList);
                            recyclerView2.setAdapter(myorderAdapter);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("myTag", "This is my message "+e.getMessage() );
            }
        });

    }
}

