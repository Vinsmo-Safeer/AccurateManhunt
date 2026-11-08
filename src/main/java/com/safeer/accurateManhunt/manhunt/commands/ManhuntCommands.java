package com.safeer.accurateManhunt.manhunt.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.safeer.accurateManhunt.manhunt.GameState;
import com.safeer.accurateManhunt.manhunt.MainManhuntClass;
import com.safeer.accurateManhunt.manhunt.Team;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Objects;

public class ManhuntCommands {

    public static LiteralCommandNode<CommandSourceStack> manhuntCommand(MainManhuntClass manhuntClass) {
        return Commands.literal("manhunt")
                .then(Commands.literal("speedrunner")
                        .requires(sourceStack ->
                                sourceStack.getSender().hasPermission("accurate-manhunt.manhunt.use"))
                        .then(Commands.literal("add")
                                .then(Commands.argument("targets", ArgumentTypes.players())
                                        .executes(context ->
                                                manhuntClass.addParticipant(
                                                        resolveTargets(context), Team.SPEEDRUNNER))))
                        .then(Commands.literal("remove")
                                .then(Commands.argument("targets", ArgumentTypes.players())
                                        .executes(context ->
                                                manhuntClass.removeParticipant(
                                                context.getSource(), resolveTargets(context), Team.SPEEDRUNNER)))))
                .then(Commands.literal("hunter")
                        .requires(sourceStack ->
                                sourceStack.getSender().hasPermission("accurate-manhunt.manhunt.use"))
                        .then(Commands.literal("add")
                                .then(Commands.argument("targets", ArgumentTypes.players())
                                        .executes(context ->
                                                manhuntClass.addParticipant(resolveTargets(context), Team.HUNTER))))
                        .then(Commands.literal("remove")
                                .then(Commands.argument("targets", ArgumentTypes.players())
                                        .executes(context -> manhuntClass.removeParticipant(
                                                context.getSource(), resolveTargets(context), Team.HUNTER)))))
                .then(Commands.literal("prepare")
                        .requires(sourceStack ->
                                sourceStack.getSender().hasPermission("accurate-manhunt.manhunt.use"))
                        .executes(context -> manhuntClass.prepare()))
                .then(Commands.literal("start")
                        .requires(sourceStack ->
                                sourceStack.getSender().hasPermission("accurate-manhunt.manhunt.use"))
                        .executes(context -> manhuntClass.start()))
                .then(Commands.literal("stop")
                        .requires(sourceStack ->
                                sourceStack.getSender().hasPermission("accurate-manhunt.manhunt.use"))
                        .executes(context -> manhuntClass.stop()))
                .then(Commands.literal("startCountdown")
                        .requires(sourceStack ->
                                sourceStack.getSender().hasPermission("accurate-manhunt.manhunt.use"))
                        .executes(context -> manhuntClass.startCountdown(3))
                        .then(Commands.argument("seconds", IntegerArgumentType.integer(0))
                                .executes(context ->
                                        manhuntClass.startCountdown(
                                                IntegerArgumentType.getInteger(context, "seconds")))))
                .then(Commands.literal("resetWorldTimeTick")
                        .requires(sourceStack ->
                                sourceStack.getSender().hasPermission("accurate-manhunt.manhunt.use"))
                        .executes(context -> {
                            Objects.requireNonNull(context.getSource().getExecutor()).getWorld().setTime(0);
                            return Command.SINGLE_SUCCESS;
                        }))
                .then(Commands.literal("heal")
                        .requires(sourceStack ->
                                sourceStack.getSender().hasPermission("accurate-manhunt.manhunt.use"))
                        .executes(context -> {
                            int success = manhuntClass.healAllPlayers();
                            context.getSource().getSender().sendMessage(Component.text(
                                    "Successfully healed " + success + " player(s)"
                                    , NamedTextColor.WHITE, TextDecoration.ITALIC
                            ));
                            return Command.SINGLE_SUCCESS;
                        }))
                .then(Commands.literal("clearInventory")
                        .requires(sourceStack ->
                                sourceStack.getSender().hasPermission("accurate-manhunt.manhunt.use"))
                        .executes(context -> {
                            int success = manhuntClass.clearAllPlayersInventory();
                            context.getSource().getSender().sendMessage(Component.text(
                                    "Successfully cleared " + success + " player(s)"
                                    , NamedTextColor.WHITE, TextDecoration.ITALIC
                            ));
                            return Command.SINGLE_SUCCESS;
                        }))
                .then(Commands.literal("setSurvival")
                        .requires(sourceStack ->
                                sourceStack.getSender().hasPermission("accurate-manhunt.manhunt.use"))
                        .executes(context -> {
                            int success = manhuntClass.setSurvivalAllPlayers();
                            context.getSource().getSender().sendMessage(Component.text(
                                    "Successfully set survival " + success + " player(s)"
                                    , NamedTextColor.WHITE, TextDecoration.ITALIC
                            ));
                            return Command.SINGLE_SUCCESS;
                        }))
                .then(Commands.literal("revokeAdvancement")
                        .requires(sourceStack ->
                                sourceStack.getSender().hasPermission("accurate-manhunt.manhunt.use"))
                        .executes(context -> {
                            int success = manhuntClass.revokeEveryAdvancementOfAllPlayers();
                            context.getSource().getSender().sendMessage(Component.text(
                                    "Successfully revoke every advancement of " + success + " player(s)"
                                    , NamedTextColor.WHITE, TextDecoration.ITALIC
                            ));
                            return Command.SINGLE_SUCCESS;
                        }))
                .then(Commands.literal("list")
                        .executes(context ->
                                manhuntClass.getList(context.getSource())))
                .then(Commands.literal("getCompass")
                        .executes(context -> manhuntClass.getCompass(context.getSource())))
                .build();
    }

    public static LiteralCommandNode<CommandSourceStack> compass(MainManhuntClass manhuntClass) {
        return Commands.literal("compass")
                .executes(context -> manhuntClass.getCompass(context.getSource()))
                .build();
    }

    public static List<Player> resolveTargets(CommandContext<CommandSourceStack> context)
    throws CommandSyntaxException {

        PlayerSelectorArgumentResolver resolver = context.getArgument(
                "targets",
                PlayerSelectorArgumentResolver.class
        );

        return resolver.resolve(context.getSource());
    }
}
