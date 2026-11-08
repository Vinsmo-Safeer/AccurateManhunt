package com.safeer.accurateManhunt.manhunt.listeners;

import com.safeer.accurateManhunt.AccurateManhunt;
import com.safeer.accurateManhunt.manhunt.GameState;
import com.safeer.accurateManhunt.manhunt.MainManhuntClass;
import com.safeer.accurateManhunt.manhunt.Participant;
import com.safeer.accurateManhunt.manhunt.Team;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class PlayerDamageListener implements Listener {

    private final AccurateManhunt plugin;

    private final MainManhuntClass manhuntClass;

    public PlayerDamageListener(AccurateManhunt plugin, MainManhuntClass manhuntClass) {
        this.plugin = plugin;
        this.manhuntClass = manhuntClass;
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent event) {

        if (manhuntClass.gameState.equals(GameState.COUNTDOWN)) {
            event.setCancelled(true);
            event.getDamager().sendActionBar(Component.text("Wait for hunters to get ready!", NamedTextColor.RED));
            return;
        }

        if (!manhuntClass.gameState.equals(GameState.PREPARED)) return;

        if (!(event.getEntity() instanceof Player victim)) return;
        if (!(event.getDamager() instanceof Player attacker)) return;

        var victimId = victim.getUniqueId();
        var attackerId = attacker.getUniqueId();


        for (Participant participant : manhuntClass.participants) {

            if (participant.team().equals(Team.SPEEDRUNNER)) {

                if (participant.uniqueId().equals(victimId)) {
                    event.setCancelled(true);
                    return;
                }

                if (participant.uniqueId().equals(attackerId)) {
                    plugin.getServer().getScheduler().runTask(plugin, manhuntClass::start);
                    return;
                }

            }

        }

        event.setCancelled(true);


    }
}
