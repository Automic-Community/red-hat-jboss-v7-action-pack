package com.uc4.ara.feature.discovery.goals;

import com.automic.actions.discovery.models.Compatibility;
import com.automic.actions.discovery.models.Goal;
import com.automic.actions.discovery.models.GoalExecutionStrategy;
import com.automic.actions.discovery.models.Plan;

public class ManagerConnection extends Goal {

	public ManagerConnection() {
		super(Jboss7Goal.CONFIGURATION, Compatibility.UNISEX);
		this.setStrategy(GoalExecutionStrategy.RUN_ALL_PLANS_REQUIRE_ONE_SUCCESS);
		this.addPlan(readJbossConfiguration());

	}

	@Override
	protected void registerFindings() {
		this.register(Jboss7Finding.OPERATING_MODE);
		this.register(Jboss7Finding.SERVER_GROUP);
		this.register(Jboss7Finding.SERVER_NAME);
		this.register(Jboss7Finding.PROFILE);
		this.register(Jboss7Finding.HOST_CONTROLLER);

	}

	private Plan readJbossConfiguration() {
		return new JbossModeConfigPlan("Read configuration from file(s)", this, Compatibility.UNISEX);
	}

}
