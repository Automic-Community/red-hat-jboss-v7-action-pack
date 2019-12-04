package com.automic.actions;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.automic.exception.AutomicException;
import com.automic.util.CommonUtil;
import com.automic.util.ConsoleWriter;
import com.automic.util.SSHConnectionProperties;
import com.automic.util.SSHTaskTimeoutException;
import com.automic.validator.SSHValidator;

/**
 * This action is used to Run Command in {@SSH}.
 *
 */
public class RunCommandAction extends AbstractSSHAction {

	protected static final int DEFAULT_MAX_OUTPUT_SIZE = 64;
	protected String standardOutputFilePath = null;
	protected String errorOutputFilePath = null;
	private boolean flag;

	public RunCommandAction() {
		addOption("cmdfile", false, "Temporary File Path");
		addOption("outfile", false, "Output File Path");
		addOption("errfile", false, "Error File Path");
		addOption("flg", false, "Flag");
	}

	@Override
	protected void executeSpecific() throws AutomicException {
		flag = CommonUtil.convert2Bool(getOptionValue("flg"));
		File commandFile;
		String temp = getOptionValue("cmdfile");

		SSHValidator.checkNotEmpty(temp, "Temporary command file");
		commandFile = new File(temp);
		SSHValidator.checkFileExists(commandFile);

		String outputFile = getOptionValue("outfile");
		if (CommonUtil.checkNotEmpty(outputFile)) {
			standardOutputFilePath = outputFile;
			SSHValidator.checkFileExists(new File(standardOutputFilePath));
		}

		String errorFile = getOptionValue("errfile");
		if (CommonUtil.checkNotEmpty(errorFile)) {
			errorOutputFilePath = errorFile;
			SSHValidator.checkFileExists(new File(errorOutputFilePath));
		}

		String cmd = CommonUtil.readFileIntoString(commandFile);
		SSHValidator.checkNotEmpty(cmd, "Command to run");

		List<String> cmdlist = CommonUtil.readFileLinebyLine(commandFile);
		String command = formCommand(cmdlist);

		int returnCode = runCommand(command);
		if (flag) {
			String status = "SUCCESSFUL";
			if (returnCode != 0)
				status = "FAILED";
			ConsoleWriter.writeln("UC4RB_SSH_COMMAND_STATUS::=" + status);
		}
	}

	private String formCommand(List<String> cmdlist) {
		StringBuilder command = new StringBuilder();
		for (String line : cmdlist) {
			if (!line.trim().isEmpty()) {
				command = command.append(line.trim()).append("\n");
			}
		}
		return command.toString().trim();
	}

	public int runCommand(String command) throws AutomicException {

		int outputMaxSize = DEFAULT_MAX_OUTPUT_SIZE * 1024;
		final StringBuilder stdOut = new StringBuilder();
		final StringBuilder errOut = new StringBuilder();

		int returnValue = -1;
		try {
			final SSHConnectionProperties connectionProperties = createConnectionProperties();

			returnValue = executeSSHCommand(connectionProperties, command, stdOut, errOut, outputMaxSize,
					standardOutputFilePath, errorOutputFilePath);
			if (flag) {
				if (errOut.toString().trim().length() != 0)
					ConsoleWriter.writeln("ERROR: " + errOut.toString());
				if (stdOut.toString().trim().length() != 0)
					ConsoleWriter.writeln("OUTPUT: " + stdOut.toString());
			}
			stdOut.setLength(0);
			errOut.setLength(0);

		} catch (final SSHTaskTimeoutException e) {
			throw new AutomicException("Command Execution Failed");
		} catch (final AutomicException e) {
			throw new AutomicException("Command Execution Failed");
		}

		return returnValue;
	}
}