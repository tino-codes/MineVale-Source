/*
    --------------------------
    Projeckt : minevale
    Package : de.bufferoverflw.minevale.commands
    Developer : BufferOverflw
    --------------------------
*/


package de.bufferoverflw.minevale.commands;

import com.plotsquared.core.PlotSquared;
import com.plotsquared.core.configuration.Configuration;
import com.plotsquared.core.location.Location;
import com.plotsquared.core.player.PlotPlayer;
import com.plotsquared.core.plot.Plot;
import com.plotsquared.core.plot.PlotArea;
import de.bufferoverflw.minevale.Minevale;
import de.bufferoverflw.minevale.utils.Message;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BlockIterator;

import java.util.Objects;

public class BreakBlockCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            new Message(sender).sendOnlyPlayer();
            return true;
        }

        if (!sender.hasPermission(Objects.requireNonNull(Minevale.getInstance().getConfig().getString("message.commands.breakblock.permission")))) {
            new Message(sender).sendNoPermission();
            return true;
        }

        Player player = (Player) sender;

        if (player.getEyeLocation().getBlock() == null) {

            new Message(player).sendMessage("commands.breakblock.noblock");
            return true;
        }


        PlotArea plotArea = PlotSquared.get().getPlotAreaManager().getPlotArea(Location.at(player.getWorld().getName(), player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ()));

        if (plotArea == null) {

            new Message(player).sendMessage("commands.breakblock.noplot");
            return true;

        }

        Plot ownedPlot = plotArea.getOwnedPlot(Location.at(player.getWorld().getName(), player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ()));

        if (ownedPlot == null) {
            new Message(player).sendMessage("commands.breakblock.noplot");
            return true;
        }

        if (!ownedPlot.getOwners().contains(player.getUniqueId())) {
            new Message(player).sendMessage("commands.breakblock.noplot");
            return true;
        }

        if (player.getInventory().contains(new ItemStack(Material.AIR))) {
            new Message(player).sendMessage("commands.breakblock.inventoryfull");
            return true;
        }

        Block block = getTargetBlock(player);

        if (block.getLocation().getBlockY() <= 0) {
            new Message(player).sendMessage("commands.breakblock.noplot");
            return true;
        }

        Location location = Location.at(player.getWorld().getName(), block.getLocation().getBlockX(), block.getLocation().getBlockY(), block.getLocation().getBlockZ());
        if (location.isPlotRoad()) {
            new Message(player).sendMessage("commands.breakblock.noplot");
            return true;
        }

        ItemStack item = new ItemStack(block.getType());



        block.setType(Material.AIR);

        player.getInventory().addItem(item);

        new Message(player).sendMessage("commands.breakblock.blockbreaked", new String[]{"%block%"}, new String[]{item.getType().name()});

        return true;
    }

    public final Block getTargetBlock(Player player) {
        BlockIterator iter = new BlockIterator(player, 3);
        Block lastBlock = iter.next();
        while (iter.hasNext()) {
            lastBlock = iter.next();
            if (lastBlock.getType() == Material.AIR) continue;
            break;
        }
        return lastBlock;
    }
}