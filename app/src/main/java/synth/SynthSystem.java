package synth;

import java.util.HashMap;


import synth.components.Component;
import synth.components.VCA;
import synth.components.VCO;
import synth.components.VolumeMixer;
import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.data.Buffer;
import net.beadsproject.beads.ugens.Gain;

public class SynthSystem {
	final AudioContext ac;
	public HashMap <String, VCO> oscillators;
	public HashMap <String, VCA> amplifiers;
	public VolumeMixer oscillatorLevels;
	public HashMap<String, Component> allComponents; 
	Gain masterGain;
//	public ComponentMethods synthActions; 
	
	public SynthSystem(AudioContext ac){
		this.ac = ac;
		this.oscillators = new HashMap<>();
		this.amplifiers = new HashMap<>();
		this.oscillatorLevels = new VolumeMixer(ac);
		this.masterGain = new Gain(ac, 1);
		this.allComponents = new HashMap<>();
		this.allComponents.put("oscMixer", this.oscillatorLevels);
		
//		this.synthActions = new ComponentMethods(this); enum method no longer needs this
	}
	
	public void addVCO(String name, Buffer waveForm, float relativeVolume){
		VCO vco = new VCO(this.ac, 440f, waveForm);
		this.oscillators.put(name, vco);
		this.allComponents.put(name, vco);
		
	}
	
	public void addVCA(String name, int numChannels){
		VCA vca = new VCA(this.ac, numChannels);
		this.amplifiers.put(name, vca);
		this.allComponents.put(name, vca);
	}
	
	public void connectOscToMix(String oscName, float relativeVolume){
		this.oscillatorLevels.addGain(oscName, this.oscillators.get(oscName).output(), relativeVolume);
	}
	
	public void connectOscToVCA(String oscName, String vcaName){
		this.amplifiers.get(vcaName).addSource(this.oscillators.get(oscName).output());
	}
	
	public void connectMixToVCA(String vcaName){
		this.amplifiers.get(vcaName).addSource(this.oscillatorLevels.output());
	}
	
	public void connectAmpsToMaster(){
		for (VCA vca : this.amplifiers.values()){
			this.masterGain.addInput(vca.output());
		}
		ac.out.addInput(masterGain);
//		ac.start();
	}
}
