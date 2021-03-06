package com.example.treasurehuntapp.createhunt;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.treasurehuntapp.MapsActivity;
import com.example.treasurehuntapp.R;
import com.example.treasurehuntapp.client.AppContext;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import treasurehunt.client.CourseRESTMethods;
import treasurehunt.model.AnswerChoice;
import treasurehunt.model.Course;
import treasurehunt.model.Riddle;
import treasurehunt.model.StepComposite;
import treasurehunt.model.StepCompositeFactory;
import treasurehunt.model.StepLeaf;
import treasurehunt.model.StepLeafFactory;
import treasurehunt.model.marshalling.JsonObjectMapperBuilder;
import treasurehunt.sqlite.CoursePersistentFactory;
import treasurehunt.sqlite.PersistentObject;
import treasurehunt.sqlite.PersistenceManager;


public class NextStepActivity extends AppCompatActivity implements View.OnClickListener {

    private AppContext appContext;

    private NextStepActivity.CreateCourseTask mCourseTask;



    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_next_step);

        appContext = AppContext.getInstance(NextStepActivity.this);
        appContext.stepsMap =new HashMap<>();

        Button nextStepButton = findViewById(R.id.nextStepButton);
        nextStepButton.setOnClickListener(this);
        Button nextStepSubmitButton = findViewById(R.id.nextStepSubmitButton);
        nextStepSubmitButton.setOnClickListener(this);

        init();


    }

    private void init() {

        Intent intent = getIntent();
        String lat = null;
        String longitude = null;
        if (null != intent) {
            Bundle bundle = getIntent().getExtras();
            lat = bundle.getString("myLocationLat");
            longitude = bundle.getString("myLocationLong");
        }


        EditText latEditText = findViewById(R.id.txtStepLat);
        if (null != lat) {
            latEditText.setText(lat);
        }
        EditText longEditText = findViewById(R.id.txtStepLong);
        if (null != longitude) {
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
                nextStep(view);
                break;

            case (R.id.nextStepSubmitButton):
                submit(view);
                finish();
                break;

            default:
                break;
        }

    }

    private void submit(View view) {
        //appel methode PUT Course
        Intent intent = new Intent(NextStepActivity.this, MapsActivity.class);
        ObjectMapper mapper = JsonObjectMapperBuilder.buildJacksonObjectMapper();
        Bundle bundle = getIntent().getExtras();
        Course courseInCreation = null;

        if (null != bundle) {

            try {
                courseInCreation = mapper.readValue(bundle.getString("startCourse"), Course.class);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        initLeafCourseFromUI(courseInCreation);

        mCourseTask=new CreateCourseTask(courseInCreation);
        mCourseTask.execute();

        //enregistrement dans la base sqlite
        putToSqliteDb(courseInCreation);

        startActivity(intent);

        finish();
    }

    private void putToSqliteDb(Course courseInCreation) {
        PersistenceManager persistenceManager = new PersistenceManager(this);
        PersistentObject<Course> coursePO = new CoursePersistentFactory().makePersistentObject(courseInCreation.id,courseInCreation);
        coursePO.toUpdate = true;
        persistenceManager.insertOrUpdateObject(coursePO);

    }

    private void nextStep(View view) {

        Intent intent = new Intent(NextStepActivity.this, MapsActivity.class);
        ObjectMapper mapper = JsonObjectMapperBuilder.buildJacksonObjectMapper();
        Bundle bundle = getIntent().getExtras();
        Course courseInCreation = null;

        if (null != bundle) {

            try {
                courseInCreation = mapper.readValue(bundle.getString("startCourse"), Course.class);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        initStepCourseFromUI(courseInCreation);

        try {
            bundle.putString("startCourse", mapper.writeValueAsString(courseInCreation));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        intent.putExtras(bundle);

        startActivity(intent);

        finish();
    }



    private void initStepCourseFromUI(Course courseIncreation) {

        EditText stepId = findViewById(R.id.txtStepId);
        String id = stepId.getText().toString();

        EditText stepLat = findViewById(R.id.txtStepLat);
        double latitude = Double.parseDouble(stepLat.getText().toString());

        EditText stepLong = findViewById(R.id.txtStepLong);
        double longitude = Double.parseDouble(stepLong.getText().toString());

        StepComposite step = (StepComposite) new StepCompositeFactory().createInstance(id, latitude, longitude);

        EditText stepDescription = findViewById(R.id.txtStepDescription);
        step.description = stepDescription.getText().toString();

        EditText stepMaxDuration = findViewById(R.id.txtStepMaxDur);
        step.maximumDurationInMinutes = Integer.parseInt(stepMaxDuration.getText().toString());

        EditText stepScoreGiven = findViewById(R.id.txtStepScoreGiven);
        step.scorePointsGivenIfSuccess = Integer.parseInt(stepScoreGiven.getText().toString());

        step.riddle = new Riddle();

        EditText stepRiddleText = findViewById(R.id.txtRidlleText);
        step.riddle.text = stepRiddleText.getText().toString();

        EditText stepRiddleJokerTxt = findViewById(R.id.txtRidlleJokerText);
        step.riddle.jokerText = stepRiddleJokerTxt.getText().toString();

        CheckBox checkBox = findViewById(R.id.checkBox);
        step.riddle.isMCQ = checkBox.isChecked();

        step.riddle.answerChoices = new ArrayList<AnswerChoice>();

        EditText stepRiddleAnswerOne = findViewById(R.id.txtRidlleAnswerOne);
        AnswerChoice answerChoiceOne = new AnswerChoice();
        answerChoiceOne.text = stepRiddleAnswerOne.getText().toString();
        CheckBox checkBoxOne = findViewById(R.id.checkBoxAnswerOne);
        answerChoiceOne.isValid = checkBoxOne.isChecked();
        step.riddle.answerChoices.add(answerChoiceOne);

        EditText stepRiddleAnswerTwo = findViewById(R.id.txtRidlleAnswerTwo);
        AnswerChoice answerChoiceTwo = new AnswerChoice();
        answerChoiceTwo.text = stepRiddleAnswerTwo.getText().toString();
        CheckBox checkBoxTwo = findViewById(R.id.checkBoxAnswerTwo);
        answerChoiceTwo.isValid = checkBoxTwo.isChecked();
        step.riddle.answerChoices.add(answerChoiceTwo);


        // ajout comme suivante à la dernière étape créée
       // appContext.courseInCreationLastCreatedStep.addStep(step);

        appContext.stepsMap.put(appContext.nbStep,step);
        appContext.nbStep++;



       /* if (step instanceof StepComposite) {
            appContext.courseInCreationLastCreatedStep = step; // l'étape créée devient la nouvelle dernière étape créée.
        }*/






    }

    private void initLeafCourseFromUI(Course courseIncreation) {

        EditText stepId = findViewById(R.id.txtStepId);
        String id = stepId.getText().toString();

        EditText stepLat = findViewById(R.id.txtStepLat);
        double latitude = Double.parseDouble(stepLat.getText().toString());

        EditText stepLong = findViewById(R.id.txtStepLong);
        double longitude = Double.parseDouble(stepLong.getText().toString());

        StepLeaf step = (StepLeaf) new StepLeafFactory().createInstance(id, latitude, longitude);

        EditText stepDescription = findViewById(R.id.txtStepDescription);
        step.description = stepDescription.getText().toString();

        EditText stepMaxDuration = findViewById(R.id.txtStepMaxDur);
        step.maximumDurationInMinutes = Integer.parseInt(stepMaxDuration.getText().toString());

        EditText stepScoreGiven = findViewById(R.id.txtStepScoreGiven);
        step.scorePointsGivenIfSuccess = Integer.parseInt(stepScoreGiven.getText().toString());

        step.riddle = new Riddle();

        EditText stepRiddleText = findViewById(R.id.txtRidlleText);
        step.riddle.text = stepRiddleText.getText().toString();

        EditText stepRiddleJokerTxt = findViewById(R.id.txtRidlleJokerText);
        step.riddle.jokerText = stepRiddleJokerTxt.getText().toString();

        CheckBox checkBox = findViewById(R.id.checkBox);
        step.riddle.isMCQ = checkBox.isChecked();

        step.riddle.answerChoices = new ArrayList<AnswerChoice>();

        EditText stepRiddleAnswerOne = findViewById(R.id.txtRidlleAnswerOne);
        AnswerChoice answerChoiceOne = new AnswerChoice();
        answerChoiceOne.text = stepRiddleAnswerOne.getText().toString();
        CheckBox checkBoxOne = findViewById(R.id.checkBoxAnswerOne);
        answerChoiceOne.isValid = checkBoxOne.isChecked();
        step.riddle.answerChoices.add(answerChoiceOne);

        EditText stepRiddleAnswerTwo = findViewById(R.id.txtRidlleAnswerTwo);
        AnswerChoice answerChoiceTwo = new AnswerChoice();
        answerChoiceTwo.text = stepRiddleAnswerTwo.getText().toString();
        CheckBox checkBoxTwo = findViewById(R.id.checkBoxAnswerTwo);
        answerChoiceTwo.isValid = checkBoxTwo.isChecked();
        step.riddle.answerChoices.add(answerChoiceTwo);


        //on ajoute les steps suivants pour chacun
        for (int i = 0; i< appContext.stepsMap.size(); ++i)
            if (appContext.stepsMap.containsKey(i)&&appContext.stepsMap.containsKey(i+1)){
            appContext.stepsMap.get(i).addStep(appContext.stepsMap.get(i+1));
            }
            else{
                appContext.stepsMap.get(i).addStep(step);
            }

        //si la map est vide, start a pour suivant stepLeaf
        if (appContext.stepsMap.size()==0)  {
            courseIncreation.start.addStep(step);
        }
        else{
            courseIncreation.start.addStep(appContext.stepsMap.get(0));
        }


    }



    /**
     * Represents an asynchronous login/registration task used to get the nearest course
     * the user.
     */
    private class CreateCourseTask extends AsyncTask<Void, Void, Boolean> {

        private Course course;

        CreateCourseTask(Course course) {
            this.course = course;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {

                CourseRESTMethods.put(appContext.getRequestQueue(), course);
            } catch (Exception e) {
                return false;
            }

            return true;

        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mCourseTask = null;
            //   showProgress(false);

            if (success) {
                Toast.makeText(NextStepActivity.this, "la chasse "+course.name+" is created :", Toast.LENGTH_LONG).show();
                Bundle bundle = getIntent().getExtras();
                ObjectMapper mapper = JsonObjectMapperBuilder.buildJacksonObjectMapper();
                try {
                    bundle.putString("startCourse", mapper.writeValueAsString(new Course()));
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }

            }
        }


        protected void onCancelled() {
            mCourseTask = null;
            //  showProgress(false);
        }
    }

}
