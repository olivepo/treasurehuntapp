package com.example.treasurehuntapp.createhunt;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.treasurehuntapp.MapsActivity;
import com.example.treasurehuntapp.R;
import com.example.treasurehuntapp.RunthroughActivity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;

import Utils.DateUtils;
import treasurehunt.model.Course;
import treasurehunt.model.StepComposite;
import treasurehunt.model.marshalling.JsonObjectMapperBuilder;

public class CreateHuntActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_hunt);

        Button cont = findViewById(R.id.createNextStepButton);
        cont.setOnClickListener(this);

        init();

    }

    private void init() {
        Intent intent =getIntent();
        String lat =null;
        String longitude=null;
        if (null!=intent){
            Bundle bundle = getIntent().getExtras();
            lat = bundle.getString("myLocationLat");
            longitude = bundle.getString("myLocationLong");
        }


        EditText latEditText = findViewById(R.id.txStartLatitude);
        if (null!=lat){
            latEditText.setText(lat);
        }
        EditText longEditText = findViewById(R.id.txStartLongitude);
        if (null!=longitude){
            longEditText.setText(longitude);
        }


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

        Course course =initCourseFromUI();

        Intent intent = new Intent(CreateHuntActivity.this, MapsActivity.class);
        Bundle bundle = new Bundle();
        ObjectMapper mapper = JsonObjectMapperBuilder.buildJacksonObjectMapper();
        try {
            bundle.putString("startCourse", mapper.writeValueAsString(course));
        } catch (JsonProcessingException e) {
            e.printStackTrace();

        }
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private Course initCourseFromUI() {
        Course course = new Course();
        EditText name=findViewById(R.id.txtUname);
        course.name=name.getText().toString();
        course.id=name.getText().toString();
        EditText mail =findViewById(R.id.txtEmail);
        course.accountEmail=mail.getText().toString();
        EditText beginDate=findViewById(R.id.txBeginDate);
        course.begin= LocalDateTime.parse(beginDate.getText().toString(), DateUtils.formatter);
        EditText endDate=findViewById(R.id.txEndDate);
        course.end=LocalDateTime.parse(endDate.getText().toString(),DateUtils.formatter);
        EditText joker = findViewById(R.id.txJokerAllowed);
        course.jokersAllowed=Integer.parseInt(joker.getText().toString());
        EditText idStart = findViewById(R.id.txStartStepId);
        EditText latStart=findViewById(R.id.txStartLatitude);
        EditText longStart=findViewById(R.id.txStartLongitude);
        StepComposite startStep = new StepComposite();
        startStep.id=idStart.toString();
        startStep.latitude=Double.parseDouble(latStart.getText().toString());
        startStep.longitude=Double.parseDouble(longStart.getText().toString());
        course.start=startStep;
        return course;
    }
}
