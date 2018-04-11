package synth.components;

import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.ugens.Envelope;
import net.beadsproject.beads.ugens.Function;
import net.beadsproject.beads.ugens.Glide;
import net.beadsproject.beads.ugens.WavePlayer;

public class FrequencyLFO extends Function{

	final AudioContext ac;
	
	WavePlayer modulator;
	float modulatorFrequency;
	Glide modulatorFrequencyTransition;
	
	Glide filterFrequency;
	
	float modulatorGain;
	Envelope modulatorGainTransition;
	
	public FrequencyLFO(AudioContext ac, WavePlayer modulator, Glide targetFrequency){
		super(modulator);
		this.modulator = modulator;
		this.ac = ac;
		this.modulatorFrequencyTransition = new Glide(ac, modulator.getFrequency(), 50);
		this.modulator.setFrequency(this.modulatorFrequencyTransition);
		
		this.filterFrequency = targetFrequency;
		
		this.modulatorGain = 1;
		this.modulatorGainTransition = new Envelope(ac, this.modulatorGain);
		
	}
	
	public float calculate(){
		float targetFreq = this.filterFrequency.getValue();
		return x[0] * (targetFreq * this.modulatorGainTransition.getCurrentValue() ) + targetFreq;
	}
	
	public void setModulatorFrequency(float frequency){
		this.modulator.setFrequency(frequency);
	}
	
	public void setModulatorFrequency(float frequency, int time){
		
	}
	
	public void setGain(float gain){
		this.modulatorGain = gain;
		this.modulatorGainTransition.setValue(gain);
	}
	
	public void setGain(float gain, int time){
		this.modulatorGain = gain;
		this.modulatorGainTransition.clear();
		this.modulatorGainTransition.addSegment(gain, time);
	}
}
