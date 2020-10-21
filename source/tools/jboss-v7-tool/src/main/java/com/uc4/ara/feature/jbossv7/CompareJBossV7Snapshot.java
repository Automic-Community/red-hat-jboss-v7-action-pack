package com.uc4.ara.feature.jbossv7;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.uc4.ara.feature.AbstractInternalFeature;
import com.uc4.ara.feature.FeatureUtil;
import com.uc4.ara.feature.globalcodes.ErrorCodes;
import com.uc4.ara.feature.utils.CmdLineParser;
import com.uc4.ara.feature.utils.FileUtil;
import com.uc4.ara.feature.utils.Maxim;
import com.uc4.ara.feature.utils.SnapshotUtil;
import com.uc4.ara.feature.utils.XmlUtil;
import com.uc4.ara.util.Logger;

public class CompareJBossV7Snapshot extends AbstractInternalFeature  {

    private final String slash = File.separator;

    private CmdLineParser.Option<String> home;
    private CmdLineParser.Option<String> host;
    private CmdLineParser.Option<Integer> port;
    private CmdLineParser.Option<String> username;
    private CmdLineParser.Option<String> password;
    private CmdLineParser.Option<String> archivePath;

    private CmdLineParser.Option<String> appExcludeDirs;
    private CmdLineParser.Option<String> destination;

    private CmdLineParser.Option<String> bondUrl;
    private CmdLineParser.Option<String> reportID;
    private CmdLineParser.Option<String> guid;

    private String reportIdValue;
    private String bondUrlValue;
    private String guidValue;
    private File snapDirectory;

    private int status = 0;
    @Override
    public void initialize() {
        super.initialize();
        StringBuilder examples = new StringBuilder();
        StringBuilder description = new StringBuilder();

        description.append("This function compare an existing jboss server/application snapshot with the current state. Should only be used from ARA.");

        home = parser.addHelp(
                parser.addStringOption("hm",  "home", true),
                "Home directory of JBoss. Ex: /root/jboss-eap-7.1/");

        host = parser.addHelp(
                parser.addStringOption("h", "host", false),
                "The IP or hostname of to CLI Management Interface. Default: localhost");

        port = parser.addHelp(
                parser.addIntegerOption("p", "port", false),
                "The port number used to connect to CLI Management Interface. Default: 9999");

        username = parser.addHelp(
                parser.addStringOption("u", "username", false),
                "Username used to authenticate with JBoss.");

        password = parser.addHelp(
                parser.addPasswordOption("pw", "password", false),
                "Password used to authenticate with JBoss.");

        reportID = parser.addHelp(
                parser.addStringOption("r",  "reportID", false),
                "Unique ID generated by ARA. Not required for Overview snapshot.");

        bondUrl = parser.addHelp(
                parser.addStringOption("b", "bondUrl", false),
                "ARA url. Ex: http://localhost/releasemanager. Not required for Overview snapshot.");

        archivePath = parser.addHelp(
                parser.addStringOption("a", "archivePath", true),
                "Directory where JBoss snapshots are stored. Ex:/root/snapshot{/guid/snap.xml}");

        guid = parser.addHelp(
                parser.addStringOption("g", "guid", true),
                "The GUID of the snapshot which will be created.");

        destination = parser.addHelp(
                parser.addStringOption("d", "destination", false),
                "Directory inside archivePath to store compare result. Format: yyyy-MM-dd. If set to NULL, using Overview Snapshot instead.");

        appExcludeDirs = parser.addHelp(
                parser.addStringOption("e", "exclude", false),
                "Defines which folders should be excluded from the application snapshot.");

        // add example text
        examples.append("java -jar ARATools ");

        parser.setExamples(examples.toString());
        parser.setDescription(description.toString());
    }

