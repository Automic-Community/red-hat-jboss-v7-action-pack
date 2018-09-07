package com.uc4.ara.feature.discovery;

import java.util.HashMap;
import java.util.Map;

import com.automic.actions.discovery.DefaultDiscoveryResult;
import com.automic.actions.discovery.models.Finding;
import com.uc4.ara.feature.discovery.goals.Jboss7Finding;


public class Jboss7Result extends DefaultDiscoveryResult {
	
	private static Map<Finding, String> mapping = new HashMap<Finding, String>() {
		private static final long serialVersionUID = 1L;

		{
			put(Jboss7Finding.SERVER_NAME, Jboss7Role.SERVER_NAME.getRole());
		}
	};

	public Jboss7Result() {
		super();
	}
	
	@Override
	public Map<Finding, String> getRolesMapping() {
		return mapping;
	}
	
}
