package com.rnba.gaithoukaithian;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.rnba.gaithoukaithian.adapter.CartsAdapter;
import com.rnba.gaithoukaithian.adapter.ProductsAdapter;
import com.rnba.gaithoukaithian.model.Cart;
import com.rnba.gaithoukaithian.model.Product;

import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity {


    CartsAdapter cartsAdapter;
    List<Cart> cartList;
    RecyclerView recyclerView;
    ProgressBar progressBar;

    Button btnCheckOut;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference cartRef = db.collection("Users");
    private CollectionReference productRef = db.collection("Products");

    ArrayList<String> productIds;
    ArrayList<Long> quantitys;
    long dTotalPirce = 0;
    long dOrderQuantity = 0;


    long totolPrice = 0;
    long deliveryFee = 0;
    long totalDfee = 0;
    long totalItem = 0;
    long static_itemTotal = 0 ;
    TextView subtotal,cartCount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        subtotal = findViewById(R.id.subtotal);
        cartCount = findViewById(R.id.cartCount);
        recyclerView = findViewById(R.id.recyclerview);
        btnCheckOut = findViewById(R.id.btnCheckOut);


        btnCheckOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkoutProcess();
            }
        });


    }

    private void checkoutProcess() {
        Intent intent = new Intent(CartActivity.this, CheckoutActivity.class);
        intent.putExtra("totalPrice", String.valueOf(dTotalPirce));
        intent.putExtra("quantity", String.valueOf(dOrderQuantity));
        startActivity(intent);

    }

    public void disableCheckOutBtn(){
        btnCheckOut.setEnabled(false);
    }
    private void loadCartItem() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(CartActivity.this));
        cartRef.document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("Cart").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                cartList = new ArrayList<>();
                cartList.clear();
                if(!queryDocumentSnapshots.isEmpty()) {
                    btnCheckOut.setVisibility(View.VISIBLE);
                    for (QueryDocumentSnapshot documentSnapshot:queryDocumentSnapshots){
                        Cart cart = documentSnapshot.toObject(Cart.class);
                        static_itemTotal = queryDocumentSnapshots.size();
                        cartList.add(cart);
                        cartsAdapter = new CartsAdapter(CartActivity.this, cartList);
                        recyclerView.setAdapter(cartsAdapter);
                    }
                }
                //cartotal()
                carttotal();
            }
        });
    }

    private void carttotal() {
        //cartotal
        cartRef.document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("Cart").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                productIds = new ArrayList<>();
                productIds.clear();
                quantitys = new ArrayList<>();
                quantitys.clear();
                for (QueryDocumentSnapshot documentSnapshot:queryDocumentSnapshots){
                    Cart cart = documentSnapshot.toObject(Cart.class);
                    totalItem = queryDocumentSnapshots.size();
                    deliveryFee = cart.getDeliveryFee();
                    long tpprice = cart.getPrice() * cart.getQuantity();
                    long aProductPrice = tpprice +deliveryFee;
                    totolPrice = totolPrice +aProductPrice;
                    totalDfee = totalDfee + deliveryFee;

                    productIds.add(cart.getProductid());
                    quantitys.add(cart.getQuantity());
                }
                dOrderQuantity  = totalItem;

                subtotal.setText("Cart subtotal ("+totalItem+ " item) : \u20b9"+totolPrice + "\n(delivery fee: \u20b9"+totalDfee+")");
                dTotalPirce = totolPrice;

                cartCount.setText(""+totalItem);
                if (static_itemTotal !=totalItem){
                    loadCartItem();
                }
                if (totalItem<1){
                    subtotal.setText("Cart empty!");
                    btnCheckOut.setVisibility(View.INVISIBLE);
                    recyclerView.setVisibility(View.INVISIBLE);
                }

                totalItem = 0;
                totolPrice = 0;
                deliveryFee = 0;
                totalDfee = 0;

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCartItem();
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

}
