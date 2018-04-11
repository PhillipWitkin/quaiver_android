package synth.components;

import net.beadsproject.beads.ugens.Function;
import net.beadsproject.beads.ugens.WavePlayer;

public class LFO extends Function{
	WavePlayer lfoSource;
	
	float lfoGain;
	
	public LFO( WavePlayer lfoSource ){
		super(lfoSource);
		this.lfoSource = lfoSource;
		this.lfoGain = 1;
	}
	
	public LFO( WavePlayer lfoSource, float gainValue ){
		super(lfoSource);
		this.lfoSource = lfoSource;
		this.setLfoGain(gainValue);
	}
	
	public void setLfoRate(float frequency){
		this.lfoSource.setFrequency(frequency);
	}
	
	public void setLfoGain(float gainValue){
		if (gainValue >= 0 && gainValue <= 1)
			this.lfoGain = gainValue;
		else if (gainValue > 1)
			this.lfoGain = 1;
		else if (gainValue < 0)
			this.lfoGain = 0;
	}
	
	public float calculate(){
		return x[0]*this.lfoGain + (1 - this.lfoGain);
	}
}
