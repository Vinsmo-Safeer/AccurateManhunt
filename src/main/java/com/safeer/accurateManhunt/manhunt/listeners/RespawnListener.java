package com.safeer.accurateManhunt.manhunt.listeners;

import com.safeer.accurateManhunt.manhunt.GameState;
import com.safeer.accurateManhunt.manhunt.MainManhuntClass;
import com.safeer.accurateManhunt.manhunt.Participant;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

public class RespawnListener implements Listener {

    private final MainManhuntClass manhuntClass;

    public RespawnListener(MainManhuntClass manhuntClass) {
        this.manhuntClass = manhuntClass;
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        if (!manhuntClass.gameState.equals(GameState.RUNNING)) return;

        Player player = event.getPlayer();

        Participant isParticipant = manhuntClass.participants.stream()
                .filter(p -> p.uniqueId().equals(player.getUniqueId()))
                .findFirst()
                .orElse(null);

        if (isParticipant == null) return;

        manhuntClass.getCompass(player);
    }
}
