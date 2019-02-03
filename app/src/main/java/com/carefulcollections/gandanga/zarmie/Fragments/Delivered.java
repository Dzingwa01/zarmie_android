package com.carefulcollections.gandanga.zarmie.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.carefulcollections.gandanga.zarmie.R;

/**
 * Created by Gandanga on 2019-01-31.
 */

public class Delivered extends Fragment {

    public Delivered(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.delivered, container, false);
    }
}
