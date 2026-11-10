package com.safeer.accurateManhunt.manhunt.listeners;

import com.safeer.accurateManhunt.manhunt.GameState;
import com.safeer.accurateManhunt.manhunt.MainManhuntClass;
import com.safeer.accurateManhunt.manhunt.Speedrunner;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.time.Duration;

public record DeathListener(MainManhuntClass manhuntClass) implements Listener {


    @EventHandler
    public void onDeath(PlayerDeathEvent event) {



        if (!manhuntClass.gameState.equals(GameState.RUNNING)) return;

        var player = event.getPlayer();
        var playerId = player.getUniqueId();

        boolean isSpeedrunner = false;

        for (Speedrunner speedrunner : manhuntClass.speedrunners) {
            if (speedrunner.getUniqueId().equals(playerId)) {
                isSpeedrunner = true;
                break;
            }
        }

        if (!isSpeedrunner) return;


        Component deathMessage = event.deathMessage();

        event.setCancelled(true);

        if (deathMessage != null) {
            Bukkit.broadcast(deathMessage);
        }

        var playerLocation = player.getLocation();

        if (!event.getKeepInventory()) {
            for (ItemStack item : event.getDrops()) {
                player.getWorld().dropItemNaturally(
                        playerLocation,
                        item
                );
            }

            event.getDrops().clear();
        }

        player.getWorld().playSound(playerLocation, Sound.ENTITY_WITHER_DEATH, 0.5f, 1f);


        player.setGameMode(GameMode.SPECTATOR);

        player.showTitle(Title.title(
                Component.text("You Lose.", NamedTextColor.RED),
                Component.empty(),
                Title.Times.times(Duration.ofMillis(300), Duration.ofSeconds(4), Duration.ofSeconds(1))
        ));

        manhuntClass.speedrunnerDied(playerId);

    }

}
