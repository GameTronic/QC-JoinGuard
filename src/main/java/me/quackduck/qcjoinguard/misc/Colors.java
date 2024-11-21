package me.quackduck.qcjoinguard.misc;
// Created by QuackDuck
import org.bukkit.ChatColor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Colors {
    public static String RED;
    public static String GOLD;
    public static String YELLOW;
    public static String GREEN;
    public static String AQUA;
    public static String BLUE;
    public static String PINK;
    public static String GRAY;

    public static void load() {
        if (Utils.getGeneralMCVersion() > 16) {
            RED = "#FF0000";
            GOLD = "#FFAA00";
            YELLOW = "#FFFF00";
            GREEN = "#00FF00";
            AQUA = "#00FFFF";
            BLUE = "#0000FF";
            PINK = "#FF00FF";
            GRAY = "#555555";
        } else {
            RED = "&c";
            GOLD = "&6";
            YELLOW = "&e";
            GREEN = "&a";
            AQUA = "&b";
            BLUE = "&9";
            PINK = "&d";
            GRAY = "&8";
        }
    }

    public static String clr(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    public static String hex(String string) {
        Pattern pattern = Pattern.compile("&?#[a-fA-F0-9]{6}");
        Matcher matcher = pattern.matcher(string);
        while (matcher.find()) {
            String color = string.substring(matcher.start(), matcher.end());
            string = string.replace(color, net.md_5.bungee.api.ChatColor.of(color) + "");
            matcher = pattern.matcher(string);
        }
        string = net.md_5.bungee.api.ChatColor.translateAlternateColorCodes('&', string);
        return string;
    }

    public static String color(String message) {
        if (Utils.getGeneralMCVersion() > 16) {
            return hex(message);
        } else {
            return clr(message);
        }
    }
}
