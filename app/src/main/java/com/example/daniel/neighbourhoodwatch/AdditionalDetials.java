package com.example.daniel.neighbourhoodwatch;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AdditionalUserInfo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.net.URI;
import java.util.UUID;


public class AdditionalDetials extends AppCompatActivity {

    Button saveDetails;
    TextView displayName,vehicle;
    ImageButton profilePic;
    private Uri filePath;

    //Firebase
    FirebaseStorage storage;
    StorageReference storageReference;

    private final int PICK_IMAGE_REQUEST = 71;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_additional_detials);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        saveDetails = findViewById(R.id.SubmitDetails);
        saveDetails.setOnClickListener(view -> {
           if(saveDetailsToFirebase()){
               startActivity(new Intent(AdditionalDetials.this, MainActivity.class));
           }
        });

        profilePic = findViewById(R.id.AddUserProPic);
        profilePic.setOnClickListener(view -> chooseImage());

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

        try{
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference mref = db.getReference().child("Users").child(user.getUid());
        mref.child("DisplayName").setValue(displayName.getText().toString());
        mref.child("Vehicle").setValue(vehicle.getText().toString());
        uploadImage();
        success=true;
        }
        catch(Exception e){
            Toast.makeText(AdditionalDetials.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }


    return success;
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                profilePic.setImageBitmap(bitmap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    private void uploadImage() {

        if(filePath != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference ref = storageReference.child("images/"+ UUID.randomUUID().toString());
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(AdditionalDetials.this, "Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(AdditionalDetials.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    });
        }
    }


}
