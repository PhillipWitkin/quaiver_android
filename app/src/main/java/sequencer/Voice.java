package sequencer;

import android.annotation.TargetApi;
import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioTrack;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

import synth.CommandMethodsEnum;
import synth.SynthSystem;



import synth.components.VCA;
import synth.components.VCO;
import net.beadsproject.beads.core.Bead;

import org.json.JSONObject;

//model-controller for one voice of the sequencer, communicates sequencer commands to its SynthSystem
//controls application's processeing of the sequencer's commands, and holds the voice's current note
//controls arpegiator
@TargetApi(24)
public class Voice {

	public SynthSystem synth;
	
	private String name;
	protected Sequence sequence;
	private boolean primaryVoice = false;
	private double numBeats;
	private Bead sequenceReceiver;
	
	float legato; // needs to be encapsulated
	
	boolean sequenceActive;
	
	public VoiceController vc;
	
	protected Arpeggiator arp;
	boolean arpOn;
	
	protected Note currentNote;
	
	private SequenceController sequenceController;

	Sequence dumpedSequence;

	SineSynth sineSynth;

	public Voice(VoiceController vc, String name, SynthSystem synth){
		this.synth = synth;
		this.name = name;
		this.sequence = new Sequence(synth, this);
		this.sequence.voiceAssigned = this;
		this.numBeats = 0;
		this.vc = vc;
		this.legato = .7f;

		class SequenceReceiver extends Bead {
			VoiceController bvc;
			String name;
			public SequenceReceiver(VoiceController vcIn, String voiceName){
				bvc = vcIn;
				name = voiceName;
			}

			// triggered at the end of a sequence; checks for primary status
			public void messageReceived(Bead messagenger){
				if (isPrimary()){
					//send a message back to voiceController
					System.out.println("voice bead end of sequence for voice " + name);
					bvc.messageAllSequences();
					System.out.println("vc messaged from " + name);
				}
			}
		}

		this.sequenceReceiver = new SequenceReceiver(vc, name);
//		this.sequenceReceiver = new Bead(){ // Sends control message back from Sequence->Voice->VoiceController
//
//			// triggered at the end of a sequence; checks for primary status
//			public void messageReceived(Bead messagenger){
//				if (isPrimary()){
//					//send a message back to voiceController
//					System.out.println("voice bead end of sequence for voice " + name);
//					vc.messageAllSequences();
//					System.out.println("vc messaged from " + name);
//				}
//			}
//		};


		this.arp = new Arpeggiator("C", 5);

		this.sequenceController = new SequenceController( this, vc.clock);
		this.dumpedSequence = null;
	}

	public Voice(VoiceController vc, String name){
		this.name = name;
		this.sequence = new Sequence(synth, this);
		this.sequence.voiceAssigned = this;
		this.numBeats = 0;
		this.vc = vc;
		this.legato = .7f;

		sineSynth = new SineSynth();

		class SequenceReceiver extends Bead {
			VoiceController bvc;
			String name;
			public SequenceReceiver(VoiceController vcIn, String voiceName){
				bvc = vcIn;
				name = voiceName;
			}

			// triggered at the end of a sequence; checks for primary status
			public void messageReceived(Bead messagenger){
				if (isPrimary()){
					//send a message back to voiceController
					System.out.println("voice bead end of sequence for voice " + name);
					bvc.messageAllSequences();
					System.out.println("vc messaged from " + name);
				}
			}
		}

		this.sequenceReceiver = new SequenceReceiver(vc, name);
		this.sequenceController = new SequenceController( this, vc.clock);
	}

	public String getName(){
		return this.name;
	}
	
	public Sequence getSequence(){
		return this.sequence;
	}



	// Sequence playback methods
	public void createSequence(){
		this.sequence = new Sequence(this.synth, this);
	}
	
	public void sequenceReady(){
		System.out.println(this.name + "ready to run again" );
//		this.sequenceController.pauseMainListner(false);
		this.sequenceController.repeatSequence();		
	}
	
	public void sequencePlay(){
//		SequencerTask st = new SequencerTask();
//		st.execute("");
		sequenceController.playSequence();
	}
	
	public void sequenceStop(){
		this.sequenceController.stopSequence();
	}
	
	public void messageSequenceReceiver(Bead message){
		this.sequenceReceiver.message(message);
	}



	// voice status methods
	public boolean isPrimary(){
		return this.primaryVoice;
	}
	
	public void setPrimaryStatus(boolean primaryStatus){
		this.primaryVoice = primaryStatus;
	}
	
	// voice length methods
	public double getNumBeats(){
		if (this.arpOn){
			return this.arp.totalBeats;
		} else {
			return this.numBeats;
		}
	}
	
	public void setNumBeats(double numBeats){
		this.numBeats = numBeats;
	}
	
	
	// SynthSystem control methods
//	private void noteGateOn(){
//		System.out.println("note gate on by voice");
//
//		for (VCO vco : this.synth.oscillators.values()){
//			vco.pitchEnvelopeOn();
//		}
//
//		for (VCA vca : this.synth.amplifiers.values()){
//			vca.adsr.triggerOn();;
//		}
//
//	}
//
//	private void noteGateOff(){
//		for (VCO vco : this.synth.oscillators.values()){
//			vco.pitchEnvelopeOff();
//		}
//
//		for (VCA vca : this.synth.amplifiers.values()){
//			vca.adsr.triggerOff();
//		}
//	}
//
//	private void setOscPitches(float frequency){
//		for (VCO vco : this.synth.oscillators.values()){
//			vco.setPitch(frequency);;
//		}
//	}
//
//	private void setComponentParams(Note note){
//		for (Note.ComponentParamValue compValue : note.getParameters()){
////			this.synth.synthActions.executeCommand(compValue.getAction(), compValue.getComponent(), compValue.getValue(), compValue.getTransitionTime());
//			// new way using enum instead
//			CommandMethodsEnum action = CommandMethodsEnum.valueOf(compValue.getAction());
//			action.execute(this.synth, compValue.getComponent(), compValue.getValue(), compValue.getTransitionTime());
//		}
//	}
//
//	private void noteOn(){
//		this.setOscPitches(this.currentNote.getFrequency());
//		this.setComponentParams(this.currentNote);
//		this.noteGateOn();
//	}
	
