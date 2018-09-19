package com.uc4.ara.feature.discovery.goals;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.automic.actions.common.utils.XmlUtility;
import com.automic.actions.discovery.models.Compatibility;
import com.automic.actions.discovery.models.FindingValue;
import com.automic.actions.discovery.models.Goal;
import com.automic.actions.discovery.models.Plan;
import com.automic.actions.discovery.models.PlanStatus;
import com.google.common.base.Optional;
import com.uc4.ara.feature.discovery.JBossOperatingModes;

public class HostAndPortGoal extends Goal {

	public HostAndPortGoal() {
		super(Jboss7Goal.HOST_PORT, Compatibility.UNISEX);
		this.addPlan(findHost());

	}

	@Override
	protected void registerFindings() {
		this.register(Jboss7Finding.OPERATING_MODE);
		this.register(Jboss7Finding.HOST);
		this.register(Jboss7Finding.PORT);
		this.register(Jboss7Finding.SERVER_NAME);

	}

	private Plan findHost() {
		return new JbossHostPlan("Read host info", this, Compatibility.UNISEX);
	}
}

class JbossHostPlan extends Plan {

	private static final String STANDALONE_INTERFACE = "/server/interfaces/interface[@name='management']/inet-address";
	private static final String SOCKET_BIND_GROUP = "/server/socket-binding-group[@name = 'standard-sockets']";
	private static final String SOCKET_BIND_REGEX = SOCKET_BIND_GROUP + "/socket-binding[@name = 'management-http']";

	private static final String MODES[] = { "standalone", "domain" };

	protected JbossHostPlan(String name, Goal goal, Compatibility compatibility) {
		super(name, goal, compatibility);

	}

	@Override
	protected PlanStatus execute() throws Exception {
		boolean success = false;

		List<FindingValue> jboss7Homes = read(Jboss7Finding.HOME_DIRECTORY);
		if (!jboss7Homes.isEmpty()) {
			for (FindingValue jboss7Home : jboss7Homes) {
				for (String mode : MODES) {
					Path configurationDirPath = Paths.get(jboss7Home.getValue().toString(), mode, "configuration");
					if (configurationDirPath.toFile().exists()) {
						if (mode.equalsIgnoreCase("standalone")) {
							success = readHostAndPortForStandalone(jboss7Home, configurationDirPath);
						} else {
							success = readHostAndPortForDomain(jboss7Home, configurationDirPath);
						}

					}
				}
			}
		} else {
			write(Jboss7Finding.HOST, JbossConfigHelper.readHost());
			success = false;

		}

		return complete(success);
	}

	private boolean readHostAndPortForDomain(FindingValue jboss7Home, Path configurationDirPath) {
		boolean success = false;

		Path hostXmlFilePath = Paths.get(configurationDirPath.toString(), "host.xml");
		if (hostXmlFilePath.toFile().exists()) {
			Optional<Document> document = XmlUtility.openDocument(hostXmlFilePath);
			if (document.isPresent()) {
				FindingValue operatingMode = write(Jboss7Finding.OPERATING_MODE,
						JBossOperatingModes.DOAMIN_MODE.getMode(), jboss7Home);

				// check for controller type i.e domain or host controller
				Optional<Node> domainControllerNodeType = XmlUtility.findNode(document.get(),
						JbossConfigHelper.MASTER_CONTROLLER_NODE);

				if (domainControllerNodeType.isPresent()) {
					// this is domain controller.

					// read host value
					Optional<Node> interfaceNode = XmlUtility.findNode(document.get(),
							JbossConfigHelper.MASTER_CONTROLLER_HOST_INTERFACE);
					if (interfaceNode.isPresent()) {
						String host = JbossConfigHelper.getSystemPropertyResolvedValue(
								JbossConfigHelper.getAttributeValue(interfaceNode.get(), "value", false),
								JbossConfigHelper.DEFAULT_MGMT_HOST);
						FindingValue standaloneHost = write(Jboss7Finding.HOST, host, operatingMode);

						// read port value
						String port = null;
						Optional<Node> socketBindingMgmtHttpNode = XmlUtility.findNode(document.get(),
								JbossConfigHelper.MASTER_CONTROLLER_PORT_INTERFACE);
						if (socketBindingMgmtHttpNode.isPresent()) {
							port = JbossConfigHelper.getSystemPropertyResolvedValue(
									JbossConfigHelper.getAttributeValue(socketBindingMgmtHttpNode.get(),
											JbossConfigHelper.ATTRIBUTE_PORT, false),
									JbossConfigHelper.DEFAULT_MGMT_PORT);

							write(Jboss7Finding.PORT, Integer.parseInt(port), standaloneHost);

						} else {
							write(Jboss7Finding.PORT, JbossConfigHelper.DEFAULT_MGMT_PORT, standaloneHost);
						}

						String servername = host + " " + port + " " + JBossOperatingModes.DOAMIN_MODE.getMode();
						write(Jboss7Finding.SERVER_NAME, servername, operatingMode);

					} else {
						FindingValue standaloneHost = write(Jboss7Finding.HOST, JbossConfigHelper.DEFAULT_MGMT_HOST,
								operatingMode);
						write(Jboss7Finding.PORT, JbossConfigHelper.DEFAULT_MGMT_PORT, standaloneHost);
						String servername = JbossConfigHelper.DEFAULT_MGMT_HOST + " "
								+ JbossConfigHelper.DEFAULT_MGMT_PORT + " " + JBossOperatingModes.DOAMIN_MODE.getMode();
						write(Jboss7Finding.SERVER_NAME, servername, operatingMode);
					}
					success = true;

				} else {
					// this is normal host controller
					Optional<Node> hostControllerNodeType = XmlUtility.findNode(document.get(),
							JbossConfigHelper.SLAVE_CONTROLLER_NODE);

					// host controller host and port
					if (hostControllerNodeType.isPresent()) {
						String port = JbossConfigHelper.getSystemPropertyResolvedValue(
								JbossConfigHelper.getAttributeValue(hostControllerNodeType.get(),
										JbossConfigHelper.ATTRIBUTE_PORT, true));
						String host = JbossConfigHelper
								.getSystemPropertyResolvedValue(
										JbossConfigHelper.getAttributeValue(hostControllerNodeType.get(),
												JbossConfigHelper.ATTRIBUTE_HOST, true),
										JbossConfigHelper.DEFAULT_MGMT_HOST);

						FindingValue domainHost = write(Jboss7Finding.HOST, host, operatingMode);
						write(Jboss7Finding.PORT, Integer.parseInt(port), domainHost);
						String servername = host + " " + port + " " + JBossOperatingModes.DOAMIN_MODE.getMode();
						write(Jboss7Finding.SERVER_NAME, servername, operatingMode);

					} else {
						FindingValue standaloneHost = write(Jboss7Finding.HOST, JbossConfigHelper.DEFAULT_MGMT_HOST,
								operatingMode);
						write(Jboss7Finding.PORT, JbossConfigHelper.DEFAULT_MGMT_PORT, standaloneHost);
						String servername = JbossConfigHelper.DEFAULT_MGMT_HOST + " "
								+ JbossConfigHelper.DEFAULT_MGMT_PORT + " " + JBossOperatingModes.DOAMIN_MODE.getMode();
						write(Jboss7Finding.SERVER_NAME, servername, operatingMode);
					}
					success = true;
				}

			}
		} else {
			write(Jboss7Finding.HOST, JbossConfigHelper.DEFAULT_MGMT_HOST);
		}

		return success;
	}

