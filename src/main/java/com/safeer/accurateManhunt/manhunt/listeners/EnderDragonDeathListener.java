package com.safeer.accurateManhunt.manhunt.listeners;

import com.safeer.accurateManhunt.manhunt.GameState;
import com.safeer.accurateManhunt.manhunt.MainManhuntClass;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class EnderDragonDeathListener implements Listener {

    private final MainManhuntClass manhuntClass;

    public EnderDragonDeathListener(MainManhuntClass manhuntClass) {
        this.manhuntClass = manhuntClass;
    }


    @EventHandler
    public void onDragonDeath(EntityDeathEvent event) {
        if (!manhuntClass.gameState.equals(GameState.RUNNING)) return;
        Entity entity = event.getEntity();
        if (!entity.getType().equals(EntityType.ENDER_DRAGON)) return;

        manhuntClass.gameOver();
    }
}
