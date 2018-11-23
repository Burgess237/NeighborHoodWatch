package com.example.daniel.neighbourhoodwatch;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.parse.ParseInstallation;
import com.parse.ParsePush;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;

import static android.view.View.GONE;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Button Viewmap,ReportIncident;
    String TAG = "MAIN ACTIVITY";
    TextView UserName,UserVehicle,UserTime;
    ImageView ProfilePic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user ==null){
            //send user to login again
            startActivity(new Intent(this, LoginActivity.class));
        }else{
            String userID = user.getUid();
            enablePush(userID);
            populateDisplay(userID);
        }

        UserName = findViewById(R.id.PatrollerName);
        UserVehicle = findViewById(R.id.PatrolCar);
        UserTime = findViewById(R.id.PatrolTime);
        ProfilePic = findViewById(R.id.patrollerPic);

        DrawerLayout drawer =  findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Viewmap = findViewById(R.id.ViewMap);
        Viewmap.setOnClickListener(view -> {
            Toast.makeText(getApplicationContext(), "Shows Map", Toast.LENGTH_LONG).show();
            startActivity(new Intent(getApplicationContext(), MapActivity.class));
        });

        ReportIncident = findViewById(R.id.ReportIncident);
        ReportIncident.setOnClickListener(view -> {
            //Report Incident Popup
        });




    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer =  findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.PanicButton) {
            //Send push notification with location to other users in the application
            //or chat message to other users with location params
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_patrols) {
            // open Patrols Window
        } else if (id == R.id.nav_schedules) {
            //open schedules
            startActivity(new Intent(this, Schedules.class));
        } else if (id == R.id.nav_chat) {
            //open chat window
            startActivity(new Intent(this, ChatActivity.class));
        } else if (id == R.id.nav_manage) {
            //Open user details changer
            startActivity(new Intent(this, AdditionalDetials.class));
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void Panic(View view){
       MapActivity map = new MapActivity();
               map.Panic();
    }

    public void enablePush(final String userId) {
        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put("GCMSenderId", "136619825454");
        installation.put("userId", userId);
        installation.saveInBackground(e -> {
            if (e == null) {
                Log.i("push", "ok");
                subscribe("all");
                subscribe(userId);
            } else {
                Log.i("push", "nok");
                e.printStackTrace();
            }
        });
    }


    public void subscribe(String channel){
        ParsePush.subscribeInBackground(channel, e -> {
            if (e == null) {
                Log.i("Push", "subscribed");
            } else {
                Log.i("push", "nok");
                e.printStackTrace();
            }
        });
    }

    public void populateDisplay(String userId){
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference storageReference = firebaseStorage.getReference().child("images/"+ userId);
        Glide.with(this /* context */)
                .load(storageReference)
                .into(ProfilePic);
        Uri uri = FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl();
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference mref = db.getReference().child("user").child(userId);
        mref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserDetails ud = dataSnapshot.getValue(UserDetails.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        });
    }

    public class UserDetails {
        String userName;
        String vehicleDetails;
        String nextPatrol;

        public UserDetails(){
        }

        public UserDetails(String DisplayName,
                           String Vehicle
                           ,String NextPatrol
        ) {
            userName = DisplayName;
            vehicleDetails = Vehicle;
            UserName.setText(getUserName());
            UserVehicle.setText(getVehicleDetails());
            nextPatrol = NextPatrol;
            if(!getNextPatrol().isEmpty()){
                UserTime.setText(getNextPatrol());
            }else{
                UserTime.setVisibility(GONE);
            }

        }

        String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

         String getVehicleDetails() {
            return vehicleDetails;
        }

        public void setVehicleDetails(String vehicleDetails) {
            this.vehicleDetails = vehicleDetails;
        }

         String getNextPatrol() {
            return nextPatrol;
        }

        public void setNextPatrol(String nextPatrol) {
            this.nextPatrol = nextPatrol;
        }
    }

    @GlideModule
    public class MyAppGlideModule extends AppGlideModule {

        @Override
        public void registerComponents(Context context, Glide glide, Registry registry) {
            // Register FirebaseImageLoader to handle StorageReference
            registry.append(StorageReference.class, InputStream.class,
                    new FirebaseImageLoader.Factory());
        }
    }
}




