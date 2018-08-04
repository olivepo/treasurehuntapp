package com.example.treasurehuntapp.createhunt;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.treasurehuntapp.MapsActivity;
import com.example.treasurehuntapp.R;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;

import Utils.DateUtils;
import treasurehunt.model.AnswerChoice;
import treasurehunt.model.Course;
import treasurehunt.model.Riddle;
import treasurehunt.model.Step;
import treasurehunt.model.marshalling.JsonObjectMapperBuilder;


public class NextStepActivity extends AppCompatActivity implements View.OnClickListener {



    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_next_step);

        Button nextStepButton = findViewById(R.id.nextStepButton);
        nextStepButton.setOnClickListener(this);
        Button nextStepSubmitButton = findViewById(R.id.nextStepSubmitButton);
        nextStepSubmitButton.setOnClickListener(this);

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


        EditText latEditText = findViewById(R.id.txtStepLat);
        if (null!=lat){
            latEditText.setText(lat);
        }
        EditText longEditText = findViewById(R.id.txtStepLong);
        if (null!=longitude){
            longEditText.setText(longitude);
        }


    }



    /**
     * Called when a view has been clicked.
     *
     * @param view The view that was clicked.
     */
    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case (R.id.nextStepButton):
                finish();
                nextStep(view);
                break;

            case (R.id.nextStepSubmitButton):
                finish();
                submit(view);
                break;    

            default:
                break;
        }

    }

    private void submit(View view) {
        //appel methode PUT Course
        startActivity(new Intent(NextStepActivity.this,MapsActivity.class));
    }

    private void nextStep(View view) {

        Intent intent =getIntent();
        ObjectMapper mapper = JsonObjectMapperBuilder.buildJacksonObjectMapper();
        Course courseInCreation = null;
        if (null!=intent){
            Bundle bundle = getIntent().getExtras();
            try {
                courseInCreation = mapper.readValue(bundle.getString("startCourse"), Course.class);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        initCourseFromUI(courseInCreation);


        startActivity(new Intent(NextStepActivity.this,MapsActivity.class));
    }

    private void initCourseFromUI(Course courseIncreation) {
        Step step = null;
        EditText stepId=findViewById(R.id.txtStepId);
        step.id=stepId.getText().toString();
        EditText stepDescription=findViewById(R.id.txtStepDescription);
        step.description =stepDescription.getText().toString();
        EditText stepLat=findViewById(R.id.txtStepLat);
        step.latitude =Double.parseDouble(stepLat.getText().toString());
        EditText stepLong=findViewById(R.id.txtStepLong);
        step.longitude =Double.parseDouble(stepLong.getText().toString());
        EditText stepMaxDuration=findViewById(R.id.txtStepMaxDur);
        step.maximumDurationInMinutes =Integer.parseInt(stepMaxDuration.getText().toString());
        EditText stepScoreGiven = findViewById(R.id.txtStepScoreGiven);
        step.scorePointsGivenIfSuccess =Integer.parseInt(stepScoreGiven.getText().toString());
        step.riddle = new Riddle()   ;
        EditText stepRiddleText = findViewById(R.id.txtRidlleText);
        step.riddle.text =stepRiddleText.getText().toString();
        EditText stepRiddleJokerTxt = findViewById(R.id.txtRidlleJokerText);
        step.riddle.jokerText =stepRiddleJokerTxt.getText().toString();
        CheckBox checkBox = findViewById(R.id.checkBox);
        step.riddle.isMCQ =checkBox.isChecked();
        step.riddle.answerChoices = new ArrayList<AnswerChoice>();
        EditText stepRiddleAnswerOne = findViewById(R.id.txtRidlleAnswerOne);
        AnswerChoice answerChoiceOne = new AnswerChoice();
        answerChoiceOne.text=stepRiddleAnswerOne.getText().toString();
        CheckBox checkBoxOne = findViewById(R.id.checkBoxAnswerOne);
        answerChoiceOne.isValid=checkBoxOne.isChecked();
        step.riddle.answerChoices.add(answerChoiceOne);
        EditText stepRiddleAnswerTwo = findViewById(R.id.txtRidlleAnswerTwo);
        AnswerChoice answerChoiceTwo = new AnswerChoice();
        answerChoiceOne.text=stepRiddleAnswerTwo.getText().toString();
        CheckBox checkBoxTwo = findViewById(R.id.checkBoxAnswerOne);
        answerChoiceOne.isValid=checkBoxTwo.isChecked();
        step.riddle.answerChoices.add(answerChoiceTwo);


    }
}
