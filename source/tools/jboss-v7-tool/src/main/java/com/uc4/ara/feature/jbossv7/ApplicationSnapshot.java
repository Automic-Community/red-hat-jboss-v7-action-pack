package com.uc4.ara.feature.jbossv7;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.jboss.as.cli.scriptsupport.CLI.Result;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.uc4.ara.feature.FeatureUtil;
import com.uc4.ara.feature.jbossv7.schemas.ApplicationRuntimeType;
import com.uc4.ara.feature.jbossv7.schemas.ApplicationType;
import com.uc4.ara.feature.jbossv7.schemas.DeploymentDescriptorsType;
import com.uc4.ara.feature.jbossv7.schemas.DsSubsystemType;
import com.uc4.ara.feature.jbossv7.schemas.Ejb3SubsystemType;
import com.uc4.ara.feature.jbossv7.schemas.EjbDDType;
import com.uc4.ara.feature.jbossv7.schemas.EjbResType;
import com.uc4.ara.feature.jbossv7.schemas.EjbResType.EjbLocalRefs.EjbLocalRef;
import com.uc4.ara.feature.jbossv7.schemas.EjbResType.EjbRefs.EjbRef;
import com.uc4.ara.feature.jbossv7.schemas.EntityBeanType;
import com.uc4.ara.feature.jbossv7.schemas.FileGroupType;
import com.uc4.ara.feature.jbossv7.schemas.FileType;
import com.uc4.ara.feature.jbossv7.schemas.MessageDrivenBeanType;
import com.uc4.ara.feature.jbossv7.schemas.MessageDrivenBeanType.ActivationConfigProperty;
import com.uc4.ara.feature.jbossv7.schemas.MessageDrivenBeanType.MessageDrivenDestination;
import com.uc4.ara.feature.jbossv7.schemas.RarDDType;
import com.uc4.ara.feature.jbossv7.schemas.RarSubsystemType;
import com.uc4.ara.feature.jbossv7.schemas.ResourceAdapterType;
import com.uc4.ara.feature.jbossv7.schemas.ResourceAdapterType.ConfigProperties.ConfigProperty;
import com.uc4.ara.feature.jbossv7.schemas.ResourceAdapterType.ConnectionDefinitions.ConnectionDefinition;
import com.uc4.ara.feature.jbossv7.schemas.ResourceRefType;
import com.uc4.ara.feature.jbossv7.schemas.ResourceRefType.EnvEntries.EnvEntry;
import com.uc4.ara.feature.jbossv7.schemas.ResourceRefType.ResourceEnvRefs.ResourceEnvRef;
import com.uc4.ara.feature.jbossv7.schemas.ResourceRefType.ResourceRefs.ResourceRef;
import com.uc4.ara.feature.jbossv7.schemas.SarDDType;
import com.uc4.ara.feature.jbossv7.schemas.SarDDType.Mbeans.Mbean;
import com.uc4.ara.feature.jbossv7.schemas.SarSubsystemType;
import com.uc4.ara.feature.jbossv7.schemas.ServletType;
import com.uc4.ara.feature.jbossv7.schemas.SessionBeanType;
import com.uc4.ara.feature.jbossv7.schemas.SubDeploymentType;
import com.uc4.ara.feature.jbossv7.schemas.SubsystemType;
import com.uc4.ara.feature.jbossv7.schemas.WebDDType;
import com.uc4.ara.feature.jbossv7.schemas.WebSubsystemType;
import com.uc4.ara.feature.utils.FileUtil;
import com.uc4.ara.feature.utils.SnapshotUtil;
import com.uc4.ara.feature.utils.ZipUtil;
import com.uc4.ara.util.Logger;

public class ApplicationSnapshot extends AbstractJBossV7Feature {

    protected final String appName;
    protected final String archivePath;
    protected final String excludeDirs;
    private  String serverInstance;

    public ApplicationSnapshot(String jbossHome, String appName, String hostname, int port,
            String username, String password, String hostController, String serverInstance, String archivePath, String excludedDirs) {

        super(jbossHome, hostname, port, username, password);

        this.archivePath = archivePath;
        this.excludeDirs = excludedDirs;
        this.appName = appName.trim();

        if (hostController == null || hostController.equals("")
                || serverInstance == null || serverInstance.equals(""))
        {
            this.serverInstance = "";
        }
        else
            this.serverInstance = "/host=" + hostController + "/server=" +serverInstance;

    }

