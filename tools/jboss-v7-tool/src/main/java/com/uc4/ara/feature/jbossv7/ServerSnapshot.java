package com.uc4.ara.feature.jbossv7;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.jboss.as.cli.scriptsupport.CLI.Result;

import com.uc4.ara.feature.FeatureUtil;
import com.uc4.ara.feature.FeatureUtil.MsgTypes;
import com.uc4.ara.feature.jbossv7.schemas.Datasources;
import com.uc4.ara.feature.jbossv7.schemas.DomainServerInfo;
import com.uc4.ara.feature.jbossv7.schemas.DomainServerSnapshot;
import com.uc4.ara.feature.jbossv7.schemas.DomainServerSnapshot.Deployments.Deployment;
import com.uc4.ara.feature.jbossv7.schemas.DomainServerSnapshot.Hosts;
import com.uc4.ara.feature.jbossv7.schemas.DomainServerSnapshot.Profiles;
import com.uc4.ara.feature.jbossv7.schemas.DomainServerSnapshot.ServerGroups;
import com.uc4.ara.feature.jbossv7.schemas.Extensions;
import com.uc4.ara.feature.jbossv7.schemas.Extensions.Extension;
import com.uc4.ara.feature.jbossv7.schemas.GeneralDataSource;
import com.uc4.ara.feature.jbossv7.schemas.Host;
import com.uc4.ara.feature.jbossv7.schemas.Host.Servers;
import com.uc4.ara.feature.jbossv7.schemas.Interface;
import com.uc4.ara.feature.jbossv7.schemas.Interfaces;
import com.uc4.ara.feature.jbossv7.schemas.JdbcDriver;
import com.uc4.ara.feature.jbossv7.schemas.JmsQueue;
import com.uc4.ara.feature.jbossv7.schemas.JmsTopic;
import com.uc4.ara.feature.jbossv7.schemas.Messaging;
import com.uc4.ara.feature.jbossv7.schemas.Messaging.HornetqServer;
import com.uc4.ara.feature.jbossv7.schemas.Profile;
import com.uc4.ara.feature.jbossv7.schemas.Server;
import com.uc4.ara.feature.jbossv7.schemas.ServerGroup;
import com.uc4.ara.feature.jbossv7.schemas.ServerGroupDeployment;
import com.uc4.ara.feature.jbossv7.schemas.ServerInfo;
import com.uc4.ara.feature.jbossv7.schemas.ServerSnapshotType;
import com.uc4.ara.feature.jbossv7.schemas.SocketBindingGroup;
import com.uc4.ara.feature.jbossv7.schemas.SocketBindingGroup.SocketBindings.SocketBinding;
import com.uc4.ara.feature.jbossv7.schemas.SocketBindingGroups;
import com.uc4.ara.feature.jbossv7.schemas.StandaloneDeployment;
import com.uc4.ara.feature.jbossv7.schemas.StandaloneServerInfo;
import com.uc4.ara.feature.jbossv7.schemas.StandaloneServerSnapshot;
import com.uc4.ara.feature.jbossv7.schemas.SubSystems;
import com.uc4.ara.feature.jbossv7.schemas.SubSystems.Subsystem;
import com.uc4.ara.feature.jbossv7.schemas.SystemProperties;
import com.uc4.ara.feature.jbossv7.schemas.SystemProperties.SystemProperty;
import com.uc4.ara.feature.jbossv7.schemas.XaDataSource;
import com.uc4.ara.util.Logger;

public class ServerSnapshot extends AbstractJBossV7Feature {

    private boolean isDomain;

    public ServerSnapshot(String jbossHome, String hostname, int port, String username, String password) {
        super(jbossHome, hostname, port, username, password);
    }

