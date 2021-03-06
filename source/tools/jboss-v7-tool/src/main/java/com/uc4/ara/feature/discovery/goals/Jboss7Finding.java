package com.uc4.ara.feature.discovery.goals;

import com.automic.actions.discovery.models.Finding;
import com.automic.actions.discovery.models.FindingVisibility;

public enum Jboss7Finding implements Finding {
	
	HOST("host"),
	HOME_DIRECTORY("home_directory"),		
	PORT("port", Integer.class),
	HOST_CONTROLLER("host_controller"),
	SERVER_INSTANCES("servername"),
	SERVER_GROUP("server_groups"),
	OPERATING_MODE("operating_mode"), 
	PROFILE("profile"),
	SERVER_NAME("server",FindingVisibility.INTERNAL),
	STANDALONE("standalone",FindingVisibility.INTERNAL),
	MANAGED_DOMAIN("domain",FindingVisibility.INTERNAL);
	
	private Class<?> clazz;
	private String propertyName;
	private FindingVisibility visibility;
	
	Jboss7Finding(String propertyName, Class<?> clazz, FindingVisibility visibility) {
		this.propertyName = propertyName;
		this.clazz = clazz;
		this.visibility = visibility;
	}

	Jboss7Finding(String propertyName, Class<?> clazz) {
		this(propertyName, clazz, FindingVisibility.PUBLIC);
	}

	Jboss7Finding(String propertyName, FindingVisibility visibility) {
		this(propertyName, String.class, visibility);
	}

	Jboss7Finding(String propertyName) {
		this(propertyName, String.class, FindingVisibility.PUBLIC);
	}

	@Override
	public String getName() {
		return propertyName;
	}

	@Override
	public Class<?> getTypeClass() {
		return clazz;
	}

	@Override
	public FindingVisibility getVisibility() {
		return visibility;
	}

}
