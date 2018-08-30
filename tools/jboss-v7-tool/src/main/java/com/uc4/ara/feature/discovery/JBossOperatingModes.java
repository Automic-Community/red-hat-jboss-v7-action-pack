package com.uc4.ara.feature.discovery;

public enum JBossOperatingModes {

	STANDALONE_MODE("Standalone"), DOAMIN_MODE("Managed Domain");

	private String mode;

	public String getMode() {
		return this.mode;
	}

	JBossOperatingModes(String mode) {
		this.mode = mode;
	}

}
