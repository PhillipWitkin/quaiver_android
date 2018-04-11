package sequencer;

import com.jsyn.JSyn;
import com.jsyn.Synthesizer;
import jsyn.devices.JSynAndroidAudioDevice;
import com.jsyn.ports.UnitInputPort;
import com.jsyn.unitgen.LineOut;
import com.jsyn.unitgen.LinearRamp;
import com.jsyn.unitgen.SineOscillator;

/**
 * Play independent sine waves on the left and right channel.
 */
public class SineSynth {

    private final Synthesizer mSynth;
    private final LinearRamp mAmpJack; // for smoothing and splitting the level
    private final SineOscillator mOsc1;
    private final SineOscillator mOsc2;
    private final LineOut mLineOut; // stereo output

    public SineSynth() {
        // Create a JSyn synthesizer that uses the Android output.
        mSynth = JSyn.createSynthesizer( new JSynAndroidAudioDevice() );

        // Create the unit generators and add them to the synthesizer.
        mSynth.add(mAmpJack = new LinearRamp());
        mSynth.add(mOsc1 = new SineOscillator());
        mSynth.add(mOsc2 = new SineOscillator());
        mSynth.add(mLineOut = new LineOut());

        // Split level setting to both oscillators.
        mAmpJack.output.connect(mOsc1.amplitude);
        mAmpJack.output.connect(mOsc2.amplitude);
        mAmpJack.time.set(0.1); // duration of ramp

        // Connect an oscillator to each channel of the LineOut.
        mOsc1.output.connect(0, mLineOut.input, 0);
        mOsc2.output.connect(0, mLineOut.input, 1);

        // Setup ports for nice UI
        getAmplitudePort().setName("Level");
        getAmplitudePort().setup(0.0, 0.5, 1.0);
        getLeftFrequencyPort().setName("FreqLeft");
        getLeftFrequencyPort().setup(100.0, 300.0, 1000.0);
        getRightFrequencyPort().setName("FreqRight");
        getRightFrequencyPort().setup(100.0, 400.0, 1000.0);
    }

    public void start() {
        mSynth.start();
        mLineOut.start();
    }

    public void stop() {
        mLineOut.stop();
        mSynth.stop();
    }

    public void setFrequency(double frequency){
        mOsc1.frequency.set(frequency);
        mOsc2.frequency.set(frequency);
    }

    public UnitInputPort getAmplitudePort() {
        return mAmpJack.getInput();
    }

    public UnitInputPort getLeftFrequencyPort() {
        return mOsc1.frequency;
    }

    public UnitInputPort getRightFrequencyPort() {
        return mOsc2.frequency;
    }
}
