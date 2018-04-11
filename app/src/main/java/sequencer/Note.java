package sequencer;

import java.util.ArrayList;
import java.util.HashMap;




public class Note {
	float frequency;
	int noteLength;
	boolean rest;
	HashMap<String, Float> noteValues = new HashMap<>();
	ArrayList<ComponentParamValue> noteParams = new ArrayList<>();
	
	public Note(float frequency, int time){
		this.frequency = frequency;
		this.noteLength = time;
	}
	
	public Note(HashMap<String, Float> noteValues){
		this.noteValues = noteValues;
	}
	
	public void setFrequency(float val){
		this.frequency = val;
	}
	
	public void setTime(int noteLength){
		this.noteLength = noteLength;
	}
	
	public float getFrequency(){
		if (!this.noteValues.isEmpty()){
			return this.noteValues.get("frequency");
		} else {
			return this.frequency;
		}
	}
	
	public int getLength(){
		if (!this.noteValues.isEmpty()){
			return this.noteValues.get("noteLength").intValue();
		} else {
			return this.noteLength;
		}
	}
	
	public int getLength(VoiceController vc){
		int length = (int)((1.0f / this.noteLength)*(vc.getTempoInterval()));
		System.out.println("note length is " + length);
		return length;
	}
	
	public int getLengthAsTicks(VoiceController vc){
		int ticksPerBeat = vc.clock.getTicksPerBeat();
		int ticksForNote = (int)((1.0f / this.noteLength)*ticksPerBeat);
		return ticksForNote;
	}
	
//	public int getLength(Clock clock, Arpeggiator arp){
//		
//	}
	
	public void addParameter(String compName, String action, float value, int time){
		ComponentParamValue newParam = new ComponentParamValue(compName, action, value, time);
		this.noteParams.add(newParam);
	}
	
	public ArrayList<ComponentParamValue> getParameters(){
		return this.noteParams;
	}
	
	
	
	public class ComponentParamValue {
		String compName;
		String action;
		float targetValue;
		int transitionTime;
		
		public ComponentParamValue(String component, String action, float value, int time){
			this.compName = component;
			this.action = action;
			this.targetValue = value;
			this.transitionTime = time;
		}
		
		public String getComponent(){
			return this.compName;
		}
		
		public String getAction(){
			return this.action;
		}
		
		public float getValue(){
			return this.targetValue;
		}
		
		public int getTransitionTime(){
			return this.transitionTime;
		}
	}
}
