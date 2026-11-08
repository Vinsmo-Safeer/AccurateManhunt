package com.safeer.accurateManhunt;

import com.safeer.accurateManhunt.manhunt.MainManhuntClass;
import com.safeer.accurateManhunt.manhunt.commands.ManhuntCommands;
import com.safeer.accurateManhunt.manhunt.commands.QuickStartManhuntCommand;
import com.safeer.accurateManhunt.manhunt.listeners.*;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

public final class AccurateManhunt extends JavaPlugin {


    /*
    * TODO:
    *  Save the location of the portal when speedrunner enters to it
    *  Remind players of the time every half an hour - SET
    *  permissions
    */



    public static NamespacedKey TRACKER_KEY;

    @Override
    public void onEnable() {

        TRACKER_KEY = new NamespacedKey(this, "tracker_compass");


        MainManhuntClass manhuntClass = new MainManhuntClass(this);

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

        getServer().getPluginManager().registerEvents(new TrackerInteractionListenerLeftClick(manhuntClass), this);
        getServer().getPluginManager().registerEvents(new TrackerInteractionListenerRightClick(manhuntClass), this);
        getServer().getPluginManager().registerEvents(new TrackerMenuListener(this, manhuntClass), this);
        getServer().getPluginManager().registerEvents(new PlayerDamageListener(this, manhuntClass), this);
        getServer().getPluginManager().registerEvents(new DeathListener(manhuntClass), this);




    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
