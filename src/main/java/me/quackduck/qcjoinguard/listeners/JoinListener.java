package me.quackduck.qcjoinguard.listeners;
// Created by QuackDuck
import me.quackduck.qcjoinguard.api.Auth;
import me.quackduck.qcjoinguard.files.Updater;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        if (!Auth.isLoggedIn()) {
            if (p.hasPermission("qc.joinguard.cmd.login") || p.isOp()) {
                Auth.notify(p);
            }
        }
        if (p.hasPermission("qc.joinguard.cmd.update") || p.isOp()) {
            Updater.checkForUpdates(p);
        }
    }
}
