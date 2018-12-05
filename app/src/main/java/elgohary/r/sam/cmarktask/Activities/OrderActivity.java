package elgohary.r.sam.cmarktask.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import elgohary.r.sam.cmarktask.R;


public class OrderActivity extends AppCompatActivity {

    ImageView mOrderImg;
    TextView mOrderTitle, mOrderDicription;

    Button Delete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        Bundle bundle = getIntent().getExtras();

        final String orderTitle = bundle.getString("orderTitle");
        String orderDescription = bundle.getString("orderDescription");
        String orderImg = bundle.getString("orderImg");

        mOrderImg = findViewById(R.id.order_img);
        mOrderTitle = findViewById(R.id.order_title);
        mOrderDicription = findViewById(R.id.OrderDescribtion);

        mOrderTitle.setText(orderTitle);
        mOrderDicription.setText(orderDescription);

        Picasso.get().load(orderImg).into(mOrderImg);

        Delete = findViewById(R.id.btn_delete);
        Delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                Query applesQuery = ref.child("Orders").orderByChild("orderTitle").equalTo(orderTitle);

                applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot appleSnapshot: dataSnapshot.getChildren()) {
                            appleSnapshot.getRef().removeValue();
                            Intent intent = new Intent(OrderActivity.this,HomeActivity.class);
                            startActivity(intent);
                            Toast.makeText(OrderActivity.this, "تم حذف المنتج", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }
        });

    }
}
