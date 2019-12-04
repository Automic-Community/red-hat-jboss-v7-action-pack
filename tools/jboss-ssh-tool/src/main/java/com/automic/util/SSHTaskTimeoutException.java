package com.automic.util;

import com.automic.exception.AutomicException;


public class SSHTaskTimeoutException extends AutomicException {
	
    public SSHTaskTimeoutException(final String message) {
        super(message);
    }

}
