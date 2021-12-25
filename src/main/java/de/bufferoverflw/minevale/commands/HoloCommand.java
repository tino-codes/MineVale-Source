/*
    --------------------------
    Projeckt : minevale
    Package : de.bufferoverflw.minevale.commands
    Developer : BufferOverflw
    --------------------------
*/


package de.bufferoverflw.minevale.commands;

import com.plotsquared.core.PlotSquared;
import com.plotsquared.core.location.Location;
import com.plotsquared.core.plot.Plot;
import com.plotsquared.core.plot.PlotArea;
import de.bufferoverflw.minevale.Minevale;
import de.bufferoverflw.minevale.utils.Message;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class HoloCommand implements CommandExecutor, TabCompleter {
    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> list = new ArrayList<>();

        if (args.length == 0) return list;

        if (args.length == 1) {
            list.add("create");
            list.add("delete");
            list.add("edit");
        }

        if (args.length == 2) {

            if (!(sender instanceof Player)) return list;

            Player player = (Player) sender;

            if (args[0].equals("create")) {
                list.add("[HoloName]");
            }

            if (args[0].equals("delete") || args[0].equals("edit")) {

                ConfigurationSection configurationSection = Minevale.getHoloConfig().getConfigurationSection(player.getUniqueId() + "");

                if (configurationSection == null) {
                    return list;
                }

                Set<String> keys = configurationSection.getKeys(false);

                if (keys == null) return list;

                if (keys.isEmpty()) return list;

                keys.forEach(s -> {

                    list.add(Minevale.getHoloConfig().getString(player.getUniqueId() + "." + s + ".name"));

                });
            }
        }

        if (args.length == 3) {
            if (args[0].equals("create") || args[0].equals("edit")) {
                list.add("[HoloText]");
            }
        }

        String current = args[args.length - 1];
        List<String> completer = new ArrayList<>();
        list.forEach(s -> {

            if (s == null) return;

            if (s.startsWith(current)) completer.add(s);

        });
        return completer;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            new Message(sender).sendOnlyPlayer();
            return true;
        }

        if (!sender.hasPermission(Minevale.getInstance().getConfig().getString("message.commands.plotholo.permission"))) {
            new Message(sender).sendNoPermission();
            return true;
        }

        Player player = (Player) sender;

        // /holo create <name> <displayText>
        // /holo delete <name>

        if (args.length >= 2) {

            switch (args[0].toLowerCase(Locale.ROOT)) {

                case "create": {

                    if (args.length >= 3) {

                        String holoName = args[1];
                        String holoText = "";

                        for (int i = 2; i < args.length; i++) {
                            holoText = String.valueOf(holoText + " " + args[i]);
                        }


                        if (Minevale.getHoloConfig().contains(player.getUniqueId() + "." + holoName + ".uuid")) {
                            new Message(player).sendMessage("commands.plotholo.holoexist", new String[]{"%holoname%"}, new String[]{holoName});
                            return true;
                        }


                        PlotArea plotArea = PlotSquared.get().getPlotAreaManager().getPlotArea(Location.at(player.getWorld().getName(), player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ()));

                        if (plotArea == null) {

                            new Message(player).sendMessage("commands.plotholo.noplot");
                            return true;

                        }

                        Plot ownedPlot = plotArea.getOwnedPlot(Location.at(player.getWorld().getName(), player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ()));

                        if (ownedPlot == null || !ownedPlot.getOwners().contains(player.getUniqueId())) {
                            new Message(player).sendMessage("commands.plotholo.noplot");
                            return true;
                        }

                        LivingEntity entity = (LivingEntity) player.getWorld().spawnEntity(player.getLocation(), EntityType.ARMOR_STAND);

                        entity.setCustomNameVisible(true);
                        entity.setCustomName(ChatColor.translateAlternateColorCodes('&', holoText));

                        entity.setInvulnerable(true);
                        entity.setGravity(false);
                        entity.setSilent(true);
                        entity.setVisualFire(false);
                        entity.setInvisible(true);
                        entity.setAI(false);

                        Minevale.getHoloConfig().set(player.getUniqueId() + "." + holoName + ".uuid", entity.getUniqueId().toString());
                        Minevale.getHoloConfig().set(player.getUniqueId() + "." + holoName + ".name", holoName);
                        Minevale.saveHoloConfig();

                        new Message(player).sendMessage("commands.plotholo.holocreated", new String[]{"%holoname%"}, new String[]{holoName});

                    } else
                        new Message(player).sendUsage("plotholo create <Name> <Text>");

                    break;
                }

                case "edit": {

                    if (args.length >= 3) {

                        String holoName = args[1];
                        String holoText = "";

                        for (int i = 2; i < args.length; i++) {
                            holoText = String.valueOf(holoText + " " + args[i]);
                        }


                        if (!Minevale.getHoloConfig().contains(player.getUniqueId() + "." + holoName + ".uuid")) {
                            new Message(player).sendMessage("commands.plotholo.holonotexist", new String[]{"%holoname%"}, new String[]{holoName});
                            return true;
                        }


                        List<Entity> nearbyEntities = player.getNearbyEntities(3, 3, 3);

                        if (nearbyEntities.isEmpty()) {
                            new Message(player).sendMessage("commands.plotholo.holonofound");
                            return true;
                        }

                        for (Entity nearbyEntity : nearbyEntities) {

                            if (nearbyEntity.getType() != EntityType.ARMOR_STAND) continue;

                            if (Minevale.getHoloConfig().getString(player.getUniqueId() + "." + holoName + ".uuid") == null) {
                                new Message(player).sendMessage("commands.plotholo.holonotexist", new String[]{"%holoname%"}, new String[]{holoName});
                                return true;
                            }

                            UUID entityUUID = UUID.fromString(Minevale.getHoloConfig().getString(player.getUniqueId() + "." + holoName + ".uuid"));

                            if (!nearbyEntity.getUniqueId().equals(entityUUID)) {
                                new Message(player).sendMessage("commands.plotholo.holonofound");
                            }

                            nearbyEntity.setCustomName(ChatColor.translateAlternateColorCodes('&', holoText));

                            new Message(player).sendMessage("commands.plotholo.holotextedit");

                        }


                    } else
                        new Message(player).sendUsage("plotholo create <Name> <Text>");

                    break;
                }

                case "delete": {

                    String holoName = args[1];

                    if (!Minevale.getHoloConfig().contains(player.getUniqueId() + "." + holoName + ".uuid")) {
                        new Message(player).sendMessage("commands.plotholo.holonotexist", new String[]{"%holoname%"}, new String[]{holoName});
                        return true;
                    }

                    String uuidString = Minevale.getHoloConfig().getString(player.getUniqueId() + "." + holoName + ".uuid");

                    for (LivingEntity livingEntity : player.getWorld().getLivingEntities()) {

                        if (livingEntity.getUniqueId().toString().equals(uuidString)) {

                            livingEntity.remove();
                            new Message(player).sendMessage("commands.plotholo.holodeleted", new String[]{"%holoname%"}, new String[]{holoName});
                            Minevale.getHoloConfig().set(player.getUniqueId() + "." + holoName + ".uuid", null);
                            Minevale.getHoloConfig().set(player.getUniqueId() + "." + holoName + ".name", null);
                            Minevale.saveHoloConfig();

                        }

                    }
                    break;
                }

                default:
                    List<String> helpMessages = Minevale.getInstance().getConfig().getStringList("message.commands.plotholo.usage");

                    helpMessages.forEach(s -> {

                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', s.replace("%prefix%", Minevale.getInstance().getConfig().getString("message.prefix"))));

                    });
                    break;

            }

        } else {

            List<String> helpMessages = Minevale.getInstance().getConfig().getStringList("message.commands.plotholo.usage");

            helpMessages.forEach(s -> {


                player.sendMessage(ChatColor.translateAlternateColorCodes('&', s.replace("%prefix%", Minevale.getInstance().getConfig().getString("message.prefix"))));

            });

        }

        return true;
    }
}