package com.example.ezhospital.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ezhospital.Common.Common;
import com.example.ezhospital.Interface.IRecyclerItemSelectedListener;
import com.example.ezhospital.Model.Barber;
import com.example.ezhospital.R;

import java.util.ArrayList;
import java.util.List;

public class MyDoctorAdapter extends RecyclerView.Adapter<MyDoctorAdapter.MyViewHolder> {

    Context context;
    List<Barber> barberList;
    List<CardView> cardViewList;
    LocalBroadcastManager localBroadcastManager;

    public MyDoctorAdapter(Context context, List<Barber> barberList) {
        this.context = context;
        this.barberList = barberList;
        cardViewList=new ArrayList<>();
        localBroadcastManager=LocalBroadcastManager.getInstance(context);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView= LayoutInflater.from(context)
                .inflate(R.layout.layout_barber,viewGroup,false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int position) {
        myViewHolder.txt_barber_name.setText(barberList.get(position).getName());
        //myViewHolder.ratingBar.setRating((float) barberList.get(position).getRating());
        if (!cardViewList.contains(myViewHolder.card_barber))
            cardViewList.add(myViewHolder.card_barber);

        myViewHolder.setiRecyclerItemSelectedListener(new IRecyclerItemSelectedListener() {
            @Override
            public void onItemSelectedListener(View view, int pos) {
                for (CardView cardView:cardViewList)
                {
                    cardView.setCardBackgroundColor(context.getResources()
                    .getColor(android.R.color.white));
                }
                //set background for choice
                myViewHolder.card_barber.setCardBackgroundColor(
                        context.getResources()
                        .getColor(android.R.color.holo_orange_dark)
                );

                //send local broadcast to enable button next
                Intent intent=new Intent(Common.KEY_ENABLE_BUTTON_NEXT);
                intent.putExtra(Common.KEY_BARBER_SELECTED, barberList.get(pos));
                intent.putExtra(Common.KEY_STEP,2);
                localBroadcastManager.sendBroadcast(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return barberList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txt_barber_name;
        RatingBar ratingBar;
        CardView card_barber;

        IRecyclerItemSelectedListener iRecyclerItemSelectedListener;

        public void setiRecyclerItemSelectedListener(IRecyclerItemSelectedListener iRecyclerItemSelectedListener) {
            this.iRecyclerItemSelectedListener = iRecyclerItemSelectedListener;
        }

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            card_barber=(CardView) itemView.findViewById(R.id.card_barber) ;
            txt_barber_name=(TextView) itemView.findViewById(R.id.txt_barber_name);
            ratingBar=(RatingBar) itemView.findViewById(R.id.rtb_barber);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            iRecyclerItemSelectedListener.onItemSelectedListener(v,getAdapterPosition());
        }
    }
}
