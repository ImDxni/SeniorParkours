package it.dani.seniorparkour.services.scoreboard;

import it.dani.seniorparkour.configuration.ConfigLoader;
import it.dani.seniorparkour.configuration.ConfigManager;
import it.dani.seniorparkour.configuration.ConfigType;
import it.dani.seniorparkour.utils.Utils;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.*;

public class ScoreboardManager implements ConfigLoader,Runnable {
    private final Map<Player, Scoreboard> activePlayers = new HashMap<>();
    private final List<String> lines = new ArrayList<>();
    private String header;

    @Override
    public void run() {
        for (Player player : activePlayers.keySet()) {
            Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();

            Objective obj = scoreboard.registerNewObjective("srParkours","dummy","header");

            obj.setDisplayName(PlaceholderAPI.setPlaceholders(player,header));

            obj.setDisplaySlot(DisplaySlot.SIDEBAR);


            for (String line : PlaceholderAPI.setPlaceholders(player,lines)) {
                obj.getScore(line);
            }

            player.setScoreboard(scoreboard);

        }
    }

    
    public void setScoreboard(Player player,boolean toggle){
        if(toggle){
            activePlayers.put(player,player.getScoreboard());
        } else {
            player.setScoreboard(activePlayers.remove(player));
        }
    }


    @Override
    public void load(ConfigManager manager) {
        YamlConfiguration config = manager.getConfig(ConfigType.MAIN_CONFIG);
        header = Utils.color(config.getString("scoreboard.header"));
        lines.addAll(Utils.color(config.getStringList("scoreboard.lines")));
    }

}
