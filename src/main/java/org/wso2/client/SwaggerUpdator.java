package org.wso2.client;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import static org.wso2.client.Constants.APIS_URL;
import static org.wso2.client.Constants.PUBLISH_URL;
import static org.wso2.client.Constants.SWAGGER_URL;

public class SwaggerUpdator {
    private Map<String, String> apiMap;
    private Config config;

    public SwaggerUpdator() throws IOException {
        this.apiMap = new HashMap<>();
        this.config = Config.getInstance();
    }

    public Map<String, String> getApiMap() {
        return apiMap;
    }

    public void setApiMap(Map<String, String> apiMap) {
        this.apiMap = apiMap;
    }

    public void getApis(int limit, Map<String, String> headers) {
        JSONObject apisResponse = null;
        try {
            HTTPClient client = HTTPClient.getInstance();
            apisResponse = client.sendGet(config.getHost() + APIS_URL + "?limit=" + limit + "&offset=0", headers);
        } catch (Exception e) {
            System.out.println("Failed to fetch the APIs.\n" + e.getMessage());
        } finally {
            if (apisResponse != null) {
                int count = apisResponse.getInt("count");
                System.out.println("Number of APIs retrieved = " + count);
                JSONArray apiList = apisResponse.getJSONArray("list");
                for (Object api : apiList) {
                    JSONObject apiJson = (JSONObject) api;
                    this.apiMap.put(apiJson.getString("id"), apiJson.getString("name") + "_" + apiJson
                            .getString("version"));
                }
            }
        }
    }

    public JSONObject getApiSwagger(String uuid, Map<String, String> headers) {
        JSONObject swaggerResponse = null;
        try {
            HTTPClient client = HTTPClient.getInstance();
            String swaggerURL = SWAGGER_URL.replaceAll("uuid", uuid);
            swaggerResponse = client.sendGet(config.getHost() + swaggerURL, headers);
        } catch (Exception e) {
            System.out.println("Failed to fetch swagger of API, uuid = " + uuid + " , name = " +  this.apiMap.get(uuid)
                    +"\n" + e.getMessage());
        }
        return swaggerResponse;
    }

    public JSONObject fixSwagger(JSONObject swagger) {
        boolean fixed = false;
        // add security scheme to API level
        if (!swagger.has("security")) {
            JSONArray securityArray = new JSONArray();
            JSONObject securityObj = new JSONObject();
            JSONArray defaultArray = new JSONArray();
            securityObj.put("default", defaultArray);
            securityArray.put(securityObj);
            swagger.put("security", securityArray);
            fixed = true;
        }
        // add authorizationUrl to scheme if not available
        if (swagger.has("components")) {
            if (swagger.getJSONObject("components").has("securitySchemes")) {
                if (swagger.getJSONObject("components").getJSONObject("securitySchemes").has("default")) {
                    if (!swagger.getJSONObject("components").getJSONObject("securitySchemes").getJSONObject("default")
                            .getJSONObject("flows").getJSONObject("implicit").has("authorizationUrl")) {
                        swagger.getJSONObject("components").getJSONObject("securitySchemes").getJSONObject("default")
                                .getJSONObject("flows").getJSONObject("implicit").put("authorizationUrl", "https://test.com");
                        fixed = true;
                    }
                }
            }
        }
        // add description to responses
        if (swagger.has("paths")) {
            JSONObject pathsObj = swagger.getJSONObject("paths");
            for (String key : pathsObj.keySet()) {
                for (String operation : pathsObj.getJSONObject(key).keySet()) {
                    if (pathsObj.getJSONObject(key).getJSONObject(operation).has("responses")) {
                        for (String response : pathsObj.getJSONObject(key).getJSONObject(operation)
                                .getJSONObject("responses").keySet()) {
                            if (!pathsObj.getJSONObject(key).getJSONObject(operation).getJSONObject("responses")
                                    .getJSONObject(response).has("description")) {
                                pathsObj.getJSONObject(key).getJSONObject(operation).getJSONObject("responses")
                                        .getJSONObject(response).put("description", "sample response description");
                                fixed = true;
                            }
                        }
                    }
                }
            }
        }
        if (!fixed) {
            return null;
        }
        return swagger;
    }

    public JSONObject updateApiSwagger(String uuid, JSONObject swagger, Map<String, String> headers) {
        JSONObject response = null;
        try {
            HTTPClient client = HTTPClient.getInstance();
            String swaggerURL = SWAGGER_URL.replaceAll("uuid", uuid);
            response = client.sendPut(config.getHost() + swaggerURL, swagger.toString(), headers);
        } catch (Exception e) {
            System.out.println("Failed to update swagger of API, uuid = " + uuid + " , name = " +  this.apiMap.get(uuid)
                    +"\n" + e.getMessage());
        }
        return response;
    }

    public JSONObject publishApi(String uuid, Map<String, String> headers) {
        JSONObject response = null;
        try {
            HTTPClient client = HTTPClient.getInstance();
            String publishURL = PUBLISH_URL.replaceAll("uuid", uuid);
            response = client.sendPost(config.getHost() + publishURL, null, headers, true);
        } catch (Exception e) {
            System.out.println("Failed to publish API, uuid = " + uuid + " , name = " +  this.apiMap.get(uuid)
                    +"\n" + e.getMessage());
        }
        return response;
    }

    public void updateAndPublishApi(String uuid, Map<String, String> headers) {
        JSONObject swagger = getApiSwagger(uuid, headers);
        if (swagger != null) {
            JSONObject fixedSwagger = fixSwagger(swagger);
            if (fixedSwagger != null) {
                JSONObject response = updateApiSwagger(uuid, fixedSwagger, headers);
                if (response != null && response.has("info")) {
                    JSONObject publishResponse = publishApi(uuid, headers);
                    if (publishResponse != null && publishResponse.has("workflowStatus")) {
                        if ("APPROVED".equals(publishResponse.getString("workflowStatus"))) {
                            System.out.println("API published successfully, uuid = " + uuid + " , name = " +
                                    this.apiMap.get(uuid));
                        }
                    } else {
                        System.out.println("API publishing failed, uuid = " + uuid + " , name = " +
                                this.apiMap.get(uuid));
                    }
                }
            } else {
                System.out.println("API doesn't need to be fixed, uuid = " + uuid + " , name = " +
                        this.apiMap.get(uuid));
            }
        }
    }
}
