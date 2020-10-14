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
import com.example.ezhospital.Model.TimeSlot;
import com.example.ezhospital.R;

import java.util.ArrayList;
import java.util.List;

public class AdminTimeSlotAdapter extends RecyclerView.Adapter<AdminTimeSlotAdapter.MyViewHolder> {
    Context context;
    List<TimeSlot> timeSlotList;
    List<CardView> itemViewList;


    public AdminTimeSlotAdapter(Context context) {
        this.context = context;
        this.timeSlotList=new ArrayList<>();
        this.itemViewList=new ArrayList<>();
    }

    public AdminTimeSlotAdapter(Context context, List<TimeSlot> timeSlotList) {
        this.context = context;
        this.timeSlotList = timeSlotList;
        itemViewList=new ArrayList<>();
    }

    @NonNull
    @Override
    public AdminTimeSlotAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.layout_time_slot,parent,false);
        return new AdminTimeSlotAdapter.MyViewHolder(itemView);
    }



    @Override
    public void onBindViewHolder(@NonNull AdminTimeSlotAdapter.MyViewHolder myViewHolder, int i) {
        myViewHolder.txt_time_slot.setText(new StringBuilder(Common.convertTimeSlotToString(i)).toString());
        if (timeSlotList.size()==0)//if all position is available,just show list
        {
            myViewHolder.card_time_slot.setCardBackgroundColor(context.getResources().getColor(android.R.color.white));

            myViewHolder.txt_time_slot_description.setText("Available");
            myViewHolder.txt_time_slot_description.setTextColor(context.getResources().getColor(android.R.color.black));
            myViewHolder.txt_time_slot.setTextColor(context.getResources().getColor(android.R.color.black));
        }
        else
        {
            for (TimeSlot slotValue:timeSlotList){
                //Loop all time slot from server and set different color
                int slot=Integer.parseInt(slotValue.getSlot().toString());
                if (slot==i){

                    myViewHolder.card_time_slot.setTag(Common.DISABLE_TAG);

                    myViewHolder.card_time_slot.setCardBackgroundColor(context.getResources().getColor(android.R.color.darker_gray));
                    myViewHolder.txt_time_slot_description.setText("Full");
                    myViewHolder.txt_time_slot_description.setTextColor(context.getResources().getColor(android.R.color.white));
                    myViewHolder.txt_time_slot.setTextColor(context.getResources().getColor(android.R.color.white));

                }
            }
        }
        //Add only available time slot card to list
        if (!itemViewList.contains(myViewHolder.card_time_slot))
            itemViewList.add(myViewHolder.card_time_slot);

        //check if card time slot is available

        myViewHolder.setiRecyclerItemSelectedListener(new IRecyclerItemSelectedListener() {
            @Override
            public void onItemSelectedListener(View view, int pos) {
                for (CardView cardView:itemViewList)
                {
                    if (cardView.getTag()==null)
                        cardView.setCardBackgroundColor(context.getResources()
                                .getColor(android.R.color.white));
                }
                //our selected card wil be change color
                myViewHolder.card_time_slot.setCardBackgroundColor(context.getResources()
                        .getColor(android.R.color.holo_orange_dark));
            }
        });
    }


    @Override
    public int getItemCount() {
        return Common.TIME_SLOT_TOTAL;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txt_time_slot,txt_time_slot_description;
        CardView card_time_slot;

        IRecyclerItemSelectedListener iRecyclerItemSelectedListener;

        public void setiRecyclerItemSelectedListener(IRecyclerItemSelectedListener iRecyclerItemSelectedListener) {
            this.iRecyclerItemSelectedListener = iRecyclerItemSelectedListener;
        }

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            card_time_slot=(CardView)itemView.findViewById(R.id.card_time_slot);
            txt_time_slot=(TextView) itemView.findViewById(R.id.txt_time_slot);
            txt_time_slot_description=(TextView) itemView.findViewById(R.id.txt_time_slot_description);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            iRecyclerItemSelectedListener.onItemSelectedListener(v,getAdapterPosition());
        }
    }
}