    @Override
    public int run(String[] args) throws Exception {

        super.run(args);

        String homeValue = parser.getOptionValue(home);
        String hostValue = parser.getOptionValue(host);
        int portValue = parser.getOptionValue(port);
        String userValue = parser.getOptionValue(username);
        String passValue = Maxim.deMaxim(parser.getOptionValue(password));

        String destinationValue = parser.getOptionValue(destination);
        String archiveValue = parser.getOptionValue(archivePath);
        String excludeDirsValue = parser.getOptionValue(appExcludeDirs);

        guidValue = parser.getOptionValue(guid);
        bondUrlValue = parser.getOptionValue(bondUrl);
        reportIdValue = parser.getOptionValue(reportID);

        try {
            snapDirectory = new File(archiveValue, guidValue);
            File snapXml = new File(snapDirectory, "snap.xml");

            if (! snapXml.exists())
            {
                Logger.log("Cannot find '" + snapXml.getAbsolutePath() + "'." +
                        " The file is neccessary for comparison. Aborting ...", "ERROR");
                return ErrorCodes.ERROR;
            }

            if (destinationValue != null && ! destinationValue.equals("NULL"))
            {
                try {
                    compareSnapshot(snapXml, homeValue, hostValue, portValue, userValue, passValue, archiveValue, excludeDirsValue);
                } catch (Exception e) {
                    Logger.log("Error while comparing snapshot. Message: " + e.getMessage() + ". Aborting ...", "ERROR");
                    return ErrorCodes.EXCEPTION;
                }

                File destinationDirectory = new File(snapDirectory, destinationValue);

                if (!destinationDirectory.exists())
                    destinationDirectory.mkdirs();

                //After generating CompareSnap.xml, move it to snapshotDirectory (normally date-time)
                FileUtil.moveFile(new File(snapDirectory, "CompareSnap.xml"), new File(destinationDirectory, "CompareSnap.xml"));

                Logger.log("CompareSnap.xml is stored in '" + destinationDirectory.getAbsolutePath() + ".", "INFO");
            }
            else
                overviewSnapshot(snapXml);

        } catch (Exception e) {
            Logger.log(e.getMessage(), "INFO");
            status = ErrorCodes.EXCEPTION;
        }
        if(status == 0)
            Logger.log("Result-Status: Consistent", "INFO");
        else if (status == 1)
            Logger.log("Result-Status: Inconsistent", "INFO");
        else
            Logger.log("Result-Status: Failed", "INFO");

        if (status <= 1)
            status = 0;

        return status;
    }

    private void overviewSnapshot(File snapXml) {

        Document doc = null;
        try {
            doc = SnapshotUtil.createDocumentFromFile(snapXml);
        } catch (ParserConfigurationException e) {
            Logger.log("Unable to parse '" + snapXml.getAbsolutePath() + ". Message:" + e.getMessage(), "ERROR");
            status = 2;
            return;
        }

        Element parent = doc.getDocumentElement();

        addStateToSnapXml(parent);

        String overviewReport;
        try {
            overviewReport = FeatureUtil.xmlToString(parent);
        } catch (Exception e) {
            Logger.log("Error when creating OverviewSnap.xml. Message:" + e.getMessage(), "ERROR");
            status = 2;
            return;
        }

        File overviewSnapXml = new File(snapXml.getParent() + slash + "OverviewSnap.xml");
        try {
            FeatureUtil.writeFile(overviewReport, overviewSnapXml);
        } catch (IOException e) {
            status = 2;
            return;
        }
    }

    /** Recursively add 'equal' state to OverviewSnap.xml
     */
    private void addStateToSnapXml(Element parent) {

        for (Node node = parent.getFirstChild(); node != null; node = node.getNextSibling())
        {
            if (node.getNodeType() == Node.ELEMENT_NODE)
            {
                Element e =  (Element) node;

                e.setAttribute("state", "equal");
                if(e.getNodeName() == "file")
                    e.removeAttribute("hash");

                addStateToSnapXml(e);
            }
        }
    }

