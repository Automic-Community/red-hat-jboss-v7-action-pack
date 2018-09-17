/**
 * 
 */
package com.uc4.ara.feature.discovery.goals;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.automic.actions.common.utils.XmlUtility;
import com.automic.actions.discovery.models.Compatibility;
import com.automic.actions.discovery.models.FindingValue;
import com.automic.actions.discovery.models.Goal;
import com.automic.actions.discovery.models.Plan;
import com.automic.actions.discovery.models.PlanStatus;
import com.google.common.base.Optional;
import com.uc4.ara.feature.discovery.JBossOperatingModes;

/**
 * The class reads the configuration information from the xml files.
 * 
 * For domain mode : It will read two files present at
 * {JBOSS_HOME}/domain/configuration, domain.xml (in case of domain controller)
 * and host.xml (for both domain/host controller).
 * </ul>
 * 
 * @author sumitsamson
 *
 */
public class JbossModeConfigPlan extends Plan {

	private static final String VALUE_SEPARATOR = ",";
	private static final String ATTRIBUTE_NAME = "name";

	private static final String HOST_CONTROLLER_MAIN_NODE = "/host";
	private static final String SLAVE_SERVER_INSTANCES = "/host/servers";
	private static final String MASTER_SERVER_GROUPS = "/domain/server-groups";

	public JbossModeConfigPlan(String name, Goal goal, Compatibility compatibility) {
		super(name, goal, compatibility);
	}

	@Override
	protected PlanStatus execute() throws Exception {
		boolean success = false;

		List<FindingValue> jboss7Homes = read(Jboss7Finding.HOME_DIRECTORY);
		for (FindingValue jboss7Home : jboss7Homes) {
			Path configurationDirPath = Paths.get(jboss7Home.getValue().toString(), "domain", "configuration");
			if (configurationDirPath.toFile().exists()) {
				success = readConfigForDomain(jboss7Home, configurationDirPath);
			}
		}

		return complete(success);
	}

	@Override
	public long getTimeout() {
		return 300;
	}

	private boolean readConfigForDomain(FindingValue jboss7Home, Path domainConfigDir) {
		FindingValue operatingMode = write(Jboss7Finding.OPERATING_MODE, JBossOperatingModes.DOAMIN_MODE.getMode(),
				jboss7Home);

		Path hostXmlFilePath = Paths.get(domainConfigDir.toString(), "host.xml");
		if (hostXmlFilePath.toFile().exists()) {
			Optional<Document> document = XmlUtility.openDocument(hostXmlFilePath);
			if (document.isPresent()) {

				// read controller name
				Optional<Node> rootNode = XmlUtility.findNode(document.get(), HOST_CONTROLLER_MAIN_NODE);
				if (rootNode.isPresent()) {

					// check for controller type i.e domain or host controller
					Optional<Node> domainControllerNodeType = XmlUtility.findNode(document.get(),
							JbossConfigHelper.MASTER_CONTROLLER_NODE);

					if (domainControllerNodeType.isPresent()) {
						// read:server groups,profiles from file domain.xml
						readServerGroupAndProfileFromConfig(domainConfigDir, operatingMode);
					} else {

						// read host controller name

						String controllerName = JbossConfigHelper.getAttributeValue(rootNode.get(), ATTRIBUTE_NAME,
								false);
						if (controllerName != null) {
							write(Jboss7Finding.HOST_CONTROLLER, controllerName, operatingMode);
						}

						// read info : server instances,server groups
						// server instances(servername) and groups
						Optional<Node> serverInstancesNodeType = XmlUtility.findNode(document.get(),
								SLAVE_SERVER_INSTANCES);
						if (serverInstancesNodeType.isPresent()) {

							StringBuffer servernames = new StringBuffer();
							StringBuffer servergrps = new StringBuffer();
							NodeList nodes = serverInstancesNodeType.get().getChildNodes();
							for (int i = 0; i < nodes.getLength(); i++) {
								Node childNode = nodes.item(i);
								if (childNode.getNodeType() == Node.ELEMENT_NODE) {

									Map<String, String> attrMap = JbossConfigHelper
											.getNodeAttributeValueMap(nodes.item(i));
									servernames.append(attrMap.get(ATTRIBUTE_NAME));
									servernames.append(VALUE_SEPARATOR);
									servergrps.append(attrMap.get("group"));
									servergrps.append(VALUE_SEPARATOR);

								}
							}

							write(Jboss7Finding.SERVER_NAME,
									servernames.subSequence(0, servernames.lastIndexOf(VALUE_SEPARATOR)),
									operatingMode);
							write(Jboss7Finding.SERVER_GROUP,
									servergrps.subSequence(0, servergrps.lastIndexOf(VALUE_SEPARATOR)), operatingMode);

						}

					}

				}

			}
		}

		return true;

	}

	private void readServerGroupAndProfileFromConfig(Path configurationDirPath, FindingValue operatingMode) {
		Path domainFilePath = Paths.get(configurationDirPath.toString(), "domain.xml");

		if (domainFilePath.toFile().exists()) {
			Optional<Document> document = XmlUtility.openDocument(domainFilePath);

			StringBuffer servergrps = new StringBuffer();
			Set<String> profilesSet = new HashSet<String>();

			if (document.isPresent()) {

				Optional<Node> domainServerNode = XmlUtility.findNode(document.get(), MASTER_SERVER_GROUPS);
				if (domainServerNode.isPresent()) {

					NodeList nodes = domainServerNode.get().getChildNodes();
					for (int i = 0; i < nodes.getLength(); i++) {
						Node childNode = nodes.item(i);
						if (childNode.getNodeType() == Node.ELEMENT_NODE) {

							Map<String, String> attrMap = JbossConfigHelper.getNodeAttributeValueMap(nodes.item(i));
							servergrps.append(attrMap.get(ATTRIBUTE_NAME));
							servergrps.append(VALUE_SEPARATOR);
							profilesSet.add(attrMap.get("profile"));

						}
					}

					StringBuffer profiles = new StringBuffer();
					for (String profile : profilesSet) {
						profiles.append(profile);
						profiles.append(VALUE_SEPARATOR);
					}

					write(Jboss7Finding.PROFILE, profiles.subSequence(0, profiles.lastIndexOf(VALUE_SEPARATOR)),
							operatingMode);
					write(Jboss7Finding.SERVER_GROUP,
							servergrps.subSequence(0, servergrps.lastIndexOf(VALUE_SEPARATOR)), operatingMode);

				}
			}

		}

	}

}