    public ApplicationType createSnapshot(boolean zipFile) throws Exception {

        ApplicationType applicationType = objectFactory.createApplicationType();
        applicationType.setName("Application Snapshot");

        File applicationFile = getApplicationFile(appName);
        ApplicationRuntimeType runtime = createApplicationRuntimeType();
        runtime.setPath(applicationFile.getAbsolutePath());

        applicationType.setApplicationRuntime(runtime);

        // Create Subsystem (only available if application is enabled)
        applicationType.setSubSystems(createSubsystemType());

        // Copy (and extract) application to a temporary directory
        File appWorkingDir = createApplicationDirectory(applicationFile);

        try {
            // Create Sub Deployment ( ear file only)
            SubDeploymentType subDeployment = createSubDeploymentType(appWorkingDir);
            if (subDeployment != null)
            {
                applicationType.setSubDeployments(subDeployment);
            }
            else
            {
                applicationType.setDeploymentDescriptors(createDeploymentDescriptorsType(appWorkingDir));
            }

            applicationType.setOtherFiles(createFileGroupType(appWorkingDir, "Other files", null, ".*\\.class"));
            applicationType.setClassFiles(createFileGroupType(appWorkingDir, "Class files", ".*\\.class", null));

            if (zipFile)
            {
                File snapZip = new File(archivePath, "snap.zip");
                ZipUtil.zipDirectory(appWorkingDir, snapZip);
                Logger.log("Snap archive file '" + snapZip + "' was created successfully.", "INFO");
            }

        } finally {
            FileUtil.deleteDirectory(appWorkingDir);
        }

        return applicationType;
    }

    private ApplicationRuntimeType createApplicationRuntimeType() throws JBossCLIException {

        ApplicationRuntimeType runtime = objectFactory.createApplicationRuntimeType();
        Result res = null;
        String jsonResult = "null";
        try {
            connect();

            if (! cli.getCommandContext().isDomainMode())
                serverInstance = "";

            try {
                res = cli.cmd(serverInstance + "/deployment=" + appName + "/:read-resource(recursive=false,include-runtime=true)");
                jsonResult = res.getResponse().get("result").toJSONString(true);
            } catch(Exception e ) {

            }

            if (jsonResult.equals("null"))
            {
                serverInstance = "";
                res = cli.cmd("/deployment=" + appName + "/:read-resource(recursive=false,include-runtime=true)");
                jsonResult = res.getResponse().get("result").toJSONString(true);
            }
            else if (! serverInstance.equals(""))
            {
                runtime.setHostController(
                        serverInstance.substring(0, serverInstance.indexOf("/server=")).replace("/host=", ""));
                runtime.setServerInstance(
                        serverInstance.replaceFirst("/host=.*/server=", ""));
            }

            ObjectMapper mapper = new ObjectMapper();
            JsonNode resultNode = null;
            try {
                resultNode = mapper.readTree(jsonResult);
            } catch (Exception e) {
                Logger.log("Cannot read application runtime information.", "ERROR");
                return null;
            }

            setObjectFields(resultNode, runtime);
            runtime.setAppName(appName);
            runtime.setName("Application Runtime");

            return runtime;

        }  finally {
            disconnect();
        }
    }

    // ********************************************** Subsystem module **********************************************

    private SubsystemType createSubsystemType() throws JBossCLIException, JsonProcessingException, IOException {

        SubsystemType subSystemType = objectFactory.createSubsystemType();
        subSystemType.setName("Subsystems");

        boolean isSubsystemExist = false;

        try {
            connect();

            Result result = cli.cmd(serverInstance +
                    "/deployment=" + appName + ":read-children-resources(child-type=subsystem,recursive=true)");

            String jsonResult = result.getResponse().get("result").toJSONString(true);
            if (jsonResult.equals("null"))
            {
                Logger.log("WARN: Cannot read subsystem information. " +
                        "Maybe the application is not currently enabled or subsystem is not provided.", "INFO");
                return null;
            }

            ObjectMapper mapper = new ObjectMapper();
            JsonNode resultNode = mapper.readTree(jsonResult);

            Iterator<String> iter = resultNode.fieldNames();

            while(iter.hasNext()) {
                String subSystem = iter.next();
                JsonNode node = resultNode.get(subSystem);

                if (subSystem.equals("ejb3"))
                {
                    subSystemType.setEjb3(createEjb3SubsystemType(node));
                }
                else if (subSystem.equals("web"))
                {
                    subSystemType.setWeb(createWebSubsystemType(node));
                }
                else if (subSystem.equals("resource-adapters"))
                {
                    subSystemType.setResourceAdapters(createRarSubsystemType(node));
                }
                else if (subSystem.equals("sar"))
                {
                    subSystemType.setSar(createSarSubsystemType(node));
                }
                else if (subSystem.equals("datasources"))
                {
                    subSystemType.setDatasources(createDsSubsystemType(node));
                }

                isSubsystemExist = true;
            }

            if (isSubsystemExist)
            {
                return subSystemType;
            }
            else
                return null;

        } finally {
            disconnect();
        }

    }

    private DsSubsystemType createDsSubsystemType(JsonNode node) {
        Iterator<String> iter = node.fieldNames();
        DsSubsystemType ds = objectFactory.createDsSubsystemType();

        while (iter.hasNext())
        {
            String dsType = iter.next(); // data-source, xa-data-source
            if (! node.get(dsType).toString().equals("null"))
            {
                Iterator<String> iter2 = node.get(dsType).fieldNames();
                while(iter2.hasNext())
                {
                    String jndiName = iter2.next();

                    DsSubsystemType.Datasource d = objectFactory.createDsSubsystemTypeDatasource();
                    setObjectFields(
                            node.get(dsType).get(jndiName), d);
                    d.setDsType(dsType);
                    d.setJndiName(jndiName);

                    ds.getDatasource().add(d);
                }
            }
        }

        return ds;
    }

