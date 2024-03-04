package com.example.userverification;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import java.util.Base64;

public class UserCheckService {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static boolean checkUserInJira(String userLogin, String adminUsername, String adminPassword) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            String auth = adminUsername + ":" + adminPassword;
            String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
            HttpGet request = new HttpGet("https://jira.example.com/rest/api/2/user?username=" + userLogin);
            request.setHeader("Authorization", "Basic " + encodedAuth);

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                // Проверяем статус ответа
                if (response.getCode() == 200) {
                    // Пользователь найден
                    return true;
                } else if (response.getCode() == 404) {
                    // Пользователь не найден
                    return false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean checkUserInERM(String userLogin, String ermToken) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet("https://erm.example.com/api/users/" + userLogin);
            request.setHeader("Authorization", "Bearer " + ermToken);

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                if (response.getCode() == 200) {
                    // Для примера, предположим, что ответ содержит флаг присутствия пользователя
                    String json = EntityUtils.toString(response.getEntity());
                    UserResponse userResponse = objectMapper.readValue(json, UserResponse.class);
                    return userResponse.isPresent();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Допустим, у нас есть класс UserResponse, который соответствует структуре ответа от ЕРМ
    public static class UserResponse {
        private boolean present;

        public boolean isPresent() {
            return present;
        }

        public void setPresent(boolean present) {
            this.present = present;
        }
    }
}
