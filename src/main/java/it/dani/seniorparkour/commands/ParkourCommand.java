package it.dani.seniorparkour.commands;

import it.dani.seniorparkour.commands.subcommands.HelpSubcommand;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class ParkourCommand implements CommandExecutor, TabExecutor {
    private final Set<Subcommand> subCommands = new LinkedHashSet<>();

    public ParkourCommand() {
        initialize();
    }

    protected Optional<Subcommand> getSubcommand(String name) {
        return subCommands.stream().filter(subcommand -> subcommand.getName().toLowerCase().startsWith(name.toLowerCase())).findAny();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length == 0) {
            Optional<Subcommand> help = getSubcommand("help");
            help.ifPresent(subcommand -> subcommand.dispatch(sender, args));
            return true;
        }

        Optional<Subcommand> subcommand = getSubcommand(args[0]);
        if (subcommand.isPresent()) {
            String[] param = Arrays.copyOfRange(args, 1, args.length + 1);
            subcommand.get().dispatch(sender, param);
        } else {
            sender.sendMessage("Comando Sconosciuto");
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, String[] args) {
        if (args.length <= 1)
            return subCommands.stream()
                    .map(Subcommand::getName)
                    .filter(name -> name.toLowerCase().startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        else {
            Optional<Subcommand> subcommand = getSubcommand(args[0]);
            if (subcommand.isPresent()) {
                String[] param = Arrays.copyOfRange(args, 1, args.length + 1);
                return subcommand.get().onTabComplete(param);
            } else
                return Bukkit.getOnlinePlayers().stream()
                        .map(Player::getName)
                        .filter(name -> name.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
                        .collect(Collectors.toList());
        }
    }



    public void initialize() {
        register(new HelpSubcommand());
    }

    private void register(Subcommand... subcommands) {
        subCommands.addAll(Arrays.asList(subcommands));
    }
}