	private boolean readHostAndPortForStandalone(FindingValue jboss7Home, Path configurationDirPath) {
		FindingValue operatingMode = write(Jboss7Finding.OPERATING_MODE, JBossOperatingModes.STANDALONE_MODE.getMode(),
				jboss7Home);
		boolean success = false;

		configurationDirPath = Paths.get(configurationDirPath.toString(), "standalone.xml");

		if (configurationDirPath.toFile().exists()) {
			Optional<Document> document = XmlUtility.openDocument(configurationDirPath);
			if (document.isPresent()) {

				Optional<Node> interfaceNode = XmlUtility.findNode(document.get(), STANDALONE_INTERFACE);

				if (interfaceNode.isPresent()) {
					String host = JbossConfigHelper.getSystemPropertyResolvedValue(
							JbossConfigHelper.getAttributeValue(interfaceNode.get(), "value", false),
							JbossConfigHelper.DEFAULT_MGMT_HOST);
					FindingValue standaloneHost = write(Jboss7Finding.HOST, host, operatingMode);

					Optional<Node> socketNode = XmlUtility.findNode(document.get(), SOCKET_BIND_GROUP);
					int portOffsetVal = 0;
					if (socketNode.isPresent()) {
						String portOffset = JbossConfigHelper.getAttributeValue(socketNode.get(), "port-offset", false);
						portOffsetVal = Integer
								.parseInt(JbossConfigHelper.getSystemPropertyResolvedValue(portOffset, "0"));

					}
					Optional<Node> socketBindingMgmtHttpNode = XmlUtility.findNode(document.get(), SOCKET_BIND_REGEX);
					String port = null;
					if (socketBindingMgmtHttpNode.isPresent()) {
						port = JbossConfigHelper
								.getSystemPropertyResolvedValue(
										JbossConfigHelper.getAttributeValue(socketBindingMgmtHttpNode.get(),
												JbossConfigHelper.ATTRIBUTE_PORT, false),
										JbossConfigHelper.DEFAULT_MGMT_PORT);
						int resolvedPort = Integer.parseInt(port) + portOffsetVal;
						port = String.valueOf(resolvedPort);
						write(Jboss7Finding.PORT, resolvedPort, standaloneHost);

					} else {
						write(Jboss7Finding.PORT, JbossConfigHelper.DEFAULT_MGMT_PORT, standaloneHost);
					}

					String servername = host + " " + port + " " + JBossOperatingModes.STANDALONE_MODE.getMode();
					write(Jboss7Finding.SERVER_NAME, servername, operatingMode);
					success = true;
				}

			}
		} else {
			write(Jboss7Finding.HOST, JbossConfigHelper.readHost(), operatingMode);
			success = false;
		}

		return success;
	}

}
