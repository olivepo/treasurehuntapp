package com.example.treasurehuntapp.createhunt;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.treasurehuntapp.MapsActivity;
import com.example.treasurehuntapp.R;


public class NextStepActivity extends AppCompatActivity implements View.OnClickListener {



    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_next_step);

        Button nextStepButton = findViewById(R.id.nextStepButton);
        nextStepButton.setOnClickListener(this);
        Button nextStepSubmitButton = findViewById(R.id.nextStepSubmitButton);
        nextStepSubmitButton.setOnClickListener(this);


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

        startActivity(new Intent(NextStepActivity.this,MapsActivity.class));
    }
}
