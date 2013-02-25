package controller;

import java.awt.event.ActionEvent;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.io.Serializable;
import elevator.rmi.Door;
import elevator.rmi.RemoteActionListener;

public class Elevator implements Serializable{
	private static final long serialVersionUID = 1L;
	private RMI controller;
	private int numElevators;
	private int numFloors;
	private ElevatorThread[] elevatorThreads;


	public Elevator () throws RemoteException{
		controller = new RMI ();
		numElevators = controller.getNumberOfElevators ();
		numFloors = controller.getNumberOfFloors ();
		elevatorThreads = new ElevatorThread[numElevators];
		installListeners ();
		for (int i=0;i<numElevators;i++){
			ElevatorThread et = new ElevatorThread (i, controller);
			elevatorThreads[i] = et;
			new Thread (et).start ();
		}
	}

	private void installListeners () throws RemoteException {
		for (int i=0;i<numFloors;i++){
			final int floor = i;
			RemoteActionListener floorListener = (RemoteActionListener) UnicastRemoteObject.exportObject (new RemoteActionListener(){
				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed (ActionEvent e)
						throws RemoteException {
					floorButtonPressed (floor, e);
				}


			}, 0);
			controller.makeFloorListener (i, floorListener);
		}
		for (int i=0;i<numElevators;i++){
			final int elevator = i;
			RemoteActionListener insideListener = (RemoteActionListener) UnicastRemoteObject.exportObject (new RemoteActionListener(){
				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed (ActionEvent e)
						throws RemoteException {
					insideButtonPressed (elevator, e);
				}
			}, 0);
			controller.makeInsideListener (i, insideListener);
		}
		for (int i=0;i<numElevators;i++){
			final int elevator = i;
			RemoteActionListener listener = (RemoteActionListener) UnicastRemoteObject.exportObject (new RemoteActionListener(){
				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed (ActionEvent e)
						throws RemoteException {
					elevatorMoved (elevator, e);
				}
			}, 0);
			controller.makePositionListener (i, listener);
		}
		RemoteActionListener listener = (RemoteActionListener) UnicastRemoteObject.exportObject (new RemoteActionListener(){
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed (ActionEvent e)
					throws RemoteException {
				velocityChanged (e);
			}
		}, 0);
		controller.makeVelocityListener (listener);
	}

	protected void velocityChanged (ActionEvent e) {
		System.out.println ("VELOCITY CHANGED");

	}

	protected void elevatorMoved (int elevator, ActionEvent e) {
		System.out.println ("Elevator "+elevator+" moved");

	}

	private void floorButtonPressed (int floor, ActionEvent e) {
		System.out.println ("FUCK floor "+floor);

	}

	private void insideButtonPressed (int elevator, ActionEvent e) {
		System.out.println ("Button inside elevator "+elevator+" pressed");

	}

	public void runElevatorController () throws RemoteException, InterruptedException{
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