    public ServerSnapshotType createSnapshot()  throws JBossCLIException {
        Result res = null;
        JsonNode root = null;
        ObjectMapper mapper = new ObjectMapper();
        try {
            connect();
            isDomain = cli.getCommandContext().isDomainMode();
            res = cli.cmd("/:read-resource(include-runtime=true,recursive=true,proxies=true)");
            root = mapper.readTree(res.getResponse().get("result").toJSONString(true));
        } catch (Exception e) {
            Logger.log("Failed to connect to JBoss CLI", "ERROR");
            throw new JBossCLIException(e.getMessage());
        } finally {
            disconnect();
        }

        if (root == null) {
            Logger.log("No result from JBoss CLI!", "WARNING");
            return null;
        }

        if (!isDomain) {
            StandaloneServerSnapshot snapshot = objectFactory.createStandaloneServerSnapshot();
            snapshot.setName("Server Snapshot");
            snapshot.setDeployments((com.uc4.ara.feature.jbossv7.schemas.StandaloneServerSnapshot.Deployments) getDeployments(root));
            snapshot.setServerInfo((StandaloneServerInfo) getServerInfo(root));
            snapshot.setSystemProperties(getSystemProperties(root));
            snapshot.setDatasources(getDatasources(root));
            snapshot.setMessaging(getMessaging(root));
            snapshot.setSubsystems(getSubSystems(root));
            snapshot.setInterfaces(getInterfaces(root));
            snapshot.setSocketBindingGroups(getSocketBindingGroups(root));
            snapshot.setExtensions(getExtentions(root));
            return snapshot;
        } else {
            DomainServerSnapshot snapshot = objectFactory.createDomainServerSnapshot();
            snapshot.setName("Server Snapshot");
            snapshot.setDeployments((com.uc4.ara.feature.jbossv7.schemas.DomainServerSnapshot.Deployments) getDeployments(root));
            snapshot.setServerInfo((DomainServerInfo) getServerInfo(root));
            snapshot.setSystemProperties(getSystemProperties(root));
            snapshot.setProfiles(getProfiles(root));
            snapshot.setHosts(getDomainHosts(root));
            snapshot.setServerGroups(getServerGroups(root));
            snapshot.setSocketBindingGroups(getSocketBindingGroups(root));
            snapshot.setExtensions(getExtentions(root));
            return snapshot;
        }

    }

    /**
     * Constructs <code>ServerInfo</code> object from JSON root node, representing overall information about server
     * @param root <code>JsonNode</code> root node
     */
    public ServerInfo getServerInfo(JsonNode root) {
        ServerInfo serverInfo = null;

        if (isDomain) {
            serverInfo = objectFactory.createDomainServerInfo();
            ((DomainServerInfo) serverInfo).setLocalHostName(root.get("local-host-name").getTextValue());
            ((DomainServerInfo) serverInfo).setLaunchType(root.get("launch-type").getTextValue());
        } else {
            serverInfo = objectFactory.createStandaloneServerInfo();
            ((StandaloneServerInfo) serverInfo).setServerState(root.get("server-state").getTextValue());
        }

        serverInfo.setName("Server Information");
        serverInfo.setServerName(root.get("name").getValueAsText());
        serverInfo.setLaunchType(root.get("launch-type").getValueAsText());
        serverInfo.setProductName(root.get("product-name").getValueAsText());
        serverInfo.setProductVersion(root.get("product-version").getValueAsText());
        serverInfo.setReleaseCodename(root.get("release-codename").getValueAsText());
        serverInfo.setReleaseVersion(root.get("release-version").getValueAsText());
        serverInfo.setManagementMajorVersion(root.get("management-major-version").getValueAsText());
        serverInfo.setManagementMinorVersion(root.get("management-minor-version").getValueAsText());
        serverInfo.setManagementMicroVersion(root.get("management-micro-version").getValueAsText());

        return serverInfo;
    }

