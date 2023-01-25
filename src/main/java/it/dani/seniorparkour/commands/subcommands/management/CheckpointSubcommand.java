package it.dani.seniorparkour.commands.subcommands.management;

import it.dani.seniorparkour.SeniorParkour;
import it.dani.seniorparkour.commands.Subcommand;
import it.dani.seniorparkour.services.parkour.ParkourService;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CheckpointSubcommand extends Subcommand {
    public CheckpointSubcommand(SeniorParkour plugin) {
        super(plugin);
    }

    @Override
    public String getName() {
        return "checkpoint";
    }

    @Override
    public String getPermission() {
        return "parkour.admin.checkpoint";
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
                service.addCheckPoint(parkour,player.getLocation().getBlock());
                sender.sendMessage("CHECKPOINT AGGIUNTO");
            },() -> sender.sendMessage("PARKOUR NON TROVATO"));

        }
    }
}
