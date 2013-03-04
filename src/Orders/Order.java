package Orders;

public abstract class Order implements Comparable<Order> {
	boolean isInsideOrder;
	public boolean emergency = false;
	
	public boolean isInsideOrder (){
		return isInsideOrder;
	}
	
	public boolean isEmergency (){
		return emergency;
	}
	
	public abstract int moveToFloor ();
	
	
}
