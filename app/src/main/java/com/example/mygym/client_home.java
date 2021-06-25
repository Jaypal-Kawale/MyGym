package com.example.mygym;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class client_home extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    private static final String TAG = "ALL Gym";
  public static   List<gym_model> list;
    boolean doubleBackToExitPressedOnce = false;
    RecyclerView recyclerView;
    SearchView searchView;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseStorage firebaseStorage= FirebaseStorage.getInstance();
    StorageReference storageReference=firebaseStorage.getReference();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_home);
        FirebaseMessaging.getInstance().subscribeToTopic("all");
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete( Task<String> task) {
                Log.d(TAG, "onComplete:token "+task.getResult());
                String token=task.getResult();
//                db.collection("token").document(FirebaseAuth.getInstance().getUid()).set(token).addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void unused) {
//                        Log.d(TAG, "onSuccess: "+"token Added Successfully");
//                    }
//                });
                db.collection("token").document(FirebaseAuth.getInstance().getUid()).update("token",token).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "onSuccess: "+"token updated Successfully");
                    }
                });
            }
        });

        bottomNavigationView=findViewById(R.id.bottom_nav);
        list=new ArrayList<>();
        getAllgyms();
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selected_fragment=null;
                switch(item.getItemId())
                {
                    case R.id.nav_allgym:
                        selected_fragment=new all_gym();
                        break;
                    case R.id.nav_workout:
                        selected_fragment=new workout();
                        break;
                    case R.id.nav_mygym:
                        selected_fragment=new mygym();
                        break;
                    case R.id.nav_profile:
                        selected_fragment=new client_profile();

                }
                getSupportFragmentManager().beginTransaction().replace(R.id.frame,selected_fragment).commit();
                return true;
            }
        });
        if (savedInstanceState==null)
        {
            Fragment selected_fragment=null;
            selected_fragment=new all_gym();

            getSupportFragmentManager().beginTransaction().replace(R.id.frame,selected_fragment).commit();
            //bottomNavigationView.setCheckedItem(R.id.nav_home);

        }
    }
    private void getAllgyms() {
        db.collection("gyms")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                gym_model d=document.toObject(gym_model.class);
                                list.add(d);
                                //   Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.searchmenu,menu);
        MenuItem menuItem=menu.findItem(R.id.search);
        SearchView searchView=(SearchView)menuItem.getActionView();
        searchView.setQueryHint("search here");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                callsearch(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //callsearch(newText);
                return false;
            }
        });
        return true;
    }

    private void callsearch(String query) {
        AlertDialog.Builder builder
                = new AlertDialog.Builder(this);
        builder.setTitle("Search Results ");
        final View customLayout
                = getLayoutInflater()
                .inflate(
                        R.layout.fragment_all_gym,
                        null);
        builder.setView(customLayout);
        RecyclerView recyclerView;
        List<gym_model> list=new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        recyclerView=customLayout.findViewById(R.id.recycler_medicine_centers);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.VERTICAL,false) );
        db.collection("gyms").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot documentSnapshot :queryDocumentSnapshots)
                {
                    gym_model c=documentSnapshot.toObject(gym_model.class);
                    if (c.getName().toLowerCase().contains(query.toLowerCase().trim()))
                        list.add(c);

                    // list.add(c);
                }

                adapter_all_gym madapter=new adapter_all_gym(list,client_home.this);
                recyclerView.setAdapter(madapter);
            }
        });
        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        AlertDialog dialog
                = builder.create();
        dialog.show();

    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            //super.onBackPressed();
            finishAffinity();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "press again to exit application", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

}