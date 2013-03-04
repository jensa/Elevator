package Orders;

public class InsideOrder extends Order{
	
	public int elevator;
	public int destination;
	
	public InsideOrder (int ele, int dest){
		elevator = ele;
		destination = dest;
		isInsideOrder = true;
	}
	
	@Override
	public int moveToFloor (){
		return destination;
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
