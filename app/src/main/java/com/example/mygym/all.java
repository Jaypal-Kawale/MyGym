package com.example.mygym;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static androidx.constraintlayout.widget.Constraints.TAG;


public class all extends Fragment {
    RecyclerView recyclerView;
    String name;
    List<Client> list=new ArrayList<>();
    List<Client> dlist=new ArrayList<>();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseStorage firebaseStorage= FirebaseStorage.getInstance();
    StorageReference storageReference=firebaseStorage.getReference();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_all,container,false);
        recyclerView=view.findViewById(R.id.clist);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false) );
      //  Log.d(TAG, "onEvent: "+getdata());
//        db.collection("client").addSnapshotListener(new EventListener<QuerySnapshot>() {
//            @Override
//            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
//                list.clear();
//                for (DocumentSnapshot doc :value)
//                {
//                    Client c=doc.toObject(Client.class);
//                   // list.add(c);
//                    String valid_until = c.getFinish();
//                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
//                    Date strDate = null;
//                    if (bottom.sh.equals("none"))
//                    {
//                        try {
//                            strDate = sdf.parse(valid_until);
//                            if (System.currentTimeMillis() > strDate.getTime()) {
//                            }
//                            else
//                            {
//                                if ((Integer.parseInt(c.getTotal()) - Integer.parseInt(c.getMoney())) <= 0)
//                                    list.add(c);
//                            }
//
//                        } catch (ParseException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                    else
//                    {
//                        if (bottom.sh.equals(c.getBatch().toLowerCase()))
//                        {
//                            try {
//                                strDate = sdf.parse(valid_until);
//                                if (System.currentTimeMillis() >strDate.getTime()) {
//                                }
//                                else
//                                {
//                                    if ((Integer.parseInt(c.getTotal()) - Integer.parseInt(c.getMoney())) <= 0)
//                                        list.add(c);
//                                }
//                            } catch (ParseException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }
//                }
//                Collections.sort(list);
//
//                cadapter adapter=new cadapter(getContext(),list);
//                recyclerView.setAdapter(adapter);
//            }
//        });
     getdata();

        return view;
    }
//    private String getdata()
//    {
//         String str="Hello" ;
//        StringBuilder stringBuilder=new StringBuilder();
//        final String[] s = new String[1];
//        db.collection("client").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//            @Override
//            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                for (DocumentSnapshot doc :queryDocumentSnapshots)
//                {
//                    Client c=doc.toObject(Client.class);
//                    // list.add(c);
//                    String valid_until = c.getFinish();
//                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
//                    Date strDate = null;
//                    Timestamp timestamp=Timestamp.now();
//                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
//                    String dateString = formatter.format(new Date(timestamp.toDate().toString()));
//                    {
//                        //  if (bottom.sh.equals(c.getBatch().toLowerCase()))
//                        {
//                            try {
//                                strDate = sdf.parse(valid_until);
//                                formatter.format(strDate);
//                                if (dateString.equals(formatter.format(strDate))) {
//                                    dlist.add(c);
//                                }
//
//                            } catch (ParseException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }
//                }
//                Log.d(TAG, "onEvent: "+dlist.get(0).getName());
//               s[0] =stringBuilder.toString();
//            }
//        });
//        if (dlist.size()<1)
//        {
//            Log.d(TAG, "onEvent: "+"size less");
//        }
//        else {
//            str=dlist.get(0).getName();
//        }
//        return str;
//    }

    private void getdata() {
        db.collection("gyms").document(FirebaseAuth.getInstance().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                list.clear();
                gym_model m=documentSnapshot.toObject(gym_model.class);
                name=m.getName();
                db.collection(name).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (DocumentSnapshot documentSnapshot :queryDocumentSnapshots)
                        {
                            Client c =documentSnapshot.toObject(Client.class);
                            String valid_until = c.getFinish();
                            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                            Date strDate = null;
                            if (bottom.sh.equals("none"))
                            {
                                try {
                                    strDate = sdf.parse(valid_until);
                                    if (System.currentTimeMillis() > strDate.getTime()) {
                                    }
                                    else
                                    {
                                        if ((Integer.parseInt(c.getTotal()) - Integer.parseInt(c.getMoney())) <= 0)
                                            list.add(c);
                                    }

                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                            else
                            {
                                if (bottom.sh.equals(c.getBatch().toLowerCase()))
                                {
                                    try {
                                        strDate = sdf.parse(valid_until);
                                        if (System.currentTimeMillis() >strDate.getTime()) {
                                        }
                                        else
                                        {
                                            if ((Integer.parseInt(c.getTotal()) - Integer.parseInt(c.getMoney())) <= 0)
                                                list.add(c);
                                        }
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                           // list.add(c);
                        }
                        Collections.sort(list);
                        cadapter adapter=new cadapter(getContext(),list,name);
                        recyclerView.setAdapter(adapter);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure( Exception e) {

            }
        });




    }
}