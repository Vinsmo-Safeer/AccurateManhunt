package com.safeer.accurateManhunt.manhunt.listeners;

import com.safeer.accurateManhunt.manhunt.MainManhuntClass;
import com.safeer.accurateManhunt.manhunt.Speedrunner;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Map;
import java.util.UUID;

public class TrackerInteractionListenerLeftClick implements Listener {

    private final MainManhuntClass manhuntClass;

    public TrackerInteractionListenerLeftClick(MainManhuntClass manhuntClass) {
        this.manhuntClass = manhuntClass;
    }

    @EventHandler
    public void onClick(PlayerInteractEvent event) {
        var action = event.getAction();
        if (event.getMaterial() != Material.COMPASS || !action.isLeftClick()) return;

        var player = event.getPlayer();
        var playerId = player.getUniqueId();

        if (!manhuntClass.hunters.containsKey(playerId)) {

            player.sendMessage(Component.text("You are not registered as a hunter!", NamedTextColor.YELLOW));
            return;
        }

        var speedrunnerClass = manhuntClass.hunters.get(playerId);

        if (speedrunnerClass == null) {

            speedrunnerClass = manhuntClass.speedrunners.stream().findFirst().orElse(null);

            if (speedrunnerClass == null) {
                player.sendMessage(Component.text("Cannot find speedrunner. Make sure speedrunner is assigned!", NamedTextColor.RED));
                return;
            }
        }

        var speedrunner = Bukkit.getPlayer(speedrunnerClass.getUniqueId());

        if (speedrunner == null) {
            player.sendMessage(Component.text("Cannot find speedrunner (Might be offline)."));
            return;
        }

        manhuntClass.getCompass(speedrunner, player, false);

        Component actionBar = Component.text("Calibrated to ", NamedTextColor.YELLOW)
                .append(Component.text(speedrunner.getName(), NamedTextColor.AQUA));

        player.sendActionBar(actionBar);

    }
}
