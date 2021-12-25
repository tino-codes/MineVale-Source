/*
    --------------------------
    Projeckt : minevale
    Package : de.bufferoverflw.minevale.commands
    Developer : BufferOverflw
    --------------------------
*/


package de.bufferoverflw.minevale.commands;

import de.bufferoverflw.minevale.Minevale;
import de.bufferoverflw.minevale.utils.Message;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class SignCommand implements CommandExecutor {


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player player)) {
            new Message(sender).sendOnlyPlayer();
            return true;
        }

        if (!player.hasPermission(Minevale.getInstance().getConfig().getString("message.commands.sign.permission"))) {
            new Message(player).sendNoPermission();
            return true;
        }

        if (args.length >= 1) {

            if (!player.hasPermission(Minevale.getInstance().getConfig().getString("message.commands.sign.bypasspermission"))) {
                if (!Minevale.getCooldownConfig().contains("signcooldown." + player.getUniqueId() + ".hour")) {
                    Minevale.getCooldownConfig().set("signcooldown." + player.getUniqueId() + ".hour", System.currentTimeMillis());
                    Minevale.saveCooldownConfig();
                } else {

                    long playerTime = Minevale.getCooldownConfig().getLong("signcooldown." + player.getUniqueId() + ".hour") + 14400000L;
                    long currentTime = System.currentTimeMillis();

                    if (playerTime > currentTime) {
                        new Message(player).sendMessage("commands.sign.cooldown");
                        return true;
                    }


                }
            }


            StringBuilder message = new StringBuilder();
            for (int i = 0; i < args.length; i++) {

                message.append(args[i]).append(" ");

            }

            if (player.getInventory().getItemInMainHand() == null || player.getInventory().getItemInMainHand().getType() == Material.AIR) {
                new Message(player).sendMessage("commands.sign.noitem");
                return true;
            }

            ItemStack item = player.getInventory().getItemInMainHand();

            ItemMeta meta = item.getItemMeta();

            List<String> lore = new ArrayList<>();

            if (Minevale.getInstance().getConfig().getStringList("message.commands.sign.lore") != null) {

                List<String> configLore = Minevale.getInstance().getConfig().getStringList("message.commands.sign.lore");

                configLore.forEach(s -> {

                    lore.add(ChatColor.translateAlternateColorCodes('&',
                            s.replace("%player%", player.getName())
                                    .replace("%date%", new SimpleDateFormat("dd.MM.yyyy").format(Calendar.getInstance().getTime()))
                                    .replace("%sign%", message.toString())));

                });

            } else throw new NullPointerException("error in config.yml!");

            assert meta != null;
            meta.setLore(lore);

            item.setItemMeta(meta);

            player.getInventory().setItemInMainHand(item);

            new Message(player).sendMessage("commands.sign.signed");

        } else
            new Message(player).sendUsage("sign <message>");


        return true;
    }
}