package com.safeer.accurateManhunt.manhunt.listeners;

import com.safeer.accurateManhunt.manhunt.MainManhuntClass;
import com.safeer.accurateManhunt.manhunt.Speedrunner;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPortalEnterEvent;

public class PortalListener implements Listener {

    private final MainManhuntClass manhuntClass;

    public PortalListener(MainManhuntClass manhuntClass) {
        this.manhuntClass = manhuntClass;
    }

    @EventHandler
    public void onPortalEnter(EntityPortalEnterEvent event) {

        if (!(event.getEntity() instanceof Player player)) return;

        var playerId = player.getUniqueId();

        for (Speedrunner speedrunner : manhuntClass.speedrunners) {
            if (speedrunner.getUniqueId().equals(playerId)) {
                speedrunner.setLocation(player.getLocation());
                break;
            }
        }
    }
}
