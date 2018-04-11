package sequencer;

import android.util.Log;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

import net.beadsproject.beads.core.Bead;
import net.beadsproject.beads.ugens.Clock;


// class which controls the action of the sequencer as it moves across the blocks
public class SequenceController {
	
	final int BEAT_RESOLUTION = 48;//96;
	
	protected Voice voiceAssigned; // voice the sequence is assigned to
	
	private Bead endOfWalk; // a bead which initiates control message back from Sequence->Voice at end of blocks
	
	Clock masterClock;
	
	private NoteOnBead mainListener; // bead which turns the notes on at the right time

	Timer seqTimer;

	boolean isPlaying;

	public SequenceController( Voice voiceAssigned, Clock masterClock){
		this.voiceAssigned = voiceAssigned;

		class EndOfWalk extends Bead {
			Voice voice;
			public EndOfWalk(Voice voiceAssigned){
				voice = voiceAssigned;
			}
			public void messageReceived(Bead messageIn){
				System.out.println("end of sequence reeached...");
//				mainListener.pause(true);
				voice.messageSequenceReceiver(messageIn);
			}

		}
		this.endOfWalk = new EndOfWalk(voiceAssigned);
//		this.endOfWalk = new Bead(){
//			public void messageReceived(Bead messageIn){
//				System.out.println("end of sequence reeached...");
//				mainListener.pause(true);
//				voiceAssigned.messageSequenceReceiver(messageIn);
//			}
//		};
//
		
		this.masterClock = masterClock;
		this.masterClock.setTicksPerBeat(BEAT_RESOLUTION);

		seqTimer = new Timer();
	}
	

	
	public void playSequence(){
		seqTimer = new Timer();
		this.listenToClock();
		if (!this.isPlaying){
			this.isPlaying = true;
		}
	}

	public void stopSequence() {
		if (this.isPlaying) {
			voiceAssigned.noteOff();
//		this.masterClock.clearMessageListeners();
			seqTimer.cancel();
			this.isPlaying = false;
		}
	}
	
//	public void pauseMainListner(boolean pause){
//		mainListener.pause(pause);
//	}
	
	
	private void listenToClock(){
		mainListener = new NoteOnBead(seqTimer);
//		mainListener.setName(voiceAssigned.getName());
		if (mainListener.seqIter.hasNextBlock())
			mainListener.advanceToNextBlock();
//		this.masterClock.addMessageListener(mainListener);
//		this.masterClock.reset();
	}
	
	public void repeatSequence(){
//		mainListener.seqIter = new SequenceIterators();
//		if (mainListener.seqIter.hasNextBlock())
//			mainListener.advanceToNextBlock();
		seqTimer.purge();
		seqTimer.cancel();
		seqTimer = new Timer();
		listenToClock();
	}
	
	
	// inner class which holds the iterators for sequence notes and their tick positions
	private class SequenceIterators{
		Iterator<Note> noteIter;
		Iterator<Integer> blockIter;

		int totalTicks;
		
		public SequenceIterators(){
			this.noteIter = voiceAssigned.getSequence().getBlocks().listIterator();
			this.blockIter = this.calculateBlockOffsets().listIterator();
		}
		
		public boolean hasNextBlock(){
			return this.blockIter.hasNext();
		}
		
		public Note getNextNote(){
			return this.noteIter.next();
		}
		
		public Integer getNextBlockOffset(){
			return this.blockIter.next();
		}
		
		private ArrayList<Integer> calculateBlockOffsets(){
			ArrayList<Integer> blockPositions = new ArrayList<>();
			int tickTime = 0;
			for (Note note :  voiceAssigned.getSequence().getBlocks()){
				blockPositions.add(tickTime);
				tickTime += (int)((4.0 / note.getLength()) * BEAT_RESOLUTION);
				System.out.println("tickTime " + tickTime);
			}
			this.totalTicks = tickTime;
			return blockPositions;
		}
	}
	

	
	// class which is instantiated as main listener for sequence
	private class NoteOnBead  {
		Timer cOn;
		SequenceIterators seqIter;
		int currentTick;
		Note currentNote;
//		NoteStopperBead turnOffNote;
		
		public NoteOnBead(Timer t){
			this.cOn = t;
			this.seqIter = new SequenceIterators();

//			this.advanceToNextBlock();


//Set the schedule function and rate
			t.scheduleAtFixedRate(new TimerTask() {
				int numTicks =0;
		  		@Override
					public void run() {
					//Called each time when time elapses
					messageReceived(numTicks);
					numTicks++;
				}
			},
//Set how long before to start calling the TimerTask (in milliseconds)
		0,
//Set the amount of time between each execution (in milliseconds)
		25);
		}
		
		public void messageReceived(int numTicks){

//			System.out.println("current tick " + cOn.getCount() + ", next: " + currentTick + ", total: " + seqIter.totalTicks);
			if (numTicks >= seqIter.totalTicks){
				// sequence is over
				Log.d("sequence over","-------------------");
				endOfWalk.message(null);

			} else if  (numTicks == currentTick || currentTick == 0){
				System.out.println("note activated by sequence, tick at: " + currentTick);

				  // play current note
				voiceAssigned.setCurrentNote(currentNote, currentNote.getLengthAsTicks(voiceAssigned.vc));

				Log.d("Current Freq","Current frequency: " + currentNote.getFrequency());
					
					// compute tick offset of when note should turn off 
//				int tickLength = currentNote.getLengthAsTicks(voiceAssigned.vc);
//				int noteOffset = (int)(currentTick + tickLength*voiceAssigned.legato*4);
//
//				turnOffNote = new NoteStopperBead(noteOffset, numTicks);
//
//	  			cOn.addMessageListener(turnOffNote);

				if (seqIter.blockIter.hasNext()){
					advanceToNextBlock();
					
				} else {
					currentTick = seqIter.totalTicks;
				}
				  
			}
	    
		}
		
		public void advanceToNextBlock(){
			this.currentNote = seqIter.getNextNote();
			this.currentTick = seqIter.getNextBlockOffset();
		}
				
	}


	// extension of Bead which will turn a note off a given number of ticks later
	private class NoteStopperBead extends Bead {
		int noteOffset;
		Timer cOff = seqTimer;
		public NoteStopperBead(int tickOffset, int numTicks){
			this.noteOffset = tickOffset;
//			this.cOff = cOff;
		}

		public void messageReceived(Bead message){
			Clock cOff = (Clock)message;
			if (cOff.getCount() == noteOffset){
				voiceAssigned.noteOff();
				cOff.removeMessageListener(this);
			}
		}

	}
}
