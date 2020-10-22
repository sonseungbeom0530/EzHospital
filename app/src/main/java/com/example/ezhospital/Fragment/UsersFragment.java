package com.example.ezhospital.Fragment;

import android.app.ActionBar;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.ezhospital.Adapter.AdapterUsers;
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

public class UsersFragment extends Fragment {

    RecyclerView recyclerView;
    AdapterUsers adapterUsers;
    List<ModelUser> userList;
    ActionBar actionBar;
    //firebase auth
    FirebaseAuth firebaseAuth;

    public UsersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_users, container, false);

        //init
        firebaseAuth=FirebaseAuth.getInstance();
        //init recyclerview
        recyclerView=view.findViewById(R.id.users_recycleView);
        //set it's properites
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        //init user list
        userList=new ArrayList<>();

        //getAll users
        getAllUsers();

        return view;
    }

    private void getAllUsers() {
        //get current user
        final FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        //get path of database named "Users" containing users info
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        //get all data from path
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    ModelUser modelUser = ds.getValue(ModelUser.class);
                    //get all users except currently signed in user
                    //if(!modelUser.getUid().equals(fUser.getUid())){
                        userList.add(modelUser);
                  //  }
                    //adapter
                    adapterUsers=new AdapterUsers(getActivity(),userList);
                    //set adapter to recycler view
                    recyclerView.setAdapter(adapterUsers);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void searchUser(final String query) {
        //get current user
        final FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        //get path of database named "Users" containing users info
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        //get all data from path
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    ModelUser modelUser = ds.getValue(ModelUser.class);
                    //get all searched users except currently signed in user
                    if(!modelUser.getUid().equals(fUser.getUid())){
                        if(modelUser.getName().toLowerCase().contains(query.toLowerCase())||
                                modelUser.getEmail().toLowerCase().contains(query.toLowerCase())){
                            userList.add(modelUser);
                        }

                    }
                    //adapter
                    adapterUsers=new AdapterUsers(getActivity(),userList);
                    //refresh adpater
                    adapterUsers.notifyDataSetChanged();
                    //set adapter to recycler view
                    recyclerView.setAdapter(adapterUsers);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);//to show menu option in fragment
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        //inflating menu
        inflater.inflate(R.menu.menu_main,menu);
        menu.findItem(R.id.action_add_post).setVisible(false);
        //menu.findItem(R.id.action_logout).setVisible(false);

        //SearchView
        MenuItem item=menu.findItem(R.id.action_search);
        SearchView searchView =(SearchView) MenuItemCompat.getActionView(item);
        //search listner
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                //called when user press search button from keyboard
                //if search query is not empty then search
                if(!TextUtils.isEmpty(s.trim())){
                    //search text contains text, search it
                    searchUser(s);
                }else {
                    //search text empty, get all users
                    getAllUsers();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                //if search query is not empty then search
                if(!TextUtils.isEmpty(s.trim())){
                    //search text contains text, search it
                    searchUser(s);
                }else {
                    //search text empty, get all users
                    getAllUsers();
                }
                return false;
            }
        });
        super.onCreateOptionsMenu(menu,inflater);

    }




}