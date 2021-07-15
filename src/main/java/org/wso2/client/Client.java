package org.wso2.client;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class Client {

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException, KeyManagementException {
        Config config = Config.getInstance();
        String accessToken;
        if (config.isEnableClientRegistration()) {
            AuthClient authClient = new AuthClient();
            accessToken = authClient.getAccessToken();
        } else {
            accessToken = config.getGeneratedAccessToken();
        }

        SwaggerUpdator swaggerUpdator = new SwaggerUpdator();
        // add auth header
        if (accessToken != null) {
            Map<String, String> headers = new HashMap<>();
            headers.put("Authorization", "Bearer " + accessToken);
            swaggerUpdator.getApis(config.getApiLimit(), headers);
            for (String uuid : swaggerUpdator.getApiMap().keySet()) {
                swaggerUpdator.updateAndPublishApi(uuid, headers);
            }
        } else {
            System.out.println("Failed to get the access token");
        }
    }

}
