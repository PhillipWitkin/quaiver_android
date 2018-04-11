package synth.components;

import java.util.ArrayList;

import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.core.UGen;
import net.beadsproject.beads.data.Buffer;
import net.beadsproject.beads.ugens.Envelope;
import net.beadsproject.beads.ugens.Function;
import net.beadsproject.beads.ugens.Glide;
import net.beadsproject.beads.ugens.WavePlayer;

// a class which contains an oscillator, and contains all inputs which determine its pitch
public class VCO extends Component {
	
	final AudioContext ac;
	public WavePlayer modulator;
	protected WavePlayer pitch;
	
	public FrequencyModulator pitchModulator;
	
	public Glide portamento;
	
	protected float targetPitch;
	protected int halfStepsAway;
	
	protected ADSREnvelopeFrequency pitchEnvelope;
	protected float pitchEnvelopeGain;

	UGen pitchController = null;
	
	public Envelope targetPitchTouchAdjustment;
	protected int targetPitchOffsetStepsTouch;
	protected UGen pitchControllerTouch = null;
	
	public VCO(AudioContext ac, float pitchFrequency, Buffer waveForm){
		this.ac = ac;
		this.pitch = new WavePlayer(ac, pitchFrequency, waveForm);
		this.halfStepsAway = 0;
		
		this.targetPitchOffsetStepsTouch = 0;
		this.targetPitchTouchAdjustment = new Envelope(ac, 1);
	}
	
	public void setPitch(float frequency){
		this.targetPitch = frequency * (float)(Math.pow(Math.pow(2, 1.0/12), this.halfStepsAway));
//		System.out.println("halfSteps: " + this.halfStepsAway);
//		System.out.println("target pitch is: " + this.targetPitch);
		if (this.portamento != null)
			this.portamento.setValue(this.targetPitch);
		if (this.pitchModulator != null )
			this.pitchModulator.setCarrierFrequency(this.targetPitch);
		if (this.pitchController == null && this.pitchControllerTouch == null)
			this.pitch.setFrequency(this.targetPitch);
	}
	
	public void setHalfStepsAway(float distance){
		this.halfStepsAway = (int)distance;
	}
	
	
	public void setPitchModulation(float modFrequency, Buffer lfoShape){
		this.modulator = new WavePlayer(this.ac, modFrequency, lfoShape);
		this.pitchModulator = new FrequencyModulator(this.ac, this.modulator, this.pitch);
		if (this.pitchController != null){
			Function pitchControl = new Function (this.pitchController, this.pitchModulator){
				public float calculate(){
					return x[0] + (x[1] - targetPitch);
				}
			};
			this.pitchController = pitchControl;
			this.pitch.setFrequency(pitchControl);
		} else {
			System.out.println("portamento set as first modulator");
			this.pitchController = this.pitchModulator;
			this.pitch.setFrequency(this.pitchController);
		}
//		this.pitchControllers.add(this.pitchModulator);
//		this.initializePitchControl();
	}
	
	
	// offset ratios should be between -1 and 1, with 0 being no change
	public void setPitchEnvelope(ADSREnvelope adsr, float startOffsetRatio, float peakOffsetRatio, float releaseOffsetRatio){

		this.pitchEnvelopeGain = 1;
		ADSREnvelopeFrequency pitchControl = new ADSREnvelopeFrequency(this.ac, this.pitchController, adsr, startOffsetRatio, peakOffsetRatio, releaseOffsetRatio);
//		if (this.pitchController != null){
//			Function pitchControl = new Function( this.pitchController, this.pitchOffset){
//				public float calculate(){
//					return x[0] + (x[1]*targetPitch);
//				}
//			};
//			pitchControl = new ADSREnvelopeFrequency(this.ac, this.pitchController, adsr, startOffsetRatio, peakOffsetRatio, releaseOffsetRatio);
//			this.pitchEnvelope = pitchControl;
//			this.pitchController = pitchControl.frequencyController;
//			this.pitch.setFrequency(pitchControl.frequencyController);
//		} else {
//			Function pitchControl = new Function( this.pitchOffset){
//				public float calculate(){
//					return targetPitch + (x[0]*targetPitch);
//				}
//			};
//			pitchControl =  new ADSREnvelopeFrequency(this.ac, adsr, startOffsetRatio, peakOffsetRatio, releaseOffsetRatio);
//			this.pitchEnvelope = pitchControl;
//			this.pitch.setFrequency(pitchControl.frequencyController);
//			this.pitchController = pitchControl.frequencyController;
//		}
		this.pitchEnvelope = pitchControl;
		this.pitch.setFrequency(pitchControl.frequencyController);
		this.pitchController = pitchControl.frequencyController;
//		this.pitchControllers.add(this.pitchEnvelope.frequencyController);
//		this.initializePitchControl();
		
	}
	
	public void setPortamento(float time){
		this.portamento = new Glide(this.ac, 440f, time);
		if (this.pitchController != null){
			Function pitchControl = new Function(this.pitchController, this.portamento){
				public float calculate(){
					return x[0] + (x[1] - targetPitch);
				}
			};
			this.pitchController = pitchControl;
			this.pitch.setFrequency(pitchControl);
		} else {
			this.pitch.setFrequency(this.portamento);
			this.pitchController = this.portamento;
		}
//		this.pitchControllers.add(this.portamento);
//		this.initializePitchControl();
	}
	
	
	public void pitchEnvelopeOn(){
//		if (this.pitchOffset != null)
//			this.pitchOffset.triggerOn(this.startLevel , this.peakLevel);
		if (this.pitchEnvelope != null)
			this.pitchEnvelope.gateOn(this.targetPitch);
	}
	
	public void pitchEnvelopeOff(){
//		if (this.pitchOffset != null)
//			this.pitchOffset.triggerOff(this.releaseLevel);
		if (this.pitchEnvelope != null)
			this.pitchEnvelope.gateOff();
	}
	

	
	// touchGate methods
	public void touchGateFreqControlInit(){
		if (this.pitchController != null){
			Function pitchControl = new Function(this.pitchController, this.targetPitchTouchAdjustment){
				public float calculate(){
					return x[0] + targetPitch*(x[1] - 1);
				}
			};
			this.pitchControllerTouch = pitchControl;
			this.pitch.setFrequency(pitchControl);
		} else {
			Function pitchControl = new Function(this.targetPitchTouchAdjustment){
				public float calculate(){
					return  targetPitch*(x[0] - 1);
				}
			};
			this.pitchControllerTouch = pitchControl;
			this.pitch.setFrequency(pitchControl);
		}
	}
	
	public int getTouchGateHalfSteps(){
		return this.targetPitchOffsetStepsTouch;
	}
	
	public void toucbGateSlideFrequency(int halfSteps, int time){
		this.targetPitchOffsetStepsTouch = halfSteps;
		float absoluteOffsetRatio =  (float)(Math.pow(Math.pow(2, 1.0/12), this.targetPitchOffsetStepsTouch));
		this.targetPitchTouchAdjustment.clear();
		this.targetPitchTouchAdjustment.addSegment(absoluteOffsetRatio, time);
	}
	
	
	
	public WavePlayer output(){
		return this.pitch;
	}
	
	
}
