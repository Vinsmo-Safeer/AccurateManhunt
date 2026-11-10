package com.safeer.accurateManhunt.manhunt;

import com.mojang.brigadier.Command;
import com.safeer.accurateManhunt.AccurateManhunt;
import com.safeer.accurateManhunt.manhunt.tasks.CountdownTask;
import com.safeer.accurateManhunt.manhunt.tasks.ManhuntTimer;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import org.bukkit.*;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jspecify.annotations.Nullable;

import java.util.*;

import static com.safeer.accurateManhunt.AccurateManhunt.TRACKER_KEY;

public class MainManhuntClass {


    public final AccurateManhunt plugin;

    public GameState gameState = GameState.IDLE;
    public Set<Participant> participants = new HashSet<>();

    public Set<Speedrunner> speedrunners;
    public Map<UUID, Speedrunner> hunters;

    BukkitTask task = null;

    public static int CountDownSeconds = 3;


    public long startedAt;

    public MainManhuntClass(AccurateManhunt plugin) {
        this.plugin = plugin;
    }


    public int startCountdown(long seconds) {


        Player speedrunner = null;



        for (Participant participant : participants) {

            var player = Bukkit.getPlayer(participant.uniqueId());

            if (player == null) continue;


            if (participant.team().equals(Team.SPEEDRUNNER)) {
                speedrunner = player;
                break;
            }
        }

        if (speedrunner == null) {
            Bukkit.broadcast(Component.text("Cannot find speedrunner (Might be offline or unassigned).", NamedTextColor.RED));
            return 0;
        }

        var speedrunnerLocation = speedrunner.getLocation();


        List<Player> hunters = new ArrayList<>();

        for (Participant participant : participants) {
            var player = Bukkit.getPlayer(participant.uniqueId());

            if (player == null) continue;

            if (participant.team().equals(Team.SPEEDRUNNER)) continue;

            player.teleport(speedrunnerLocation);

            player.setGameMode(GameMode.ADVENTURE);

            player.setAllowFlight(true);
            player.setFlying(true);

            hunters.add(player);

        }


        gameState = GameState.COUNTDOWN;

        long countDownStartAt = System.currentTimeMillis();


        BukkitTask countDownTask = new CountdownTask(plugin,
                this,
                hunters,
                countDownStartAt,
                speedrunnerLocation).runTaskTimer(plugin, 0L, 20L);


        return Command.SINGLE_SUCCESS;
    }

    public int prepare() {

        if (checkBeforeStarting() == 0) return 0;

        gameState = GameState.PREPARED;

        Bukkit.broadcast(Component.text("Manhunt prepared. Manhunt will start when the speedrunner hits a hunter.", NamedTextColor.AQUA));

        return Command.SINGLE_SUCCESS;
    }

    public int start() {


        if (checkBeforeStarting() == 0) return 0;


        initializeManhunt();


        return Command.SINGLE_SUCCESS;
    }

    private void initializeManhunt() {
        initializeSpeedrunnersAndHunters();

        getCompassToEveryHunter();

        gameState = GameState.RUNNING;


        startedAt = System.currentTimeMillis();
        task = new ManhuntTimer(this, startedAt, hunters).runTaskTimer(plugin, 0L, 60L);


        Bukkit.broadcast(Component.text("Manhunt started!", NamedTextColor.GREEN));
    }

    public void getCompass(Player hunter) {

        if (hunter == null) return;


        Player speedrunner = Bukkit.getPlayer(hunters.get(hunter.getUniqueId()).getUniqueId());

        if (speedrunner == null) {
            speedrunner = getFirstSpeedrunner();
            if (speedrunner == null) return;
        }

        getCompass(speedrunner, hunter, true);
    }

