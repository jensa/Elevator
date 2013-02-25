package controller;

import java.awt.event.ActionEvent;
import java.rmi.RemoteException;

import elevator.rmi.RemoteActionListener;

public class ThingListener implements RemoteActionListener{

	@Override
	public void actionPerformed (ActionEvent e) throws RemoteException {
		System.out.println ("fuck");
		
	}

}
