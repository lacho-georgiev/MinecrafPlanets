package lacho.lacho;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeleportToDimension implements CommandExecutor {

    World dimension;

    public TeleportToDimension(World dimension) {
        this.dimension = dimension;
        System.out.println("called");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            if (command.getName().contains("dimension")) {
                Player player = (Player) sender;
                player.teleport(new Location(dimension, 0, 80, 0));
                player.sendMessage("Teleported");
            }
        }
        return false;
    }
}
