package com.example.mygym;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

public class cadapter extends RecyclerView.Adapter<cadapter.cholder> {
    Context context;
    String name;
    List<Client>list=new ArrayList<>();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseStorage firebaseStorage= FirebaseStorage.getInstance();

    public cadapter(Context context, List<Client> list) {
        this.context = context;
        this.list = (ArrayList<Client>) list;
    }

    public cadapter(Context context, List<Client> list, String name) {
        this.context = context;
        this.name = name;
        this.list = list;
    }

    @NonNull
    @Override
    public cholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(context).inflate(R.layout.item_client,parent,false);
        return new cholder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull cholder holder, int position) {
holder.iname.setText(list.get(position).name);
holder.ibatch.setText(list.get(position).batch);
holder.ifinish.setText(list.get(position).finish);
        if ((Integer.parseInt(list.get(position).getTotal())-Integer.parseInt(list.get(position).getMoney()))>0)
        {
            holder.mrs.setVisibility(View.VISIBLE);
            holder.mtxt.setVisibility(View.VISIBLE);
            holder.mrs.setText(String.valueOf(Integer.parseInt(list.get(position).getTotal())-Integer.parseInt(list.get(position).getMoney())));
        }
holder.btn.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        PopupMenu popup = new PopupMenu(context,holder.btn);
        popup.getMenuInflater().inflate(R.menu.popup,popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                db.collection("client").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                   for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots)
                   {
                       Client c=documentSnapshot.toObject(Client.class);
                       if (c.getName().equals(list.get(position).getName())&&c.getBatch().equals(list.get(position).getBatch()))
                       {
                           db.collection("client").document(documentSnapshot.getId()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                               @Override
                               public void onSuccess(Void aVoid) {
                                   Toast.makeText(context,"Client Deleted Successfully",Toast.LENGTH_SHORT).show();
                               }
                           });
                           break;
                       }
                   }
                    }
                });
                return false;
            }
        });
        popup.show();
    }
});
holder.itemView.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        Intent intent=new Intent(context, client_details.class);
        Bundle bundle=new Bundle();
      bundle.putString("receipt",list.get(position).getRno());
      bundle.putString("name",name);
        bundle.putParcelable("client",list.get(position));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtras(bundle);
        //Log.d("rno receipt", "onCreate: "+list.get(position).getRno()+" "+list.get(position).getPnomber());
        context.startActivity(intent);

    }
});
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public class cholder extends RecyclerView.ViewHolder {
        TextView iname,ibatch,ifinish,mtxt,mrs;
        ImageButton btn;
        public cholder(@NonNull View itemView) {
            super(itemView);
            iname=itemView.findViewById(R.id.iname);
           ibatch=itemView.findViewById(R.id.ibatch);
           ifinish=itemView.findViewById(R.id.ifinish);
           btn=itemView.findViewById(R.id.popbtn);
           mtxt=itemView.findViewById(R.id.mremain2);
           mrs=itemView.findViewById(R.id.mremain);
        }
    }
}
