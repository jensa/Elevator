package controller;

import java.util.concurrent.ConcurrentLinkedQueue;

public class ElevatorThread implements Runnable{
	RMI controller;
	int id;
	private ConcurrentLinkedQueue<Order> orders;
	
	public ElevatorThread (int id, RMI controller){
		this.controller = controller;
		this.id = id;
	}

	@Override
	public void run () {
		//Algorithm for parsing orders
	}
	
	public Order getNextOrder (){
		return orders.poll ();
	}
	
	public void addOrder (Order o){
		orders.add (o);
	}
	
	public ConcurrentLinkedQueue<Order> getOrders (){
		return orders;
	}

}
