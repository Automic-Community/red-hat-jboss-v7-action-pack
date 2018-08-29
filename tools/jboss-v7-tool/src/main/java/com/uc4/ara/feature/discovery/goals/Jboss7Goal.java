package com.uc4.ara.feature.discovery.goals;

import com.automic.actions.discovery.models.GoalIdentity;

public enum Jboss7Goal implements GoalIdentity{
	HOST,
	HOME_DIRECTORY,
	CONNECTION;

	private String name;

	Jboss7Goal() {
		this.name = this.name();
	}
	
	Jboss7Goal(String name) {
		this.name = name;
	}
	
	@Override
	public String getName() {
		return name;
	}
}