    /** Generate CompareSnap.xml by comparing current state to snap.xml
     */
    private void compareSnapshot(File snapXml, String home, String host, int port,
            String username, String password, String archive, String exclude) throws Exception {

        String appName = null;
        String hostController = "";
        String serverInstance = "";

        Document doc = SnapshotUtil.createDocument(snapXml.getAbsolutePath());
        Element parent = doc.getDocumentElement();

        NodeList application = parent.getElementsByTagName("Application");
        NodeList serverNode = parent.getElementsByTagName("Server");

        String mode = serverNode.getLength() > 0 ? serverNode.item(0).getParentNode().isSameNode(parent) ? "SERVER" : "" : "";
        if (application.getLength() > 0 && application.item(0).getParentNode().isSameNode(parent)) {
            mode = "SERVER".equals(mode) ? "BOTH" : "APPLICATION";
        }

        if ("".equals(mode)) throw new Exception("Failed to detect snapshot type of " + snapXml.getAbsolutePath() + "Aborting..");

        if (application.getLength() > 0)
        {
            XPath xPath = XPathFactory.newInstance().newXPath();
            XPathExpression xPathExpression = xPath.compile("//Application/application-runtime/app-name/text()");
            appName = (String) xPathExpression.evaluate(doc, XPathConstants.STRING);

            try {
                xPathExpression = xPath.compile("//Application/application-runtime/host-controller/text()");
                hostController = (String) xPathExpression.evaluate(doc, XPathConstants.STRING);

            } catch(XPathExpressionException e) {
            }

            try {
                xPathExpression = xPath.compile("//Application/application-runtime/server-instance/text()");
                serverInstance = (String) xPathExpression.evaluate(doc, XPathConstants.STRING);

            } catch(XPathExpressionException e) {
            }

        }

        if (("both".equalsIgnoreCase(mode) || "application".equalsIgnoreCase(mode)) && StringUtils.isEmpty(appName))
            throw new Exception ("Failed to get application name.");

        // Rerun CreateJBossV7Snapshot (create CompareSnap.xml instead of snap.xml)
        CreateJBossV7Snapshot createSnapXml = new CreateJBossV7Snapshot();
        createSnapXml.setSnapXmlFile("CompareSnap.xml");

        String[] input = new String[]{"--home", home, "--host", host, "--port", Integer.toString(port),
                "--username", username, "--password", password, "--mode", mode, "--archivePath",
                archive, "--name", StringUtils.isEmpty(appName)?"":appName.trim(), "--exclude", exclude, "--guid", guidValue,
                        "--hostController", hostController, "--serverInstance", serverInstance};

        createSnapXml.initialize();
        try {
            createSnapXml.run(input);
        } catch (Exception e) {
            throw new Exception("Failed to recreate snapshot: " + e.getMessage());
        }

        // Compare CompareSnap.xml with snap.xml
        Document sDoc = SnapshotUtil.createDocument(snapXml.getAbsolutePath());
        Element sEle = sDoc.getDocumentElement();

        Document cDoc = SnapshotUtil.createDocument(snapXml.getParent() + slash + "CompareSnap.xml");
        Element cEle = cDoc.getDocumentElement();

        cEle.setAttribute("zipDate", sEle.getAttribute("date"));
        mapNodes(sEle, cEle);

        String report = FeatureUtil.xmlToString(cEle);

        File resultFile = new File(snapXml.getParent() + slash + "CompareSnap.xml");
        FeatureUtil.writeFile(report, resultFile);
    }

    /** Compare attributes or text content of a single node.
     * @param snapNode
     * @param compareNode
     * @return true if nodes are different
     */
    private boolean compareNodes(Node snapNode, Node compareNode) {

        String sNodeContent = XmlUtil.getFirstLevelTextContent(snapNode);
        String cNodeContent = XmlUtil.getFirstLevelTextContent(compareNode);

        if ( !sNodeContent.equals(cNodeContent))
        {
            Logger.log("Different: " + snapNode.getNodeName() + " ( " + sNodeContent + " >< " + cNodeContent + " )", "INFO");
            return true;
        }

        Set<String> compareSet = new HashSet<String>();
        NamedNodeMap cMap = compareNode.getAttributes();
        NamedNodeMap sMap = snapNode.getAttributes();

        for (int i = 0; i < cMap.getLength(); i++)
        {
            Node node = cMap.item(i);
            compareSet.add(node.getNodeName() + " :: " +node.getNodeValue());
        }

        for (int i = 0; i < sMap.getLength(); i++)
        {
            Node node = sMap.item(i);

            boolean missing = ! compareSet.remove(node.getNodeName() + " :: " + node.getNodeValue());
            if(missing && ! node.getNodeName().equals("lastModified"))
            {
                String diffStr = "Different: " + snapNode.getNodeName() + ":" + ((Element)snapNode).getAttribute("name") + " (";
                for (String attr : compareSet)
                    if (attr.contains(node.getNodeName() + " :: "))
                        diffStr += attr + " >< " + node.getNodeValue() + " ";
                Logger.log(diffStr + " )", "INFO");

                return true;
            }
        }

        if( compareSet.size() > 0 )
        {
            for (String attr : compareSet)
                if (attr.contains("lastModified :: ")) {
                    compareSet.remove(attr);
                    break;
                }

            if (compareSet.size() > 0) {
                Logger.log("Different: " + ( (Element)snapNode ).getAttribute("name") + compareSet.toString(), "INFO");
                return true;
            }
        }

        return false;

    }

    /**Set the same state to the parent node of the current node
     * @param node
     */
    private void updateNodeState(Node node) {

        Node parent = node.getParentNode();

        if (parent!=null && !((Element)parent).getNodeName().equals("snapshot"))
        {
            ((Element)parent).setAttribute("state", "different");
            updateNodeState( parent);
        }
    }

    /** Unique identification of an element
     */
    private String getId (Element ele) {

        String name = ele.getNodeName();
        String[] attrName = new String[]{ "name", "res-ref-name", "ejb-name",
                "ejb-ref-name", "resource-env-ref-name", "env-entry-name", "config-property-name",
                "class-name", "jndi-name", "servlet-name"};

        for (String attribute : attrName)

            if ( ! "".equals(ele.getAttribute(attribute)))
            {
                return name + " :: " + ele.getAttribute(attribute);
            }

        return name + " :: ";
    }

