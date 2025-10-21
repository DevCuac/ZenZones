package com.cuac_xd.zenzones.managers;

import com.cuac_xd.zenzones.ZenZones;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class SettingsManager {

    private final ZenZones plugin;
    private FileConfiguration langConfig;

    public SettingsManager(ZenZones plugin) {
        this.plugin = plugin;
    }

    public void loadConfigAndLang() {
        plugin.saveDefaultConfig();
        File langFile = new File(plugin.getDataFolder(), "lang.yml");
        if (!langFile.exists()) {
            plugin.saveResource("lang.yml", false);
        }
        langConfig = YamlConfiguration.loadConfiguration(langFile);
    }

    public void reload() {
        plugin.reloadConfig();
        loadConfigAndLang();
    }

    public String getMessage(String path) {
        return getMessage(path, true);
    }

    public String getMessage(String path, boolean withPrefix) {
        String message = langConfig.getString(path, "&cMensaje no encontrado: " + path);

        if (withPrefix) {
            String prefix = langConfig.getString("prefix", "&8[&bZenZones&8] &r");
            message = prefix + message;
        }

        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public boolean isWelcomeFarewellEnabled() {
        return plugin.getConfig().getBoolean("show-welcome-farewell-messages", true);
    }
}
