package com.example.ezhospital.Fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AlertDialogLayout;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.text.format.DateUtils;
import android.transition.Slide;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ezhospital.Adapter.HomeSliderAdapter;
import com.example.ezhospital.Adapter.LookbookAdapter;
import com.example.ezhospital.BookingActivity;
import com.example.ezhospital.Common.Common;
import com.example.ezhospital.Interface.IBannerLoadListener;
import com.example.ezhospital.Interface.IBookingInfoLoadListener;
import com.example.ezhospital.Interface.IBookingInformationChangeListener;
import com.example.ezhospital.Interface.ILookBookLoadListener;
import com.example.ezhospital.LoginActivity;
import com.example.ezhospital.MainActivity;
import com.example.ezhospital.Model.Banner;
import com.example.ezhospital.Model.BookingInformation;
import com.example.ezhospital.R;
import com.example.ezhospital.Service.PicassoImageLoadingService;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;
import ss.com.bannerslider.Slider;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.DISPLAY_SERVICE;


public class HomeFragment extends Fragment implements IBannerLoadListener, ILookBookLoadListener, IBookingInfoLoadListener, IBookingInformationChangeListener {


    private Unbinder unbinder;
    FirebaseAuth firebaseAuth;

    AlertDialog dialog;

    @BindView(R.id.banner_slider)
    Slider banner_slider;
    @BindView(R.id.recycler_look_book)
    RecyclerView recycler_look_book;

    TextView txt_user_name,txt_member_type;

    @BindView(R.id.card_booking_info)
    CardView card_booking_info;
    @BindView(R.id.txt_salon_address)
    TextView txt_salon_address;
    @BindView(R.id.txt_salon_barber)
    TextView txt_salon_barber;
    @BindView(R.id.txt_time)
    TextView txt_time;
    @BindView(R.id.txt_time_remain)
    TextView txt_time_remain;

    @OnClick(R.id.btn_delete_booking)
    void deleteBooking(){
        deleteBookingFromBarber(false);
    }
    @OnClick(R.id.btn_change_booking)
    void changeBooking(){
        changeBookingFromUser();
    }

