package com.uc4.ara.feature.discovery.goals;

import com.automic.actions.discovery.models.Compatibility;
import com.automic.actions.discovery.models.Goal;
import com.automic.actions.discovery.models.Plan;

public class ManagerConnection extends Goal {

	public ManagerConnection() {
		super(Jboss7Goal.CONFIGURATION, Compatibility.UNISEX);		
		this.addPlan(readJbossConfiguration());

	}

	@Override
	protected void registerFindings() {
		this.register(Jboss7Finding.OPERATING_MODE);
		this.register(Jboss7Finding.SERVER_GROUP);
		this.register(Jboss7Finding.SERVER_INSTANCES);
		this.register(Jboss7Finding.PROFILE);
		this.register(Jboss7Finding.HOST_CONTROLLER);
		this.register(Jboss7Finding.MANAGED_DOMAIN);

	}

	private Plan readJbossConfiguration() {
		return new JbossModeConfigPlan("Read configuration from file(s)", this, Compatibility.UNISEX);
	}

}
