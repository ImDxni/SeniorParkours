package it.dani.seniorparkour.commands.subcommands.top;

import it.dani.seniorparkour.SeniorParkour;
import it.dani.seniorparkour.commands.Subcommand;
import it.dani.seniorparkour.configuration.ConfigType;
import it.dani.seniorparkour.configuration.Messages;
import it.dani.seniorparkour.inventories.impl.TopInventory;
import it.dani.seniorparkour.services.parkour.ParkourService;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
        if (args.length < 1) {
            return;
        }

        if (sender instanceof Player player) {
            String name = args[0];

            ParkourService service = getPlugin().getParkourService();
            int limit = getPlugin().getConfigManager().getConfig(ConfigType.MAIN_CONFIG).getInt("top.limit");
            service.getParkourByName(name).ifPresentOrElse((parkour) ->
                            getPlugin().getDatabaseManager().getTop(name, limit)
                                    .thenAccept((result) -> {
                                        if(result.isEmpty()){
                                            sendMessage(sender,Messages.NO_TOP);
                                            return;
                                        }

                                        Bukkit.getScheduler().runTask(getPlugin(),
                                                () -> new TopInventory(getPlugin().getConfigManager(), result).getInventory()
                                                        .open(player));
                                    })
                    , () -> sendMessage(sender, Messages.PARKOUR_NOT_FOUND));

        }
    }
}
