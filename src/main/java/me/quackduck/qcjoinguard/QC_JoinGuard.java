package me.quackduck.qcjoinguard;
// Created by QuackDuck
import me.quackduck.qcjoinguard.api.Api;
import me.quackduck.qcjoinguard.cmds.Cmd;
import me.quackduck.qcjoinguard.cmds.CmdComplete;
import me.quackduck.qcjoinguard.listeners.JoinListener;
import me.quackduck.qcjoinguard.listeners.LoginListener;
import me.quackduck.qcjoinguard.listeners.QuitListener;
import me.quackduck.qcjoinguard.files.Alts;
import me.quackduck.qcjoinguard.api.Auth;
import me.quackduck.qcjoinguard.api.Blocked;
import me.quackduck.qcjoinguard.files.Updater;
import me.quackduck.qcjoinguard.misc.Colors;
import me.quackduck.qcjoinguard.schedulers.AutoSave;
import me.quackduck.qcjoinguard.schedulers.UpdateLists;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SingleLineChart;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;

public final class QC_JoinGuard extends JavaPlugin {
    public static QC_JoinGuard plugin;
    public static String prefix;
    private boolean isGuarded;
    private String serverIP = "ERROR";

    @SuppressWarnings("DataFlowIssue")
    @Override
    public void onEnable() {
        plugin = this;
        initialize();
        saveDefaultConfig();
        Metrics metrics = new Metrics(this, 23474);
        AutoSave.setup();
        Auth.generateUUID();
        Auth.tryLoggingIn();
        getCommand("joinguard").setExecutor(new Cmd());
        getCommand("joinguard").setTabCompleter(new CmdComplete());
        registerEvents(this, new LoginListener(), new JoinListener(), new QuitListener());
        UpdateLists.start();
        metrics.addCustomChart(new SingleLineChart("authenticated_servers", () -> Auth.isLoggedIn() ? 1 : 0));
        metrics.addCustomChart(new SingleLineChart("blocked_attempts", Blocked::getAttempts));
        Alts.cleanupExpiredIPs();
        Updater.checkForUpdates(Bukkit.getConsoleSender());
        if (!Auth.isLoggedIn()) {
            Auth.notify(Bukkit.getConsoleSender());
        }
        isGuarded = true;
    }

    @Override
    public void onDisable() {
        AutoSave.stop();
        AutoSave.save();
        UpdateLists.stop();
    }

    public void registerEvents(org.bukkit.plugin.Plugin plugin, Listener... listeners) {
        for (Listener listener : listeners) {
            Bukkit.getServer().getPluginManager().registerEvents(listener, plugin);
        }
    }

    private void initialize() {
        isGuarded = false;
        Colors.load();
        prefix = Colors.color(Colors.GRAY + "[" + Colors.RED + "QC-JoinGuard" + Colors.GRAY + "]&r");
        Blocked.setNames(new ArrayList<>());
        Blocked.setUuids(new ArrayList<>());
        Blocked.setIps(new ArrayList<>());
        Blocked.setAttempts(0);
        Alts.setDb(new HashMap<>());
        Alts.setCurrentlyOnline(new HashMap<>());
        Auth.setLoggedIn(false);
    }

    public boolean isGuarded() {
        return isGuarded;
    }

    public String getServerIP() {
        return serverIP;
    }

    public void setServerIP(String serverIP) {
        this.serverIP = serverIP;
    }
}
