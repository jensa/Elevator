package controller;

import java.rmi.RemoteException;

import elevator.rmi.Door;

public class Elevator {
	private RMI controller;
	private int numElevators;
	private int numFloors;
	
	
	public Elevator (){
		controller = new RMI ();
		numElevators = controller.getNumberOfElevators ();
		numFloors = controller.getNumberOfFloors ();
	}
	
	public void runElevatorController () throws RemoteException, InterruptedException{
		int[] allDoors = new int[numElevators];
		
		Door[] doors = new Door[numElevators];
		for (int i=1;i<=numElevators;i++){
			doors[i-1] = controller.getDoor (i);
		}
		for (int i=0;i<100;i++){
			for (Door d : doors){
				d.open ();
				Thread.sleep (100);
			}
			Thread.sleep (1000);
			for (Door d : doors){
				d.close ();
				Thread.sleep (200);
			}
		}
	}
	
	public static void main(String[] args){
		//Set policy file so we don't get weird permission errors
		System.setProperty("java.security.policy","file:./rmi.policy");
		try {
			new Elevator ().runElevatorController ();
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
