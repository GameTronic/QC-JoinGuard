package me.quackduck.qcjoinguard.files;
// Created by QuackDuck
import me.quackduck.qcjoinguard.QC_JoinGuard;
import me.quackduck.qcjoinguard.misc.AltsEntry;
import me.quackduck.qcjoinguard.misc.Colors;
import me.quackduck.qcjoinguard.misc.Config;
import me.quackduck.qcjoinguard.misc.Utils;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class Alts {
    private static HashMap<String, AltsEntry> db;
    private static HashMap<UUID, String> currentlyOnline;

    private static final String DIRECTORY_PATH = "plugins/QC-JoinGuard";
    private static final String FILE_PATH = DIRECTORY_PATH + File.separator + "alts.quack";
    private static final int EXPIRATION_DAYS = 90;

    @SuppressWarnings("unchecked")
    public static HashMap<String, AltsEntry> load() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            return new HashMap<>();
        }
        try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(Paths.get(FILE_PATH)))) {
            return (HashMap<String, AltsEntry>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            Utils.log(QC_JoinGuard.prefix + Colors.RED + " Failed to load alts file.");
            if (Config.getBoolean("debug", false)) {
                Utils.debug(e.getMessage());
            }
            return new HashMap<>();
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void save() {
        File dir = new File(DIRECTORY_PATH);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(Paths.get(FILE_PATH)))) {
            oos.writeObject(db);
        } catch (IOException e) {
            Utils.log(QC_JoinGuard.prefix + Colors.RED + " Failed to save alts file.");
            if (Config.getBoolean("debug", false)) {
                Utils.debug(e.getMessage());
            }
        }
    }

    public static void setup() {
        db = load();
    }

    public static void cleanupExpiredIPs() {
        Iterator<Map.Entry<String, AltsEntry>> iterator = db.entrySet().iterator();
        long now = System.currentTimeMillis();
        long expirationMillis = EXPIRATION_DAYS * 24L * 60 * 60 * 1000;
        while (iterator.hasNext()) {
            Map.Entry<String, AltsEntry> entry = iterator.next();
            if (now - entry.getValue().getLastAccess() > expirationMillis) {
                iterator.remove();
            }
        }
        save();
    }

    public static HashMap<String, AltsEntry> getDb() {
        return db;
    }

    public static void setDb(HashMap<String, AltsEntry> db) {
        Alts.db = db;
    }

    public static void putDb(String ip, AltsEntry entry) {
        db.put(ip, entry);
    }

    public static HashMap<UUID, String> getCurrentlyOnline() {
        return currentlyOnline;
    }

    public static void setCurrentlyOnline(HashMap<UUID, String> currentlyOnline) {
        Alts.currentlyOnline = currentlyOnline;
    }

    public static void putCurrentlyOnline(UUID uuid, String ip) {
        currentlyOnline.put(uuid, ip);
    }

    public static void removeCurrentlyOnline(UUID uuid) {
        currentlyOnline.remove(uuid);
    }
}
