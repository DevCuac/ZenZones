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

    // --- SECCIÓN CORREGIDA ---

    /**
     * Obtiene un mensaje del lang.yml y le añade el prefijo por defecto.
     * @param path La ruta del mensaje en el archivo lang.yml.
     * @return El mensaje formateado con prefijo.
     */
    public String getMessage(String path) {
        // Llama a la versión más completa del método, siempre con prefijo.
        return getMessage(path, true);
    }

    /**
     * Obtiene un mensaje del lang.yml, con la opción de incluir o no el prefijo.
     * @param path La ruta del mensaje en el archivo lang.yml.
     * @param withPrefix Si es true, se añadirá el prefijo definido en lang.yml.
     * @return El mensaje formateado.
     */
    public String getMessage(String path, boolean withPrefix) {
        String message = langConfig.getString(path, "&cMensaje no encontrado: " + path);

        if (withPrefix) {
            String prefix = langConfig.getString("prefix", "&8[&bZenZones&8] &r");
            message = prefix + message;
        }

        return ChatColor.translateAlternateColorCodes('&', message);
    }

    // --- FIN DE LA SECCIÓN CORREGIDA ---

    public boolean isWelcomeFarewellEnabled() {
        return plugin.getConfig().getBoolean("show-welcome-farewell-messages", true);
    }
}