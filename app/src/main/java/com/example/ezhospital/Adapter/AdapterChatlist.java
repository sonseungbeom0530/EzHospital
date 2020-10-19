package com.example.ezhospital.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.ezhospital.ChatActivity;
import com.example.ezhospital.Model.ModelUser;
import com.example.ezhospital.R;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

public class AdapterChatlist extends RecyclerView.Adapter<AdapterChatlist.MyHolder>{

    Context context;
    List<ModelUser> userList; //get user info
    private HashMap<String,String> lastMessageHashMap;

    //constructor
    public AdapterChatlist(Context context, List<ModelUser> userList) {
        this.context = context;
        this.userList = userList;
        lastMessageHashMap = new HashMap<>();
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        //inflate layout row_chatlist.xml
        View view = LayoutInflater.from(context).inflate(R.layout.row_chatlist,viewGroup,false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder myHolder, int i) {
        //get data
        final String hisUid=userList.get(i).getUid();
        String userImage=userList.get(i).getImage();
        String userName=userList.get(i).getName();
        String lastMessage=lastMessageHashMap.get(hisUid);

        //set data
        myHolder.nameTv.setText(userName);
        if(lastMessage==null || lastMessage.equals("default")){
            myHolder.lastMessageTv.setVisibility(View.GONE);
        }else {
            myHolder.lastMessageTv.setVisibility(View.VISIBLE);
            myHolder.lastMessageTv.setText(lastMessage);
        }
        try{
            Picasso.get().load(userImage).placeholder(R.drawable.ic_baseline_face_24).into(myHolder.profileIv);
        }catch (Exception e){
            //Picasso.get().load(R.drawable.ic_default_img_purple).into(myHolder.profileIv);
        }

        //handle click of user in chatlist
        myHolder.itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //start chat activity with that user
                Intent intent =new Intent(context, ChatActivity.class);
                intent.putExtra("hisUid",hisUid);
                context.startActivity(intent);
            }
        });
    }

    public void setLastMessageHashMap(String userId,String lastMessage){
        lastMessageHashMap.put(userId,lastMessage);
    }

    @Override
    public int getItemCount() {
        return userList.size();//size of the list
    }



    class MyHolder extends RecyclerView.ViewHolder{
        //views of row chatlist.xml
        ImageView profileIv;
        TextView nameTv,lastMessageTv;
        public MyHolder(@NonNull View itemView) {
            super(itemView);

            //init views
            profileIv=itemView.findViewById(R.id.profileIv);
            nameTv=itemView.findViewById(R.id.nameTv);
            lastMessageTv=itemView.findViewById(R.id.lastMessageTv);
        }
    }
}