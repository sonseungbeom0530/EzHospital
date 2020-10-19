package com.example.ezhospital.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;


import com.example.ezhospital.Adapter.AdapterChatlist;
import com.example.ezhospital.Model.ModelChat;
import com.example.ezhospital.Model.ModelChatlist;
import com.example.ezhospital.Model.ModelUser;
import com.example.ezhospital.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class ChatListFragment extends Fragment {

    FirebaseAuth firebaseAuth;
    RecyclerView recyclerView;
    List<ModelChatlist> chatlistList;
    List<ModelUser> userList;
    DatabaseReference reference;
    FirebaseUser currentUser;
    AdapterChatlist adapterChatlist;

    public ChatListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat_list, container, false);

        //init
        firebaseAuth=FirebaseAuth.getInstance();
        currentUser=FirebaseAuth.getInstance().getCurrentUser();

        recyclerView=view.findViewById(R.id.recyclerView);

        chatlistList=new ArrayList<>();
        reference= FirebaseDatabase.getInstance().getReference("Chatlist").child(currentUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatlistList.clear();
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelChatlist chatlist=ds.getValue(ModelChatlist.class);
                    chatlistList.add(chatlist);
                }
                loadChats();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return view;
    }

    private void loadChats() {
        userList=new ArrayList<>();
        reference=FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    ModelUser user = ds.getValue(ModelUser.class);
                    for(ModelChatlist chatlist : chatlistList){
                        if(user.getUid() !=null && user.getUid().equals(chatlist.getId())){
                            userList.add(user);
                            break;
                        }
                    }
                    //adapter
                    adapterChatlist=new AdapterChatlist(getContext(),userList);
                    //set adapter
                    recyclerView.setAdapter(adapterChatlist);
                    //set last message
                    for(int i=0;i<userList.size();i++){
                        lastMessage(userList.get(i).getUid());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void lastMessage(final String userId) {
        DatabaseReference reference =FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String theLastMessage = "default";
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    ModelChat chat = ds.getValue(ModelChat.class);
                    if(chat==null){
                        continue;
                    }
                    String sender = chat.getSender();
                    String receiver = chat.getReceiver();
                    if(sender==null || receiver==null){
                        continue;
                    }
                    if (chat.getReceiver().equals(currentUser.getUid())
                            && chat.getSender().equals(userId) ||
                            chat.getReceiver().equals(userId) &&
                                    chat.getSender().equals(currentUser.getUid())){
                        theLastMessage=chat.getMessage();
                    }
                }
                adapterChatlist.setLastMessageHashMap(userId,theLastMessage);
                adapterChatlist.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}