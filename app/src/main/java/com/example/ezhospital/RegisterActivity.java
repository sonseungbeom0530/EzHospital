package com.example.ezhospital;

import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;

import android.content.Intent;

import android.location.LocationManager;
import android.os.Bundle;

import android.text.TextUtils;

import android.util.Patterns;

import android.view.View;

import android.widget.Button;

import android.widget.EditText;

import android.widget.ImageButton;
import android.widget.TextView;

import android.widget.Toast;


import com.google.android.gms.tasks.OnFailureListener;

import com.google.android.gms.tasks.OnSuccessListener;


import com.google.firebase.auth.AuthResult;

import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.database.DatabaseReference;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

import butterknife.BindView;


public class RegisterActivity extends AppCompatActivity {

    //views
    private EditText etPhone,etPass,etConPass,etName,etEmail,etAddress,etCountry,etState,etCity;
    private Button btnReg;
    private TextView regAdmin;
    private FirebaseAuth firebaseAuth;
    //progressbar to display while registering user
    private ProgressDialog progressDialog;

    //permissions
    private static final int LOCATION_REQUEST_CODE=100;

    private String[] locationPermissions;

    private double latitude,longitude;

    private LocationManager locationManager;

    CollectionReference userRef;

    @Override

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_register);

        //init
        etPhone=findViewById(R.id.etPhone);
        etPass=findViewById(R.id.etPass);
        etConPass=findViewById(R.id.etConPass);
        etEmail=findViewById(R.id.etEmail);
        etName=findViewById(R.id.etName);
        etAddress=findViewById(R.id.etAddress);
        etCity=findViewById(R.id.etCity);
        etAddress=findViewById(R.id.etAddress);
        etCountry=findViewById(R.id.etCountry);
        etState=findViewById(R.id.etState);

        btnReg=findViewById(R.id.btnReg);
        regAdmin=findViewById(R.id.registerAdmin);

        //In the onCreate() method, initialize the FirebaseAuth instance
        firebaseAuth = FirebaseAuth.getInstance();


        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Registering User...");

        //
        regAdmin.setOnClickListener(new View.OnClickListener(){

            @Override

            public void onClick(View v){
                //open register admin activity
                startActivity(new Intent(RegisterActivity.this,AdminRegisterActivity.class));
            }

        });

        btnReg.setOnClickListener(new View.OnClickListener(){

            @Override

            public void onClick(View v){
                //register user
                inputData();
            }

        });


    }

    private String name,email,password,confirmPassword,phone,country,state,city,address;

    private  void inputData(){
        //input data
        name=etName.getText().toString().trim();
        phone=etPhone.getText().toString().trim();
        password=etPass.getText().toString().trim();
        confirmPassword=etConPass.getText().toString().trim();
        email=etEmail.getText().toString().trim();
        country=etCountry.getText().toString().trim();
        state=etState.getText().toString().trim();
        city=etCity.getText().toString().trim();
        address=etAddress.getText().toString().trim();
        //validate data
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(this,"Invalid email pattern",Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(name)){
            Toast.makeText(this,"Enter name",Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(phone)){
            Toast.makeText(this,"Enter phone number",Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.length()<6){
            Toast.makeText(this,"password must be at least 6 characters long",Toast.LENGTH_SHORT).show();
            return;
        }
        if (!password.equals(confirmPassword)){
            Toast.makeText(this,"password doesn't match",Toast.LENGTH_SHORT).show();
            return;
        }

        createAccount();
    }

    private void createAccount() {
        progressDialog.setMessage("Creating Account");
        progressDialog.show();

        //create account
        firebaseAuth.createUserWithEmailAndPassword(email,password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {

                    @Override

                    public void onSuccess(AuthResult authResult) {
                        //account created
                        saverFirebaseData();
                    }

                })
                .addOnFailureListener(new OnFailureListener() {

                    @Override

                    public void onFailure(@NonNull Exception e) {
                        //failed creating account
                        progressDialog.dismiss();
                        Toast.makeText(RegisterActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                    }

                });

    }

    private void saverFirebaseData() {
        progressDialog.setMessage("Saving Account Info...");
        String timestamp=""+System.currentTimeMillis();

        //setup data to save
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("uid",""+firebaseAuth.getUid());
        hashMap.put("email",""+email);
        hashMap.put("name",""+name);
        hashMap.put("password",""+password);
        hashMap.put("timestamp",""+timestamp);
        hashMap.put("accountType","User");
        hashMap.put("online","true");
        hashMap.put("image","");
        hashMap.put("cover","");
        hashMap.put("city",city);
        hashMap.put("state",state);
        hashMap.put("country",country);
        hashMap.put("address",address);

        //save to db
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {

                    @Override

                    public void onSuccess(Void aVoid) {
                        //db updated

                        progressDialog.dismiss();
                        startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
                        finish();

                    }

                })

                .addOnFailureListener(new OnFailureListener() {

                    @Override

                    public void onFailure(@NonNull Exception e) {

                        //failed updating db
                        progressDialog.dismiss();
                        startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
                        finish();

                    }

                });

    }







}