package com.example.ezhospital.Fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ezhospital.Adapter.HomeSliderAdapter;
import com.example.ezhospital.Adapter.LookbookAdapter;
import com.example.ezhospital.AdminBookingActivity;
import com.example.ezhospital.AdminChatDashboardActivity;
import com.example.ezhospital.BookingActivity;
import com.example.ezhospital.ChatDashboardActivity;
import com.example.ezhospital.InformationDashboardActivity;
import com.example.ezhospital.Interface.IBannerLoadListener;
import com.example.ezhospital.Interface.ILookBookLoadListener;
import com.example.ezhospital.LoginActivity;
import com.example.ezhospital.Model.Banner;
import com.example.ezhospital.R;
import com.example.ezhospital.Service.PicassoImageLoadingService;
import com.example.ezhospital.WriteInformationActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import ss.com.bannerslider.Slider;

public class AdminHomeFragment extends Fragment implements IBannerLoadListener, ILookBookLoadListener {

    private Unbinder unbinder;
    FirebaseAuth firebaseAuth;

    AlertDialog dialog;

    @BindView(R.id.banner_slider)
    Slider banner_slider;
    @BindView(R.id.recycler_look_book)
    RecyclerView recycler_look_book;

    @OnClick(R.id.card_view_chat)
    void viewChat(){
        startActivity(new Intent(getActivity(), AdminChatDashboardActivity.class));
    }
    @OnClick(R.id.card_view_medical_information)
    void viewMedicalInformation(){ startActivity(new Intent(getActivity(), WriteInformationActivity.class)); }
    @OnClick(R.id.card_view_booking)
    void booking(){
        startActivity(new Intent(getActivity(), AdminBookingActivity.class));
    }
    @OnClick(R.id.card_logout)
    void logout(){
        startActivity(new Intent(getActivity(), LoginActivity.class));
    }



    TextView txt_user_name,txt_member_type;

    //FireStore
    CollectionReference bannerRef,lookbookRef;
    //Interface
    IBannerLoadListener iBannerLoadListener;
    ILookBookLoadListener iLookBookLoadListener;

    public AdminHomeFragment() {
        bannerRef= FirebaseFirestore.getInstance().collection("Banner");
        lookbookRef=FirebaseFirestore.getInstance().collection("Lookbook");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.frament_admin_home, container, false);
        unbinder = ButterKnife.bind(this, view);

        txt_user_name = view.findViewById(R.id.txt_user_name);
        txt_member_type = view.findViewById(R.id.txt_member_type);

        firebaseAuth = FirebaseAuth.getInstance();

        //init
        Slider.init(new PicassoImageLoadingService());

        //if (AccountKit.getCurrentAccessToken()!=null){
        //  setUserInformation();
        //}

        iBannerLoadListener = this;
        iLookBookLoadListener = this;

        checkUser();
        loadBanner();
        loadLookBook();

        return view;
    }

    private void checkUser() {
        FirebaseUser user=firebaseAuth.getCurrentUser();
        if (user==null){
            startActivity(new Intent(getActivity(), LoginActivity.class));
        }else {
            loadMyInfo();
        }
    }

    private void loadMyInfo() {
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("uid").equalTo(firebaseAuth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds:dataSnapshot.getChildren()){
                    String name=""+ds.child("name").getValue();
                    String accountType=""+ds.child("accountType").getValue();

                    txt_user_name.setText(name);
                    txt_member_type.setText(accountType);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadLookBook() {
        lookbookRef.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        List<Banner> lookbooks=new ArrayList<>();
                        if (task.isSuccessful()){
                            for (QueryDocumentSnapshot bannerSnapShot:task.getResult()){
                                Banner banner=bannerSnapShot.toObject(Banner.class);
                                lookbooks.add(banner);
                            }
                            iLookBookLoadListener.onLookbookLoadSuccess(lookbooks);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                iLookBookLoadListener.onLookbookLoadFailed(e.getMessage());
            }
        });
    }

    private void loadBanner() {
        bannerRef.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        List<Banner> banners=new ArrayList<>();
                        if (task.isSuccessful()){
                            for (QueryDocumentSnapshot bannerSnapShot:task.getResult()){
                                Banner banner=bannerSnapShot.toObject(Banner.class);
                                banners.add(banner);
                            }
                            iBannerLoadListener.onBannerLoadSuccess(banners);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                iBannerLoadListener.onBannerLoadFailed(e.getMessage());
            }
        });
    }

    @Override
    public void onBannerLoadSuccess(List<Banner> banners) {
        banner_slider.setAdapter(new HomeSliderAdapter(banners));
    }

    @Override
    public void onBannerLoadFailed(String message) {
        Toast.makeText(getActivity(),message,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLookbookLoadSuccess(List<Banner> banners) {
        recycler_look_book.setHasFixedSize(true);
        recycler_look_book.setLayoutManager(new LinearLayoutManager(getActivity()));
        recycler_look_book.setAdapter(new LookbookAdapter(getActivity(),banners));
    }

    @Override
    public void onLookbookLoadFailed(String message) {
        Toast.makeText(getActivity(),message,Toast.LENGTH_SHORT).show();

    }

}

