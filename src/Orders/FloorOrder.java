package Orders;

public class FloorOrder extends Order {
	public int floor;
	public boolean goingUp;
	
	
	public FloorOrder (int fl, boolean gUp){
		floor = fl;
		isInsideOrder = false;
		goingUp = gUp;
	}

	@Override
	public int compareTo(Order o) {
		if (!(o instanceof FloorOrder))
			return -1;
		FloorOrder fo = (FloorOrder) o;

		if (floor == fo.floor && fo.goingUp == goingUp)
			return 1;
		else
			return 0;
	}

	@Override
	public int getDestination() {
		return floor;
	}
}
