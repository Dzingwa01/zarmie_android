package com.carefulcollections.gandanga.zarmie.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.carefulcollections.gandanga.zarmie.Models.Order;
import com.carefulcollections.gandanga.zarmie.R;

import java.util.List;

/**
 * Created by Gandanga on 2019-02-01.
 */

public class AcceptedOrdersAdapter extends RecyclerView.Adapter<AcceptedOrdersAdapter.MyViewHolder> {

    private final List<Order> orderList;
    Context context;

    public AcceptedOrdersAdapter(List<Order> orderList,Context context) {
        this.orderList = orderList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.order_list_row,parent,false);
        return new MyViewHolder(itemView);
    }



    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Order order = orderList.get(position);
        holder.full_name.setText(order.name + " " +order.surname);
        holder.phone_number.setText("Phone Number: "+order.phone_number);
        holder.address.setText("Address: "+order.address);
        StringBuilder item = new StringBuilder();
        item.append("Order Name: " +order.item_name );
        item.append("\n");
        item.append("Size: " + order.item_category);
        item.append("\n");
        item.append("Bread: " + order.bread_type + " Toast: "+order.toast_type);

        holder.order_details.setText(item.toString());

        holder.numerics.setText("Quantity: "+String.valueOf(order.quantity) + " Total Cost: R "+ String.valueOf(order.prize));
        holder.order_date.setText("Order Date: " + order.order_date);
        if(order.extra_instructions!=null){
            holder.extra_instructions.setText("Extra Instructions - " + order.extra_instructions);
        }
        holder.delivery_details.setText(order.delivery_or_collect + " For: " + order.delivery_time);
        StringBuilder sb = new StringBuilder();
        sb.append("Ingredients: ");
        sb.append("\n");
        for(int i = 0;i<order.ingredients.size();i++){
            sb.append(order.ingredients.get(i));
            sb.append("\n");
        }
        holder.ingredients.setText(sb.toString());

        if(order.toppings.size()>0){
            StringBuilder sb_tops = new StringBuilder();
            sb_tops.append("Toppings: ");
            sb_tops.append("\n");
            for(int i = 0;i<order.toppings.size();i++){
                sb_tops.append(order.toppings.get(i));
                sb_tops.append("\n");
            }
            if(sb_tops.toString()!="Toppings: "){
                holder.toppings.setText(sb_tops.toString());
            }
        }

        if(order.drinks.size()>0){
            StringBuilder sb_drinks = new StringBuilder();
            sb_drinks.append("Drinks: ");
            sb_drinks.append("\n");
            for(int i = 0;i<order.drinks.size();i++){
                sb_drinks.append(order.drinks.get(i));
                sb_drinks.append("\n");
            }
            if(sb_drinks.toString()!="Drinks: "){
                holder.drinks.setText(sb_drinks.toString());
            }
        }
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public class MyViewHolder extends  RecyclerView.ViewHolder {

        public TextView full_name,order_details,address,phone_number,numerics,toppings,ingredients,drinks,order_date,extra_instructions,delivery_details;

        public MyViewHolder(View itemView) {
            super(itemView);
            full_name = (TextView) itemView.findViewById(R.id.full_name);
            phone_number = (TextView) itemView.findViewById(R.id.phone_number);
            address = (TextView) itemView.findViewById(R.id.address);
            order_details = (TextView) itemView.findViewById(R.id.order_details);
            numerics = (TextView)itemView.findViewById(R.id.numerics);
            toppings = (TextView)itemView.findViewById(R.id.toppings);
            ingredients = (TextView)itemView.findViewById(R.id.ingredients);
            order_date = (TextView)itemView.findViewById(R.id.order_date);
            drinks = (TextView)itemView.findViewById(R.id.drinks);
            extra_instructions = (TextView)itemView.findViewById(R.id.extra_instructions);
            delivery_details = (TextView)itemView.findViewById(R.id.delivery_details);

        }


    }
}
