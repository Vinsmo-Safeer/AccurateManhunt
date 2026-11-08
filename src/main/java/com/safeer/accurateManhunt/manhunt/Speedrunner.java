package com.safeer.accurateManhunt.manhunt;

import org.bukkit.Location;
import org.bukkit.WorldType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Speedrunner {

    private final UUID uniqueId;

    private Map<String, Location> location = new HashMap<>();


    public Speedrunner(UUID uniqueId) {
        this.uniqueId = uniqueId;
    }

    public UUID getUniqueId() {
        return uniqueId;
    }

    public void setLocation(Location location) {
        this.location.put(location.getWorld().getName(), location.clone());
    }

    public Location getLastOverWorldLocation(String worldName) {
        return this.location.get(worldName);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Speedrunner other)) return false;

        return uniqueId.equals(other.getUniqueId());
    }

    @Override
    public int hashCode() {
        return uniqueId.hashCode();
    }
}
