package com.example.ezhospital.Fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.ezhospital.EditUserProfileActivity;
import com.example.ezhospital.InformationDashboardActivity;
import com.example.ezhospital.LoginActivity;
import com.example.ezhospital.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class UserProfileFragment extends Fragment {

    private TextView tvName, tvPhone, tvPassword, tvEmail, tvAccountType, tvAddress, tvCountry, tvState, tvCity;

    private ImageButton editBtn;
    private FirebaseAuth firebaseAuth;


    public UserProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_user_profile, container, false);
        firebaseAuth = FirebaseAuth.getInstance();

        tvPhone = view.findViewById(R.id.tvPhone);
        tvName = view.findViewById(R.id.tvName);
        tvPassword = view.findViewById(R.id.tvPassword);
        tvEmail =view.findViewById(R.id.tvEmail);
        tvAccountType =view.findViewById(R.id.tvAccountType);
        tvAddress = view.findViewById(R.id.tvAddress);
        tvCountry = view.findViewById(R.id.tvCountry);
        tvCity = view.findViewById(R.id.tvCity);
        tvState =view.findViewById(R.id.tvState);


        editBtn = view.findViewById(R.id.editBtn);


        checkUser();


        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), EditUserProfileActivity.class));
            }

        });

        return view;
    }

    private void checkUser() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null) {
            startActivity(new Intent(getActivity(), LoginActivity.class));
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
                        for(DataSnapshot ds : dataSnapshot.getChildren()){
                            String accountType=""+ds.child("accountType").getValue();
                            String name=""+ds.child("name").getValue();
                            String password=""+ds.child("password").getValue();
                            String email=""+ds.child("email").getValue();
                            String phone=""+ds.child("phone").getValue();
                            String address=""+ds.child("address").getValue();
                            String city=""+ds.child("city").getValue();
                            String state=""+ds.child("state").getValue();
                            String country=""+ds.child("country").getValue();



                            tvName.setText(name);
                            tvEmail.setText(email);
                            tvPassword.setText(password);
                            tvPhone.setText(phone);
                            tvAccountType.setText(accountType);
                            tvAddress.setText(address);
                            tvCountry.setText(country);
                            tvCity.setText(city);
                            tvState.setText(state);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
}