package com.example.ezhospital.Fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ezhospital.Adapter.MyDepartmentAdapter;
import com.example.ezhospital.Common.Common;
import com.example.ezhospital.Common.SpaceItemDecoration;
import com.example.ezhospital.Interface.IHospitalLoadListener;
import com.example.ezhospital.Interface.IBranchLoadListener;
import com.example.ezhospital.Model.Department;
import com.example.ezhospital.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import dmax.dialog.SpotsDialog;

public class BookingStep1Fragment extends Fragment implements IHospitalLoadListener, IBranchLoadListener {

    //Variable
    CollectionReference allSalonRef;
    CollectionReference branchRef;

    IHospitalLoadListener iHospitalLoadListener;
    IBranchLoadListener iBranchLoadListener;

    @BindView(R.id.spinner)
    MaterialSpinner spinner;
    @BindView(R.id.recycler_salon)
    RecyclerView recycler_salon;

    Unbinder unbinder;

    AlertDialog alertDialog;

    static BookingStep1Fragment instance;

    public static BookingStep1Fragment getInstance(){
        if (instance==null)
            instance=new BookingStep1Fragment();
        return instance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        allSalonRef= FirebaseFirestore.getInstance().collection("AllSalon");
        iHospitalLoadListener =this;
        iBranchLoadListener=this;

        alertDialog=new SpotsDialog.Builder().setContext(getActivity()).build();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View itemView= inflater.inflate(R.layout.fragment_booking_step_one,container,false);
        unbinder= ButterKnife.bind(this,itemView);

        initView();
        loadAllSalon();

        return itemView;
    }

    private void initView() {
        recycler_salon.setHasFixedSize(true);
        recycler_salon.setLayoutManager(new GridLayoutManager(getActivity(),2));
        recycler_salon.addItemDecoration(new SpaceItemDecoration(4));
    }

    private void loadAllSalon() {
        allSalonRef.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            List<String> list=new ArrayList<>();
                            list.add("Please choose floor");
                                for (QueryDocumentSnapshot documentSnapshot:task.getResult())
                                    list.add(documentSnapshot.getId());
                                iHospitalLoadListener.onAllSalonLoadSuccess(list);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                iHospitalLoadListener.onAllSalonLoadFailed(e.getMessage());
            }
        });
    }

    @Override
    public void onAllSalonLoadSuccess(List<String> areaNameList) {
        spinner.setItems(areaNameList);
        spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                if (position>0){
                    loadBranchOfCity(item.toString());
                }
                else {
                    recycler_salon.setVisibility(View.GONE);
                }
            }
        });
    }

    private void loadBranchOfCity(String cityName) {
        alertDialog.show();
        Common.city=cityName;
        branchRef=FirebaseFirestore.getInstance()
                .collection("AllSalon")
                .document(cityName)
                .collection("Branch");

        branchRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                List<Department> list=new ArrayList<>();
                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot documentSnapshot:task.getResult())
                    {
                        Department department =documentSnapshot.toObject(Department.class);
                        department.setSalonId(documentSnapshot.getId());
                        list.add(department);
                    }
                    iBranchLoadListener.onBranchLoadSuccess(list);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                iBranchLoadListener.onBranchLoadFailed(e.getMessage());
            }
        });
    }

    @Override
    public void onAllSalonLoadFailed(String message) {
        Toast.makeText(getActivity(),message,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBranchLoadSuccess(List<Department> departmentList) {
        MyDepartmentAdapter adapter=new MyDepartmentAdapter(getActivity(), departmentList);
        recycler_salon.setAdapter(adapter);
        recycler_salon.setVisibility(View.VISIBLE);
        alertDialog.dismiss();
    }

    @Override
    public void onBranchLoadFailed(String message) {
        Toast.makeText(getActivity(),message,Toast.LENGTH_SHORT).show();
        alertDialog.dismiss();
    }
}
