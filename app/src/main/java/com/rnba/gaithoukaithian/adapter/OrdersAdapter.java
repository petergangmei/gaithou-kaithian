package com.rnba.gaithoukaithian.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.rnba.gaithoukaithian.MyorderActivity;
import com.rnba.gaithoukaithian.R;
import com.rnba.gaithoukaithian.model.Order;
import com.rnba.gaithoukaithian.model.User;
import com.squareup.picasso.Picasso;

import java.util.List;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.ViewHolder> {

    Context context;
    List<Order> productsList;

    public OrdersAdapter(Context context, List<Order> productsList) {
        this.context = context;
        this.productsList = productsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.myorder_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Order product = productsList.get(position);
        holder.price.setText("\u20b9"+product.getTotalPrice() * product.getQuantity());
        holder.oderOn.setText("Order On: "+product.getTimeNdate());
        holder.orderId.setText("OrderID: "+product.getOrderId());
        Picasso.get().load(product.getProductImage()).into(holder.imageView);

        //buyerdetail
        showbuyerInfo(product, holder.buyerName);
        holder.address.setText(product.getShippingAddress());
        //status
        if (product.isOrdercanceled()){
            holder.status.setText("Canceled");
            holder.status.setTextColor(Color.parseColor("#D73502"));
            holder.orderMenu.setVisibility(View.GONE);
        }else {
            holder.status.setText(product.getStatus());
        }
        //product name
        if (product.getQuantity()>1){
            holder.title.setText(product.getProductName()+ " x("+product.getQuantity()+")");
        }else {
            holder.title.setText(product.getProductName());
        }
        //payment
        if (product.getPayment().equals("COD")){
            holder.payment.setText("(Cash on delivery)");
        }else {
            holder.payment.setText("("+product.getPayment()+")");
        }

        //order delete menu section
        holder.orderMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(context, holder.orderMenu);
                popupMenu.inflate(R.menu.menu_employee_orders);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.cancelOrder:
                                showAlertdialongConfirmDelete(product, holder.layOut);
                                return true;
                            case R.id.deliverOrder:
                                showAlertdialongConfirm(product, holder.layOut);
                                return true;
                        }
                        return false;
                    }
                });

                popupMenu.show();
            }
        });

    }

    private void showAlertdialongConfirm(Order product, final LinearLayout layOut) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference ordersRef = db.collection("Orders");
        ordersRef.document(product.getOrderId()).update("status", "Delivered").addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(context, "Delivered", Toast.LENGTH_SHORT).show();
                layOut.setVisibility(View.GONE);
            }
        });
    }

    private void showbuyerInfo(Order product, final TextView buyerName) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference userref = db.collection("Users");
        userref.document(product.getOrderBy()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);
                buyerName.setText(user.getFullName() + " ("+user.getPhone()+")");
            }
        });
    }


    private void showAlertdialongConfirmDelete(Order product, final LinearLayout layOut) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference ordersRef = db.collection("Orders");
        if (!product.getStatus().equals("Delivered")){
            ordersRef.document(product.getOrderId()).update("ordercanceled", true, "status", "Delivered").addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(context, "Order canceled!", Toast.LENGTH_SHORT).show();
                    layOut.setVisibility(View.GONE);
                    if (context instanceof MyorderActivity) {
                        ((MyorderActivity)context).loadOrderDetails();
                    }

                }
            });
        }else {
            Toast.makeText(context, "This item is already delivered, It can't be canceled", Toast.LENGTH_LONG).show();
        }



    }


    @Override
    public int getItemCount() {
        return productsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView, orderMenu;
        TextView title, price, oderOn, status,orderId, payment, buyerName, address;
        LinearLayout layOut;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            title = itemView.findViewById(R.id.title);
            price = itemView.findViewById(R.id.price);
            oderOn = itemView.findViewById(R.id.oderOn);
            status = itemView.findViewById(R.id.status);
            orderId = itemView.findViewById(R.id.orderId);
            payment = itemView.findViewById(R.id.payment);
            buyerName = itemView.findViewById(R.id.buyerName);
            address = itemView.findViewById(R.id.address);
            layOut = itemView.findViewById(R.id.layOut);
            orderMenu = itemView.findViewById(R.id.orderMenu);
        }
    }
}

