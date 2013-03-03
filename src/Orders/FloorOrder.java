package Orders;

public class FloorOrder extends Order {
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

	@Override
	public int compareTo(Order o) {
		if (!(o instanceof FloorOrder))
			return -1;
		FloorOrder fo = (FloorOrder) o; 
		
		if (floor == fo.floor && goingUp == fo.goingUp)
			return 1;
		else
			return 0;
	}
}
