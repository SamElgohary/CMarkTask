package elgohary.r.sam.cmarktask.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import elgohary.r.sam.cmarktask.R;


public class ProfileActiity extends AppCompatActivity {

    ImageView mUserAvatar;
    TextView mEmail, mName;

    FirebaseAuth mAuth;

    String userID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_actiity);

        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();



        mUserAvatar = findViewById(R.id.user_avatar);
        mEmail      = findViewById(R.id.email);
        mName       = findViewById(R.id.name);


        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference ref = database.getReference("User");

        try{ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                for (DataSnapshot itemSnapShot : dataSnapshot.getChildren()) {

                    Picasso.get().load(itemSnapShot.child("userImg").getValue(String.class)).into(mUserAvatar);
                    mEmail.setText(itemSnapShot.child("userEmail").getValue(String.class));
                    mName.setText(itemSnapShot.child("userName").getValue(String.class));

                    Log.i("item",itemSnapShot.child("userImg").getValue(String.class));

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });} catch (Exception e) {
            e.printStackTrace();
        }


    }
}
