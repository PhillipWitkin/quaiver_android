package sequencer;

import java.util.Arrays;
import java.util.HashMap;


import net.beadsproject.beads.data.Pitch;

public class Arpeggiator {
	double totalBeats;
	int noteDuration;
	// note pattern
	String mode;
	// pitch root by midi number;
	int rootPitch;
	int octaveSpan;
	// scale / chord type
	String tonality;
	int[] currentScale;
	HashMap<String, Integer[]> scalesInC;
	
	public Arpeggiator(String root, int octave){
		this.noteDuration = 8;
		this.tonality = "major";
		this.mode = "ascending";
		this.octaveSpan = 2;
		
		
		
		this.scalesInC = new HashMap<>();
		
		int[] major = Pitch.major;
		Integer[] majorBaseC = new Integer[7];
		for (int i=0; i < major.length; i++)
			majorBaseC[i] = major[i];
		
		this.scalesInC.put("major", majorBaseC);
		
		int[] minor = Pitch.minor;
		Integer[] minorBaseC = new Integer[7];
		for (int i=0; i < minor.length; i++)
			minorBaseC[i] = minor[i];
		
		this.scalesInC.put("minor", minorBaseC);
		
		
//		this.currentScale = new int[7];
		this.setRootPitch(root, octave);
	}
	
	
	private int getPitchNumber(String pitch){
		String[] allPitches = Pitch.pitchNames;
		for (int i=0; i < allPitches.length; i++){
			if (pitch.equals(allPitches[i])){
				return i;
			}
		}
		return -1;
	}
	
	private int[] getCurrentScale(){
		int[] scale = new int[7];
		Integer[] tonalityRootC = this.scalesInC.get(this.tonality);
		for (int i = 0; i < tonalityRootC.length; i++ )
			scale[i] = tonalityRootC[i] + this.rootPitch;
		
		return scale;
	}
	
	private int[] scaleToTriad(){
		int[] triad = new int[4*this.octaveSpan];
		for (int octave = 0; octave < this.octaveSpan; octave++){
			triad[4*octave] = this.currentScale[0] + 12*octave;
			triad[4*octave+1] = this.currentScale[2] + 12*octave;
			triad[4*octave+2] = this.currentScale[4] + 12*octave;
			triad[4*octave+3] = this.currentScale[0] + 12 + 12*octave;
		}
		return triad;
	}
	
	
	public void setRootPitch(String root, int octave){
		int pitchBase = this.getPitchNumber(root);
		if (pitchBase != -1)
			this.rootPitch = pitchBase + (12 * octave);
		this.currentScale = this.getCurrentScale();
	}
	
	public void setTonality(String tonality){
		this.tonality = tonality;
		this.currentScale = this.getCurrentScale();
	}
	
	public void setMode(String mode){
		this.mode = mode;
	}
	
	public void setRange(int octaves){
		this.octaveSpan = octaves;
	}
	
	public void setNoteDuration(int count){
		this.noteDuration = count;
	}
	
	public void setTotalBeats(double numBeats){
		this.totalBeats = numBeats;
	}
	
	public Note[] generateBlocks(){
		int numBlocks = ((this.noteDuration)*this.totalBeats > 1) ? (int)((this.noteDuration )*this.totalBeats) : 1;
		Note[] blocks = null;
		if (this.mode.equals("ascending"))
			blocks = this.generateBlocksAscendingTriad(numBlocks);
		else if (this.mode.equals("descending"))
			blocks = this.generateBlocksDescendingTriad(numBlocks);
		
		return blocks;
	}
	
	private Note[] generateBlocksAscendingTriad(int numBlocks){
		Note[] notes = new Note[numBlocks];
		int[] pitches = this.scaleToTriad();
		System.out.println("pitches before note assignment: " + Arrays.toString(pitches)  );

		int chordPosition = 0;
		for (int i=0; i < numBlocks; i++){
			notes[i] = new Note(Pitch.mtof(pitches[chordPosition]), this.noteDuration);
			chordPosition++;
			if (chordPosition >= pitches.length)
				chordPosition = 0;
//			if (!pitchesIt.hasNext())
//				pitchesIt = pitchesList.iterator();
		}
		return notes;
	}
	
	private Note[] generateBlocksDescendingTriad(int numBlocks){
		Note[] notes = new Note[numBlocks];
		int[] pitches = this.scaleToTriad();
//		System.out.println("pitches before note assignment: " + Arrays.toString(pitches)  );

		int chordPosition = pitches.length - 1;
		for (int i=0; i < numBlocks; i++){
			notes[i] = new Note(Pitch.mtof(pitches[chordPosition]), this.noteDuration);
			chordPosition--;
			if (chordPosition < 0)
				chordPosition = pitches.length - 1;

		}
		return notes;
	}
}
