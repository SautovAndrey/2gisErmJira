package com.example.userverification;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import java.util.Base64;

public class UserCheckService {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public boolean checkUserInJira(String userLogin, String adminUsername, String adminPassword) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            String auth = adminUsername + ":" + adminPassword;
            String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
           // HttpGet request = new HttpGet("https://jira.2gis.ru/rest/api/2/user?username=$userlogin" + userLogin);
            HttpGet request = new HttpGet("https://jira.2gis.ru/rest/api/2/user?username=" + userLogin);

            request.setHeader("Authorization", "Basic " + encodedAuth);

            try (var response = httpClient.execute(request)) {
                int statusCode = response.getCode();
                return statusCode == 200;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean checkUserInERM(String userLogin, String ermToken) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
           // HttpGet request = new HttpGet("https://aim.api.prod.erm.2gis.ru/api/v1.0/odata/users/check(login=" + userLogin);
            HttpGet request = new HttpGet("https://aim.api.prod.erm.2gis.ru/api/v1.0/odata/users/check(login=" + userLogin + ")");

            request.setHeader("Authorization", "Bearer " + ermToken);

            try (var response = httpClient.execute(request)) {
                if (response.getCode() == 200) {
                    String jsonResponse = EntityUtils.toString(response.getEntity());
                    // Предположим, что ответ содержит объект с флагом "exists"
                    var rootNode = objectMapper.readTree(jsonResponse);
                    return rootNode.path("exists").asBoolean();
                }
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}