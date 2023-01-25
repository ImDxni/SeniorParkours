package it.dani.seniorparkour.commands;

import it.dani.seniorparkour.SeniorParkour;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
public abstract class Subcommand {
    @Getter
    private final SeniorParkour plugin;
    public abstract String getName();

    public abstract String getPermission();


    public abstract void dispatch(CommandSender sender, String[] args);

    public List<String> onTabComplete(String[] args){
        return Collections.emptyList();
    }
}
