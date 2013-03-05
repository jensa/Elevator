package controller;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Stack;
import java.util.concurrent.ConcurrentLinkedDeque;

import Orders.FloorOrder;
import Orders.InsideOrder;
import Orders.Order;

import elevator.rmi.Elevator;
import elevator.rmi.Motor;
import java.applet.*;

public class ElevatorThread implements Runnable{
	public static final int STOP_FLOOR = 32000;
	RMI controller;
	int id;
	private Order currentOrder;
	private AudioClip bing;
	private boolean isMoving = false;
	private ConcurrentLinkedDeque<Order> emergencyOrders = new ConcurrentLinkedDeque<Order> ();
	private ConcurrentLinkedDeque<Order> elevatorOrders = new ConcurrentLinkedDeque<Order> ();
	private int movingToFloor;

	public ElevatorThread (int id, RMI controller){
		this.controller = controller;
		this.id = id;
		URL soundPath = null;
		try {
			File f = new File ("sound.wav");
			soundPath = f.toURI ().toURL ();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		bing = Applet.newAudioClip(soundPath);
	}
	/**
	 * Runs this elevator thread, checking for new orders added to it
	 */
	@Override
	public void run () {
		try{
			while (true){
				while (emergencyOrders.isEmpty () && !elevatorOrders.isEmpty ()){
					currentOrder = getNextOrder ();
					move (currentOrder.getDestination ());
				}
				while (!emergencyOrders.isEmpty ()){
					currentOrder = getNextEmergencyOrder ();
					if (currentOrder.getDestination () == STOP_FLOOR)
						stop ();
					else
						move (currentOrder.getDestination ());
				}
			}
		}catch (Exception e){
			e.printStackTrace ();
		}
	}
	/**
	 * Stop elevator and clear all existing orders
	 * @throws RemoteException
	 */
	private void stop () throws RemoteException {
		controller.getMotor (id).stop ();
		elevatorOrders.clear ();
		emergencyOrders.clear ();

	}
	/**
	 * Move elevator to the specified floor
	 * @param floor
	 * @throws RemoteException
	 */
	private void move (int floor) throws RemoteException{
		setIsMoving (true);
		Motor m = controller.getMotor (id);
		Elevator el = controller.getElevator (id);
		if (getDestination (floor, m, el)){
			setIsMoving (false);
			openDoor ();
		} else{
			setIsMoving (false);
		}
	}
	/**
	 * Open the elevator door and play a nice sound, and close it after a time
	 * @throws RemoteException
	 */
	private void openDoor () throws RemoteException{
		controller.getDoor (id).open ();
		bing.play ();
		try {
			double velocity = controller.getVelocity() * 100000;
			int sleeptime = (int) (30000/velocity);
			Thread.sleep (sleeptime);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		controller.getDoor (id).close ();
	}

	private boolean getDestination (int floor, Motor m, Elevator el) throws RemoteException{
		setMovingToFloor (floor);
		if (el.whereIs () > floor){
			while (el.whereIs () > floor){
				if (!emergencyOrders.isEmpty ()){
					elevatorOrders.addFirst (new InsideOrder (-1, floor));
					return false;
				}else
					m.down ();
			}
		} else if (el.whereIs () < floor){
			while (el.whereIs () < floor){
				if (!emergencyOrders.isEmpty ()){
					elevatorOrders.addFirst (new InsideOrder (-1, floor));
					return false;
				}else
					m.up ();
			}
		}
		m.stop ();
		return true;
	}

	public Comparator<Order> getGoingUpComparator (){
		return new Comparator<Order> (){

			@Override
			public int compare (Order o1, Order o2) {
				if (o1.getDestination () < o2.getDestination ())
					return 1;
				else if (o1.getDestination () > o2.getDestination ())
					return -1;
				return 0;
			}

		};
	}

	public Comparator<Order> getGoingDownComparator (){
		return new Comparator<Order> (){

			@Override
			public int compare (Order o1, Order o2) {
				if (o1.getDestination () > o2.getDestination ())
					return 1;
				else if (o1.getDestination () < o2.getDestination ())
					return -1;
				return 0;
			}

		};
	}

	public Order getNextOrder (){
		return elevatorOrders.poll ();
	}

	public Order getNextEmergencyOrder (){
		return emergencyOrders.poll ();
	}

	public void addOrder (Order o) throws RemoteException{
		if (checkForDuplicate (o, elevatorOrders))
			return;
		if (o.getDestination () == STOP_FLOOR)
			emergencyOrders.addFirst (o);
		else if (o.emergency){
			boolean goingUp = movingToFloor > controller.getElevator (id).whereIs ();
			if (emergencyOrders.isEmpty ()){
				emergencyOrders.addFirst (o);
				return;
			}
			addEmergencyOrder (o, goingUp);
			emergencyOrders.addLast (o);
		}else
			elevatorOrders.addFirst (o);
	}

	private boolean checkForDuplicate (Order o, ConcurrentLinkedDeque<Order> queue) {

		boolean isDuplicate = false;
		Iterator<Order> it = queue.iterator();

		if (currentOrder != null) {
			isDuplicate = (currentOrder.compareTo(o) == 1);
		}
		
		while (it.hasNext()) {
			if (o.compareTo(it.next()) == 1)
				isDuplicate = true;
		}

		if (isDuplicate) {
			System.out.println("Duplicate Order, not added to the queue");
			return true;
		}

		return false;
	}
	private void addEmergencyOrder (Order o, boolean goingUp) {
		if (checkForDuplicate (o, emergencyOrders))
			return;

		Stack<Order> temp = new Stack<Order> ();
		Order cur = emergencyOrders.getFirst ();
		if (goingUp){
			while (!emergencyOrders.isEmpty () && cur.getDestination () < o.getDestination ()){
				temp.push (emergencyOrders.poll ());
				if (!emergencyOrders.isEmpty ())
					cur = emergencyOrders.peek ();
			}
			emergencyOrders.addFirst (o);
			while (!temp.isEmpty ())
				emergencyOrders.addFirst (temp.pop ());
		} else{
			while (!emergencyOrders.isEmpty () && cur.getDestination () > o.getDestination ()){
				temp.push (emergencyOrders.poll ());
			}
			emergencyOrders.addFirst (o);
			while (!temp.isEmpty ())
				emergencyOrders.addFirst (temp.pop ());
		}

	}
	public ConcurrentLinkedDeque<Order> getOrders (){
		return elevatorOrders;
	}

	public synchronized boolean isMoving (){
		return isMoving;
	}

	public synchronized void setIsMoving (boolean isMo){
		isMoving = isMo;
	}

	public synchronized void setMovingToFloor (int fl){
		movingToFloor = fl;
	}

	public synchronized int getMovingToFloor (){
		return movingToFloor;
	}

	public Order getCurrentOrder () {
		return currentOrder;
	}

	public Order getLastOrder () {
		return elevatorOrders.peekLast();
	}
}
