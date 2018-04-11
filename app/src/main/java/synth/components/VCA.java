package synth.components;

import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.core.UGen;
import net.beadsproject.beads.data.Buffer;
import net.beadsproject.beads.ugens.Gain;
import net.beadsproject.beads.ugens.WavePlayer;

public class VCA extends Component{
	AudioContext ac;
	public ADSREnvelope adsr;
	public LFO tremoloSource;
	Gain tremoloGain;
	Gain volume;
	
	public VCA(AudioContext ac, int numChannels){
		this.ac = ac;
		this.volume = new Gain(ac, numChannels, 0f);
		this.tremoloGain = new Gain(this.ac, 1);
		this.volume.addInput(tremoloGain);
	}
	
	public void createEnvelope(float attackTime, float decayTime, float sustainLevel, float releaseTime){
		this.adsr = new ADSREnvelope(this.ac, attackTime, decayTime, sustainLevel, releaseTime);
		this.volume.setGain(adsr);
	}
	
	public void createEnvelope(ADSREnvelope adsr){
		this.adsr = adsr;
		this.volume.setGain(adsr);
	}
	
	public void createTremolo(float frequency, Buffer waveForm, float tremoloGain){
		this.tremoloSource = new LFO(new WavePlayer(this.ac, frequency, waveForm), tremoloGain);
		this.tremoloGain.setGain(this.tremoloSource);
		
	}
	
	public void addSource(UGen source){
		this.tremoloGain.addInput(source);
	}
	
	public Gain output(){
		return this.volume;
	}
}