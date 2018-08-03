package com.example.treasurehuntapp.createhunt;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.treasurehuntapp.MapsActivity;
import com.example.treasurehuntapp.R;

import java.time.LocalDateTime;

import Utils.DateUtils;

public class CreateHuntActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_hunt);

        Button cont = findViewById(R.id.createNextStepButton);
        cont.setOnClickListener(this);

        Intent intent =getIntent();
        String lat =null;
        String longitude=null;
        if (null!=intent){
            Bundle bundle = getIntent().getExtras();
            lat = bundle.getString("myLocationLat");
            longitude = bundle.getString("myLocationLong");
        }


        EditText latEditText = findViewById(R.id.txStartLatitude);
        latEditText.setText(lat);
        EditText longEditText = findViewById(R.id.txStartLongitude);
        longEditText.setText(longitude);

        EditText beginDate = findViewById(R.id.txBeginDate);
        beginDate.setText(LocalDateTime.now().format(DateUtils.formatter));

        EditText endDate = findViewById(R.id.txEndDate);
        endDate.setText(LocalDateTime.now().plusMonths(1).format(DateUtils.formatter));

    }

    /**
     * Called when a view has been clicked.
     *
     * @param view The view that was clicked.
     */
    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case (R.id.createNextStepButton):
                nextStep(view);
                finish();
                break;

            default:
                break;
        }

    }

    private void nextStep(View view) {
        startActivity(new Intent(CreateHuntActivity.this,MapsActivity.class));
    }
}
