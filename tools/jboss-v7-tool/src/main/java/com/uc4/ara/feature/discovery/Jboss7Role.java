package com.uc4.ara.feature.discovery;

public enum Jboss7Role {
	
	STANDALONE("Standalone"),MANAGED_DOMAIN("Managed Domain");

	private final String role;

	private Jboss7Role(String role) {
		this.role = role;
	}
	
	public String getRole() {
        return this.role;
    }

}