    /**
     * @param node Contain all attributes of EJB3 (session-bean, entity-bean ...)
     * @return
     * @throws JsonProcessingException
     * @throws IOException
     */
    private Ejb3SubsystemType createEjb3SubsystemType(JsonNode node) {

        Iterator<String> iter = node.fieldNames();
        Ejb3SubsystemType ejb3 = objectFactory.createEjb3SubsystemType();

        while (iter.hasNext())
        {
            String beanType = iter.next();//stateless-session-bean, stateful-session-bean ...

            if (! node.get(beanType).toString().equals("null"))
            {
                Iterator<String> iter2 = node.get(beanType).fieldNames();

                while(iter2.hasNext())
                {
                    String beanName = iter2.next();

                    Ejb3SubsystemType.Bean bean = objectFactory.createEjb3SubsystemTypeBean();
                    setObjectFields(node.get(beanType).get(beanName), bean );
                    bean.setName(beanName);
                    bean.setBeanType(beanType);

                    ejb3.getBean().add(bean);
                }
            }
        }

        return ejb3;
    }

    /**
     * @param node Contain all attributes of WEB (context-root, servlet .. )
     * @return
     */
    private WebSubsystemType createWebSubsystemType(JsonNode node)  {

        WebSubsystemType web = objectFactory.createWebSubsystemType();

        node = node.get("servlet");
        Iterator<String> iter = node.fieldNames();

        while (iter.hasNext())
        {
            String svlName = iter.next();
            JsonNode svlInfo = node.get(svlName);

            String svlClass = svlInfo.get("servlet-class").textValue();

            WebSubsystemType.Servlet s = objectFactory.createWebSubsystemTypeServlet();
            s.setServletClass(svlClass);
            s.setServletName(svlName);

            web.getServlet().add(s);
        }
        if (web.getServlet().size() > 0)
            return web;

        return null;
    }

    private RarSubsystemType createRarSubsystemType(JsonNode node) {
        RarSubsystemType rarSubsystem = objectFactory.createRarSubsystemType();

        List<JsonNode> resourceAdapterNodeList = node.findValues("resource-adapter");
        for (JsonNode raNode : resourceAdapterNodeList) {
            Iterator<String> iter = raNode.fieldNames();
            // loops through all resource adapters
            while (iter.hasNext()) {
                ResourceAdapterType resourceAdapter = objectFactory.createResourceAdapterType();
                resourceAdapter.setConnectionDefinitions(objectFactory.createResourceAdapterTypeConnectionDefinitions());
                resourceAdapter.setConfigProperties(objectFactory.createResourceAdapterTypeConfigProperties());
                String raName = iter.next();
                resourceAdapter.setName(raName);

                JsonNode adtNode = raNode.get(raName);
                JsonNode connectionDefinitions = adtNode.get("connection-definitions");
                Iterator<String> cdNameIter = connectionDefinitions.fieldNames();
                while (cdNameIter.hasNext()) {
                    String cdName = cdNameIter.next();
                    JsonNode conDefNode = connectionDefinitions.get(cdName);
                    ConnectionDefinition conDef = objectFactory.createResourceAdapterTypeConnectionDefinitionsConnectionDefinition();
                    conDef.setClassName(conDefNode.get("class-name").textValue());
                    conDef.setJndiName(conDefNode.get("jndi-name").textValue());
                    resourceAdapter.getConnectionDefinitions().getConnectionDefinition().add(conDef);
                }

                JsonNode configProperties = adtNode.get("config-properties");
                Iterator<String> cpNameIter = configProperties.fieldNames();
                while (cpNameIter.hasNext()) {
                    String cpName = cpNameIter.next();
                    JsonNode configPropNode = configProperties.get(cpName);
                    ConfigProperty configProp = objectFactory.createResourceAdapterTypeConfigPropertiesConfigProperty();
                    configProp.setConfigPropertyName(configPropNode.get("config-property-name").textValue());
                    configProp.setConfigPropertyType(configPropNode.get("config-property-type").textValue());
                    configProp.setConfigPropertyValue(configPropNode.get("config-propery-value").textValue());
                    resourceAdapter.getConfigProperties().getConfigProperty().add(configProp);
                }

                rarSubsystem.getResourceAdapter().add(resourceAdapter);
            }
        }

        if (rarSubsystem.getResourceAdapter().size() > 0)
            return rarSubsystem;

        return null;
    }

    // TODO: nothing to do with this subsystem atm.
    private SarSubsystemType createSarSubsystemType(JsonNode node) {
        return null;
    }

    // ********************************************** Sub deployment module **********************************************

