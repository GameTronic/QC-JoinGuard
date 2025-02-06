package me.quackduck.qcjoinguard.listeners;
// Created by QuackDuck
import me.quackduck.qcjoinguard.QC_JoinGuard;
import me.quackduck.qcjoinguard.api.Api;
import me.quackduck.qcjoinguard.files.Alts;
import me.quackduck.qcjoinguard.api.Auth;
import me.quackduck.qcjoinguard.api.Blocked;
import me.quackduck.qcjoinguard.files.Logger;
import me.quackduck.qcjoinguard.misc.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.json.JSONObject;

import java.net.InetAddress;
import java.util.List;
import java.util.UUID;

public class LoginListener implements Listener {
    @EventHandler
    public void onLogin(PlayerLoginEvent e) {
        if (!QC_JoinGuard.plugin.isGuarded()) {
            e.disallow(PlayerLoginEvent.Result.KICK_OTHER, Colors.color("&cJoinGuard isn't fully enabled yet. Please try again later."));
            return;
        }
        InetAddress net = e.getRealAddress();
        Player player = e.getPlayer();
        String ip = Api.getPlayerIP(player);
        if (ip.equals("ERROR")) ip = e.getRealAddress().getHostAddress();
        String serverIp = QC_JoinGuard.plugin.getServerIP();
        if (!net.isAnyLocalAddress() && !net.isLoopbackAddress()) {
            checkAlts(player, ip, serverIp);
        }
        if (Config.getBoolean("maintenance.enabled", false) &&
                !(player.hasPermission("qc.joinguard.bypass.maintenance") || player.isOp())) {
            e.disallow(PlayerLoginEvent.Result.KICK_OTHER, Colors.color(Config.getString("maintenance.message")));
            return;
        }
        if (!Config.getStringList("whitelist").contains(e.getPlayer().getName())) {
            if (Blocked.getNames().contains(player.getName()) ||
                    Blocked.getUuids().contains(player.getUniqueId().toString())) {
                Blocked.sendAttempt();
                reportAttempt(player, ip, serverIp);
                Logger.logBlockedAttempt(player, ip);
                e.disallow(PlayerLoginEvent.Result.KICK_BANNED, Colors.color(Config.getString("kick-message")));
                return;
            }
        }
        if (Config.getInt("concurrent-connections.max", 3) > 0) {
            if (!Config.getBoolean("concurrent-connections.bypass", true) ||
                    (!player.hasPermission("qc.joinguard.bypass.concurrent") && !player.isOp())) {
                if (checkConcurrentConnections(player, ip)) {
                    e.disallow(PlayerLoginEvent.Result.KICK_OTHER, Colors.color(Config.getString("concurrent-connections.kick-message")));
                    return;
                }
            }
        }
        Alts.putCurrentlyOnline(player.getUniqueId(), ip);
    }

    private boolean checkConcurrentConnections(Player player, String ip) {
        Alts.removeCurrentlyOnline(player.getUniqueId());
        return Alts.getCurrentlyOnline().values().stream().filter(ip::equals).count() >= Config.getInt("concurrent-connections.max", 3);
    }

    private void checkAlts(Player player, String ip, String serverIp) {
        String hashedIp = Utils.hash(ip);
        AltsEntry entry = Alts.getDb().getOrDefault(hashedIp, new AltsEntry());
        entry.addPlayer(player.getUniqueId());
        Alts.putDb(hashedIp, entry);
        if (containsBlockedAlts(entry.getPlayers()) || Blocked.getIps().contains(hashedIp)) {
            reportAlts(player, entry.getPlayers(), ip, serverIp);
        }
    }

    private void reportAttempt(Player player, String ip, String serverIp) {
        JSONObject attemptJson = createMainJson(player, ip, serverIp);
        Utils.doAsync(() -> {
            String response = Api.sendJsonPayload("https://joinguard.raidvm.com/api/attempt", attemptJson);
            if (Config.getBoolean("debug", false)) {
                Utils.debug(response);
            }
        });
    }

    private boolean containsBlockedAlts(List<UUID> alts) {
        for (UUID alt : alts) {
            if (Blocked.getUuids().contains(alt.toString())) {
                return true;
            }
            if (Blocked.getNames().contains(Bukkit.getOfflinePlayer(alt).getName())) {
                return true;
            }
        }
        return false;
    }

    private void reportAlts(Player player, List<UUID> alts, String ip, String serverIp) {
        JSONObject altsJson = createMainJson(player, ip, serverIp);
        altsJson.put("alts", alts);
        Utils.doAsync(() -> {
            String response = Api.sendJsonPayload("https://joinguard.raidvm.com/api/alts", altsJson);
            if (Config.getBoolean("debug", false)) {
                Utils.debug(response);
            }
        });
    }

    private JSONObject createMainJson(Player player, String ip, String serverIp) {
        JSONObject playerJson = new JSONObject();
        playerJson.put("nick", player.getName());
        playerJson.put("uuid", player.getUniqueId().toString());
        playerJson.put("ip", ip);
        JSONObject serverJson = new JSONObject();
        serverJson.put("ip", serverIp);
        serverJson.put("port", Bukkit.getPort());
        JSONObject finalJson = new JSONObject();
        finalJson.put("api", Auth.getKey());
        finalJson.put("player", playerJson);
        finalJson.put("server", serverJson);
        return finalJson;
    }
}
