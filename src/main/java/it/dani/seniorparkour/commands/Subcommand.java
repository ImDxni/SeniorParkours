package it.dani.seniorparkour.commands;

import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

public abstract class Subcommand {
    public abstract String getName();

    public abstract String getPermission();


    public abstract void dispatch(CommandSender sender, String[] args);

    public List<String> onTabComplete(String[] args){
        return Collections.emptyList();
    }
}
