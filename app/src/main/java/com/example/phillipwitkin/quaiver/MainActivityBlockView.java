package com.example.phillipwitkin.quaiver;

import android.content.Intent;
import android.content.res.Configuration;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import net.beadsproject.beads.core.AudioContext;

import java.util.ArrayList;

import sequencer.Note;
import sequencer.VoiceController;

public class MainActivityBlockView extends AppCompatActivity {

    public static VoiceController vc;
    GridButton[] blocks;
    DatabaseManager dbManager;
    TextView sequenceName;

    boolean sequencePlaying;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Configuration config = getResources( ).getConfiguration( );
        blocks = new GridButton[8];
        modifyLayout( config );
        AudioContext ac = new AudioContext();
        if (savedInstanceState == null){
            vc = new VoiceController(ac);
            vc.addVoice("voice1");
            vc.setPrimaryVoice("voice1");
            vc.voices.get("voice1").setSequenceID(-1);
        } else {
            displayUsedBlocks();
        }
        sequencePlaying = false;


    }

    @Override
    protected void onResume(){
        super.onResume();
        displayUsedBlocks();
    }


    public void onConfigurationChanged( Configuration newConfig ) {
        Log.w( "MainActivity", "Inside onConfigurationChanged" );
        super.onConfigurationChanged( newConfig );
        modifyLayout( newConfig );
    }

    // assigns the 8 block slots to the array of GridButtons
    public void modifyLayout( Configuration newConfig ) {
        if( newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE ) {
            setContentView(R.layout.activity_main_block_view_landscape);
            blocks[0] = (GridButton) findViewById(R.id.Lblock1);
            blocks[0].setBlockNumber(0);
            blocks[1] = (GridButton) findViewById(R.id.Lblock2);
            blocks[1].setBlockNumber(1);
            blocks[2] = (GridButton) findViewById(R.id.Lblock3);
            blocks[2].setBlockNumber(2);
            blocks[3] = (GridButton) findViewById(R.id.Lblock4);
            blocks[3].setBlockNumber(3);
            blocks[4] = (GridButton) findViewById(R.id.Lblock5);
            blocks[4].setBlockNumber(4);
            blocks[5] = (GridButton) findViewById(R.id.Lblock6);
            blocks[5].setBlockNumber(5);
            blocks[6] = (GridButton) findViewById(R.id.Lblock7);
            blocks[6].setBlockNumber(6);
            blocks[7] = (GridButton) findViewById(R.id.Lblock8);
            blocks[7].setBlockNumber(7);
            sequenceName = (TextView)findViewById(R.id.LsequenceName);
        }

        else if( newConfig.orientation == Configuration.ORIENTATION_PORTRAIT ) {
            setContentView(R.layout.activity_main_block_view);
            blocks[0] = (GridButton)findViewById(R.id.block1);
            blocks[1] = (GridButton)findViewById(R.id.block2);
            blocks[2] = (GridButton)findViewById(R.id.block3);
            blocks[3] = (GridButton)findViewById(R.id.block4);
            blocks[4] = (GridButton)findViewById(R.id.block5);
            blocks[5] = (GridButton)findViewById(R.id.block6);
            blocks[6] = (GridButton)findViewById(R.id.block7);
            blocks[7] = (GridButton)findViewById(R.id.block8);
            blocks[0].setBlockNumber(0);
            blocks[1].setBlockNumber(1);
            blocks[2].setBlockNumber(2);
            blocks[3].setBlockNumber(3);
            blocks[4].setBlockNumber(4);
            blocks[5].setBlockNumber(5);
            blocks[6].setBlockNumber(6);
            blocks[7].setBlockNumber(7);
            sequenceName = (TextView)findViewById(R.id.sequenceName);
        }
    }

    // sets the blocks to be colored correctly
    public void displayUsedBlocks(){
        for (int i=0; i < 8; i++){
            blocks[i].setBackgroundColor(ContextCompat.getColor(this, R.color.blockDefault));
        }
        int numBlocksSet = vc.voices.get("voice1").getNumBlocks();
        for (int i=0; i < numBlocksSet;i++){
            blocks[i].setBackgroundColor(ContextCompat.getColor(this, R.color.blockTaken));
        }
        blocks[numBlocksSet].setBackgroundColor(ContextCompat.getColor(this, R.color.blockOpen));
    }

    // called when any of the 8 blocks are clicked
    public void onClickBlock(View v){
        if (sequencePlaying){
            return;
        }
        GridButton gb = (GridButton)v;
        int numBlocksSet = vc.voices.get("voice1").getNumBlocks();
        int blockNumClicked = gb.getBlockNumber();
        System.out.println("Block clicked: " + blockNumClicked);
        if (blockNumClicked <= numBlocksSet){
            Intent noteSelectIntent = new Intent(this, NoteSelect.class);
            noteSelectIntent.putExtra("blockNum", blockNumClicked);
            // if the block clicked was one already set, load the existing note data for that block
            if (blockNumClicked < numBlocksSet){
                Note note = vc.voices.get("voice1").getSequence().getBlocks().get(blockNumClicked);
                noteSelectIntent.putExtra("frequency", note.getFrequency());
                noteSelectIntent.putExtra("duration", note.getLength());
            }

//            startActivityForResult(noteSelectIntent, 1);
            startActivity(noteSelectIntent);
        }

    }

    // called when we return from the main activity from any of the others
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // return from add note
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            Bundle extrasBundle = data.getExtras();
            if (extrasBundle != null){
                int duration = extrasBundle.getInt("duration");
                float frequency = extrasBundle.getFloat("frequency");
                int blockNumber = extrasBundle.getInt("blockNum");
                int numBlocksSet = vc.voices.get("voice1").getNumBlocks();
                if (numBlocksSet + 1 == blockNumber){
//                    vc.voices.get("voice1").addBlock(new Note(frequency, duration));
                } else if (numBlocksSet + 1 < blockNumber){
                    vc.voices.get("voice1").replaceBlock(blockNumber, new Note(frequency, duration));
                }
            }
            displayUsedBlocks();

            // return from saving sequence
        } else if (requestCode == 2 && resultCode == RESULT_OK ){
            sequenceName.setText(vc.voices.get("voice1").getSequenceName());

            // return from loading sequence
        } else if (requestCode == 3 && resultCode == RESULT_OK ){
            System.out.println("Returned from loading sequence: " + vc.voices.get("voice1").getSequenceName());
            sequenceName.setText(vc.voices.get("voice1").getSequenceName());
            displayUsedBlocks();
        }


    }

    // starts the loadSequence activity
    public void loadSequence(View v){
        if (sequencePlaying){
            return;
        }
        Intent load = new Intent(this, LoadSequence.class);
        startActivityForResult(load, 3);
    }

    // starts the saveSequence activity
    public void saveNewSequence(View v){
        if (sequencePlaying){
            return;
        }
        Intent insertSequence = new Intent(this, SaveSequence.class);
        startActivityForResult(insertSequence, 2);
    }

    // updates the current sequence by re-saving the block data in the database
    public void updateSequence(View v){
        if (sequencePlaying){
            return;
        }
        dbManager = new DatabaseManager(this);
        int sequenceID = vc.voices.get("voice1").getSequenceID();
        if (sequenceID > 0){
            dbManager.updateSequence(sequenceID, vc.voices.get("voice1").getSequence().getBlocks());
            Toast.makeText(this, "Current blocks for " + vc.voices.get("voice1").getSequenceName() + " has been saved.", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Current Sequence has not yet been saved to Database. \n Please save sequence first.", Toast.LENGTH_LONG).show();
        }

    }

    // starts the audio playback
    public void playSequence(View v){
        if (sequencePlaying){
            return;
        }
        if (vc.voices.get("voice1").getNumBlocks() > 0){
            vc.startSequencer();
            sequencePlaying = true;
        } else {
            Toast.makeText(this, "Sequence must have at least one block set to play.", Toast.LENGTH_LONG).show();
        }

    }

    // stops audio playback
    public void stopSequence(View v){
        if (!sequencePlaying){
            return;
        }
        vc.stopSequencer();
        sequencePlaying = false;
    }


}
