package com.rnba.gaithoukaithian;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;
import com.rnba.gaithoukaithian.model.Cart;
import com.rnba.gaithoukaithian.model.Order;
import com.rnba.gaithoukaithian.model.Product;
import com.rnba.gaithoukaithian.model.User;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class CheckoutActivity extends AppCompatActivity implements PaymentResultListener {

    private Button selectAddressBtn, editAddressBtn, editBtn, proceedChckout, continuePayment,PlayOrderCOD;
    private TextView addressField, fullName, phone, caddress;
    private CardView lay1, lay2,lay3,lay4;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference userRef = db.collection("Users");
    private CollectionReference orderRef = db.collection("Orders");
    private CollectionReference productRef = db.collection("Products");

    private RadioGroup radioPyamentOption;
    private RadioButton radioPaymentButton;
    private Boolean userexist = false;
    String totalPirce, quantity, shippingAddress, cartID, userPhone, userEmail;

    ProgressDialog orderingPD;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        final Intent intent = getIntent();
        totalPirce = intent.getExtras().getString("totalPrice");
        quantity = intent.getExtras().getString("quantity");

        addressField = findViewById(R.id.address);
        selectAddressBtn = findViewById(R.id.selectAddressBtn);
        editAddressBtn = findViewById(R.id.editAddressBtn);
        editBtn = findViewById(R.id.editBtn);
        proceedChckout = findViewById(R.id.proceedBtn);
        continuePayment = findViewById(R.id.continuePayment);
        PlayOrderCOD = findViewById(R.id.PlayOrderCOD);
        radioPyamentOption = findViewById(R.id.radioPyamentOption);
        fullName = findViewById(R.id.fullName);
        phone = findViewById(R.id.phone);
        caddress = findViewById(R.id.caddress);
        lay1 = findViewById(R.id.lay1);
        lay2 = findViewById(R.id.lay2);
        lay3 = findViewById(R.id.lay3);
        lay4 = findViewById(R.id.lay4);



        editAddressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startProfileEditAcitivity();
            }
        });
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startProfileEditAcitivity();
            }
        });
        selectAddressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCheckOutDetails();
            }
        });

        String s = FirebaseAuth.getInstance().getCurrentUser().getUid().substring(0,5);
        cartID = "COID-"+s+"-"+System.currentTimeMillis();

        //closeActivity
        ImageView btnClose = findViewById(R.id.btnClose);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //proceedcheckout
        proceedChckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lay2.setVisibility(View.GONE);
                lay3.setVisibility(View.VISIBLE);    }
        });
        //proceed payment
        continuePayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProceedWithPayment();
            }
        });
    }

    private void startProfileEditAcitivity() {
        if (userexist){
            Intent intent = new Intent(this,ProfileActivity.class);
            intent.putExtra("updating", true);
            intent.putExtra("show", "editLay");
            startActivity(intent);
        }else {
            Intent intent = new Intent(this,ProfileActivity.class);
            intent.putExtra("updating", false);
            intent.putExtra("show", "editLay");
            startActivity(intent);
        }
    }

    private void ProceedWithPayment() {
        int selectedId = radioPyamentOption.getCheckedRadioButtonId();
        radioPaymentButton = findViewById(selectedId);

        if (radioPaymentButton.getTag().toString().equals("COD")){
           orderNcodpayment();
        }else if (radioPaymentButton.getTag().toString().equals("Online")){
            startOnlinePaymenGateWay();
        }
    }

    private void startOnlinePaymenGateWay() {

        final Activity activity = this;
        Checkout checkout = new Checkout();
        Toast.makeText(activity, ""+totalPirce, Toast.LENGTH_SHORT).show();
        int op = Integer.parseInt(totalPirce);
        int orderPriceV = op *100;

        try {
            JSONObject object = new JSONObject();
            object.put("Name", "Gaithou Kaithian");
            object.put("description", "Online purchase");
            object.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png");
            object.put("currency", "INR");
            object.put("amount", orderPriceV);

            JSONObject preFill = new JSONObject();
            preFill.put("email", userEmail);
            preFill.put("contact", userPhone);

            object.put("prefill", preFill);
            checkout.open(activity, object);

        }catch (Exception e){
            Toast.makeText(this, "error:"+e.getMessage().toLowerCase(), Toast.LENGTH_SHORT).show();
        }
    }

    private void orderNcodpayment() {
        orderingPD = new ProgressDialog(CheckoutActivity.this);
        orderingPD.setMessage("Processing..");
        orderingPD.setCancelable(false);
        orderingPD.show();
        userRef.document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("Cart")
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (!queryDocumentSnapshots.isEmpty()){
                    int total=0;
                    for (QueryDocumentSnapshot documentSnapshots:queryDocumentSnapshots){
                        Cart cart = documentSnapshots.toObject(Cart.class);
                        total = queryDocumentSnapshots.size();
                        addingOrder(cart, cartID, "COD");
                    }

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            orderingPD.dismiss();
                            //timeanddate
                            Calendar calForTime = Calendar.getInstance();
                            SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh:mm a");
                            String currentTime =currentTimeFormat.format(calForTime.getTime());
                            Calendar calForDate = Calendar.getInstance();
                            SimpleDateFormat currentDateFormat = new SimpleDateFormat("dd-mm-yyyy");
                            String currentDate = currentDateFormat.format(calForDate.getTime());
                            final String currentTimeNdate = currentTime +"--"+ currentDate;
                            //delete cart
                            userRef.document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("Cart")
                                    .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    for (DocumentSnapshot documentSnapshot:queryDocumentSnapshots){
                                        String id = documentSnapshot.getId();
                                        userRef.document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("Cart")
                                                .document(id).delete();
                                    }
                                }
                            });

                            Toast.makeText(CheckoutActivity.this, "Order submited", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            Intent intnt = new Intent(CheckoutActivity.this, MainActivity.class);
                            intnt.putExtra("from","order");
                            intnt.putExtra("orderID",cartID);
                            intnt.putExtra("orderTime",currentTimeNdate);
                            startActivity(intnt);
                        }
                    },3000);
                }
            }
        });
    }

    private void LoadDetailFromData() {
        //load userinfo
        userRef.document(FirebaseAuth.getInstance().getCurrentUser().getUid()).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                       if (documentSnapshot.exists()){
                           User user = documentSnapshot.toObject(User.class);
                           addressField.setText(user.getAddress());
                           fullName.setText(user.getFullName());
                           phone.setText(user.getPhone());
                           caddress.setText(user.getAddress());
                           shippingAddress = user.getAddress();
                           userPhone = user.getPhone();
                           userEmail = user.getEmail();
                           selectAddressBtn.setEnabled(true);
                           userexist = true;
                       }
                    }
                });
    }
    private void addingOrder(final Cart cart, final String cartID, final String paymentType) {
        String s = FirebaseAuth.getInstance().getCurrentUser().getUid().substring(0,5);
        final String orderid = "OID-"+s+"-"+System.currentTimeMillis();
        //timeanddate
        Calendar calForTime = Calendar.getInstance();
        SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh:mm a");
         String currentTime =currentTimeFormat.format(calForTime.getTime());
        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDateFormat = new SimpleDateFormat("dd-mm-yyyy");
         String currentDate = currentDateFormat.format(calForDate.getTime());

        final String currentTimeNdate = currentTime +"/"+ currentDate;
       productRef.document(cart.getProductid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
           @Override
           public void onSuccess(DocumentSnapshot documentSnapshot) {
               Product p = documentSnapshot.toObject(Product.class);
               long q1 = Integer.parseInt(quantity);
               long q2 = p.getStocks() -q1;
               productRef.document(cart.getProductid()).update("stocks", q2);

           }
       });
        productRef.document(cart.getProductid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Product product = documentSnapshot.toObject(Product.class);
                Order order = new Order(orderid, FirebaseAuth.getInstance().getCurrentUser().getUid(),shippingAddress, cart.getProductid(),
                        cartID, "In Progress", product.getTitle(), product.getImageURL(),paymentType ,product.getdPrice(), product.getDeliveryFee(),cart.getQuantity() ,System.currentTimeMillis(), false ,currentTimeNdate);
                orderRef.document(orderid).set(order);
            }
        });
    }

    private void showCheckOutDetails() {
        lay1.setVisibility(View.GONE);
        lay2.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPaymentSuccess(String s) {
        orderingPD = new ProgressDialog(CheckoutActivity.this);
        orderingPD.setMessage("Processing..");
        orderingPD.setCancelable(false);
        orderingPD.show();
        userRef.document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("Cart")
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (!queryDocumentSnapshots.isEmpty()){
                    int total=0;
                    for (QueryDocumentSnapshot documentSnapshots:queryDocumentSnapshots){
                        Cart cart = documentSnapshots.toObject(Cart.class);
                        total = queryDocumentSnapshots.size();
                        addingOrder(cart, cartID, "PAID");
                    }
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            orderingPD.dismiss();
                            //timeanddate
                            Calendar calForTime = Calendar.getInstance();
                            SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh:mm a");
                            String currentTime =currentTimeFormat.format(calForTime.getTime());
                            Calendar calForDate = Calendar.getInstance();
                            SimpleDateFormat currentDateFormat = new SimpleDateFormat("dd-mm-yyyy");
                            String currentDate = currentDateFormat.format(calForDate.getTime());
                            final String currentTimeNdate = currentTime +"--"+ currentDate;
                            //delete cart
                            userRef.document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("Cart")
                                    .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    for (DocumentSnapshot documentSnapshot:queryDocumentSnapshots){
                                        String id = documentSnapshot.getId();
                                        userRef.document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("Cart")
                                                .document(id).delete();
                                    }
                                }
                            });
                            Toast.makeText(CheckoutActivity.this, "Order submited", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            Intent intnt = new Intent(CheckoutActivity.this, MainActivity.class);
                            intnt.putExtra("from","order");
                            intnt.putExtra("orderID",cartID);
                            intnt.putExtra("orderTime",currentTimeNdate);
                            startActivity(intnt);
                        }
                    },3000);
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        LoadDetailFromData();
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
    public void onPaymentError(int i, String s) {
        Toast.makeText(this, " "+s, Toast.LENGTH_SHORT).show();
        finish();
    }
}
