package controller;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.concurrent.ConcurrentLinkedDeque;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import Orders.FloorOrder;
import Orders.InsideOrder;
import Orders.Order;

import elevator.rmi.Elevator;
import elevator.rmi.Motor;
import java.applet.*;

public class ElevatorThread implements Runnable{
	private static final int STOP_FLOOR = 32000;
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
		} // Get the Sound URL
		bing = Applet.newAudioClip(soundPath);
	}

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

	private void stop () throws RemoteException {
		controller.getMotor (id).stop ();
		elevatorOrders.clear ();
		emergencyOrders.clear ();
		
	}

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
			if (o.moveToFloor () == STOP_FLOOR || o.emergency)
				emergencyOrders.addFirst (o);
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

	public Order getCurrentOrder() {
		return currentOrder;
	}

}
