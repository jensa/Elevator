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

}
