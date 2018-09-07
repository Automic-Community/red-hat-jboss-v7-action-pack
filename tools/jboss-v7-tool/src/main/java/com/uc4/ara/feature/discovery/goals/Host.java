package com.uc4.ara.feature.discovery.goals;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;

import com.automic.actions.discovery.models.Compatibility;
import com.automic.actions.discovery.models.Goal;
import com.automic.actions.discovery.models.GoalExecutionStrategy;
import com.automic.actions.discovery.models.Plan;
import com.automic.actions.discovery.models.PlanStatus;
import com.automic.actions.shell.ShellCommandResult;
import com.automic.actions.shell.unix.UnixShell;
import com.automic.actions.shell.windows.WindowsCmdShell;

public class Host extends Goal {

	public Host() {
		super(Jboss7Goal.HOST, Compatibility.UNISEX);
		this.setStrategy(GoalExecutionStrategy.RUN_ALL_PLANS_REQUIRE_ONE_SUCCESS);

		this.addPlan(findHost());
	}

	@Override
	protected void registerFindings() {
		this.register(Jboss7Finding.HOST);
	}

	private Plan findHost() {
		return new Plan("Run Command To Find Host", this, Compatibility.UNISEX) {
			@Override
			public long getTimeout() {
				return 100;
			}

			@Override
			protected PlanStatus execute() throws Exception {
				boolean success = false;

				ShellCommandResult result = SystemUtils.IS_OS_WINDOWS ? WindowsCmdShell.instance().execute("hostname")
						: UnixShell.instance().execute("hostname");

				if (result.getReturnCode() == 0) {
					String hostName = StringUtils.trim(result.getOutput());
					if (StringUtils.isNotBlank(hostName)) {
						write(Jboss7Finding.HOST, hostName);
						success = true;
					}
				}
				return complete(success);
			}
		};
	}

}
