package com.selim_jnu.kothayacho;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.selim_jnu.kothayacho.adapter.OfficerAdapter;

import java.util.ArrayList;
import java.util.List;

public class AdminActivity extends AppCompatActivity {
    OfficerAdapter officerAdapter;
    List<User> userList = new ArrayList<>();
    View progressBar;
    EditText keyEt;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        officerAdapter = new OfficerAdapter(AdminActivity.this, userList);
        RecyclerView userView = findViewById(R.id.sddss);
        progressBar = findViewById(R.id.progressBar);
        keyEt = findViewById(R.id.key);
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        userView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        userView.setAdapter(officerAdapter);
        progressBar.setVisibility(View.VISIBLE);
        findViewById(R.id.all).setOnClickListener(view -> {
            String k = keyEt.getText().toString();
            userList.clear();
            db.child("Users").orderByChild("key").equalTo(k).addChildEventListener(new ChildEventListener() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    userList.add(snapshot.getValue(User.class));
                    progressBar.setVisibility(View.GONE);
                    userView.setAdapter(officerAdapter);
                    officerAdapter.notifyDataSetChanged();
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    progressBar.setVisibility(View.GONE);
                }
            });
        });



    }
}