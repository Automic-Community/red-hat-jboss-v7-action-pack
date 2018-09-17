package com.uc4.ara.feature.discovery.goals;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.automic.actions.shell.ShellCommandResult;
import com.automic.actions.shell.unix.UnixShell;
import com.automic.actions.shell.windows.WindowsCmdShell;

public class JbossConfigHelper {
	
	private static final String SYSTEM_PROPERTY_REGEX = "\\$\\{([0-9a-zA-Z.-]+):([0-9a-zA-Z.-]+)\\}";
	private static final Pattern SYSTEM_PROPERTY_PATTERN = Pattern.compile(SYSTEM_PROPERTY_REGEX,
			Pattern.CASE_INSENSITIVE);
	public static final String DEFAULT_MGMT_HOST = "127.0.0.1";
	public static final String DEFAULT_MGMT_PORT = "9999";
	public static final String ATTRIBUTE_HOST = "host";
	public static final String ATTRIBUTE_PORT = "port";
	public static final String MASTER_CONTROLLER_NODE = "/host/domain-controller/local";
	public static final String MASTER_CONTROLLER_HOST_INTERFACE = "/host/interfaces/interface[@name='management']/inet-address";
	public static final String MASTER_CONTROLLER_PORT_INTERFACE = "/host/management/management-interfaces/native-interface[@security-realm='ManagementRealm']/socket[@interface='management']";
	
	public static final String SLAVE_CONTROLLER_NODE = "/host/domain-controller/remote[@security-realm='ManagementRealm']";
	
	public static Map<String, String> getNodeAttributeValueMap(Node node) {
		Map<String, String> attrMap = new HashMap<String, String>();
		if (node.hasAttributes()) {
			NamedNodeMap namedMap = node.getAttributes();
			for (int i = 0; i < namedMap.getLength(); i++) {
				Attr attr = (Attr) namedMap.item(i);
				if (attr != null) {
					attrMap.put(attr.getName(), attr.getValue());

				}

			}

		}

		return attrMap;
	}
	
	public static String getAttributeValue(Node node, String attributeName, boolean checkChildNodes) {

		if (node.hasAttributes()) {
			Attr attr = (Attr) node.getAttributes().getNamedItem(attributeName);
			if (attr != null) {
				return attr.getValue();

			}
		}

		if (checkChildNodes) {
			NodeList childNodes = node.getChildNodes();
			if (childNodes != null) {
				for (int i = 0; i < childNodes.getLength(); i++) {
					Node childNode = childNodes.item(i);
					if (childNode.getNodeType() == Node.ELEMENT_NODE) {
						String value = getAttributeValue(childNode, attributeName, checkChildNodes);
						if (!value.equals("")) {
							return value;
						}
					}

				}
			}
		}

		return "";

	}
	
	
	public static String getSystemPropertyResolvedValue(String value) {
		String resolvedValue = "";
		Matcher matcher = SYSTEM_PROPERTY_PATTERN.matcher(value);
		while (matcher.find()) {
			String dynamicSystemValue = matcher.group(1);
			String defaultValue = matcher.group(2);
			resolvedValue = System.getProperty(dynamicSystemValue, defaultValue);

		}
		return resolvedValue;
	}

	public static String getSystemPropertyResolvedValue(String value, String defaultValue) {
		String resolvedValue = getSystemPropertyResolvedValue(value);
		return (resolvedValue != null && !resolvedValue.isEmpty() ? resolvedValue : defaultValue);
	}
	
	public static String readHost() {
		ShellCommandResult result = SystemUtils.IS_OS_WINDOWS ? WindowsCmdShell.instance().execute("hostname")
				: UnixShell.instance().execute("hostname");

		String hostName = null;
		if (result.getReturnCode() == 0) {
			hostName = StringUtils.trim(result.getOutput());

		}

		return (StringUtils.isNotBlank(hostName)) ? hostName : DEFAULT_MGMT_HOST;

	}

}