    /**
     * Builds <code>DomainServerSnapshot.Deployments</code> or <code>StandaloneServerSnapshot.Deployments</code> object from JSON root node, containing list of deployments on server
     * @param root <code>JsonNode</code> root node
     * @return
     */
    public Object getDeployments(JsonNode root) {
        JsonNode deploymentsNode = root.get("deployment");
        Iterator<String> iter = deploymentsNode.getFieldNames();
        Object deployments = null;
        if (isDomain) {
            deployments = objectFactory.createDomainServerSnapshotDeployments();
            while (iter.hasNext()) {
                String depName = iter.next();
                JsonNode depNode = deploymentsNode.get(depName);
                ((DomainServerSnapshot.Deployments) deployments).setName("Deployments");
                Deployment dep = objectFactory.createDomainServerSnapshotDeploymentsDeployment();
                dep.setName(depName);
                dep.setRuntimeName(depNode.get("runtime-name").getValueAsText());
                ((DomainServerSnapshot.Deployments) deployments).getDeployment().add(dep);
            }
        } else {
            deployments = objectFactory.createStandaloneServerSnapshotDeployments();
            while (iter.hasNext()) {
                String depName = iter.next();
                JsonNode depNode = deploymentsNode.get(depName);
                ((StandaloneServerSnapshot.Deployments) deployments).setName("Deployments");
                StandaloneDeployment dep = objectFactory.createStandaloneDeployment();
                dep.setName(depName);
                dep.setEnabled(depNode.get("enabled").getTextValue());
                dep.setRuntimeName(depNode.get("runtime-name").getValueAsText());
                dep.setStatus(depNode.get("status").getValueAsText());
                ((StandaloneServerSnapshot.Deployments) deployments).getDeployment().add(dep);
            }

        }

        return deployments;
    }

    /**
     * Builds <code>SystemProperties</code> object containing list of system properties
     * @param root <code>JsonNode</code> root node
     * @return
     */
    public SystemProperties getSystemProperties(JsonNode root) {
        SystemProperties sysProps = objectFactory.createSystemProperties();
        sysProps.setName("System Properties");
        JsonNode sysPropsNode = root.get("system-property");
        Iterator<String> iter = sysPropsNode.getFieldNames();
        while (iter.hasNext()) {
            String propName = iter.next();
            String value = sysPropsNode.get(propName).get("value").getValueAsText();
            SystemProperty prop = objectFactory.createSystemPropertiesSystemProperty();
            prop.setName(propName);
            prop.setValue(value);
            sysProps.getSystemProperty().add(prop);
        }
        return sysProps;
    }

    /**
     * Builds <code>Datasources</code> object containing list of datasources
     * @param root <code>JsonNode</code> root node
     * @return
     */
    public Datasources getDatasources(JsonNode root) {
        Datasources datasources = objectFactory.createDatasources();
        datasources.setName("Datasources Subsystem");
        datasources.setJdbcDrivers(objectFactory.createJdbcDrivers());
        datasources.setDataSources(objectFactory.createGeneralDataSources());
        datasources.setXaDataSources(objectFactory.createXaDataSources());

        datasources.getDataSources().setName("Datasources");
        datasources.getJdbcDrivers().setName("Jdbc Drivers");
        datasources.getXaDataSources().setName("Xa Datasources");

        JsonNode dsRootNode = root.get("subsystem").get("datasources");
        if (dsRootNode == null) return datasources;

        JsonNode driversNode = dsRootNode.get("installed-drivers");
        JsonNode dataSourceNode = dsRootNode.get("data-source");
        JsonNode xaDataSourceNode = dsRootNode.get("xa-data-source");

        if (driversNode == null) {
            driversNode = dsRootNode.get("jdbc-driver");
            Iterator<String> iter = driversNode.getFieldNames();
            while (iter.hasNext()) {
                String drvName = iter.next();
                JsonNode drvNode = driversNode.get(drvName);
                JdbcDriver driver = objectFactory.createJdbcDriver();
                driver.setName(drvName);
                driver.setDriverName(drvNode.get("driver-name").getValueAsText());
                if (drvNode.has("driver-module-name")) driver.setDriverModuleName(drvNode.get("driver-module-name").getValueAsText());
                if (drvNode.has("driver-class-name")) driver.setDriverClassName(drvNode.get("driver-class-name").getValueAsText());
                if (drvNode.has("driver-major-version")) driver.setDriverMajorVersion(drvNode.get("driver-major-version").getValueAsText());
                if (drvNode.has("driver-minor-version")) driver.setDriverMinorVersion(drvNode.get("driver-minor-version").getValueAsText());
                if (drvNode.has("jdbc-compliant")) driver.setJdbcCompliant(drvNode.get("jdbc-compliant").getValueAsText());
                datasources.getJdbcDrivers().getJdbcDriver().add(driver);
            }
        } else {
            for (int i = 0; i < driversNode.size(); i++) {
                JsonNode driverNode = driversNode.get(i);
                JdbcDriver driver = objectFactory.createJdbcDriver();
                String name = driverNode.get("driver-name").getValueAsText();
                driver.setName(name);
                driver.setDriverName(name);
                if (driverNode.has("driver-class-name")) driver.setDriverClassName(driverNode.get("driver-class-name").getValueAsText());
                if (driverNode.has("driver-major-version")) driver.setDriverMajorVersion(driverNode.get("driver-major-version").getValueAsText());
                if (driverNode.has("driver-minor-version")) driver.setDriverMinorVersion(driverNode.get("driver-minor-version").getValueAsText());
                if (driverNode.has("jdbc-compliant")) driver.setJdbcCompliant(driverNode.get("jdbc-compliant").getValueAsText());
                if (driverNode.has("driver-module-name")) driver.setDriverModuleName(driverNode.get("driver-module-name").getValueAsText());
                datasources.getJdbcDrivers().getJdbcDriver().add(driver);
            }
        }

        Iterator<String> iter = dataSourceNode.getFieldNames();
        while (iter.hasNext()) {
            String dsName = iter.next();
            JsonNode dsNode = dataSourceNode.get(dsName);
            GeneralDataSource ds = objectFactory.createGeneralDataSource();
            ds.setName(dsName);
            ds.setDriverName(dsNode.get("driver-name").getValueAsText());
            ds.setDriverClass(dsNode.get("driver-class").getValueAsText());
            ds.setEnabled(dsNode.get("enabled").getValueAsText());
            ds.setJndiName(dsNode.get("jndi-name").getValueAsText());
            ds.setDatasourceClass(dsNode.get("datasource-class").getValueAsText());
            ds.setConnectionUrl(dsNode.get("connection-url").getValueAsText());
            datasources.getDataSources().getDataSource().add(ds);
        }

        iter = xaDataSourceNode.getFieldNames();
        while (iter.hasNext()) {
            String dsName = iter.next();
            JsonNode dsNode = xaDataSourceNode.get(dsName);
            XaDataSource ds = objectFactory.createXaDataSource();
            ds.setName(dsName);
            ds.setDriverName(dsNode.get("driver-name").getValueAsText());
            ds.setEnabled(dsNode.get("enabled").getValueAsText());
            ds.setJndiName(dsNode.get("jndi-name").getValueAsText());
            ds.setXaDatasourceClass(dsNode.get("xa-datasource-class").getValueAsText());
            datasources.getXaDataSources().getXaDataSource().add(ds);
        }

        return datasources;
    }

