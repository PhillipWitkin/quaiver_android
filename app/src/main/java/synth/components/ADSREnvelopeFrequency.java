package synth.components;

import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.core.UGen;
import net.beadsproject.beads.ugens.Function;

public class ADSREnvelopeFrequency extends ADSREnvelope {

	protected float startOffsetRatio;
	protected float maxOffsetRatio;
	protected float endOffsetRatio;
	
	float targetFrequency;
	
	UGen frequencyController; // the actual output of the frequency value, being controller by the envelope to move around targetFrequency
	
	public ADSREnvelopeFrequency(AudioContext ac, UGen freqController, ADSREnvelope adsr, float startOffsetRatio, float maxOffsetRatio, float endOffsetRatio){
		super(ac, adsr.attackTime, adsr.decayTime, adsr.sustainLevel, adsr.releaseTime);
		this.startOffsetRatio = startOffsetRatio;
		this.maxOffsetRatio = maxOffsetRatio;
		this.endOffsetRatio = endOffsetRatio;
		if (freqController != null){
			this.frequencyController = new Function(freqController, this){
				public float calculate(){
					return x[0] + (x[1]*targetFrequency);
				}
			};
		} else {
			this.frequencyController = new Function(this){
				public float calculate(){
					return x[0]*targetFrequency + targetFrequency;
				}
			};
		}
	}
	
//	public ADSREnvelopeFrequency(AudioContext ac, ADSREnvelope adsr, float startOffsetRatio, float maxOffsetRatio, float endOffsetRatio){
//		super(ac, adsr.attackTime, adsr.decayTime, adsr.sustainLevel, adsr.releaseTime);
//		this.startOffsetRatio = startOffsetRatio;
//		this.maxOffsetRatio = maxOffsetRatio;
//		this.endOffsetRatio = endOffsetRatio;
//		this.frequencyController = new Function(this){
//			public float calculate(){
//				return x[0]*targetFrequency + targetFrequency;
//			}
//		};
//	}
//	
	public void gateOn(float targetFreq){
		this.targetFrequency = targetFreq;
		super.triggerOn(this.startOffsetRatio, this.maxOffsetRatio);
	}
	
	public void gateOff(){
		super.triggerOff(this.endOffsetRatio);
	}
}
