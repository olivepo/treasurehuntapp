package com.example.treasurehuntapp.createhunt;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.treasurehuntapp.R;

public class CreateHuntActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_hunt);

        Button cont = findViewById(R.id.continueButton);
        cont.setOnClickListener(this);

    }

    /**
     * Called when a view has been clicked.
     *
     * @param view The view that was clicked.
     */
    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case (R.id.continueButton):
                nextStep(view);
               // finish();
                break;

            default:
                break;
        }

    }

    private void nextStep(View view) {
        startActivity(new Intent(CreateHuntActivity.this,NextStepActivity.class));
    }
}
