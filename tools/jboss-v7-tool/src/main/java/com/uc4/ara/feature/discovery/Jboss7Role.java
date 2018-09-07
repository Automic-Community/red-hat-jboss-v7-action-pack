package com.uc4.ara.feature.discovery;

public enum Jboss7Role {
	
	SERVER_NAME("Server");

	private final String role;

	private Jboss7Role(String role) {
		this.role = role;
	}
	
	public String getRole() {
        return this.role;
    }

}
