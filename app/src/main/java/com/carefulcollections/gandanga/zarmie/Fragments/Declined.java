package com.carefulcollections.gandanga.zarmie.Fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.carefulcollections.gandanga.zarmie.Adapters.AcceptedOrdersAdapter;
import com.carefulcollections.gandanga.zarmie.Adapters.DeclinedOrdersAdapter;
import com.carefulcollections.gandanga.zarmie.Adapters.OrdersAdapter;
import com.carefulcollections.gandanga.zarmie.Models.Order;
import com.carefulcollections.gandanga.zarmie.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gandanga on 2019-01-31.
 */

public class Declined extends Fragment {

    private List<Order> orderList = new ArrayList<>();
    private RecyclerView recyclerView;
    private DeclinedOrdersAdapter mAdapter;
    SwipeRefreshLayout pullToRefresh;
    RecyclerView.LayoutManager mLayoutManager;
    ProgressBar progressBar;
    private static boolean refreshing = false;

    public Declined(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.current_orders, container, false);
        recyclerView = (RecyclerView)view.findViewById(R.id.recycler_view);
        mLayoutManager = new LinearLayoutManager(getContext());

        progressBar = view.findViewById(R.id.progress);
        getCurrentOrders();
        pullToRefresh = (SwipeRefreshLayout)view.findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshing = true;
                getCurrentOrders();
            }
        });


        return view;
    }
    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            progressBar.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            progressBar.setVisibility(show ? View.GONE : View.VISIBLE);
        }

    }
    private void getCurrentOrders() {
        orderList = new ArrayList<>();
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        String url = "https://zarmie.co.za/api/get-declined-orders";
        if(!refreshing){
            showProgress(true);
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                JSONArray response_obj = null;
                showProgress(false);
                try {
                    response_obj = response.getJSONArray("orders");

                    if (response_obj.length() > 0) {
                        for (int i = 0; i < response_obj.length(); i++) {
                            JSONObject obj = response_obj.getJSONObject(i);
                            Log.d("Response",obj.toString());
                            String name = obj.getJSONObject("user").optString("name");
                            String surname = obj.getJSONObject("user").optString("surname");
                            String address = obj.optString("address");
                            String phone_number = obj.optString("phone_number");
                            String item_name = obj.optString("item_name");
                            String item_category = obj.optString("item_category");
                            int quantity = obj.optInt("quantity");
                            int order_id = obj.optInt("id");
                            double prize = Double.valueOf(obj.optString("prize"));
                            String bread_type = obj.optString("bread_type");
                            String toast_type = obj.optString("toast_type");
                            String order_date = obj.optString("created_at");
                            Log.d("order date",order_date);
                            String delivery_time = obj.optString("delivery_time");
                            String delivery_or_collect = obj.optString("delivery_or_collect");
                            String extra_instructions = obj.optString("extra_instructions");;
                            List<String> ingredients = new ArrayList<>();
                            List<String> toppings = new ArrayList<>();
                            List<String> drinks = new ArrayList<>();

                            JSONArray ingrs = obj.getJSONArray("order_ingredients");
                            Log.d("order ingredients",String.valueOf(ingrs));
                            if(ingrs.length()>0){
                                for(int x=0;x<ingrs.length();x++){
                                    JSONObject cur = ingrs.getJSONObject(x);
                                    ingredients.add(cur.optString("name"));
                                }
                            }

                            JSONArray tops = obj.getJSONArray("toppings");
                            if(tops.length()>0){
                                for(int x=0;x<tops.length();x++) {
                                    JSONObject cur = tops.getJSONObject(x);
                                    toppings.add(cur.optString("name"));
                                }
                            }

                            JSONArray drinks_arr = obj.getJSONArray("drinks");
                            if(drinks_arr.length()>0){
                                for(int x=0;x<drinks_arr.length();x++) {
                                    JSONObject cur = drinks_arr.getJSONObject(x);
                                    drinks.add(cur.optString("name"));
                                }
                            }

                            Order order = new Order(order_id,name,surname,address,phone_number,item_name,item_category,quantity,bread_type,toast_type,prize,extra_instructions,delivery_time,ingredients,toppings,drinks,order_date,delivery_or_collect);
                            orderList.add(order);
                        }
                        mAdapter = new DeclinedOrdersAdapter(orderList,getContext());
                        recyclerView.setLayoutManager(mLayoutManager);
                        recyclerView.setItemAnimator(new DefaultItemAnimator());
                        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
                        recyclerView.setAdapter(mAdapter);
                        mAdapter.notifyDataSetChanged();
                        pullToRefresh.setRefreshing(false);
                        if(!refreshing){
                            showProgress(false);
                        }
                        refreshing = false;
                    }else{
                        Toast.makeText(getContext(), " There are no declined orders at the moment", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    showProgress(false);
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("error", error.toString());
                showProgress(false);
            }
        });
        requestQueue.add(request);

    }
}