    private void changeBookingFromUser() {
        AlertDialog.Builder confirmDialog=new AlertDialog.Builder(getActivity())
                .setCancelable(false)
                .setTitle("Hey!")
                .setMessage("Do you really want to change booking information? \n Because we will delete your old booking information \n Just confirm")
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteBookingFromBarber(true);
                    }
                });
        confirmDialog.show();
    }

    private void deleteBookingFromBarber(boolean isChange) {
        if (Common.currentBooking!=null){

            dialog.show();

            DocumentReference barberBookingInfo=FirebaseFirestore.getInstance()
                    .collection("AllSalon")
                    .document(Common.currentBooking.getCityBook())
                    .collection("Branch")
                    .document(Common.currentBooking.getSalonId())
                    .collection("Barber")
                    .document(Common.currentBooking.getBarberId())
                    .collection(Common.convertTimeStampToStringKey(Common.currentBooking.getTimestamp()))
                    .document(Common.currentBooking.getSlot().toString());

            barberBookingInfo.delete().addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            }).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    deleteBookingFromUser(isChange);
                }
            });
        }else {
            Toast.makeText(getContext(),"Current Booking must not be null",Toast.LENGTH_SHORT).show();
        }

    }

    private void deleteBookingFromUser(boolean isChange) {
        if (!TextUtils.isEmpty(Common.currentBookingId))
        {
            DocumentReference userBookingInfo=FirebaseFirestore.getInstance()
                    .collection("Booking")
                    .document(Common.currentBookingId);
            userBookingInfo.delete().addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                    Paper.init(getActivity());
                    Uri eventUri=Uri.parse(Paper.book().read(Common.EVENT_URI_CACHE.toString()));
                    getActivity().getContentResolver().delete(eventUri,null,null);

                    Toast.makeText(getActivity(),"Success delete booking!",Toast.LENGTH_SHORT).show();

                    loadUserBooking();

                    if (isChange)
                        iBookingInformationChangeListener.onBookingInformationChange();

                    dialog.dismiss();
                }
            });

        }else {
            dialog.dismiss();
            Toast.makeText(getContext(),"Booking information must not be empty",Toast.LENGTH_SHORT).show();
        }
    }

    //FireStore
    CollectionReference bannerRef,lookbookRef;
    //Interface
    IBannerLoadListener iBannerLoadListener;
    ILookBookLoadListener iLookBookLoadListener;
    IBookingInfoLoadListener iBookingInfoLoadListener;
    IBookingInformationChangeListener iBookingInformationChangeListener;



    @OnClick(R.id.card_view_booking)
    void booking(){
        startActivity(new Intent(getActivity(), BookingActivity.class));
    }


    public HomeFragment() {
        bannerRef=FirebaseFirestore.getInstance().collection("Banner");
        lookbookRef=FirebaseFirestore.getInstance().collection("Lookbook");

    }

    @Override
    public void onResume() {
        super.onResume();
        loadUserBooking();
    }

    private void loadUserBooking() {
        /*CollectionReference userBooking=FirebaseFirestore.getInstance()
                .collection("User")
                .document(Common.currentUser.getPhoneNumber())
                .collection("Booking");*/

        final CollectionReference userBooking=FirebaseFirestore.getInstance()
                .collection("Booking");

        Calendar calendar=Calendar.getInstance();
        calendar.add(Calendar.DATE,0);
        calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.MINUTE,0);

        Timestamp toDayTimeStamp=new Timestamp(calendar.getTime());

        userBooking.whereGreaterThanOrEqualTo("timestamp",toDayTimeStamp)
                .whereEqualTo("done",false)
                .limit(1)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful())
                        {
                            if (!task.getResult().isEmpty()){
                                for (QueryDocumentSnapshot queryDocumentSnapshot:task.getResult())
                                {
                                    BookingInformation bookingInformation=queryDocumentSnapshot.toObject(BookingInformation.class);
                                    iBookingInfoLoadListener.onBookingInfoLoadSuccess(bookingInformation,queryDocumentSnapshot.getId());
                                    break;
                                }
                            }else {
                                iBookingInfoLoadListener.onBookingInfoLoadEmpty();
                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                iBookingInfoLoadListener.onBookingInfoLoadFailed(e.getMessage());
            }
        });

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dialog=new SpotsDialog.Builder().setContext(getContext()).setCancelable(false).build();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_home, container, false);
        unbinder=ButterKnife.bind(this,view);

        txt_user_name=view.findViewById(R.id.txt_user_name);
        txt_member_type=view.findViewById(R.id.txt_member_type);

        firebaseAuth=FirebaseAuth.getInstance();

        //init
        Slider.init(new PicassoImageLoadingService());

        //if (AccountKit.getCurrentAccessToken()!=null){
          //  setUserInformation();
        //}

        iBannerLoadListener=this;
        iLookBookLoadListener=this;
        iBookingInfoLoadListener=this;
        iBookingInformationChangeListener=this;


        checkUser();
        loadBanner();
        loadLookBook();
        loadUserBooking();
        return view;

    }

    private void setUserInformation() {
        txt_user_name.setText(Common.currentUser.getName());
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


    @Override
    public void onBookingInfoLoadEmpty() {
        card_booking_info.setVisibility(View.GONE);
    }

    @Override
    public void onBookingInfoLoadSuccess(BookingInformation bookingInformation,String bookingId) {

        Common.currentBooking=bookingInformation;
        Common.currentBookingId=bookingId;

        txt_salon_address.setText(bookingInformation.getSalonAddress());
        txt_salon_barber.setText(bookingInformation.getBarberName());
        txt_time.setText(bookingInformation.getTime());
        String dateRemain = DateUtils.getRelativeTimeSpanString(
                Long.valueOf(bookingInformation.getTimestamp().toDate().getTime()),
                Calendar.getInstance().getTimeInMillis(),0).toString();

        txt_time_remain.setText(dateRemain);


        card_booking_info.setVisibility(View.VISIBLE);

        dialog.dismiss();
    }

    @Override
    public void onBookingInfoLoadFailed(String message) {
        Toast.makeText(getContext(),message,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBookingInformationChange() {
        startActivity(new Intent(getActivity(),BookingActivity.class));
    }
}