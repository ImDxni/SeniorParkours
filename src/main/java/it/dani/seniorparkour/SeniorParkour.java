package it.dani.seniorparkour;

import it.dani.seniorparkour.commands.ParkourCommand;
import it.dani.seniorparkour.configuration.ConfigLoader;
import it.dani.seniorparkour.configuration.ConfigManager;
import it.dani.seniorparkour.configuration.ConfigType;
import it.dani.seniorparkour.database.DatabaseManager;
import it.dani.seniorparkour.listeners.FlyListener;
import it.dani.seniorparkour.listeners.MoveListener;
import it.dani.seniorparkour.listeners.QuitListener;
import it.dani.seniorparkour.placeholders.ParkourExpansion;
import it.dani.seniorparkour.services.holograms.HologramService;
import it.dani.seniorparkour.services.parkour.ParkourService;
import it.dani.seniorparkour.services.scoreboard.ScoreboardManager;
import it.dani.seniorparkour.commons.HologramAdapter;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class SeniorParkour extends JavaPlugin {

    private final Set<ConfigLoader> loadedService = new HashSet<>();
    @Getter
    private ConfigManager configManager;
    @Getter
    private ScoreboardManager scoreboardManager;

    @Getter
    private DatabaseManager databaseManager;

    @Getter
    private ParkourService parkourService;

    @Getter
    private HologramService hologramService;

    @Override
    public void onEnable() {
        configManager = new ConfigManager(this);
        configManager.register(ConfigType.values());

        databaseManager = new DatabaseManager(this);

        HologramAdapter adapter = detectCurrentVersion();

        if(adapter == null){
            Bukkit.getLogger().info("This version is not supported, disabling plugin");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        scoreboardManager = new ScoreboardManager();
        hologramService = new HologramService(this);
        hologramService.setAdapter(adapter);

        parkourService = new ParkourService(this);

        loadServices(parkourService,scoreboardManager);


        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new ParkourExpansion(parkourService).register();
        }

        Bukkit.getPluginManager().registerEvents(new FlyListener(parkourService),this);
        Bukkit.getPluginManager().registerEvents(new MoveListener(this),this);
        Bukkit.getPluginManager().registerEvents(new QuitListener(parkourService),this);


        Bukkit.getScheduler().runTaskTimer(this,scoreboardManager,0,5);

        getCommand("parkour").setExecutor(new ParkourCommand(this));
    }

    @Override
    public void onDisable() {
        unloadServices();

        hologramService.destroy();
    }

    private void loadServices(ConfigLoader... loaders){
        for (ConfigLoader loader : loaders) {
            loader.load(configManager);

            loadedService.add(loader);
        }
    }

    private void unloadServices(){
        loadedService.forEach(loader -> loader.unload(configManager));
    }

    private HologramAdapter detectCurrentVersion() {
        Matcher matcher = Pattern.compile("v\\d+_\\d+_R\\d+").matcher(Bukkit.getServer().getClass().getPackage().getName());
        if (!matcher.find()) {
            return null;
        }

        String nmsVersionName = matcher.group();

        switch(nmsVersionName){
            case "v1_19_R2" -> {
                return new it.dani.seniorparkour.nms.v1_19_R2.HologramAdapterImpl();
            }
            case "v1_19_R1" -> {
                return new it.dani.seniorparkour.nms.v1_19_R1.HologramAdapterImpl();
            }
            case "v1_18_R2" -> {
                return new it.dani.seniorparkour.nms.v1_18_R2.HologramAdapterImpl();
            }
            case "v1_18_R1" -> {
                return new it.dani.seniorparkour.nms.v1_18_R1.HologramAdapterImpl();
            }
            case "v1_17_R1" -> {
                return new it.dani.seniorparkour.nms.v1_17_R1.HologramAdapterImpl();
            }
            case "v1_16_R3" -> {
                return new it.dani.seniorparkour.nms.v1_16_R3.HologramAdapterImpl();
            }
            default -> {
                return null;
            }
        }
    }

}