	public void noteOff(){
		sineSynth.stop();
	}
//
//	public void setCurrentNote(Note note){
//		this.currentNote = note;
//		this.noteOn();
//	}

	public void setCurrentNote(Note note, int duration){
		sineSynth.setFrequency(note.getFrequency());
		sineSynth.start();
	}
	
	public Note getCurrentNote(){
		return this.currentNote;
	}
	





	// methods for arpeggiator -- should now be partially migrated to Sequence
	public Sequence useArppeggiator(boolean arpStatus, Sequence replacementSequence){
		this.arpOn = arpStatus;
		Sequence dump = null;
		if (!arpStatus){
			this.sequence = replacementSequence;
			double numBeats = 0;
			for ( Note block : replacementSequence.getBlocks() ){
				numBeats += 1.0 / block.getLength();
			}
			this.setNumBeats(numBeats);
		} else {
			dump = this.sequence;
			this.dumpedSequence = dump;
		}
		return dump;	
	}
	
	public void initializeArp(String root, int octave){
		this.arp.setRootPitch(root, octave);
		this.arp.setTotalBeats(this.numBeats);
		this.generateArpBlocks();
	}
	
	private void generateArpBlocks(){
		Note[] blocks = this.arp.generateBlocks();
		ArrayList<Note> oldBlocks = new ArrayList<>(this.sequence.getBlocks());
		this.sequence.removeAllBlocksDirectly();
		Iterator<Note> oldBlockIt = oldBlocks.iterator();
		for (Note note : blocks){
			if (oldBlockIt.hasNext()){
				Note oldBlock = oldBlockIt.next();
				if (!oldBlock.noteParams.isEmpty()){
					note.noteParams = oldBlock.noteParams;
				}
					
			}
			this.addArpBlock(note);
		}
	}
	
	private void addArpBlock(Note note){
//		if (this.isPrimary() ){
//			this.sequence.addBlockDirectly(note);
//		} else {//if (this.numBeats + (1.0 / note.getLength()) <= this.vc.getPrimaryVoice().getNumBeats() ) {
//			this.sequence.addBlockDirectly(note);
//		}
		this.sequence.addBlockDirectly(note);
	}
	
	public void setArpRange(int numOctaves){
		this.arp.setRange(numOctaves);
		this.generateArpBlocks();
	}
	
	public void setArpMaxBeats(double maxNumBeats){
		double numBeatsPrimaryVoice = this.vc.voices.get(this.vc.getPrimaryVoice()).numBeats;
		if (this.isPrimary() || maxNumBeats <= numBeatsPrimaryVoice){
			this.arp.setTotalBeats(maxNumBeats);
		}else{
			this.arp.setTotalBeats(numBeatsPrimaryVoice);
		}
		this.generateArpBlocks();
	}
	
	public void setArpMode(String mode){
		this.arp.setMode(mode);
		this.generateArpBlocks();
	}
	

	
	// methods for sequence data -- partially migrated to Sequence
	public void addBlock(Note note){
		if (this.isPrimary() ){
			this.sequence.addBlockDirectly(note);
			this.numBeats += 1.0 / note.getLength();
		} else if (this.numBeats + (1.0 / note.getLength()) <= this.vc.getPrimaryVoice().getNumBeats() ) {
			this.sequence.addBlockDirectly(note);
			this.numBeats += 1.0 / note.getLength();
		}
	}
	
	public void removeBlock(int blockNumber){
		this.numBeats -= 1.0 / this.sequence.getBlock(blockNumber).getLength();
		this.sequence.removeBlockDirectly(blockNumber);
	}
	
	public void replaceBlock(int blockNumber, Note note){
		if (this.isPrimary() || (note.getLength() == this.sequence.getBlock(blockNumber).getLength()) ){
			this.sequence.replaceBlock(blockNumber, note);
		} else if (this.numBeats - (1.0 / this.sequence.getBlock(blockNumber).getLength()) + (1.0 / note.getLength()) <= this.vc.getPrimaryVoice().numBeats ){
			this.sequence.replaceBlock(blockNumber, note);
		}

	}

	public int getNumBlocks(){
		return this.sequence.getNumBlocks();
	}
	
	public void trimBlocksToBeats(double maxBeats){
		int numBlocks = this.sequence.getBlocks().size();
		do {
			this.removeBlock(numBlocks - 1);
			numBlocks--;
		} while (this.numBeats > maxBeats || numBlocks <= 0);
	}
	
	public void addActionToBlock(int blockNumber, String compName, String action, float value, int time){
		this.sequence.addActionToBlock(blockNumber, compName, action, value, time);
	}

	public String getSequenceName(){
		return this.sequence.getName();
	}

	public void setSequenceName(String name){
		this.sequence.setName(name);
	}

	public int getSequenceID(){ return this.sequence.getSequenceID(); }

	public void setSequenceID(int seqID){
		this.sequence.setID(seqID);
	}




}	