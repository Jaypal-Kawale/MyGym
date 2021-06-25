package com.example.mygym;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class mygym extends Fragment {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    TextView hos_name,abt_hos,abt_hos_desc,hos_location,hos_lbl,rerating,reperson;
    ImageView img,starimg;
    String cname,lname,nam;
    Toolbar toolbar;
    Button btn,btnrat,btnlogout;

    CardView cardView;
    private static final String TAG = "Medicine_center_details";
    RecyclerView recyclerView;
    List<gym_model> hospitals=new ArrayList<>();
    gym_model g;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_mygym, container, false);
        btn=view.findViewById(R.id.showmap);
        // btnrat=findViewById(R.id.showrate);
        cardView=view.findViewById(R.id.showrate);
        hos_name=view.findViewById(R.id.name_hospital);
        abt_hos=view.findViewById(R.id.about_hospital);
        hos_lbl=view.findViewById(R.id.hos_treatment_lbl);
        abt_hos_desc=view.findViewById(R.id.about_hospital_desc);
        hos_location=view.findViewById(R.id.location_hospital);
        rerating=view.findViewById(R.id.revrat);
        reperson=view.findViewById(R.id.revperson);
        starimg=view.findViewById(R.id.revstar);
        img=view.findViewById(R.id.img_hospital);
        btnlogout=view.findViewById(R.id.btnlogout);


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str= g.getName()+" "+ g.getLocation();
                Uri gmmIntentUri = Uri.parse("geo:0,0?q="+str);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getContext(),reviewfinal.class);
                intent.putExtra("name", g.getName());
                startActivity(intent);
            }
        });

        recyclerView=view.findViewById(R.id.hospital_details_recycler);
        getdata();
        return view;
    }

    private void getdata() {
        db.collection("gyms").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
              for(DocumentSnapshot documentSnapshot :queryDocumentSnapshots)
              {
                  gym_model h=documentSnapshot.toObject(gym_model.class);
                  g=documentSnapshot.toObject(gym_model.class);

                  db.collection(h.getName()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                      @Override
                      public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                         for (DocumentSnapshot doc :queryDocumentSnapshots)
                         {
                             if(doc.getId().equals(FirebaseAuth.getInstance().getUid()))
                             {
                                 Log.d(TAG, "onSuccess: "+"matched");

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
                                 package_adapter adapter=new package_adapter(getContext(),plist);
                                 recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false) );
                                 recyclerView.setAdapter(adapter);
                                 ratingfunction(h);
                                 break;
                             }
                             Log.d(TAG, "onSuccess: "+"not matched");
                         }
                      }
                  });
              }
            }
        });

    }
    private void ratingfunction(gym_model h) {
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

}