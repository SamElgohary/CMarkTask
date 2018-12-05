package elgohary.r.sam.cmarktask.Activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import elgohary.r.sam.cmarktask.Model.Order;
import elgohary.r.sam.cmarktask.R;




public class AddOrderActivity extends AppCompatActivity {

    ImageView mOrderImg;

    EditText mOrderTitle ,mOrderDiscription;

    Button mUpload;

    FirebaseAuth mAuth;


    private Uri resultUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_order);

        mAuth = FirebaseAuth.getInstance();


        mOrderImg = findViewById(R.id.order_img);
        mOrderTitle = findViewById(R.id.order_title);
        mOrderDiscription = findViewById(R.id.orderDescribtion);
        mUpload = findViewById(R.id.btn_upload);

        mOrderImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
            }
        });

        mUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                uploadOrder();
            }
        });
    }

    public void uploadOrder(){


        int random = (int)(Math.random() * 50 + 1);

        final Order order = new Order();
        order.setName(mAuth.getCurrentUser().getDisplayName());
        order.setEmail(mAuth.getCurrentUser().getEmail());
        order.setId(String.valueOf(random));
        order.setOrderTitle(mOrderTitle.getText().toString());
        order.setOrderDescription(mOrderDiscription.getText().toString());

        Log.i("resultUri", String.valueOf(resultUri));

        if(resultUri != null) {

            StorageReference filePath = FirebaseStorage.getInstance().getReference().child("orders_images").child(mAuth.getCurrentUser().getUid());
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), resultUri);
            } catch (IOException e) {
                e.printStackTrace();
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
            byte[] data = baos.toByteArray();
            UploadTask uploadTask = filePath.putBytes(data);

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    finish();
                    return;
                }
            });
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();

                    order.setOrderImg(downloadUrl.toString());

                    DatabaseReference current_user_db = FirebaseDatabase.getInstance().getReference().child("Orders");
                    current_user_db.push().setValue(order);

                    finish();
                    return;
                }
            });
        }else{
            finish();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == Activity.RESULT_OK){
            final Uri imageUri = data.getData();
            resultUri = imageUri;
            mOrderImg.setImageURI(resultUri);
        }
    }
}
