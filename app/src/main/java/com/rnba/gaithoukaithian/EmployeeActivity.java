package com.rnba.gaithoukaithian;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.rnba.gaithoukaithian.adapter.MyorderAdapter;
import com.rnba.gaithoukaithian.adapter.OrdersAdapter;
import com.rnba.gaithoukaithian.adapter.ProductsAdapter;
import com.rnba.gaithoukaithian.model.Order;
import com.rnba.gaithoukaithian.model.Product;

import java.util.ArrayList;
import java.util.List;

public class EmployeeActivity extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference ordertRef = db.collection("Orders");
    String filterValue;
    Button btnFilter;
    RecyclerView recyclerView;
    OrdersAdapter myorderAdapter;
    List<Order> orderList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee);


        btnFilter = findViewById(R.id.BtnFilter);
        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        loadSpiner();
        orderList = new ArrayList<>();
        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QueryDatabase();
            }
        });
    }

    private void QueryDatabase() {
        if (!filterValue.isEmpty()){
            if (filterValue.equals("Delivered")){
                FilterOrder(filterValue);
            }else if (filterValue.equals("In Progress")){
                FilterOrder(filterValue);
            }else if(filterValue.equals("Canceled")){
                FilterOrderDelete(filterValue);
            }
        }
    }



    private void FilterOrder(String filterValue) {
        ordertRef.orderBy("timestamp", Query.Direction.DESCENDING).whereEqualTo("status", filterValue).whereEqualTo("ordercanceled", false).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                orderList.clear();
                for (QueryDocumentSnapshot documentSnapshot:queryDocumentSnapshots){
                    Order order = documentSnapshot.toObject(Order.class);
                    orderList.add(order);

                }
                myorderAdapter = new OrdersAdapter(EmployeeActivity.this, orderList);
                recyclerView.setAdapter(myorderAdapter);
                Toast.makeText(EmployeeActivity.this, "sucess", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EmployeeActivity.this, "failed", Toast.LENGTH_SHORT).show();
                Log.d("myTag", "msg:"+e.getMessage());
            }
        });
    }

    private void FilterOrderDelete(String filterValue) {
        ordertRef.orderBy("timestamp", Query.Direction.DESCENDING).whereEqualTo("status", "Delivered").whereEqualTo("ordercanceled",true).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                orderList.clear();
                for (QueryDocumentSnapshot documentSnapshot:queryDocumentSnapshots){
                    Order order = documentSnapshot.toObject(Order.class);
                    orderList.add(order);

                }
                myorderAdapter = new OrdersAdapter(EmployeeActivity.this, orderList);
                recyclerView.setAdapter(myorderAdapter);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("myTag", "msg:"+e.getMessage());
            }
        });
    }
    private void loadSpiner() {
        Spinner dynamicSpinner = (Spinner) findViewById(R.id.spinner1);
        String[] items = new String[] { "order-In progress", "order-Delivered", "order-canceled" };

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, items);

        dynamicSpinner.setAdapter(adapter);

        dynamicSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                Log.v("item", (String) parent.getItemAtPosition(position));
                switch (position){
                    case 0:
                        filterValue = "In Progress";
                        return;
                    case 1:
                        filterValue = "Delivered";
                        return;
                    case 2:
                        filterValue = "Canceled";
                        return;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });
    }

}
