package com.automic.actions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.automic.constants.Constants;
import com.automic.exception.AutomicException;
import com.automic.util.CommonUtil;
import com.automic.util.ConsoleWriter;
import com.automic.util.SSHConnectionProperties;
import com.automic.util.SSHTaskTimeoutException;
import com.automic.validator.SSHValidator;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.KeyPair;
import com.jcraft.jsch.Session;

/**
 * This class defines the execution of any action.It provides some initializations and validations on common inputs .The
 * child actions will implement its executeSpecific() method as per their own need.
 */
public abstract class AbstractSSHAction extends AbstractAction {

    protected String hostname;

    protected String username;

    protected String password;

    protected Integer connectionTimeout;

    protected Integer port;

    protected String privateKey;

    protected String privateKeyPwd;

    protected boolean strictHostKeyCheck;

    protected Integer timeout;
    
    protected String sudo;
    
    protected String sudoPassword;
    
    protected boolean tty = false;

    public AbstractSSHAction() {
        addOption(Constants.HOST, true, "SSH hostname");
        addOption(Constants.PORT, true, "SSH port");
        addOption(Constants.USERNAME, true, "Username for Login into SSH host");
        addOption(Constants.CONN_TIMEOUT, false, "Connection Timeout(seconds)");
        addOption(Constants.PRIVATE_KEY, false, "SSH Private Key");
        addOption(Constants.STRICT_HOST_KEY_CHECKING, false, "Strict Host Key Checking");
        addOption("sshtimeout", false, "Timeout(seconds)");
        addOption("sudo", false, "Running using sodoers");
        addOption("tty", false, "Force pseudo-tty allocation for ssh");
    }

    /**
     * This method initializes the arguments and calls the execute method.
     *
     * @throws AutomicException
     *             exception while executing an action
     */
    public final void execute() throws AutomicException {
        prepareCommonInputs();

        executeSpecific();

    }

    private void prepareCommonInputs() throws AutomicException {

        this.hostname = getOptionValue("sshhost");
        SSHValidator.checkNotEmpty(this.hostname, "SSH host");

        this.port = CommonUtil.parseStringValue(getOptionValue("sshport"), 22);
        SSHValidator.lessThan(this.port, 0, "SSH port");

        this.username = getOptionValue("username");
        SSHValidator.checkNotEmpty(username, "Username for Login into SSH host");

        this.privateKey = getOptionValue("sshkey");

        this.password = System.getenv(Constants.ENV_PASSWORD);
        
        this.sudo = getOptionValue("sudo");
        
        this.sudoPassword = System.getenv(Constants.ENV_SUDO_PASSPHRASE);

        boolean privateKeyProvided = CommonUtil.checkNotEmpty(this.privateKey);
        boolean passwordProvided = CommonUtil.checkNotEmpty(this.password);

        if (!(privateKeyProvided ^ passwordProvided)) {
            throw new AutomicException("Please provide either private key or password");
        }

        this.connectionTimeout = CommonUtil.parseStringValue(getOptionValue("conntimeout"), 30);

        this.strictHostKeyCheck = CommonUtil.convert2Bool(getOptionValue("hostkeycheck"));

        if (privateKeyProvided) {
            SSHValidator.checkNotEmpty(this.privateKey, "SSH Private Key");
            File temp = new File(this.privateKey);
            SSHValidator.checkFileExists(temp);
            this.privateKeyPwd = System.getenv(Constants.ENV_PASSPHRASE);
        }

        timeout = CommonUtil.parseStringValue(getOptionValue("sshtimeout"), 0);
        this.tty = CommonUtil.convert2Bool(getOptionValue("tty"));
    }

    protected SSHConnectionProperties createConnectionProperties() {
        if (CommonUtil.checkNotEmpty(password)) {
            return new SSHConnectionProperties(username, hostname, port, password, connectionTimeout);
        } else {
            return new SSHConnectionProperties(username, hostname, port, privateKey,
                    privateKeyPwd == null ? null : privateKeyPwd, connectionTimeout);
        }
    }