    /**
     * @param earDirectory
     * @return	Map contains module names. Ex: [web-uri:{sample.war}, java:{common.jar, sample.jar}, ejb:{sampleEJB.jar}]
     * @throws JBossCLIException
     * @throws ParserConfigurationException
     */
    private Map<String, ArrayList<String>> getSubDeploymentFromEar(File earDirectory) throws JBossCLIException, ParserConfigurationException {

        Map<String, ArrayList<String> > appTypes = new HashMap<String, ArrayList<String> >();

        File applicationXml = new File(earDirectory, "META-INF/application.xml");

        if (! applicationXml.exists() )
            return appTypes;

        Document doc = SnapshotUtil.createDocumentFromFile(applicationXml);
        if(doc == null) {
            FeatureUtil.logMsg("Warning! Can't parse " + applicationXml + "!");
            return appTypes;
        }

        Element root = doc.getDocumentElement();

        for (String module : new String[]{"web-uri", "connector", "java", "ejb"})
        {
            NodeList xmlFileNodes = root.getElementsByTagNameNS(root.getNamespaceURI(),module);
            if(xmlFileNodes.getLength() > 0)
            {
                ArrayList<String> appNames = new ArrayList<String>();

                for(int i = 0; i < xmlFileNodes.getLength(); i++)
                    appNames.add(
                            xmlFileNodes.item(i).getTextContent());

                if(appNames.size() > 0)
                    appTypes.put(module, appNames);
            }
        }

        return appTypes;
    }

    private SubDeploymentType createSubDeploymentType(File earDirectory) throws Exception {

        SubDeploymentType subDeploymentType = objectFactory.createSubDeploymentType();
        subDeploymentType.setName("Subdeployments");
        Map<String, ArrayList<String>> appTypes = getSubDeploymentFromEar(earDirectory);

        if (appTypes.size() == 0)
            return null;

        for (File moduleFile : earDirectory.listFiles())

            for (String appType : appTypes.keySet())

                if (appTypes.get(appType).contains(moduleFile.getName()))
                {
                    if (appType.equals("ejb") || appType.equals("web-uri") || appType.equals("connector"))
                    {
                        File moduleWorkingDir = createApplicationDirectory(moduleFile);
                        try {
                            SubDeploymentType.Subdeployment s = objectFactory.createSubDeploymentTypeSubdeployment();
                            s.setName(moduleFile.getName());

                            s.setDeploymentDescriptors(createDeploymentDescriptorsType(moduleWorkingDir));
                            s.setSubSystems(createSubsystemType());

                            s.setOtherFiles(createFileGroupType(moduleWorkingDir, "Other files", null, ".*\\.class"));
                            s.setClassFiles(createFileGroupType(moduleWorkingDir, "Class files", ".*\\.class", null));

                            subDeploymentType.getSubdeployment().add(s);
                        } finally {
                            FileUtil.deleteDirectory(moduleWorkingDir);
                        }
                    }
                }

        return subDeploymentType;
    }

    // ********************************************** Deployment descriptor module **********************************************

    private DeploymentDescriptorsType createDeploymentDescriptorsType(File appWorkingDir) {

        DeploymentDescriptorsType desDescriptors = objectFactory.createDeploymentDescriptorsType();
        desDescriptors.setName("Deployment Descriptors");

        try {
            desDescriptors.setEjb(createEjbDDType(appWorkingDir));
            desDescriptors.setWar(createWebDDType(appWorkingDir));
            desDescriptors.setRar(createRarDDType(appWorkingDir));
            desDescriptors.setSar(createSarDDType(appWorkingDir));
        } catch (Exception e) {
            Logger.log("Warning: Error when getting information from Deployment descriptor in '" +
                    appWorkingDir.getAbsolutePath() + "'. Message: " + e.getMessage() , "ERROR");
            return null;
        }

        if (desDescriptors.getEjb() != null || desDescriptors.getRar() != null
                || desDescriptors.getWar() != null || desDescriptors.getSar() != null)
        {
            return desDescriptors;
        }
        return null;
    }

