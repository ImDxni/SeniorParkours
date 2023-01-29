package it.dani.seniorparkour.commands.subcommands.management;

import it.dani.seniorparkour.SeniorParkour;
import it.dani.seniorparkour.commands.Subcommand;
import it.dani.seniorparkour.configuration.Messages;
import it.dani.seniorparkour.services.parkour.ParkourService;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EndSubcommand extends Subcommand {
    public EndSubcommand(SeniorParkour plugin) {
        super(plugin);
    }

    @Override
    public String getName() {
        return "end";
    }

    @Override
    public String getPermission() {
        return "parkour.admin.end";
    }

    @Override
    public void dispatch(CommandSender sender, String[] args) {
        if(args.length < 1){
            return;
        }

        if(sender instanceof Player player) {
            String name = args[0];

            ParkourService service = getPlugin().getParkourService();
            service.getParkourByName(name).ifPresentOrElse((parkour) -> {
                service.addEndPoint(parkour,player.getLocation().getBlock());

                sendMessage(sender,Messages.PARKOUR_END_CREATED);
            },() -> sendMessage(sender,Messages.PARKOUR_NOT_FOUND));

        }
    }
}
