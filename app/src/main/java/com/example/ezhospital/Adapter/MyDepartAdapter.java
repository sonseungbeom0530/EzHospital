package com.example.ezhospital.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ezhospital.Common.Common;
import com.example.ezhospital.Interface.IRecyclerItemSelectedListener;
import com.example.ezhospital.Model.Department;
import com.example.ezhospital.R;

import java.util.ArrayList;
import java.util.List;

public class MyDepartAdapter extends RecyclerView.Adapter<MyDepartAdapter.MyViewHolder> {

    Context context;
    List<Department> departmentList;
    List<CardView> cardViewList;
    LocalBroadcastManager localBroadcastManager;

    public MyDepartAdapter(Context context, List<Department> departmentList) {
        this.context = context;
        this.departmentList = departmentList;
        cardViewList=new ArrayList<>();
        localBroadcastManager=LocalBroadcastManager.getInstance(context);
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
                //set white background for all card not be selected
                for (CardView cardView:cardViewList)
                    cardView.setCardBackgroundColor(context.getResources().getColor(android.R.color.white));

                //Set selected BG for only selected item
                myViewHolder.card_salon.setCardBackgroundColor(context.getResources()
                        .getColor(android.R.color.holo_orange_dark));

                //send broadcast to tell booking activity enable button next
                Intent intent=new Intent(Common.KEY_ENABLE_BUTTON_NEXT);
                intent.putExtra(Common.KEY_SALON_STORE, departmentList.get(pos));
                intent.putExtra(Common.KEY_STEP,1);
                localBroadcastManager.sendBroadcast(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return departmentList.size();
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
