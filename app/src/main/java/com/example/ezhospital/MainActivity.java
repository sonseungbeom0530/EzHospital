package com.example.ezhospital;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.ezhospital.Common.Common;
import com.example.ezhospital.Fragment.HomeFragment;
import com.example.ezhospital.Fragment.ShoppingFragment;
import com.example.ezhospital.Model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.CollectionReference;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.bottom_navigation)
    BottomNavigationView bottomNavigationView;

    BottomSheetDialog bottomSheetDialog;

    CollectionReference userRef;

    AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(MainActivity.this);

        //userRef = FirebaseFirestore.getInstance().collection("Users");
        //dialog = new SpotsDialog.Builder().setContext(this).build();

        /*if (getIntent() != null) {
            boolean isLogin = getIntent().getBooleanExtra(Common.IS_LOGIN, false);
            if (isLogin) {
                dialog.show();
                FirebaseUser users = FirebaseAuth.getInstance().getCurrentUser();

                DocumentReference currentUser = userRef.document(users.getPhoneNumber());
                currentUser.get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                DocumentSnapshot userSnapShot = task.getResult();
                                if (!userSnapShot.exists()) {
                                    showUpdateDialog(users.getPhoneNumber());
                                } else {
                                    Common.currentUser = userSnapShot.toObject(Users.class);
                                    bottomNavigationView.setSelectedItemId(R.id.action_home);
                                }
                                if (dialog.isShowing())
                                    dialog.dismiss();
                            }
                        });
            }

        }*/

        //view
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            Fragment fragment=null;
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId()==R.id.action_home)
                    fragment=new HomeFragment();
                else if (menuItem.getItemId()==R.id.action_shopping)
                    fragment=new ShoppingFragment();

                return loadFragment(fragment);
            }
        });
        bottomNavigationView.setSelectedItemId(R.id.action_home);
    }

    private boolean loadFragment(Fragment fragment) {
        if (fragment!=null){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,fragment)
                    .commit();
            return true;
        }
        return false;
    }

    /*private void showUpdateDialog(String phoneNumber){

        bottomSheetDialog=new BottomSheetDialog(this);
        bottomSheetDialog.setTitle("One more step!");
        bottomSheetDialog.setCanceledOnTouchOutside(false);
        bottomSheetDialog.setCancelable(false);
        View sheetView=getLayoutInflater().inflate(R.layout.layout_update_information,null);

        Button btn_update=(Button)sheetView.findViewById(R.id.btn_update);
        final TextInputEditText edt_name=(TextInputEditText)sheetView.findViewById(R.id.edt_name);
        final TextInputEditText edt_address=(TextInputEditText)sheetView.findViewById(R.id.edt_address);

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!dialog.isShowing())
                    dialog.show();
                final User users= new User(edt_name.getText().toString(),
                        edt_address.getText().toString(),phoneNumber);
                userRef.document(phoneNumber)
                        .set(users)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                bottomSheetDialog.dismiss();
                                if (dialog.isShowing())
                                    dialog.dismiss();
                                Common.currentUser=users;
                                bottomNavigationView.setSelectedItemId(R.id.action_home);
                                Toast.makeText(MainActivity.this,"Thank you",Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                bottomSheetDialog.dismiss();
                                if (dialog.isShowing())
                                    dialog.dismiss();
                                Toast.makeText(MainActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        });

            }
        });
        bottomSheetDialog.setContentView(sheetView);
        bottomSheetDialog.show();

    }*/
}