package com.example.daniel.neighbourhoodwatch;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseInstallation;


public class app extends Application{

    @Override
    public void onCreate(){
        super.onCreate();

        //setup Back4App
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(getString(R.string.back4app_app_id))
                // if defined
                .clientKey(getString(R.string.back4app_client_key))
                .server(getString(R.string.back4app_server_url))
                .build()
        );
        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put("GCMSenderId", getString(R.string.back4app_sender_id));
        installation.saveInBackground();
    }
}
