package Orders;

public class FloorOrder extends ServerOrder{
	public int floor;
	public boolean goingUp;
	
	public FloorOrder (int fl, boolean gu){
		floor = fl;
		goingUp = gu;
		isInsideOrder = false;
	}
}
