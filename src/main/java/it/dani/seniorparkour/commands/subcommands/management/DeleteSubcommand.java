package it.dani.seniorparkour.commands.subcommands.management;

import it.dani.seniorparkour.SeniorParkour;
import it.dani.seniorparkour.commands.Subcommand;
import it.dani.seniorparkour.configuration.Messages;
import it.dani.seniorparkour.services.parkour.ParkourService;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DeleteSubcommand extends Subcommand {
    public DeleteSubcommand(SeniorParkour plugin) {
        super(plugin);
    }

    @Override
    public String getName() {
        return "delete";
    }

    @Override
    public String getPermission() {
        return "parkour.admin.delete";
    }

    @Override
    public void dispatch(CommandSender sender, String[] args) {
        if(args.length < 1){
            return;
        }

        if(sender instanceof Player) {
            String name = args[0];

            ParkourService service = getPlugin().getParkourService();
            service.getParkourByName(name).ifPresentOrElse((parkour) -> {
                if(args.length == 1) {
                    service.deleteParkour(parkour);
                    sendMessage(sender,Messages.PARKOUR_DELETED);
                } else {
                    int checkpoint;
                    if(NumberUtils.isNumber(args[1])){
                        checkpoint = Integer.parseInt(args[1]);
                    } else {
                        sendMessage(sender,Messages.INVALID_FORMAT);
                        return;
                    }

                    if(checkpoint >= 0 && checkpoint < parkour.getCheckPoints().size()) {
                        service.removeCheckPoint(parkour,checkpoint-1);
                        sendMessage(sender,Messages.CHECKPOINT_REMOVED);
                    } else {
                        sendMessage(sender,Messages.CHECKPOINT_NOT_FOUND);
                    }
                }
            },() -> sendMessage(sender,Messages.PARKOUR_NOT_FOUND));

        }
    }
}
