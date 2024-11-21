package me.quackduck.qcjoinguard.schedulers;
// Created by QuackDuck
import me.quackduck.qcjoinguard.QC_JoinGuard;
import me.quackduck.qcjoinguard.api.Blocked;
import me.quackduck.qcjoinguard.misc.Utils;
import org.bukkit.Bukkit;

import java.util.List;

public class UpdateLists {
    private static Object id;

    public static void start() {
        id = Bukkit.getScheduler().scheduleSyncRepeatingTask(QC_JoinGuard.plugin, () -> {
            updateNames();
            updateUuids();
            updateIps();
        }, 0L, 300*20L);
    }

    public static void stop() {
        Utils.cancelTask(id);
        id = null;
    }

    private static void updateNames() {
        Utils.doAsync(() -> {
            List<String> nicknames = Blocked.getNewList("name");
            if (!nicknames.isEmpty()) {
                Blocked.setNames(nicknames);
            }
        });
    }

    private static void updateUuids() {
        Utils.doAsync(() -> {
            List<String> uuids = Blocked.getNewList("uuid");
            if (!uuids.isEmpty()) {
                Blocked.setUuids(uuids);
            }
        });
    }

    private static void updateIps() {
        Utils.doAsync(() -> {
            List<String> ips = Blocked.getNewList("ip");
            if (!ips.isEmpty()) {
                Blocked.setIps(ips);
            }
        });
    }
}
