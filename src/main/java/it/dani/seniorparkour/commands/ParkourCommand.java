package it.dani.seniorparkour.commands;

import it.dani.seniorparkour.SeniorParkour;
import it.dani.seniorparkour.commands.subcommands.*;
import it.dani.seniorparkour.commands.subcommands.info.InfoSubcommand;
import it.dani.seniorparkour.commands.subcommands.info.StatsSubcommand;
import it.dani.seniorparkour.commands.subcommands.management.*;
import it.dani.seniorparkour.commands.subcommands.top.DelTopSubcommand;
import it.dani.seniorparkour.commands.subcommands.top.SetTopSubcommand;
import it.dani.seniorparkour.commands.subcommands.top.TopSubcommand;
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
    private final SeniorParkour plugin;

    public ParkourCommand(SeniorParkour plugin) {
        this.plugin = plugin;
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

        Optional<Subcommand> optionalSubcommand = getSubcommand(args[0]);
        if (optionalSubcommand.isPresent()) {
            String[] param = Arrays.copyOfRange(args, 1, args.length + 1);
            Subcommand subcommand = optionalSubcommand.get();
            if(sender.hasPermission(subcommand.getPermission())) {
                subcommand.dispatch(sender, param);
            }
        } else {
            sender.sendMessage("Comando Sconosciuto");
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, String[] args) {
        if (args.length <= 1) {
            return subCommands.stream()
                    .filter(subcommand -> sender.hasPermission(subcommand.getPermission()) &&
                            subcommand.getName().toLowerCase().startsWith(args[0].toLowerCase()))
                    .map(Subcommand::getName)
                    .collect(Collectors.toList());
        } else {
            Optional<Subcommand> optionalSubcommand = getSubcommand(args[0]);
            if (optionalSubcommand.isPresent()) {
                String[] param = Arrays.copyOfRange(args, 1, args.length + 1);
                Subcommand subcommand = optionalSubcommand.get();
                if(sender.hasPermission(subcommand.getPermission())) {
                    return optionalSubcommand.get().onTabComplete(param);
                }
            }
        }

        return Bukkit.getOnlinePlayers().stream()
                .map(Player::getName)
                .filter(name -> name.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
                .collect(Collectors.toList());
    }



    public void initialize() {
        register(new HelpSubcommand(),
                new CreateSubcommand(plugin),
                new CheckpointSubcommand(plugin),
                new DeleteSubcommand(plugin),
                new EndSubcommand(plugin),
                new TeleportSubcommand(plugin),
                new InfoSubcommand(plugin),
                new StatsSubcommand(plugin),
                new DelTopSubcommand(plugin),
                new SetTopSubcommand(plugin),
                new TopSubcommand(plugin));
    }

    private void register(Subcommand... subcommands) {
        subCommands.addAll(Arrays.asList(subcommands));
    }
}
