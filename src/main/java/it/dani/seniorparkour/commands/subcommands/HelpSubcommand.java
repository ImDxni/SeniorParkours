package it.dani.seniorparkour.commands.subcommands;

import it.dani.seniorparkour.SeniorParkour;
import it.dani.seniorparkour.commands.Subcommand;
import org.bukkit.command.CommandSender;

public class HelpSubcommand extends Subcommand {
    public HelpSubcommand(SeniorParkour plugin) {
        super(plugin);
    }

    @Override
    public void dispatch(CommandSender sender, String[] args) {
        sender.sendMessage("HELP COMMAND");
    }

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getPermission() {
        return null;
    }

}
