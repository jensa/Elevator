package controller;

import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.event.ActionEvent;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.io.File;
import java.io.Serializable;

import Orders.FloorOrder;
import Orders.InsideOrder;
import Orders.Order;
import elevator.rmi.RemoteActionListener;

public class ElevatorController implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private static final double VELOCITY_LIMIT_TRIGGER = 10;
	private RMI controller;
	private int numElevators;
	private int numFloors;
	private ElevatorThread[] elevatorThreads;
	private ConcurrentLinkedDeque<Order> elevatorOrders = new ConcurrentLinkedDeque<Order> ();
	private double[] currentPositions;
	private double[] lastPositions;
	private double velocity;
	private boolean overSpeedLimit = false;


	public ElevatorController () throws RemoteException{
		controller = new RMI ();
		numElevators = controller.getNumberOfElevators ();
		numFloors = controller.getNumberOfFloors ();
		elevatorThreads = new ElevatorThread[numElevators];
		currentPositions = new double[numElevators];
		lastPositions = new double[numElevators];
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
		String command = e.getActionCommand ();
		double rawVelocity = Double.parseDouble (command.split (" ")[1]);
		double vel = rawVelocity * 100000;
		velocity = vel;
		if (velocity > VELOCITY_LIMIT_TRIGGER)
			overSpeedLimit = true;
		else
			overSpeedLimit = false;
	}
	/**
	 * Update the position tracking arrays with a new position for the specified elevator
	 * @param elevator
	 * @param e
	 */
	protected void elevatorMoved (int elevator, ActionEvent e) {
		String command = e.getActionCommand ();
		double position = Double.parseDouble (command.split (" ")[2]);
		lastPositions[elevator] = currentPositions[elevator];
		currentPositions[elevator] = position;
		//System.out.println ("Elevator "+elevator+" moved");
	}

	private void floorButtonPressed (int floor, ActionEvent e) throws RemoteException {
		// find the nearest? elevator that's not occupied and send it to the floor
		String command = e.getActionCommand ();
		int direction = Integer.parseInt (command.split (" ")[2]);
		FloorOrder order = new FloorOrder (floor, direction > 0);
		elevatorOrders.addLast (order);
	}

	private void insideButtonPressed (int elevator, ActionEvent e) {
		String command = e.getActionCommand ();
		System.out.println (command);
		int destination = Integer.parseInt (command.split (" ")[2]);
		InsideOrder order = new InsideOrder (elevator, destination);
		elevatorOrders.addLast (order);
	}

	public void runElevatorController () throws RemoteException, InterruptedException{
		playMuzak ();
		while (true){
			while (!elevatorOrders.isEmpty ()){
				Order o = elevatorOrders.poll ();
				if (o.isInsideOrder ()){
					handleInsideOrder ((InsideOrder) o);
				} else
					handleFloorOrder ((FloorOrder) o);
			}
		}
	}

	private void playMuzak () {
		URL soundPath = null;
		try {
			File f = new File ("loop.wav");
			soundPath = f.toURI ().toURL ();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		AudioClip muzak = Applet.newAudioClip(soundPath);
		muzak.loop ();
		
	}

	private void handleFloorOrder (FloorOrder o) throws RemoteException {
		for (int i = 0; i < numElevators; i++) {
			if (elevatorThreads[i].isMoving ()) {
				if (o.compareTo(elevatorThreads[i].getCurrentOrder()) == 1) {
					System.out.println("Elevator " + (i+1) + " is already moving towards floor " + o.floor);
					return;
				}
			}
		}
		
		for (int i=0;i<numElevators;i++){
			if (!elevatorThreads[i].isMoving ()){
				if (elevatorIsOnFloor (o.floor, currentPositions[i])){
					System.out.println("Moving elevator " + i + " to floor " + o.floor);
					elevatorThreads[i].addOrder (o);
					return;
				}
			}
		}
		int closestElevator = -1;
		double dist = 10000000;
		for (int i=0;i<numElevators;i++){
			if (!elevatorThreads[i].isMoving ()){
				double distToFloor = Math.abs (o.floor-currentPositions[i]);
				if (distToFloor < dist){
					dist = distToFloor;
					closestElevator = i;
				}
			}
		}
		if (closestElevator > -1) {
			elevatorThreads[closestElevator].addOrder (o);
		} else {
			int minDistance = Integer.MAX_VALUE;
			
			for (int i = 0; i < numElevators; i++) {
				int destination;
				
				if (elevatorThreads[i].getLastOrder() == null)
					destination = elevatorThreads[i].getCurrentOrder().getDestination();
				else 
					destination = elevatorThreads[i].getLastOrder().getDestination();
				
				int distance = Math.abs(destination - o.getDestination());
				
				if (distance < minDistance) {
					closestElevator = i;
					minDistance = distance; 
				}
			}
			o.emergency = destinationIsOnTheWay (closestElevator, o.floor);
			System.out.println (o.emergency);
			elevatorThreads[closestElevator].addOrder (o);
		}
	}

	

	private boolean elevatorIsOnFloor (int floor, double d) {
		return d > floor-0.001 && d < floor+0.001;
	}

	private void handleInsideOrder (InsideOrder o) throws RemoteException {
		if (o.destination == ElevatorThread.STOP_FLOOR && !elevatorThreads[o.elevator].isMoving ())
			return;
		//Find out if this order is 'on the way', if so, make it an emergency order
		//so that the elevator will go there before its current destination
		o.emergency = destinationIsOnTheWay (o.elevator, o.destination);
		elevatorThreads[o.elevator].addOrder(o);
	}
	private boolean destinationIsOnTheWay (int elevator, int destination) {
		double curPos = currentPositions[elevator];
		ElevatorThread elevatorThread = elevatorThreads[elevator];
		if (curPos > lastPositions[elevator]){
			//going up
			//if the floor wanted is on the way, we'll give priority to this order
			if (destination > curPos && destination < elevatorThread.getMovingToFloor ())
				return true;
		} else if (curPos < lastPositions[elevator]){
			//going down
			if (destination < curPos && destination > elevatorThread.getMovingToFloor ())
				return true;
		}
		return false;
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
