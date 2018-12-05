package elgohary.r.sam.cmarktask.Activities;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Map;

import elgohary.r.sam.cmarktask.Adapter.OrderAdapter;
import elgohary.r.sam.cmarktask.Model.Order;
import elgohary.r.sam.cmarktask.R;


public class HomeActivity extends AppCompatActivity {

    Button mLogout;

    FloatingActionButton mAddOrder;

    ImageView mUserAvatar;

    FirebaseAuth mAuth;
    private String userID;

    private GridView mGridView;
    private OrderAdapter mGridAdapter;
    private ArrayList<Order> mGridData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();

        mLogout = findViewById(R.id.logout);
        mLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseAuth.getInstance().signOut();
                Toast.makeText(HomeActivity.this, "تم تسجيل الخروج بنجاح", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        });

        mAddOrder = findViewById(R.id.addOrder);
        mAddOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(HomeActivity.this, AddOrderActivity.class);
                startActivity(intent);
            }
        });

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("User");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot datas: dataSnapshot.getChildren()){

                    String img =datas.child("userImg").getValue(String.class);

                    Picasso.get().load(img).into(mUserAvatar);

                    Log.i("img",img);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        mUserAvatar = findViewById(R.id.userAvatar);
        mUserAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(HomeActivity.this,ProfileActiity.class);
                startActivity(intent);

            }
        });

        mGridView = (GridView) findViewById(R.id.ordersGridView);
//        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
//        mProgressBar.setVisibility(View.VISIBLE);

        //Initialize with empty data
        mGridData = new ArrayList<>();
        mGridAdapter = new OrderAdapter(this, R.layout.item_order, mGridData);
        mGridView.setAdapter(mGridAdapter);

        //Grid view click event

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                //Get item at position
                Order order = (Order) parent.getItemAtPosition(position);

                Intent intent = new Intent(HomeActivity.this, OrderActivity.class);
                ImageView imageView = (ImageView) v.findViewById(R.id.order_img);

                // Interesting data to pass across are the thumbnail size/location, the
                // resourceId of the source bitmap, the picture description, and the
                // orientation (to avoid returning back to an obsolete configuration if
                // the device rotates again in the meantime)

                int[] screenLocation = new int[2];
                imageView.getLocationOnScreen(screenLocation);

                //Pass the image title and url to DetailsActivity
                        intent.putExtra("orderTitle", order.getOrderTitle()).
                        putExtra("orderDescription",order.getOrderDescription()).
                        putExtra("orderImg", order.getOrderImg());

                //Start details activity
                startActivity(intent);
            }
        });

        final FirebaseDatabase ddatabase = FirebaseDatabase.getInstance();
        final DatabaseReference reff = ddatabase.getReference("Orders");

        try{reff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                for (DataSnapshot itemSnapShot : dataSnapshot.getChildren()) {

                    Order order = new Order();

                    order.setOrderTitle(itemSnapShot.child("orderTitle").getValue(String.class));
                    order.setOrderDescription(itemSnapShot.child("orderDescription").getValue(String.class));
                    order.setOrderImg(itemSnapShot.child("orderImg").getValue(String.class));

                    mGridData.add(order);
//                    mProgressBar.setVisibility(View.GONE);

                }
                mGridAdapter.setGridData(mGridData);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });} catch (Exception e) {
            e.printStackTrace();
        }



    }
}
