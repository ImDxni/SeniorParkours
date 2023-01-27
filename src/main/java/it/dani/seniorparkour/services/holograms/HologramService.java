package it.dani.seniorparkour.services.holograms;

import it.dani.seniorparkour.SeniorParkour;
import it.dani.seniorparkour.configuration.ConfigLoader;
import it.dani.seniorparkour.configuration.ConfigManager;
import it.dani.seniorparkour.database.entity.RPlayer;
import it.dani.seniorparkour.nms.HologramAdapter;
import it.dani.seniorparkour.services.holograms.object.Hologram;
import it.dani.seniorparkour.services.parkour.Parkour;
import it.dani.seniorparkour.utils.Utils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.minecraft.server.v1_16_R3.EntityLiving;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;


@RequiredArgsConstructor
public class HologramService implements ConfigLoader {
    @Setter
    private HologramAdapter adapter;

    private final SeniorParkour plugin;

    private final List<Hologram> holograms = new ArrayList<>();


    public void createHologram(Location loc, HologramType type, Parkour parkour){
        switch (type) {
            case PARKOUR_END, PARKOUR_CHECKPOINT, PARKOUR_START -> {
                List<String> lines = new ArrayList<>();
                createStaticHologram(loc, lines);
            }

            case PARKOUR_TOP -> createTopHologram(loc,parkour);
        }
    }


    private void createStaticHologram(Location loc, List<String> lines){
        Hologram hologram = new Hologram(loc);

        lines.forEach(hologram::addLine);

        holograms.add(hologram);
    }

    private void createTopHologram(Location loc, Parkour parkour){
        Hologram hologram = new Hologram(loc);

        hologram.addLine("PARKOUR " + parkour.getName());

        hologram.addLine("", (armorStand) -> {
            int entityID = ((EntityLiving)armorStand).getId();
            adapter.setEntityID(entityID);
            adapter.sendHologram((player) -> {
                try {
                    return plugin.getDatabaseManager().getTime(player, parkour.getName())
                            .thenCombine(plugin.getDatabaseManager().getPosition(player, parkour.getName()),
                                    (time, position) -> position +". " + Utils.convertMillis(time)).get();
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
            });
        });

        plugin.getDatabaseManager().getTop(parkour.getName(), 3).thenAccept(result -> {
            Bukkit.getScheduler().runTask(plugin, () -> {
                for (RPlayer rPlayer : result) {
                     hologram.addLine("1. " + rPlayer.username() + " - " + Utils.convertMillis(rPlayer.time()));
                }
            });
        });

        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin,hologram.updateTask(),0,1200);

    }

    @Override
    public void load(ConfigManager manager) {
        //TODO LOADING
    }

    @Override
    public void unload(ConfigManager manager) {
        ConfigLoader.super.unload(manager);
    }
}
