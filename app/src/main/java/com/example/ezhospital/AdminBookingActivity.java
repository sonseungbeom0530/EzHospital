package com.example.ezhospital;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import com.example.ezhospital.Adapter.MyStateAdapter;
import com.example.ezhospital.Common.Common;
import com.example.ezhospital.Common.SpaceItemDecoration;
import com.example.ezhospital.Interface.IOnAllStateLoadListener;
import com.example.ezhospital.Model.Barber;
import com.example.ezhospital.Model.Department;
import com.example.ezhospital.Model.Floor;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.ThrowOnExtraProperties;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;

public class AdminBookingActivity extends AppCompatActivity implements IOnAllStateLoadListener {

    @BindView(R.id.recycler_state)
    RecyclerView recycler_state;

    CollectionReference hospitalCollection;

    IOnAllStateLoadListener iOnAllStateLoadListener;

    MyStateAdapter adapter;
    AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Paper.init(this);
        String user=Paper.book().read(Common.LOGGED_KEY);
        if (TextUtils.isEmpty(user)){
            setContentView(R.layout.activity_admin_booking);

            ButterKnife.bind(this);

            initView();

            init();

            loadAllStateFromFireStore();
        }else {
            Gson gson=new Gson();
            Common.state_name=Paper.book().read(Common.STATE_KEY);
            Common.selectedDepartment=gson.fromJson(Paper.book().read(Common.DEPARTMENT_KEY,""),
                    new TypeToken<Department>(){}.getType());
            Common.currentBarber=gson.fromJson(Paper.book().read(Common.DOCTOR_KEY,""),
                    new TypeToken<Barber>(){}.getType());

            Intent intent=new Intent(this,AdminCheckBookingActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK) ;
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }


    }

    private void loadAllStateFromFireStore() {
        dialog.show();
        hospitalCollection
                .get().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                iOnAllStateLoadListener.onAllStateLoadFailed(e.getMessage());
            }
        }).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    List<Floor> floors=new ArrayList<>();
                    for (DocumentSnapshot floorSnapShot:task.getResult()){
                        Floor floor=floorSnapShot.toObject(Floor.class);
                        floors.add(floor);
                    }
                    iOnAllStateLoadListener.onAllStateLoadSuccess(floors);
                }
            }
        });
    }

    private void init() {
        hospitalCollection= FirebaseFirestore.getInstance().collection("Hospital");
        iOnAllStateLoadListener=this;
        dialog=new SpotsDialog.Builder().setContext(this).setCancelable(false).build();
    }

    private void initView() {
        recycler_state.setHasFixedSize(true);
        recycler_state.setLayoutManager(new GridLayoutManager(this,2));
        recycler_state.addItemDecoration(new SpaceItemDecoration(0));
    }

    @Override
    public void onAllStateLoadSuccess(List<Floor> floorList) {
        adapter=new MyStateAdapter(this,floorList);
        recycler_state.setAdapter(adapter);

        dialog.dismiss();
    }

    @Override
    public void onAllStateLoadFailed(String message) {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
        dialog.dismiss();

    }
}