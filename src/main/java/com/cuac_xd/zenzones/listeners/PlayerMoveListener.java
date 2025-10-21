package com.cuac_xd.zenzones.listeners;

import com.cuac_xd.zenzones.ZenZones;
import com.cuac_xd.zenzones.managers.SettingsManager;
import com.cuac_xd.zenzones.managers.ZoneManager;
import com.cuac_xd.zenzones.utils.ProtectedZone;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMoveListener implements Listener {

    private final ZenZones plugin;
    private final ZoneManager zoneManager;
    private final SettingsManager settingsManager;

    public PlayerMoveListener(ZenZones plugin) {
        this.plugin = plugin;
        this.zoneManager = plugin.getZoneManager();
        this.settingsManager = plugin.getSettingsManager();
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.getFrom().getBlockX() == event.getTo().getBlockX() && event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {
            return;
        }
        Player player = event.getPlayer();
        ProtectedZone toZone = zoneManager.getZoneAt(event.getTo());
        ProtectedZone fromZone = zoneManager.getZoneAt(event.getFrom());

        if (toZone != null && fromZone == null) {
            String permission = "zenzones.entry." + toZone.getName();
            if (!player.hasPermission(permission) && !player.hasPermission("zenzones.entry.*")) {
                event.setTo(event.getFrom());
                player.sendMessage(settingsManager.getMessage("entry-denied").replace("{zone}", toZone.getName()));
                return;
            }
            if (settingsManager.isWelcomeFarewellEnabled()) {
            }
        } else if (toZone == null && fromZone != null) {
            if (settingsManager.isWelcomeFarewellEnabled()) {
            }
        }
    }
}
