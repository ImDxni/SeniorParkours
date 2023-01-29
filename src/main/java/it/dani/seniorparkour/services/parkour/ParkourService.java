package it.dani.seniorparkour.services.parkour;

import it.dani.seniorparkour.SeniorParkour;
import it.dani.seniorparkour.configuration.ConfigLoader;
import it.dani.seniorparkour.configuration.ConfigManager;
import it.dani.seniorparkour.configuration.ConfigType;
import it.dani.seniorparkour.database.DatabaseManager;
import it.dani.seniorparkour.database.entity.RPlayer;
import it.dani.seniorparkour.services.holograms.HologramService;
import it.dani.seniorparkour.services.holograms.HologramType;
import it.dani.seniorparkour.services.holograms.object.Hologram;
import it.dani.seniorparkour.services.parkour.object.ParkourPlayer;
import it.dani.seniorparkour.services.scoreboard.ScoreboardManager;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.*;


public class ParkourService implements ConfigLoader {
    private final ScoreboardManager scoreboardManager;
    private final DatabaseManager databaseManager;
    private final HologramService hologramService;

    @Getter
    private final Set<Parkour> parkours = new HashSet<>();

    private final Set<ParkourPlayer> activePlayers = new HashSet<>();

    private final Material startMaterial, endMaterial, checkPointMaterial;

    public ParkourService(SeniorParkour plugin) {
        YamlConfiguration config = plugin.getConfigManager().getConfig(ConfigType.MAIN_CONFIG);
        scoreboardManager = plugin.getScoreboardManager();
        databaseManager = plugin.getDatabaseManager();
        hologramService = plugin.getHologramService();

        startMaterial = Material.valueOf(config.getString("blocks.start"));
        endMaterial = Material.valueOf(config.getString("blocks.end"));
        checkPointMaterial = Material.valueOf(config.getString("blocks.checkpoint"));
    }

    public Optional<Parkour> getParkourByStart(Block start) {
        return parkours.stream()
                .filter(parkour -> parkour.getStart().getBlock().equals(start))
                .findFirst();
    }

    public Optional<Parkour> getParkourByName(String name) {
        return parkours.stream()
                .filter(parkour -> parkour.getName().equalsIgnoreCase(name))
                .findFirst();
    }

    public Optional<ParkourPlayer> getParkourPlayer(Player player) {
        return activePlayers.stream()
                .filter(parkourPlayer -> parkourPlayer.getUuid().equals(player.getUniqueId()))
                .findFirst();
    }

    public void startParkour(Player player, Parkour parkour) {
        ParkourPlayer parkourPlayer = new ParkourPlayer(player.getUniqueId(), parkour);

        databaseManager.playerExists(player.getUniqueId(), parkour.getName()).thenAccept((exists) -> {
            if (!exists) {
                activePlayers.add(parkourPlayer);
                return;
            }

            databaseManager.getTime(player, parkour.getName())
                    .thenAcceptBoth(databaseManager.getPosition(player, parkour.getName()),
                            (time, position) -> {
                                parkourPlayer.setPosition(position);
                                parkourPlayer.setRecordTime(time);

                                activePlayers.add(parkourPlayer);

                            });
        });


        scoreboardManager.setScoreboard(player, true);

    }

    public void endParkour(Player player) {
        Iterator<ParkourPlayer> iterator = activePlayers.iterator();

        scoreboardManager.setScoreboard(player,false);
        while (iterator.hasNext()) {
            ParkourPlayer parkourPlayer = iterator.next();

            if (parkourPlayer.getUuid().equals(player.getUniqueId())) {
                iterator.remove();
                RPlayer record = new RPlayer(player.getUniqueId(), player.getName(), parkourPlayer.getParkour().getName(), parkourPlayer.getParkourTime());
                databaseManager.insertPlayer(record);
                return;
            }
        }
    }

    public void removeActivePlayer(Player player){
        activePlayers.removeIf(parkourPlayer -> player.getUniqueId().equals(parkourPlayer.getUuid()));

        scoreboardManager.setScoreboard(player,false);
    }

    public void createParkour(String name, Block block) {
        Parkour parkour = new Parkour(name,block.getLocation());
        parkours.add(parkour);

        setPointBlock(block, startMaterial);

        hologramService.createHologram(block.getLocation(), HologramType.PARKOUR_START,parkour);
    }

    public void deleteParkour(Parkour parkour){
        parkours.remove(parkour);

        setPointBlock(parkour.getStart().getBlock(), Material.AIR);
        hologramService.getByLocation(parkour.getStart()).ifPresent(Hologram::destroy);

        if(parkour.getEnd() != null) {
            setPointBlock(parkour.getEnd().getBlock(), Material.AIR);
            hologramService.getByLocation(parkour.getEnd()).ifPresent(Hologram::destroy);
        }

        for (Location checkPoint : parkour.getCheckPoints()) {
            setPointBlock(checkPoint.getBlock(),Material.AIR);

            hologramService.getByLocation(checkPoint).ifPresent(Hologram::destroy);
        }

        databaseManager.deleteStats(parkour.getName());


        if(parkour.getTopLocation() != null) {

            System.out.println(parkour.getTopLocation());
            System.out.println("OLOGRAMMI");
            hologramService.getByLocation(parkour.getTopLocation()).ifPresent(Hologram::destroy);
        }
        Iterator<ParkourPlayer> iterator = activePlayers.iterator();
        while (iterator.hasNext()) {
            ParkourPlayer parkourPlayer = iterator.next();

            if (parkourPlayer.getParkour().equals(parkour)) {
                iterator.remove();

                scoreboardManager.setScoreboard(Bukkit.getPlayer(parkourPlayer.getUuid()),false);
                return;
            }
        }

    }

