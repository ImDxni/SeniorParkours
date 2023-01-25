package it.dani.seniorparkour.commands.subcommands.info;

import it.dani.seniorparkour.SeniorParkour;
import it.dani.seniorparkour.commands.Subcommand;
import org.bukkit.command.CommandSender;

public class InfoSubcommand extends Subcommand {
    public InfoSubcommand(SeniorParkour plugin) {
        super(plugin);
    }

    @Override
    public String getName() {
        return "info";
    }

    @Override
    public String getPermission() {
        return "parkour.admin.info";
    }

    @Override
    public void dispatch(CommandSender sender, String[] args) {

    }
}
