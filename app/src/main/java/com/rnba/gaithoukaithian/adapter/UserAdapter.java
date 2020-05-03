package com.rnba.gaithoukaithian.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rnba.gaithoukaithian.R;
import com.rnba.gaithoukaithian.model.User;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder>{

    Context context;
    List<User> userList;

    public UserAdapter(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new UserAdapter.ViewHolder(LayoutInflater.from(context).inflate(R.layout.user_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final User user = userList.get(position);
        holder.name.setText(user.getFullName());
        holder.email.setText(user.getEmail());
        holder.phone.setText(user.getPhone());
        holder.address.setText(user.getAddress());
        holder.signupOn.setText(user.getTimeNdate());
        holder.role.setText("role:"+user.getRole());
        holder.role.setTextColor(Color.GREEN);

        holder.moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(context, holder.moreBtn);
                popupMenu.inflate(R.menu.menu_admin_user);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        CollectionReference userRef = db.collection("Users");
                        switch (item.getItemId()){
                            case R.id.promote:
                                 userRef.document(user.getId()).update("role", "employee").addOnSuccessListener(new OnSuccessListener<Void>() {
                                     @Override
                                     public void onSuccess(Void aVoid) {
                                         Toast.makeText(context, "Pormoted", Toast.LENGTH_SHORT).show();
                                     }
                                 });
                                return true;
                            case R.id.depromote:
                                userRef.document(user.getId()).update("role", "customer").addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(context, "dePromoted", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                return true;
                            case R.id.banaccount:
//                                userRef.document(user.getId()).update("role", "customer").addOnSuccessListener(new OnSuccessListener<Void>() {
//                                    @Override
//                                    public void onSuccess(Void aVoid) {
//                                        Toast.makeText(context, "Pormoted", Toast.LENGTH_SHORT).show();
//                                    }
//                                });
                                return true;
                        }
                        return false;
                    }
                });

                popupMenu.show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView moreBtn;
        TextView signupOn,email,phone,name,address,role;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            moreBtn = itemView.findViewById(R.id.moreBtn);
            email = itemView.findViewById(R.id.email);
            phone = itemView.findViewById(R.id.phone);
            name = itemView.findViewById(R.id.name);
            signupOn = itemView.findViewById(R.id.signupOn);
            address = itemView.findViewById(R.id.address);
            role = itemView.findViewById(R.id.role);
        }
    }
}
