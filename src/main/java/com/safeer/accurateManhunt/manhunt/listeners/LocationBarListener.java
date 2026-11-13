package com.safeer.accurateManhunt.manhunt.listeners;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.safeer.accurateManhunt.AccurateManhunt;
import com.safeer.accurateManhunt.manhunt.GameState;
import com.safeer.accurateManhunt.manhunt.MainManhuntClass;
import com.safeer.accurateManhunt.manhunt.Team;
import net.minecraft.network.protocol.game.ClientboundTrackedWaypointPacket;
import net.minecraft.world.waypoints.TrackedWaypoint;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.awt.*;

public class LocationBarListener implements Listener {

    private final AccurateManhunt instance;
    private final MainManhuntClass manhuntClass;


    public LocationBarListener(AccurateManhunt instance, MainManhuntClass manhuntClass) {
        this.instance = instance;
        this.manhuntClass = manhuntClass;

        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(
                instance,
                PacketType.Play.Server.TRACKED_WAYPOINT
        ) {
            @Override
            public void onPacketSending(PacketEvent event) {

                if (!manhuntClass.gameState.equals(GameState.RUNNING)) return;

                PacketContainer packetContainer = event.getPacket().deepClone();
                ClientboundTrackedWaypointPacket packet = (ClientboundTrackedWaypointPacket) packetContainer.getHandle();
                TrackedWaypoint tracked = packet.waypoint();

                tracked.id().ifLeft(uuid -> {
                    Player viewer = event.getPlayer();
                    if (uuid.equals(viewer.getUniqueId())) return;

                    Player target = Bukkit.getPlayer(uuid);
                    if (target == null || !target.isOnline()) return;

                    Team viewerTeam = manhuntClass.getTeam(viewer.getUniqueId());
                    Team targetTeam = manhuntClass.getTeam(target.getUniqueId());

                    if (targetTeam == null) {
                        event.setCancelled(true);
                    }
                    else if (!targetTeam.equals(viewerTeam)) {
                        event.setCancelled(true);
                    }
                });
            }
        });
    }
}
