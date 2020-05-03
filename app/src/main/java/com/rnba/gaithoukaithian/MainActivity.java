package com.rnba.gaithoukaithian;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.rnba.gaithoukaithian.adapter.ProductsAdapter;
import com.rnba.gaithoukaithian.model.Cart;
import com.rnba.gaithoukaithian.model.Product;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference productRef = db.collection("Products");
    private CollectionReference userRef = db.collection("Users");
    private ImageView cart,account;
    private TextView orderTxt, orderNumber,cartCount;
    private Button viewOrder;
    private LinearLayout orderCLay;

    ProductsAdapter productsAdapter;
    List<Product> productList;
    RecyclerView recyclerView;
    ProgressBar progressBar;


    private boolean isOnline = false;
    private TextView create;

    String from, orderID, orderTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

         recyclerView = findViewById(R.id.recyclerview);
         progressBar = findViewById(R.id.progressBar);
         cart = findViewById(R.id.cart);
        orderTxt = findViewById(R.id.orderTxt);
        orderNumber = findViewById(R.id.orderNumber);
        viewOrder = findViewById(R.id.viewOrder);
        orderCLay = findViewById(R.id.orderCLay);
        cartCount = findViewById(R.id.cartCount);

        account = findViewById(R.id.account);

        final Intent intent = getIntent();
        from = intent.getExtras().getString("from");
        orderID = intent.getExtras().getString("orderID");
        orderTime = intent.getExtras().getString("orderTime");




        //checkifromorder
        checkfrom();

         cart.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 if (FirebaseAuth.getInstance().getCurrentUser() !=null){
                     startActivity(new Intent(getApplicationContext(), CartActivity.class));
                 }else {
                     Toast.makeText(MainActivity.this, "required login first.", Toast.LENGTH_SHORT).show();
                     Intent intent1 = new Intent(getApplicationContext(), ProfileActivity.class);
                     intent1.putExtra("show", "userProfile");
                     intent1.putExtra("updating", true);
                     startActivity(intent1);
                 }
             }
         });
         account.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
//                         startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                 Intent intent1 = new Intent(getApplicationContext(), ProfileActivity.class);
                 intent1.putExtra("show", "userProfile");
                 intent1.putExtra("updating", true);
                 startActivity(intent1);
             }
         });


    }

    private void checkfrom() {
        if (from.equals("order")){
            orderCLay.setVisibility(View.VISIBLE);
            orderTxt.setText("Time: "+orderTime);
            orderNumber.setText("reference id: "+orderID);
        }
        viewOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, MyorderActivity.class));
            }
        });
    }

    private void loadProduct() {
        recyclerView.setHasFixedSize(true);
        GridLayoutManager linearLayoutManager = new GridLayoutManager(this,2);
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this,2));
        productRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                productList = new ArrayList<>();
                productList.clear();
                for (QueryDocumentSnapshot documentSnapshot:queryDocumentSnapshots){
                    Product product = documentSnapshot.toObject(Product.class);
                    productList.add(product);
                }
                productsAdapter = new ProductsAdapter(MainActivity.this, productList);
                recyclerView.setAdapter(productsAdapter);
                progressBar.setVisibility(View.GONE);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, "Error:"+e.getMessage().toLowerCase(), Toast.LENGTH_LONG).show();
            }
        });

        //show cartcoutn
       if (FirebaseAuth.getInstance().getCurrentUser() !=null){
           userRef.document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("Cart").get()
                   .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                       @Override
                       public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                           int mycartcount ;
                           if (!queryDocumentSnapshots.isEmpty()){
                               mycartcount = queryDocumentSnapshots.size();
                               cartCount.setText(""+mycartcount);
                           }
                           mycartcount = 0;
                       }
                   });
       }

    }

    @Override
    protected void onStart() {
        super.onStart();
        loadProduct();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadProduct();
        ConnectivityManager connMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            // fetch data
        } else {
            new AlertDialog.Builder(this)
                    .setTitle("Connection Failure")
                    .setMessage("Please Connect to the Internet")
                    .setCancelable(false)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            moveTaskToBack(true);
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
    }

    @Override
    public void onBackPressed() {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Wanna exit the app?");

        alertDialogBuilder.setPositiveButton("yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                       moveTaskToBack(true);
                    }
                });

        alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        final AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.show();
    }


}

