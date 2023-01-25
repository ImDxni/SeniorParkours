package it.dani.seniorparkour.commands.subcommands.info;

import it.dani.seniorparkour.SeniorParkour;
import it.dani.seniorparkour.commands.Subcommand;
import org.bukkit.command.CommandSender;

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

    }
}