    /** Compare 2 elements. Add attribute "state = equal(different, missing, added)"
     * 	to each node depends on the result
     * @param snapXml
     * @param compareXml
     */
    private void mapNodes(Element snapXml, Element compareXml){

        //XmlUtil.removeElementXML(snapXml, Node.TEXT_NODE, null);
        Set<String> compareSet = new HashSet<String>();
        NodeList cChildNodes = compareXml.getChildNodes();
        NodeList sChildNodes = snapXml.getChildNodes();

        if(cChildNodes.getLength() == 0  && sChildNodes.getLength() == 0)
            return;

        for (Node cNode = compareXml.getFirstChild(); cNode != null; cNode = cNode.getNextSibling())
        {
            if (cNode.getNodeType() != Node.ELEMENT_NODE) continue;
            Element ele = (Element) cNode;

            compareSet.add(getId(ele));
        }

        // *************** Check for missing node ***************
        for (Node sNode = snapXml.getFirstChild(); sNode != null; sNode = sNode.getNextSibling())
        {
            if (sNode.getNodeType() != Node.ELEMENT_NODE) continue;
            Element ele = (Element) sNode;

            boolean missing = !(compareSet.remove(getId(ele)));

            if (missing)
            {
                status = 1;
                Element missingNode = (Element) compareXml.getOwnerDocument().importNode(ele, false);
                Logger.log("Missing: " + getId(ele), "INFO");

                missingNode.setAttribute("state", "missing");
                compareXml.appendChild(missingNode);
                updateNodeState(missingNode);
            }
        }

        // *************** Check for added node ***************
        for (Node cNode = compareXml.getFirstChild(); cNode != null; cNode = cNode.getNextSibling())
        {
            if (cNode.getNodeType() != Node.ELEMENT_NODE) continue;
            Element cEle = (Element) cNode;

            for(String addedNode : compareSet)
            {
                if (addedNode.equals(getId(cEle)))
                {
                    status = 1;
                    cEle.setAttribute("state", "added");
                    updateNodeState(cEle);
                    Logger.log("Added: " + getId(cEle), "INFO");
                }
            }

            // *************** Check for different node ***************
            if(  cEle.getAttribute("state").equals("") )
                for (Node sNode = snapXml.getFirstChild(); sNode != null; sNode = sNode.getNextSibling())
                {
                    if (sNode.getNodeType() != Node.ELEMENT_NODE) continue;
                    Element sEle = (Element) sNode;

                    if( getId(sEle).equals(getId(cEle)) )
                    {
                        boolean different = compareNodes(sEle, cEle);

                        if(different) {
                            status = 1;
                            cEle.setAttribute("state", "different");
                            updateNodeState(cEle);
                        }
                        else
                            cEle.setAttribute("state", "equal");

                        if (cEle.getNodeName().equals("file"))
                        {
                            cEle.setAttribute("lastModifiedArchived", sEle.getAttribute("lastModified"));
                            cEle.removeAttribute("hash");

                            if (cEle.getAttribute("state").equals("different"))
                            {
                                cEle.setAttribute("sizeArchived", sEle.getAttribute("size"));
                                cEle.setAttribute("guid", guidValue);
                                cEle.setAttribute("url", bondUrlValue);
                                cEle.setAttribute("reportId", reportIdValue);

                                try {
                                    setBinaryAttribute(cEle);
                                } catch (IOException e) {
                                    return;
                                }
                            }
                        }

                        mapNodes(sEle, cEle);
                        break;
                    }
                }
        }
    }

    /**Check if the file (specified by ele) is a binary file or not. Set the "isBinary" attribute accordingly
     * @param ele
     * 			<file name="" location="" module=""></file>
     * @throws IOException
     */
    private void setBinaryAttribute(Element ele) throws IOException {
        ele.setAttribute("isBinary", "no");
        ZipInputStream zis = null;
        try{
            zis = new ZipInputStream(
                    new FileInputStream(new File(snapDirectory + "/" + guidValue, "snap.zip")));

            ZipEntry entry = null;

            String filePath = ele.getAttribute("name");
            filePath = FileUtil.normalize(filePath.replace("\\", "/"));

            while ((entry = zis.getNextEntry()) != null)
            {
                String entryPath = FileUtil.normalize(entry.getName().replace("\\", "/"));
                if (entryPath.equals(filePath))
                    break;
            }

            byte buf[] = new byte[100];
            int read = zis.read(buf, 0, buf.length);

            for (int k = 0; k < read; k++)
                if (buf[k] == 0) {
                    ele.setAttribute("isBinary", "yes");
                    break;
                }

        } finally {
            if(zis != null) zis.close();
        }
    }

}
