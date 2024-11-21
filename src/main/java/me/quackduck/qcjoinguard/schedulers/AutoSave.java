package me.quackduck.qcjoinguard.schedulers;
// Created by QuackDuck
import me.quackduck.qcjoinguard.QC_JoinGuard;
import me.quackduck.qcjoinguard.files.Alts;
import me.quackduck.qcjoinguard.misc.Utils;
import org.bukkit.Bukkit;

public class AutoSave {
    private static Object id;

    public static void start() {
        id = Bukkit.getScheduler().scheduleSyncRepeatingTask(QC_JoinGuard.plugin, AutoSave::save, 0, 60L * 20);
    }

    public static void stop() {
        Utils.cancelTask(id);
        id = null;
    }

    public static void setup() {
        Alts.setup();
    }

    public static void save() {
        Alts.save();
    }
}
