package it.dani.seniorparkour.services.parkour;

import it.dani.seniorparkour.SeniorParkour;
import it.dani.seniorparkour.configuration.ConfigLoader;
import it.dani.seniorparkour.configuration.ConfigManager;
import it.dani.seniorparkour.configuration.ConfigType;
import it.dani.seniorparkour.services.parkour.object.ParkourPlayer;
import it.dani.seniorparkour.services.scoreboard.ScoreboardManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.*;


public class ParkourService implements ConfigLoader {
    private final ScoreboardManager scoreboardManager;
    private final Set<Parkour> parkours = new HashSet<>();

    private final Set<ParkourPlayer> activePlayers = new HashSet<>();

    private final Material startMaterial, endMaterial, checkPointMaterial;

    public ParkourService(SeniorParkour plugin) {
        YamlConfiguration config = plugin.getConfigManager().getConfig(ConfigType.MAIN_CONFIG);
        scoreboardManager = plugin.getScoreboardManager();

        startMaterial = Material.valueOf(config.getString("blocks.start"));
        endMaterial = Material.valueOf(config.getString("blocks.end"));
        checkPointMaterial = Material.valueOf(config.getString("blocks.checkpoint"));
    }

    public Optional<Parkour> getParkourByStart(Block start){
        return parkours.stream()
                .filter(parkour -> parkour.getStart().getBlock().equals(start))
                .findFirst();
    }

    public Optional<ParkourPlayer> getParkourPlayer(Player player){
        return activePlayers.stream()
                .filter(parkourPlayer -> parkourPlayer.getUuid().equals(player.getUniqueId()))
                .findFirst();
    }

    public void startParkour(Player player, Parkour parkour){
        activePlayers.add(new ParkourPlayer(player.getUniqueId(),parkour));


        scoreboardManager.setScoreboard(player,true);
    }

    public void endParkour(Player player){
        activePlayers.removeIf(parkourPlayer -> parkourPlayer.getUuid().equals(player.getUniqueId()));

        scoreboardManager.setScoreboard(player,false);
    }

    public void createParkour(String name, Location loc){
        parkours.add(new Parkour(name,loc));

        setPointBlock(loc,startMaterial);
    }

    public void addEndPoint(String name, Location loc){
        getByName(name).ifPresent(parkour -> {
            if(parkour.getEnd() != null){
                parkour.getEnd().getBlock().setType(Material.AIR);
            }

            parkour.setEnd(loc);

            setPointBlock(loc,endMaterial);
        });
    }

    public void addCheckPoint(String name, Location loc){
        getByName(name).ifPresent(parkour -> {
            parkour.setEnd(loc);

            setPointBlock(loc,checkPointMaterial);
        });
    }


    public void removeCheckPoint(String name, int index){
        getByName(name).ifPresent(parkour ->{
            Location loc = parkour.getCheckPoints().remove(index);

            loc.getBlock().setType(Material.AIR);
        });
    }


    public Optional<Parkour> getByName(String name){
        return parkours.stream()
                .filter(parkour -> parkour.getName().equalsIgnoreCase(name))
                .findFirst();
    }

    @Override
    public void load(ConfigManager manager) {
        YamlConfiguration config = manager.getConfig(ConfigType.PARKOUR);

        ConfigurationSection parkourSection = config.getConfigurationSection("parkours");
        if(parkourSection == null){
            config.createSection("parkours");
            return;
        }

        for (String key : parkourSection.getKeys(false)) {
            Location start,end;

            ConfigurationSection startSection = parkourSection.getConfigurationSection(key+".start");
            start = getLocation(startSection);

            ConfigurationSection endSection = parkourSection.getConfigurationSection(key+".end");

            end = getLocation(endSection);

            Parkour parkour = new Parkour(key,start);

            if(end != null){
                parkour.setEnd(end);
            }

            ConfigurationSection checkPoints = parkourSection.getConfigurationSection(key+".checkpoints");

            if(checkPoints != null){

                for (String pointKey : checkPoints.getKeys(false)) {
                    parkour.addCheckPoint(getLocation(checkPoints.getConfigurationSection(pointKey)));
                }

            }
        }
    }

    @Override
    public void unload(ConfigManager manager) {
        YamlConfiguration config = manager.getConfig(ConfigType.PARKOUR);

        config.set("parkours",null);

        ConfigurationSection parkourSection = config.createSection("parkours");

        for (Parkour parkour : parkours) {
            String name = parkour.getName();
            ConfigurationSection startSection = parkourSection.createSection(name+".start");
            serializeLocation(startSection,parkour.getStart());

            if(parkour.getEnd() != null){
                ConfigurationSection endSection = parkourSection.createSection(name+".end");
                serializeLocation(endSection,parkour.getEnd());
            }

            if(!parkour.getCheckPoints().isEmpty()){
                ConfigurationSection pointsSection = parkourSection.createSection(name+".checkpoints");

                int i = 0;
                for (Location checkPoint : parkour.getCheckPoints()) {
                    serializeLocation(pointsSection.createSection(name + i),checkPoint);

                    i++;
                }
            }
        }


    }

    private void setPointBlock(Location loc, Material type){
        loc.getBlock().setType(type);
    }

    private Location getLocation(ConfigurationSection section){
        if(section == null)
            return null;

        String world = section.getString("world","world");
        int x = section.getInt("x");
        int y = section.getInt("x");
        int z = section.getInt("x");

        return new Location(Bukkit.getWorld(world),x,y,z);
    }

    private void serializeLocation(ConfigurationSection section,Location loc){
        section.set("world",loc.getWorld().getName());
        section.set("x",loc.getBlockX());
        section.set("y",loc.getBlockY());
        section.set("z",loc.getBlockZ());
    }
}
