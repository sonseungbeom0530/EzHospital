package com.example.ezhospital;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class EditUserProfileActivity extends AppCompatActivity {


    private EditText etPhone, etName, etPass, etEmail,etAddress,etCountry,etState,etCity;
    private ImageButton backBtn;
    private Button updateBtn;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_profile);

        etPhone = findViewById(R.id.etPhone);
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        backBtn = findViewById(R.id.backBtn);
        etPass = findViewById(R.id.etPass);
        etAddress = findViewById(R.id.etAddress);
        etCountry = findViewById(R.id.etCountry);
        etState = findViewById(R.id.etState);
        etCity = findViewById(R.id.etCity);

        updateBtn = findViewById(R.id.updateBtn);


        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        firebaseAuth = FirebaseAuth.getInstance();

        checkUser();

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputData();
            }
        });


    }


    private String name,phone,country,state,city,address;

    private void inputData() {
        name = etName.getText().toString().trim();
        phone = etPhone.getText().toString().trim();
        country = etCountry.getText().toString().trim();
        state = etState.getText().toString().trim();
        city = etCity.getText().toString().trim();
        address = etAddress.getText().toString().trim();

        updateProfile();

    }

    private void updateProfile() {
        progressDialog.setMessage("updating profile");
        progressDialog.show();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("name", "" + name);
        hashMap.put("phone", "" + phone);
        hashMap.put("country", "" + country);
        hashMap.put("city", "" + city);
        hashMap.put("state", "" + state);
        hashMap.put("address", "" + address);


        //update to db
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //updated
                        progressDialog.dismiss();
                        Toast.makeText(EditUserProfileActivity.this, "Profile updated.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //failed to update
                        progressDialog.dismiss();
                        Toast.makeText(EditUserProfileActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void checkUser() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null) {
            startActivity(new Intent(EditUserProfileActivity .this, LoginActivity.class));
        } else {
            loadMyInfo();
        }
    }

    private void loadMyInfo() {
        //load user info, and set to views
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            String accountType = "" + ds.child("accountType").getValue();
                            String name = "" + ds.child("name").getValue();
                            String password = "" + ds.child("password").getValue();
                            String email = "" + ds.child("email").getValue();
                            String phone = "" + ds.child("phone").getValue();
                            String address = "" + ds.child("address").getValue();
                            String city = "" + ds.child("city").getValue();
                            String country = "" + ds.child("country").getValue();
                            String state = "" + ds.child("state").getValue();
                            String uid = "" + ds.child("uid").getValue();

                            etName.setText(name);
                            etEmail.setText(email);
                            etPass.setText(password);
                            etPhone.setText(phone);
                            etAddress.setText(address);
                            etCity.setText(city);
                            etCountry.setText(country);
                            etState.setText(state);


                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
}