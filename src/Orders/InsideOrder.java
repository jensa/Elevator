package Orders;

public class InsideOrder extends ServerOrder{
	
	public int elevator;
	public int destination;
	
	public InsideOrder (int ele, int dest){
		elevator = ele;
		destination = dest;
		isInsideOrder = true;
	}

}
