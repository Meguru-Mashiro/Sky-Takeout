package com.sky.client;

import com.sky.properties.AiProperties;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class AiClient {

    @Autowired
    private AiProperties aiProperties;

    private final OkHttpClient httpClient;

    public AiClient() {
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
    }

    public String chat(String userMessage, String systemPrompt) {
        List<Map<String, String>> messages = new ArrayList<>();

        if (systemPrompt != null && !systemPrompt.isEmpty()) {
            messages.add(createMessage("system", systemPrompt));
        }

        messages.add(createMessage("user", userMessage));

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", aiProperties.getModel());
        requestBody.put("messages", messages);
        requestBody.put("temperature", aiProperties.getTemperature());
        requestBody.put("max_tokens", aiProperties.getMaxTokens());

        String json = toJson(requestBody);

        // 修改点：使用 MediaType.get() 替代 parse()，并直接使用 RequestBody.create(String, MediaType)
        MediaType mediaType = MediaType.get("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(json, mediaType);

        Request request = new Request.Builder()
                .url(aiProperties.getApiUrl())
                .post(body)
                .addHeader("Authorization", "Bearer " + aiProperties.getApiKey())
                .addHeader("Content-Type", "application/json")
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                log.error("AI API调用失败，状态码: {}", response.code());
                return "抱歉，系统繁忙，请稍后再试";
            }

            String responseBody = response.body().string();
            return parseResponse(responseBody);
        } catch (IOException e) {
            log.error("AI API调用异常", e);
            return "抱歉，系统繁忙，请稍后再试";
        }
    }

    private Map<String, String> createMessage(String role, String content) {
        Map<String, String> message = new HashMap<>();
        message.put("role", role);
        message.put("content", content);
        return message;
    }

    private String parseResponse(String json) {
        try {
            com.alibaba.fastjson2.JSONObject jsonObject = com.alibaba.fastjson2.JSON.parseObject(json);
            com.alibaba.fastjson2.JSONArray choices = jsonObject.getJSONArray("choices");
            if (choices != null && !choices.isEmpty()) {
                return choices.getJSONObject(0)
                        .getJSONObject("message")
                        .getString("content");
            }
            return "抱歉，系统繁忙，请稍后再试";
        } catch (Exception e) {
            log.error("解析AI响应失败", e);
            return "抱歉，系统繁忙，请稍后再试";
        }
    }

    private String toJson(Object obj) {
        return com.alibaba.fastjson2.JSON.toJSONString(obj);
    }
}
