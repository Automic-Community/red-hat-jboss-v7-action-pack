/**---------------------------------------
 *  DetailedCompareWeblogicSnapshot.java
 *
 *  Author: Roger Talkov
 *
 * Copyright (C) 2012 UC4 Software, Inc.  All Rights Reserved.
 *
 * Last check in $DateTime$
 * by $Author$
 * $Change$
 */
package com.uc4.ara.feature.jbossv7;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.uc4.ara.feature.AbstractInternalFeature;
import com.uc4.ara.feature.FeatureUtil;
import com.uc4.ara.feature.globalcodes.ErrorCodes;
import com.uc4.ara.feature.utils.ClasspathHacker;
import com.uc4.ara.feature.utils.CmdLineParser;
import com.uc4.ara.feature.utils.FileUtil;
import com.uc4.ara.feature.utils.Maxim;
import com.uc4.ara.feature.utils.SnapshotUtil;
import com.uc4.ara.feature.utils.ZipUtil;
import com.uc4.ara.util.Logger;

import difflib.DiffRow;
import difflib.DiffRowGenerator;
import difflib.DiffUtils;
import difflib.Patch;

/**
 * Diff the file with the snapshot
 *
 * @author Roger Talkov
 * @version $Change$
 */

public class DetailedCompareJBossV7Snapshot extends  AbstractInternalFeature {

    private CmdLineParser.Option<String> home;
    private CmdLineParser.Option<String> host;
    private CmdLineParser.Option<Integer> port;
    private CmdLineParser.Option<String> username;
    private CmdLineParser.Option<String> password;

    private CmdLineParser.Option<String> comparedFile;
    private CmdLineParser.Option<String> resultFile;
    private CmdLineParser.Option<String> archivePath;

