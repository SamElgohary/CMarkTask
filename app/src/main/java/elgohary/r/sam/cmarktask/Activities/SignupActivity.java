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
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import elgohary.r.sam.cmarktask.Model.User;
import elgohary.r.sam.cmarktask.R;


public class SignupActivity extends AppCompatActivity {

    TextView mLinkLogin;
    EditText mEmail, mPassword,mAddress,mName,mPhoneNumber;

    ImageView mProfileImage;

    Button   mRegistration;

    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener firebaseAuthListener;

    private Uri resultUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();

        // if user already is logged go to Home Activity
        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user!=null){
                    Intent intent = new Intent(SignupActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                    return;
                }
            }
        };


        mEmail =  findViewById(R.id.input_email);
        mPassword = findViewById(R.id.input_password);
        mAddress = findViewById(R.id.input_address);
        mName = findViewById(R.id.input_name);
        mPhoneNumber = findViewById(R.id.input_mobile);

        mProfileImage = findViewById(R.id.userAvatar);

        mRegistration = findViewById(R.id.btn_signup);

        // Registration method
        mRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = mEmail.getText().toString();
                final String password = mPassword.getText().toString();
                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(!task.isSuccessful()){
                            Toast.makeText(SignupActivity.this, "sign up error", Toast.LENGTH_SHORT).show();
                        }else{

                            saveUserData();
                        }
                    }
                });
            }
        });

        mProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
            }
        });

        //Intent To login Activity
        linkLogin();



    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(firebaseAuthListener);
    }
    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(firebaseAuthListener);
    }

    //Intent To login Activity
    public void linkLogin() {
        mLinkLogin = findViewById(R.id.link_login);
        mLinkLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signup = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(signup);
            }
        });
    }

    public void saveUserData(){

        final User user = new User();

        user.setId(mAuth.getCurrentUser().getUid());
        user.setUserEmail(mEmail.getText().toString());
        user.setUserName(mName.getText().toString());
        user.setUserAddress(mAddress.getText().toString());
        user.setUserPhoneNumper(mPhoneNumber.getText().toString());

        Log.i("resultUri", String.valueOf(resultUri));

        if(resultUri != null) {

            StorageReference filePath = FirebaseStorage.getInstance().getReference().child("profile_images").child(mAuth.getCurrentUser().getUid());
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

                    user.setUserImg(downloadUrl.toString());

                    Log.i("downloadUrl", String.valueOf(downloadUrl.toString()));


                    DatabaseReference current_user_db = FirebaseDatabase.getInstance().getReference().child("User").child(mAuth.getCurrentUser().getUid());
                    current_user_db.setValue(user);

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
            mProfileImage.setImageURI(resultUri);
        }
    }
}
