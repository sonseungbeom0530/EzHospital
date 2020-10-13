package com.example.ezhospital.Fragment;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.ezhospital.Common.Common;
import com.example.ezhospital.Model.BookingInformation;
import com.example.ezhospital.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;

public class BookingStep4Fragment extends Fragment {

    SimpleDateFormat simpleDateFormat;
    LocalBroadcastManager localBroadcastManager;
    Unbinder unbinder;
    FirebaseAuth firebaseAuth;

    AlertDialog alertDialog;

    private static final String TAG = "Tag";
    @BindView(R.id.txt_booking_barber_text)
    TextView txt_booking_barber_text;
    @BindView(R.id.txt_booking_time_text)
    TextView txt_booking_time_text;
    @BindView(R.id.txt_salon_address)
    TextView txt_salon_address;
    @BindView(R.id.txt_salon_name)
    TextView txt_salon_name;
    @BindView(R.id.txt_salon_open_hours)
    TextView txt_salon_open_hours;
    @BindView(R.id.txt_salon_phone)
    TextView txt_salon_phone;
    @BindView(R.id.txt_salon_website)
    TextView txt_salon_website;

    @OnClick(R.id.btn_confirm)
    void confirmBooking(){

        firebaseAuth=FirebaseAuth.getInstance();

        alertDialog.show();

        String startTime=Common.convertTimeSlotToString(Common.currentTimeSlot);
        String[] convertTime =startTime.split("-");
        String[] startTimeConvert=convertTime[0].split(":");
        int startHourInt=Integer.parseInt(startTimeConvert[0].trim());
        int startMinInt=Integer.parseInt(startTimeConvert[1].trim());

        Calendar bookingDateWithourHouse=Calendar.getInstance();
        bookingDateWithourHouse.setTimeInMillis(Common.bookingDate.getTimeInMillis());
        bookingDateWithourHouse.set(Calendar.HOUR_OF_DAY,startHourInt);
        bookingDateWithourHouse.set(Calendar.MINUTE,startMinInt);

        Timestamp timestamp=new Timestamp(bookingDateWithourHouse.getTime());
        //create booking information
        BookingInformation bookingInformation=new BookingInformation();

        bookingInformation.setCityBook(Common.city);
        bookingInformation.setTimestamp(timestamp);
        bookingInformation.setDone(false);
        bookingInformation.setBarberId(Common.currentBarber.getBarberId());
        bookingInformation.setBarberName(Common.currentBarber.getName());
       //bookingInformation.setCustomerName(Common.currentUser.getName());
        //bookingInformation.setCustomerPhone(Common.currentUser.getPhone());

        bookingInformation.setSalonId(Common.currentSalon.getSalonId());
        bookingInformation.setSalonAddress(Common.currentSalon.getAddress());
        bookingInformation.setSalonName(Common.currentSalon.getName());
        bookingInformation.setTime(new StringBuilder(Common.convertTimeSlotToString(Common.currentTimeSlot))
                .append(" at")
                .append(simpleDateFormat.format(bookingDateWithourHouse.getTime())).toString());
        bookingInformation.setSlot(Long.valueOf(Common.currentTimeSlot));

        DocumentReference bookingDate=  FirebaseFirestore.getInstance()
                .collection("AllSalon")
                .document(Common.city)
                .collection("Branch")
                .document(Common.currentSalon.getSalonId())
                .collection("Barber")
                .document(Common.currentBarber.getBarberId())
                .collection(Common.simpleDateFormat.format(Common.bookingDate.getTime()))
                .document(String.valueOf(Common.currentTimeSlot));

        bookingDate.set(bookingInformation)
                .addOnSuccessListener(aVoid -> {
                    addToUserBooking(bookingInformation);
                    /*resetStaticData();
                    getActivity().finish();
                    Toast.makeText(getContext(),"Success!",Toast.LENGTH_SHORT).show();
                    if (alertDialog.isShowing())
                        alertDialog.dismiss();*/
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(),""+e.getMessage(),Toast.LENGTH_SHORT).show();

            }
        });


    }

    private void addToCalendar(Calendar bookingDate, String startDate) {
        String startTime=Common.convertTimeSlotToString(Common.currentTimeSlot);
        String[] convertTime =startTime.split("-");
        String[] startTimeConvert=convertTime[0].split(":");
        int startHourInt=Integer.parseInt(startTimeConvert[0].trim());
        int startMinInt=Integer.parseInt(startTimeConvert[1].trim());

        String[] endTimeConvert=convertTime[1].split(":");
        int endHourInt=Integer.parseInt(endTimeConvert[0].trim());
        int endMinInt=Integer.parseInt(endTimeConvert[1].trim());

        Calendar startEvent=Calendar.getInstance();
        startEvent.setTimeInMillis(bookingDate.getTimeInMillis());
        startEvent.set(Calendar.HOUR_OF_DAY,startHourInt);
        startEvent.set(Calendar.MINUTE,startMinInt);

        Calendar endEvent=Calendar.getInstance();
        endEvent.setTimeInMillis(bookingDate.getTimeInMillis());
        endEvent.set(Calendar.HOUR_OF_DAY,endHourInt);
        endEvent.set(Calendar.MINUTE,endMinInt);

        SimpleDateFormat calendarDateFormat=new SimpleDateFormat("dd-MM-yyyy HH:mm");
        String startEventTime=calendarDateFormat.format(startEvent.getTime());
        String endEventTime=calendarDateFormat.format(endEvent.getTime());

        addToDeviceCalendar(startEventTime,endEventTime,"Haircut Booking",
                new StringBuilder("Haircut from")
        .append(startTime)
        .append(" with ")
        .append(Common.currentBarber.getName())
        .append(" at ")
        .append(Common.currentSalon.getName()).toString(),
                new StringBuilder("Address : ").append(Common.currentSalon.getAddress()).toString());

    }

    private void addToDeviceCalendar(String startEventTime, String endEventTime, String title, String description, String location) {
        SimpleDateFormat calendarDateFormat=new SimpleDateFormat("dd-MM-yyyy HH:mm");
        try {
            Date start=calendarDateFormat.parse(startEventTime);
            Date end=calendarDateFormat.parse(endEventTime);

            ContentValues event=new ContentValues();
            event.put(CalendarContract.Events.CALENDAR_ID,getCalendar(getContext()));
            event.put(CalendarContract.Events.TITLE,title);
            event.put(CalendarContract.Events.DESCRIPTION, description);
            event.put(CalendarContract.Events.EVENT_LOCATION,location);

            event.put(CalendarContract.Events.DTSTART,start.getTime());
            event.put(CalendarContract.Events.DTEND,end.getTime());
            event.put(CalendarContract.Events.ALL_DAY,0);
            event.put(CalendarContract.Events.HAS_ALARM,1);

            String timezone= TimeZone.getDefault().getID();
            event.put(CalendarContract.Events.EVENT_TIMEZONE,timezone);

            Uri calendars;
            if(Build.VERSION.SDK_INT>=8)
                calendars=Uri.parse("content://com.android.calendar/events");
            else
                calendars =Uri.parse("content://calendar/events");


            Uri uri_save=getActivity().getContentResolver().insert(calendars,event);

            Paper.init(getActivity());
            Paper.book().write(Common.EVENT_URI_CACHE,uri_save.toString());

        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    private String getCalendar(Context context) {
        String gmailIdCalendar="";
        String projection[]={"_id","calendar_displayName"};
        Uri calendars=Uri.parse("content://com.android.calendar/calendars");

        ContentResolver contentResolver=context.getContentResolver();
        Cursor managedCursor=contentResolver.query(calendars,projection,null,null,null);

        if (managedCursor.moveToFirst()){
            String calName;
            int nameCol=managedCursor.getColumnIndex(projection[1]);
            int idCol=managedCursor.getColumnIndex(projection[0]);
            do {
                calName=managedCursor.getString(nameCol);
                if (calName.contains("@gmail.com")){
                    gmailIdCalendar=managedCursor.getString(idCol);
                    break;
                }
            }while (managedCursor.moveToNext());
            managedCursor.close();
        }
        return gmailIdCalendar;
    }

    private void addToUserBooking(BookingInformation bookingInformation) {


        /*final CollectionReference userBooking=FirebaseFirestore.getInstance()
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
                        //if (task.getResult().isEmpty())
                        //{
                            userBooking.document()
                                    .set(bookingInformation)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            if (alertDialog.isShowing())
                                                alertDialog.dismiss();

                                            addToCalendar(Common.bookingDate,Common.convertTimeSlotToString(Common.currentTimeSlot));
                                            resetStaticData();
                                            getActivity().finish();
                                            Toast.makeText(getContext(),"Success!",Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            if (alertDialog.isShowing())
                                                alertDialog.dismiss();
                                            Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        //}
                        //else
                            //{
                            //if (alertDialog.isShowing())
                              //  alertDialog.dismiss();
                            //resetStaticData();
                            //getActivity().finish();
                            //Toast.makeText(getContext(),"Success!!!!!",Toast.LENGTH_SHORT).show();
                        //}
                    }
                });


    }

    private void resetStaticData() {
        Common.step=0;
        Common.currentTimeSlot=-1;
        Common.currentSalon=null;
        Common.currentBarber =null;
        Common.bookingDate.add(Calendar.DATE,0);
    }

    BroadcastReceiver confirmBookingReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            setData();
        }
    };

    private void setData() {
        txt_booking_barber_text.setText(Common.currentBarber.getName());
        txt_booking_time_text.setText(new StringBuilder(Common.convertTimeSlotToString(Common.currentTimeSlot))
        .append(" at")
        .append(simpleDateFormat.format(Common.bookingDate.getTime())));

        txt_salon_address.setText(Common.currentSalon.getAddress());
        //txt_salon_website.setText(Common.currentSalon.getWebsite());
        txt_salon_website.setText("www.dsf.com");
        txt_salon_name.setText(Common.currentSalon.getName());
        //txt_salon_open_hours.setText(Common.currentSalon.getOpenHours());
        txt_salon_open_hours.setText("9:00");

    }

    static BookingStep4Fragment instance;

    public static BookingStep4Fragment getInstance(){
        if (instance==null)
            instance=new BookingStep4Fragment();
        return instance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        simpleDateFormat=new SimpleDateFormat("dd/MM/yyyy");

        alertDialog=new SpotsDialog.Builder().setContext(getContext()).setCancelable(false).build();

        localBroadcastManager=LocalBroadcastManager.getInstance(getContext());
        localBroadcastManager.registerReceiver(confirmBookingReceiver,new IntentFilter(Common.KEY_CONFIRM_BOOKING));


    }

    @Override
    public void onDestroy() {
        localBroadcastManager.unregisterReceiver(confirmBookingReceiver);
        super.onDestroy();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View itemView= inflater.inflate(R.layout.fragment_booking_step_four,container,false);
        unbinder= ButterKnife.bind(this,itemView);

        return itemView;

    }
}
