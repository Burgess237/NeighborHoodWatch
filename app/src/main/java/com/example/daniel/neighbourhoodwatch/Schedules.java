package com.example.daniel.neighbourhoodwatch;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;



public class Schedules extends AppCompatActivity {

    FirebaseAuth mAuth;
    RecyclerView recycleView;
    Button Delete;
    FirebaseRecyclerAdapter<ScheduleResponse, HistoryHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedules);
        recycleView = findViewById(R.id.recycler_view);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mAuth = FirebaseAuth.getInstance();
        recycleView.setLayoutManager(new LinearLayoutManager(this));
        Delete = findViewById(R.id.btnDelete);

        getHistoryList();

    }


    private void getHistoryList() {
        FirebaseUser user =  mAuth.getCurrentUser();
        String userId = user.getUid();
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        //query sets the user schedule wit upcoming dates

        Query query = db
                .getReference("Users")
                .child(userId)
                .child("Schedule");

        FirebaseRecyclerOptions<ScheduleResponse> options = new FirebaseRecyclerOptions.Builder<ScheduleResponse>()
                .setQuery(query, ScheduleResponse.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<ScheduleResponse, HistoryHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull HistoryHolder holder, int position, @NonNull ScheduleResponse model) {
                //set data to holder from modal
                holder.setDate("" + model.getDate());
                holder.setName("" + model.getName());
                holder.setStartTime(""+ model.getStartTime());
                holder.setEndTime("" + model.getEndTime());
                Delete.setOnClickListener(view -> FirebaseDatabase.getInstance().getReference("Users")
                        .child(userId).child("Schedule").removeValue().addOnCompleteListener(task -> {
                            if(task.isSuccessful())
                                Toast.makeText(getApplicationContext(),"Scheduled Patrol Deleted",Toast.LENGTH_LONG).show();
                            else
                                Toast.makeText(getApplicationContext(),"Patrol Failed to delete",Toast.LENGTH_LONG).show();
                        }));
            }

            @NonNull
            @Override
            public HistoryHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_view, viewGroup,false);
                return new HistoryHolder(view);
            }
        };
        recycleView.setAdapter(adapter);

    }
    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (adapter != null) {
            adapter.stopListening();
        }
    }

    public void onPanic(View view){

    }

    class HistoryHolder extends RecyclerView.ViewHolder {
        private View view;

        HistoryHolder(View itemView) {
            super(itemView);
            view = itemView;
        }

        void setStartTime(String text){
            TextView textView = view.findViewById(R.id.txtStartTime);
            textView.setText(text);
        }

        void setEndTime(String text){
            TextView textView = view.findViewById(R.id.txtEndTime);
            textView.setText(text);
        }

        void setName(String text){
            TextView textView = view.findViewById(R.id.txtName);
            textView.setText(text);
        }

        void setDate(String text){
            TextView textView = view.findViewById(R.id.date);
            textView.setText(text);
        }

    }

}