    private WebDDType createWebDDType(File webDirectory) throws SAXException, IOException, ParserConfigurationException {

        WebDDType war = objectFactory.createWebDDType();
        war.setName("web.xml");
        war.setServlets(objectFactory.createWebDDTypeServlets());

        File webXml = new File(webDirectory, "WEB-INF/web.xml");
        if ( !webXml.exists()) {
            return null;
        }

        Document xmlDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(webXml);

        Element root = xmlDoc.getDocumentElement();

        // extract general info. about webapp?
        NodeList generalChildNodes = root.getChildNodes();

        for (int j = 0; j < generalChildNodes.getLength(); j++)
        {
            Node node = generalChildNodes.item(j);

            if (node.getNodeType() != Node.ELEMENT_NODE)
                continue;

            String nodeName = (node.getLocalName() == null) ? node.getNodeName() : node.getLocalName();

            if (nodeName.equals("display-name"))
            {
                war.setDisplayName(node.getTextContent());
            }
            else if (nodeName.equals("distributable"))
            {
                war.setDistributable("yes");
            }
            else if (nodeName.equals("session-timeout"))
            {
                war.setSessionTimeout(node.getTextContent());
            }
            else if (nodeName.equals("description"))
            {
                war.setDescription(node.getTextContent());
            }
        }

        NodeList servletNodes = getElementsByTagName(root, "servlet");

        for (int i = 0; i < servletNodes.getLength(); i++)
        {
            ServletType servletType = objectFactory.createServletType();

            for (Node node = servletNodes.item(i).getFirstChild(); node != null; node = node.getNextSibling())
            {
                if (node.getNodeType() != Node.ELEMENT_NODE) continue;

                String nodeName = (node.getLocalName() == null)? node.getNodeName() : node.getLocalName();

                if (nodeName.equals("servlet-name"))
                {
                    servletType.setServletName(node.getTextContent());
                }
                else if (nodeName.equals("servlet-class"))
                {
                    servletType.setServletClass(node.getTextContent());
                }
                else if (nodeName.equals("init-param")) {
                    //TODO
                }
            }

            String servletName = servletType.getServletName();
            if (servletName == null || servletName.equals(""))
                continue;

            NodeList servletMappingNodes = getElementsByTagName(root, "servlet-mapping");

            List<String> urlPatterns = new ArrayList<String>();

            for (int j = 0; j < servletMappingNodes.getLength(); j++)
            {
                Element ele = ((Element) servletMappingNodes.item(j));

                if (ele.getElementsByTagName("servlet-name").getLength() != 1)
                    continue;

                // servlet-name of <servlet> and <servlet-mapping> should match each other.
                if (servletName.equals(ele.getElementsByTagName("servlet-name").item(0).getTextContent()))
                {
                    NodeList patternList = ele.getElementsByTagName("url-pattern");
                    for (int k = 0; k < patternList.getLength(); k++)
                    {
                        String url = patternList.item(k).getTextContent();
                        if (!urlPatterns.contains(url))
                            urlPatterns.add(url);
                    }
                    servletType.setUrlPattern(urlPatterns.toString());
                }
            }
            war.getServlets().getServlet().add(servletType);
        }
        if (war.getServlets().getServlet().size() == 0)
            war.setServlets(null);

        war.setResRefs(createResourceRefType(root));

        return war;
    }

    private RarDDType createRarDDType(File raDir) throws Exception {
        RarDDType rar = objectFactory.createRarDDType();
        rar.setName("ra.xml");

        File raXml = new File(raDir, "META-INF/ra.xml");
        if (!raXml.exists()) {
            return null;
        }

        Document xmlDoc = null;
        try {
            xmlDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(raXml);
        } catch (Exception e) {
            Logger.log("Failed to parse deployment descriptor file META-INF/ra.xml", "ERROR");
            throw e;
        }

        Element root = xmlDoc.getDocumentElement();
        NodeList resourceAdapterNodes = root.getElementsByTagName("resource-adapter");

        for (int i = 0; i < resourceAdapterNodes.getLength(); i++) {
            ResourceAdapterType raType = objectFactory.createResourceAdapterType();
            Element raNode = (Element) resourceAdapterNodes.item(i);
            NodeList raChildNodes = raNode.getChildNodes();
            for (int j = 0; j < raChildNodes.getLength(); j++) {
                Node childNode = raChildNodes.item(j);
                if (childNode.getNodeType() != Node.ELEMENT_NODE)
                    continue;
                String childName = childNode.getLocalName() == null ? childNode.getNodeName() : childNode.getLocalName();
                if ("config-property".equals(childName)) {
                    ConfigProperty configProp = objectFactory.createResourceAdapterTypeConfigPropertiesConfigProperty();
                    Element configPropEle = (Element) childNode;
                    String propName = configPropEle.getElementsByTagName("config-property-name").item(0).getTextContent();
                    String propType = configPropEle.getElementsByTagName("config-property-type").item(0).getTextContent();
                    String propValue = "";
                    if (configPropEle.getElementsByTagName("config-property-value").getLength() > 0) {
                        propValue = configPropEle.getElementsByTagName("config-property-value").item(0).getTextContent();
                    }
                    configProp.setConfigPropertyName(propName);
                    configProp.setConfigPropertyType(propType);
                    configProp.setConfigPropertyValue(propValue);
                    raType.getConfigProperties().getConfigProperty().add(configProp);
                }
            }

            NodeList conDefNodes = raNode.getElementsByTagName("connection-definition");
            for (int j = 0; j < conDefNodes.getLength(); j++) {
                Element conDefEle = (Element) conDefNodes.item(j);
                ConnectionDefinition conDef = objectFactory.createResourceAdapterTypeConnectionDefinitionsConnectionDefinition();
                conDef.setClassName(conDefEle.getAttribute("class-name"));
                conDef.setJndiName(conDefEle.getAttribute("jndi-name"));
                raType.getConnectionDefinitions().getConnectionDefinition().add(conDef);
            }

            rar.getResourceAdapters().getResourceAdapter().add(raType);
        }

        return rar;
    }

