package sequencer;

import android.util.Log;

import java.util.HashMap;

import synth.SynthSystem;

import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.ugens.Clock;
import net.beadsproject.beads.ugens.Envelope;

public class VoiceController {
	 public Clock clock;
	 private int tempoInterval;
	 private float bpm;
	 private Envelope clockTempo;
	 
	 private AudioContext ac;
	 public HashMap<String, Voice> voices;
	 
	 private boolean sequencerRepeat = true;
	 private boolean sequencerActive;
	
	
	public VoiceController(AudioContext aContext){
		this.ac = aContext;
		this.tempoInterval = 1000;
		this.bpm = 60.0f;
		this.clockTempo = new Envelope(ac, this.tempoInterval);
		this.clock = new Clock(ac, (int)(60000.0f / bpm));
		this.ac.out.addDependent(clock);
//		this.clock.setIntervalEnvelope(this.clockTempo);
		this.voices = new HashMap<>();
		

	}
	
	public Voice getPrimaryVoice(){

		Voice primaryVoice = null;
		for (Voice voice : this.voices.values()){
//			String name = voice.getName();
			if ( voice.isPrimary() ){
				primaryVoice = voice;
				Log.d("prmimary voice",voice.getName() + " found as primary voice");
			} 
			
		}
		
		return primaryVoice;
	}
	
	public void setTempo(int intervalInMS){
		this.tempoInterval = intervalInMS;
		this.bpm = 60000.0f / intervalInMS;
		this.clockTempo.addSegment(tempoInterval, tempoInterval/2);
	}
	
	public void setTempo(float bpmValue){
		this.bpm = bpmValue;
		this.tempoInterval = (int)(60000.0f / bpmValue);
		Log.d("Tempo interval","Tempo interval set to: " + tempoInterval);
//		this.clockTempo.addSegment(tempoInterval, 100);
		this.clockTempo.setValue(tempoInterval);
	}
	
	public int getTempoInterval(){
		return this.tempoInterval;
	}
	
	public float getTempoBPM(){
		return this.bpm;
	}
	
	
	public void addVoice(String name, SynthSystem synth){
		Voice voice = new Voice(this, name);
		this.voices.put(name, voice);
	}

	public void addVoice(String name){
		Voice voice = new Voice(this, name);
		this.voices.put(name, voice);
	}

	public void setPrimaryVoice(String voiceName){
		Voice previousPrimary = this.getPrimaryVoice();
//		if (previousPrimary != null){
//			System.out.println(this.getPrimaryVoice().name + " found as previous primary voice");
//			double numBeatsPreviousPrimary = previousPrimary.getNumBeats();
//			double numBeatsNewPrimary = this.voices.get(voiceName).getNumBeats();
//			if (numBeatsPreviousPrimary > numBeatsNewPrimary){
//				// trim to right number of beats
//				previousPrimary.trimBlocksToBeats(numBeatsNewPrimary);
//			}
//
//		} else { 
//			System.out.println("no voice found as previous primary");
//		}
		if (previousPrimary == null){
			this.voices.get(voiceName).setPrimaryStatus(true);
			System.out.println("no voice found as previous primary");
			return;
		}
		
		double numBeatsNewPrimary = this.voices.get(voiceName).getNumBeats();
		for (HashMap.Entry<String, Voice> entry : this.voices.entrySet()){
			String name = entry.getKey();
			Voice voice = entry.getValue();
			voice.setPrimaryStatus(false);
			if (voice.getNumBeats() > numBeatsNewPrimary && !name.equals(voiceName)){
				voice.trimBlocksToBeats(numBeatsNewPrimary);
			}
		}
//		this.voices.forEach((name, voice) -> {
//			voice.setPrimaryStatus(false);
//			if (voice.getNumBeats() > numBeatsNewPrimary && !name.equals(voiceName)){
//				voice.trimBlocksToBeats(numBeatsNewPrimary);
//			}
//		});
		this.voices.get(voiceName).setPrimaryStatus(true);
	}
	
	public void startSequencer(){
		if (!this.sequencerActive){
			this.sequencerActive = true;
			
			for (Voice voice : voices.values()){
				voice.sequencePlay();
			}
		}
	}
	
	public void stopSequencer(){
		this.sequencerActive = false;
		for (Voice voice : voices.values()){
			voice.sequenceStop();
		}
	}
	
	public void messageAllSequences(){
		this.clock.pause(true);
		for (Voice voice : voices.values()){
			voice.sequenceReady();;
		}
		this.clock.pause(false);
		this.clock.reset();
		
		Log.d("VoiceCtronoller message","vc sent out permission to all voices");
	}
	
//	public void sequenceComplete(){
//		for (Voice voice : voices.values()){
//			if (voice.sequenceActive)
//				return;
//		}
//		this.sequencerActive = false;
//	}
}
