package me.quackduck.qcjoinguard.files;
// Created by QuackDuck
import me.quackduck.qcjoinguard.QC_JoinGuard;
import me.quackduck.qcjoinguard.api.Api;
import me.quackduck.qcjoinguard.misc.Colors;
import me.quackduck.qcjoinguard.misc.Config;
import me.quackduck.qcjoinguard.misc.Utils;
import org.bukkit.command.CommandSender;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.*;
import java.util.concurrent.CompletableFuture;

public class Updater {
    private static final String GITHUB_API_URL = "https://api.github.com/repos/GameTronic/QC-JoinGuard/releases/latest";
    private static final String CURRENT_VERSION = "v" + QC_JoinGuard.plugin.getDescription().getVersion();

    public static void checkForUpdates(CommandSender sender) {
        String latestVersion = Api.getPayload(GITHUB_API_URL, "tag_name");
        if (latestVersion == null) {
            sendInfoError(sender);
            return;
        }
        if (Config.getBoolean("debug", false)) {
            Utils.debug(CURRENT_VERSION + " -> " + latestVersion);
        }
        if (isNewVersion(latestVersion)) {
            sender.sendMessage(Colors.color(QC_JoinGuard.prefix + Colors.YELLOW + " Dostępna nowsza wersja: " + latestVersion));
            sender.sendMessage(Colors.color(Colors.GOLD + "Użyj /joinguard update aby zaktualizować plugin. "));
        } else {
            sender.sendMessage(Colors.color(QC_JoinGuard.prefix + Colors.GREEN + " Posiadasz najnowsza wersję."));
        }
    }

    public static void downloadNewVersion(CommandSender sender) {
        JSONObject githubPayload = Api.getPayload(GITHUB_API_URL);
        if (githubPayload == null) {
            sendDownloadError(sender);
            return;
        }
        JSONArray assets = githubPayload.optJSONArray("assets");
        if (assets == null || assets.isEmpty()) {
            sendDownloadError(sender);
            return;
        }
        JSONObject asset = assets.getJSONObject(0);
        String downloadUrl = asset.optString("browser_download_url", null);
        if (downloadUrl == null || downloadUrl.isEmpty()) {
            sendDownloadError(sender);
            return;
        }
        CompletableFuture.runAsync(() -> {
            try {
                URL url = new URL(downloadUrl);
                Path tempFilePath = Paths.get("plugins/QC-JoinGuard/QC-JoinGuard.jar");
                try (InputStream in = url.openStream()) {
                    Files.copy(in, tempFilePath, StandardCopyOption.REPLACE_EXISTING);
                }
                sender.sendMessage(Colors.color(QC_JoinGuard.prefix + Colors.GREEN + " Pobrano najnowszą wersję. Uruchom ponownie serwer."));
                replacePluginFile(tempFilePath);
            } catch (Exception e) {
                sendDownloadError(sender);
                if (Config.getBoolean("debug", false)) {
                    Utils.debug(e.getMessage());
                }
            }
        });
    }

    private static void replacePluginFile(Path tempFilePath) {
        Path pluginPath = Paths.get("plugins/QC-JoinGuard.jar");
        try (FileChannel sourceChannel = FileChannel.open(tempFilePath, StandardOpenOption.READ);
             FileChannel targetChannel = FileChannel.open(pluginPath, StandardOpenOption.WRITE, StandardOpenOption.CREATE)) {
            try (FileLock lock = targetChannel.lock()) {
                targetChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
                if (Files.exists(tempFilePath)) {
                    Files.delete(tempFilePath);
                }
            } catch (IOException e) {
                if (Config.getBoolean("debug", false)) {
                    Utils.debug(e.getMessage());
                }
            }
        } catch (IOException e) {
            if (Config.getBoolean("debug", false)) {
                Utils.debug(e.getMessage());
            }
        }
    }

    private static boolean isNewVersion(String latest) {
        String current = CURRENT_VERSION;
        latest = latest.startsWith("v") ? latest.substring(1) : latest;
        current = current.startsWith("v") ? current.substring(1) : current;
        String[] latestParts = latest.split("-", 2);
        String[] currentParts = current.split("-", 2);
        String[] latestNums = latestParts[0].split("\\.");
        String[] currentNums = currentParts[0].split("\\.");
        int len = Math.max(latestNums.length, currentNums.length);
        for (int i = 0; i < len; i++) {
            int lNum = i < latestNums.length ? Integer.parseInt(latestNums[i]) : 0;
            int cNum = i < currentNums.length ? Integer.parseInt(currentNums[i]) : 0;
            if (lNum != cNum) return lNum > cNum;
        }
        String latestSuffix = latestParts.length > 1 ? latestParts[1] : "";
        String currentSuffix = currentParts.length > 1 ? currentParts[1] : "";
        if (latestSuffix.isEmpty() != currentSuffix.isEmpty()) return currentSuffix.isEmpty();
        return latestSuffix.compareTo(currentSuffix) > 0;
    }

    private static void sendInfoError(CommandSender sender) {
        sender.sendMessage(Colors.color(QC_JoinGuard.prefix + Colors.RED + " Nie udało się pobrać informacji o najnowszej wersji. Spróbuj ponownie później."));
    }

    private static void sendDownloadError(CommandSender sender) {
        sender.sendMessage(Colors.color(QC_JoinGuard.prefix + Colors.RED + " Wystąpił błąd podczas pobierania nowej wersji. Plugin nie został zaktualizowany."));
    }
}
