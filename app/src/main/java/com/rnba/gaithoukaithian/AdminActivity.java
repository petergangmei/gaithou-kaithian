package com.rnba.gaithoukaithian;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.rnba.gaithoukaithian.adapter.MyorderAdapter;
import com.rnba.gaithoukaithian.adapter.OrdersAdapter;
import com.rnba.gaithoukaithian.adapter.ProductsAdapter;
import com.rnba.gaithoukaithian.adapter.UserAdapter;
import com.rnba.gaithoukaithian.model.Order;
import com.rnba.gaithoukaithian.model.Product;
import com.rnba.gaithoukaithian.model.User;

import java.util.ArrayList;
import java.util.List;

public class AdminActivity extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference userRef = db.collection("Users");
    private CollectionReference ordertRef = db.collection("Orders");
    private CollectionReference productRef = db.collection("Products");

    RecyclerView recyclerView;
    ProductsAdapter productsAdapter;
    OrdersAdapter myorderAdapter;
    UserAdapter userAdapter;
    List<Order> orderList;
    List<Product> productList;
    List<User> usersList;

    ImageView postProduct;

    String filterValue = null;
    Button BtnFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);


        BtnFilter = findViewById(R.id.BtnFilter);
        recyclerView = findViewById(R.id.recyclerview);
        postProduct = findViewById(R.id.addItem);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        productList = new ArrayList<>();
        orderList = new ArrayList<>();
        usersList = new ArrayList<>();
        //load spiner
        loadSpiner();

        //Filter query
        BtnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QueryDatabase();
            }
        });
        //
        postProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), CreateActivity.class));
            }
        });





    }

    private void loadSpiner() {
        Spinner dynamicSpinner = (Spinner) findViewById(R.id.spinner1);
        String[] items = new String[]{"Customer", "Employee", "Products", "order-In progress", "order-Delivered", "order-canceled"};

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, items);

        dynamicSpinner.setAdapter(adapter);

        dynamicSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                Log.v("item", (String) parent.getItemAtPosition(position));
                switch (position) {
                    case 0:
                        filterValue = "customer";
                        return;
                    case 1:
                        filterValue = "employee";
                        return;
                    case 2:
                        filterValue = "Products";
                        return;
                    case 3:
                        filterValue = "In Progress";
                        return;
                    case 4:
                        filterValue = "Delivered";
                        return;
                    case 5:
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

    private void QueryDatabase() {
        Toast.makeText(this, "Filter: " + filterValue, Toast.LENGTH_SHORT).show();
        if (!filterValue.isEmpty()) {
            if (filterValue.equals("customer")) {
                FilterUsers(filterValue);

            } else if (filterValue.equals("employee")) {
                FilterUsers(filterValue);
            } else if (filterValue.equals("Products")) {
                FilterProducts(filterValue);

            } else if (filterValue.equals("In Progress")) {
                FilterOrder(filterValue);

            } else if (filterValue.equals("Delivered")) {
                FilterOrder(filterValue);

            } else if (filterValue.equals("Canceled")) {
                FilterOrderDelete(filterValue);
            }
        }
    }


    private void FilterProducts(String filterValue) {

        productRef.orderBy("timestamp", Query.Direction.DESCENDING).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                productList.clear();
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    Product product = documentSnapshot.toObject(Product.class);
                    productList.add(product);

                }
                productsAdapter = new ProductsAdapter(getApplicationContext(), productList);
                recyclerView.setAdapter(productsAdapter);

            }
        });
    }

    private void FilterOrder(String filterValue) {
        ordertRef.orderBy("timestamp", Query.Direction.DESCENDING).whereEqualTo("status", filterValue).whereEqualTo("ordercanceled", false).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                orderList.clear();
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    Order order = documentSnapshot.toObject(Order.class);
                    orderList.add(order);

                }
                myorderAdapter = new OrdersAdapter(AdminActivity.this, orderList);
                recyclerView.setAdapter(myorderAdapter);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("myTag", "msg:" + e.getMessage());
            }
        });
    }

    private void FilterOrderDelete(String filterValue) {
        ordertRef.orderBy("timestamp", Query.Direction.DESCENDING).whereEqualTo("status", "Delivered").whereEqualTo("ordercanceled", true).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                orderList.clear();
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    Order order = documentSnapshot.toObject(Order.class);
                    orderList.add(order);

                }
                myorderAdapter = new OrdersAdapter(AdminActivity.this, orderList);
                recyclerView.setAdapter(myorderAdapter);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("myTag", "msg:" + e.getMessage());
            }
        });
    }


    private void FilterUsers(String filterValue) {
        userRef.orderBy("timestamp", Query.Direction.DESCENDING).whereEqualTo("role", filterValue).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                usersList.clear();
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    User user = documentSnapshot.toObject(User.class);
                    usersList.add(user);
                }
                userAdapter = new UserAdapter(AdminActivity.this, usersList);
                recyclerView.setAdapter(userAdapter);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("myTag", "msg" + e.getMessage());
            }
        });
    }




}
