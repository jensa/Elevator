package Orders;

public abstract class Order {
	boolean isInsideOrder;
	
	public boolean isInsideOrder (){
		return isInsideOrder;
	}
	
	public abstract int moveToFloor ();
}
