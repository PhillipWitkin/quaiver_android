package com.example.phillipwitkin.quaiver;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.sun.tools.apt.Main;

public class SaveSequence extends AppCompatActivity {

    DatabaseManager dbManager;
    EditText sequenceName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbManager = new DatabaseManager(this);
        sequenceName = (EditText)findViewById(R.id.editSequenceName);
        setContentView(R.layout.activity_save_sequence);
    }

    public void saveSequence(View v){
        sequenceName = (EditText)findViewById(R.id.editSequenceName);
        String sequenceTitle = sequenceName.getText().toString();
        int seqID = dbManager.insertSequence(sequenceTitle, MainActivityBlockView.vc.voices.get("voice1").getSequence().getBlocks());
        if (seqID == -1){
            Toast.makeText(this, "Sequence Name Already Taken", Toast.LENGTH_LONG).show();
        } else {
            MainActivityBlockView.vc.voices.get("voice1").setSequenceName(sequenceTitle);
            MainActivityBlockView.vc.voices.get("voice1").setSequenceID(seqID);
            Toast.makeText(this, "Sequence " + sequenceTitle + " added to database", Toast.LENGTH_LONG).show();
            Intent output = new Intent();
            setResult(RESULT_OK, output);
            this.finish();
        }
    }
}