    private void getCompassToEveryHunter() {
        for (Map.Entry<UUID, Speedrunner> entry : hunters.entrySet()) {

            UUID key = entry.getKey();
            Speedrunner value = entry.getValue();

            if (key == null) continue;


            var hunter = Bukkit.getPlayer(key);
            if (hunter == null) continue;

            if (value != null && value.getUniqueId() != null) {

                var speedrunner = Bukkit.getPlayer(value.getUniqueId());

                if (speedrunner != null) {
                    getCompass(speedrunner, hunter, true);
                    return;
                }
            }

            var speedrunner = getFirstSpeedrunner();
            if (speedrunner == null) {

//                hunter.sendMessage(Component.text("Unable to find speedrunner (Might be offline or unassigned)."));
                return;
            }

            getCompass(speedrunner, hunter, true);
            entry.setValue(getSpeedrunnerClass(speedrunner));
        }
    }

    public Player getFirstSpeedrunner() {

        for (Speedrunner speedrunner : speedrunners) {
            if (speedrunner == null) continue;
            var uuid = speedrunner.getUniqueId();
            if (uuid == null) continue;
            var player = Bukkit.getPlayer(uuid);
            if (player == null) continue;
            return player;
        }

        return null;

    }

    public void changeTrackingPlayerOf(UUID hunter, Speedrunner speedrunner) {
        hunters.replace(hunter, speedrunner);
    }

    private void initializeSpeedrunnersAndHunters() {

        speedrunners = new HashSet<>();
        hunters = new HashMap<>();

        for (Participant participant : participants) {

            Team team = participant.team();

            var player = Bukkit.getPlayer(participant.uniqueId());
            boolean isPlayerNull = false;
            if (player == null) {
                Bukkit.broadcast(Component.text(
                        "Unable to change waypoint attribute of player with id " +
                                participant.uniqueId() + ". Cannot find player!", NamedTextColor.RED));
                isPlayerNull = true;
            }


            if (team.equals(Team.SPEEDRUNNER)) {
                speedrunners.add(new Speedrunner(participant.uniqueId()));

                if (isPlayerNull) continue;

                Objects.requireNonNull(player.getAttribute(Attribute.WAYPOINT_RECEIVE_RANGE))
                        .setBaseValue(0);
                Objects.requireNonNull(player.getAttribute(Attribute.WAYPOINT_TRANSMIT_RANGE))
                        .setBaseValue(0);


                continue;
            }

            if (team.equals(Team.HUNTER)) {
                hunters.put(participant.uniqueId(), null);

                if (isPlayerNull) continue;

                Objects.requireNonNull(player.getAttribute(Attribute.WAYPOINT_RECEIVE_RANGE))
                        .setBaseValue(60000000);
                Objects.requireNonNull(player.getAttribute(Attribute.WAYPOINT_TRANSMIT_RANGE))
                        .setBaseValue(60000000);
            }


        }
    }

    private int checkBeforeStarting() {

        // Check if there is at least one speedrunner and a hunter

        boolean hasSpeedrunner = false;
        boolean hasHunter = false;

        for (Participant participant : participants) {
            if (participant.team().equals(Team.HUNTER)) {
                hasHunter = true;
                continue;
            }
            if (participant.team().equals(Team.SPEEDRUNNER)) hasSpeedrunner = true;
        }

        if (!hasHunter || !hasSpeedrunner) {
            StringBuilder message = new StringBuilder();

            message.append("Assign ");

            if (!hasHunter && !hasSpeedrunner) {
                message.append("a speedrunner and a hunter ");
            } else {
                if (!hasHunter) {
                    message.append("a hunter ");
                } else {
                    message.append("a speedrunner ");
                }
            }

            message.append("before starting the game.");

            Bukkit.broadcast(Component.text(message.toString(), NamedTextColor.RED));

            return 0;
        }

        return Command.SINGLE_SUCCESS;
    }

    public void gameOver() {

        Title title = Title.title(
                Component.text("Speedrunner Won", NamedTextColor.GOLD),
                Component.text("Hunters Lost the game.", NamedTextColor.RED));

        var players = Bukkit.getOnlinePlayers();
        for (Player player : players) {
            player.showTitle(title);
        }

        long duration = System.currentTimeMillis() - startedAt;

        long hours = duration / 3_600_000;
        long minutes = (duration / 60_000) % 60;
        long seconds = (duration / 1_000) % 60;

        String formattedTime = String.format("%02d:%02d:%02d", hours, minutes, seconds);

        Bukkit.broadcast(Component.text("Lasted " + formattedTime
                , NamedTextColor.YELLOW));

        gameState = GameState.IDLE;

        task.cancel();
        task = null;
        speedrunners = null;
        hunters = null;
    }

