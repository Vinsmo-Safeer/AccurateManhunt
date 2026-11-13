package com.safeer.accurateManhunt;

import com.safeer.accurateManhunt.manhunt.MainManhuntClass;
import com.safeer.accurateManhunt.manhunt.commands.ManhuntCommands;
import com.safeer.accurateManhunt.manhunt.commands.QuickStartManhuntCommand;
import com.safeer.accurateManhunt.manhunt.listeners.*;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class AccurateManhunt extends JavaPlugin {



    public static NamespacedKey TRACKER_KEY;

    @Override
    public void onEnable() {

        TRACKER_KEY = new NamespacedKey(this, "tracker_compass");


        MainManhuntClass manhuntClass = new MainManhuntClass(this);

        registerCommands(manhuntClass);

        registerListeners(manhuntClass);


    }

    private void registerCommands(MainManhuntClass manhuntClass) {
        getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
                    event.registrar().register(
                            ManhuntCommands.manhuntCommand(manhuntClass),
                            "Manhunt configuration"
                    );
                    event.registrar().register(
                            ManhuntCommands.compass(manhuntClass),
                            "Get tracker"
                    );
                    event.registrar().register(
                            QuickStartManhuntCommand.create(manhuntClass),
                            "Allows the player to quickly start manhunt and skip configurations"
                    );
                }
        );
    }

    private void registerListeners(MainManhuntClass manhuntClass) {
        PluginManager pm = getServer().getPluginManager();

        pm.registerEvents(new TrackerInteractionListenerLeftClick(manhuntClass), this);
        pm.registerEvents(new TrackerInteractionListenerRightClick(manhuntClass), this);
        pm.registerEvents(new TrackerMenuListener(this, manhuntClass), this);
        pm.registerEvents(new PlayerDamageListener(this, manhuntClass), this);
        pm.registerEvents(new DeathListener(manhuntClass), this);
        pm.registerEvents(new EnderDragonDeathListener(manhuntClass), this);
        pm.registerEvents(new RespawnListener(manhuntClass), this);
        pm.registerEvents(new TrackerDropEvent(), this);
        pm.registerEvents(new LocationBarListener(this, manhuntClass), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
