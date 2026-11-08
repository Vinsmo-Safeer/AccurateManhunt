package com.safeer.accurateManhunt.manhunt.listeners;

import com.safeer.accurateManhunt.manhunt.MainManhuntClass;
import com.safeer.accurateManhunt.manhunt.Speedrunner;
import com.safeer.accurateManhunt.manhunt.inventories.TrackerMenu;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;

public class TrackerInteractionListenerRightClick implements Listener {


    private final MainManhuntClass manhuntClass;

    public TrackerInteractionListenerRightClick(MainManhuntClass manhuntClass) {
        this.manhuntClass = manhuntClass;
    }

    @EventHandler
    public void onClick(PlayerInteractEvent event) {
        var action = event.getAction();
        if (event.getMaterial() != Material.COMPASS || !action.isRightClick()) return;

        List<Speedrunner> speedrunners = new ArrayList<>(manhuntClass.speedrunners);

        Inventory SpeedrunnerSelector = new TrackerMenu(speedrunners).getInventory();

        event.getPlayer().openInventory(SpeedrunnerSelector);
    }
}