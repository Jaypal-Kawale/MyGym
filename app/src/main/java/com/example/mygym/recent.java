package com.example.mygym;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import static android.content.ContentValues.TAG;

public class recent extends Fragment {
    RecyclerView recyclerView;
    String name;
    List<Client> list=new ArrayList<>();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseStorage firebaseStorage= FirebaseStorage.getInstance();
    StorageReference storageReference=firebaseStorage.getReference();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_recent,container,false);
        recyclerView=view.findViewById(R.id.rlist);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false) );
        getdata();

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
//                    if (bottom.sh.equals("none"))
//                    {
//                        try {
//                            strDate = sdf.parse(valid_until);
//                            if (System.currentTimeMillis() > strDate.getTime()) {
//                            }
//                            else
//                                list.add(c);
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
//                                if (System.currentTimeMillis() > strDate.getTime()) {
//                                }
//                                else
//                                    list.add(c);
//                            } catch (ParseException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }
//                }
//                Collections.sort(list, new Comparator<Client>() {
//                    @Override
//                    public int compare(Client client, Client t1) {
//                        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy kk:mm");
//                        Date strDate = null,str=null;
//                        String v=t1.getTimestamp();
//                        String w=client.getTimestamp();
//                        try {
//                            strDate=sdf.parse(v);
//                            str=sdf.parse(w);
//                        } catch (ParseException e) {
//                            e.printStackTrace();
//                        }
//                        return strDate.compareTo(str);
//                    }
//                });
//              //  Collections.reverse(list);
//                cadapter adapter=new cadapter(getContext(),list);
//                recyclerView.setAdapter(adapter);
//            }
//        });
        return view;
    }

    private void getdata() {
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
                                        list.add(c);
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
                                        if (System.currentTimeMillis() > strDate.getTime()) {
                                        }
                                        else
                                            list.add(c);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                           // list.add(c);
                        }
                        Collections.sort(list, new Comparator<Client>() {
                            @Override
                            public int compare(Client client, Client t1) {
                                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy kk:mm");
                                Date strDate = null,str=null;
                                String v=t1.getTimestamp();
                                String w=client.getTimestamp();
                                try {
                                    strDate=sdf.parse(v);
                                    str=sdf.parse(w);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                return strDate.compareTo(str);
                            }
                        });
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