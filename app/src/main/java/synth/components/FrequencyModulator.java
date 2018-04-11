package synth.components;

import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.ugens.Envelope;
import net.beadsproject.beads.ugens.Function;
import net.beadsproject.beads.ugens.Glide;
import net.beadsproject.beads.ugens.WavePlayer;

// a  subClass of Function for modulating the pitch of one WavePlayer with another (to create vibrato or FM synthesis)
// the Carrier is the WavePlayer which produces the heard sound
public class FrequencyModulator extends Function {
	
	final AudioContext ac;
	final float HALF_STEP_RATIO = (float)Math.pow(2, 1.0/12);
	float modulatorFrequency;
	Glide modulatorFrequencyTransition;
	float carrierFrequency;
	WavePlayer modulator;
	private WavePlayer carrier;
	
	private float modulatorGain;
	private Envelope modulatorGainTransition;
	

	
	
	public FrequencyModulator(AudioContext ac, WavePlayer modulator, WavePlayer carrier){
		super(modulator);
		this.ac = ac;
		this.modulator = modulator;
		this.modulatorFrequency = modulator.getFrequency();
		this.modulatorFrequencyTransition = new Glide(ac, modulator.getFrequency(), 50);
		this.modulator.setFrequency(this.modulatorFrequencyTransition);
		this.setModulatorFrequency(modulatorFrequency);
		
		this.carrierFrequency = carrier.getFrequency();
		this.carrier = carrier; // unused
		
		this.modulatorGain = 44.67f;
		this.modulatorGainTransition = new Envelope(ac);
		this.modulatorGainTransition.setValue(this.modulatorGain);
	}
	
	public float calculate(){
		return x[0] * (this.carrierFrequency / this.modulatorGainTransition.getCurrentValue()) + this.carrierFrequency;
//		return x[0] * (this.carrierFrequency * this.modulatorGainTransition.getCurrentValue() - this.carrierFrequency) + this.carrierFrequency;
	}
	
	public void setCarrierFrequency(float frequency){
		this.carrierFrequency = frequency;
	}
	
	public float getModulatorFrequency(){
		return this.modulatorFrequencyTransition.getValue();
	}
	
	public void setModulatorFrequency(float frequency){
		this.modulatorFrequencyTransition.setValueImmediately(frequency);
	}
	
	public void setModulatorFrequency(float frequency, int time){
		this.modulatorFrequencyTransition.setGlideTime(time);
		this.modulatorFrequencyTransition.setValue(frequency);
	}
	
	// returns a somewhat arbitrary function that accepts a gain in the range [0, 1]
	// it sets the actual modulatorGain value to be one that is appropriate for vibrato, with .5 resulting in a 1/4 step vibratro
	public void setGain(float gain){
		this.modulatorGain =  (float)(1000*(Math.pow(10, -3*(gain - .05) ) ));
//		this.modulatorGain = (float)(Math.pow(HALF_STEP_RATIO, gain*2));
		this.modulatorGainTransition.setValue(this.modulatorGain);
	}
	
	public void setGain(float gain, int time){
//		this.modulatorGainTransition.clear();
		float gainAdjusted = (float)(1000*(Math.pow(10, -3*(gain - .05) ) ));
		this.modulatorGain = gainAdjusted;
//		float gainAdjusted = (float)(Math.pow(HALF_STEP_RATIO, gain*2));
		this.modulatorGainTransition.addSegment(this.modulatorGain, time);
		System.out.println("actual vibrato gain set to: " + gainAdjusted);
	}
	
}
