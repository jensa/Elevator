package controller;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.Collections;
import java.util.Comparator;
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
				while (!elevatorOrders.isEmpty ()){
					currentOrder = getNextOrder ();
					if (currentOrder.moveToFloor () == STOP_FLOOR)
						stop ();
					else
						move (currentOrder.moveToFloor ());
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
		if (moveToFloor (floor, m, el)){
			setIsMoving (false);
			openDoor ();
		} else{
			setIsMoving (false);
		}
	}
	/**
	 * Open the elevator door and play a nice sound, and close it after a time
	 * TODO: Door opening time should be adjusted when velocity is changed
	 * @throws RemoteException
	 */
	private void openDoor () throws RemoteException{
		controller.getDoor (id).open ();
		bing.play ();
		try {
			Thread.sleep (2000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		controller.getDoor (id).close ();
	}
	
	private boolean moveToFloor (int floor, Motor m, Elevator el) throws RemoteException{
		setMovingToFloor (floor);
		if (el.whereIs () > floor){
			while (el.whereIs () > floor){
				if (!emergencyOrders.isEmpty ()){
					elevatorOrders.addFirst (new InsideOrder (-1, floor));
					elevatorOrders.addFirst (emergencyOrders.poll ());
					return false;
				}else
					m.down ();
			}
		} else if (el.whereIs () < floor){
			while (el.whereIs () < floor){
				if (!emergencyOrders.isEmpty ()){
					elevatorOrders.addFirst (new InsideOrder (-1, floor));
					elevatorOrders.addFirst (emergencyOrders.poll ());
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
				if (o1.moveToFloor () < o2.moveToFloor ())
					return 1;
				else if (o1.moveToFloor () > o2.moveToFloor ())
					return -1;
				return 0;
			}
			
		};
	}
	
	public Comparator<Order> getGoingDownComparator (){
		return new Comparator<Order> (){

			@Override
			public int compare (Order o1, Order o2) {
				if (o1.moveToFloor () > o2.moveToFloor ())
					return 1;
				else if (o1.moveToFloor () < o2.moveToFloor ())
					return -1;
				return 0;
			}
			
		};
	}

	public Order getNextOrder (){
		return elevatorOrders.poll ();
	}

	public void addOrder (Order o){
		if (o instanceof FloorOrder) {
			if (!elevatorOrders.isEmpty()) {
				if (elevatorOrders.peekLast().compareTo(o) == 1) {
					System.out.println("Duplicate FloorOrder, not added to the queue");
					return;
				}
			}
			elevatorOrders.addLast(o);
		} else {
			if (!elevatorOrders.isEmpty()) {
				if (elevatorOrders.peekFirst().compareTo(o) == 1) {
					System.out.println("Duplicate InsideOrder, not added to the queue");
					return;
				}
			}
			if (o.moveToFloor () == STOP_FLOOR)
				emergencyOrders.addFirst (o);
			else if (o.emergency)
				emergencyOrders.addLast (o);
			else
				elevatorOrders.addFirst (o);
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
