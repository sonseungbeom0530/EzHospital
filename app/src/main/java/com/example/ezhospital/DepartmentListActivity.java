package com.example.ezhospital;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ezhospital.Adapter.MyDepartAdapter;
import com.example.ezhospital.Adapter.MyDepartmentAdapter;
import com.example.ezhospital.Common.Common;
import com.example.ezhospital.Common.SpaceItemDecoration;
import com.example.ezhospital.Interface.IBranchLoadListener;
import com.example.ezhospital.Interface.IOnLoadCountFloor;
import com.example.ezhospital.Model.Department;
import com.example.ezhospital.Model.Floor;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import dmax.dialog.SpotsDialog;

public class DepartmentListActivity extends AppCompatActivity implements IOnLoadCountFloor, IBranchLoadListener {

    @BindView(R.id.txt_floor_count)
    TextView txt_floor_count;

    @BindView(R.id.recycler_floor)
    RecyclerView recycler_floor;

    IOnLoadCountFloor iOnLoadCountFloor;
    IBranchLoadListener iBranchLoadListener;

    AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_floor_list);

        ButterKnife.bind(this);
        
        initView();
        
        init();

        loadDepartmentBaseOnFloor(Common.state_name);
        
    }

    private void loadDepartmentBaseOnFloor(String name) {
        dialog.show();
        FirebaseFirestore.getInstance().collection("Hospital")
                .document(name)
                .collection("Branch")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()){
                            List<Department> departments=new ArrayList<>();
                            iOnLoadCountFloor.onLoadCountFloorSuccess(task.getResult().size());
                            for (DocumentSnapshot departmentSnapShot:task.getResult())
                            {
                                Department department=departmentSnapShot.toObject(Department.class);
                                departments.add(department);
                            }
                            iBranchLoadListener.onBranchLoadSuccess(departments);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                iBranchLoadListener.onBranchLoadFailed(e.getMessage());
            }
        });
    }

    private void init() {
        dialog=new SpotsDialog.Builder().setContext(this).setCancelable(false).build();
        iOnLoadCountFloor=this;
        iBranchLoadListener=this;
    }

    private void initView() {
        recycler_floor.setHasFixedSize(true);
        recycler_floor.setLayoutManager(new GridLayoutManager(this,2));
        recycler_floor.addItemDecoration(new SpaceItemDecoration(0));
    }

    @Override
    public void onLoadCountFloorSuccess(int count) {
        txt_floor_count.setText(new StringBuilder("All Department (")
        .append(count)
        .append(")"));
    }

    @Override
    public void onBranchLoadSuccess(List<Department> branchList) {
        MyDepartAdapter departAdapter=new MyDepartAdapter(this,branchList);
        recycler_floor.setAdapter(departAdapter);
        dialog.dismiss();
    }

    @Override
    public void onBranchLoadFailed(String message) {

        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
        dialog.dismiss();
    }
}