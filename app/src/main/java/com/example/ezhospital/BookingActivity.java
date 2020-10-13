package com.example.ezhospital;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;

import com.example.ezhospital.Adapter.MyViewPagerAdapter;
import com.example.ezhospital.Common.Common;
import com.example.ezhospital.Common.NonSwipeViewPager;
import com.example.ezhospital.Model.Barber;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.shuhart.stepview.StepView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dmax.dialog.SpotsDialog;

public class BookingActivity extends AppCompatActivity {

    LocalBroadcastManager localBroadcastManager;

    AlertDialog dialog;

    CollectionReference barberRef;

    @BindView(R.id.step_view)
    StepView stepView;
    @BindView(R.id.view_pager)
    NonSwipeViewPager viewPager;
    @BindView(R.id.btn_prev_step)
    Button btn_prev_step;
    @BindView(R.id.btn_next_step)
    Button btn_next_step;

    @OnClick(R.id.btn_prev_step)
    void previousClick(){
        if (Common.step==3||Common.step>0){
            Common.step--;
            viewPager.setCurrentItem(Common.step);
            if (Common.step<3)
            {
                btn_next_step.setEnabled(true);
                setColorButton();
            }
        }
    }

    @OnClick(R.id.btn_next_step)
    void nextClick(){
        if (Common.step<3 || Common.step==0){
            Common.step++;
            if (Common.step==1){
                if (Common.currentDepartment !=null)
                    loadBarberBySalon(Common.currentDepartment.getSalonId());
            }
            else if (Common.step==2)//pick time slot
            {
                if (Common.currentBarber !=null)
                    loadTimeSlotOfBarber(Common.currentBarber.getBarberId());
            }
            else if (Common.step==3)//confirm
            {
                if (Common.currentTimeSlot!=-1)
                    confirmBooking();
            }
            viewPager.setCurrentItem(Common.step);
        }
    }

    private void confirmBooking() {
        //Send broadcast to fragment step four
        Intent intent=new Intent(Common.KEY_CONFIRM_BOOKING);
        localBroadcastManager.sendBroadcast(intent);
    }

    private void loadTimeSlotOfBarber(String barberId) {
        //Send local broadcast to fragment step3
        Intent intent=new Intent(Common.KEY_DISPLAY_TIME_SLOT);
        localBroadcastManager.sendBroadcast(intent);

    }

    private void loadBarberBySalon(String salonId) {
        dialog.show();

        ///AllSalon/NewYork/Branch/54Mp1pXRr1zpyPAjNjjT/Baber
        if (!TextUtils.isEmpty(Common.city)){
        barberRef= FirebaseFirestore.getInstance()
                .collection("AllSalon")
                .document(Common.city)
                .collection("Branch")
                .document(salonId)
                .collection("Barber");

        barberRef.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        ArrayList<Barber> barbers = new ArrayList<>();
                        for (QueryDocumentSnapshot barberSnapShot:task.getResult()){
                            Barber barber =barberSnapShot.toObject(Barber.class);
                            barber.setPassword("");
                            barber.setBarberId(barberSnapShot.getId());
                            barbers.add(barber);
                        }
                        Intent intent=new Intent(Common.KEY_BARBER_LOAD_DONE);
                        intent.putParcelableArrayListExtra(Common.KEY_BARBER_LOAD_DONE, barbers);
                        localBroadcastManager.sendBroadcast(intent);

                        dialog.dismiss();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        dialog.dismiss();
                    }
                });
        }
    }


    private BroadcastReceiver buttonNextReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            int step=intent.getIntExtra(Common.KEY_STEP,0);
            if (step==1)
                Common.currentDepartment =intent.getParcelableExtra(Common.KEY_SALON_STORE);
            else if (step==2)
                Common.currentBarber =intent.getParcelableExtra(Common.KEY_BARBER_SELECTED);
            else if (step==3)
                Common.currentTimeSlot=intent.getIntExtra(Common.KEY_TIME_SLOT,-1);
            btn_next_step.setEnabled(true);
            setColorButton();
        }
    };

    @Override
    protected void onDestroy() {
        localBroadcastManager.unregisterReceiver(buttonNextReceiver);
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        ButterKnife.bind(BookingActivity.this);

        dialog =new SpotsDialog.Builder().setContext(this).build();

        localBroadcastManager=LocalBroadcastManager.getInstance(this);
        localBroadcastManager.registerReceiver(buttonNextReceiver,new IntentFilter(Common.KEY_ENABLE_BUTTON_NEXT));

        setupStepView();
        setColorButton();

        //view
        viewPager.setAdapter(new MyViewPagerAdapter(getSupportFragmentManager()));
        viewPager.setOffscreenPageLimit(4);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int i) {
                stepView.go(i,true);
                if (i==0)
                    btn_prev_step.setEnabled(false);
                else
                    btn_prev_step.setEnabled(true);

                //set disable button next here
                btn_next_step.setEnabled(false);

                setColorButton();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void setColorButton() {
        if (btn_next_step.isEnabled()){
            btn_next_step.setBackgroundResource(R.color.gray);
        }else {
            btn_next_step.setBackgroundResource(android.R.color.darker_gray);
        }

        if (btn_prev_step.isEnabled()){
            btn_prev_step.setBackgroundResource(R.color.gray);
        }else {
            btn_prev_step.setBackgroundResource(android.R.color.darker_gray);

        }
    }

    private void setupStepView() {
        List<String> stepList=new ArrayList<>();
        stepList.add("Department");
        stepList.add("Barber");
        stepList.add("Time");
        stepList.add("Confirm");
        stepView.setSteps(stepList);
    }
}