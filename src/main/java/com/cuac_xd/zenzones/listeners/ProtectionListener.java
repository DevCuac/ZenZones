package com.cuac_xd.zenzones.listeners;

import com.cuac_xd.zenzones.ZenZones;
import com.cuac_xd.zenzones.managers.ZoneManager;
import com.cuac_xd.zenzones.utils.ProtectedZone;
import net.citizensnpcs.api.CitizensAPI;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class ProtectionListener implements Listener {

    private final ZenZones plugin;
    private final ZoneManager zoneManager;

    public ProtectionListener(ZenZones plugin) {
        this.plugin = plugin;
        this.zoneManager = plugin.getZoneManager();
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        Entity damaged = event.getEntity();

        ProtectedZone zone = zoneManager.getZoneAt(damaged.getLocation());
        if (zone == null || !zone.getFlag("pvp")) {
            return;
        }

        if (plugin.isCitizensHooked()) {
            boolean isDamagerNPC = CitizensAPI.getNPCRegistry().isNPC(damager);
            boolean isDamagedNPC = CitizensAPI.getNPCRegistry().isNPC(damaged);
            if (isDamagerNPC || isDamagedNPC) {
                return;
            }
        }
        if (damager instanceof Player && damaged instanceof Player) {
            Player attacker = (Player) damager;
            if (!attacker.hasPermission("zenzones.bypass.pvp")) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        handleBuildProtection(event.getPlayer(), event.getBlock().getLocation(), event);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        handleBuildProtection(event.getPlayer(), event.getBlock().getLocation(), event);
    }

    private void handleBuildProtection(Player player, Location location, org.bukkit.event.Cancellable event) {
        ProtectedZone zone = zoneManager.getZoneAt(location);
        if (zone != null && zone.getFlag("build")) {
            if (!player.hasPermission("zenzones.bypass.build")) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        ProtectedZone zone = zoneManager.getZoneAt(player.getLocation());
        if (zone != null && zone.getFlag("item-drop")) {
            if (!player.hasPermission("zenzones.bypass.itemdrop")) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null) return;
        Player player = event.getPlayer();
        ProtectedZone zone = zoneManager.getZoneAt(event.getClickedBlock().getLocation());
        if (zone != null && zone.getFlag("use")) {
            if (!player.hasPermission("zenzones.bypass.use")) {
                event.setCancelled(true);
            }
        }
    }
}
