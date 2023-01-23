package it.dani.seniorparkour;

import it.dani.seniorparkour.configuration.ConfigLoader;
import it.dani.seniorparkour.configuration.ConfigManager;
import it.dani.seniorparkour.configuration.ConfigType;
import it.dani.seniorparkour.database.DatabaseManager;
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

    @Override
    public void onEnable() {
        configManager = new ConfigManager(this);
        configManager.register(ConfigType.values());

        databaseManager = new DatabaseManager(this);

        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new ParkourExpansion().register();
        }

        scoreboardManager = new ScoreboardManager();

        loadServices(new ParkourService(this),scoreboardManager);


        Bukkit.getScheduler().runTaskTimer(this,scoreboardManager,0,5);
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
