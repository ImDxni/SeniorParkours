package it.dani.seniorparkour.services.scoreboard;

import it.dani.seniorparkour.configuration.ConfigLoader;
import it.dani.seniorparkour.configuration.ConfigManager;
import it.dani.seniorparkour.configuration.ConfigType;
import it.dani.seniorparkour.utils.Utils;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.*;

public class ScoreboardManager implements ConfigLoader,Runnable {
    private final Map<Player, Scoreboard> activePlayers = new HashMap<>();

    private final List<String> lines = new ArrayList<>();
    private String header;

    @Override
    public void run() {
        for (Player player : activePlayers.keySet()) {
            updatePlayer(player);
        }
    }

    
    public void setScoreboard(Player player,boolean toggle){
        if(toggle){
            activePlayers.put(player,player.getScoreboard());

            Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();

            Objective obj = scoreboard.registerNewObjective("srParkours","dummy","header");
            obj.setDisplayName(PlaceholderAPI.setPlaceholders(player,header));
            obj.setDisplaySlot(DisplaySlot.SIDEBAR);

            player.setScoreboard(scoreboard);

            setupScores(player,obj);
        } else {
            Scoreboard board = activePlayers.remove(player);

            if(board != null){
                player.setScoreboard(board);
            }
        }
    }

    private void setupScores(Player player,Objective obj){
        int i = lines.size();

        Scoreboard board = player.getScoreboard();

        for (String line : PlaceholderAPI.setPlaceholders(player,lines)) {
            Team team = board.registerNewTeam("line-"+i);
            String entry = ChatColor.values()[i]+"";
            team.addEntry(entry);
            team.setPrefix(line);

            Score score = obj.getScore(entry);
            score.setScore(i--);
        }
    }

    private void updatePlayer(Player player){
        Scoreboard board = player.getScoreboard();
        int i = lines.size();

        for (String line : PlaceholderAPI.setPlaceholders(player,lines)) {
            Team team = board.getTeam("line-"+i--);
            if(team != null)
                team.setPrefix(line);
        }
    }


    @Override
    public void load(ConfigManager manager) {
        YamlConfiguration config = manager.getConfig(ConfigType.MAIN_CONFIG);
        header = Utils.color(config.getString("scoreboard.header"));
        lines.addAll(Utils.color(config.getStringList("scoreboard.lines")));
    }

}