    /**
     * Builds <code>Messaging</code> object, focus on JMS messaging subsystem.
     * @param root <code>JsonNode</code> root node
     * @return
     */
    public Messaging getMessaging(JsonNode root) {
        Messaging messaging = objectFactory.createMessaging();
        messaging.setName("Messaging Subsystem");
        JsonNode messagingNode = root.get("subsystem").get("messaging");
        if (messagingNode == null) return messaging;
        JsonNode hornetQNode = messagingNode.get("hornetq-server");
        Iterator<String> iter = hornetQNode.getFieldNames();
        while (iter.hasNext()) {
            String hornetQName = iter.next();
            JsonNode hornetQServerNode = hornetQNode.get(hornetQName);
            HornetqServer hornetQ = objectFactory.createMessagingHornetqServer();
            hornetQ.setName(hornetQName);
            hornetQ.setClustered(hornetQServerNode.get("clustered").getValueAsText());
            hornetQ.setJmxDomain(hornetQServerNode.get("jmx-domain").getValueAsText());
            hornetQ.setManagementAddress(hornetQServerNode.get("management-address").getValueAsText());
            if (hornetQServerNode.has("version")) hornetQ.setVersion(hornetQServerNode.get("version").getValueAsText());

            hornetQ.setJmsQueues(objectFactory.createMessagingHornetqServerJmsQueues());
            hornetQ.setJmsTopics(objectFactory.createMessagingHornetqServerJmsTopics());

            JsonNode queuesNode = hornetQServerNode.get("jms-queue");
            JsonNode topicsNode = hornetQServerNode.get("jms-topic");

            Iterator<String> iter1 = queuesNode.getFieldNames();
            while (iter1.hasNext()) {
                String queueName = iter1.next();
                JmsQueue queue = objectFactory.createJmsQueue();
                JsonNode queueNode = queuesNode.get(queueName);
                queue.setName(queueName);
                if (queueNode.get("entries").size() > 0) queue.setEntries(queueNode.get("entries").get(0).getValueAsText());
                if (queueNode.has("queue-address")) queue.setQueueAddress(queueNode.get("queue-address").getValueAsText());
                hornetQ.getJmsQueues().getJmsQueue().add(queue);
            }

            iter1 = topicsNode.getFieldNames();
            while (iter1.hasNext()) {
                String topicName = iter1.next();
                JmsTopic topic = objectFactory.createJmsTopic();
                JsonNode topicNode = topicsNode.get(topicName);
                topic.setName(topicName);
                if (topicNode.get("entries").size() > 0) topic.setEntries(topicNode.get("entries").get(0).getValueAsText());
                if (topicNode.has("topic-address")) topic.setTopicAddress(topicNode.get("topic-address").getValueAsText());
                hornetQ.getJmsTopics().getJmsTopic().add(topic);
            }

            messaging.getHornetqServer().add(hornetQ);
        }

        return messaging;
    }

