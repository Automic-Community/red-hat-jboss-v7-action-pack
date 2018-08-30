package com.uc4.ara.feature.discovery.goals;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.SystemUtils;

import com.automic.actions.discovery.models.Compatibility;
import com.automic.actions.discovery.models.FindingValue;
import com.automic.actions.discovery.models.Goal;
import com.automic.actions.discovery.models.GoalExecutionStrategy;
import com.automic.actions.discovery.models.Plan;
import com.automic.actions.discovery.models.PlanStatus;
import com.automic.actions.shell.unix.UnixShell;
import com.automic.actions.shell.windows.Services;
import com.automic.actions.shell.windows.WindowsCmdShell;

public class HomeDirectory extends Goal {

	private static final String FILE_PATH_REGEX = "\"?((.*)\\\\bin\\\\(standalone|domain).(bat|sh))";
	public static final String REG_PATH_SERVICE = "\"?((.*)\\\\bin\\\\(jbosssvc.exe)).*";

	private static final Pattern PATH_PATTERN = Pattern.compile(FILE_PATH_REGEX, Pattern.CASE_INSENSITIVE);
	private static final Pattern SERVICE_PATTERN = Pattern.compile(REG_PATH_SERVICE, Pattern.CASE_INSENSITIVE);

	private static final String STANDALONE_BAT = "standalone.bat";
	private static final String STANDALONE_SH = "standalone.sh";

	private static final String DOMAIN_BAT = "domain.bat";
	private static final String DOMAIN_SH = "domain.sh";

	public HomeDirectory() {
		super(Jboss7Goal.HOME_DIRECTORY, Compatibility.UNISEX);
		this.setStrategy(GoalExecutionStrategy.RUN_ALL_PLANS_REQUIRE_ONE_SUCCESS);

		this.addPlan(findService());
		this.addPlan(findBatchFile());
	}

	private Plan findBatchFile() {
		return new Plan("Find batch file", this, Compatibility.UNISEX) {
			@Override
			public long getTimeout() {
				return 300;
			}

			@Override
			protected PlanStatus execute() throws Exception {
				boolean success = false;

				List<Path> standalonePaths = SystemUtils.IS_OS_WINDOWS
						? WindowsCmdShell.instance().fileSystem().findFile(STANDALONE_BAT)
						: UnixShell.instance().fileSystem().findFile(STANDALONE_SH);
				List<Path> domainPaths = SystemUtils.IS_OS_WINDOWS
						? WindowsCmdShell.instance().fileSystem().findFile(DOMAIN_BAT)
						: UnixShell.instance().fileSystem().findFile(DOMAIN_SH);

				Set<Path> totalPaths = new HashSet<>();
				totalPaths.addAll(standalonePaths);
				totalPaths.addAll(domainPaths);

				for (Path path : totalPaths) {

					Matcher m = PATH_PATTERN.matcher(path.toString());

					if (m.matches()) {
						List<FindingValue> jbossHosts = read(Jboss7Finding.HOST);
						for (FindingValue jbossHost : jbossHosts) {
							String hDir = path.getParent().getParent().toString();
							write(Jboss7Finding.HOME_DIRECTORY, hDir, jbossHost);

						}

					}

				}

				return complete(success);
			}
		};
	}

	private Plan findService() {

		return new Plan("Find windows service", this, Compatibility.WINDOWS) {
			@Override
			public long getTimeout() {
				return 200;
			}

			@Override
			protected PlanStatus execute() throws Exception {
				List<String> paths = WindowsCmdShell.instance().services().findService("jboss",
						EnumSet.of(Services.Field.PathName));

				boolean success = false;

				for (String path : paths) {

					Matcher matcher = SERVICE_PATTERN.matcher(path.toString());

					if (matcher.matches()) {
						Path executablePath = Paths.get(matcher.group(1));
						if (Files.exists(executablePath)) {
							Path home = executablePath.getParent().getParent();

							List<FindingValue> jbossHosts = read(Jboss7Finding.HOST);
							for (FindingValue jbossHost : jbossHosts) {
								write(Jboss7Finding.HOME_DIRECTORY, home.toFile().getCanonicalPath(), jbossHost);
								success = true;
							}
						}
					}
				}

				return complete(success);
			}
		};

	}

	@Override
	protected void registerFindings() {
		this.register(Jboss7Finding.HOME_DIRECTORY);

	}

}
