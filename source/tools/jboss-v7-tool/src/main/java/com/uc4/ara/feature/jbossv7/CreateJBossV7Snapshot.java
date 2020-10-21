package com.uc4.ara.feature.jbossv7;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.util.Date;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;

import com.uc4.ara.feature.AbstractInternalFeature;
import com.uc4.ara.feature.globalcodes.ErrorCodes;
import com.uc4.ara.feature.jbossv7.schemas.ApplicationType;
import com.uc4.ara.feature.jbossv7.schemas.JBossV7SnapshotType;
import com.uc4.ara.feature.jbossv7.schemas.ServerSnapshotType;
import com.uc4.ara.feature.utils.CmdLineParser;
import com.uc4.ara.feature.utils.Maxim;
import com.uc4.ara.util.Logger;

public class CreateJBossV7Snapshot extends AbstractInternalFeature  {

    private CmdLineParser.Option<String> home;
    private CmdLineParser.Option<String> host;
    private CmdLineParser.Option<Integer> port;
    private CmdLineParser.Option<String> username;
    private CmdLineParser.Option<String> password;
    private CmdLineParser.Option<String> snapshotMode;
    private CmdLineParser.Option<String> archivePath;
    private CmdLineParser.Option<String> guid;
    private CmdLineParser.Option<String> appName;
    private CmdLineParser.Option<String> hostController;
    private CmdLineParser.Option<String> serverInstance;

    private CmdLineParser.Option<String> appExcludeDirs;

    private String snapXmlFile = "snap.xml";

    @Override
    public void initialize() {
        super.initialize();
        StringBuilder examples = new StringBuilder();
        StringBuilder description = new StringBuilder();

        description.append("This function creates jboss server/application snapshot.");

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

        snapshotMode = parser.addHelp(
                parser.addStringOption("m", "mode", true),
                "Defines which parts should be included in the snapshot. Possible values [APPLICATION/SERVER/BOTH].");

        archivePath = parser.addHelp(
                parser.addStringOption("path", "archivePath", true),
                "The destination to store the snapshot.");

        guid = parser.addHelp(
                parser.addStringOption("g", "guid", true),
                "The GUID of the snapshot which will be created.");

        hostController = parser.addHelp(
                parser.addStringOption("hc", "hostController", false),
                "The host controller contains the server instance. Required if server instance is specified. Domain mode only. ");

        serverInstance = parser.addHelp(
                parser.addStringOption("s", "serverInstance", false),
                "Server instance where application is deployed to. Provide information about subsystem. Domain mode only. ");

        appName = parser.addHelp(
                parser.addStringOption("n", "name", false),
                "The name of the application to snapshot.");

        appExcludeDirs = parser.addHelp(
                parser.addStringOption("e", "exclude", false),
                "Defines which folders should be excluded from the application snapshot.");

        examples.append("java -jar ARATools ");

        parser.setExamples(examples.toString());
        parser.setDescription(description.toString());
    }

    @Override
    public int run(String[] args) throws Exception {

        int errorCode = ErrorCodes.OK;
        super.run(args);

        String homeValue = parser.getOptionValue(home);
        String hostValue = parser.getOptionValue(host);
        int portValue = parser.getOptionValue(port);
        String userValue = parser.getOptionValue(username);
        String passValue = Maxim.deMaxim(parser.getOptionValue(password));
        String modeValue = parser.getOptionValue(snapshotMode);
        String archiveValue = parser.getOptionValue(archivePath);
        String guidValue = parser.getOptionValue(guid);
        String appNameValue = parser.getOptionValue(appName);
        String hostControllerValue = parser.getOptionValue(hostController);
        String serverInstanceValue = parser.getOptionValue(serverInstance);

        String excludeDirsValue = parser.getOptionValue(appExcludeDirs);

        Object object = null;

        File jbossHome = new File(homeValue);
        if (! jbossHome.exists() )
        {
            Logger.log("JBoss home directory '" + jbossHome.getAbsolutePath() + "' does not exist. Aborting ...", "INFO");
            return ErrorCodes.ERROR;
        }
        else if (! new File(jbossHome, "bin/client/jboss-cli-client.jar").exists())
        {
            Logger.log("jboss-cli-client.jar does not exist in bin/client directory. " +
                    "The file is required for extracting information from JBoss. Aborting ...", "INFO");
            return ErrorCodes.ERROR;
        }

        

        // Directory used to contain snap.zip and snap.xml
        File snapDirectory = new File(archiveValue, guidValue);

        if (! snapDirectory.isDirectory() && ! snapDirectory.mkdirs() )
        {
            Logger.log("Can't create snapshot directory at '" + snapDirectory.getAbsolutePath() + "'. Aborting ...", "ERROR");
            return ErrorCodes.ERROR;
        }

        JBossV7SnapshotType jb = new JBossV7SnapshotType();

        jb.setType("JBossV7");
        jb.setGuid(guidValue);
        jb.setHashType("MD5");
        jb.setDate(DateFormat.getDateTimeInstance(
                DateFormat.SHORT, DateFormat.FULL).format(new Date()));

        if (modeValue.toUpperCase().equals("SERVER")
                || modeValue.toUpperCase().equals("BOTH")) {
            ServerSnapshot serverSnapshot = new ServerSnapshot(homeValue, hostValue, portValue, userValue, passValue);
            object = serverSnapshot.createSnapshot();
            jb.setServer((ServerSnapshotType) object);
        }
        if (modeValue.toUpperCase().equals("APPLICATION")
                || modeValue.toUpperCase().equals("BOTH")) {

            if (hostControllerValue == null || hostControllerValue.equals(""))
                hostControllerValue = "master";

            ApplicationSnapshot appSnapshot = new ApplicationSnapshot(homeValue, appNameValue, hostValue, portValue,
                    userValue, passValue, hostControllerValue, serverInstanceValue, snapDirectory.getAbsolutePath(),
                    excludeDirsValue);

            try {
                if (snapXmlFile.equals("snap.xml"))
                    object = appSnapshot.createSnapshot(true);
                else
                    object = appSnapshot.createSnapshot(false);
            } catch (Exception e) {
                Logger.log("Error when create application snapshot. Message: " + e.getMessage() + ". Aborting ...", "ERROR");
                return ErrorCodes.EXCEPTION;
            }

            jb.setApplication((ApplicationType) object);
        }

        if (object == null)
        {
            Logger.log("Unable to create snapshot of mode " + modeValue + ". Aborting ...", "ERROR");
            return ErrorCodes.ERROR;
        }

        // Convert Snapshot Object to snap.xml
        JAXBContext context = JAXBContext.newInstance(JBossV7SnapshotType.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        @SuppressWarnings({ "rawtypes", "unchecked" })
        JAXBElement jaxbElement = new JAXBElement(new QName("snapshot"), JBossV7SnapshotType.class, jb);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(new File(snapDirectory, snapXmlFile));
            marshaller.marshal(jaxbElement, fos);
        } catch (JAXBException e) {
            Logger.log("Unable to create snap.xml. Aborting ...", "ERROR");
            return ErrorCodes.EXCEPTION;
        } finally {
            fos.close();
        }

        Logger.log("Snap file '" + new File(snapDirectory, snapXmlFile).getAbsolutePath() + "' was created successfully.", "INFO");

        return errorCode;
    }

    /*
     * snap.xml, OverviewSnap.xml, CompareSnap.xml
     */
    public void setSnapXmlFile(String file){
        snapXmlFile = file;
    }

}