    /**
     * Builds list of <code>Subsystem</code> objects representing list of subsystems which were configured on server.
     * @param root <code>JsonNode</code> root node
     * @return
     */
    public SubSystems getSubSystems(JsonNode root) {
        SubSystems subsystems = objectFactory.createSubSystems();
        subsystems.setName("Subsystems");
        Iterator<String> iter = root.get("subsystem").getFieldNames();
        while (iter.hasNext()) {
            String ssName = iter.next();
            Subsystem ss = objectFactory.createSubSystemsSubsystem();
            ss.setName(ssName);
            subsystems.getSubsystem().add(ss);
        }
        return subsystems;
    }

    /**
     * List of <code>Extension</code>
     * @param root <code>JsonNode</code> root node
     * @return
     */
    public Extensions getExtentions(JsonNode root) {
        Extensions extensions = objectFactory.createExtensions();
        extensions.setName("Extensions");
        Iterator<String> iter = root.get("extension").getFieldNames();
        while (iter.hasNext()) {
            String exName = iter.next();
            Extension ex = objectFactory.createExtensionsExtension();
            ex.setName(exName);
            extensions.getExtension().add(ex);
        }
        return extensions;
    }

    /**
     * Builds list of <code>Profile</code> objects representing all profiles in Domain mode.
     * @param root <code>JsonNode</code> root node
     * @return
     */
    public Profiles getProfiles(JsonNode root) {
        Profiles profiles = objectFactory.createDomainServerSnapshotProfiles();
        profiles.setName("Profiles");
        JsonNode profilesNode = root.get("profile");
        if (profilesNode == null) return profiles;
        Iterator<String> iter = profilesNode.getFieldNames();
        while (iter.hasNext()) {
            String pName = iter.next();
            Profile profile = objectFactory.createProfile();
            profile.setName(pName);
            profiles.getProfile().add(profile);

            profile.setSubsystems(objectFactory.createSubSystems());
            JsonNode pNode = profilesNode.get(pName);
            JsonNode ssNode = pNode.get("subsystem");
            if (ssNode == null) continue;

            // process datasources subsystem
            JsonNode dssNode = ssNode.get("datasources");
            if (dssNode != null) {
                Datasources pDatasources = getDatasources(pNode);
                profile.setDatasources(pDatasources);
            }

            // process messaging subsystem
            JsonNode msgNode = ssNode.get("messaging");
            if (msgNode != null) {
                Messaging pMessaging = getMessaging(pNode);
                profile.setMessaging(pMessaging);
            }

            // get list of subsystems
            Iterator<String> iter1 = ssNode.getFieldNames();
            while (iter1.hasNext()) {
                String ssName = iter1.next();
                Subsystem ss = objectFactory.createSubSystemsSubsystem();
                ss.setName(ssName);
                profile.getSubsystems().getSubsystem().add(ss);
            }
        }
        return profiles;
    }

