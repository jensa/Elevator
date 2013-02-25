package controller;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;

import elevator.rmi.GetAll;
import elevator.rmi.RemoteActionListener;

public class RMI {
	public static final String LOCALHOST = "127.0.0.1";
	
	public GetAll get;
	
	public void init (){
		try {
			setupRegistry ();
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (NotBoundException e) {
			e.printStackTrace();
		}
	}
	
	private void setupRegistry () throws RemoteException, NotBoundException{
		if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
		String name = "GetAll";
        Registry registry = LocateRegistry.getRegistry(LOCALHOST);
        get = (GetAll) registry.lookup(name);
	}
	
	public static void main(String[] args){
		RMI r = new RMI ();
		try {
			System.out.println (r.get.getNumberOfElevators ());
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

}
