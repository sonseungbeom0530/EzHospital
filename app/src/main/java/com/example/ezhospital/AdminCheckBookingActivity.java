package com.example.ezhospital;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.ezhospital.Adapter.AdminTimeSlotAdapter;
import com.example.ezhospital.Adapter.MyTimeSlotAdapter;
import com.example.ezhospital.Common.Common;
import com.example.ezhospital.Common.SpaceItemDecoration;
import com.example.ezhospital.Interface.ITimeSlotLoadListener;
import com.example.ezhospital.Model.TimeSlot;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import devs.mulham.horizontalcalendar.HorizontalCalendar;
import devs.mulham.horizontalcalendar.HorizontalCalendarView;
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarListener;
import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;

public class AdminCheckBookingActivity extends AppCompatActivity implements ITimeSlotLoadListener {

    @BindView(R.id.activity_main)
    DrawerLayout drawerLayout;

    ITimeSlotLoadListener iTimeSlotLoadListener;

    DocumentReference doctorDoc;
    android.app.AlertDialog dialog;


    @BindView(R.id.recycler_time_slot)
    RecyclerView recycler_time_slot;
    @BindView(R.id.calendarView)
    HorizontalCalendarView calendarView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_check_booking);

        ButterKnife.bind(this);
        
        init();
        initView();
    }



    private void initView() {
        recycler_time_slot.setHasFixedSize(true);
        GridLayoutManager layoutManager=new GridLayoutManager(this,3);
        recycler_time_slot.setLayoutManager(layoutManager);
        recycler_time_slot.addItemDecoration(new SpaceItemDecoration(0));

        dialog=new SpotsDialog.Builder().setCancelable(false).setContext(this).build();

        Calendar date= Calendar.getInstance();
        date.add(Calendar.DATE,0);
        loadAvailableTimeSlotOfBarber(Common.currentBarber.getBarberId(),
                Common.simpleDateFormat.format(date.getTime()));


        Calendar startDate=Calendar.getInstance();
        startDate.add(Calendar.DATE,0);
        Calendar endDate=Calendar.getInstance();
        endDate.add(Calendar.DATE,2);

        HorizontalCalendar horizontalCalendar=new HorizontalCalendar.Builder(this,R.id.calendarView)
                .range(startDate,endDate)
                .datesNumberOnScreen(1)
                .mode(HorizontalCalendar.Mode.DAYS)
                .defaultSelectedDate(startDate)
                .configure()
                .end()
                .build();

        horizontalCalendar.setCalendarListener(new HorizontalCalendarListener() {
            @Override
            public void onDateSelected(Calendar date, int position) {
                if (Common.bookingDate.getTimeInMillis()!=date.getTimeInMillis()){
                    Common.bookingDate=date;
                    loadAvailableTimeSlotOfBarber(Common.currentBarber.getBarberId(),
                            Common.simpleDateFormat.format(date.getTime()));
                }
            }
        });
    }

    private void loadAvailableTimeSlotOfBarber(String barberId, String bookDate) {
        dialog.show();

        ////AllSalon/NewYork/Branch/54Mp1pXRr1zpyPAjNjjT/Barber/2QZ1DBgxDJegAGTsoVG6
        doctorDoc= FirebaseFirestore.getInstance()
                .collection("Hospital")
                .document(Common.state_name)
                .collection("Branch")
                .document(Common.selectedDepartment.getSalonId())
                .collection("Doctor")
                .document(Common.currentBarber.getBarberId());
        doctorDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot documentSnapshot=task.getResult();
                    if (documentSnapshot.exists()){
                        CollectionReference date=FirebaseFirestore.getInstance()
                                .collection("Hospital")
                                .document(Common.state_name)
                                .collection("Branch")
                                .document(Common.selectedDepartment.getSalonId())
                                .collection("Doctor")
                                .document(Common.currentBarber.getBarberId())
                                .collection(bookDate); //bookDate is date simpleformat with dd_MM_YYYY

                        date.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()){
                                    QuerySnapshot querySnapshot=task.getResult();
                                    if (querySnapshot.isEmpty())
                                        iTimeSlotLoadListener.onTimeSlotLoadEmpty();
                                    else {
                                        List<TimeSlot> timeSlots=new ArrayList<>();
                                        for (QueryDocumentSnapshot document:task.getResult())
                                            timeSlots.add(document.toObject(TimeSlot.class));
                                        iTimeSlotLoadListener.onTimeSlotLoadSuccess(timeSlots);
                                    }
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                iTimeSlotLoadListener.onTimeSlotLoadFailed(e.getMessage());
                            }
                        });
                    }
                }
            }
        });
    }

    private void init() {
        iTimeSlotLoadListener=this;
    }

    private void logOut(){
        Paper.init(this);
        Paper.book().delete(Common.DEPARTMENT_KEY);
        Paper.book().delete(Common.DOCTOR_KEY);
        Paper.book().delete(Common.STATE_KEY);
        Paper.book().delete(Common.LOGGED_KEY);

    }

    @Override
    public void onBackPressed() {
        logOut();
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent mainActivity=new Intent(AdminCheckBookingActivity.this,AdminMainActivity.class);
                        mainActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        mainActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(mainActivity);
                        finish();
                    }
                }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();

    }

    @Override
    public void onTimeSlotLoadSuccess(List<TimeSlot> timeSlotList) {
        AdminTimeSlotAdapter adapter=new AdminTimeSlotAdapter(this,timeSlotList);
        recycler_time_slot.setAdapter(adapter);

        dialog.dismiss();
    }

    @Override
    public void onTimeSlotLoadFailed(String message) {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
        dialog.dismiss();
    }

    @Override
    public void onTimeSlotLoadEmpty() {
        AdminTimeSlotAdapter adapter=new AdminTimeSlotAdapter(this);
        recycler_time_slot.setAdapter(adapter);

        dialog.dismiss();
    }
}