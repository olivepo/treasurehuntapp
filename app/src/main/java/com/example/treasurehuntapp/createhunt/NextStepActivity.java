package com.example.treasurehuntapp.createhunt;

import android.app.ListActivity;
import android.os.Bundle;

import com.example.treasurehuntapp.R;

import java.util.ArrayList;

import treasurehunt.model.Step;


public class NextStepActivity extends ListActivity {

    private ItemAdapter itemAdapter;

    private ArrayList<Step> stepList;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_next_step);

        itemAdapter = new ItemAdapter(NextStepActivity.this,stepList) ;
        setListAdapter(itemAdapter);


    }



}
