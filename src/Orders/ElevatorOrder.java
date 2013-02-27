package Orders;

public class ElevatorOrder {
	public enum Type{
		MOVE, STOP, OPEN, CLOSE
	}
	public Type type;
	public int argument;
	public String message;

}
