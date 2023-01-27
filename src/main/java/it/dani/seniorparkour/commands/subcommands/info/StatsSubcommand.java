package it.dani.seniorparkour.commands.subcommands.info;

import it.dani.seniorparkour.SeniorParkour;
import it.dani.seniorparkour.commands.Subcommand;
import it.dani.seniorparkour.inventories.impl.StatsInventory;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class StatsSubcommand extends Subcommand {
    public StatsSubcommand(SeniorParkour plugin) {
        super(plugin);
    }

    @Override
    public String getName() {
        return "stats";
    }

    @Override
    public String getPermission() {
        return "parkour.stats";
    }

    @Override
    public void dispatch(CommandSender sender, String[] args) {
        if (args.length < 1) {
            return;
        }

        if (sender instanceof Player player) {
            String target = args[0];

            getPlugin().getDatabaseManager().getStats(target).thenAccept((result) ->
                    Bukkit.getScheduler().runTask(getPlugin(),
                            () -> new StatsInventory(getPlugin().getConfigManager(), result).getInventory()
                                    .open(player)));
        }
    }

    @Override
    public List<String> onTabComplete(String[] args) {
        return Collections.emptyList();
    }
}
