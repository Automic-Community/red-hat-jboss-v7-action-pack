package com.uc4.ara.feature.discovery;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.uc4.ara.feature.FeatureUtil;
import com.uc4.ara.feature.discovery.goals.Jboss7DiscoveryManager;
import com.uc4.ara.feature.globalcodes.ErrorCodes;

public class Jboss7 extends com.automic.actions.common.AbstractFeature{
	
	private Logger logger = LogManager.getLogger(Jboss7.class);

	public int run(String[] args) throws Exception {
		super.run(args);
		logger.info("STARTED:");

		Jboss7DiscoveryManager manager = new Jboss7DiscoveryManager();
		manager.run();

        logger.info(String.format("Discovery process finished successfully."));
        logger.info(String.format("Please review discovery results in Job Report."));
		return ErrorCodes.OK;
	}

	@Override
	public void printUsage() {
		FeatureUtil.logMsg("Command:");
		FeatureUtil.logMsg("discovery Jboss7");
	}

}
