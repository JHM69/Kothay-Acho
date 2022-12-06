package com.selim_jnu.kothayacho;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;


public class SignInActivity extends AppCompatActivity {
    ProgressDialog mDialog;
    boolean found=false;
    User user=new User();
    EditText nameEt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sign_up);
        EditText numberEt = findViewById(R.id.number);
        Button next = findViewById(R.id.button);
        nameEt = findViewById(R.id.name);

        mDialog = new ProgressDialog(this);
        mDialog.setMessage("Please wait..");
        mDialog.setIndeterminate(true);
        mDialog.setCancelable(false);
        mDialog.setCanceledOnTouchOutside(false);


        next.setOnClickListener(view -> {
            try {
                String number;
                number = numberEt.getText().toString();
                String name = nameEt.getText().toString().trim();
                if (number.length() == 11) {
                    if(name.isEmpty()){
                        nameEt.setError("Name is error...");
                        nameEt.requestFocus();
                        return;
                    }

                    mDialog.show();


                    user.setName(name);
                    found = true;
                    user.setNumber(number);

                    user.setId(number);
                    user.setLastSeen(System.currentTimeMillis());
                    FirebaseDatabase.getInstance().getReference().child("Users").child(number).setValue(user);
                    SharedPrefs.save(getApplicationContext(), "number", number);
                    SharedPrefs.save(getApplicationContext(), "name", user.getName());
                    Toast.makeText(SignInActivity.this, "Successfully Signed In", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                    intent.putExtra("number", number);
                    intent.putExtra("init", true);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    mDialog.dismiss();
                    startActivity(intent);
                } else {
                    Toast.makeText(SignInActivity.this, "Error mail or Number", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception k) {
                Toast.makeText(SignInActivity.this, "Error mail", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void how(View v) {
        Intent intent = new Intent(getApplicationContext(), HowToUseActivity.class);
        startActivity(intent);
    }

}