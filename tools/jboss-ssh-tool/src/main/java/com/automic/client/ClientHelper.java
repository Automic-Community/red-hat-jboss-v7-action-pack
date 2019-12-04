package com.automic.client;

import com.automic.actions.AbstractAction;
import com.automic.cli.Cli;
import com.automic.cli.CliOptions;
import com.automic.constants.Constants;
import com.automic.constants.ExceptionConstants;
import com.automic.exception.AutomicException;
import com.automic.util.ConsoleWriter;

/**
 * This class delegates the request to specified action.
 *
 */
public class ClientHelper {
    private static final String ABSOLUTEPATH = "com.automic.actions";

    private ClientHelper() {
    }

    /**
     * Method to delegate parameters to an instance of {@link AbstractAction} based on the value of Action parameter.
     *
     * @param actionParameters
     *            of options with key as option name and value is option value
     * @throws AutomicException
     */

    public static void executeAction(String[] actionParameters) throws AutomicException {
        String actionName = new Cli(new CliOptions(), actionParameters).getOptionValue(Constants.ACTION);

        AbstractAction action = null;
        try {
            Class<?> classDefinition = Class.forName(getCanonicalName(actionName));
            action = (AbstractAction) classDefinition.newInstance();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            ConsoleWriter.writeln(e);
            String msg = String.format(ExceptionConstants.INVALID_INPUT_PARAMETER, Constants.ACTION, actionName);
            throw new AutomicException(msg);
        }
        action.executeAction(actionParameters);
    }

    private static String getCanonicalName(String clsName) {
        return ABSOLUTEPATH + "." + clsName;
    }
}
