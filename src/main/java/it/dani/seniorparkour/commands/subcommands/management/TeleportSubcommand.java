package it.dani.seniorparkour.commands.subcommands.management;

import it.dani.seniorparkour.SeniorParkour;
import it.dani.seniorparkour.commands.Subcommand;
import it.dani.seniorparkour.configuration.Messages;
import it.dani.seniorparkour.services.parkour.ParkourService;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeleportSubcommand extends Subcommand {
    public TeleportSubcommand(SeniorParkour plugin) {
        super(plugin);
    }

    @Override
    public String getName() {
        return "teleport";
    }

    @Override
    public String getPermission() {
        return "parkour.admin.teleport";
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
                Location loc;
                if(args.length < 2) {
                    loc = parkour.getStart();
                } else {
                    int checkpoint;
                    if(NumberUtils.isNumber(args[1])){
                        checkpoint = Integer.parseInt(args[1]) -1;
                        if(checkpoint >= 0 && checkpoint < parkour.getCheckPoints().size()) {
                            loc = parkour.getCheckPoints().get(checkpoint);
                        } else {
                            sendMessage(sender, Messages.CHECKPOINT_NOT_FOUND);
                            return;
                        }
                    } else {
                        sendMessage(sender,Messages.INVALID_FORMAT);
                        return;
                    }
                }

                player.teleport(loc);
            },() -> sendMessage(sender,Messages.PARKOUR_NOT_FOUND));

        }
    }
}
