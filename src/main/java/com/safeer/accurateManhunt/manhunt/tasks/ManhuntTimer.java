package com.safeer.accurateManhunt.manhunt.tasks;

import com.safeer.accurateManhunt.manhunt.GameState;
import com.safeer.accurateManhunt.manhunt.MainManhuntClass;
import com.safeer.accurateManhunt.manhunt.Speedrunner;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

import static com.safeer.accurateManhunt.AccurateManhunt.TRACKER_KEY;

public final class ManhuntTimer extends BukkitRunnable {

    private final MainManhuntClass manhuntClass;


    private final long startedAt;

    private static final long HALF_HOUR_MS = 30L * 60 * 1000;
    private long lastIntervalAnnounced = 0;


    public ManhuntTimer(MainManhuntClass manhuntClass, long startedAt, Map<UUID, Speedrunner> hunters) {
        this.manhuntClass = manhuntClass;
        this.startedAt = startedAt;
    }


    @Override
    public void run() {

        if (!(manhuntClass.gameState == GameState.RUNNING)) {
            cancel();
            return;
        }

        long elapsed = System.currentTimeMillis() - startedAt;
        long interval = elapsed / HALF_HOUR_MS;

        if (interval > lastIntervalAnnounced) {
            lastIntervalAnnounced = interval;

            long totalMinutes = interval * 30;
            long hours = totalMinutes / 60;
            long minutes = totalMinutes % 60;

            String time = (hours > 0 ? hours + "h " : "") + (minutes > 0 || hours == 0 ? minutes + "m" : "");

            Bukkit.broadcast(Component.text("Time elapsed: " + time.trim(), NamedTextColor.YELLOW));
        }




        for (Map.Entry<UUID, Speedrunner> entry : manhuntClass.hunters.entrySet()) {

            UUID key = entry.getKey();
            Speedrunner value = entry.getValue();

            if (key == null) continue;


            var hunter = Bukkit.getPlayer(key);
            if (hunter == null) continue;

            if (value != null && value.getUniqueId() != null) {

                var speedrunner = Bukkit.getPlayer(value.getUniqueId());

                if (speedrunner != null) {
                    getCompass(speedrunner, hunter);
                    continue;
                }

            }

            var speedrunner = manhuntClass.getFirstSpeedrunner();
            if (speedrunner == null) {
                hunter.sendMessage(Component.text("Unable to find speedrunner (Might be offline or unassigned)."));
                continue;
            }

            getCompass(speedrunner, hunter);
        }
    }




    public void getCompass(Player speedrunner, Player hunter) {



        var ref = new Object() {
            Location speedrunnerLocation = speedrunner.getLocation();
        };
        var hunterLocation = hunter.getLocation();

        manhuntClass.updateSpeedrunnerLocation(speedrunner);

        if (!ref.speedrunnerLocation.getWorld().getName().equals(hunterLocation.getWorld().getName())) {
            var speedrunnerClass = manhuntClass.getSpeedrunnerClass(speedrunner);

            if (speedrunnerClass == null) {
                hunter.sendMessage(Component.text("Cannot find speedrunner!", NamedTextColor.RED));
                return;
            }
            var speedrunnerLastKnownLocation = speedrunnerClass.getLastOverWorldLocation(hunterLocation.getWorld().getName());
            if (speedrunnerLastKnownLocation == null) {
                hunter.sendMessage(Component.text("Cannot find speedrunner's last know location (speedrunner might be in another dimension)."));
                return;
            }
            ref.speedrunnerLocation = speedrunnerLastKnownLocation;
        }

        var hunterInventory = hunter.getInventory();

        for (int i=0;i<hunterInventory.getSize();i++) {
            ItemStack item = hunterInventory.getItem(i);
            if (item == null || item.getType() != Material.COMPASS) continue;

            CompassMeta meta = (CompassMeta) item.getItemMeta();
            if (!meta.getPersistentDataContainer().has(TRACKER_KEY, PersistentDataType.BYTE)) continue;

            meta.setLodestone(ref.speedrunnerLocation);
            meta.setLodestoneTracked(false);
            item.setItemMeta(meta);

            return;
        }

        Bukkit.getLogger().info("No tracker compass found for " + hunter.getName());

    }
}
