package com.cuac_xd.zenzones.commands;

import com.cuac_xd.zenzones.ZenZones;
import com.cuac_xd.zenzones.managers.SettingsManager;
import com.cuac_xd.zenzones.managers.ZoneManager;
import com.cuac_xd.zenzones.utils.ProtectedZone;
import com.sk89q.worldedit.bukkit.selections.Selection;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ZoneCommand implements CommandExecutor, TabCompleter {

    private final ZenZones plugin;
    private final ZoneManager zoneManager;
    private final SettingsManager settingsManager;
    private final List<String> availableFlags = Arrays.asList("pvp", "build", "mob-spawning", "item-drop", "use");

    public ZoneCommand(ZenZones plugin) {
        this.plugin = plugin;
        this.zoneManager = plugin.getZoneManager();
        this.settingsManager = plugin.getSettingsManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Este comando solo puede ser usado por un jugador.");
            return true;
        }
        Player player = (Player) sender;
        if (!player.hasPermission("zenzones.admin")) {
            player.sendMessage(settingsManager.getMessage("no-permission-command"));
            return true;
        }

        if (args.length == 0) {
            handleHelp(player);
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "create": handleCreate(player, args); break;
            case "delete": handleDelete(player, args); break;
            case "list": handleList(player); break;
            case "flag": handleFlag(player, args); break;
            case "flaglist": handleFlagList(player); break;
            case "reload":
                settingsManager.reload();
                zoneManager.reloadZones();
                player.sendMessage(settingsManager.getMessage("reload-success"));
                break;
            case "help":
            default:
                handleHelp(player);
                break;
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> subCommands = Arrays.asList("create", "delete", "list", "flag", "flaglist", "reload", "help");
            return StringUtil.copyPartialMatches(args[0], subCommands, new ArrayList<>());
        }

        if (args.length == 2 && (args[0].equalsIgnoreCase("delete") || args[0].equalsIgnoreCase("flag"))) {
            List<String> zoneNames = zoneManager.getProtectedZones().stream().map(ProtectedZone::getName).collect(Collectors.toList());
            return StringUtil.copyPartialMatches(args[1], zoneNames, new ArrayList<>());
        }

        if (args.length == 3 && args[0].equalsIgnoreCase("flag")) {
            return StringUtil.copyPartialMatches(args[2], availableFlags, new ArrayList<>());
        }

        if (args.length == 4 && args[0].equalsIgnoreCase("flag")) {
            return StringUtil.copyPartialMatches(args[3], Arrays.asList("allow", "deny"), new ArrayList<>());
        }

        return Collections.emptyList();
    }

    private void handleHelp(Player player) {
        player.sendMessage(settingsManager.getMessage("help-header", false));
        player.sendMessage(settingsManager.getMessage("help-create", false));
        player.sendMessage(settingsManager.getMessage("help-delete", false));
        player.sendMessage(settingsManager.getMessage("help-list", false));
        player.sendMessage(settingsManager.getMessage("help-flag", false));
        player.sendMessage(settingsManager.getMessage("help-flaglist", false));
        player.sendMessage(settingsManager.getMessage("help-reload", false));
    }

    private void handleFlagList(Player player) {
        player.sendMessage(settingsManager.getMessage("flag-list-header"));
        for (String flag : availableFlags) {
            player.sendMessage(settingsManager.getMessage("flag-list-item", false).replace("{flag}", flag));
        }
    }

    private void handleCreate(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(settingsManager.getMessage("usage-create"));
            return;
        }
        Selection selection = plugin.getWorldEdit().getSelection(player);
        if (selection == null) {
            player.sendMessage(settingsManager.getMessage("no-selection"));
            return;
        }
        String zoneName = args[1].toLowerCase();

        if (zoneManager.getZoneByName(zoneName) != null) {
            return;
        }

        ProtectedZone newZone = new ProtectedZone(zoneName, player.getWorld().getName(), player.getUniqueId(),
                selection.getMinimumPoint().getX(), selection.getMinimumPoint().getY(), selection.getMinimumPoint().getZ(),
                selection.getMaximumPoint().getX(), selection.getMaximumPoint().getY(), selection.getMaximumPoint().getZ());

        zoneManager.getProtectedZones().add(newZone);
        zoneManager.saveZones();
        player.sendMessage(settingsManager.getMessage("zone-created").replace("{zone}", zoneName));
    }

    private void handleDelete(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(settingsManager.getMessage("usage-delete"));
            return;
        }
        String zoneName = args[1].toLowerCase();
        ProtectedZone zone = zoneManager.getZoneByName(zoneName);
        if (zone == null) {
            player.sendMessage(settingsManager.getMessage("zone-not-found").replace("{zone}", zoneName));
            return;
        }
        zoneManager.getProtectedZones().remove(zone);
        zoneManager.saveZones();
        player.sendMessage(settingsManager.getMessage("zone-deleted").replace("{zone}", zoneName));
    }

    private void handleList(Player player) {
        player.sendMessage(settingsManager.getMessage("zone-list-header"));
        if (zoneManager.getProtectedZones().isEmpty()) {
            player.sendMessage(settingsManager.getMessage("no-zones-defined"));
            return;
        }
        for (ProtectedZone zone : zoneManager.getProtectedZones()) {
            player.sendMessage(settingsManager.getMessage("zone-list-item", false).replace("{zone}", zone.getName()));
        }
    }

    private void handleFlag(Player player, String[] args) {
        if (args.length < 4) {
            player.sendMessage(settingsManager.getMessage("usage-flag"));
            return;
        }
        String zoneName = args[1].toLowerCase();
        ProtectedZone zone = zoneManager.getZoneByName(zoneName);
        if (zone == null) {
            player.sendMessage(settingsManager.getMessage("zone-not-found").replace("{zone}", zoneName));
            return;
        }
        String flag = args[2].toLowerCase();
        if (!availableFlags.contains(flag)) {
            player.sendMessage(settingsManager.getMessage("invalid-flag").replace("{flag}", flag));
            return;
        }
        String valueStr = args[3].toLowerCase();
        if (!valueStr.equals("allow") && !valueStr.equals("deny")) {
            player.sendMessage(settingsManager.getMessage("usage-flag"));
            return;
        }
        boolean value = valueStr.equals("deny");

        zone.setFlag(flag, value);
        zoneManager.saveZones();
        player.sendMessage(settingsManager.getMessage("flag-updated")
                .replace("{flag}", flag)
                .replace("{zone}", zoneName)
                .replace("{value}", valueStr));
    }
}