    /**
     * Builds list of <code>Interface</code> objects
     * @param root <code>JsonNode</code> root node or parent node
     * @return
     */
    public Interfaces getInterfaces(JsonNode root) {

        Interfaces interfaces = objectFactory.createInterfaces();
        interfaces.setName("Interfaces");
        JsonNode interfacesNode = root.get("interface");
        if (interfacesNode == null) return null;
        Iterator<String> iter = interfacesNode.getFieldNames();
        while (iter.hasNext()) {
            String iName = iter.next();
            JsonNode iNode = interfacesNode.get(iName);
            Interface interfake = objectFactory.createInterface();
            interfake.setName(iName);
            String inetAddr = "";
            if (iNode.get("inet-address").isObject()) {
                try {
                    inetAddr = iNode.get("inet-address").get("EXPRESSION_VALUE").getValueAsText();
                } catch (Exception e) {
                    inetAddr = iNode.get("inet-address").toString();
                }
            } else inetAddr = iNode.get("inet-address").getValueAsText();
            interfake.setInetAddress(inetAddr);
            interfaces.getInterface().add(interfake);
        }
        return interfaces;
    }

    /**
     * Builds <code>SocketBindingsGroups</code> object containing list of <code>SocketBindingGroup</code> objects.
     * Each <code>SocketBindingGroup</code> contains a list of <code>SocketBinding</code>.
     * @param root <code>JsonNode</code> root node
     * @return
     */
    public SocketBindingGroups getSocketBindingGroups(JsonNode root) {
        SocketBindingGroups sbgs = objectFactory.createSocketBindingGroups();
        sbgs.setName("Socket Binding Groups");
        JsonNode sbgsNode = root.get("socket-binding-group");
        Iterator<String> iter = sbgsNode.getFieldNames();
        while (iter.hasNext()) {
            String sbgName = iter.next();
            SocketBindingGroup sbg = objectFactory.createSocketBindingGroup();
            JsonNode sbgNode = sbgsNode.get(sbgName);
            sbg.setName(sbgName);
            sbg.setDefaultInterface(sbgNode.get("default-interface").getValueAsText());
            sbg.setSocketBindings(objectFactory.createSocketBindingGroupSocketBindings());
            sbg.getSocketBindings().setName("Socket Bindings");
            JsonNode sbsNode = sbgNode.get("socket-binding");
            Iterator<String> iter1 = sbsNode.getFieldNames();
            while (iter1.hasNext()) {
                String sbName = iter1.next();
                JsonNode sbNode = sbsNode.get(sbName);
                SocketBinding sb = objectFactory.createSocketBindingGroupSocketBindingsSocketBinding();
                sb.setName(sbName);
                sb.setPort(sbNode.get("port").getValueAsText());
                sbg.getSocketBindings().getSocketBinding().add(sb);
            }

            sbgs.getSocketBindingGroup().add(sbg);
        }

        return sbgs;
    }

    /**
     * Builds <code>Hosts</code> object containing list of <code>Host</code> objects.
     * Each <code>Host</code> object has some <code>Server</code> objects representing server instances on that host.
     * @param root <code>JsonNode</code> root node
     * @return
     */
    public Hosts getDomainHosts(JsonNode root) {
        Hosts hosts = objectFactory.createDomainServerSnapshotHosts();
        hosts.setName("Domain Hosts");
        JsonNode hostsNode = root.get("host");
        if (hostsNode == null) return hosts;
        Iterator<String> iter = hostsNode.getFieldNames();
        while (iter.hasNext()) {
            String hostName = iter.next();
            JsonNode hostNode = hostsNode.get(hostName);

            Host host = objectFactory.createHost();
            host.setName(hostName);
            host.setProductName(hostNode.get("product-name").getValueAsText());
            host.setProductVersion(hostNode.get("product-version").getValueAsText());
            host.setReleaseVersion(hostNode.get("release-version").getValueAsText());
            host.setRunningMode(hostNode.get("running-mode").getValueAsText());
            host.setMaster(hostNode.get("master").getValueAsText());
            host.setHostState(hostNode.get("host-state").getValueAsText());
            host.setDirectoryGrouping(hostNode.get("directory-grouping").getValueAsText());
            hosts.getHost().add(host);

            host.setInterfaces(getInterfaces(hostNode));

            Servers serversEle = objectFactory.createHostServers();
            serversEle.setName("Host Servers");
            host.setServers(serversEle);
            try {
                List<Server> servers = getDomainHostServers(hostName);
                if (servers != null) host.getServers().getServer().addAll(servers);
            } catch (Exception e) {
                FeatureUtil.logMsg("Failed to retrieve servers from host " + hostName, MsgTypes.WARNING);
                FeatureUtil.logMsg("Error message: " + e.getMessage(), MsgTypes.WARNING);
            }
        }

        return hosts;
    }

