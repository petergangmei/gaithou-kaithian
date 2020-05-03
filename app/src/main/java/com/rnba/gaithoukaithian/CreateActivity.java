package com.rnba.gaithoukaithian;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rnba.gaithoukaithian.model.Product;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.UUID;

public class CreateActivity extends AppCompatActivity {

    FirebaseFirestore db;
    private EditText title_field, overview_field, Mprice_field, Dprice_field, stocks, sallerLocation, availableIn, deliveryFee;
    private CheckBox checkBox;
    private Button BtnContinue;
    private ImageView imageView;
    private Button uploadImgBtn;
    static int sendid =1;
    private static  final  int ImageBack =1;
    private StorageReference Folder;
    private Uri imageData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);
        Folder = FirebaseStorage.getInstance().getReference().child("ImageFolder");
        db = FirebaseFirestore.getInstance();

        title_field = findViewById(R.id.title_field);
        overview_field = findViewById(R.id.overview_field);
        Mprice_field = findViewById(R.id.Mprice_field);
        Dprice_field = findViewById(R.id.Dprice_field);
        stocks = findViewById(R.id.stockCount);
        sallerLocation = findViewById(R.id.SallerLocation);
        availableIn = findViewById(R.id.availableIn);
        checkBox = findViewById(R.id.cod);

        deliveryFee = findViewById(R.id.deliveryFee);
        BtnContinue = findViewById(R.id.BtnContinue);
        imageView = findViewById(R.id.imageView);



        BtnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = title_field.getText().toString();
                String des = overview_field.getText().toString();
                String sallerLocationV = sallerLocation.getText().toString();
                String  availableInV = availableIn.getText().toString();
                int mpriceV = Integer.parseInt(Mprice_field.getText().toString());
                int dpriceV = Integer.parseInt(Dprice_field.getText().toString());
                int stocksV = Integer.parseInt(stocks.getText().toString());

                int deliveryFeeV = Integer.parseInt(deliveryFee.getText().toString());
                boolean codV;
                if (checkBox.isChecked()){
                     codV = true;
                }else {
                     codV = false;
                }

                Calendar calForTime = Calendar.getInstance();
                SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh:mm a");
                final String currentTime =currentTimeFormat.format(calForTime.getTime());

                Calendar calForDate = Calendar.getInstance();
                SimpleDateFormat currentDateFormat = new SimpleDateFormat("dd-mm-yyyy");

                String currentDate = currentDateFormat.format(calForDate.getTime());

                UpdateData(title, des, mpriceV, dpriceV,deliveryFeeV, stocksV, sallerLocationV, availableInV, codV, System.currentTimeMillis(),currentTime, currentDate);
            }
        });


        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, ImageBack);
            }
        });


    }

    private void UpdateData(final String title, final String des, final int mpriceV, final int dpriceV,final int deliveryFeeV, final int stocksV, final String sallerLocationV, final String  availableInV, final boolean codV, final long timestamp, final String currentTime, final String currentDate) {
        final String id = UUID.randomUUID().toString();

        if(imageData !=null){

            final String mmName = System.currentTimeMillis()+".jpg";
            final StorageReference imageref = Folder.child(mmName);
            final ProgressDialog progressDialog = new ProgressDialog(CreateActivity.this);
            progressDialog.setMessage("Adding product..");
            progressDialog.show();
            progressDialog.setCancelable(false);

            imageref.putFile(imageData).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    imageref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            final String durl = uri.toString();
                            final Product product = new Product(id, title, des, durl, mpriceV,dpriceV,deliveryFeeV, stocksV, sallerLocationV, availableInV, codV, timestamp,currentTime, currentDate );
                            db.collection("Products").document(id).set(product)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(CreateActivity.this, "created!", Toast.LENGTH_SHORT).show();
                                            progressDialog.dismiss();
                                            finish();
                                        }
                                    });
                        }
                    });


                }
            });


        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ImageBack){
            if (resultCode == RESULT_OK){

                imageData = data.getData();
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),imageData);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                imageView.setImageBitmap(bitmap);
            }
        }
    }

}
