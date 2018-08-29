package com.uc4.ara.feature.discovery.goals;

import com.automic.actions.discovery.DiscoveryManager;
import com.uc4.ara.feature.discovery.Jboss7Result;


public class Jboss7DiscoveryManager {
	
	private DiscoveryManager manager;

	public Jboss7DiscoveryManager() {
		manager = new DiscoveryManager(new Jboss7Result());
		manager.addGoal(new Host());
		manager.addGoal(new HomeDirectory());		
		manager.addGoal(new ManagerConnection());
	}

	public void run() throws Exception {
		manager.printGoals();
		manager.run();
		manager.printGoals();
		manager.printReport();

		manager.getResult().printResult();
	}

}
