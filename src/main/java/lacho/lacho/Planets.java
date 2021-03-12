package lacho.lacho;

import lacho.lacho.Listeners.Listeners;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Skull;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.Rotatable;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.FileUtil;
import org.bukkit.util.Vector;
import org.graalvm.compiler.lir.alloc.lsra.LinearScan;

public final class Planets extends JavaPlugin {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new Listeners(), this);

        Bukkit.unloadWorld("dimension", false);
        WorldCreator wc = new WorldCreator("dimension");
        wc.environment(World.Environment.NORMAL);
        wc.generator(new EmptyChunkGenerator());
        World newWorld = wc.createWorld();
        World oldWorld = Bukkit.getWorld("world");

        changeWorld(oldWorld, newWorld);

        TeleportToDimension teleportToDimension = new TeleportToDimension(newWorld);
        this.getCommand("dimension").setExecutor(teleportToDimension);
    }

    private void changeWorld(World oldWorld, World newWorld) {
        int length = 512, width = 512, height = 128, r0 = 16;
        int ySum = 0;
        for (int x = 0; x < length; x++) {
            for (int z = 0; z < width; z++) {
                for (int y = height; y > 0; y--) {
                    if (oldWorld.getBlockAt(x, y, z).getType() != Material.AIR) {
                        ySum += y;
                        break;
                    }
                }
            }
        }

        double R  = height;
//        double R = length/(2 * Math.PI) - r0; // 128
        for (int x = 0; x < length; x++) {
            for (int y = height; y > 0; y--) {
                for (int z = 0; z < width; z++) {
                    Material material = oldWorld.getBlockAt(x, y, z).getType();
                    double newX, newY, newZ;
                    double r = (R-r0) * y / (double)height + r0;
                    double phi = x * 2 * Math.PI / length;
                    double thita = z * 2 * Math.PI / width;
                    newX = r * Math.cos(phi) * Math.sin(thita);
                    newY = r * Math.sin(phi) * Math.sin(thita) + (height + r0);
                    newZ = r * Math.cos(thita);

                    newWorld.getBlockAt((int)newX, (int)newY, (int)newZ).setType(material);
                }
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            if (command.getName().equals("k")) {
                Player player = (Player) sender;
                player.setHealth(0);
                player.sendMessage("Killed");
            } else if (command.getName().equals("block")) {
                Player player = (Player) sender;

                World world = Bukkit.getWorld("world");
                Block block = world.getBlockAt(-10, 90, 0);
                BlockData blockData = block.getBlockData();
                if(blockData instanceof Directional) {
                    player.sendMessage("YES");
                    Directional directional = (Directional) blockData;

                    player.sendMessage("FACES:" + directional.getFaces());

                    directional.setFacing(BlockFace.NORTH);
                    block.setBlockData(directional);
                } else {
                    player.sendMessage("NOPE");
                }
            } else if (command.getName().equals("velocity")) {

                Player p = (Player) sender;
                p.sendRawMessage("velocity");

            } else if (command.getName().equals("rotate")) {
                Player player = (Player) sender;
                Location loc = player.getLocation();
                Location axis = player.getLocation();
                axis.setY(axis.getY() + 1);
                double angle = 90;
                Location new_loc =  rotateLocXZ(loc, axis, angle);
                player.teleport(new_loc);
            }
        }
        return false;
    }
    /*
    public Vector rotateAroundY(Location loc,double angle) {
        double angleCos = Math.cos(angle);
        double angleSin = Math.sin(angle);

        double x = angleCos * getX() + angleSin * getZ();
        double z = -angleSin * getX() + angleCos * getZ();
        return setX(x).setZ(z);
    }*/
    public static Location rotateLocXZ(Location loc, Location axis, double angle) {
        angle *= -1; // By default, we use counterclockwise rotations. Uncomment to use clcokwise rotations instead.
        //angle *= 180 / Math.PI; // By default, angle is in radians. Uncomment to use degrees instead.
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        Vector r1 = new Vector(cos, 0, -sin); // Bukkit vectors need 3 components, so set the y-component to 0
        Vector r2 = new Vector(sin, 0, cos);
        Vector v = loc.clone().subtract(axis).toVector();
        Vector rotated = new Vector(r1.dot(v), 0, r2.dot(v)); // Perform the matrix multiplication
        return rotated.add(axis.toVector()).toLocation(loc.getWorld());
    }
    
}
