/**
 * 
 */
package com.uc4.ara.feature.discovery.goals;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
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
	private static final String SLAVE_SERVER_INSTANCES = "/host/servers/server";
	private static final String MASTER_SERVER_GROUPS = "/domain/server-groups/server-group";
	private static final String MASTER_PROFILES = "/domain/profiles/profile";

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
					Set<String> profileSet = new HashSet<String>();
					Set<String> instanceSet = new HashSet<String>();
					Set<String> serverGroupSet = new HashSet<String>();

					if (domainControllerNodeType.isPresent()) {
						// read:server groups,profiles from file domain.xml
						readServerGroupAndProfileFromConfig(domainConfigDir, operatingMode, profileSet, serverGroupSet);
					}

					// read host controller name

					String controllerName = JbossConfigHelper.getAttributeValue(rootNode.get(), ATTRIBUTE_NAME, false);
					if (controllerName != null && !controllerName.isEmpty()) {
						write(Jboss7Finding.HOST_CONTROLLER, controllerName, operatingMode);
					}

					// read info : server instances,server groups
					// server instances(servername) and groups
					Optional<NodeList> serverInstancesNodeType = XmlUtility.findNodeList(document.get(),
							SLAVE_SERVER_INSTANCES);
					if (serverInstancesNodeType.isPresent()) {

						for (int i = 0; i < serverInstancesNodeType.get().getLength(); i++) {
							Node childNode = serverInstancesNodeType.get().item(i);
							instanceSet.add(JbossConfigHelper.getAttributeValue(childNode, ATTRIBUTE_NAME, false));
							serverGroupSet.add(JbossConfigHelper.getAttributeValue(childNode, "group", false));

						}
					}
					write(Jboss7Finding.SERVER_INSTANCES, getValueList(instanceSet), operatingMode);
					write(Jboss7Finding.SERVER_GROUP, getValueList(serverGroupSet), operatingMode);

				}

			}
		}

		return true;

	}

	private void readServerGroupAndProfileFromConfig(Path configurationDirPath, FindingValue operatingMode,
			Set<String> profilesSet, Set<String> serverGroupSet) {
		Path domainFilePath = Paths.get(configurationDirPath.toString(), "domain.xml");

		if (domainFilePath.toFile().exists()) {
			Optional<Document> document = XmlUtility.openDocument(domainFilePath);

			if (document.isPresent()) {

				Optional<NodeList> domainServerNode = XmlUtility.findNodeList(document.get(), MASTER_SERVER_GROUPS);
				if (domainServerNode.isPresent()) {

					for (int i = 0; i < domainServerNode.get().getLength(); i++) {
						Node childNode = domainServerNode.get().item(i);
						serverGroupSet.add(JbossConfigHelper.getAttributeValue(childNode, ATTRIBUTE_NAME, false));

					}

				}

				Optional<NodeList> domainProfileNode = XmlUtility.findNodeList(document.get(), MASTER_PROFILES);
				if (domainProfileNode.isPresent()) {

					for (int i = 0; i < domainProfileNode.get().getLength(); i++) {
						Node childNode = domainProfileNode.get().item(i);
						profilesSet.add(JbossConfigHelper.getAttributeValue(childNode, ATTRIBUTE_NAME, false));

					}
				}
			}

			write(Jboss7Finding.SERVER_GROUP, getValueList(serverGroupSet), operatingMode);
			write(Jboss7Finding.PROFILE, getValueList(profilesSet), operatingMode);

		}

	}

	private String getValueList(Set<String> valueSet) {
		StringBuilder valueText = new StringBuilder();
		for (String value : valueSet) {
			valueText.append(value);
			valueText.append(VALUE_SEPARATOR);
		}

		if (valueText.length() > 1) {
			return valueText.subSequence(0, valueText.lastIndexOf(VALUE_SEPARATOR)).toString();
		}

		return valueText.toString();

	}

}