    /**
     * excecutes remote command through SSH2
     * 
     * @param host
     *            host to connect to
     * @param username
     *            credentials
     * @param password
     *            credentials
     * @param command
     *            command to execute
     * @param stdout
     *            StringBuilder to hold the stdout of executed command. If null, stdout is ignored
     * @param stderr
     *            StringBuilder to hold the stderr of executed command. If null, stderr is ignored
     * @return resultcode of executed command if available, null if exit code could not be evaluated.
     * @throws ExecutionException
     * @throws TimeoutException
     * @throws JSchException
     * @throws java.io.IOException
     *             if exception occures during connection or execution.
     */

    public int executeSSHCommand(final SSHConnectionProperties connectionProps, final String command,
            final StringBuilder stdout, final StringBuilder stderr, final int size, final String stdOutFilePath,
            final String stdErrFilePath) throws AutomicException {
        Session session = null;
        int ret = 0;
        try {
            session = createSession(connectionProps);
            ret = executeCommand(command, stdout, stderr, size, stdOutFilePath, stdErrFilePath, session,tty,timeout);
        } catch (final JSchException e) {
        	ConsoleWriter.writeln(e);
            ConsoleWriter.writeln("Can not create the connection, please check connection propertes.");
            throw new AutomicException("Command Execution Failed.");
        } catch (final IOException e) {
        	ConsoleWriter.writeln(e);
            throw new AutomicException(e.getMessage());
        } catch (final TimeoutException e) {
        	ConsoleWriter.writeln(e);
            throw new SSHTaskTimeoutException("Timeout: Process has breached the timeout specified");
        } finally {
            if (session != null) {
                session.disconnect();
            }
        }
        return ret;
    }

    private int executeCommand(final String command, final StringBuilder stdout, final StringBuilder stderr,
            final int size, final String stdOutFilePath, final String stdErrFilePath, final Session session,
            final boolean tty, final int timeOut) throws JSchException, IOException, TimeoutException, AutomicException {
        int result;

        result = runSSHCommandInExecChannel(command, stdout, stderr, size, stdOutFilePath, stdErrFilePath, session,
                tty, timeOut);

        return result;
    }

