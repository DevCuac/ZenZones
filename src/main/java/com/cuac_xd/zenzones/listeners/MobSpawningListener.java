package com.cuac_xd.zenzones.listeners;

import com.cuac_xd.zenzones.ZenZones;
import com.cuac_xd.zenzones.managers.ZoneManager;
import com.cuac_xd.zenzones.utils.ProtectedZone;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class MobSpawningListener implements Listener {

    private final ZoneManager zoneManager;

    public MobSpawningListener(ZenZones plugin) {
        this.zoneManager = plugin.getZoneManager();
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.NATURAL) {
            return;
        }

        ProtectedZone zone = zoneManager.getZoneAt(event.getLocation());
        if (zone != null && zone.getFlag("mob-spawning")) {
            event.setCancelled(true);
        }
    }
}
