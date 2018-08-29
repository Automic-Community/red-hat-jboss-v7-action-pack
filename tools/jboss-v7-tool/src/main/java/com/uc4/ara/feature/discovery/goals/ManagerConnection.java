package com.uc4.ara.feature.discovery.goals;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.automic.actions.common.utils.XmlUtility;
import com.automic.actions.discovery.models.Compatibility;
import com.automic.actions.discovery.models.FindingValue;
import com.automic.actions.discovery.models.Goal;
import com.automic.actions.discovery.models.GoalExecutionStrategy;
import com.automic.actions.discovery.models.Plan;
import com.automic.actions.discovery.models.PlanStatus;
import com.google.common.base.Optional;

public class ManagerConnection extends Goal {

	private static final String SOCKET_BIND_GROUP = "/server/socket-binding-group[@name = 'standard-sockets']";
	private static final String SOCKET_BIND_REGEX = SOCKET_BIND_GROUP + "/socket-binding";
	private static final String PORT_REGEX = "\\$\\{([0-9a-zA-Z.-]+):([0-9]{1,})\\}";
	private static final Pattern PORT_PATTERN = Pattern.compile(PORT_REGEX, Pattern.CASE_INSENSITIVE);

	public ManagerConnection() {
		super(Jboss7Goal.CONNECTION, Compatibility.UNISEX);
		this.setStrategy(GoalExecutionStrategy.RUN_ALL_PLANS_REQUIRE_ONE_SUCCESS);

		this.addPlan(getFromServerConfig());
	}

	@Override
	protected void registerFindings() {
		this.register(Jboss7Finding.SERVER_NAME);
		this.register(Jboss7Finding.PORT);
		this.register(Jboss7Finding.PROTOCOL);
		this.register(Jboss7Finding.APPBASE_DIRECTORY);

	}

	private Plan getFromServerConfig() {
		return new Plan("Read configuration from standalone file(s)", this, Compatibility.UNISEX, Jboss7Goal.HOST) {
			@Override
			public long getTimeout() {
				return 300;
			}

			@Override
			protected PlanStatus execute() throws Exception {
				boolean success = false;

				List<FindingValue> jboss7Homes = read(Jboss7Finding.HOME_DIRECTORY);
				for (FindingValue jboss7Home : jboss7Homes) {

					Path configurationDirPath = Paths.get(jboss7Home.getValue().toString(), "standalone",
							"configuration");
					Path serverConfigPath = configurationDirPath.resolve("standalone.xml");

					if (Files.exists(serverConfigPath)) {
						Optional<Document> document = XmlUtility.openDocument(serverConfigPath);
						if (document.isPresent()) {
							// findAppBaseDirectory(document, jboss7Home);
							findProtocolAndPort(document, jboss7Home);
							success = true;
						}
					}

				}

				return complete(success);
			}

			private void findProtocolAndPort(Optional<Document> document, FindingValue jboss7Home) {
				Optional<Node> socketNode = XmlUtility.findNode(document.get(), SOCKET_BIND_GROUP);
				int portOffsetVal = 0;
				if (socketNode.isPresent()) {
					String portOffset = socketNode.get().getAttributes().getNamedItem("port-offset").getNodeValue();
					portOffsetVal = getResolvedValue(portOffset);

				}
				Optional<NodeList> nodeList = XmlUtility.findNodeList(document.get(), SOCKET_BIND_REGEX);
				if (nodeList.isPresent()) {
					for (int i = 0; i < nodeList.get().getLength(); i++) {
						String protocol = nodeList.get().item(i).getAttributes().getNamedItem("name").getNodeValue().toLowerCase();
						if (protocol.matches("management-http") || protocol.matches("management-https")) {
							protocol =protocol.substring(protocol.indexOf("-")+1, protocol.length());
							FindingValue protocolValue = write(Jboss7Finding.PROTOCOL, protocol, jboss7Home);
							readProtocolPort(jboss7Home, protocolValue, nodeList.get().item(i), portOffsetVal);
						}
					}
				}
			}

			private void readProtocolPort(FindingValue jboss7Home, FindingValue protocolValue, Node node,
					int portOffsetVal) {
				String port = node.getAttributes().getNamedItem("port").getNodeValue();
				int resolvedPort = getResolvedValue(port) + portOffsetVal;
				FindingValue portValue = write(Jboss7Finding.PORT, resolvedPort, protocolValue);
				write(Jboss7Finding.SERVER_NAME, getServerName(resolvedPort), portValue);

			}

			private Integer getResolvedValue(String value) {
				int resolvedValue = -1;
				Matcher matcher = PORT_PATTERN.matcher(value);
				while (matcher.find()) {
					String dynamicSystemValue = matcher.group(1);
					String defaultValue = matcher.group(2);
					resolvedValue = Integer.valueOf(System.getProperty(dynamicSystemValue, defaultValue));

				}
				return resolvedValue;
			}

			private String getServerName(Integer port) {
				List<FindingValue> hosts = read(Jboss7Finding.HOST);
				if (hosts.size() > 0) {
					return hosts.get(0).getValue() + " " + port;
				}
				return null;
			}

			private void findAppBaseDirectory(Optional<Document> document, FindingValue jboss7Home) {
				Optional<NodeList> nodeList = XmlUtility.findNodeList(document.get(),
						"/Server/Service/Engine/Host/@appBase");
				if (nodeList.isPresent()) {
					for (int i = 0; i < nodeList.get().getLength(); i++) {
						String appBaseName = nodeList.get().item(i).getTextContent();
						write(Jboss7Finding.APPBASE_DIRECTORY, appBaseName, jboss7Home);
					}
				}
			}

			private Optional<Document> readConfigurationFile(Path serverConfig) {

				Document doc = null;
				try {

					// File inputFile = new File(fileName);
					DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
					DocumentBuilder dBuilder;

					dBuilder = dbFactory.newDocumentBuilder();

					doc = dBuilder.parse(serverConfig.toFile());
					doc.getDocumentElement().normalize();

				} catch (Exception e) {

				}
				return Optional.of(doc);
			}
		};
	}

}
