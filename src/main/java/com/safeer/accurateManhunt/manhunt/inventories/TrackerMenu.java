package com.safeer.accurateManhunt.manhunt.inventories;

import com.safeer.accurateManhunt.manhunt.Speedrunner;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class TrackerMenu implements InventoryHolder {

    private static final int SIZE = 9;

    private final Inventory inventory;
    private final List<Speedrunner> speedrunners;

    private List<ItemStack> heads;

    public TrackerMenu(List<Speedrunner> speedrunners) {
        this.speedrunners = speedrunners;
        this.inventory = Bukkit.createInventory(this, SIZE, "Speedrunners");

        getHeads();


        for (int i = 0; i<heads.size(); i++) {
            inventory.addItem(heads.get(i));
        }
    }

    private void getHeads() {

        heads = new ArrayList<>();

        for (Speedrunner speedrunner : speedrunners) {
            UUID uuid = speedrunner.getUniqueId();
            ItemStack head = new ItemStack(Material.PLAYER_HEAD);
            head.editMeta(SkullMeta.class, meta -> {
                meta.setOwningPlayer(Bukkit.getOfflinePlayer(uuid));
            });

            heads.add(head);
        }
    }

    public @Nullable Speedrunner getSpeedrunnerAt(int slot) {
        if (slot < 0 || slot >= speedrunners.size()) return null;
        return speedrunners.get(slot);
    }


    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }
}