    /**
     * Gets list of <code>Server</code> object within the given host in domain mode
     * @param hostname name of host
     * @return <code>List</code> of <code>Server</code> objects
     * @throws Exception
     */
    private List<Server> getDomainHostServers(String hostname) throws Exception {
        try {
            connect();
            Result res = cli.cmd("/host=" + hostname + "/:read-resource");
            ObjectMapper mapper = new ObjectMapper();
            JsonNode hostNode = mapper.readTree(res.getResponse().get("result").toJSONString(true));
            JsonNode serversNode = hostNode.get("server");
            if (serversNode == null) {
                return null;
            }
            List<Server> servers = new ArrayList<Server>();
            Iterator<String> iter = serversNode.getFieldNames();
            while (iter.hasNext()) {
                String serverName = iter.next();
                res = cli.cmd("/host=" + hostname + "/server=" + serverName + "/:read-resource(include-runtime=true)");
                JsonNode serverNode = mapper.readTree(res.getResponse().get("result").toJSONString(true));
                if (serverNode == null) continue;
                Server server = objectFactory.createServer();
                server.setName(serverName);
                server.setServerState(serverNode.get("server-state").getValueAsText());
                server.setRunningMode(serverNode.get("running-mode").getValueAsText());

                if (hostNode.get("server-config") != null) {
                    res = cli.cmd("/host=" + hostname + "/server-config=" + serverName + "/:read-resource(include-runtime=true)");
                    JsonNode serverConfigNode = mapper.readTree(res.getResponse().get("result").toJSONString(true));
                    if (serverConfigNode != null) {
                        server.setAutoStart(serverConfigNode.get("auto-start").getValueAsText());
                        server.setGroup(serverConfigNode.get("group").getValueAsText());
                        server.setSocketBindingGroup(serverConfigNode.get("socket-binding-group").getValueAsText());
                        server.setSocketBindingPortOffset(serverConfigNode.get("socket-binding-port-offset").getValueAsText());
                    }
                }
                servers.add(server);
            }
            return servers;
        } finally {
            disconnect();
        }
    }

    /**
     * Builds <code>ServerGroups</code>
     * @param root <code>JsonNode</code> root node
     * @return
     */
    public ServerGroups getServerGroups(JsonNode root) {
        ServerGroups sgs = objectFactory.createDomainServerSnapshotServerGroups();
        sgs.setName("Server Groups");
        JsonNode sgsNode = root.get("server-group");
        if (sgsNode == null) return sgs;
        Iterator<String> iter = sgsNode.getFieldNames();
        while (iter.hasNext()) {
            String sgName = iter.next();
            ServerGroup sg = objectFactory.createServerGroup();
            JsonNode sgNode = sgsNode.get(sgName);
            sg.setName(sgName);
            sg.setProfile(sgNode.get("profile").getValueAsText());
            sg.setSocketBindingGroup(sgNode.get("socket-binding-group").getValueAsText());
            sg.setSocketBindingPortOffset(sgNode.get("socket-binding-port-offset").getValueAsText());

            sg.setDeployments(objectFactory.createServerGroupDeployments());

            JsonNode deploymentsNode = sgNode.get("deployment");
            if (deploymentsNode != null) {
                Iterator<String> iter1 = deploymentsNode.getFieldNames();
                while (iter1.hasNext()) {
                    String depName = iter1.next();
                    JsonNode depNode = deploymentsNode.get(depName);
                    ServerGroupDeployment sgd = objectFactory.createServerGroupDeployment();
                    sgd.setName("Server Group Deployments");
                    sgd.setName(depName);
                    sgd.setEnabled(depNode.get("enabled").getValueAsText());
                    sgd.setRuntimeName(depNode.get("runtime-name").getValueAsText());
                    sg.getDeployments().getDeployment().add(sgd);
                }
            }

            sgs.getServerGroup().add(sg);

        }

        return sgs;
    }
}
