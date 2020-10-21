package com.uc4.ara.feature.discovery;

import com.automic.actions.discovery.DiscoveryManager;
import com.uc4.ara.feature.discovery.goals.HomeDirectory;
import com.uc4.ara.feature.discovery.goals.HostAndPortGoal;
import com.uc4.ara.feature.discovery.goals.ManagerConnection;


public class Jboss7DiscoveryManager {
	
	private DiscoveryManager manager;

	public Jboss7DiscoveryManager() {
		manager = new DiscoveryManager(new Jboss7Result());		
		manager.addGoal(new HomeDirectory());	
		manager.addGoal(new HostAndPortGoal());
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
