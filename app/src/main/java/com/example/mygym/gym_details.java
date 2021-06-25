package com.example.mygym;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class gym_details extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    TextView hos_name,abt_hos,abt_hos_desc,hos_location,hos_lbl,rerating,reperson;
    ImageView img,starimg;
    String cname,lname,nam;
    Toolbar toolbar;
    Button btn,btnrat,btnapply;

    CardView cardView;
    private static final String TAG = "Medicine_center_details";
    RecyclerView recyclerView;
    List<gym_model> hospitals=new ArrayList<>();
    gym_model h;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gym_details);
        btn=findViewById(R.id.showmap);
         // btnrat=findViewById(R.id.showrate);
        cardView=findViewById(R.id.showrate);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        cname=getIntent().getStringExtra("name");
        lname=getIntent().getStringExtra("location");
     //   nam=getIntent().getStringExtra("nam");
        hos_name=findViewById(R.id.name_hospital);
        abt_hos=findViewById(R.id.about_hospital);
        hos_lbl=findViewById(R.id.hos_treatment_lbl);
        abt_hos_desc=findViewById(R.id.about_hospital_desc);
        hos_location=findViewById(R.id.location_hospital);
        rerating=findViewById(R.id.revrat);
        reperson=findViewById(R.id.revperson);
        starimg=findViewById(R.id.revstar);
        img=findViewById(R.id.img_hospital);
        btnapply=findViewById(R.id.btnapply);
        hospitals=client_home.list;
        h  =new gym_model();


        for(int t=0;t<hospitals.size();t++)
        {
            if (hospitals.get(t).getName().equals(cname)&&hospitals.get(t).getLocation().equals(lname))
            {
                Log.d(TAG, "onCreate: "+"matched ");
                h=hospitals.get(t);
                break;
            }
        }
       ratingfunction();


        final gym_model finalH = h;
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str= finalH.getName()+" "+ finalH.getLocation();
                Uri gmmIntentUri = Uri.parse("geo:0,0?q="+str);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });
        final gym_model finalH1 = h;

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(gym_details.this,reviewfinal.class);
                intent.putExtra("name", finalH1.getName());
                startActivity(intent);
            }
        });
        hos_name.setText(h.getName());
        abt_hos.setText("About "+h.getName());
        abt_hos_desc.setText(h.getAbout());


        packages p=new packages("Monthly","30",h.getList().get("monthly"));
        packages q=new packages("Quaterly","120",h.getList().get("quaterly"));
        List<packages>plist=new ArrayList<>();
        plist.add(p);
        plist.add(q);
        recyclerView=findViewById(R.id.hospital_details_recycler);
        package_adapter adapter=new package_adapter(gym_details.this,plist);
        recyclerView.setLayoutManager(new LinearLayoutManager(gym_details.this,LinearLayoutManager.VERTICAL,false) );
        recyclerView.setAdapter(adapter);
        //  if (!h.getLink().equals("link"))
        {
        //    Picasso.get().load(h.getLink()).into(img);
        }

        hos_location.setText(h.getLocation());


        btnapply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             //   applytogym();
                Intent intent=new Intent(gym_details.this,MainActivity.class);
                intent.putExtra("name",h.getName());
                intent.putExtra("uid",FirebaseAuth.getInstance().getUid());
                startActivity(intent);
            }
        });
    }

    private void getdata() {
        db.collection("gyms").document(FirebaseAuth.getInstance().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                h=documentSnapshot.toObject(gym_model.class);
                hos_name.setText(h.getName());
                abt_hos.setText("About "+h.getName());
                abt_hos_desc.setText(h.getAbout());
                hos_location.setText(h.getLocation());
                Log.d(TAG, "onSuccess: here is the list"+h.getList());
                packages p=new packages("Monthly","30",h.getList().get("monthly"));
                packages q=new packages("Quaterly","120",h.getList().get("quaterly"));
                List<packages>plist=new ArrayList<>();
                plist.add(p);
                plist.add(q);
                package_adapter adapter=new package_adapter(gym_details.this,plist);
                recyclerView.setLayoutManager(new LinearLayoutManager(gym_details.this,LinearLayoutManager.VERTICAL,false) );
                recyclerView.setAdapter(adapter);
                ratingfunction();
            }
        });
    }

//    private void applytogym() {
//        List<User>list=new ArrayList<>();
//        db.collection("user").document(FirebaseAuth.getInstance().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//            @Override
//            public void onSuccess(DocumentSnapshot documentSnapshot) {
//                User u=documentSnapshot.toObject(User.class);
//                list.add(u);
//                Log.d(TAG, "onSuccess: apply to gym"+u.getEmail()+u.getFullname());
//            }
//        });
//        db.collection("gyms").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//            @Override
//            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//            for (DocumentSnapshot documentSnapshot :queryDocumentSnapshots)
//            {
//                gym_model m=documentSnapshot.toObject(gym_model.class);
//                if(m.user!=null)
//                {
//                    if(m.user.isEmpty())
//                    {
//
//                        Log.d(TAG, "onSuccess:checking ulist "+"is empty"+m.user.get(0).getFullname());
//                    }
//                    else{
//                        Log.d(TAG, "onSuccess:checking ulist "+"is not empty");
//                        Log.d(TAG, "onSuccess: "+m.getUser());
//                        list.addAll(m.getUser());
//                    }
//                    list.addAll(m.getUser());
//                }
//
//
//                if(m.getName().equals("AB HEALTHCARE AND FITNESS"))
//                {
//                    documentSnapshot.getReference().update("user",list).addOnSuccessListener(new OnSuccessListener<Void>() {
//                        @Override
//                        public void onSuccess(Void unused) {
//                            Log.d(TAG, "onSuccess: "+"updated successfully");
//                        }
//                    }).addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure( Exception e) {
//                            Log.d(TAG, "onFailure: failed to upload"+e.toString());
//                        }
//                    });
//                }
//            }
//
//
//
//            }
//        });
//
//
//    }

    private void ratingfunction() {
        db.collection("reviews").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int k=0;
                float sum=0;
                int no=0;
                for (DocumentSnapshot documentSnapshot:queryDocumentSnapshots)
                {

                    user_model m=documentSnapshot.toObject(user_model.class);
                    // Log.d(TAG, "onSuccess: "+m.getHospital()+" "+h.getName());
                    if (m.getHospital().equals(h.getName()))
                    {
                        sum+=m.getRating();
                        no++;
                    }
                }
                if (no>0)
                {
                    sum=sum/no;
                    String st=String.format("%.1f",sum);
                    rerating.setText(st);
                    reperson.setText(String.valueOf(no)+".0");
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        ratingfunction();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


}