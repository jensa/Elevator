package controller;

import java.awt.event.ActionEvent;
import java.rmi.RemoteException;

import elevator.rmi.Door;
import elevator.rmi.RemoteActionListener;

public class Elevator {
	private RMI controller;
	private int numElevators;
	private int numFloors;
	
	
	public Elevator (){
		controller = new RMI ();
		numElevators = controller.getNumberOfElevators ();
		numFloors = controller.getNumberOfFloors ();
		installListeners ();
	}
	
	private void installListeners () {
		for (int i=1;i<=numFloors;i++){
			final int floor = i;
			controller.makeFloorListener (i, new RemoteActionListener(){
				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed (ActionEvent e)
						throws RemoteException {
					floorButtonPressed (floor, e);
				}

				
			});
		}
		for (int i=1;i<=numElevators;i++){
			final int elevator = i;
			controller.makeFloorListener (i, new RemoteActionListener(){
				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed (ActionEvent e)
						throws RemoteException {
					insideButtonPressed (elevator, e);
				}
			});
		}
		for (int i=1;i<=numElevators;i++){
			final int elevator = i;
			controller.makePositionListener(i, new RemoteActionListener(){
				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed (ActionEvent e)
						throws RemoteException {
					elevatorMoved (elevator, e);
				}
			});
		}
			controller.makeVelocityListener (new RemoteActionListener(){
				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed (ActionEvent e)
						throws RemoteException {
					velocityChanged (e);
				}
			});
		
	}
	
	protected void velocityChanged (ActionEvent e) {
		// TODO Auto-generated method stub
		
	}

	protected void elevatorMoved (int elevator, ActionEvent e) {
		// TODO Auto-generated method stub
		
	}

	private void floorButtonPressed (int floor, ActionEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	private void insideButtonPressed (int elevator, ActionEvent e) {
		// TODO Auto-generated method stub
		
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
