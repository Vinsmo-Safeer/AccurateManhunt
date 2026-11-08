package com.safeer.accurateManhunt.manhunt.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.safeer.accurateManhunt.manhunt.MainManhuntClass;
import com.safeer.accurateManhunt.manhunt.Participant;
import com.safeer.accurateManhunt.manhunt.Team;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class QuickStartManhuntCommand {




    public static LiteralCommandNode<CommandSourceStack> create(MainManhuntClass manhuntClass) {
        return Commands.literal("quickstartmanhunt")
                .requires(sourceStack -> sourceStack.getSender().hasPermission("accurate-manhunt.manhunt.use"))
                .then(Commands.argument("targets", ArgumentTypes.players())
                        .executes(context -> {



                            manhuntClass.resetEverySettings();


                            var target = ManhuntCommands.resolveTargets(context);

                            manhuntClass.addParticipant(target, Team.SPEEDRUNNER);

                            List<Player> hunters = new ArrayList<>(Bukkit.getOnlinePlayers());
                            hunters.removeAll(target);

                            manhuntClass.addParticipant(hunters, Team.HUNTER);

                            manhuntClass.healClearRevokeAdvancementAndSetSurvivalOfAllPlayers();

                            target.getFirst().getWorld().setTime(0);

                            target.getFirst().getWorld().setSpawnLocation(target.getFirst().getLocation());

                            if (manhuntClass.startCountdown(MainManhuntClass.CountDownSeconds) == 0) return 0;

                            var speedrunnersList = Component.text()
                                    .append(Component.text("Speedrunner(s): ", NamedTextColor.AQUA, TextDecoration.BOLD));

                            var huntersList = Component.text()
                                    .append(Component.text("Hunter(s): ", NamedTextColor.DARK_PURPLE, TextDecoration.BOLD));

                            for (Participant participant : manhuntClass.participants) {
                                switch (participant.team()) {
                                    case HUNTER -> huntersList.append(Component.newline()
                                            .append(Component.text(
                                                    Objects.requireNonNull(
                                                            Bukkit.getPlayer(participant.uniqueId())).getName()
                                                    , NamedTextColor.WHITE)));
                                    case SPEEDRUNNER -> speedrunnersList.append(Component.newline()
                                            .append(Component.text(
                                                    Objects.requireNonNull(
                                                            Bukkit.getPlayer(participant.uniqueId())).getName()
                                                    , NamedTextColor.WHITE)));
                                }
                            }




                            return Command.SINGLE_SUCCESS;
                        }))
                .build();
    }
}
