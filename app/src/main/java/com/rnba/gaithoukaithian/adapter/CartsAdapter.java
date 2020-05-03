package com.rnba.gaithoukaithian.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rnba.gaithoukaithian.CartActivity;
import com.rnba.gaithoukaithian.ProductActivity;
import com.rnba.gaithoukaithian.R;
import com.rnba.gaithoukaithian.model.Cart;
import com.rnba.gaithoukaithian.model.Product;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.UUID;

public class CartsAdapter extends RecyclerView.Adapter<CartsAdapter.ViewHolder> {
    Context context;
    List<Cart> cartList;

    public CartsAdapter(Context context, List<Cart> cartList) {
        this.context = context;
        this.cartList = cartList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CartsAdapter.ViewHolder(LayoutInflater.from(context).inflate(R.layout.cart_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final Cart cart = cartList.get(position);
        holder.quantity.setText(""+cart.getQuantity());

        holder.btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { removeQuantity(cart, holder.quantity); }
        });
        holder.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { addQuantity(cart, holder.quantity);    }
        });



        //checkQauntity
        checkproductQuantity(cart, holder.quantity, holder.instock);
        //savelater
        holder.saveLater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveForLater(cart);
            }
        });
        //deleteitem
        holder.Delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteItem(cart);
            }
        });
        

        //start new activity
       holder.imageView.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               goToProductActivity(cart);
           }
       });
        holder.productName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {goToProductActivity(cart);    }
        });

        //load product details
        FirebaseFirestore db = FirebaseFirestore.getInstance();
         CollectionReference productRef = db.collection("Products");
         productRef.document(cart.getProductid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
             @Override
             public void onSuccess(DocumentSnapshot documentSnapshot) {
                 Product product = documentSnapshot.toObject(Product.class);
                 Picasso.get().load(product.getImageURL()).into(holder.imageView);
                 holder.productName.setText(product.getTitle());
                 holder.price.setText("\u20B9"+product.getdPrice());
//                 if (product.getStocks()>0){
//                     holder.instock.setText("In stock");
//                 }else {
//                     holder.instock.setText("Currently unavailable");
//                     holder.instock.setTextColor(R.drawable.btn_bg1);
//                 }
                 if(product.getDeliveryFee()>0){
                     holder.deliveryFee.setText("- Delivery fee \u20b9"+product.getDeliveryFee());
                 }else {
                     holder.deliveryFee.setText("- Free delivery");
                 }
             }
         });
    }

    private void checkproductQuantity(final Cart cart,final TextView quantity, final TextView instock ) {

        final long Quantity = Integer.parseInt(quantity.getText().toString());
        final long newQuantiy = Quantity +1;
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference productRef = db.collection("Products");
        final CollectionReference cartRef = db.collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("Cart");
        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Please wait..");
        progressDialog.setCancelable(false);
        progressDialog.show();;
        productRef.document(cart.getProductid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Product product = documentSnapshot.toObject(Product.class);
                if (product.getStocks()<Quantity){
//                    if (context instanceof CartActivity){
//                        ((CartActivity)context).disableCheckOutBtn();
//                    }
//                    instock.setText("Available: "+product.getStocks()+ " (quantity)");
//                    instock.setTextColor(Color.RED);
                    cartRef.document(cart.getProductid()).update("quantity", product.getStocks());
                    quantity.setText(""+product.getStocks());
                    progressDialog.dismiss();
                }else {
                    instock.setText("In stock");
                    progressDialog.dismiss();
                }
            }
        });
    }


    private void saveForLater(final Cart cart) {
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        String id = UUID.randomUUID().toString();
        CollectionReference productRef = db.collection("Users")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("Favorite");
        Cart fav = new Cart(id, cart.getProductid() ,cart.getPurchaserid(), cart.getPrice(), cart.getDeliveryFee(),1, System.currentTimeMillis());
        productRef.document(cart.getProductid()).set(fav).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                CollectionReference productRef = db.collection("Users")
                        .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .collection("Cart");
                productRef.document(cart.getProductid()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context, "Saved for later.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        
    }
    private void deleteItem(Cart cart) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference productRef = db.collection("Users");
        productRef.document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("Cart").document(cart.getProductid()).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context, "Item Removed!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void goToProductActivity(Cart cart) {
        Intent intent = new Intent(context, ProductActivity.class);
        intent.putExtra("id", cart.getProductid());
        context.startActivity(intent);
    }

    private void addQuantity(final Cart cart, final TextView quantity) {
        long Quantity = Integer.parseInt(quantity.getText().toString());
        final long newQuantiy = Quantity +1;
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference productRef = db.collection("Products");
        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Please wait..");
        progressDialog.setCancelable(false);
        progressDialog.show();;
        productRef.document(cart.getProductid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Product product = documentSnapshot.toObject(Product.class);
                if (product.getStocks()<newQuantiy){
                    Toast.makeText(context, "Out of stock", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }else {
                    progressDialog.dismiss();
                    if (newQuantiy<11){
                        quantity.setText(""+newQuantiy);
                        updateQuantityInDB(newQuantiy, cart.getProductid());
                    }
                }
            }
        });

    }

    private void removeQuantity(Cart cart, final TextView quantity) {
        long Quantity = Integer.parseInt(quantity.getText().toString());
        final long newQuantiy = Quantity -1;
        if (newQuantiy>0){
            quantity.setText(""+newQuantiy);
            updateQuantityInDB(newQuantiy, cart.getProductid());
        }
    }

    private void updateQuantityInDB(long newQuantiy, String productid) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference cartRef = db.collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("Cart");
        cartRef.document(productid).update("quantity", newQuantiy).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                calculateSubTotal();     
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "Failed to add/remove item.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void calculateSubTotal() {
    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private ImageView imageView;
        private TextView  productName, price, instock, quantity, deliveryFee;
        private Button saveLater, Delete, btnAdd, btnRemove;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            productName = itemView.findViewById(R.id.productName);
            price = itemView.findViewById(R.id.price);
            instock = itemView.findViewById(R.id.instock);
            quantity = itemView.findViewById(R.id.quantity);
            deliveryFee = itemView.findViewById(R.id.deliveryFee);

            saveLater = itemView.findViewById(R.id.saveLater);
            Delete = itemView.findViewById(R.id.deleteItem);
            btnAdd = itemView.findViewById(R.id.btnAdd);
            btnRemove = itemView.findViewById(R.id.btnRemove);


        }
    }
}
