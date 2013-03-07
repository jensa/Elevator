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
			InsideOrder io = new InsideOrder (0, ele.id);
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
		Boolean stillMoving = false;
		
		while (true) {
			for (ElevatorThread elevator : controller.getElevatorThreads ()){
				if (elevator.isMoving()) {
					stillMoving = true;
					break;
				}
			}
			if (stillMoving != true) {
				break;
			} else {
				System.out.println("Some elevator is still moving");
				Thread.sleep(1000);
			}
		}
	}
	
	public static void main(String[] args) {
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