    private SarDDType createSarDDType(File sarDir) throws Exception {
        SarDDType sar = objectFactory.createSarDDType();
        sar.setName("jboss-service.xml");

        sar.setMbeans(objectFactory.createSarDDTypeMbeans());
        File sarXml = new File(sarDir, "META-INF/jboss-service.xml");
        if (!sarXml.exists()) {
            return null;
        }

        Document xmlDoc = null;
        try {
            xmlDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(sarXml);
        } catch (Exception e) {
            Logger.log("Failed to parse deployment descriptor file META-INF/ra.xml", "ERROR");
            throw e;
        }
        Element root = xmlDoc.getDocumentElement();
        NodeList mbeanNodes = root.getElementsByTagName("mbean");
        for (int i = 0; i < mbeanNodes.getLength(); i++) {
            Element mbeanEle = (Element) mbeanNodes.item(i);
            Mbean mbean = objectFactory.createSarDDTypeMbeansMbean();
            mbean.setName(mbeanEle.getAttribute("name"));
            mbean.setCode(mbeanEle.getAttribute("code"));

            // get attribute elements
            NodeList childNodes = mbeanEle.getChildNodes();
            for (int j = 0; j < childNodes.getLength(); j++) {
                if (childNodes.item(j).getNodeType() != Node.ELEMENT_NODE) continue;
                Element childEle = (Element) childNodes.item(j);
                Mbean.Attribute attribute = objectFactory.createSarDDTypeMbeansMbeanAttribute();
                attribute.setName(childEle.getAttribute("name"));
                attribute.setValue(childEle.getTextContent());
                mbean.getAttribute().add(attribute);
            }
            sar.getMbeans().getMbean().add(mbean);
        }

        return sar;
    }

    private EjbDDType createEjbDDType(File ejbDirectory) throws ParserConfigurationException {

        File ejbXml = new File(ejbDirectory, "META-INF/ejb-jar.xml");
        if (ejbXml.exists()) {
            EjbDDType ddType = objectFactory.createEjbDDType();
            ddType.setName("ejb-jar.xml");
            Document doc = SnapshotUtil.createDocumentFromFile(ejbXml);

            ddType.setSessionBeans(objectFactory.createEjbDDTypeSessionBeans());
            ddType.setMessageDrivenBeans(objectFactory.createEjbDDTypeMessageDrivenBeans());
            ddType.setEntityBeans(objectFactory.createEjbDDTypeEntityBeans());

            ddType.getSessionBeans().setName("List of Session Beans");
            ddType.getEntityBeans().setName("List of Entity Beans");
            ddType.getMessageDrivenBeans().setName("List of MessageDrivenBeans");

            ddType.getSessionBeans().getSessionBean().addAll(createSessionBeanType(doc));
            ddType.getMessageDrivenBeans().getMessageDrivenBean().addAll(createMessageDrivenBeanType(doc));
            ddType.getEntityBeans().getEntityBean().addAll(createEntityBeanType(doc));

            return ddType;
        }
        return null;
    }

    private List<SessionBeanType> createSessionBeanType(Document ejbJarXml) {

        List<SessionBeanType> sessionBeans = new ArrayList<SessionBeanType>();
        Element root = ejbJarXml.getDocumentElement();

        NodeList sessionNodes = root.getElementsByTagNameNS(root.getNamespaceURI(),"session");

        for (int i = 0; i < sessionNodes.getLength(); i ++)
        {
            Element e = (Element)sessionNodes.item(i);
            SessionBeanType sessionBean = objectFactory.createSessionBeanType();

            // Set common elements of a session bean (ejb-name, home ...)
            setObjectFields(e, sessionBean);

            sessionBean.setEjbRefs(createEjbResType(e));
            sessionBean.setResRefs(createResourceRefType(e));

            sessionBeans.add(sessionBean);
        }

        return sessionBeans;
    }

    private List<MessageDrivenBeanType> createMessageDrivenBeanType(Document ejbJarXml) {

        List<MessageDrivenBeanType> mdBeans = new ArrayList<MessageDrivenBeanType>();
        Element root = ejbJarXml.getDocumentElement();

        NodeList mdNodes = root.getElementsByTagNameNS(root.getNamespaceURI(),"message-driven");

        for (int i = 0; i < mdNodes.getLength(); i ++)
        {
            Element e = (Element)mdNodes.item(i);
            MessageDrivenBeanType mdType = objectFactory.createMessageDrivenBeanType();

            setObjectFields(e, mdType);

            ActivationConfigProperty[] acProp = setObjectsFields(
                    getElementsByTagName(e, "activation-config-property"), ActivationConfigProperty.class);

            if (acProp.length > 0)
                mdType.getActivationConfigProperty().addAll(Arrays.asList(acProp));

            MessageDrivenDestination[] mdProp = setObjectsFields(
                    getElementsByTagName(e, "message-driven-destination"), MessageDrivenDestination.class);

            if (mdProp.length > 0)
                mdType.setMessageDrivenDestination(mdProp[0]);

            mdBeans.add(mdType);
        }

        return mdBeans;

    }

