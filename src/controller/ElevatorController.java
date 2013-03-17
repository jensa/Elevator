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
	
	private RMI controller;
	private int numElevators;
	private int numFloors;
	private ElevatorThread[] elevatorThreads;
	private ConcurrentLinkedDeque<Order> elevatorOrders = new ConcurrentLinkedDeque<Order> ();
	private double[] currentPositions;
	private double[] lastPositions;
	/**
	 * Creates a new ElevatorController, initializes Elevator threads and RMI communication/listeners.
	 * @throws RemoteException if something goes wrong with the RMI communication.
	 */
	public ElevatorController () throws RemoteException{
		controller = new RMI ();
		numElevators = controller.getNumberOfElevators ();
		numFloors = controller.getNumberOfFloors ();
		elevatorThreads = new ElevatorThread[numElevators];
		currentPositions = new double[numElevators];
		lastPositions = new double[numElevators];
		for (int i=0;i<numElevators;i++){
			currentPositions[i] = controller.getElevator (i+1).whereIs ();
			lastPositions[i] = controller.getElevator (i+1).whereIs ();
		}
		installListeners ();
		for (int i=0;i<numElevators;i++){
			ElevatorThread et = new ElevatorThread (i+1, controller);
			elevatorThreads[i] = et;
			new Thread (et).start ();
		}
	}
	/**
	 * Install RMI listeners which the GUI/model server activates.
	 * @throws RemoteException
	 */
	private void installListeners () throws RemoteException {
		for (int i=0;i<numFloors;i++){
			final int floor = i;
			RemoteActionListener floorListener = (RemoteActionListener) UnicastRemoteObject.exportObject (new RemoteActionListener(){
				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed (ActionEvent e)
						throws RemoteException {
					synchronized (this){
						this.notify ();
					}
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
					synchronized (this){
						this.notify ();
					}
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
	/**
	 * Called by listener when the velocity slider is moved.
	 * not used.
	 * @param e
	 */
	protected void velocityChanged (ActionEvent e) {
	}
	/**
	 * Update the position tracking arrays with a new position for the specified elevator
	 * @param elevator the index of the elevator that moved
	 * @param e the movement event (new position)
	 */
	protected void elevatorMoved (int elevator, ActionEvent e) {
		String command = e.getActionCommand ();
		double position = Double.parseDouble (command.split (" ")[2]);
		lastPositions[elevator] = currentPositions[elevator];
		currentPositions[elevator] = position;
		//System.out.println ("Elevator "+elevator+" moved");
	}
	/**
	 * Called by listener when a button on a floor is pressed
	 * @param floor the flor which the button that was pressed is on
	 * @param e the event itself (which direction to go in)
	 * @throws RemoteException
	 */
	private void floorButtonPressed (int floor, ActionEvent e) throws RemoteException {
		// find the nearest? elevator that's not occupied and send it to the floor
		String command = e.getActionCommand ();
		int direction = Integer.parseInt (command.split (" ")[2]);
		FloorOrder order = new FloorOrder (floor, direction > 0);
		elevatorOrders.addLast (order);
	}
	/**
	 * Called by listener when a button inside an elevator is pressed
	 * @param elevator index of elevator
	 * @param e the event itself (which button was pressed)
	 */
	private void insideButtonPressed (int elevator, ActionEvent e) {
		String command = e.getActionCommand ();
		int destination = Integer.parseInt (command.split (" ")[2]);
		InsideOrder order = new InsideOrder (elevator, destination);
		elevatorOrders.addLast (order);
	}
	/**
	 * Main/master thread loop. Waits on this object, and when notified, 
	 * checks its dispatch queue until empty and send messages to separate elevator threads
	 * @throws RemoteException
	 * @throws InterruptedException
	 */
	public void runElevatorController () throws RemoteException, InterruptedException{
		playMuzak ();
		while (true){
			synchronized (this){
				this.wait (1000);
			}
			while (!elevatorOrders.isEmpty ()){
				Order o = elevatorOrders.poll ();
				if (o.isInsideOrder ()){
					handleInsideOrder ((InsideOrder) o);
				} else
					handleFloorOrder ((FloorOrder) o);
			}
		}
	}
	/**
	 * continously plays a jazz version of the Pokémon theme as elevator music
	 */
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
	/**
	 * Floor order calculation algorithm. Decides on which elevator should get which request (button press).
	 * @param o The request (order) containing information on which floor it was issued from, and what direction it has.
	 * @throws RemoteException
	 */
	public void handleFloorOrder (FloorOrder o) throws RemoteException {
		//Check if an elevator is on the way to the requested floor
		double[] costs = new double[numElevators];
		for (int i = 0; i < numElevators; i++) {
			if (elevatorThreads[i].isMoving ()) {
				if (o.compareTo(elevatorThreads[i].getCurrentOrder()) == 1) {
					System.out.println("Elevator " + (i+1) + " is already moving towards floor " + o.floor);
					return;
				}
			}
		}
		//Check if any elevator is standing still on the requested floor
		for (int i=0;i<numElevators;i++){
			if (!elevatorThreads[i].isMoving ()){
				if (elevatorIsOnFloor (o.floor, currentPositions[i])){
					System.out.println("Moving elevator " + i + " to floor " + o.floor);
					elevatorThreads[i].addOrder (o);
					return;
				}
			}
		}
		//give a destination cost to elevators based on the distance to the requested floor from their current final destination
		for (int i = 0; i < numElevators; i++) {
			if (!elevatorThreads[i].isMoving ()){
				costs[i] = Math.abs (o.floor-currentPositions[i]);
				continue;
			}
			int destination = elevatorThreads[i].getLastOrder().getDestination();
			int distance = (1+Math.abs(destination - o.getDestination()))*3;
			boolean onTheWay = destinationIsOnTheWay (i, o.floor) && elevatorGoingUp (i) == o.goingUp;
			costs[i] = distance;
			if (onTheWay)
				costs[i] *= 0.1;
			if (goingToFloorWithDifferentDirection (i, o))
				costs[i] *= 100; // only take this elevator in the worst case
			if (orderIsOnTheImpliedWay (i,o))
				costs[i] *=0.3;
		}
		double leastScore = Double.MAX_VALUE;
		int closestElevator = -1;
		for (int i=0;i<numElevators;i++){
			if (costs[i] < leastScore){
				closestElevator = i;
				leastScore = costs[i];
			}
		}
		boolean onTheWay = destinationIsOnTheWay (closestElevator, o.floor);
		boolean goingUp = currentPositions[closestElevator] > lastPositions[closestElevator];
		if (onTheWay && goingUp == o.goingUp)
			o.emergency = true;
		elevatorThreads[closestElevator].addOrder (o);
	}
	/**
	 * Compares the direction and destination of the current order of the specified elevator and the specified FloorOrder.
	 * Return true if the destination, but not the direction matches.
	 * This is so that we can avoid the case ofone elevator getting both an order to go down and up from the same floor.
	 * @param i
	 * @param floor
	 * @param goingUp
	 * @return true if going to the same floor, but with different directions
	 */
	private boolean goingToFloorWithDifferentDirection (int i, FloorOrder o){
		if (elevatorThreads[i].getCurrentOrder () != null){
			if (elevatorThreads[i].getCurrentOrder () instanceof FloorOrder){
				FloorOrder current = (FloorOrder) elevatorThreads[i].getCurrentOrder ();
				if (current.floor == o.floor && current.goingUp != o.goingUp)
					return true;
			}
		}
		return false;
	}
	/**
	 * Check if the specified order corresponds to the direction of 
	 * the order currently being handled by the elevator with the specified id.
	 * 
	 * Example:
	 * 
	 * current order is 'go up' from floor 2. specified order is 'go up' from floor 4.
	 * floor 4 is 'up' from floor 2, and so it lies on the elevator's future route.
	 * @param id the id of the elevator to check
	 * @param o the order to check
	 * @return true if the specified order in on the elevator's future route, false otherwise
	 */
	private boolean orderIsOnTheImpliedWay (int id, FloorOrder o){
		if (elevatorThreads[id].getCurrentOrder () != null){
			if (elevatorThreads[id].getCurrentOrder () instanceof FloorOrder){
				FloorOrder current = (FloorOrder) elevatorThreads[id].getCurrentOrder ();
				if (current.floor > o.floor && !current.goingUp && current.goingUp == o.goingUp)
					return true;
				if (current.floor < o.floor && current.goingUp && current.goingUp == o.goingUp)
					return true;
			}
		}
		return false;
	}

	/**
	 * Check if elevator is going up
	 * @param el elevator index
	 * @return true if going up, false if going down
	 */
	private boolean elevatorGoingUp (int el){
		return currentPositions[el] > lastPositions[el];
	}
	/**
	 * Checks if a position is on a specified floor, with 0.001 precision.
	 * @param floor the floor ot check
	 * @param position the elevator's position
	 * @return true if the position is on the specified floor, false otherwise.
	 */
	private boolean elevatorIsOnFloor (int floor, double position) {
		return position > floor-0.001 && position < floor+0.001;
	}
	/**
	 * Pass on orders coming from inside an elevator. Decides whether or not the order 
	 * should be treated as an emergency order (i.e be dealt with during travel)
	 * @param o
	 * @throws RemoteException
	 */
	public void handleInsideOrder (InsideOrder o) throws RemoteException {
		if (o.destination == ElevatorThread.STOP_FLOOR && !elevatorThreads[o.elevator].isMoving ())
			return;
		//Find out if this order is 'on the way', if so, make it an emergency order
		//so that the elevator will go there before its current destination
		o.emergency = destinationIsOnTheWay (o.elevator, o.destination);
		elevatorThreads[o.elevator].addOrder(o);
	}
	/**
	 * Checks whether the specified destination is on the way 
	 * to a specified elevators current final destination.
	 * @param elevator the elevator to check
	 * @param destination the floor destination
	 * @return true if on the way, false otherwise
	 */
	private boolean destinationIsOnTheWay (int elevator, int destination) {
		double curPos = currentPositions[elevator];
		ElevatorThread elevatorThread = elevatorThreads[elevator];
		if (curPos > lastPositions[elevator]){
			//going up
			if (destination > curPos && destination < elevatorThread.getMovingToFloor ())
				return true;
		} else if (curPos < lastPositions[elevator]){
			//going down
			if (destination < curPos && destination > elevatorThread.getMovingToFloor ())
				return true;
		}
		return false;
	}
	/**
	 * Return all elevator threads
	 * @return an array containing all elevator threads.
	 */
	public ElevatorThread[] getElevatorThreads () {
		return elevatorThreads;
	}
	/**
	 * Main method. Set security policy and start controller
	 * @param args
	 */
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
