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
        Location centerLoc = loc.add(0.5,0,0.5);
        Hologram hologram = new Hologram(centerLoc,height);

        lines.forEach(hologram::addLine);

        holograms.add(hologram);
    }

    private void createTopHologram(Location loc, Parkour parkour){
        Hologram hologram = new Hologram(loc,0);
        YamlConfiguration config = plugin.getConfigManager().getConfig(ConfigType.MAIN_CONFIG);
        List<String> lines = Utils.color(config.getStringList(HologramType.PARKOUR_TOP.getPath()+".lines"));

        for (String line : lines) {
            if (line.startsWith("%top")) {
                int index = Integer.parseInt(line.replace("%", "").split("_")[1]);
                String format = Utils.color(config.getString(HologramType.PARKOUR_TOP.getPath() + ".format.top.result"));
                String fallback = Utils.color(config.getString(HologramType.PARKOUR_TOP.getPath()+".format.top.fallback"));

                hologram.addLine("N/A", armorStand -> {
                    try {
                        RPlayer player = plugin.getDatabaseManager().getPlayerInPosition(parkour.getName(), index).get();

                        String result = player == null ? fallback : format.replace("%player%", player.username())
                                .replace("%position%", String.valueOf(index))
                                .replace("%time%", Utils.convertMillis(player.time()));

                        Bukkit.getScheduler().runTask(plugin, () -> armorStand.setCustomName(result));
                    } catch (InterruptedException | ExecutionException e) {
                        throw new RuntimeException(e);
                    }
                });
            } else if (line.contains("%personal%")) {

                String format = Utils.color(config.getString(HologramType.PARKOUR_TOP.getPath() + ".format.personal"));

                hologram.addLine("", (armorStand) -> {
                    int entityID = armorStand.getEntityId();
                    adapter.setEntityID(entityID);
                    adapter.sendHologram((player) -> {
                        String personal;
                        try {
                            personal = plugin.getDatabaseManager().getTime(player, parkour.getName())
                                    .thenCombine(plugin.getDatabaseManager().getPosition(player, parkour.getName()),
                                            (time, position) -> format.replace("%position%", String.valueOf(position))
                                                    .replace("%time%", Utils.convertMillis(time))
                                                    .replace("%player%",player.getName())).get();
                        } catch (InterruptedException | ExecutionException e) {
                            throw new RuntimeException(e);
                        }

                        return line.replace("%personal%", personal);
                    });
                });
            } else {
                hologram.addLine(line.replace("%parkour%", parkour.getName()));
            }
        }

        holograms.add(hologram);

        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin,hologram.updateTask(),0,1200);

    }

    public Optional<Hologram> getByLocation(Location startLocation){
        return holograms.stream()
                .filter(hologram -> hologram.getStartLocation().equals(startLocation) || hologram.getStartLocation().getBlock().equals(startLocation.getBlock()))
                .findFirst();
    }

    public void destroy(){
        holograms.forEach(Hologram::destroy);
    }
}
