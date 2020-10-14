package com.example.ezhospital.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ezhospital.AdminCheckBookingActivity;
import com.example.ezhospital.Common.AdminAuthenticationDialog;
import com.example.ezhospital.Common.Common;
import com.example.ezhospital.Interface.IDialogClickListener;
import com.example.ezhospital.Interface.IGetBarberListener;
import com.example.ezhospital.Interface.IRecyclerItemSelectedListener;
import com.example.ezhospital.Interface.IUserLoginRememberListener;
import com.example.ezhospital.Model.Barber;
import com.example.ezhospital.Model.Department;
import com.example.ezhospital.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class MyDepartAdapter extends RecyclerView.Adapter<MyDepartAdapter.MyViewHolder> implements IDialogClickListener {

    Context context;
    List<Department> departmentList;
    List<CardView> cardViewList;

    IUserLoginRememberListener iUserLoginRememberListener;
    IGetBarberListener iGetBarberListener;

    public MyDepartAdapter(Context context, List<Department> departmentList,IUserLoginRememberListener iUserLoginRememberListener,IGetBarberListener iGetBarberListener) {
        this.context = context;
        this.departmentList = departmentList;
        cardViewList=new ArrayList<>();
        this.iGetBarberListener=iGetBarberListener;
        this.iUserLoginRememberListener=iUserLoginRememberListener;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView= LayoutInflater.from(context)
                .inflate(R.layout.layout_salon,viewGroup,false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int position) {
        myViewHolder.txt_salon_name.setText(departmentList.get(position).getName());
        myViewHolder.txt_salon_address.setText(departmentList.get(position).getAddress());
        if(!cardViewList.contains(myViewHolder.card_salon))
            cardViewList.add(myViewHolder.card_salon);

        myViewHolder.setiRecyclerItemSelectedListener(new IRecyclerItemSelectedListener() {
            @Override
            public void onItemSelectedListener(View view, int pos) {

                Common.selectedDepartment=departmentList.get(pos);
                showAuthenticationDialog();
            }
        });
    }

    private void showAuthenticationDialog() {
        AdminAuthenticationDialog.getInstance().showAuthenticationDialog("Admin Authentication",
                "Authentication",
                "Cancel",context,this);
    }

    @Override
    public int getItemCount() {
        return departmentList.size();
    }

    @Override
    public void onClickPositiveButton(DialogInterface dialogInterface, String userName, String password) {
        AlertDialog loading=new SpotsDialog.Builder().setCancelable(false).setContext(context).build();

        loading.show();

        FirebaseFirestore.getInstance()
                .collection("Hospital")
                .document(Common.state_name)
                .collection("Branch")
                .document(Common.selectedDepartment.getSalonId())
                .collection("Doctor")
                .whereEqualTo("username",userName)
                .whereEqualTo("password",password)
                .limit(1)
                .get()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context,e.getMessage(), Toast.LENGTH_SHORT).show();
                        loading.dismiss();
                    }
                }).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful())
                {
                    if (task.getResult().size()>0)
                    {
                        dialogInterface.dismiss();
                        loading.dismiss();

                        iUserLoginRememberListener.onUserLoginSuccess(userName);
                        Barber barber=new Barber();
                        for (DocumentSnapshot barberSnapShot:task.getResult())
                        {
                            barber=barberSnapShot.toObject(Barber.class);
                            barber.setBarberId(barberSnapShot.getId());
                        }
                        iGetBarberListener.onGetBarberSuccess(barber);

                        Intent intent=new Intent(context, AdminCheckBookingActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);

                    }else {
                        loading.dismiss();
                        Toast.makeText(context,"Wrong name/password",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

    @Override
    public void onClickNegativeButton(DialogInterface dialogInterface) {
        dialogInterface.dismiss();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txt_salon_name,txt_salon_address;
        CardView card_salon;

        IRecyclerItemSelectedListener iRecyclerItemSelectedListener;

        public void setiRecyclerItemSelectedListener(IRecyclerItemSelectedListener iRecyclerItemSelectedListener) {
            this.iRecyclerItemSelectedListener = iRecyclerItemSelectedListener;
        }

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            card_salon=(CardView)itemView.findViewById(R.id.card_salon);
            txt_salon_address=(TextView)itemView.findViewById(R.id.txt_salon_address);
            txt_salon_name=(TextView)itemView.findViewById(R.id.txt_salon_name);

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            iRecyclerItemSelectedListener.onItemSelectedListener(v,getAdapterPosition());
        }
    }
}
