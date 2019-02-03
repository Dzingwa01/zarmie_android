package com.carefulcollections.gandanga.zarmie.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.carefulcollections.gandanga.zarmie.Models.Order;
import com.carefulcollections.gandanga.zarmie.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Gandanga on 2019-01-28.
 */

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.MyViewHolder> {

    private final List<Order> orderList;
    Context context;

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

    public class MyViewHolder extends  RecyclerView.ViewHolder implements View.OnClickListener {

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

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
//            Toast.makeText(itemView.getContext(),"Position "+ getLayoutPosition(),Toast.LENGTH_LONG).show();
            final Order order = orderList.get(getLayoutPosition());
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
            alertDialogBuilder.setMessage("Do You Want Accept or Decline Order for " +order.name +" "+order.surname + " - " +order.item_name +"( "+order.item_category + " )");
                    alertDialogBuilder.setPositiveButton("Accept",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
//                                    Toast.makeText(context,"You clicked accept button",Toast.LENGTH_LONG).show();
                                    AcceptOrder(order);
                                }
                            });

            alertDialogBuilder.setNegativeButton("Decline",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    declineOrder(order);
//                    Toast.makeText(context,"You clicked decline button",Toast.LENGTH_LONG).show();
                }
            });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
    }


    public void declineOrder(final Order order){

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        String url = "https://zarmie.co.za/api/decline-order/"+order.id;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                String response_obj = null;
                try {
                    response_obj = response.getString("message");
                    Toast.makeText(context,response_obj,Toast.LENGTH_LONG).show();
                    orderList.remove(order);
                    notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("error", error.toString());
            }
        });
        requestQueue.add(request);

    }


    public void AcceptOrder(final Order order){

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        String url = "https://zarmie.co.za/api/accept-order/"+order.id;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                String response_obj = null;
                try {

                    response_obj = response.getString("message");
                    Toast.makeText(context,response_obj,Toast.LENGTH_LONG).show();
                    orderList.remove(order);
                    notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("error", error.toString());
            }
        });
        requestQueue.add(request);

    }

    public OrdersAdapter(List<Order> orderList,Context context){
        this.orderList = orderList;
        this.context = context;
    }
}
