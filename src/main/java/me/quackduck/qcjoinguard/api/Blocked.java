package me.quackduck.qcjoinguard.api;
// Created by QuackDuck
import me.quackduck.qcjoinguard.QC_JoinGuard;
import org.bukkit.Bukkit;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Blocked {
    private static List<String> names;
    private static List<String> uuids;
    private static List<String> ips;
    private static int attempts;

    public static List<String> getNewList(String main) {
        String url = "https://joinguard.raidvm.com/list/" + main;
        JSONObject jsonResponse = Api.getPayload(url);
        List<String> list = new ArrayList<>();
        if (jsonResponse == null) return list;
        JSONArray dataArray = jsonResponse.getJSONArray("data");
        for (int i = 0; i < dataArray.length(); i++) {
            list.add(dataArray.getString(i));
        }
        return list;
    }

    public static void sendAttempt() {
        attempts++;
        Bukkit.getScheduler().runTaskLater(QC_JoinGuard.plugin, () -> attempts--, 35 * 60 * 20L);
    }

    public static List<String> getNames() {
        return names;
    }

    public static void setNames(List<String> names) {
        Blocked.names = names;
    }

    public static List<String> getUuids() {
        return uuids;
    }

    public static void setUuids(List<String> uuids) {
        Blocked.uuids = uuids;
    }

    public static List<String> getIps() {
        return ips;
    }

    public static void setIps(List<String> ips) {
        Blocked.ips = ips;
    }

    public static int getAttempts() {
        return attempts;
    }

    public static void setAttempts(int attempts) {
        Blocked.attempts = attempts;
    }
}