    private List<EntityBeanType> createEntityBeanType(Document ejbJarXml) {

        List<EntityBeanType> entityBeans = new ArrayList<EntityBeanType>();
        Element root = ejbJarXml.getDocumentElement();

        NodeList sessionNodes = root.getElementsByTagNameNS(root.getNamespaceURI(),"entity");

        for (int i = 0; i < sessionNodes.getLength(); i ++)
        {
            Element e = (Element)sessionNodes.item(i);
            EntityBeanType entityBean = objectFactory.createEntityBeanType();

            setObjectFields(e, entityBean);

            entityBean.setEjbRefs(createEjbResType(e));
            entityBean.setResRefs(createResourceRefType(e));

            entityBeans.add(entityBean);
        }

        return entityBeans;
    }

    private EjbResType createEjbResType(Element e) {

        // Set ejb references (ejb-ref, ejb-local-ref)
        EjbResType ejbRes = objectFactory.createEjbResType();
        ejbRes.setName("All ejb references");

        EjbRef[] ejbRefs = setObjectsFields(
                e.getElementsByTagNameNS(e.getNamespaceURI(), "ejb-ref"), EjbRef.class);

        if (ejbRefs.length > 0)
        {
            ejbRes.setEjbRefs(objectFactory.createEjbResTypeEjbRefs());
            ejbRes.getEjbRefs().setName("Ejb references");
            ejbRes.getEjbRefs().getEjbRef().addAll(Arrays.asList(ejbRefs));
        }

        EjbLocalRef[] ejbLocalRefs = setObjectsFields(
                e.getElementsByTagNameNS(e.getNamespaceURI(), "ejb-local-ref"), EjbLocalRef.class);

        if (ejbLocalRefs.length > 0)
        {
            ejbRes.setEjbLocalRefs(objectFactory.createEjbResTypeEjbLocalRefs());
            ejbRes.getEjbLocalRefs().setName("Ejb local references");
            ejbRes.getEjbLocalRefs().getEjbLocalRef().addAll(Arrays.asList(ejbLocalRefs));
        }

        if (ejbLocalRefs.length > 0 || ejbRefs.length > 0)
            return ejbRes;

        return null;
    }

    private ResourceRefType createResourceRefType (Element e) {
        // Set resource references (resource-ref, resource-env-ref, env-entry)
        ResourceRefType reRes = objectFactory.createResourceRefType();
        reRes.setName("All resource references");

        ResourceRef[] resRefs = setObjectsFields(
                getElementsByTagName(e, "resource-ref"), ResourceRef.class);

        if (resRefs.length > 0)
        {
            reRes.setResourceRefs(objectFactory.createResourceRefTypeResourceRefs());
            reRes.getResourceRefs().setName("Resource references");
            reRes.getResourceRefs().getResourceRef().addAll(Arrays.asList(resRefs));
        }

        ResourceEnvRef[] resEnvRefs = setObjectsFields(
                getElementsByTagName(e, "resource-env-ref"), ResourceEnvRef.class);

        if (resEnvRefs.length > 0)
        {
            reRes.setResourceEnvRefs(objectFactory.createResourceRefTypeResourceEnvRefs());
            reRes.getResourceEnvRefs().setName("Resource environment references");
            reRes.getResourceEnvRefs().getResourceEnvRef().addAll(Arrays.asList(resEnvRefs));
        }

        EnvEntry[] envEntries = setObjectsFields(
                getElementsByTagName(e, "env-entry"), EnvEntry.class);

        if (envEntries.length > 0)
        {
            reRes.setEnvEntries(objectFactory.createResourceRefTypeEnvEntries());
            reRes.getEnvEntries().setName("Environment entries");
            reRes.getEnvEntries().getEnvEntry().addAll(Arrays.asList(envEntries));
        }

        if (envEntries.length > 0 || resEnvRefs.length > 0 || resRefs.length > 0)
            return reRes;

        return null;
    }

    private FileGroupType createFileGroupType(File directory, String groupName, String include, String exclude) throws IOException {
        FileGroupType fileGroup = objectFactory.createFileGroupType();
        fileGroup.setName(groupName);

        List<File> ejbFiles = FileUtil.listFiles(directory);

        int sourcePathIndex = directory.getCanonicalPath().length() + 1;

        for (File file : ejbFiles) {
            String name = "";
            try {
                name = file.getCanonicalPath().substring(sourcePathIndex);

            } catch(IndexOutOfBoundsException e) {
                continue;
            }

            if (include != null )
            {
                if (! name.toLowerCase().matches(include))
                    continue;
            }
            else if (exclude != null)
                if (name.toLowerCase().matches(exclude))
                    continue;

            FileType fileType = objectFactory.createFileType();
            try {
                fileType.setHash(FileUtil.calcHash(file, DEFAULT_HASH));
            } catch (NoSuchAlgorithmException e) {
                continue;
            }

            fileType.setName(name);
            fileType.setLastModified(
                    dateFormat.format(new Date(file.lastModified())));
            fileType.setSize((int) file.length());

            fileGroup.getFile().add(fileType);
        }

        if (fileGroup.getFile().size() > 0)
            return fileGroup;

        return null;
    }

