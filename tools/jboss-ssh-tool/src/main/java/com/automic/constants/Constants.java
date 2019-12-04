package com.automic.constants;

/**
 * Constants class contains all the constants.
 *
 */
public class Constants {

	public static final String ACTION = "action";
	public static final String HELP = "help";

	public static final String HOST = "sshhost";

	public static final int MINUS_ONE = -1;
	public static final int ZERO = 0;

	public static final String YES = "YES";
	public static final String TRUE = "TRUE";
	public static final String ONE = "1";
	public static final String USERNAME = "username";

	public static final String ENV_PASSWORD = "UC4_DECRYPTED_PWD";
    public static final String ENV_PASSPHRASE = "UC4_DECRYPTED_PASSPHRASE";
	public static final String ENV_CONNECTION_TIMEOUT = "ENV_CONNECTION_TIMEOUT";
	public static final String ENV_SUDO_PASSPHRASE = "UC4_DECRYPTED_SUDO_PASS";

	public static final int CONNECTION_TIMEOUT = 30000;
	public static final int READ_TIMEOUT = 60000;
	public static final String PORT = "sshport";
	public static final String PRIVATE_KEY = "sshkey";
	public static final String CONN_TIMEOUT = "conntimeout";
    public static final String STRICT_HOST_KEY_CHECKING = "hostkeycheck";

	private Constants() {
	}
}
