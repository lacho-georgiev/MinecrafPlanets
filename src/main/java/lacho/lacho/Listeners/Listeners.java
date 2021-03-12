package lacho.lacho.Listeners;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

public class Listeners implements Listener {
    long lastPrintTime = 0;
    long lastTime = 0;

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        long currTime = System.currentTimeMillis();
        if (lastTime == 0) {
            lastTime = currTime;
            lastPrintTime = currTime;
            return;
        }
        long g = 18;
        double dt = (currTime - lastTime) / 1000.0;
        Player player = event.getPlayer();
        Vector currV = player.getVelocity();
        Vector newV;
        Vector r2 = new Vector(0,192,0);
        Vector r21 = r2.subtract(currV);
        Vector e = r21.normalize();
        e.multiply(-g*dt);
        newV = currV.add(e);
        Location loc = player.getLocation();
        if (currTime - lastPrintTime > 1000) {
//            System.out.println("dt: " + dt + "; r:" + loc.toString() + "; V:" + newV.toString());
            lastPrintTime = currTime;
        }

        player.setVelocity(newV);
        lastTime = currTime;
    }
}
