package controller;

import java.io.Serializable;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import elevator.rmi.Door;
import elevator.rmi.Doors;
import elevator.rmi.Elevator;
import elevator.rmi.Elevators;
import elevator.rmi.GetAll;
import elevator.rmi.Motor;
import elevator.rmi.Motors;
import elevator.rmi.RemoteActionListener;
import elevator.rmi.Scale;
import elevator.rmi.Scales;

public class RMI implements Serializable, Remote{
	private static final long serialVersionUID = 1L;
	public GetAll get;
	
	public RMI (){
		init ();
	}
	
	private void init (){
		try {
			setupRegistry ();
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (NotBoundException e) {
			e.printStackTrace();
		}
	}
	/**
	 * Sets up securitymanager and RMI registry.
	 * @throws RemoteException
	 * @throws NotBoundException
	 */
	private void setupRegistry () throws RemoteException, NotBoundException{
		if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
		String name = "GetAll";
        Registry registry = LocateRegistry.getRegistry(null);
        get = (GetAll) registry.lookup(name);
	}
	
	public Door getDoor (int index){
		try {
			return get.getDoor (index);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return null;
	}
	  /**
	   * Returns an array of objects with the <code>Door</code>
	   *    interface which is used for controlling a door of the elevator with the
	   *    given number via Java RMI.
	   * @param number An array of integer numbers of elevators whose
	   *     <code>Door</code> to get.
	   * @return An array of objects with the <code>Door</code> interface.
	   * @throws RemoteException if failed to get/create <code>Door</code> objects.
	   * @see elevator.rmi.Door
	   */
	  public Door[] getDoor(int[] number){
		  try {
			return get.getDoor (number);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		  return null;
	  }
	  /**
	   * Returns an object with the <code>Doors</code> interface that
	   *    is used for controlling doors of elevators via Java RMI.
	   * @return An object with the <code>Doors</code> interface.
	   * @throws RemoteException if failed to get/create an <code>Doors</code> object.
	   * @see elevator.rmi.Doors
	   */
	  public Doors getDoors(){
		  try {
			return get.getDoors ();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		  return null;
	  }
	  /**
	   * Returns an object with the <code>Elevator</code> interface
	   *    that is used for controlling all components (the motor, the door, and
	   *    the scale) of the elevator with the given number
	   *    via Java RMI.
	   * @param number The integer number of elevator whose <code>Elevator</code> to get.
	   * @return An object with the <code>Elevator</code> interface.
	   * @throws RemoteException if failed to get/create an <code>Elevator</code> object.
	   * @see elevator.rmi.Elevator
	   */
	  public Elevator getElevator(int number){
		  try {
			return get.getElevator (number);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return null;
	  }
	  /**
	   * Returns an array of objects with the <code>Elevator</code>
	   *    interface which is used for controlling all components (a motor, a door,
	   *    a scale) of an elevators with a given
	   *    number via Java RMI.
	   * @param number An array of integer numbers of elevators whose
	   *     <code>Elevator</code> to get.
	   * @return An array of objects with the <code>Elevator</code> interface.
	   * @throws RemoteException if failed to get/create <code>Elevator</code> objects.
	   * @see elevator.rmi.Elevator
	   */
	  public Elevator[] getElevator(int[] number){
		  try {
			return get.getElevator (number);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return null;
	  }
	  /**
	   * Returns an object with the <code>Elevators</code> interface that is
	   *    used for controlling all components (motors, doors, scales) of all
	   *    elevators via Java RMI.
	   * @return An object with the <code>Elevators</code> interface.
	   * @throws RemoteException if failed to get/create an <code>Elevators</code> object.
	   * @see elevator.rmi.Elevators
	   */
	  public Elevators getElevators(){
		  try {
			return get.getElevators ();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return null;
	  }
	  /**
	   * Returns the velocity of an elevator in "floor units" per millisecond.
	   * @return A double velocity of an elevator in "floor units" per millisecond.
	   * @throws RemoteException is failed to execute
	   */
	  public double getVelocity(){
		  try {
			return get.getVelocity ();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return 0;
	  }
	  /**
	   * Returns the total number of elevators.
	   * @return An interger number of elevators.
	   * @throws RemoteException is failed to execute
	   */
	  public int getNumberOfElevators(){
		  try {
			return get.getNumberOfElevators ();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return 0;
	  }
	  /**
	   * Returns the total number of floors.
	   * @return An interger number of floors.
	   * @throws RemoteException is failed to execute
	   */
	  public int getNumberOfFloors(){
		  try {
			return get.getNumberOfFloors ();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return 0;
	  }
	  /**
	   * Returns an object with the <code>Motor</code> interface which is
	   *    used for controlling a motor of the elevator with the given number
	   *    via Java RMI.
	   * @param number An integer number of elevator whose <code>Motor</code>
	   *      to get.
	   * @return An object with the <code>Motor</code> interface.
	   * @throws RemoteException if failed to get/create an <code>Motor</code> object.
	   * @see elevator.rmi.Motor
	   */
	  public Motor getMotor(int number){
		  try {
			return get.getMotor (number);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return null;
	  }
	  /**
	   * Returns an array of objects with the <code>Motor</code> interface which is
	   *    used for controlling a motor of the elevator with the given number
	   *    via Java RMI.
	   * @param number An array of integer numbers of elevators whose
	   *     <code>Motor</code> to get.
	   * @return An array of objects with the <code>Motor</code> interface.
	   * @throws RemoteException if failed to get/create <code>Motor</code> objects.
	   * @see elevator.rmi.Motor
	   */
	  public Motor[] getMotor(int[] number){
		  try {
			return get.getMotor (number);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return null;
	  }
	  /**
	   * Returns an object with the <code>Motors</code> interface that is
	   *    used for controlling motors of elevators via Java RMI.
	   * @return An object with the <code>Motors</code> interface.
	   * @throws RemoteException if failed to get/create an <code>Motors</code> object.
	   * @see elevator.rmi.Motors
	   */
	  public Motors getMotors() {
		  try {
			return get.getMotors ();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return null;
	  }
	  /**
	   * Returns an object with the <code>Scale</code> interface which is
	   *    used for controlling a scale of the elevator with the given number
	   *    via Java RMI.
	   * @param number An integer number of elevator whose <code>Scale</code>
	   *     to get.
	   * @return An object with the <code>Scale</code> interface.
	   * @throws RemoteException if failed to get an <code>Scale</code> object.
	   * @see elevator.rmi.Scale
	   */
	  public Scale getScale(int number){
		  try {
			return get.getScale (number);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return null;
	  }
	  /**
	   * Returns an array of objects with the <code>Scale</code>
	   *    interface which is used for controlling a scale of the elevator with
	   *    the given number via Java RMI.
	   * @param number An array of integer numbers of elevators whose
	   *     <code>Scale</code> to get.
	   * @return An array of objects with the <code>Scale</code> interface.
	   * @throws RemoteException if failed to get/create <code>Scale</code> objects.
	   * @see elevator.rmi.Scale
	   */
	  public Scale[] getScale(int[] number){
		  try {
			return get.getScale (number);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return null;
	  }
	  /**
	   * Returns an object with the <code>Scales</code> interface that is
	   *    used for controlling scales of elevators via Java RMI.
	   * @return An object with the <code>Scales</code> interface.
	   * @throws RemoteException if failed to get/create an <code>Scales</code> object.
	   * @see elevator.rmi.Scales
	   */
	  public Scales getScales(){
		  try {
			return get.getScales ();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return null;
	  }
	  /**
	   * Returns the number of the top floor.
	   * @return An interger number of the top floor.
	   * @throws RemoteException is failed to execute
	   */
	  public int getTopFloor(){
		  try {
			return get.getTopFloor ();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return -1;
	  }
	  /**
	   * Creates <code>FloorListener</code> that receives action events
	   * from floor buttons located at a given floor and forwards the events to the
	   * specified action listener via the listener's <code>RemoteActionListener</code> remote
	   * interface.
	   * @param floor is the relative number (0, 1, ...) of the floor whose
	   *  <code>FloorListener</code> to make
	   * @param listener is <code>RemoteActionListener</code> to forward events to
	   * @throws RemoteException if failed to make an
	   *    <code>InsideListener</code> object.
	   * @see elevator.rmi.RemoteActionListener
	   */
	  public void makeFloorListener(int floor, RemoteActionListener listener){
		  try {
			get.makeFloorListener (floor, listener);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	  }
	  /**
	   * Creates <code>InsideListener</code> that receives action events
	   * from inside panel buttons of a given elevator and forwards the events to the
	   * specified action listener via the listener's <code>RemoteActionListener</code> remote
	   * interface.
	   * @param number is the relative number (0, 1, ...) of the elevator whose
	   *  <code>InsideListener</code> to make
	   * @param listener is <code>RemoteActionListener</code> to forward events to
	   * @throws RemoteException if failed to make an
	   *    <code>InsideListener</code> object.
	   * @see elevator.rmi.RemoteActionListener
	   */
	  public void makeInsideListener(int number, RemoteActionListener listener){
		  try {
			get.makeInsideListener (number, listener);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	  }
	  /**
	   * Creates <code>PositionListener</code> that receives action events
	   * from the elevator timer and forwards the events with a current position of
	   * an elevator with the given number to the
	   * specified action listener via the listener's <code>RemoteActionListener</code> remote
	   * interface.
	   * @param number is the relative number (0, 1, ...) of the elevator whose
	   *  <code>InsideListener</code> to make
	   * @param listener is <code>RemoteActionListener</code> to forward events to
	   * @throws RemoteException if failed to make an
	   *    <code>PositionListener</code> object.
	   * @see elevator.rmi.RemoteActionListener
	   */
	  public void makePositionListener(int number, RemoteActionListener listener){
		  try {
			get.makePositionListener (number, listener);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	  }
	  /**
	   * Creates <code>VelocityListener</code> that receives an action event
	   * when the velocity of elevators has changed by the velocity slider.
	   * @param listener is <code>RemoteActionListener</code> to forward events to
	   * @throws RemoteException if failed to make an
	   *    <code>PositionListener</code> object.
	   * @see elevator.rmi.RemoteActionListener
	   */
	  public void makeVelocityListener(RemoteActionListener listener){
		  try {
			get.makeVelocityListener (listener);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	  }

}
