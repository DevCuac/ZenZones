package com.cuac_xd.zenzones.managers;

import com.cuac_xd.zenzones.ZenZones;
import com.cuac_xd.zenzones.utils.ProtectedZone;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class ZoneManager {

    private final ZenZones plugin;
    private final List<ProtectedZone> protectedZones = new ArrayList<>();
    private File zonesFile;
    private FileConfiguration zonesConfig;

    public ZoneManager(ZenZones plugin) {
        this.plugin = plugin;
        setupZoneFile();
    }

    private void setupZoneFile() {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdir();
        }
        zonesFile = new File(plugin.getDataFolder(), "zones.yml");
        if (!zonesFile.exists()) {
            try {
                zonesFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("¡No se pudo crear el archivo zones.yml!");
            }
        }
        zonesConfig = YamlConfiguration.loadConfiguration(zonesFile);
    }

    public void loadZones() {
        protectedZones.clear();
        if (zonesConfig.getConfigurationSection("zones") == null) return;

        for (String zoneName : zonesConfig.getConfigurationSection("zones").getKeys(false)) {
            String path = "zones." + zoneName;
            UUID ownerUUID = UUID.fromString(zonesConfig.getString(path + ".owner"));

            ProtectedZone zone = new ProtectedZone(
                    zoneName,
                    zonesConfig.getString(path + ".world"),
                    ownerUUID,
                    zonesConfig.getDouble(path + ".min.x"), zonesConfig.getDouble(path + ".min.y"), zonesConfig.getDouble(path + ".min.z"),
                    zonesConfig.getDouble(path + ".max.x"), zonesConfig.getDouble(path + ".max.y"), zonesConfig.getDouble(path + ".max.z")
            );

            // Cargar flags
            if (zonesConfig.isConfigurationSection(path + ".flags")) {
                for (String flag : zonesConfig.getConfigurationSection(path + ".flags").getKeys(false)) {
                    zone.setFlag(flag, zonesConfig.getBoolean(path + ".flags." + flag));
                }
            }
            protectedZones.add(zone);
        }
        plugin.getLogger().info(protectedZones.size() + " zonas han sido cargadas desde zones.yml.");
    }

    public void saveZones() {
        zonesConfig.set("zones", null); // Limpiar para reescribir
        for (ProtectedZone zone : protectedZones) {
            String path = "zones." + zone.getName();
            zonesConfig.set(path + ".owner", zone.getOwner().toString());
            zonesConfig.set(path + ".world", zone.getWorldName());
            zonesConfig.set(path + ".min.x", zone.getMinX());
            zonesConfig.set(path + ".min.y", zone.getMinY());
            zonesConfig.set(path + ".min.z", zone.getMinZ());
            zonesConfig.set(path + ".max.x", zone.getMaxX());
            zonesConfig.set(path + ".max.y", zone.getMaxY());
            zonesConfig.set(path + ".max.z", zone.getMaxZ());

            // Guardar flags
            for (String flag : zone.getFlags().keySet()) {
                zonesConfig.set(path + ".flags." + flag, zone.getFlag(flag));
            }
        }
        try {
            zonesConfig.save(zonesFile);
        } catch (IOException e) {
            plugin.getLogger().severe("¡No se pudo guardar el archivo zones.yml!");
        }
    }

    public void reloadZones() {
        zonesConfig = YamlConfiguration.loadConfiguration(zonesFile);
        loadZones();
    }

    public List<ProtectedZone> getProtectedZones() {
        return protectedZones;
    }

    public ProtectedZone getZoneByName(String name) {
        return protectedZones.stream()
                .filter(z -> z.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    public ProtectedZone getZoneAt(Location location) {
        return protectedZones.stream()
                .filter(z -> z.contains(location))
                .findFirst()
                .orElse(null);
    }
}