package com.automic.actions;

import java.io.File;
import java.io.IOException;
import java.util.Vector;
import java.util.concurrent.TimeoutException;

import com.automic.exception.AutomicException;
import com.automic.util.ConsoleWriter;
import com.automic.util.SSHConnectionProperties;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

public class PutFileAction extends RunCommandAction {
	private Session session;
	private ChannelSftp sftpChannel;
	private String tempDir;
	private String destPath;
	private String command;

	public PutFileAction() {
		this.addOption("sourcepath", true, "Complete path of source file");
		this.addOption("tempdir", true, "Run id of process");
		this.addOption("command", true, "Command to run files");
	}

	protected void executeSpecific() throws AutomicException {

		String outputFile = getOptionValue("outfile");

		String errorFile = getOptionValue("errfile");

		String srcPath = getOptionValue("sourcepath");
		command = getOptionValue("command");
		tempDir = getOptionValue("tempdir");
		File sourceFile = new File(srcPath);
		createConnection();
		final StringBuilder stdOut = new StringBuilder();
		final StringBuilder errOut = new StringBuilder();
		try {
			this.destPath = this.sftpChannel.pwd() + "/" + this.tempDir;
			this.sftpChannel.mkdir(this.destPath);
			this.transferFileOrFolder(sourceFile, this.destPath);
			runSSHCommandInExecChannel(this.command, stdOut, errOut, DEFAULT_MAX_OUTPUT_SIZE * 1024, outputFile,
					errorFile, session, tty, timeout);
			if (errOut.toString().trim().length() != 0) {
				ConsoleWriter.writeln((Object) ("" + errOut.toString()));
			}
			if (stdOut.toString().trim().length() != 0) {
				ConsoleWriter.writeln((Object) ("OUTPUT: " + stdOut.toString()));
			}
			stdOut.setLength(0);
			errOut.setLength(0);
		} catch (SftpException e) {
			ConsoleWriter.writeln((Throwable) e);
			throw new AutomicException(
					String.format("Unable to create directory %1$s \n %2$s", sourceFile.getName(), e.getMessage()));
		} catch (final JSchException e) {
			ConsoleWriter.writeln(e);
			ConsoleWriter.writeln("Can not create the connection, please check connection propertes.");
			throw new AutomicException("Command Execution Failed.");
		} catch (final IOException e) {
			ConsoleWriter.writeln(e);
			throw new AutomicException(e.getMessage());
		} catch (final TimeoutException e) {
			ConsoleWriter.writeln(e);
			throw new AutomicException("Timeout: Process has breached the timeout specified");
		} finally {
			this.remoteDirectoryDelete(this.destPath);
			if (this.sftpChannel != null)
				this.sftpChannel.disconnect();
			if (this.session != null)
				this.session.disconnect();
		}
	}

	private void createConnection() throws AutomicException {
		final SSHConnectionProperties sshConnectionProperties = this.createConnectionProperties();
		Channel channel = null;
		try {
			this.session = createSession(sshConnectionProperties);
			channel = this.session.openChannel("sftp");
			channel.connect(60000);
		} catch (JSchException e) {
			ConsoleWriter.writeln((Throwable) e);
			ConsoleWriter.newLine();
			ConsoleWriter.writeln((Object) "Unable to create sftp connection for put file");
			throw new AutomicException("Unable to create connection for PUT_FILE Action");
		}
		this.sftpChannel = (ChannelSftp) channel;
	}

	private void transferFileOrFolder(final File sourceFile, String dest) throws AutomicException {
		if (sourceFile.isFile()) {
			try {
				this.sftpChannel.put(sourceFile.getAbsolutePath(), dest, 0);
			} catch (SftpException e) {
				ConsoleWriter.writeln((Throwable) e);
				throw new AutomicException(
						String.format("Unable to create file %1$s \n %2$s", sourceFile.getName(), e.getMessage()));
			}
			return;
		}
		if (sourceFile.isDirectory()) {
			try {
				ConsoleWriter.newLine();
				ConsoleWriter.write(String.format("Creating Directory : %1$s", sourceFile.getName()));
				this.sftpChannel.mkdir(dest + "/" + sourceFile.getName());
				ConsoleWriter.newLine();
				ConsoleWriter.write(String.format("Directory Created : %1$s", sourceFile.getName()));
				ConsoleWriter.newLine();
				dest = dest + "/" + sourceFile.getName();
			} catch (SftpException e) {
				ConsoleWriter.writeln((Throwable) e);
				throw new AutomicException(
						String.format("Unable to create directory %1$s \n %2$s", sourceFile.getName(), e.getMessage()));
			}
		}
		final File[] listFiles;
		final File[] dir_contents = listFiles = sourceFile.listFiles();
		for (final File f : listFiles) {
			if (f.isDirectory()) {
				this.transferFileOrFolder(f, dest);
			} else {
				this.transferFileOrFolder(f, dest + "/");
			}
		}
	}

	private boolean isRemoteDirectory(final String path) throws AutomicException {
		try {
			return this.sftpChannel.stat(path).isDir();
		} catch (SftpException e) {
			ConsoleWriter.writeln((Throwable) e);
			throw new AutomicException(String.format("No folder found at path : %1$s ", path));
		}
	}

	private void remoteDirectoryDelete(String remoteDirPath) throws AutomicException {
		try {
			remoteDirPath += "/";
			if (this.isRemoteDirectory(remoteDirPath)) {
				final Vector<ChannelSftp.LsEntry> dirList = (Vector<ChannelSftp.LsEntry>) this.sftpChannel
						.ls(remoteDirPath);
				for (final ChannelSftp.LsEntry entry : dirList) {
					if (!entry.getFilename().equals(".") && !entry.getFilename().equals("..")) {
						if (entry.getAttrs().isDir()) {
							this.remoteDirectoryDelete(remoteDirPath + entry.getFilename() + "/");
						} else {
							this.sftpChannel.rm(remoteDirPath + entry.getFilename());
						}
					}
				}
				this.sftpChannel.cd("..");
				this.sftpChannel.rmdir(remoteDirPath);
			}
		} catch (SftpException e) {
			ConsoleWriter.writeln((Throwable) e);
			throw new AutomicException("Unable to delete recursively.");
		}
	}
}
