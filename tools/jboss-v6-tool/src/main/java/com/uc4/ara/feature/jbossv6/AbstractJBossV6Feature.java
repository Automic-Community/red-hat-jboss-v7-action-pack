package com.uc4.ara.feature.jbossv6;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Iterator;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.jboss.as.cli.scriptsupport.CLI;
import org.jboss.as.cli.scriptsupport.CLI.Result;

import com.uc4.ara.feature.jbossv6.schemas.ObjectFactory;
import com.uc4.ara.util.Logger;

abstract class AbstractJBossV6Feature  {

    public String slash = File.separator;

    protected final String jbossHome;
    private final String hostname;
    private final int port;
    private final String username;
    private final String password;

    protected static final String DEFAULT_HASH = "MD5";
    protected static final DateFormat dateFormat = DateFormat
            .getDateTimeInstance(DateFormat.SHORT, DateFormat.FULL);

    protected ObjectFactory objectFactory;
    protected final CLI cli;

    public AbstractJBossV6Feature(String jbossHome, String hostname, int port,
            String username, String password) {

        this.jbossHome = jbossHome;
        this.hostname = hostname;
        this.port = port;
        this.username = username;
        this.password = password;

        objectFactory = new ObjectFactory();
        cli = CLI.newInstance();
    }

    /*
     * Initialize connection to JBoss Management CLI
     */
    protected void connect() throws JBossCLIException {
        try {
            cli.connect(hostname, port , username, password.toCharArray());
        } catch (Exception e) {
            throw new JBossCLIException("Can't connect to JBoss Management CLI. Message: " + e.getMessage());
        }

    }

    protected void disconnect() throws JBossCLIException  {
        try {
            cli.disconnect();
        } catch (Exception e) {
            throw new JBossCLIException("Can't disconnect from JBoss Management CLI. Message:" + e.getMessage());
        }
    }

    protected class JBossCLIException extends Exception {
        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        public JBossCLIException(String message) {
            super(message);
        }
    }

    protected File getApplicationFile(String appName) throws JBossCLIException, JsonProcessingException, IOException {

        Result result = null;
        ObjectMapper mapper = null;
        JsonNode resultNode = null;
        String jsonResult = null;
        String jbBaseDir = "jboss.server.base.dir";
        String appHash = null;
        File appFile = null;

        try {
            connect();

            if (cli.getCommandContext().isDomainMode())
            {
                result = cli.cmd(":read-attribute(name=local-host-name)");
                jsonResult = result.getResponse().get("result").toJSONString(true);
                String localHC = jsonResult.replaceAll("\"", "");

                if (localHC.equals("null"))
                    throw new JBossCLIException("Cannot get local host controller. Neccessary to find base directory of application.");

                cli.cmd("cd /host=" + localHC + "/core-service=platform-mbean/type=runtime");
                jbBaseDir = "jboss.domain.base.dir";
            }
            else
                cli.cmd("cd /core-service=platform-mbean/type=runtime");

            result = cli.cmd(":read-attribute(name=system-properties)");

            jsonResult = result.getResponse().get("result").toJSONString(true);
            if (jsonResult.equals("null"))
                throw new JBossCLIException("Cannot get system properties. Neccessary to find base directory of application.");

            mapper = new ObjectMapper();
            resultNode = mapper.readTree(jsonResult);
            Iterator<String> iter = resultNode.getFieldNames();

            while (iter.hasNext()) {
                String sysProp = iter.next();
                if (sysProp.equals(jbBaseDir))
                {
                    jbBaseDir = resultNode.get(sysProp).getTextValue();
                    break;
                }
            }

            if (jbBaseDir.matches("jboss.*.base.dir"))
                throw new JBossCLIException("Cannot get base directory " + jbBaseDir);

            result = cli.cmd("/deployment=" + appName + ":read-attribute(name=content)");
            jsonResult = result.getResponse().get("result").toJSONString(true);

            if (jsonResult.equals("null"))
                throw new JBossCLIException("Application does not exist.");

            mapper = new ObjectMapper();
            resultNode = mapper.readTree(jsonResult);
            resultNode = resultNode.findValue("BYTES_VALUE");

            // BYTES_VALUE is not available in case of deployment scanner.
            if (resultNode != null)
            {
                appHash = "";
                for (byte b : resultNode.getBinaryValue()) {
                    String s = Integer.toHexString(b & 0xff);
                    while (s.length() < 2)
                        s = "0" + s;
                    appHash += s;
                }
                if (appHash.length() > 2)
                    appFile = new File(jbBaseDir + slash + "data" + slash + "content" + slash
                            + appHash.substring(0, 2) + slash + appHash.substring(2) + slash + "content");

            }
            else if (! cli.getCommandContext().isDomainMode())
                appFile = new File(jbBaseDir + slash + "deployments" + slash + appName);

            if (appFile != null && appFile.exists())
            {
                Logger.log("Application directory: " + appFile.getAbsolutePath(), "INFO");
                return appFile;
            }
            else
                throw new JBossCLIException("Cannot get the application file in '" + appFile.getAbsolutePath() + "'.");

        } finally {
            disconnect();
        }
    }

}
