package me.quackduck.qcjoinguard.api;
// Created by QuackDuck
import me.quackduck.qcjoinguard.QC_JoinGuard;
import me.quackduck.qcjoinguard.misc.Colors;
import me.quackduck.qcjoinguard.misc.Config;
import me.quackduck.qcjoinguard.misc.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class Auth {
    private static UUID key;
    private static boolean loggedIn;

    public static void generateUUID() {
        UUID uuid;
        try {
            uuid = UUID.fromString(Config.getString("api-key"));
        } catch (Exception e) {
            uuid = UUID.randomUUID();
            Config.set("api-key", uuid.toString());
            Config.save();
            if (Config.getBoolean("debug", false)) {
                Utils.debug(e.getMessage());
            }
        }
        key = uuid;
    }

    public static void tryLoggingIn() {
        String response = Api.getPayload("https://joinguard.raidvm.com/api/check?api=" + Auth.key, "status");
        if (response == null) {
            Auth.loggedIn = false;
            return;
        }
        Auth.loggedIn = response.equalsIgnoreCase("ok");
    }

    public static void notify(Player p) {
        p.sendMessage(Colors.color(QC_JoinGuard.prefix + Colors.RED + " Serwer nie jest zarejestrowany!" +
                " Użyj /joinguard login aby się zalogować."));
    }

    public static void notify(CommandSender sender) {
        sender.sendMessage(Colors.color(QC_JoinGuard.prefix + Colors.RED + " Serwer nie jest zarejestrowany!" +
                " Użyj /joinguard login aby się zalogować."));
    }

    public static UUID getKey() {
        return key;
    }

    public static boolean isLoggedIn() {
        return loggedIn;
    }

    public static void setLoggedIn(boolean loggedIn) {
        Auth.loggedIn = loggedIn;
    }
}