    private OutputStream getFileOutputStream(final String path) throws IOException {
        if (path != null && !path.trim().isEmpty()) {
            return new FileOutputStream(path);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public int runSSHCommandInExecChannel(final String command, final StringBuilder stdout, final StringBuilder stderr,
            final int size, final String stdOutFilePath, final String stdErrFilePath, final Session session,
            final boolean usePty,final int timeOut) throws AutomicException, JSchException, IOException, TimeoutException {
        int result;
        ChannelExec channelExec = null;
        final ExecutorService executorOut = Executors.newSingleThreadExecutor();
        final ExecutorService executorErr = Executors.newSingleThreadExecutor();

        try {
            channelExec = (ChannelExec) session.openChannel("exec");
            if (CommonUtil.checkNotEmpty(sudo)) {
                channelExec.setCommand("sudo -S -p '' " + command);
            } else {
                channelExec.setCommand(command);
            }
            channelExec.setPty(usePty);
            final InputStream inputStream = channelExec.getInputStream();
            final OutputStream outputStream = channelExec.getOutputStream();
            final InputStream errorStream = channelExec.getErrStream();
            channelExec.connect(connectionTimeout * 1000);
            
            if (CommonUtil.checkNotEmpty(sudoPassword)) {
                outputStream.write((sudoPassword + "\n").getBytes());
                outputStream.flush();
            }
            
            
            final Future<String> futureOut = executorOut
                    .submit(new SSHStreamHandler(channelExec, inputStream, stdout, size, stdOutFilePath));
            final Future<String> futureErr = executorErr
                    .submit(new SSHStreamHandler(channelExec, errorStream, stderr, size, stdErrFilePath));

            try {
                if (timeOut <= 0) {
                    futureOut.get();
                    futureErr.get();
                } else {
                    futureOut.get(timeOut, TimeUnit.SECONDS);
                    futureErr.get(timeOut, TimeUnit.SECONDS);
                }

            } catch (final InterruptedException e) {
            	ConsoleWriter.writeln(e);
                ConsoleWriter.writeln("Cannot execute command");
                throw new AutomicException("Execution of SSH command was interrupted.");
            } catch (final ExecutionException e) {
            	ConsoleWriter.writeln(e);
                ConsoleWriter.writeln("Cannot execute command");
                throw new AutomicException("Cannot read sdtout and stderr of the executed command.");
            }

            result = channelExec.getExitStatus();
            return result;
        } finally {
            if (channelExec != null) {
                channelExec.disconnect();
                executorErr.shutdownNow();
                executorOut.shutdownNow();
            }
        }
    }

    public Session createSession(final SSHConnectionProperties connectionProps)
            throws JSchException, AutomicException {

        final Properties config = new Properties();
        if (!strictHostKeyCheck) {
            config.put("StrictHostKeyChecking", "no");
        }
        config.put("PreferredAuthentications", "publickey,password");
        JSch.setConfig(config);

        final JSch jsch = new JSch();

        final Session session = jsch.getSession(connectionProps.getUsername(), connectionProps.getHost(),
                connectionProps.getPort());

        if (connectionProps.getPassword() != null) {
            session.setPassword(connectionProps.getPassword());
        } else if (connectionProps.getKeystoreFile() != null) {
            if (!new File(connectionProps.getKeystoreFile()).exists()) {
                throw new AutomicException(
                        "Given keystore [" + connectionProps.getKeystoreFile() + "] does not exist.");
            }
            final KeyPair kpair = KeyPair.load(jsch, connectionProps.getKeystoreFile());
            if (kpair.isEncrypted()) {
                if (connectionProps.getKeystorePass() == null) {
                    throw new AutomicException("No password supplied for encrypted keystore.");
                }
                jsch.addIdentity(connectionProps.getKeystoreFile(), connectionProps.getKeystorePass());
            } else {
                jsch.addIdentity(connectionProps.getKeystoreFile(), connectionProps.getKeystorePass());
            }
        } else {
            throw new AutomicException("No password and no keystore is given for SSH connection");
        }
        session.setTimeout(connectionProps.getTimeOut() * 1000 /* convert from seconds to milli */);
        session.connect();

        ConsoleWriter.writeln("Successfully created SSH session to " + connectionProps.getHost() + " with username "
                + connectionProps.getUsername() + ".");
        return session;

    }

    private class SSHStreamHandler implements Callable {
        Channel channel;
        InputStream in;
        StringBuilder outBuffer;
        int size;
        String outPath;

        private SSHStreamHandler(final Channel channel, final InputStream in, final StringBuilder outBuffer,
                final int size, final String outPath) {
            this.channel = channel;
            this.in = in;
            this.outBuffer = outBuffer;
            this.size = size;
            this.outPath = outPath;
        }

        @Override
        public String call() throws AutomicException {
            OutputStream os = null;

            try {
                os = getFileOutputStream(outPath);
                if (in != null && outBuffer != null) {
                    do {
                        final byte[] tmp = new byte[1024];
                        while (in.available() > 0) {
                            final int i = in.read(tmp, 0, 1024);
                            if (i < 0) {
                                break;
                            }
                            if (os != null) {
                                os.write(tmp, 0, i);
                                os.flush();
                            }
                            final int remainingBytes = size - outBuffer.length();
                            if (outBuffer.length() < size) {
                                outBuffer.append(new String(tmp, 0, i < remainingBytes ? i : remainingBytes));
                            }
                        }
                        if (channel.isClosed()) {

                            break;
                        }
                        try {
                            Thread.sleep(100);
                        } catch (final InterruptedException e) {
                            break;
                        }
                    } while (true);
                }
            } catch (final IOException e) {
            	ConsoleWriter.writeln(e);
                throw new AutomicException("Error occured while running command on remote ssh server");

            } finally {
                if (os != null) {
                    try {
                        os.close();
                    } catch (final IOException e) {
                    	ConsoleWriter.writeln(e);
                    }
                }
            }
            return null;
        }

    }

    /**
     * Method to execute the action.
     *
     * @throws AutomicException
     */
    protected abstract void executeSpecific() throws AutomicException;

}