    public int stop() {


        Bukkit.broadcast(Component.text("Manhunt Stopped", NamedTextColor.RED));

        long duration = System.currentTimeMillis() - startedAt;

        double minutes = duration / 60_000.0;

        String formattedTime = String.format("%.2f minutes", minutes);

        Bukkit.broadcast(Component.text("Lasted " + formattedTime
        , NamedTextColor.YELLOW));

        gameState = GameState.IDLE;

        task.cancel();
        task = null;
        speedrunners = null;
        hunters = null;



        return Command.SINGLE_SUCCESS;
    }

    public int addParticipant(List<Player> playerList, Team team) {

        for (Player player : playerList) {

            var UUID = player.getUniqueId();

            Participant isParticipant = participants.stream()
                    .filter(p -> p.uniqueId().equals(UUID))
                    .findFirst()
                    .orElse(null);

            if (isParticipant == null) {
                participants.add(new Participant(player.getUniqueId(), team));
                Bukkit.broadcast(
                        Component.text("Player " + player.getName()
                                        + " is now a " + Participant.getTeamName(team) + "."
                            , NamedTextColor.GREEN));
                continue;
            }

            if (isParticipant.team().equals(team)) {
                Bukkit.broadcast(Component.text("Player "
                        + player.getName()
                        +" is already a "
                        + Participant.getTeamName(isParticipant.team())
                        + ".", NamedTextColor.YELLOW));
                continue;
            }

            Team initialTeam = isParticipant.team();

            participants.remove(isParticipant);
            participants.add(new Participant(isParticipant.uniqueId(), team));

            Bukkit.broadcast(Component.text("Changed player "
                    + player.getName()
                    + " from "
                    + Participant.getTeamName(initialTeam)
                    + " to a "
                    + Participant.getTeamName(team) + "."
                    , NamedTextColor.GREEN));
        }


        return Command.SINGLE_SUCCESS;
    }

    public int removeParticipant(CommandSourceStack source, List<Player> playerList, Team team) {

        var sender = source.getSender();

        int numberOfPlayersRemoved = 0;

        boolean successful = true;

        for (Player player : playerList) {

            var UUID = player.getUniqueId();

            Participant isParticipant = participants.stream()
                    .filter(p -> p.uniqueId().equals(UUID))
                    .findFirst()
                    .orElse(null);

            if (isParticipant == null) {
                sender.sendMessage(Component.text(
                        "Player " + player.getName() + " is not registered in the game.", NamedTextColor.RED)
                );
                sender.sendMessage(Component.text("Use /manhunt help for instructions.", NamedTextColor.AQUA));
                successful = false;
                continue;
            }

            if (!Participant.getTeamName(isParticipant.team()).equals(Participant.getTeamName(team))) {
                sender.sendMessage(Component.text(
                        "Player " + player.getName()
                                + " is a " + Participant.getTeamName(isParticipant.team())
                                +", not a " + Participant.getTeamName(team)
                        ,NamedTextColor.RED)
                );
                successful = false;
                continue;
            }


            participants.remove(isParticipant);
            numberOfPlayersRemoved++;
        }

        if (!successful) {
            if (numberOfPlayersRemoved <= 0) {
                return 0;
            }
            sender.sendMessage(
                    Component.text(
                            "Only removed " 
                                    + numberOfPlayersRemoved
                                    + " player(s) from the game"
                            , NamedTextColor.YELLOW)
            );
            return 0;
        }


        sender.sendMessage(
                Component.text(
                        "Removed "
                                + numberOfPlayersRemoved
                                + " player(s) from the game"
                        , NamedTextColor.GRAY, TextDecoration.ITALIC)
        );

        return Command.SINGLE_SUCCESS;
    }

