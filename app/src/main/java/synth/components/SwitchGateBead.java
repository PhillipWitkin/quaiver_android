package synth.components;

import net.beadsproject.beads.core.Bead;

public class SwitchGateBead extends Bead {

	boolean state;
	
	public SwitchGateBead(){
		this.state = false;
	}
	
	public void messageReceived(Bead messageForward){
		this.state = true;
		if (messageForward != null)
			messageForward.message(this);
		System.out.println("switchGateBead received message");
	}
	
	public boolean getState(){
		return this.state;
	}
	
	public void setState(boolean state){
		this.state = state;
	}
}
