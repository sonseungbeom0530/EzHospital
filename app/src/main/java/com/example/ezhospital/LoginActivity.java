package com.example.ezhospital;

import androidx.annotation.NonNull;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import android.app.AlertDialog;
import android.app.ProgressDialog;

import android.content.DialogInterface;
import android.content.Intent;

import android.os.Bundle;

import android.text.InputType;
import android.text.TextUtils;


import android.util.Patterns;

import android.view.View;

import android.widget.Button;

import android.widget.EditText;

import android.widget.LinearLayout;
import android.widget.TextView;

import android.widget.Toast;


import com.example.ezhospital.Common.Common;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;

import com.google.android.gms.tasks.OnSuccessListener;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;

import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;

import com.google.firebase.database.DatabaseError;

import com.google.firebase.database.DatabaseReference;

import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;


import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {

    private static final int APP_REQUEST_CODE=7117;

    //views
    EditText etEmail,etPassword;
    Button btnLogin;
    TextView tvReg,tvForgotPassword;
    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;

    private List<AuthUI.IdpConfig> providers;
    private FirebaseAuth firebaseAuths;
    private FirebaseAuth.AuthStateListener authStateListener;


    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        //init views
        etEmail=findViewById(R.id.etEmail);
        etPassword=findViewById(R.id.etPass);
        btnLogin=findViewById(R.id.btnLogin);
        tvReg=findViewById(R.id.tvReg);
        tvForgotPassword=findViewById(R.id.forgotPassword);

        firebaseAuth=FirebaseAuth.getInstance();

        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);


        /*providers= Arrays.asList(new AuthUI.IdpConfig.PhoneBuilder().build());
        firebaseAuths=FirebaseAuth.getInstance();
        authStateListener=firebaseAuth1 -> {
            FirebaseUser user=firebaseAuth1.getCurrentUser();
            if (user!=null){
                checkUserFromFirebase(user);
            }
        };*/

       // AccessToken accessToken=AccountKit.getCurrentAcessToken();
        //if (accessToken!=null){
          //  Intent intent=new Intent(this,MainActivity.class);
            //intent.putExtra(Common.IS_LOGIN,true);
           // startActivity(intent);
        //    finish();
        //}else {
        //    setContentView(R.layout.activity_login);
        //    ButterKnife.bind(LoginActivity.this);
       // }

        //handle register button click
        btnLogin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                loginUser();
            }
        });

        tvReg.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(i);
                finish();
            }

        });
        tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRecoverPasswordDialog();
            }
        });

    }

    private void showRecoverPasswordDialog() {
        //AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Recover Password");

        //set layout linear layout
        LinearLayout linearLayout = new LinearLayout(this);
        //views to set in dialog
        final EditText emailEt = new EditText(this);
        emailEt.setHint("Email");
        emailEt.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        emailEt.setMinEms(16);

        linearLayout.addView(emailEt);
        linearLayout.setPadding(10,10,10,10);

        builder.setView(linearLayout);

        //buttons recover
        builder.setPositiveButton("Recover", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //input email
                String email=emailEt.getText().toString().trim();
                beginRecovery(email);
            }
        });
        //buttons cancel
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //dismiss dialog
                dialog.dismiss();
            }
        });

        //show dialog
        builder.create().show();
    }

    private void beginRecovery(String email) {
        //show progress dialog
        progressDialog.setMessage("Sending email...");
        progressDialog.show();

        firebaseAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressDialog.dismiss();
                        if(task.isSuccessful()){
                            Toast.makeText(LoginActivity.this,"Email sent",Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(LoginActivity.this, "Failed...",Toast.LENGTH_SHORT).show();
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                //get and show proper error message
                Toast.makeText(LoginActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }


    private String email,password;

    private void loginUser() {

        email=etEmail.getText().toString().trim();
        password=etPassword.getText().toString().trim();

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(this,"Invalid email pattern...",Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password)){
            Toast.makeText(this,"Enter password...",Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.setMessage("Logging In...");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(email,password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {

                    @Override

                    public void onSuccess(AuthResult authResult) {
                        //logged in successfully
                        makeMeOnline();

                    }

                })
                .addOnFailureListener(new OnFailureListener() {

                    @Override

                    public void onFailure(@NonNull Exception e) {
                        //failed Logging in
                        progressDialog.dismiss();
                        Toast.makeText(LoginActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                    }

                });

    }



    private void makeMeOnline() {
        //after logging in, make user online
        progressDialog.setMessage("Checking user...");
        HashMap<String,Object>hashMap = new HashMap<>();
        hashMap.put("online","true");

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //update successfully
                        checkUserType();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //failed updating
                        progressDialog.dismiss();
                        Toast.makeText(LoginActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();

                    }
                });
    }

    private void checkUserType() {
        //if user is admin,start admin main screen
        //if user is customer,start user main screen
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot ds : dataSnapshot.getChildren()){
                            String accountType=""+ds.child("accountType").getValue();
                            if(accountType.equals("Admin")){
                                progressDialog.dismiss();
                                startActivity(new Intent(LoginActivity.this,AdminMainActivity.class));
                            }else {
                                progressDialog.dismiss();
                                startActivity(new Intent(LoginActivity.this,MainActivity.class));
                                //authentication();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void authentication() {
        startActivityForResult(AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers).build(),APP_REQUEST_CODE);

        //final Intent intent=new Intent(this,AccountKitActivity.class);
        //AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder=
         //       new AccountKitConfiguration.AccountKitConfigurationBuilder(LoginType.PHONE,
         //               AccountKitActivity.ResponseType.TOKEN);
        //intent.putExtra(AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION,
        //        configurationBuilder.build);
        //startActivityForResult(intent,APP_REQUEST_CODE);

    }


    /*@Override
    public void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }*/

    /*@Override
    public void onStop() {
        if (authStateListener!=null)
            firebaseAuth.removeAuthStateListener(authStateListener);
        super.onStop();
    }*/

    private void checkUserFromFirebase(FirebaseUser user) {
        FirebaseInstanceId.getInstance()
                .getInstanceId()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        //Common.updateToken(getBaseContext(),task.getResult().getToken());
                        Intent intent=new Intent(LoginActivity.this,MainActivity.class);
                        intent.putExtra(Common.IS_LOGIN,true);
                        startActivity(intent);
                        finish();
                    }

                }).addOnFailureListener(e -> {
                   Toast.makeText(LoginActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                   Intent intent=new Intent(LoginActivity.this,MainActivity.class);
                   intent.putExtra(Common.IS_LOGIN,true);
                   startActivity(intent);
                   finish();
                });
    }

    /*@Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==APP_REQUEST_CODE){
            IdpResponse response= IdpResponse.fromResultIntent(data);

            if (resultCode==RESULT_OK){
                FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
            }else {
                Toast.makeText(LoginActivity.this,"Login cancelled",Toast.LENGTH_SHORT).show();
            }

        }
    }*/

}