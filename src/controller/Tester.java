package controller;

import java.rmi.RemoteException;

import Orders.*;

public class Tester {

	private ElevatorController controller;

	public Tester () {
		try {
			controller = new ElevatorController ();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	public void testFloorOrder () throws RemoteException, InterruptedException {
		
		for (ElevatorThread ele : controller.getElevatorThreads()) {
			InsideOrder io = new InsideOrder (ele.id-1, 0);
			System.out.println("Moving elevator " + ele.id + " to ground floor");
			controller.handleInsideOrder(io);
		}
		
		stillMoving ();
		
		System.out.println("All elevators are now at ground floor");
		
		FloorOrder fo = new FloorOrder (3, true);
		System.out.println("TEST: Floorbutton 3UP pressed");
		controller.handleFloorOrder(fo);
		
		fo = new FloorOrder (3, false);
		System.out.println("TEST: Floorbutton 3DOWN pressed");
		controller.handleFloorOrder(fo);
		
		stillMoving ();
		
		fo = new FloorOrder (5, false);
		System.out.println("TEST: Floorbutton 5DOWN pressed");
		controller.handleFloorOrder(fo);
		
		fo = new FloorOrder (5, false);
		System.out.println("TEST: Floorbutton 5DOWN pressed");
		controller.handleFloorOrder(fo);
	}

	public void testInsideOrder () {
		
	}
	
	private void stillMoving() throws InterruptedException {
		Boolean stillMoving;
		Thread.sleep(1000);
		while (true) {
			stillMoving = false;
			for (ElevatorThread elevator : controller.getElevatorThreads ()){
				if (elevator.isMoving()) {
					stillMoving = true;
					break;
				}
			}
			if (stillMoving != true) {
				System.out.println("No elevator is moving, starting the next test..");
				break;
			}
		}
	}
	
	public static void main(String[] args) {
		System.setProperty("java.security.policy","file:./rmi.policy");
		Tester t = new Tester ();
		try {
			t.testFloorOrder();
			t.testInsideOrder();
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
