package com.example.ezhospital.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.transition.Slide;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.ezhospital.Adapter.HomeSliderAdapter;
import com.example.ezhospital.Adapter.LookbookAdapter;
import com.example.ezhospital.BookingActivity;
import com.example.ezhospital.Interface.IBannerLoadListener;
import com.example.ezhospital.Interface.ILookBookLoadListener;
import com.example.ezhospital.MainActivity;
import com.example.ezhospital.Model.Banner;
import com.example.ezhospital.R;
import com.example.ezhospital.Service.PicassoImageLoadingService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException;
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


public class HomeFragment extends Fragment implements IBannerLoadListener, ILookBookLoadListener {

    private Unbinder unbinder;


    @BindView(R.id.banner_slider)
    Slider banner_slider;
    @BindView(R.id.recycler_look_book)
    RecyclerView recycler_look_book;

    //FireStore
    CollectionReference bannerRef,lookbookRef;
    //Interface
    IBannerLoadListener iBannerLoadListener;
    ILookBookLoadListener iLookBookLoadListener;

    @BindView(R.id.card_view_booking)
    CardView card_view_booking;
    @OnClick(R.id.card_view_booking)
    void booking(){
        startActivity(new Intent(getActivity(), BookingActivity.class));
    }

    public HomeFragment() {
        bannerRef=FirebaseFirestore.getInstance().collection("Banner");
        lookbookRef=FirebaseFirestore.getInstance().collection("Lookbook");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_home, container, false);
        unbinder=ButterKnife.bind(this,view);

        //init
        Slider.init(new PicassoImageLoadingService());
        iBannerLoadListener=this;
        iLookBookLoadListener=this;

        loadBanner();
        loadLookBook();
        return view;

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