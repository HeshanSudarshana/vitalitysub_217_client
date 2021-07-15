package org.wso2.client;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static org.wso2.client.Constants.*;

public class Config {
    private static Config instance = null;

    private String host;
    private String gatewayHost;
    private String username;
    private String password;
    private int apiLimit;
    private boolean enableClientRegistration;
    private String generatedAccessToken;

    private Config() throws IOException {
        initiateConfigs();
    }

    public static Config getInstance() throws IOException {
        if (instance == null)
            instance = new Config();

        return instance;
    }

    public void initiateConfigs() throws IOException {
//        InputStream inputStream = Config.class.getResourceAsStream("/config/config.properties");
        InputStream inputStream = new FileInputStream(CONFIG_PROPERTIES_FILE);
        Properties props = new Properties();
        props.load(inputStream);

        this.host = props.getProperty(CONFIG_HOST, DEFAULT_HOST);
        this.gatewayHost = props.getProperty(CONFIG_GATEWAY_HOST, DEFAULT_GATEWAY_HOST);
        this.username = props.getProperty(CONFIG_USERNAME, DEFAULT_USERNAME);
        this.password = props.getProperty(CONFIG_PASSWORD, DEFAULT_PASSWORD);
        this.apiLimit = Integer.parseInt(props.getProperty(CONFIG_API_LIMIT, DEFAULT_API_LIMIT));
        this.enableClientRegistration = Boolean.parseBoolean(props.getProperty(CONFIG_ENABLE_CLIENT_REGISTRATION, "true"));
        this.generatedAccessToken = props.getProperty(CONFIG_GENERATED_ACCESS_TOKEN, DEFAULT_GENERATED_ACCESS_TOKEN);
        inputStream.close();
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getGatewayHost() {
        return gatewayHost;
    }

    public void setGatewayHost(String gatewayHost) {
        this.gatewayHost = gatewayHost;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getApiLimit() {
        return apiLimit;
    }

    public void setApiLimit(int apiLimit) {
        this.apiLimit = apiLimit;
    }

    public boolean isEnableClientRegistration() {
        return enableClientRegistration;
    }

    public void setEnableClientRegistration(boolean enableClientRegistration) {
        this.enableClientRegistration = enableClientRegistration;
    }

    public String getGeneratedAccessToken() {
        return generatedAccessToken;
    }

    public void setGeneratedAccessToken(String generatedAccessToken) {
        this.generatedAccessToken = generatedAccessToken;
    }
}
