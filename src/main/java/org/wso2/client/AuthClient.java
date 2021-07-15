package org.wso2.client;

import org.json.JSONObject;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import static org.wso2.client.Constants.CLIENT_REGISRATION_URL;
import static org.wso2.client.Constants.TOKEN_URL;

public class AuthClient {
    private Config config;

    public AuthClient() throws IOException {
        this.config = Config.getInstance();
    }

    public JSONObject registerClient() throws IOException, NoSuchAlgorithmException, KeyManagementException {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Basic " + Base64.getUrlEncoder()
                .encodeToString((config.getUsername() + ":" + config.getPassword()).getBytes()));
        HTTPClient client = HTTPClient.getInstance();
        String jsonBody = "{\n" +
                "   \"callbackUrl\":\"www.google.lk\",\n" +
                "   \"clientName\":\"rest_api_publisher\",\n" +
                "   \"owner\":\"" + config.getUsername() + "\",\n" +
                "   \"grantType\":\"password refresh_token\",\n" +
                "   \"saasApp\":true\n" +
                "}";
        return client.sendPost(config.getHost() + CLIENT_REGISRATION_URL, jsonBody, headers, true);
    }

    public JSONObject sendAccessTokenRequest(String clientId, String clientSecret)
            throws IOException, NoSuchAlgorithmException, KeyManagementException {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Basic " + Base64.getUrlEncoder()
                .encodeToString((clientId + ":" + clientSecret).getBytes()));
        HTTPClient client = HTTPClient.getInstance();
        String params = "grant_type=password&username="+ config.getUsername() + "&password=" + config.getPassword()
                + "&scope=apim:api_create apim:api_publish apim:api_view";
        return client.sendPost(config.getGatewayHost() + TOKEN_URL, params, headers, false);
    }

    public String getAccessToken() throws IOException, NoSuchAlgorithmException, KeyManagementException {
        JSONObject registerResponse = registerClient();
        if (registerResponse.has("clientId") && registerResponse.has("clientSecret")) {
            JSONObject accessTokenResponse = sendAccessTokenRequest(registerResponse.getString("clientId"),
                    registerResponse.getString("clientSecret"));
            if (accessTokenResponse.has("access_token")) {
                System.out.println("Access token has been generated successfully.");
                return accessTokenResponse.getString("access_token");
            }
        }
        return null;
    }
}