    private String zipDate;
    private final DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.FULL);

    @Override
    public void initialize() {
        super.initialize();
        StringBuilder examples = new StringBuilder();
        StringBuilder description = new StringBuilder();

        description.append("This function creates jboss server/application snapshot.");

        home = parser.addHelp(
                parser.addStringOption("hm",  "home", true),
                "Home directory of JBoss. Ex: /root/jboss-eap-6.1/");

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

        archivePath = parser.addHelp(
                parser.addStringOption("path", "archivePath", true),
                "The destination to store the snapshot.");

        comparedFile = parser.addHelp(
                parser.addStringOption("cf", "comparedFile", true),
                "Relative path to the file to be compared.");

        resultFile = parser.addHelp(
                parser.addStringOption("rf", "resultFile", true),
                "File contains the differences.");

        examples.append("java -jar ARATools ");

        parser.setExamples(examples.toString());
        parser.setDescription(description.toString());
    }

    @Override
    public int run(String[] args) throws Exception {

        int errorCode = ErrorCodes.OK;
        super.run(args);

        // handle parameters
        String homeValue = parser.getOptionValue(home);
        String hostValue = parser.getOptionValue(host);
        int portValue = parser.getOptionValue(port);
        String userValue = parser.getOptionValue(username);
        String passValue = Maxim.deMaxim(parser.getOptionValue(password));

        String archiveValue = parser.getOptionValue(archivePath);

        String fileValue = parser.getOptionValue(comparedFile);
        String resultFileValue = parser.getOptionValue(resultFile);

        String appName = null;

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

        ClasspathHacker.addFile(new File(homeValue , "bin/client/jboss-cli-client.jar"));

        File snapDirectory = new File(archiveValue);
        if ((! new File(snapDirectory, "snap.xml").exists())
                || (! new File(snapDirectory, "snap.zip").exists()))
        {
            Logger.log("Cannot  find snap.zip or snap.xml in '" + snapDirectory.getAbsolutePath() + "'. Aborting ...", "INFO");
            return ErrorCodes.ERROR;
        }

        Document doc = FeatureUtil.createDocument(new File(snapDirectory, "snap.xml"));
        Element parent = doc.getDocumentElement();
        NodeList application = parent.getElementsByTagName("Application");

        if (application.getLength() == 1)
        {
            XPath xPath = XPathFactory.newInstance().newXPath();
            XPathExpression xPathExpression = xPath.compile("//Application/application-runtime/app-name/text()");
            appName = (String) xPathExpression.evaluate(doc, XPathConstants.STRING);
        }

        if (appName == null || appName.equals(""))
            throw new Exception ("Failed to get application name. Aborting ...");

        zipDate = parent.getAttribute("date");

        AbstractJBossV7Feature a = new AbstractJBossV7Feature(homeValue, hostValue, portValue, userValue, passValue) {};

        File appFile = a.getApplicationFile(appName);

        File workingDir = createApplicationDirectory(appFile);

        File file = new File(workingDir, fileValue);
        fileValue = fileValue.replaceAll("\\\\", "/");

        // In case the compared file is inside a sub deployment of ear file
        String[] subDirs = fileValue.split("/");
        if (subDirs.length > 1)
        {
            File tmp = new File(workingDir, subDirs[0]);
            if (tmp.isFile())
            {
                File dir = new File(workingDir, subDirs[0] + "-tmp");
                if (dir.exists())
                    FileUtil.deleteDirectory(dir);

                ZipUtil.unzipArchive(tmp, dir);
                file = dir;
                for (int i = 1; i < subDirs.length; i++)
                    file = new File(file, subDirs[i]);

            }
        }

        if (!file.exists())
            throw new FileNotFoundException(
                    new File(workingDir, fileValue).getCanonicalPath());

        ZipInputStream is = new ZipInputStream(new FileInputStream(new File(
                snapDirectory, "snap.zip")));

        ZipEntry entry = null;
        String name = null;
        while ((entry = is.getNextEntry()) != null) {
            name = entry.getName().replaceAll("\\\\", "/");
            if (!name.equals(fileValue)) {
                continue;
            }

            break;
        }
        if (entry == null) {
            is.close();
            throw new FileNotFoundException("zip://" + fileValue); // not in zip
        }

        File unzipDir = new File(snapDirectory, "diffs");
        File unzipFile = new File(unzipDir, fileValue);
        File diffFile = new File(unzipDir, resultFileValue);

        unzipFile.getParentFile().mkdirs();
        FileOutputStream out = new FileOutputStream(unzipFile);

        byte b[] = new byte[8192];
        while (is.available() > 0) {
            int ret = is.read(b);
            if (ret == -1)
                break;
            out.write(b, 0, ret);
            out.flush();
        }
        out.close();
        is.close();

        diff(unzipFile, file, diffFile); // generate the diffs

        FileUtil.deleteDirectory(workingDir);

        Logger.log("Result-File: " + diffFile.getAbsolutePath(), "INFO");

        return errorCode;
    }

    /** Copy application file(directory) to a temporary directory
     * @param applicationFile
     * @return
     * @throws Exception
     */
    private File createApplicationDirectory(File applicationFile) throws Exception {
        File appWorkingDir = FileUtil.createTempDirectory();

        if (applicationFile.isDirectory())
            SnapshotUtil.copyDirectory(applicationFile, appWorkingDir, null, SnapshotUtil.FileCopyOption.PreserveDate);

        else
        {
            File appTempDir = new File(FileUtil.createTempDirectory(), applicationFile.getName());

            SnapshotUtil.copyFile(applicationFile, appTempDir, SnapshotUtil.FileCopyOption.PreserveDate);

            ZipUtil.unzipArchive(appTempDir, appWorkingDir);

            FileUtil.deleteDirectory(appTempDir.getParentFile());
        }

        return appWorkingDir;
    }

    public Document diff(File archiveFile, File currentFile, File diffFile)
            throws Exception {

        Logger.log("Archive: " + archiveFile.getAbsolutePath(), "INFO");

        List<String> archiveLines = getLines(archiveFile);
        List<String> currentLines = getLines(currentFile);

        DiffRowGenerator.Builder builder = new DiffRowGenerator.Builder();

        boolean sideBySideBool = true;
        builder.showInlineDiffs(!sideBySideBool);
        builder.columnWidth(120);
        DiffRowGenerator sideBySide = builder.build();

        Patch patch = DiffUtils.diff(archiveLines, currentLines);
        List<DiffRow> sideBySideRows = sideBySide.generateDiffRows(
                archiveLines, currentLines, patch);

        Document doc = FeatureUtil.createDocument("<diffs/>");
        Element root = doc.getDocumentElement();

        root.setAttribute("file", currentFile.getCanonicalPath());
        root.setAttribute("zipDate", zipDate);
        root.setAttribute("lastModified",
                dateFormat.format(new Date(currentFile.lastModified())));

        for (DiffRow row : sideBySideRows)
        {
            Element lineNode = doc.createElement("line");

            String oldLine = row.getOldLine();
            String newLine = row.getNewLine();

            newLine = newLine.replace("	", "&middot;");
            newLine = newLine.replace(" ", "&middot;");

            oldLine = oldLine.replace("	", "&middot;");
            oldLine = oldLine.replace(" ", "&middot;");

            lineNode.setAttribute("archiveLine", oldLine);
            lineNode.setAttribute("currentLine", newLine);

            if (row.getTag() == DiffRow.Tag.EQUAL) {
                lineNode.setAttribute("state", "=");
            } else if (row.getTag() == DiffRow.Tag.CHANGE) {
                if (row.getOldLine().trim().equals(""))
                    lineNode.setAttribute("state", "+");
                else
                    lineNode.setAttribute("state", "!");

            } else if (row.getTag() == DiffRow.Tag.INSERT) {
                lineNode.setAttribute("state", "+");
            } else if (row.getTag() == DiffRow.Tag.DELETE) {
                lineNode.setAttribute("state", "-");
            }

            root.appendChild(lineNode);
        }

        FeatureUtil.writeFile(FeatureUtil.xmlToString(doc), diffFile);

        return doc;
    }

    /**
     * get the lines of a file
     *
     * @param f
     *            the file
     * @return A List of the lines
     */
    private List<String> getLines(File f) throws IOException {
        List<String> lines = new ArrayList<String>();

        RandomAccessFile raf = new RandomAccessFile(f, "r");
        System.out.println("Reading: " + f.getAbsolutePath());
        System.out.println("Readable: " + f.canRead());

        String line;
        try
        {
            while ((line = raf.readLine()) != null) {
                lines.add(line);
            }
        }
        finally {
            raf.close();
        }
        return lines;
    }
}
