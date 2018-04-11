package com.example.phillipwitkin.quaiver;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import sequencer.Note;

public class LoadSequence extends Activity {

    DatabaseManager dbManager;
    RecyclerView recyclerView;
    RecycleAdapter recycler;
    List<DataModel> dataModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbManager = new DatabaseManager(this);
        setContentView(R.layout.sequence_load_view);
        dataModel = dbManager.getAllSequences();
        this.recyclerView = (RecyclerView)findViewById(R.id.recycleView);
        // the recycler holds the method which is called when any of the items are clicked
        recycler = new RecycleAdapter(dataModel, new RecycleAdapter.OnItemClickListener(){
            @Override public void onItemClick(DataModel item){
                System.out.println("load sequence item clicked");
                MainActivityBlockView.vc.voices.get("voice1").setSequenceName(item.getSequenceName());
                MainActivityBlockView.vc.voices.get("voice1").setSequenceID(item.getSequenceID());
                selectSequence(item.getSequenceID(), item.getSequenceName());
            }
        });
        RecyclerView.LayoutManager reLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(reLayoutManager);
        recyclerView.setAdapter(recycler);
        if (dataModel.size() < 1){
            Toast.makeText(this, "No saved sequences yet.", Toast.LENGTH_LONG).show();
        }
    }

    public void selectSequence(int sequenceID, String sequenceName){
        MainActivityBlockView.vc.voices.get("voice1").getSequence().removeAllBlocksDirectly();
        MainActivityBlockView.vc.voices.get("voice1").setSequenceID(sequenceID);
        MainActivityBlockView.vc.voices.get("voice1").setSequenceName(sequenceName);
        ArrayList<Note> newNotes = dbManager.selectNotesForSequence(sequenceID);
        for (Note note : newNotes){
            MainActivityBlockView.vc.voices.get("voice1").addBlock(note);
        }
        Intent output = new Intent();
        setResult(RESULT_OK, output);
        this.finish();
    }
}
