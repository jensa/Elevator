package controller;

import java.awt.event.ActionEvent;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.io.Serializable;
import elevator.rmi.Door;
import elevator.rmi.Elevator;
import elevator.rmi.Motor;
import elevator.rmi.RemoteActionListener;

public class ElevatorController implements Serializable{
	private static final long serialVersionUID = 1L;
	private RMI controller;
	private int numElevators;
	private int numFloors;
	private ElevatorThread[] elevatorThreads;


	public ElevatorController () throws RemoteException{
		controller = new RMI ();
		numElevators = controller.getNumberOfElevators ();
		numFloors = controller.getNumberOfFloors ();
		elevatorThreads = new ElevatorThread[numElevators];
		installListeners ();
		for (int i=0;i<numElevators;i++){
			ElevatorThread et = new ElevatorThread (i+1, controller);
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

	private void floorButtonPressed (int floor, ActionEvent e) throws RemoteException {
		// find the nearest? elevator that's not occupied and send it to the floor
		String command = e.getActionCommand ();
		int direction = Integer.parseInt (command.split (" ")[2]);
		Order o = new Order();
		o.argument = floor;
		o.type = Order.Type.MOVE;
		if (direction > 0){
			//passenger want to go upwards
		}else{
			//go down
		}
		elevatorThreads[0].addOrder (o);

	}

	private void insideButtonPressed (int elevator, ActionEvent e) {
		String command = e.getActionCommand ();
		int destination = Integer.parseInt (command.split (" ")[2]);
		Order o = new Order();
		o.argument = destination;
		o.type = Order.Type.MOVE;
		elevatorThreads[elevator].addOrder(o);
	}

	public void runElevatorController () throws RemoteException, InterruptedException{
	}

	public static void main(String[] args){
		//Set policy file so we don't get weird permission errors
		System.setProperty("java.security.policy","file:./rmi.policy");
		try {
			new ElevatorController ().runElevatorController ();
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
