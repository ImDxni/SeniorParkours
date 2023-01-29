package it.dani.seniorparkour.commands.subcommands.management;

import it.dani.seniorparkour.SeniorParkour;
import it.dani.seniorparkour.commands.Subcommand;
import it.dani.seniorparkour.configuration.Messages;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CreateSubcommand extends Subcommand {

    public CreateSubcommand(SeniorParkour plugin) {
        super(plugin);
    }

    @Override
    public String getName() {
        return "create";
    }

    @Override
    public String getPermission() {
        return "parkour.admin.create";
    }

    @Override
    public void dispatch(CommandSender sender, String[] args) {
        if(args.length < 1){
            return;
        }

        if(sender instanceof Player player) {
            String name = args[0];

            if(getPlugin().getParkourService().getParkourByName(name).isPresent()){
                sendMessage(sender,Messages.PARKOUR_EXISTS);
                return;
            }

            getPlugin().getParkourService().createParkour(name,player.getLocation().getBlock());
            sendMessage(sender,Messages.PARKOUR_CREATED);
        }
    }
}
