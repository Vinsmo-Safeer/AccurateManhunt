package com.safeer.accurateManhunt.manhunt.listeners;

import com.safeer.accurateManhunt.AccurateManhunt;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.inventory.meta.ItemMeta;

public class TrackerDropEvent implements Listener {

    @EventHandler
    public void onItemSpawn(ItemSpawnEvent event) {

        Item item = event.getEntity();

        ItemMeta meta = item.getItemStack().getItemMeta();

        if (meta.getPersistentDataContainer().has(AccurateManhunt.TRACKER_KEY)) {
            item.remove();
        }

    }
}
