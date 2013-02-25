package controller;

public class Order {
	public enum Type{
		MOVE, STOP, OPEN, CLOSE
	}
	public Type type;
	public int argument;
	public String message;

}
