package com.rnba.gaithoukaithian;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.rnba.gaithoukaithian.model.Cart;
import com.rnba.gaithoukaithian.model.Product;
import com.squareup.picasso.Picasso;

import java.util.UUID;

public class ProductActivity extends AppCompatActivity {
    private TextView title, description, mrpPrice, cartCount, dPrice, sPrice, deliveryFee, stockCount, sallerLocation,myName, stock;
    private ImageView imageView,cart;
    private EditText quantity;
    String imageURL, productid, title_v;
    private LinearLayout codLay;
    private RelativeLayout mainBody;
    private SpinKitView spin_kit;
    private long deliveryFeeV = 0;

    private Button BtnAddToCart, BtnBuyNow;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference productRef = db.collection("Products");
    private CollectionReference userRef = db.collection("Users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        final Intent intent = getIntent();
        productid = intent.getExtras().getString("id");


        imageView = findViewById(R.id.imageView);
        cart = findViewById(R.id.cart);
        title = findViewById(R.id.title);
        mrpPrice = findViewById(R.id.MRPprice);
        dPrice =findViewById(R.id.DISCOUNTEDprice);
        sPrice = findViewById(R.id.SAVEprice);
        stockCount = findViewById(R.id.stockCount);
        stock = findViewById(R.id.stock);
        sallerLocation = findViewById(R.id.sallerLocation);
        spin_kit = findViewById(R.id.spin_kit);
        quantity = findViewById(R.id.quantity);
        BtnBuyNow =findViewById(R.id.BtnBuyNow);
        myName = findViewById(R.id.myName);

        codLay = findViewById(R.id.codLay);
        deliveryFee = findViewById(R.id.deliveryFee);
        mainBody = findViewById(R.id.mainBody);

        description = findViewById(R.id.description);
        cartCount = findViewById(R.id.cartCount);
        BtnAddToCart = findViewById(R.id.addToCart);



        loadDetails();
        loadCartCount();

        //closeActivity
        ImageView btnClose = findViewById(R.id.btnClose);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {startActivity(new Intent(getApplicationContext(), CartActivity.class));}
        });


        //buynow
        BtnBuyNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long price = Integer.parseInt(dPrice.getText().toString());
                if (FirebaseAuth.getInstance().getCurrentUser() != null){
                    addToCart(price, true);
                }else {
                    Intent intent1 = new Intent(getApplicationContext(), ProfileActivity.class);
                    intent1.putExtra("show", "userProfile");
                    intent1.putExtra("updating", false);
                    startActivity(intent1);
                    Toast.makeText(ProductActivity.this, "required login first", Toast.LENGTH_SHORT).show();
                }
            }
        });
        //add to cart
        BtnAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FirebaseAuth.getInstance().getCurrentUser() !=null){
                    BtnBuyNow = findViewById(R.id.BtnBuyNow);
                    if (quantity.getText().toString().isEmpty()){
                        Toast.makeText(getApplicationContext(), "Require minimum 1 product quantity.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    long price = Integer.parseInt(dPrice.getText().toString());

                    int itemCount = Integer.parseInt(cartCount.getText().toString());
                    if (itemCount<10){
                        int newItemCount = itemCount +1;
                        cartCount.setText(""+newItemCount);
                        addToCart(price, false);
                    }else {
                        Toast.makeText(ProductActivity.this, "Sorry, there is limited quanity of this item available.", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(ProductActivity.this, "You need to login first.", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                }
            }
        });


    }

    private void loadCartCount() {
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



    private void addToCart(long price, final boolean buynow) {
        String id = UUID.randomUUID().toString();
       if (quantity.getText().toString().isEmpty()){
           Toast.makeText(this, "Require minimum 1 product quantity.", Toast.LENGTH_SHORT).show();
           return;
       }
        int quantyV = Integer.parseInt(quantity.getText().toString());
        if (quantyV<1){
            Toast.makeText(this, "Require minimum 1 product quantity.", Toast.LENGTH_SHORT).show();
            return;
        }
        Cart cart = new Cart(id,productid, FirebaseAuth.getInstance().getCurrentUser().getUid(),
                price, deliveryFeeV,quantyV, System.currentTimeMillis()
        );
//        progressdialog
        final ProgressDialog progressDoalog = new ProgressDialog(ProductActivity.this);
        progressDoalog.setMax(100);
        progressDoalog.setMessage("Processing..");
        progressDoalog.setCancelable(false);
        progressDoalog.show();
        userRef.document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("Cart")
                .document(productid).set(cart).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                progressDoalog.dismiss();
                if (buynow){
                    startActivity(new Intent(getApplicationContext(),CartActivity.class));
                }else {
                    Toast.makeText(ProductActivity.this, "Added to cart!", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ProductActivity.this, "Failed to add to cart: "+e.getMessage().toLowerCase(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void loadDetails() {


        //load product detail
        productRef.document(productid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Product product = documentSnapshot.toObject(Product.class);
                title.setText(product.getTitle());
                Picasso.get().load(product.getImageURL()).into(imageView);
                mrpPrice.setText(""+product.getmPrice());
                mrpPrice.setPaintFlags(mrpPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                dPrice.setText(""+product.getdPrice());
                int saving = product.getmPrice() - product.getdPrice();
                sPrice.setText(""+saving);
                description.setText(product.getDescription());
                sallerLocation.setText(product.getSallerLocation() + "(saller location)");
                deliveryFeeV = product.getDeliveryFee();

                //stocks
                if (product.getStocks()>10){
                    stockCount.setText(" "+product.getStocks()+ " items available.");
                }else if (product.getStocks()==0){
                    stockCount.setText("Coming soon...");
                }else {
                    stockCount.setText("Hurry! Only "+product.getStocks()+ " items left.");
                }

                if (product.getStocks()==0){
                    stock.setText("Out of stock");
                    stock.setTextColor(Color.RED);
                    BtnAddToCart.setEnabled(false);
                    BtnBuyNow.setEnabled(false);
                }
                if (product.isCod()){
                    codLay.setVisibility(View.VISIBLE);
                }
                if (product.getDeliveryFee() >0){
                    deliveryFee.setText("Delivery Fee \u20B9"+product.getDeliveryFee());
                }
                mainBody.setVisibility(View.VISIBLE);
                spin_kit.setVisibility(View.GONE);

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
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
