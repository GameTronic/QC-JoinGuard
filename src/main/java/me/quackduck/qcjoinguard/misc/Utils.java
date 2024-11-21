package me.quackduck.qcjoinguard.misc;
// Created by QuackDuck
import com.google.common.hash.Hashing;
import me.quackduck.qcjoinguard.QC_JoinGuard;
import org.bukkit.Bukkit;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class Utils {
    public static void log(String message) {
        Bukkit.getConsoleSender().sendMessage(Colors.color(message));
    }

    public static void doAsync(Runnable task){
        Bukkit.getScheduler().runTaskAsynchronously(QC_JoinGuard.plugin, task);
    }

    public static String getMCVersion() {
        String[] serverVersion = Bukkit.getBukkitVersion().split("-");
        return serverVersion[0];
    }

    public static void cancelTask(Object object) {
        if (object != null) {
            Bukkit.getScheduler().cancelTask((int) object);
        }
    }

    public static int getGeneralMCVersion() {
        String version = getMCVersion();
        if (version.matches("1.1")) {
            return 1;
        } else if (version.matches("1.2.1") || version.matches("1.2.2") || version.matches("1.2.3") || version.matches("1.2.4") || version.matches("1.2.5")) {
            return 2;
        } else if (version.matches("1.3.1") || version.matches("1.3.2")) {
            return 3;
        } else if (version.matches("1.4.2") || version.matches("1.4.4") || version.matches("1.4.5") || version.matches("1.4.6") || version.matches("1.4.7")) {
            return 4;
        } else if (version.matches("1.5") || version.matches("1.5.1") || version.matches("1.5.2")) {
            return 5;
        } else if (version.matches("1.6.1") || version.matches("1.6.2") || version.matches("1.6.4")) {
            return 6;
        } else if (version.matches("1.7.2") || version.matches("1.7.4") || version.matches("1.7.5") || version.matches("1.7.6") || version.matches("1.7.7") || version.matches("1.7.8") || version.matches("1.7.9") || version.matches("1.7.10")) {
            return 7;
        } else if (version.matches("1.8") || version.matches("1.8.1") || version.matches("1.8.2") || version.matches("1.8.3") || version.matches("1.8.4") || version.matches("1.8.5") || version.matches("1.8.6") || version.matches("1.8.7") || version.matches("1.8.8") || version.matches("1.8.9")) {
            return 8;
        } else if (version.matches("1.9") || version.matches("1.9.1") || version.matches("1.9.2") || version.matches("1.9.3") || version.matches("1.9.4")) {
            return 9;
        } else if (version.matches("1.10") || version.matches("1.10.1") || version.matches("1.10.2")) {
            return 10;
        } else if (version.matches("1.11") || version.matches("1.11.1") || version.matches("1.11.2")) {
            return 11;
        } else if (version.matches("1.12") || version.matches("1.12.1") || version.matches("1.12.2")) {
            return 12;
        } else if (version.matches("1.13") || version.matches("1.13.1") || version.matches("1.13.2")) {
            return 13;
        } else if (version.matches("1.14") || version.matches("1.14.1") || version.matches("1.14.2") || version.matches("1.14.3") || version.matches("1.14.4")) {
            return 14;
        } else if (version.matches("1.15") || version.matches("1.15.1") || version.matches("1.15.2")) {
            return 15;
        } else if (version.matches("1.16") || version.matches("1.16.1") || version.matches("1.16.2") || version.matches("1.16.3") || version.matches("1.16.4") || version.matches("1.16.5")) {
            return 16;
        } else if (version.matches("1.17") || version.matches("1.17.1")) {
            return 17;
        } else if (version.matches("1.18") || version.matches("1.18.1") || version.matches("1.18.2")) {
            return 18;
        } else if (version.matches("1.19") || version.matches("1.19.1") || version.matches("1.19.2") || version.matches("1.19.3") || version.matches("1.19.4")) {
            return 19;
        } else if (version.matches("1.20") || version.matches("1.20.1") || version.matches("1.20.2") || version.matches("1.20.3") || version.matches("1.20.4")) {
            return 20;
        } else if (version.matches("1.20.5") || version.matches("1.20.6") || version.matches("1.21") || version.matches("1.21.1") || version.matches("1.21.2") || version.matches("1.21.3") || version.matches("1.21.4")) {
            return 21;
        } else {
            return 0;
        }
    }

    public static void debug(String message) {
        Bukkit.getConsoleSender().sendMessage(Colors.color(Colors.RED + "[DEBUG]\n&r" + message));
    }

    public static boolean isPremium() {
        boolean premium = false;
        try {
            BufferedReader is = new BufferedReader(new FileReader("server.properties"));
            Properties props = new Properties();
            props.load(is);
            is.close();
            premium = Boolean.parseBoolean(props.getProperty("online-mode"));
        } catch (IOException ignored) {}
        return premium;
    }

    public static String hash(String string) {
        return Hashing.sha512().hashString(string, StandardCharsets.UTF_8).toString();
    }
}
