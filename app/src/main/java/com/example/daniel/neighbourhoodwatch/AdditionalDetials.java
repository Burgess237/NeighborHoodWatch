package com.example.daniel.neighbourhoodwatch;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.net.URI;


public class AdditionalDetials extends AppCompatActivity {

    Button saveDetails;
    TextView displayName,vehicle;
    ImageButton profilePic;
    Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_additional_detials);

        saveDetails = findViewById(R.id.SubmitDetails);
        saveDetails.setOnClickListener(view -> {
           if(saveDetailsToFirebase()){
               startActivity(new Intent(AdditionalDetials.this, MainActivity.class));
           }
        });

        profilePic = findViewById(R.id.imgbtnUser);
        profilePic.setOnClickListener(view -> pickImageFromGallery());

        displayName = findViewById(R.id.addUserName);
        vehicle = findViewById(R.id.addVehicle);


    }

    public boolean saveDetailsToFirebase(){
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user  = mAuth.getCurrentUser();
        UserProfileChangeRequest update;
        Boolean success = false;
        //Check there aren't errors
        if(displayName.getText().toString().isEmpty()){
            Toast.makeText(AdditionalDetials.this,"Please enter a valid display name",Toast.LENGTH_LONG).show();
            success = false;
        }
        if(vehicle.getText().toString().isEmpty()){
            Toast.makeText(AdditionalDetials.this,"Please enter a Vehicle model",Toast.LENGTH_LONG).show();
            success = false;
        }
        try {
            if (uri.toString().isEmpty()) {
                update = new UserProfileChangeRequest.Builder()
                        .setDisplayName(displayName.getText().toString())
                        .build();
                user.updateProfile(update);
                success = true;
            } else {
                update = new UserProfileChangeRequest.Builder()
                        .setDisplayName(displayName.getText().toString())
                        .setPhotoUri(uri)
                        .build();
                user.updateProfile(update);
                success = true;
            }
        }catch(Exception e){
            Toast.makeText(AdditionalDetials.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

        try{
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference mref = db.getReference().child("Users").child(user.getUid());
        mref.child("DisplayName").setValue(displayName.getText().toString());
        mref.child("Vehicle").setValue(vehicle.getText().toString());
        success=true;
        }
        catch(Exception e){
            Toast.makeText(AdditionalDetials.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }


    return success;
    }

    public void pickImageFromGallery() {
            Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(galleryIntent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent      data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1 && resultCode == RESULT_OK){
           uri = data.getData();
            profilePic.setImageURI(uri);
        }
    }
}
