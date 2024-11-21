package me.quackduck.qcjoinguard.misc;
// Created by QuackDuck
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AltsEntry implements Serializable {
    private List<UUID> players;
    private long lastAccess;

    public AltsEntry() {
        this.players = new ArrayList<>();
        this.lastAccess = System.currentTimeMillis();
    }

    public List<UUID> getPlayers() {
        return players;
    }

    public void setPlayers(List<UUID> players) {
        this.players = players;
    }

    public long getLastAccess() {
        return lastAccess;
    }

    public void setLastAccess(long lastAccess) {
        this.lastAccess = lastAccess;
    }

    public void addPlayer(UUID uuid) {
        if (!players.contains(uuid)) {
            players.add(uuid);
        }
        this.lastAccess = System.currentTimeMillis();
    }
}