    /** Copy application file(directory) to a temporary directory
     * @param applicationFile
     * @return
     * @throws Exception
     */
    private File createApplicationDirectory(File applicationFile) throws Exception {
        File appWorkingDir = FileUtil.createTempDirectory();

        if (applicationFile.isDirectory())
            SnapshotUtil.copyDirectory(applicationFile, appWorkingDir, excludeDirs, SnapshotUtil.FileCopyOption.PreserveDate);

        else
        {
            File appTempDir = new File(FileUtil.createTempDirectory(), applicationFile.getName());

            SnapshotUtil.copyFile(applicationFile, appTempDir, SnapshotUtil.FileCopyOption.PreserveDate);

            ZipUtil.unzipArchive(appTempDir, appWorkingDir);

            SnapshotUtil.cleanDirectory(appWorkingDir, excludeDirs);
            FileUtil.deleteDirectory(appTempDir.getParentFile());
        }

        return appWorkingDir;
    }

    /**Set all declared field's values in the given object to suitable values of XML elements
     */
    private void setObjectFields(Element parent, Object obj)  {
        NodeList children = parent.getChildNodes();
        String tagName;
        String tagTextContent;

        Class<?> clazz = obj.getClass();

        for (int i = 0; i < children.getLength(); i++)
        {
            Node child = children.item(i);
            if ( !(child instanceof Element)) continue;

            Element childElement = (Element) child;

            tagName = (childElement.getLocalName() == null) ? childElement.getNodeName() : childElement.getLocalName();
            tagTextContent = ((Text) childElement.getFirstChild()).getData().trim();

            for (Field field : clazz.getDeclaredFields())
            {
                String name = null;
                Annotation[] annotations = field.getDeclaredAnnotations();

                for (Annotation annotation : annotations)
                {
                    if (annotation instanceof XmlAttribute)
                    {
                        name = ((XmlAttribute) annotation).name();
                        if (name.equals("##default"))
                            name = field.getName();
                        break;
                    }
                    else if (annotation instanceof XmlElement)
                    {
                        name = ((XmlElement) annotation).name();
                        if (name.equals("##default"))
                            name = field.getName();
                        break;
                    }
                }

                if ((name != null) && name.equals(tagName))
                    try {
                        field.setAccessible(true);
                        field.set(obj, tagTextContent);

                    } catch (Exception e) {
                    }
            }
        }
    }

    /**Set all declared field's values in the given object to suitable values of JsonNode
     */
    private void setObjectFields(JsonNode node, Object obj)  {

        Iterator<String> iter = node.fieldNames();
        Class<?> clazz = obj.getClass();

        while (iter.hasNext())
        {
            String attribute = iter.next();
            String value = node.get(attribute).toString();

            for (Field field : clazz.getDeclaredFields())
            {
                String name = null;
                Annotation[] annotations = field.getDeclaredAnnotations();

                for (Annotation annotation : annotations)
                {
                    if (annotation instanceof XmlAttribute)
                    {
                        name = ((XmlAttribute) annotation).name();
                        if (name.equals("##default"))
                            name = field.getName();
                        break;
                    }
                    else if (annotation instanceof XmlElement)
                    {
                        name = ((XmlElement) annotation).name();
                        if (name.equals("##default"))
                            name = field.getName();
                        break;
                    }
                }

                if ((name != null) && name.equals(attribute))
                    try {
                        field.setAccessible(true);
                        field.set(obj, value.trim());

                    } catch (Exception e) {
                    }
            }
        }
    }

    /**Set suitable values of XML elements (NodeList) to all declared field's values of the given Class type.
     */
    private <C> C[] setObjectsFields(NodeList nl, Class<C> clazz) {

        @SuppressWarnings("unchecked")
        C[] obj = (C[]) Array.newInstance(clazz, nl.getLength());
        for (int i = 0; i < nl.getLength(); i++) {

            C c = null;
            try {
                c = clazz.newInstance();

            } catch (InstantiationException e) {

            } catch (IllegalAccessException e) {
            }

            setObjectFields((Element) nl.item(i), c);
            obj[i] = c;

        }
        return obj;
    }

    /** Use (Element) getElementsByTagName() but aware of namespaces.
     */
    private NodeList getElementsByTagName(Element e, String tagName) {
        NodeList nodes = e.getElementsByTagNameNS(e.getNamespaceURI(), tagName);
        if (nodes.getLength() == 0)
            nodes = e.getElementsByTagName(tagName);

        return nodes;
    }
}
