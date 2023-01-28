package it.dani.seniorparkour.services.holograms;

import it.dani.seniorparkour.SeniorParkour;
import it.dani.seniorparkour.configuration.ConfigType;
import it.dani.seniorparkour.database.entity.RPlayer;
import it.dani.seniorparkour.services.holograms.object.Hologram;
import it.dani.seniorparkour.services.parkour.Parkour;
import it.dani.seniorparkour.utils.Utils;
import it.dani.seniorparkour.commons.HologramAdapter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;


@RequiredArgsConstructor
public class HologramService {
    @Setter
    private HologramAdapter adapter;

    private final SeniorParkour plugin;

    private final List<Hologram> holograms = new ArrayList<>();


    public void createHologram(Location loc, HologramType type, Parkour parkour){
        switch (type) {
            case PARKOUR_END, PARKOUR_CHECKPOINT, PARKOUR_START -> {
                YamlConfiguration config = plugin.getConfigManager().getConfig(ConfigType.MAIN_CONFIG);
                List<String> lines = Utils.color(config.getStringList(type.getPath()));
                int height = config.getInt("holograms.height");
                createStaticHologram(loc, height,lines);
            }

            case PARKOUR_TOP -> createTopHologram(loc,parkour);
        }
    }


    private void createStaticHologram(Location loc, int height,List<String> lines){
        Hologram hologram = new Hologram(loc,height);

        lines.forEach(hologram::addLine);

        holograms.add(hologram);
    }

    private void createTopHologram(Location loc, Parkour parkour){
        Hologram hologram = new Hologram(loc,0);

        hologram.addLine("PARKOUR " + parkour.getName());

        hologram.addLine("", (armorStand) -> {
            int entityID = armorStand.getEntityId();
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

       hologram.addLine("Top 1", armorStand ->{
           try {
               RPlayer player = plugin.getDatabaseManager().getPlayerInPosition(parkour.getName(), 1).get();
               String line = player == null ? "N/A" : "1. " + player.username() + " - " + Utils.convertMillis(player.time());

               Bukkit.getScheduler().runTask(plugin, () -> armorStand.setCustomName(line));
           } catch (InterruptedException | ExecutionException e) {
               throw new RuntimeException(e);
           }
       });

        hologram.addLine("Top 2", armorStand ->{
            try {
                RPlayer player = plugin.getDatabaseManager().getPlayerInPosition(parkour.getName(), 2).get();
                String line = player == null ? "N/A" : "2. " + player.username() + " - " + Utils.convertMillis(player.time());

                Bukkit.getScheduler().runTask(plugin, () -> armorStand.setCustomName(line));
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        });

        hologram.addLine("Top 3", armorStand ->{
            try {
                RPlayer player = plugin.getDatabaseManager().getPlayerInPosition(parkour.getName(), 3).get();
                String line = player == null ? "N/A" : "3. " + player.username() + " - " + Utils.convertMillis(player.time());

                Bukkit.getScheduler().runTask(plugin, () -> armorStand.setCustomName(line));
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        });


        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin,hologram.updateTask(),0,1200);

    }

    public Optional<Hologram> getByLocation(Location startLocation){
        return holograms.stream()
                .filter(hologram -> hologram.getStartLocation().equals(startLocation))
                .findFirst();
    }

    public void destroy(){
        holograms.forEach(Hologram::destroy);
    }
}
