package controller;

import java.rmi.RemoteException;
import java.util.concurrent.ConcurrentLinkedDeque;

import Orders.ElevatorOrder;

import elevator.rmi.Elevator;
import elevator.rmi.Motor;

public class ElevatorThread implements Runnable{
	RMI controller;
	int id;
	private ConcurrentLinkedDeque<ElevatorOrder> elevatorOrders = new ConcurrentLinkedDeque<ElevatorOrder> ();
	
	public ElevatorThread (int id, RMI controller){
		this.controller = controller;
		this.id = id;
	}

	@Override
	public void run () {
		try{
			while (true){
				while (!elevatorOrders.isEmpty ()){
					ElevatorOrder o = getNextOrder ();
					switch (o.type){
					case MOVE: move (o.argument);break;
					}
				}
			}
		}catch (Exception e){
			e.printStackTrace ();
		}
	}
	
	private void move (int floor) throws RemoteException{
		Motor m = controller.getMotor (id);
		Elevator el = controller.getElevator (id);
		moveToFloor (floor, m, el);
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
				m.up ();
			}
		}

		m.stop ();
	}

	public ElevatorOrder getNextOrder (){
		return elevatorOrders.poll ();
	}
	
	public void addOrder (ElevatorOrder o){
		elevatorOrders.addFirst (o);
	}
	
	public ConcurrentLinkedDeque<ElevatorOrder> getOrders (){
		return elevatorOrders;
	}

}
