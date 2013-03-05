package Orders;

public class InsideOrder extends Order{
	
	public int destination;
	public int elevator;
	
	public InsideOrder (int ele, int dest){
		destination = dest;
		elevator = ele;
		isInsideOrder = true;
	}

	@Override
	public int compareTo(Order o) {
		if (!(o instanceof InsideOrder))
			return -1;
		InsideOrder io = (InsideOrder) o;

		if (elevator == io.elevator && destination == io.destination)
			return 1;
		else
			return 0;
	}

	@Override
	public int getDestination() {
		return destination;
	}
}
