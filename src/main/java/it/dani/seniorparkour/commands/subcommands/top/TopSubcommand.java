package it.dani.seniorparkour.commands.subcommands.top;

import it.dani.seniorparkour.SeniorParkour;
import it.dani.seniorparkour.commands.Subcommand;
import org.bukkit.command.CommandSender;

public class TopSubcommand extends Subcommand {
    public TopSubcommand(SeniorParkour plugin) {
        super(plugin);
    }

    @Override
    public String getName() {
        return "top";
    }

    @Override
    public String getPermission() {
        return "parkour.top";
    }

    @Override
    public void dispatch(CommandSender sender, String[] args) {

    }
}
