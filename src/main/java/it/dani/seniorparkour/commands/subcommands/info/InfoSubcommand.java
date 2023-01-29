package it.dani.seniorparkour.commands.subcommands.info;

import it.dani.seniorparkour.SeniorParkour;
import it.dani.seniorparkour.commands.Subcommand;
import it.dani.seniorparkour.configuration.Messages;
import it.dani.seniorparkour.inventories.impl.info.InfoInventory;
import it.dani.seniorparkour.services.parkour.ParkourService;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
        if(args.length < 1){
            return;
        }

        if(sender instanceof Player player) {
            String name = args[0];

            ParkourService service = getPlugin().getParkourService();
            service.getParkourByName(name).ifPresentOrElse((parkour) ->
                    new InfoInventory(getPlugin().getConfigManager(),parkour).getInventory()
                            .open(player), () -> sendMessage(sender,Messages.PARKOUR_NOT_FOUND));

        }
    }
}
