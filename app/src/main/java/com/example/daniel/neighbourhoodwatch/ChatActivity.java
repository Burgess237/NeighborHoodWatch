package com.example.daniel.neighbourhoodwatch;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;


public class ChatActivity extends AppCompatActivity {

    ImageButton send;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        send = findViewById(R.id.btnSend);
        send.setOnClickListener((View view) -> {
            EditText input = findViewById(R.id.input);

            // Read the input field and push a new instance
            // of ChatMessage to the Firebase database
            try{
            FirebaseDatabase.getInstance()
                    .getReference().child("chats")
                    .push()
                    .setValue(new ChatMessage(input.getText().toString(),
                            FirebaseAuth.getInstance()
                                    .getCurrentUser()
                                    .getDisplayName())
                    );

            // Clear the input
            input.setText("");
        }catch(Exception e){
                Toast.makeText(this, "Message Failed to send, Check you have a Display name linked with your account", Toast.LENGTH_LONG).show();
            }});


        displayMessages();

    }

    public void displayMessages() {
        ListView listOfMessages = (ListView)findViewById(R.id.messages_view);
        //Suppose you want to retrieve "chats" in your Firebase DB:
        Query query = FirebaseDatabase.getInstance().getReference().child("chats");
//The error said the constructor expected FirebaseListOptions - here you create them:
        FirebaseListOptions<ChatMessage> options = new FirebaseListOptions.Builder<ChatMessage>()
                .setQuery(query, ChatMessage.class)
                .setLayout(R.layout.their_message)
                .build();
        //Finally you pass them to the constructor here:
        FirebaseListAdapter<ChatMessage> adapter = new FirebaseListAdapter<ChatMessage>(options){
            @Override
            protected void populateView(View v, ChatMessage model, int position) {
                // Get references to the views of message.xml
                TextView messageText = v.findViewById(R.id.message_body);
                TextView messageTime = v.findViewById(R.id.message_time);
                TextView messageName = v.findViewById(R.id.message_name);

                // Set their text
                messageText.setText(model.getMessageText());
                //set name from text message
                messageName.setText(model.getMessageUser());
                // Format the date before showing it
                messageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)", model.getMessageTime()));
            }
        };
        listOfMessages.setAdapter(adapter);
    }
}
