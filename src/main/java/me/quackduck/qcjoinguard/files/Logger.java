package me.quackduck.qcjoinguard.files;
// Created by QuackDuck
import me.quackduck.qcjoinguard.QC_JoinGuard;
import me.quackduck.qcjoinguard.misc.Colors;
import me.quackduck.qcjoinguard.misc.Config;
import me.quackduck.qcjoinguard.misc.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CompletableFuture;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class Logger {
    private static final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final String DIRECTORY_PATH = "plugins/QC-JoinGuard";
    private static final String FILE_PATH = DIRECTORY_PATH + File.separator + "attempts.log";
    private static final String ARCHIVE_FOLDER = DIRECTORY_PATH + File.separator + "archives";
    private static final long MAX_FILE_SIZE_BYTES = 10 * 1024 * 1024;

    public static void logBlockedAttempt(Player player, String ip) {
        if (Config.getBoolean("logger.file", true)) {
            CompletableFuture.runAsync(() -> {
                archiveIfNeeded();
                String logEntry = String.format("[%s] Blocked join attempt - Player: %s, UUID: %s, IP: %s",
                        LocalDateTime.now().format(dateFormat), player.getName(), player.getUniqueId(), ip);
                File dir = new File(DIRECTORY_PATH);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
                    writer.write(logEntry);
                    writer.newLine();
                } catch (IOException e) {
                    Utils.log(QC_JoinGuard.prefix + Colors.RED + " Failed to write to join attempts log file.");
                    if (Config.getBoolean("debug", false)) {
                        Utils.debug(e.getMessage());
                    }
                }
            });
        }
        String logEntry = QC_JoinGuard.prefix + Colors.RED + String.format(" Blocked join attempt - Player: %s, UUID: %s, IP: %s",
                player.getName(), player.getUniqueId(), ip);
        if (Config.getBoolean("logger.console", false)) {
            Utils.log(logEntry);
        }
        if (Config.getBoolean("logger.operator", false)) {
            for(Player p : Bukkit.getOnlinePlayers()) {
                if (p.hasPermission("qc.joinguard.logger") || p.isOp()) {
                    p.sendMessage(Colors.color(logEntry));
                }
            }
        }
    }

    private static void archiveIfNeeded() {
        CompletableFuture.runAsync(() -> {
            File logFile = new File(FILE_PATH);
            if (logFile.exists() && logFile.length() >= MAX_FILE_SIZE_BYTES) {
                File archiveDir = new File(ARCHIVE_FOLDER);
                if (!archiveDir.exists()) {
                    archiveDir.mkdirs();
                }
                String archiveFileName = String.format("attempts_%s.log", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")));
                File archiveFile = new File(archiveDir, archiveFileName);
                try {
                    Files.move(logFile.toPath(), archiveFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    Utils.log(QC_JoinGuard.prefix + Colors.RED + " Log file archived as: " + archiveFileName);
                } catch (IOException e) {
                    Utils.log(QC_JoinGuard.prefix + Colors.RED + " Failed to archive log file.");
                    if (Config.getBoolean("debug", false)) {
                        Utils.debug(e.getMessage());
                    }
                }
            }
        });
    }
}
