package synth;

public enum CommandMethodsEnum {

	// effective immediately
	setPortamentoTime {
		public void execute(SynthSystem synth, String compName, Float value, Integer transition){
			if (synth.oscillators.get(compName).portamento != null)
				synth.oscillators.get(compName).portamento.setValue(value);
		}
	},
	
	setHalfStepsAway { 
		public void execute (SynthSystem synth, String compName, Float value, Integer transition) {
			synth.oscillators.get(compName).setHalfStepsAway(value);
			System.out.println("executing setHallfStepsAway on " + compName + " with value " + value);
		}
	},
			
	setVibratoRateNow { 
		public void execute(SynthSystem synth, String compName, Float value, Integer transition) {	
			synth.oscillators.get(compName).pitchModulator.setModulatorFrequency(value);
		}
	},
			
	setVibratoGainNow {
		public void execute(SynthSystem synth, String compName, Float value, Integer transition){
			synth.oscillators.get(compName).pitchModulator.setGain(value);
		}
	},
	
	// target value reached after time
	setVibratoRate {
		public void execute(SynthSystem synth, String compName, Float value, Integer transition){
			if (synth.oscillators.get(compName).pitchModulator != null){
				synth.oscillators.get(compName).pitchModulator.setModulatorFrequency(value, transition);
				System.out.println("vibrato rate changing to: " + value);
			}
		}
	},
			
	setVibratoGain {
		public void execute(SynthSystem synth, String compName, Float value, Integer transition){
			synth.oscillators.get(compName).pitchModulator.setGain(value, transition);
		}
	};
//			
	
	public abstract void execute(SynthSystem synth, String comp, Float value, Integer time);
}
