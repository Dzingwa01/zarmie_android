package com.carefulcollections.gandanga.zarmie.Fragments;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.carefulcollections.gandanga.zarmie.Adapters.OrdersAdapter;
import com.carefulcollections.gandanga.zarmie.Home;
import com.carefulcollections.gandanga.zarmie.Models.Order;
import com.carefulcollections.gandanga.zarmie.MyBroadcastReceiver;
import com.carefulcollections.gandanga.zarmie.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.pusher.client.Pusher;
import com.pusher.client.PusherOptions;
import com.pusher.client.channel.Channel;
import com.pusher.client.channel.SubscriptionEventListener;

import static android.content.Context.ALARM_SERVICE;

/**
 * Created by Gandanga on 2019-01-31.
 */

public class CurrentOrders extends Fragment {
    private List<Order> orderList = new ArrayList<>();
    private RecyclerView recyclerView;
    private OrdersAdapter mAdapter;
    SwipeRefreshLayout pullToRefresh;
    RecyclerView.LayoutManager mLayoutManager;
    ProgressBar progressBar;
    private static boolean refreshing = false;
    String CHANNEL_ID = "MS1235";
    Context context;

    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;


    public CurrentOrders(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    public void initializePusher(){
        PusherOptions options = new PusherOptions();
        options.setCluster("eu");
        Pusher pusher = new Pusher("8787890e09bb11d146f4", options);

        Channel channel = pusher.subscribe("orders-channel");

        channel.bind("order-received-event", new SubscriptionEventListener() {
            @Override
            public void onEvent(String channelName, String eventName, final String data) {
//                System.out.println(data);
                Log.d("data received",data);
                JsonParser parser = new JsonParser();
                JsonElement element = parser.parse(data);
                Gson gson = new Gson();
                Order order = gson.fromJson(element, Order.class);
                if(!orderList.contains(order)){
                    orderList.add(0,order);
//                    setAdapter();
                    if(getActivity() != null){
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                setAdapter();
                            }
                        });
                    }
////                    alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
////                    Intent intent = new Intent(context, AlarmReceiver.class);
////                    alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
//
//                    Intent intent = new Intent(getActivity(), Home.class);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                    PendingIntent pendingIntent = PendingIntent.getActivity(getActivity(), 0, intent, 0);
//                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getContext())
//                            .setSmallIcon(R.drawable.ic_notification_icon)
//                            .setContentTitle("New Order - " +order.item_name + " - "+order.item_category)
//                            .setContentText(order.name + " " +order.surname)
//                            .setPriority(NotificationCompat.PRIORITY_DEFAULT);
//                    NotificationManager mNotificationManager =
//                            (NotificationManager) getActivity().getSystemService(getContext().NOTIFICATION_SERVICE);
//                    mBuilder.setContentIntent(pendingIntent);
//                    mNotificationManager.notify(order.id, mBuilder.build());
////                    startAlert();
                }
            }
        });

        pusher.connect();
    }

    public void startAlert() {
        int timeInSec = 2;
        Intent intent = new Intent(getActivity(), MyBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                getActivity().getApplicationContext(), 234, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() +(timeInSec*1000), pendingIntent);

    }

    public void setAdapter(){
        mAdapter = new OrdersAdapter(orderList,getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
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
        context = getContext();
        initializePusher();


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
        String url = "https://zarmie.co.za/api/get-all-orders";
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
//                        mAdapter = new OrdersAdapter(orderList,getContext());
//                        recyclerView.setLayoutManager(mLayoutManager);
//                        recyclerView.setItemAnimator(new DefaultItemAnimator());
//                        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
//                        recyclerView.setAdapter(mAdapter);
//                        mAdapter.notifyDataSetChanged();
                        pullToRefresh.setRefreshing(false);
                        setAdapter();
                        if(!refreshing){
                            showProgress(false);
                        }
                        refreshing = false;
                    }else{
                        Toast.makeText(getContext(),"There are no available orders at the moment",Toast.LENGTH_LONG).show();
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

    @Override
    public void onPause() {
        super.onPause();
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }
}
