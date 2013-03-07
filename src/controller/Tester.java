package controller;

import java.rmi.RemoteException;

import Orders.*;

public class Tester {

	private ElevatorController controller;

	public Tester (ElevatorController ec) {
		controller = ec;
	}

	public void testFloorOrder () throws RemoteException {
		
		FloorOrder fo = new FloorOrder (3, true);
		System.out.println("TEST: Floorbutton 3UP pressed");
		controller.handleFloorOrder(fo);
		
		fo = new FloorOrder (3, false);
		System.out.println("TEST: Floorbutton 3DOWN pressed");
		controller.handleFloorOrder(fo);
	}
	
	public void testInsideOrder () {
		
	}
}
