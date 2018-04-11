package synth.components;

import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.ugens.Envelope;

// a subclass of Envelope,
// set up to manage changes in gain or frequency through ADSR through on and off gates
public  class ADSREnvelope extends Envelope{
	
//	Envelope envelope;
	float attackTime;
	float decayTime;
	float sustainLevel;
	float releaseTime;
	
	public ADSREnvelope(AudioContext context, float attackTime, float decayTime, float sustainLevel, float releaseTime){
		super(context);
		this.attackTime = attackTime;
		this.decayTime = decayTime;
		this.sustainLevel = sustainLevel;
		this.releaseTime = releaseTime;
	}
	

	public void triggerOn(){
		System.out.println("trigger on called");
		this.clear();
//		this.addSegment(0, 10);
		this.addSegment(.1f, this.attackTime);
		this.addSegment(sustainLevel, decayTime);
	}
	
	public void triggerOn(float hold){
		this.clear();
		this.addSegment(.1f, attackTime);
		this.addSegment(sustainLevel, decayTime);
		this.addSegment(sustainLevel, hold);
	}
	
	public void triggerOff(){
		this.clear();
		this.addSegment(0, releaseTime);
	}
	
	public void silence(){
		this.setValue(0);
	}
	
	//used for pitch offset
	public void triggerOn(float startVal, float peakVal){
		this.clear();
		this.setValue(startVal);
		this.addSegment(peakVal, attackTime);
		this.addSegment(0, decayTime);
	}
	
	public void triggerOff(float endingVal){
		this.addSegment(endingVal, releaseTime);
	}
	
	
	
}

