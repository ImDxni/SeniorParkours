package it.dani.seniorparkour.commands;

import it.dani.seniorparkour.SeniorParkour;
import it.dani.seniorparkour.services.parkour.Parkour;
import it.dani.seniorparkour.services.parkour.ParkourService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public abstract class Subcommand {
    @Getter
    private final SeniorParkour plugin;
    public abstract String getName();

    public abstract String getPermission();


    public abstract void dispatch(CommandSender sender, String[] args);

    public List<String> onTabComplete(String[] args) {
        ParkourService service = getPlugin().getParkourService();
        if (args.length == 0) {
            return service.getParkours().stream()
                    .map(Parkour::getName)
                    .collect(Collectors.toList());
        }

        return Collections.emptyList();
    }

}
