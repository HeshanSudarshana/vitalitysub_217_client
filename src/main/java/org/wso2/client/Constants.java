package org.wso2.client;

public class Constants {
    public static String CONFIG_PROPERTIES_FILE = "config/config.properties";

    public static String CLIENT_REGISRATION_URL = "/client-registration/v0.15/register";
    public static String TOKEN_URL = "/token";

    public static String APIS_URL = "/api/am/publisher/v1.0/apis";
    public static String SWAGGER_URL = "/api/am/publisher/v1.0/apis/uuid/swagger";
    public static String PUBLISH_URL = "/api/am/publisher/v1.0/apis/change-lifecycle?action=Publish&apiId=uuid";

    public static String CONFIG_HOST = "host";
    public static String CONFIG_GATEWAY_HOST = "gateway_host";
    public static String CONFIG_USERNAME = "username";
    public static String CONFIG_PASSWORD = "password";
    public static String CONFIG_API_LIMIT = "api_limit";
    public static String CONFIG_ENABLE_CLIENT_REGISTRATION = "enable_client_registration";
    public static String CONFIG_GENERATED_ACCESS_TOKEN = "generated_access_token";

    public static String DEFAULT_HOST = "https://localhost:9443";
    public static String DEFAULT_GATEWAY_HOST = "https://localhost:8243";
    public static String DEFAULT_USERNAME = "admin";
    public static String DEFAULT_PASSWORD = "password";
    public static String DEFAULT_API_LIMIT = "100";
    public static String DEFAULT_GENERATED_ACCESS_TOKEN = "access_token";

}
