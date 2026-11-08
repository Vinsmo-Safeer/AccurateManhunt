package com.safeer.accurateManhunt.manhunt.listeners;

import com.safeer.accurateManhunt.AccurateManhunt;
import com.safeer.accurateManhunt.manhunt.MainManhuntClass;
import com.safeer.accurateManhunt.manhunt.Speedrunner;
import com.safeer.accurateManhunt.manhunt.inventories.TrackerMenu;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class TrackerMenuListener implements Listener {

    private final AccurateManhunt plugin;

    private final MainManhuntClass manhuntClass;

    public TrackerMenuListener(AccurateManhunt plugin, MainManhuntClass manhuntClass) {
        this.plugin = plugin;
        this.manhuntClass = manhuntClass;
    }

    @EventHandler
    public void onMenuClick(InventoryClickEvent event) {

        if (!(event.getInventory().getHolder() instanceof TrackerMenu menu)) return;

        if (!(event.getWhoClicked() instanceof Player player)) return;

        Speedrunner speedrunner = menu.getSpeedrunnerAt(event.getSlot());

        manhuntClass.changeTrackingPlayerOf(player.getUniqueId(), speedrunner);
        player.sendActionBar(Component.text("Changed tracking player", NamedTextColor.GREEN));

        event.setCancelled(true);

        Bukkit.getScheduler().runTask(plugin, () -> player.closeInventory());
    }
}
