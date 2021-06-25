package com.example.mygym;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class adapter_all_gym extends RecyclerView.Adapter<adapter_all_gym.vholder> {
    List<gym_model> list;

 Context context;

    public adapter_all_gym(List<gym_model> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public vholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(context).inflate(R.layout.item_gym_model,parent,false);
        return new vholder(v);

    }

    @Override
    public void onBindViewHolder( adapter_all_gym.vholder holder, int position) {

//        {
//            Picasso.get().load(filtered.get(position).getLink()).into(holder.imageView_hospital);
//        }
        holder.name_hospital.setText(list.get(position).getName());
        holder.addr_hospital.setText(list.get(position).getLocation());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context, gym_details.class);
                intent.putExtra("name",list.get(position).getName());
                intent.putExtra("location",list.get(position).getLocation());
            //    intent.putExtra("nam",filtered.get(position).getName());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class vholder extends RecyclerView.ViewHolder {
        TextView name_hospital;
        ImageView imageView_hospital;
        TextView addr_hospital;
        public vholder( View itemView) {
            super(itemView);
            imageView_hospital = itemView.findViewById(R.id.image_hospital4);
            addr_hospital=itemView.findViewById(R.id.te24);
            name_hospital = itemView.findViewById(R.id.te14);
        }
    }
}
