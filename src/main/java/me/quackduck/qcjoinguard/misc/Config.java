package me.quackduck.qcjoinguard.misc;
// Created by QuackDuck
import me.quackduck.qcjoinguard.QC_JoinGuard;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.util.Vector;

import java.util.List;

public class Config {
    public static Object get(String s) {
        return QC_JoinGuard.plugin.getConfig().get(s);
    }
    public static Object get(String s, Object d) {
        return QC_JoinGuard.plugin.getConfig().get(s, d);
    }

    public static Boolean getBoolean(String s) {
        return QC_JoinGuard.plugin.getConfig().getBoolean(s);
    }
    public static Boolean getBoolean(String s, Boolean d) {
        return QC_JoinGuard.plugin.getConfig().getBoolean(s, d);
    }

    public static Integer getInt(String s) {
        return QC_JoinGuard.plugin.getConfig().getInt(s);
    }
    public static Integer getInt(String s, Integer d) {
        return QC_JoinGuard.plugin.getConfig().getInt(s, d);
    }

    public static String getString(String s) {
        return QC_JoinGuard.plugin.getConfig().getString(s);
    }
    public static String getString(String s, String d) {
        return QC_JoinGuard.plugin.getConfig().getString(s, d);
    }

    public static Double getDouble(String s) {
        return QC_JoinGuard.plugin.getConfig().getDouble(s);
    }
    public static Double getDouble(String s, Double d) {
        return QC_JoinGuard.plugin.getConfig().getDouble(s, d);
    }

    public static Long getLong(String s) {
        return QC_JoinGuard.plugin.getConfig().getLong(s);
    }
    public static Long getLong(String s, Long d) {
        return QC_JoinGuard.plugin.getConfig().getLong(s, d);
    }

    public static Location getLocation(String s) {
        return QC_JoinGuard.plugin.getConfig().getLocation(s);
    }
    public static Location getLocation(String s, Location d) {
        return QC_JoinGuard.plugin.getConfig().getLocation(s, d);
    }

    public static List<String> getStringList(String s) {
        return QC_JoinGuard.plugin.getConfig().getStringList(s);
    }

    public static ConfigurationSection getConfigurationSection(String s) {
        return QC_JoinGuard.plugin.getConfig().getConfigurationSection(s);
    }

    public static List<?> getList(String s) {
        return QC_JoinGuard.plugin.getConfig().getList(s);
    }
    public static List<?> getList(String s, List<?> d) {
        return QC_JoinGuard.plugin.getConfig().getList(s, d);
    }

    public static Vector getVector(String s) {
        return QC_JoinGuard.plugin.getConfig().getVector(s);
    }
    public static Vector getVector(String s, Vector d) {
        return QC_JoinGuard.plugin.getConfig().getVector(s, d);
    }

    public static void set(String s, Object v) {
        QC_JoinGuard.plugin.getConfig().set(s, v);
    }

    public static void save() {
        QC_JoinGuard.plugin.saveConfig();
    }

    public static void reload() {
        QC_JoinGuard.plugin.reloadConfig();
    }
}
