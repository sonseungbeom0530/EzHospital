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
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class AdminRegisterActivity extends AppCompatActivity  {


    private EditText etPhone,etPass,etConPass,etShopName,etEmail,etAddress,etCountry,etState,etCity;
    private Button btnReg;

    private FirebaseAuth firebaseAuth;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_register);

        etPhone=findViewById(R.id.etPhone);
        etPass=findViewById(R.id.etPass);
        etConPass=findViewById(R.id.etConPass);
        etEmail=findViewById(R.id.etEmail);
        etShopName=findViewById(R.id.etShopName);
        etCity=findViewById(R.id.etCity);
        etAddress=findViewById(R.id.etAddress);
        etCountry=findViewById(R.id.etCountry);
        etState=findViewById(R.id.etState);

        btnReg=findViewById(R.id.btnReg);

        firebaseAuth=FirebaseAuth.getInstance();
        pd=new ProgressDialog(this);
        pd.setTitle("Please wait");
        pd.setCanceledOnTouchOutside(false);

        btnReg.setOnClickListener(new View.OnClickListener(){

            @Override

            public void onClick(View v){
                //register user
                inputData();
            }

        });

    }

    private String shopName,phone,country,state,city,address,email,password,confirmPassword;

    private  void inputData(){
        //input data
        shopName=etShopName.getText().toString().trim();
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
        if (TextUtils.isEmpty(shopName)){
            Toast.makeText(this,"Enter name",Toast.LENGTH_SHORT).show();
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

        if (TextUtils.isEmpty(phone)){
            Toast.makeText(this,"Enter phone number",Toast.LENGTH_SHORT).show();
            return;
        }

        createAccount();
    }

    private void createAccount() {
        pd.setMessage("Creating Account");
        pd.show();

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
                        pd.dismiss();
                        Toast.makeText(AdminRegisterActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                    }

                });

    }

    private void saverFirebaseData() {
        pd.setMessage("Saving Account Info...");
        String timestamp=""+System.currentTimeMillis();

        //setup data to save
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("uid",""+firebaseAuth.getUid());
        hashMap.put("email",""+email);
        hashMap.put("name",""+shopName);
        hashMap.put("password",""+password);
        hashMap.put("phone",""+phone);
        hashMap.put("timestamp",""+timestamp);
        hashMap.put("accountType","Admin");
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
                        pd.dismiss();
                        startActivity(new Intent(AdminRegisterActivity.this,LoginActivity.class));
                        finish();
                    }

                })

                .addOnFailureListener(new OnFailureListener() {

                    @Override

                    public void onFailure(@NonNull Exception e) {
                        //failed updating db
                        pd.dismiss();
                        startActivity(new Intent(AdminRegisterActivity.this,LoginActivity.class));
                        finish();
                    }

                });

    }


}