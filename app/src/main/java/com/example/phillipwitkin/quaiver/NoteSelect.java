package com.example.phillipwitkin.quaiver;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import net.beadsproject.beads.data.Pitch;

import sequencer.Note;


public class NoteSelect extends AppCompatActivity {

    Spinner pitchSelect;
    Spinner durationSelect;
    float frequency =0;
    int duration=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_select);

        Spinner pitchSelect = (Spinner) findViewById(R.id.pitchSpinner);
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapterPitch = ArrayAdapter.createFromResource(this,
                R.array.Pitches, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapterPitch.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        pitchSelect.setAdapter(adapterPitch);
        pitchSelect.setOnItemSelectedListener(new PitchSpinner());

        Spinner durationSelect = (Spinner) findViewById(R.id.durationSpinner);
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapterDuration = ArrayAdapter.createFromResource(this,
                R.array.times, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapterDuration.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        durationSelect.setAdapter(adapterDuration);
        durationSelect.setOnItemSelectedListener(new DurationSpinner());

        Intent current = getIntent();
        if (current.hasExtra("frequency")){
            int midiNum = (int)Pitch.ftom(current.getFloatExtra("frequency", 0));
            int distanceFromC = midiNum % 12;
            int octave = (midiNum / 12)-1 ;
            String noteString = "";
            switch (distanceFromC){
                case 0:
                    noteString="C ";
                    break;
                case 1:
                    noteString="C# ";
                    break;
                case 2:
                    noteString="D ";
                    break;
                case 3:
                    noteString="D# ";
                    break;
                case 4:
                    noteString="E ";
                    break;
                case 5:
                    noteString="F ";
                    break;
                case 6:
                    noteString="F# ";
                    break;
                case 7:
                    noteString="G ";
                    break;
                case 8:
                    noteString="G# ";
                    break;
                case 9:
                    noteString="A ";
                    break;
                case 10:
                    noteString="A# ";
                    break;
                case 11:
                    noteString="B ";
                    break;

            }
            noteString += octave;
            int pos = adapterPitch.getPosition(noteString);
            pitchSelect.setSelection(pos);
        }
        if (current.hasExtra("duration")){
            String duration = current.getIntExtra("duration",0)+"";
            int pos = adapterDuration.getPosition(duration);
            durationSelect.setSelection(pos);
        }
    }

    public class PitchSpinner extends Activity implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view,
                                   int pos, long id) {

            String selected = parent.getItemAtPosition(pos).toString();
            String[] note = selected.split("\\s+");
            int octave = Integer.parseInt(note[1]);
            int base =0;
            switch (note[0]){
                case "A":
                    base = 21;
                    break;
                case "A#":
                    base = 22;
                    break;
                case "B":
                    base = 23;
                    break;
                case "C":
                    base = 24;
                    break;
                case "C#":
                    base = 25;
                    break;
                case "D":
                    base=26;
                    break;
                case "D#":
                    base = 27;
                    break;
                case "E":
                    base = 28;
                    break;
                case "F":
                    base = 29;
                    break;
                case "F#":
                    base = 30;
                    break;
                case "G":
                    base = 31;
                    break;
                case "G#":
                    base = 32;
                    break;

            }
            base += 12*octave;
            frequency = Pitch.mtof(base);

        }

        public void onNothingSelected(AdapterView<?> parent) {
            // Another interface callback
        }
    }


    public class DurationSpinner extends Activity implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view,
                                   int pos, long id) {
            // An item was selected. You can retrieve the selected item using
            String length =  parent.getItemAtPosition(pos).toString();
            duration = Integer.parseInt(length);
        }

        public void onNothingSelected(AdapterView<?> parent) {
            // Another interface callback
        }
    }

    public void selectNote(View v) {
        if (this.frequency != 0 && this.duration != 0) {
            Intent output = new Intent();
            Intent current = getIntent();
            output.putExtra("frequency", this.frequency);
            output.putExtra("duration", this.duration);
            int forBlock = current.getIntExtra("blockNum", 0);
//            output.putExtra("blockNum", );
            int numBlocksSet = MainActivityBlockView.vc.voices.get("voice1").getNumBlocks();
            setResult(RESULT_OK, output);

            if (numBlocksSet == forBlock){
                MainActivityBlockView.vc.voices.get("voice1").addBlock(new Note(frequency, duration));
            } else if (numBlocksSet  > forBlock){
                MainActivityBlockView.vc.voices.get("voice1").replaceBlock(forBlock, new Note(frequency, duration));
            }
            Toast.makeText(this, "Block " + forBlock + " is set, with Frequency: "+this.frequency+", Duration: "+this.duration, Toast.LENGTH_LONG).show();
            finish();
        }
    }
}
