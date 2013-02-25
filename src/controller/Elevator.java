package controller;

public class Elevator {
	private RMI controller;
	
	public Elevator (){
		controller = new RMI ();
	}
	
	public void runElevatorController (){
		
	}
	
	public static void main(String[] args){
		//Set policy file so we don't get weird permission errors
		System.setProperty("java.security.policy","file:./rmi.policy");
		new Elevator ().runElevatorController ();
	}
}
