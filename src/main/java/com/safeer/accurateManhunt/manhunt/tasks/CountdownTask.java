package com.safeer.accurateManhunt.manhunt.tasks;

import com.safeer.accurateManhunt.AccurateManhunt;
import com.safeer.accurateManhunt.manhunt.MainManhuntClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;

public class CountdownTask extends BukkitRunnable {


    private final MainManhuntClass manhuntClass;

    private final List<Player> hunters;
    private final long countDownStartTime;

    private final double distanceLimit = 5.0;

    private BukkitTask task;



    public CountdownTask(
            AccurateManhunt plugin,
            MainManhuntClass manhuntClass,
            List<Player> hunters,
            long countDownStartTime,
            Location speedrunnerLocation) {
        this.manhuntClass = manhuntClass;
        this.hunters = hunters;
        this.countDownStartTime = countDownStartTime;

        task = new BukkitRunnable() {
            @Override
            public void run() {

                for (Player player : hunters) {

                    if (!player.isOnline()) continue;

                    if (player.getLocation().distanceSquared(speedrunnerLocation) > distanceLimit * distanceLimit) {

                        player.teleport(speedrunnerLocation);
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 5L);
    }


    @Override
    public void run() {

        long elapsed = System.currentTimeMillis() -  countDownStartTime;
        long countDownMillis = MainManhuntClass.CountDownSeconds * 1000L;

        long secondsLeft = (long) Math.ceil((countDownMillis - elapsed) / 1000.0);

        if (secondsLeft < 0) {
            for (Player player : hunters) {

                if (player == null) continue;

                player.setFlying(false);
                player.setAllowFlight(false);

                player.setGameMode(GameMode.SURVIVAL);
            }

            manhuntClass.prepare();

            task.cancel();
            task = null;
            cancel();

            return;
        }

        for (Player player : hunters) {

            if (player == null) continue;

            Title title = Title.title(Component.text(secondsLeft + "(s) left", NamedTextColor.RED),
                    Component.text("to start manhunt", NamedTextColor.WHITE), 5, 20, 0);

            player.showTitle(title);
        }

    }
}
