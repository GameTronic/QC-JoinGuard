package me.quackduck.qcjoinguard.api;
// Created by QuackDuck
import me.quackduck.qcjoinguard.misc.Config;
import me.quackduck.qcjoinguard.misc.Utils;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.io.support.ClassicRequestBuilder;
import org.bukkit.entity.Player;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

public class Api {
    public static String getPlayerIP(Player player) {
        final String[] ip = new String[1];
        getPlayerIP_CF(player).thenAcceptAsync(str -> ip[0] = str).join();
        return ip[0];
    }

    private static CompletableFuture<String> getPlayerIP_CF(Player player) {
        return CompletableFuture.supplyAsync(() -> {
            InetSocketAddress address = player.getAddress();
            if (address == null) return "ERROR";
            return address.getAddress().getHostAddress();
        });
    }

    public static String getServerIP() {
        String ip = getPayload("https://ipinfo.io/json", "ip");
        if (ip == null) return "ERROR";
        return ip;
    }

    public static String getPayload(String url, String key) {
        JSONObject jsonResponse = getPayload(url);
        if (jsonResponse == null) return null;
        if (!jsonResponse.has(key)) return null;
        return jsonResponse.getString(key);
    }

    public static JSONObject getPayload(String url) {
        final JSONObject[] jsonResponse = {null};
        getPayload_CF(url).thenAcceptAsync(str -> jsonResponse[0] = str).join();
        return jsonResponse[0];
    }

    private static CompletableFuture<JSONObject> getPayload_CF(String url) {
        return CompletableFuture.supplyAsync(() -> {
            final JSONObject[] jsonResponse = {null};
            try (CloseableHttpClient client = HttpClients.createDefault()) {
                ClassicHttpRequest request = ClassicRequestBuilder.get(url).build();
                HttpClientResponseHandler<JSONObject> responseHandler = response -> {
                    HttpEntity entity = response.getEntity();
                    if (entity != null) {
                        try (BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent(), StandardCharsets.UTF_8))) {
                            StringBuilder responseContent = new StringBuilder();
                            String line;
                            while ((line = reader.readLine()) != null) {
                                responseContent.append(line);
                            }
                            jsonResponse[0] = new JSONObject(responseContent.toString());
                        }
                    }
                    EntityUtils.consume(entity);
                    return jsonResponse[0];
                };
                jsonResponse[0] = client.execute(request, responseHandler);
            } catch (Exception e) {
                if (Config.getBoolean("debug", false)) {
                    Utils.debug(e.getMessage());
                }
            }
            return jsonResponse[0];
        });
    }

    public static String sendJsonPayload(String url, JSONObject payload) {
        final String[] responseMessage = new String[1];
        sendJsonPayload_CF(payload, url).thenAcceptAsync(str -> responseMessage[0] = str).join();
        return responseMessage[0];
    }

    private static CompletableFuture<String> sendJsonPayload_CF(JSONObject payload, String url) {
        return CompletableFuture.supplyAsync(() -> {
            final String[] responseMessage = new String[1];
            try (CloseableHttpClient client = HttpClients.createDefault()) {
                ClassicHttpRequest request = ClassicRequestBuilder.post(url)
                        .setEntity(new StringEntity(payload.toString(), ContentType.APPLICATION_JSON))
                        .build();
                HttpClientResponseHandler<String> responseHandler = response -> {
                    HttpEntity entity = response.getEntity();
                    if (entity != null) {
                        try (BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent(), StandardCharsets.UTF_8))) {
                            StringBuilder responseContent = new StringBuilder();
                            String line;
                            while ((line = reader.readLine()) != null) {
                                responseContent.append(line);
                            }
                            responseMessage[0] = responseContent.toString();
                        }
                    }
                    EntityUtils.consume(entity);
                    return responseMessage[0];
                };
                responseMessage[0] = client.execute(request, responseHandler);
            } catch (Exception e) {
                if (Config.getBoolean("debug", false)) {
                    Utils.debug(e.getMessage());
                }
            }
            return responseMessage[0];
        });
    }
}
