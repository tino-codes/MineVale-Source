/*
    --------------------------
    Projeckt : minevale
    Package : de.bufferoverflw.minevale.utils
    Developer : BufferOverflw
    --------------------------
*/


package de.bufferoverflw.minevale.utils;

import de.bufferoverflw.minevale.Minevale;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

public class Message {

    private CommandSender sender;
    private String prefix = Minevale.getInstance().getConfig().getString("message.prefix");
    private FileConfiguration cfg = Minevale.getInstance().getConfig();

    public Message(CommandSender sender) {
        this.sender = sender;
    }

    public void sendNoPermission() {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + " " + cfg.getString("message.nopermission")));
    }

    public void sendOnlyPlayer() {

        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + " " + cfg.getString("message.onlyplayer")));

    }

    public void sendMessage(String path, String[] before, String[] after) {

        String outputMessage = "";
        String message = cfg.getString("message." + path);

        for (int i = 0; i < before.length; i++) {

            outputMessage = message.replace(before[i], after[i]);

        }

        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + " " + outputMessage));

    }

    public void sendMessage(String path) {

        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + " " + cfg.getString("message." + path)));

    }

    public void sendUsage(String usage) {

        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + " " +
                cfg.getString("message.usage").replace("%command%", usage)));

    }
}
