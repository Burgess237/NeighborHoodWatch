package com.example.daniel.neighbourhoodwatch;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class RegisterActivity extends AppCompatActivity {

        private EditText inputEmail, inputPassword;
        private ProgressBar progressBar;
        private FirebaseAuth auth;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_register);

            //Get Firebase auth instance
            auth = FirebaseAuth.getInstance();

            Button btnSignIn = findViewById(R.id.sign_in_button);

            inputEmail =  findViewById(R.id.RegEmail);
            inputPassword =  findViewById(R.id.RegPassword);
            progressBar =  findViewById(R.id.progressBar);
            Button btnResetPassword = findViewById(R.id.btn_reset_password);

           // btnResetPassword.setOnClickListener(v -> startActivity(new Intent(RegisterActivity.this, ResetPasswordActivity.class)));



            btnSignIn.setOnClickListener(v -> {
                startActivity (new Intent(RegisterActivity.this, MainActivity.class));
                finish();
            });

            Button btnSignUp = findViewById(R.id.sign_up_button);
            btnSignUp.setOnClickListener(v -> {

                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() < 6) {
                    Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
                    return;
                }

                //create user
                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(RegisterActivity.this, task -> {
                            Toast.makeText(RegisterActivity.this, "createUserWithEmail:onComplete:" + task.isSuccessful(), Toast.LENGTH_LONG).show();
                            // If sign in fails, display a message to the user. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.

                            if (!task.isSuccessful()) {
                                Toast.makeText(RegisterActivity.this, "Authentication failed." + task.getException(),
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(RegisterActivity.this, Task -> {
                                    if(Task.isSuccessful()){
                                        FirebaseUser user = auth.getCurrentUser();
                                        startActivity(new Intent(RegisterActivity.this, AdditionalDetials.class));
                                        finish();
                                        Toast.makeText(RegisterActivity.this, "Signed In as :" + user.getEmail(),
                                                Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
            });
        }

        @Override
        protected void onResume() {
            super.onResume();
            progressBar.setVisibility(View.GONE);
        }



    }