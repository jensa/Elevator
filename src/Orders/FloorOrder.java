package Orders;

public class FloorOrder extends Order {
	public int floor;
	
	public FloorOrder (int fl){
		floor = fl;
		isInsideOrder = false;
	}

	@Override
	public int compareTo(Order o) {
		if (!(o instanceof FloorOrder))
			return -1;
		FloorOrder fo = (FloorOrder) o; 
		
		if (floor == fo.floor)
			return 0;
		else if (floor < fo.floor)
			return -1;
		else
			return 1;
	}

	@Override
	public int getDestination() {
		return floor;
	}
}
