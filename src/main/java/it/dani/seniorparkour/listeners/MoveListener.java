package it.dani.seniorparkour.listeners;

import it.dani.seniorparkour.SeniorParkour;
import it.dani.seniorparkour.database.DatabaseManager;
import it.dani.seniorparkour.database.entity.RPlayer;
import it.dani.seniorparkour.services.parkour.ParkourService;
import it.dani.seniorparkour.services.parkour.object.ParkourPlayer;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.Optional;

@RequiredArgsConstructor
public class MoveListener implements Listener {
    private final ParkourService service;
    private final DatabaseManager databaseManager;

    public MoveListener(SeniorParkour plugin) {
        service = plugin.getParkourService();
        databaseManager = plugin.getDatabaseManager();
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e){
        Player player = e.getPlayer();
        Optional<ParkourPlayer> optionalPlayer = service.getParkourPlayer(player);
        if(optionalPlayer.isPresent()){
            ParkourPlayer parkourPlayer = optionalPlayer.get();

            Location checkpoint = parkourPlayer.getNextCheckpoint();
            if(checkpoint != null) {

                if (player.getLocation().getBlock().equals(checkpoint.getBlock())) {
                    parkourPlayer.incrementCheckpoint();

                    player.sendMessage("Check point superato");
                }
            } else {
                Location endPoint = parkourPlayer.getParkour().getEnd();
                if (endPoint != null) {


                    if (player.getLocation().getBlock().equals(endPoint.getBlock())) {
                        service.endParkour(player);
                        player.sendMessage("PARKOUR FINITO");
                    }
                }
            }

        } else {
            service.getParkourByStart(player.getLocation().getBlock()).ifPresent((parkour) -> {
                service.startParkour(player,parkour);

                player.sendMessage("PARKOUR INIZIATO");
            });
        }
    }
}
