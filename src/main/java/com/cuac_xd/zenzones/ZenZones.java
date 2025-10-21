package com.cuac_xd.zenzones;

import com.cuac_xd.zenzones.commands.ZoneCommand;
import com.cuac_xd.zenzones.listeners.MobSpawningListener;
import com.cuac_xd.zenzones.listeners.PlayerMoveListener;
import com.cuac_xd.zenzones.listeners.ProtectionListener;
import com.cuac_xd.zenzones.managers.SettingsManager;
import com.cuac_xd.zenzones.managers.ZoneManager;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public class ZenZones extends JavaPlugin {

    private SettingsManager settingsManager;
    private ZoneManager zoneManager;
    private WorldEditPlugin worldEdit;
    private boolean citizensHook = false;

    @Override
    public void onEnable() {
        if (getServer().getPluginManager().getPlugin("WorldEdit") == null) {
            getLogger().severe("WorldEdit no fue encontrado! El plugin ZenZones se desactivará.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        worldEdit = (WorldEditPlugin) getServer().getPluginManager().getPlugin("WorldEdit");

        if (getServer().getPluginManager().getPlugin("Citizens") != null) {
            citizensHook = true;
            getLogger().info("Citizens ha sido detectado. La integración para PvP en NPCs está activada.");
        } else {
            getLogger().info("Citizens no fue encontrado. La integración para PvP en NPCs está desactivada.");
        }

        this.settingsManager = new SettingsManager(this);
        this.zoneManager = new ZoneManager(this);

        settingsManager.loadConfigAndLang();
        zoneManager.loadZones();

        getServer().getPluginManager().registerEvents(new PlayerMoveListener(this), this);
        getServer().getPluginManager().registerEvents(new ProtectionListener(this), this);
        getServer().getPluginManager().registerEvents(new MobSpawningListener(this), this);

        // --- CAMBIO IMPORTANTE: REGISTRAR EL EXECUTOR Y EL TABCOMPLETER ---
        ZoneCommand zoneCommand = new ZoneCommand(this);
        PluginCommand command = getCommand("zenzones");
        command.setExecutor(zoneCommand);
        command.setTabCompleter(zoneCommand);
        // -----------------------------------------------------------------

        getLogger().info("ZenZones ha sido activado correctamente.");
    }

    public SettingsManager getSettingsManager() { return settingsManager; }
    public ZoneManager getZoneManager() { return zoneManager; }
    public WorldEditPlugin getWorldEdit() { return worldEdit; }
    public boolean isCitizensHooked() { return citizensHook; }
}