package controller;

import java.rmi.registry.Registry;

public class RMIInterface {
	
	public void installListeners (){
		String name = "Compute";
        Registry registry = LocateRegistry.getRegistry(args[0]);
        GetAll getAll = (Compute) registry.lookup(name);
        Pi task = new Pi(Integer.parseInt(args[1]));
        BigDecimal pi = comp.executeTask(task);
        System.out.println(pi);
	}

}
