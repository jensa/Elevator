package controller;

import java.rmi.RemoteException;
import java.util.concurrent.ConcurrentLinkedDeque;
import Orders.Order;

import elevator.rmi.Elevator;
import elevator.rmi.Motor;

public class ElevatorThread implements Runnable{
	RMI controller;
	int id;
	private boolean isMoving = false;
	private ConcurrentLinkedDeque<Order> elevatorOrders = new ConcurrentLinkedDeque<Order> ();
	
	public ElevatorThread (int id, RMI controller){
		this.controller = controller;
		this.id = id;
	}

	@Override
	public void run () {
		try{
			while (true){
				while (!elevatorOrders.isEmpty ()){
					Order o = getNextOrder ();
					move (o.moveToFloor ());
				}
			}
		}catch (Exception e){
			e.printStackTrace ();
		}
	}
	
	private void move (int floor) throws RemoteException{
		setIsMoving (true);
		Motor m = controller.getMotor (id);
		Elevator el = controller.getElevator (id);
		moveToFloor (floor, m, el);
		setIsMoving (false);
		openDoor ();
		
	}
	
	private void openDoor () throws RemoteException{
		controller.getDoor (id).open ();
		try {
			Thread.sleep (2000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		controller.getDoor (id).close ();
	}

	private void moveToFloor (int floor, Motor m, Elevator el) throws RemoteException{
		if (el.whereIs () > floor){
			while (el.whereIs () > floor){
				m.down ();
			}
		} else if (el.whereIs () < floor){
			while (el.whereIs () < floor){
				System.out.println ("Elevator "+id+"moving up");
				m.up ();
			}
		}

		m.stop ();
	}

	public Order getNextOrder (){
		return elevatorOrders.poll ();
	}
	
	public void addOrder (Order o){
		elevatorOrders.addFirst (o);
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

}
