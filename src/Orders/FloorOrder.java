package Orders;

public class FloorOrder extends Order{
	public int floor;
	public boolean goingUp;
	
	public FloorOrder (int fl, boolean gu){
		floor = fl;
		goingUp = gu;
		isInsideOrder = false;
	}
	
	@Override
	public int moveToFloor (){
		return floor;
	}
}
