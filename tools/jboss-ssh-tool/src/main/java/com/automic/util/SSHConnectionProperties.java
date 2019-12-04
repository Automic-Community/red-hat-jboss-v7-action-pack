package com.automic.util;

public class SSHConnectionProperties {
    String username;
    String host;
    int port;
    String password;
    String keystoreFile;
    String keystorePass;
    int timeOut;

    public SSHConnectionProperties(final String username, final String host, final int port, final String password,
            final int time) {
        this.username = username;
        this.host = host;
        this.port = port;
        this.password = password;
        timeOut = time;
    }

    public SSHConnectionProperties(final String username, final String host, final int port, final String keystoreFile,
            final String keystorePass, final int time) {
        this.username = username;
        this.host = host;
        this.port = port;
        this.keystoreFile = keystoreFile;
        this.keystorePass = keystorePass;
        timeOut = time;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    public String getHost() {
        return host;
    }

    public int getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(final int timeOut) {
        this.timeOut = timeOut;
    }

    public void setHost(final String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(final int port) {
        this.port = port;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public String getKeystoreFile() {
        return keystoreFile;
    }

    public void setKeystoreFile(final String keystoreFile) {
        this.keystoreFile = keystoreFile;
    }

    public String getKeystorePass() {
        return keystorePass;
    }

    public void setKeystorePass(final String keystorePass) {
        this.keystorePass = keystorePass;
    }
}
