package com.example.mygym;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static android.content.ContentValues.TAG;

public class bottom extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseStorage firebaseStorage= FirebaseStorage.getInstance();
BottomNavigationView bottomNavigationView;
FloatingActionButton floatingActionButton;
    boolean doubleBackToExitPressedOnce = false;
String name;
public static String sh="none";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom);
        bottomNavigationView=findViewById(R.id.bottom_nav);
        floatingActionButton = findViewById(R.id.fbtn);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.collection("gyms").document(FirebaseAuth.getInstance().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        gym_model m=documentSnapshot.toObject(gym_model.class);
                        Intent intent=new Intent(bottom.this,MainActivity.class);
                        intent.putExtra("name",m.getName());
                        startActivity(intent);
                    }
                });

            }
        });
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete( Task<String> task) {
                Log.d(TAG, "onComplete:token+server side "+task.getResult());
            }
        });
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selected_fragment=null;
                switch(item.getItemId())
                {
                    case R.id.nav_home :
                        selected_fragment=new recent();
                        break;
                    case R.id.nav_destination :
                        selected_fragment=new all();
                        break;
                    case R.id.nav_treatment :
                        selected_fragment=new expire();
                        break;
                    case R.id.nav_remain :
                        selected_fragment=new unpaid();
                        break;
                    case R.id.nav_profile:
                        selected_fragment=new profile();


                }
                if (selected_fragment==null)
                    Log.d(TAG, "onNavigationItemSelected: "+"null");
                getSupportFragmentManager().beginTransaction().replace(R.id.frame,selected_fragment).commit();
                return true;
            }
        });
        if (savedInstanceState==null)
        {
            Fragment selected_fragment=null;
            selected_fragment=new recent();

            getSupportFragmentManager().beginTransaction().replace(R.id.frame,selected_fragment).commit();
            //bottomNavigationView.setCheckedItem(R.id.nav_home);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.popup_menu,menu);
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
                        R.layout.fragment_expire,
                        null);
        builder.setView(customLayout);
        RecyclerView recyclerView;
        List<Client> list=new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        recyclerView=customLayout.findViewById(R.id.elist);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.VERTICAL,false) );
//        db.collection("client").addSnapshotListener(new EventListener<QuerySnapshot>() {
//            @Override
//            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
//                list.clear();
//                for (DocumentSnapshot doc :value)
//                {
//                    Client c=doc.toObject(Client.class);
//                    String valid_until = c.getFinish();
//                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
//                    Date strDate = null;
//                   if (c.getName().toLowerCase().contains(query.toLowerCase().trim()))
//                    list.add(c);
//
//                }
//                Collections.sort(list);
//                cadapter adapter=new cadapter(getApplicationContext(),list);
//                recyclerView.setAdapter(adapter);
//            }
//        });
        db.collection("gyms").document(FirebaseAuth.getInstance().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                gym_model m=documentSnapshot.toObject(gym_model.class);
                name=m.getName();
                db.collection(name).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (DocumentSnapshot documentSnapshot :queryDocumentSnapshots)
                        {
                            Client c=documentSnapshot.toObject(Client.class);
                            String valid_until = c.getFinish();
                            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                            Date strDate = null;
                            if (c.getName().toLowerCase().contains(query.toLowerCase().trim()))
                                list.add(c);

                            // list.add(c);
                        }

                        Collections.sort(list);
                        cadapter adapter=new cadapter(getApplicationContext(),list,name);
                        recyclerView.setAdapter(adapter);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure( Exception e) {

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
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.one :
                sh="morning";
                break;
            case R.id.two :
                sh="evening";
                break;
            case R.id.three :
                sh="none";
                break;
            case R.id.send :
                callpostdialog();
                Log.d(TAG, "onOptionsItemSelected: "+"Notification clicked");
                break;
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                Intent i = new Intent(bottom.this, login.class);
                startActivity(i);
                finish();
                if (user == null) {
                   // Intent i = new Intent(bottom.this, login.class);
                   // i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                } else {
                    Log.d((String) TAG, "onAuthStateChanged:signed_out");
                }
        }
        Fragment selected_fragment=null;
        switch (bottomNavigationView.getSelectedItemId())
        {
            case R.id.nav_home :
                selected_fragment=new recent();
                break;
            case R.id.nav_destination :
                selected_fragment=new all();
                break;
            case R.id.nav_treatment :
                selected_fragment=new expire();
                break;
            case R.id.nav_remain :
                selected_fragment=new unpaid();

        }
        getSupportFragmentManager().beginTransaction().replace(R.id.frame,selected_fragment).commit();
        item.setChecked(true);
        return super.onOptionsItemSelected(item);
    }

    private void callpostdialog() {
        AlertDialog.Builder builder
                = new AlertDialog.Builder(this);
        builder.setTitle("Enter Post Message");
        final View customLayout
                = getLayoutInflater()
                .inflate(
                        R.layout.alert_editname,
                        null);
        builder.setView(customLayout);
        builder
                .setPositiveButton(
                        "Post",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(
                                    DialogInterface dialog,
                                    int which)
                            {
                                EditText editText
                                        = customLayout
                                        .findViewById(
                                                R.id.editText);
                                if (editText.getText().toString()!=null&&editText.getText().toString().length()>0)
                                {
                                   sndnotification(editText.getText().toString());
                                }
                                else {

                                    Toast.makeText(getApplicationContext(),
                                            "Enter msg",
                                            Toast.LENGTH_SHORT)
                                            .show();

                                }

                            }
                        }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        AlertDialog dialog
                = builder.create();
        dialog.show();

    }


    private void sndnotification(String msg) {
        db.collection("gyms").document(FirebaseAuth.getInstance().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                gym_model m=documentSnapshot.toObject(gym_model.class);
                name=m.getName();
                db.collection(name).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (DocumentSnapshot documentSnapshot :queryDocumentSnapshots)
                        {
                            Log.d(TAG, "onSuccess: tokens"+documentSnapshot.getId());
                          db.collection("token").document(documentSnapshot.getId()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                              @Override
                              public void onSuccess(DocumentSnapshot documentSnapshot) {
                                  refreshtoken token=documentSnapshot.toObject(refreshtoken.class);
                                  if(token!=null)
                                  {
                                      Log.d(TAG, "onSuccess: tokens is not null"+token.getToken());
                                      FcmNotificationsSender sender=new FcmNotificationsSender(token.getToken(),"GymApp",msg,getApplicationContext(),
                                              bottom.this);
                                      sender.SendNotifications();
                                      Log.d(TAG, "onSuccess: "+"token"+token.getToken());
                                  }
                                  else
                                      Log.d(TAG, "onSuccess: tokens is  null");


                              }
                          });
                            // list.add(c);
                        }

                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure( Exception e) {

            }
        });




//        String s="ffnuf4HFRGuUvxi51PHFdo:APA91bERVKm30kSkrTzFysjWMDUrM-CLDUrVj-GOpe-F9UKIXj0dtdrJPbRpihvI89WOPtiXDUSEKm9WZEeMDQsCqHT8AERVdqzuPwyNGI7Tw3T8aNUnds2-vH3AfZFuCNgxU6L-JjN-";
//       FcmNotificationsSender sender=new FcmNotificationsSender(s,"GymApp","Today is Holiday",getApplicationContext(),
//               bottom.this);
//       sender.SendNotifications();
//        Log.d(TAG, "sndnotification: "+"Notification Sent");

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