package com.example.treasurehuntapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;

import treasurehunt.model.AnswerChoice;
import treasurehunt.model.Riddle;
import treasurehunt.model.marshalling.JsonObjectMapperBuilder;

public class RiddleActivity extends AppCompatActivity {

    private Riddle riddle;
    private Intent resultIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_riddle);

        // récupération de l'énigme
        Bundle bundle = getIntent().getExtras();
        ObjectMapper mapper = JsonObjectMapperBuilder.buildJacksonObjectMapper();
        int jokersLeft = 0;
        if (bundle == null) {
            finish();
        }
        else
        {
            jokersLeft = bundle.getInt("jokersLeft");
            try {
                riddle = mapper.readValue(bundle.getString("serializedRiddle"),Riddle.class);
            } catch (IOException e) {
                e.printStackTrace();
                finish();
            }
        }

        setTitle("Etape atteinte !");
        TextView riddleText = findViewById(R.id.riddleText);
        riddleText.setText(riddle.text);
        Button useJoker = findViewById(R.id.useJoker);
        if (jokersLeft > 0) {
            useJoker.setText(String.format("Utiliser un joker (%d restant)",jokersLeft));
        } else {
            useJoker.setAlpha(.5f);
            useJoker.setClickable(false);
        }
        useJoker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resultIntent.putExtra("jokerUsed", true);
                setResult(Activity.RESULT_CANCELED, resultIntent);
                finish();
            }
        });

        ListView answersListView = findViewById(R.id.answersListView);
        answersListView.setAdapter(new CustomAdapter((ArrayList<AnswerChoice>) riddle.answerChoices, RiddleActivity.this));

        // réponse par défaut
        resultIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, resultIntent);

    }

    private class CustomAdapter extends BaseAdapter implements ListAdapter {
        private ArrayList<AnswerChoice> list = new ArrayList<AnswerChoice>();
        private Context context;

        public CustomAdapter(ArrayList<AnswerChoice> list, Context context) {
            this.list = list;
            this.context = context;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int pos) {
            return list.get(pos);
        }

        @Override
        public long getItemId(int pos) {
            return 0;
            //just return 0 if your list items do not have an Id variable.
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.riddle_answer_layout, null);
            }

            //Handle buttons and add onClickListeners
            Button callbtn= (Button)view.findViewById(R.id.answerButton);
            callbtn.setText(list.get(position).text);
            EditText isAnswerValid = (EditText) view.findViewById(R.id.isAnswerValid);
            isAnswerValid.setText(list.get(position).isValid ? "1" : "0");

            callbtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    View parentView = (View) v.getParent();
                    EditText isAnswerValid = parentView.findViewById(R.id.isAnswerValid);

                    boolean isValid = isAnswerValid.getText().toString().equals("1");

                    // préparation de l'intent de resultat
                    resultIntent.putExtra("jokerUsed", false);
                    setResult((isValid ? Activity.RESULT_OK : Activity.RESULT_CANCELED), resultIntent);

                    AlertDialog.Builder builder = new AlertDialog.Builder(RiddleActivity.this);
                    builder.setMessage((isValid ? "Bonne réponse :-)" : "Mauvaise réponse :-/"))
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    finish();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();

                }
            });

            return view;
        }
    }

}
