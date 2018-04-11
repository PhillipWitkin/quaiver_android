package sequencer;

import java.util.ArrayList;

import synth.SynthSystem;

import synth.components.SwitchGateBead;
import net.beadsproject.beads.core.Bead;
import net.beadsproject.beads.ugens.Clock;


// model for sequence data
public class Sequence {
	
	protected Voice voiceAssigned;
	
	private ArrayList<Note> blocks;

	protected SynthSystem synth;

	private String sequenceName;
	private int sequenceID;
		
	public Sequence(SynthSystem synth, Voice voiceAssigned){
//		this.synth = synth;
		this.voiceAssigned = voiceAssigned;
		this.blocks = new ArrayList<Note>();
				
	}
	
	public Note getBlock(int blockNum){
		return this.blocks.get(blockNum);
	}
	
	public void addBlockDirectly(Note note){
		this.blocks.add(note);
	}
	
	public void addBlockDirectly(Note note, int blockNum){
		this.blocks.add(blockNum, note);
	}
	
	public Note removeBlockDirectly(int blockNum){
		Note block = this.blocks.remove(blockNum);
		return block;
	}
	
	public void removeAllBlocksDirectly(){
		this.blocks.removeAll(blocks);
	}
	
	public void replaceBlock(int blockNumber, Note note){
		this.blocks.remove(blockNumber);
		this.blocks.add(blockNumber, note);
	}
	
	public void addActionToBlock(int blockNumber, String compName, String action, float value, int time){
		System.out.println("adding " + action + " on " + compName);
		this.blocks.get(blockNumber).addParameter(compName, action, value, time);
	}
	
	public ArrayList<Note> getBlocks(){
		return this.blocks;
	}

	public int getNumBlocks(){
		return this.blocks.size();
	}

	public void setName(String name){
		this.sequenceName = name;
	}

	public String getName() {return this.sequenceName; }

	public void setID(int sid){
		this.sequenceID = sid;
	}

	public int getSequenceID() { return this.sequenceID; }

}