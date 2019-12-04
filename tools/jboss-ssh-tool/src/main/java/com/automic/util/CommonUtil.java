package com.automic.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import com.automic.constants.Constants;
import com.automic.exception.AutomicException;

/**
 * Common Utility class contains basic function(s) required by {@SSH} actions.
 *
 */
public class CommonUtil {

    private CommonUtil() {
    }

    /**
     * Method to format error message in the format "ERROR | message"
     *
     * @param message
     * @return formatted message
     */
    public static final String formatErrorMessage(final String message) {
        final StringBuilder sb = new StringBuilder();
        if (null != message && !message.isEmpty()) {
            sb.append("ERROR").append(" | ").append(message);
        }

        return sb.toString();
    }

    /**
     * Method to check if a String is not empty
     *
     * @param field
     * @return true if String is not empty else false
     */
    public static final boolean checkNotEmpty(String field) {
        return field != null && !field.isEmpty();
    }

    /**
     * Method to read environment value. If not defined then it returns the default value as specified.
     *
     * @param value
     * @param defaultValue
     * @return
     */
    public static final String getEnvParameter(final String paramName, String defaultValue) {
        String val = System.getenv(paramName);
        if (val == null) {
            val = defaultValue;
        }
        return val;
    }

    /**
     *
     * Method to parse String containing numeric integer value. If string is not valid, then it returns the default
     * value as specified.
     *
     * @param value
     * @param defaultValue
     * @return numeric value
     */
    public static int parseStringValue(final String value, int defaultValue) {
        int i = defaultValue;
        if (checkNotEmpty(value)) {
            try {
                i = Integer.parseInt(value);
            } catch (final NumberFormatException nfe) {
                i = defaultValue;
            }
        }
        return i;
    }

    /**
     * Method to convert YES/NO values to boolean true or false
     *
     * @param value
     * @return true if YES, 1
     */
    public static final boolean convert2Bool(String value) {
        boolean ret = false;
        if (checkNotEmpty(value)) {
            ret = Constants.YES.equalsIgnoreCase(value) || Constants.TRUE.equalsIgnoreCase(value)
                    || Constants.ONE.equalsIgnoreCase(value);
        }
        return ret;
    }

    public static final String formatDate(final String message) {
        final StringBuilder sb = new StringBuilder();

        return sb.toString();
    }

    /**
     *
     * Method to read the value as defined in environment. If value is not valid integer, then it returns the default
     * value as specified.
     *
     * @param paramName
     * @param defaultValue
     * @return parameter value
     */
    public static final int getEnvParameter(final String paramName, int defaultValue) {
        String val = System.getenv(paramName);
        int i;
        if (val != null) {
            try {
                i = Integer.parseInt(val);
            } catch (final NumberFormatException nfe) {
                i = defaultValue;
            }
        } else {
            i = defaultValue;
        }
        return i;
    }

    public static String readFileIntoString(File file) throws AutomicException {

        try {
            String content = new String(Files.readAllBytes(Paths.get(file.getPath())));
            return content.trim();
        } catch (IOException e) {
            throw new AutomicException(
                    String.format("Error occured while reading contents from file [%s] : %s ", file, e.getMessage()));
        }
    }

    /**
     * Print the content of file given as an input
     *
     * @param parameterName
     *            input parameter
     * @param parameterValue
     *            value for the input parameter
     */
    public static void printContent(String parameterName, String parameterValue) {
        ConsoleWriter.writeln("Parameter [" + parameterName + "] = " + parameterValue);
    }

    /**
     * Reads the content of file line by line
     *
     * @param file
     *            file to be read
     * @return line List of all the lines present in file.
     */

    public static List<String> readFileLinebyLine(File file) throws AutomicException {
        try {
            List<String> lines = Files.readAllLines(Paths.get(file.getPath()),java.nio.charset.StandardCharsets.UTF_8);
            return lines;
        } catch (IOException e) {
            e.printStackTrace();
            throw new AutomicException(
                    String.format("Error occured while reading contents from file [%s] : %s ", file, e.getMessage()));
        }

    }
}
