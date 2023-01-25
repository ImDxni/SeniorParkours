package it.dani.seniorparkour;

import it.dani.seniorparkour.commands.ParkourCommand;
import it.dani.seniorparkour.configuration.ConfigLoader;
import it.dani.seniorparkour.configuration.ConfigManager;
import it.dani.seniorparkour.configuration.ConfigType;
import it.dani.seniorparkour.database.DatabaseManager;
import it.dani.seniorparkour.listeners.FlyListener;
import it.dani.seniorparkour.listeners.MoveListener;
import it.dani.seniorparkour.placeholders.ParkourExpansion;
import it.dani.seniorparkour.services.parkour.ParkourService;
import it.dani.seniorparkour.services.scoreboard.ScoreboardManager;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;

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

    @Override
    public void onEnable() {
        configManager = new ConfigManager(this);
        configManager.register(ConfigType.values());

        databaseManager = new DatabaseManager(this);


        scoreboardManager = new ScoreboardManager();
        parkourService = new ParkourService(this);

        loadServices(parkourService,scoreboardManager);


        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new ParkourExpansion(parkourService).register();
        }

        Bukkit.getPluginManager().registerEvents(new FlyListener(parkourService),this);
        Bukkit.getPluginManager().registerEvents(new MoveListener(this),this);


        Bukkit.getScheduler().runTaskTimer(this,scoreboardManager,0,5);

        getCommand("parkour").setExecutor(new ParkourCommand(this));

        //TODO MESSAGGI CONFIGURABILI - OLOGRAMMI
    }

    @Override
    public void onDisable() {

        unloadServices();
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
}
