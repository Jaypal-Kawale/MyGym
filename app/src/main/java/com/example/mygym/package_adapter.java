package com.example.mygym;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class package_adapter extends RecyclerView.Adapter<package_adapter.vholder> {
   Context context;
   List<packages> list;

    public package_adapter(Context context, List<packages> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public vholder onCreateViewHolder( ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(context).inflate(R.layout.item_package,parent,false);
        return new vholder(v);
    }

    @Override
    public void onBindViewHolder(package_adapter.vholder holder, int position) {
   holder.plan.setText(list.get(position).getPlan());
   holder.money.setText(list.get(position).getMoney());
        holder.validity.setText(list.get(position).getValidity());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class vholder extends RecyclerView.ViewHolder {
        TextView plan,money,validity;
        public vholder( View itemView) {
            super(itemView);
            plan=itemView.findViewById(R.id.plan);
            money=itemView.findViewById(R.id.pack_money);
            validity=itemView.findViewById(R.id.validity);
        }
    }
}
