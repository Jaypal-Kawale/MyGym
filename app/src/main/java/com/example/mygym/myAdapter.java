package com.example.mygym;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class myAdapter extends RecyclerView.Adapter<myAdapter.myholder> {
    Context context;
    List<user_model> rev=new ArrayList<>();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String n;
    final String sn=  FirebaseAuth.getInstance().getCurrentUser().getEmail().replaceFirst("@gmail.com","");
    public myAdapter(Context context, List<user_model> rev, String n) {
        this.context = context;
        this.rev=rev;
        this.n=n;
    }

    @NonNull
    @Override
    public myholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(context).inflate(R.layout.item_review_model,parent,false);
        return new myholder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final myholder holder, final int position) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String dateString = formatter.format(new Date(rev.get(position).getTimestamp().toDate().toString()));
        holder.name.setText(rev.get(position).getName());
        holder.rating.setRating(rev.get(position).getRating());
        holder.time.setText(String.valueOf(dateString));
        holder.revi.setText(rev.get(position).getReview());
        holder.btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(context, holder.btn);
                popup.getMenuInflater().inflate(R.menu.popup_menu2,popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId())
                        {
                            case R.id.two :
                                call_inappropriate(rev.get(position).getName(),rev.get(position).getHospital());
                                Toast.makeText(context,"Review Flaged as inappropriate",Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.three :
                                call_inappropriate(rev.get(position).getName(),rev.get(position).getHospital());
                                Toast.makeText(context,"Review Flagged as spam",Toast.LENGTH_SHORT).show();
                                break;
                        }
                        return false;
                    }
                });
                popup.show();
            }
        });
    }

    private void call_inappropriate(final String user1, final String hos) {
        spam s=new spam(user1,hos,sn);
        db.collection("spam").add(s).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
               // Toast.makeText(context,"Review Flaged as inappropriate",Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
               // Toast.makeText(context,"Review failed to Flag as inappropriate",Toast.LENGTH_SHORT).show();
            }
        });

        db.collection("spam").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int count=0,k=0;
                for (DocumentSnapshot documentSnapshot :queryDocumentSnapshots)
                {
                    spam s=documentSnapshot.toObject(spam.class);
                    if (s.getHospital().equals(hos)&&s.getUserposted().equals(user1))
                    {
                        k++;
                      //  Log.d("in spam", "onSuccess: "+s.getHospital());
                    }

                }
                if (k>=3)
                {
                    Log.d("in spam", "onSuccess: "+k);
                    call_delete_review(user1,hos);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
               // Toast.makeText(context,"failed",Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void call_delete_review(final String user1, final String hos) {
        db.collection("reviews").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (DocumentSnapshot documentSnapshot :queryDocumentSnapshots)
                        {
                            user_model d=documentSnapshot.toObject(user_model.class);
                            if (d.getHospital().equals(hos)&&d.getName().equals(user1))
                            {
                              //  Log.d("in spam", "onSuccess: "+d.getName()+d.getHospital());
                                db.collection("reviews").document(documentSnapshot.getId()).delete()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                              //  Toast.makeText(context,"Review deleted Successfully",Toast.LENGTH_SHORT).show();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        //Toast.makeText(context,"failed to delete review",Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
               // Toast.makeText(context,"failed to retrive data",Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return rev.size();
    }

    public class myholder extends RecyclerView.ViewHolder {
        TextView name,time,revi;
        RatingBar rating;
        ImageButton btn;
        public myholder(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.rname);
            rating=itemView.findViewById(R.id.rrating);
            time=itemView.findViewById(R.id.rdate);
            revi=itemView.findViewById(R.id.rreview);
            btn=itemView.findViewById(R.id.rpopup);
        }
    }
}