    public void addEndPoint(Parkour parkour, Block block) {
        if (parkour.getEnd() != null) {
            parkour.getEnd().getBlock().setType(Material.AIR);


            hologramService.getByLocation(parkour.getEnd()).ifPresent(Hologram::destroy);
        }

        parkour.setEnd(block.getLocation());

        hologramService.createHologram(block.getLocation(), HologramType.PARKOUR_END,parkour);

        setPointBlock(block, endMaterial);
    }

    public void addCheckPoint(Parkour parkour, Block block) {
        parkour.getCheckPoints().add(block.getLocation());

        hologramService.createHologram(block.getLocation(), HologramType.PARKOUR_CHECKPOINT,parkour);
        setPointBlock(block, checkPointMaterial);
    }


    public void removeCheckPoint(Parkour parkour, int index) {
        if(index >= 0 && index < parkour.getCheckPoints().size()) {
            Location loc = parkour.getCheckPoints().remove(index);

            setPointBlock(loc.getBlock(), Material.AIR);

            hologramService.getByLocation(loc).ifPresent(Hologram::destroy);
        }
    }

    public void createTop(Parkour parkour, Location location){
        if(parkour.getTopLocation() != null){
            deleteTop(parkour);
        }

        parkour.setTopLocation(location);

        hologramService.createHologram(location,HologramType.PARKOUR_TOP,parkour);
    }

    public void deleteTop(Parkour parkour){
        hologramService.getByLocation(parkour.getTopLocation()).ifPresent(Hologram::destroy);
        parkour.setTopLocation(null);
    }


    @Override
    public void load(ConfigManager manager) {
        YamlConfiguration config = manager.getConfig(ConfigType.PARKOUR);

        ConfigurationSection parkourSection = config.getConfigurationSection("parkours");
        if (parkourSection == null) {
            config.createSection("parkours");
            return;
        }

        for (String key : parkourSection.getKeys(false)) {
            Location start, end,top;
            System.out.println("TROVATO PARKOUR " + key);
            ConfigurationSection startSection = parkourSection.getConfigurationSection(key + ".start");
            start = getLocation(startSection);
            System.out.println("START LOCATION " + start);

            ConfigurationSection endSection = parkourSection.getConfigurationSection(key + ".end");

            end = getLocation(endSection);

            System.out.println("END LOCATION " + start);

            Parkour parkour = new Parkour(key, start);

            hologramService.createHologram(start, HologramType.PARKOUR_START,parkour);
            System.out.println("OLOGRAMMA START CREATO");

            if (end != null) {
                parkour.setEnd(end);

                hologramService.createHologram(end, HologramType.PARKOUR_END,parkour);

                System.out.println("OLOGRAMMA END CREATO");
            }

            ConfigurationSection checkPoints = parkourSection.getConfigurationSection(key + ".checkpoints");

            if (checkPoints != null) {
                System.out.println("TROVATI CHECKPOINT");

                for (String pointKey : checkPoints.getKeys(false)) {
                    Location loc = getLocation(checkPoints.getConfigurationSection(pointKey));
                    parkour.addCheckPoint(loc);

                    hologramService.createHologram(loc, HologramType.PARKOUR_CHECKPOINT,parkour);
                    System.out.println("CHECKPOINT CREATO");
                }

            }

            ConfigurationSection topSection = parkourSection.getConfigurationSection(key + ".top");

            top = getLocation(topSection);

            if(top != null){
                System.out.println("TROVATA TOP");
                createTop(parkour,top);

                System.out.println("TOP CREATA");
            }

            parkours.add(parkour);
        }
    }

    @Override
    public void unload(ConfigManager manager) {
        YamlConfiguration config = manager.getConfig(ConfigType.PARKOUR);

        config.set("parkours", null);

        ConfigurationSection parkourSection = config.createSection("parkours");

        for (Parkour parkour : parkours) {
            String name = parkour.getName();
            ConfigurationSection startSection = parkourSection.createSection(name + ".start");
            serializeLocation(startSection, parkour.getStart());

            if (parkour.getEnd() != null) {
                ConfigurationSection endSection = parkourSection.createSection(name + ".end");
                serializeLocation(endSection, parkour.getEnd());
            }

            if (!parkour.getCheckPoints().isEmpty()) {
                ConfigurationSection pointsSection = parkourSection.createSection(name + ".checkpoints");

                int i = 0;
                for (Location checkPoint : parkour.getCheckPoints()) {
                    serializeLocation(pointsSection.createSection(String.valueOf(i)), checkPoint);

                    i++;
                }
            }

            if(parkour.getTopLocation() != null){
                ConfigurationSection topSection = parkourSection.createSection(name + ".top");
                serializeLocation(topSection, parkour.getTopLocation());
            }
        }

        try {
            config.save(manager.getFile(ConfigType.PARKOUR));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void setPointBlock(Block block, Material type) {
        block.setType(type);
    }

    private Location getLocation(ConfigurationSection section) {
        if (section == null)
            return null;

        String world = section.getString("world", "world");
        int x = section.getInt("x");
        int y = section.getInt("y");
        int z = section.getInt("z");

        return new Location(Bukkit.getWorld(world), x, y, z);
    }

    private void serializeLocation(ConfigurationSection section, Location loc) {
        section.set("world", loc.getWorld().getName());
        section.set("x", loc.getX());
        section.set("y", loc.getY());
        section.set("z", loc.getZ());
    }
}
