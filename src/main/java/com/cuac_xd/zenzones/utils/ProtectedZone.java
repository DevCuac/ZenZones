package com.cuac_xd.zenzones.utils;

import org.bukkit.Location;
import java.util.*;

public class ProtectedZone {

    private final String name;
    private final String worldName;
    private final UUID owner;
    private final double minX, minY, minZ;
    private final double maxX, maxY, maxZ;
    private final Map<String, Boolean> flags = new HashMap<>();

    public ProtectedZone(String name, String worldName, UUID owner, double x1, double y1, double z1, double x2, double y2, double z2) {
        this.name = name.toLowerCase();
        this.worldName = worldName;
        this.owner = owner;
        this.minX = Math.min(x1, x2); this.minY = Math.min(y1, y2); this.minZ = Math.min(z1, z2);
        this.maxX = Math.max(x1, x2); this.maxY = Math.max(y1, y2); this.maxZ = Math.max(z1, z2);

        // Valores por defecto para las flags (true = DENY, false = ALLOW)
        flags.put("pvp", true);
        flags.put("build", true);
        flags.put("mob-spawning", true);
        flags.put("item-drop", false);
        flags.put("use", false);
    }

    public boolean contains(Location loc) {
        if (!loc.getWorld().getName().equals(worldName)) {
            return false;
        }
        return loc.getX() >= minX && loc.getX() <= maxX &&
                loc.getY() >= minY && loc.getY() <= maxY &&
                loc.getZ() >= minZ && loc.getZ() <= maxZ;
    }

    // Getters
    public String getName() { return name; }
    public String getWorldName() { return worldName; }
    public UUID getOwner() { return owner; }
    public double getMinX() { return minX; }
    public double getMinY() { return minY; }
    public double getMinZ() { return minZ; }
    public double getMaxX() { return maxX; }
    public double getMaxY() { return maxY; }
    public double getMaxZ() { return maxZ; }

    // Flag management
    public boolean getFlag(String flagName) {
        return flags.getOrDefault(flagName.toLowerCase(), false);
    }

    public void setFlag(String flagName, boolean value) {
        flags.put(flagName.toLowerCase(), value);
    }

    public Map<String, Boolean> getFlags() {
        return flags;
    }
}