    public int getList(CommandSourceStack source) {

        var sender = source.getSender();

        StringBuilder speedrunnerList = new StringBuilder();
        StringBuilder hunterList = new StringBuilder();

        for (Participant participant : participants) {
            var playerName = Objects.requireNonNull(Bukkit.getPlayer(participant.uniqueId())).getName();

            if (Participant.getTeamName(participant.team()).equals(Participant.getTeamName(Team.SPEEDRUNNER))) {
                speedrunnerList.append("\n").append(playerName);
                continue;
            }

            if (Participant.getTeamName(participant.team()).equals(Participant.getTeamName(Team.HUNTER))) {
                hunterList.append("\n").append(playerName);
            }
        }


        var output = Component.text()
                .append(Component.text("GAME STATE: " + gameState.name() , NamedTextColor.AQUA))
                .append(Component.newline())
                .append(Component.text("SPEEDRUNNERS: ", NamedTextColor.YELLOW)
                        .append(Component.text(speedrunnerList.toString())))
                .append(Component.newline())
                .append(Component.text("HUNTERS: ", NamedTextColor.YELLOW)
                        .append(Component.text(hunterList.toString())))
                .build();

        sender.sendMessage(output);

        return Command.SINGLE_SUCCESS;
    }

    public int getCompass(Player speedrunner, Player hunter, boolean forceGetCompass) {

        var ref = new Object() {
            Location speedrunnerLocation = speedrunner.getLocation();
        };
        var hunterLocation = hunter.getLocation();

        updateSpeedrunnerLocation(speedrunner);

        if (!ref.speedrunnerLocation.getWorld().getName().equals(hunterLocation.getWorld().getName())) {
            var speedrunnerClass = getSpeedrunnerClass(speedrunner);

            if (speedrunnerClass == null) {
                hunter.sendMessage(Component.text("Cannot find speedrunner!", NamedTextColor.RED));
                return 0;
            }
            var speedrunnerLastKnownLocation = speedrunnerClass.getLastOverWorldLocation(hunterLocation.getWorld().getName());
            if (speedrunnerLastKnownLocation == null) {
                hunter.sendMessage(Component.text("Cannot find speedrunner's last know location (speedrunner might be in another dimension)."));
                return 0;
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

            return Command.SINGLE_SUCCESS;
        }


        if (!forceGetCompass) return Command.SINGLE_SUCCESS;

        ItemStack compass = new ItemStack(Material.COMPASS);

        compass.editMeta(CompassMeta.class, meta -> {
            meta.customName(Component.text("Tracker", NamedTextColor.YELLOW)
                    .decoration(TextDecoration.ITALIC, false));

            meta.lore(List.of(
                    Component.text("Track players by right-click", NamedTextColor.GRAY)
                            .decoration(TextDecoration.ITALIC, false)
            ));
            meta.setLodestone(ref.speedrunnerLocation);
            meta.setLodestoneTracked(false);
            meta.getPersistentDataContainer().set(TRACKER_KEY, PersistentDataType.BYTE, (byte) 1);
        });

        HashMap<Integer, ItemStack> remainingItems = hunterInventory.addItem(compass);

        if (!remainingItems.isEmpty()) {
            hunter.sendMessage(Component.text("Free up your inventory for a tracker.", NamedTextColor.YELLOW));
            return 0;
        }

        return Command.SINGLE_SUCCESS;
    }

    public int getCompass(CommandSourceStack source) {

        if (!(source.getExecutor() instanceof Player player)) {
            source.getSender().sendMessage(Component.text("Only a player can execute this command.", NamedTextColor.RED));
            return 0;
        }

        if (!(gameState.equals(GameState.RUNNING))) {
            player.sendMessage(Component.text("The game is not running. You can only get a compass when the game starts!", NamedTextColor.RED));
        }

        UUID key = player.getUniqueId();
        Speedrunner value = hunters.get(player.getUniqueId());

        var hunter = Bukkit.getPlayer(key);
        if (hunter == null) return 0;

        if (value != null && value.getUniqueId() != null) {

            var speedrunner = Bukkit.getPlayer(value.getUniqueId());

            if (speedrunner != null) {
                return getCompass(speedrunner, hunter, true);
            }
        }

        var speedrunner = getFirstSpeedrunner();
        if (speedrunner == null) {
            hunter.sendMessage(Component.text("Unable to find speedrunner (Might be offline or unassigned)."));
            return 0;
        }

        getCompass(speedrunner, hunter, true);
        hunters.put(key, getSpeedrunnerClass(speedrunner));
        return Command.SINGLE_SUCCESS;
    }


    public void updateSpeedrunnerLocation(Player speedrunner) {

        var speedrunnerLocation = speedrunner.getLocation();

        Speedrunner speedrunnerClass = getSpeedrunnerClass(speedrunner);
        if (speedrunnerClass == null) return;
        speedrunnerClass.setLocation(speedrunnerLocation);
    }

    @Nullable
    public Speedrunner getSpeedrunnerClass(Player speedrunner) {
        Speedrunner speedrunnerClass = speedrunners.stream()
                .filter(sr -> sr.getUniqueId().equals(speedrunner.getUniqueId()))
                .findFirst()
                .orElse(null);
        if (speedrunnerClass == null) {
            return null;
        }
        return speedrunnerClass;
    }

    public void speedrunnerDied(UUID speedrunnerId) {
        Speedrunner speedrunner = speedrunners.stream()
                .filter(sp -> sp.getUniqueId().equals(speedrunnerId))
                .findFirst().orElse(null);

        speedrunners.remove(speedrunner);

        if (speedrunners.isEmpty()) {
            stop();
        }
    }

    public int healAllPlayers() {

        int successfulPlayers = 0;

        for (Participant participant : participants) {

            UUID playerId = participant.uniqueId();


            Player player = Bukkit.getPlayer(playerId);

            if (player == null) {
                continue;
            }

            player.setHealth(20.0);
            player.setFoodLevel(20);
            player.setSaturation(5.0f);
            player.setExhaustion(0.0f);

            successfulPlayers++;

        }



        return successfulPlayers;
    }

    public int clearAllPlayersInventory() {

        int successfulPlayers = 0;

        for (Participant participant : participants) {

            UUID playerId = participant.uniqueId();


            Player player = Bukkit.getPlayer(playerId);

            if (player == null) {
                continue;
            }

            player.getInventory().clear();
            successfulPlayers++;

        }


        return successfulPlayers;
    }

    public int setSurvivalAllPlayers() {

        int successfulPlayers = 0;

        for (Participant participant : participants) {

            UUID playerId = participant.uniqueId();


            Player player = Bukkit.getPlayer(playerId);

            if (player == null) {
                continue;
            }

            player.setGameMode(GameMode.SURVIVAL);

            successfulPlayers++;
        }

        return successfulPlayers;
    }

    public int revokeEveryAdvancementOfAllPlayers() {
        int successfulPlayers = 0;

        for (Participant participant : participants) {

            UUID playerId = participant.uniqueId();


            Player player = Bukkit.getPlayer(playerId);

            if (player == null) {
                continue;
            }


            resetAdvancements(player);

            successfulPlayers++;
        }

        return successfulPlayers;
    }



    public void healClearRevokeAdvancementAndSetSurvivalOfAllPlayers() {


        for (Participant participant : participants) {
            UUID playerId = participant.uniqueId();


            Player player = Bukkit.getPlayer(playerId);

            if (player == null) {
                continue;
            }

            player.setHealth(20.0);
            player.setFoodLevel(20);
            player.setSaturation(5.0f);
            player.setExhaustion(0.0f);

            player.getInventory().clear();

            player.setGameMode(GameMode.SURVIVAL);

            resetAdvancements(player);

        }

    }

    public void resetAdvancements(Player player) {

        Iterator<Advancement> advancements = Bukkit.advancementIterator();

        while (advancements.hasNext()) {
            Advancement advancement = advancements.next();

            AdvancementProgress progress = player.getAdvancementProgress(advancement);

            for (String criterion : progress.getAwardedCriteria()) {
                progress.revokeCriteria(criterion);
            }
        }
    }

    public void resetEverySettings() {
        gameState = GameState.IDLE;
        hunters = new HashMap<>();
        speedrunners = new HashSet<>();
        task = null;
        startedAt = System.currentTimeMillis();
        participants = new HashSet<>();
    }

}
