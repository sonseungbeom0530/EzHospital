package com.example.ezhospital;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.ezhospital.Adapter.AdapterPosts;
import com.example.ezhospital.Model.ModelPost;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ThereProfileActivity extends AppCompatActivity {


    FirebaseAuth firebaseAuth;
    //views from xml
    ImageView avatarIv,coverIv;
    TextView nameTv,emailTv,phoneTv;

    RecyclerView postsRecyclerView;

    List<ModelPost> postList;
    AdapterPosts adapterPosts;
    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_there_profile);

        ActionBar actionBar =getSupportActionBar();
        actionBar.setTitle("Profile");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        avatarIv=findViewById(R.id.avatarIv);
        coverIv=findViewById(R.id.coverIv);
        nameTv=findViewById(R.id.nameTv);
        emailTv=findViewById(R.id.emailTv);
        phoneTv=findViewById(R.id.phoneTv);
        postsRecyclerView=findViewById(R.id.recyclerview_post);

        firebaseAuth=FirebaseAuth.getInstance();

        //get uid of clicked user to retrieve his posts
        Intent intent=getIntent();
        uid=intent.getStringExtra("uid");

        Query query = FirebaseDatabase.getInstance().getReference("Users").orderByChild("uid").equalTo(uid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //check until required data get
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    //get data
                    String name = ""+ds.child("name").getValue();
                    String email=""+ds.child("email").getValue();
                    String phone =""+ds.child("phone").getValue();
                    String image = ""+ds.child("image").getValue();
                    String cover = ""+ds.child("cover").getValue();
                    //set data
                    nameTv.setText(name);
                    emailTv.setText(email);
                    phoneTv.setText(phone);

                    try{
                        //if image is received then get
                        Picasso.get().load(image).into(avatarIv);
                    }catch (Exception e){
                        //if there is any exception while getting image then set default
                        Picasso.get().load(R.drawable.ic_baseline_face_24_white).into(avatarIv);
                    }
                    try{
                        //if image is received then get
                        Picasso.get().load(cover).into(coverIv);
                    }catch (Exception e){
                        //if there is any exception while getting image then set default

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        postList=new ArrayList<>();

        checkUserStatus();
        loadHisPosts();

    }

    private void loadHisPosts() {
        //linear layout for recycler view
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        //show newest post first, for this from last
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        //set this layout to recyclerview
        postsRecyclerView.setLayoutManager(layoutManager);

        //init posts list
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
        //query to load posts
        //whenever user publishes a post the uid of this user is also saved as into of post
        // so we're retrieving post having uid equals to uid of current user
        Query query=ref.orderByChild("uid").equalTo(uid);
        //get all data from this ref
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();
                for(DataSnapshot ds :dataSnapshot.getChildren()){
                    ModelPost myPosts = ds.getValue(ModelPost.class);

                    //add to value
                    postList.add(myPosts);

                    //adapter
                    adapterPosts=new AdapterPosts(ThereProfileActivity.this,postList);
                    //set this adapter to recyclerview
                    postsRecyclerView.setAdapter(adapterPosts);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ThereProfileActivity.this,""+databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void searchHisPosts(final String searchQuery) {
        //linear layout for recycler view
        LinearLayoutManager layoutManager=new LinearLayoutManager(ThereProfileActivity.this);
        //show newest post first, for this from last
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        //set this layout to recyclerview
        postsRecyclerView.setLayoutManager(layoutManager);

        //init posts list
        DatabaseReference ref =FirebaseDatabase.getInstance().getReference("Posts");
        //query to load posts
        //whenever user publishes a post the uid of this user is also saved as into of post
        // so we're retrieving post having uid equals to uid of current user
        Query query=ref.orderByChild("uid").equalTo(uid);
        //get all data from this ref
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();
                for(DataSnapshot ds :dataSnapshot.getChildren()){
                    ModelPost myPosts = ds.getValue(ModelPost.class);

                    if(myPosts.getpTitle().toLowerCase().contains(searchQuery.toLowerCase())
                            ||myPosts.getpDescr().toLowerCase().contains(searchQuery.toLowerCase())){
                        //add to list
                        postList.add(myPosts);

                    }

                    //adapter
                    adapterPosts=new AdapterPosts(ThereProfileActivity.this,postList);
                    //set this adapter to recyclerview
                    postsRecyclerView.setAdapter(adapterPosts);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ThereProfileActivity.this,""+databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkUserStatus(){
        //get current user
        FirebaseUser user=firebaseAuth.getCurrentUser();
        if(user!=null){

        }else {
            //user not signed in, go to main activity
            startActivity(new Intent(this,LoginActivity.class));
            finish();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        menu.findItem(R.id.action_add_post).setVisible(false);
        //inflating menu

        MenuItem item = menu.findItem(R.id.action_search);
        final SearchView searchView=(SearchView) MenuItemCompat.getActionView(item);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                //called when user press search button
                if(!TextUtils.isEmpty(s)){
                    //search
                    searchHisPosts(s);
                }else {
                    loadHisPosts();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                //called whenever user type and letter
                if(!TextUtils.isEmpty(s)){
                    //search
                    searchHisPosts(s);
                }else {
                    loadHisPosts();
                }
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        //if(id==R.id.action_logout){
        //    firebaseAuth.signOut();
        //    checkUserStatus();
        //}
        return super.onOptionsItemSelected(item);
    }
}