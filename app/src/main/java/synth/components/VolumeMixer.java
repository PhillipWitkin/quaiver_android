package synth.components;

import java.util.HashMap;

import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.core.UGen;
import net.beadsproject.beads.ugens.Gain;

public class VolumeMixer extends Component{
	HashMap<String, Gain> levels;
	Gain[] gains;
	AudioContext ac;
	Gain master;
	
	public VolumeMixer(AudioContext ac, Gain[] inputs){
		this.ac = ac;
		this.gains = inputs;
		this.master = new Gain(this.ac, 1);
	};
	
	public VolumeMixer(AudioContext ac){
		this.ac = ac;
		this.levels = new HashMap<String, Gain>();
		this.master = new Gain(this.ac, 1);
	}
	
	public void addGain(String name, UGen gainInput, float level){
		Gain g = new Gain(this.ac, 1, level);
//		g.setGain(level);
		g.addInput(gainInput);
		this.levels.put(name, g);
		this.master.addInput(g);
	}
	
	public void setGainLevel(String name, float level){
		this.levels.get(name).setGain(level);
	}

	
	public Gain output(){
		return this.master;
	}
}