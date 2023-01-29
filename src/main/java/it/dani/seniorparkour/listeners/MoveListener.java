package it.dani.seniorparkour.listeners;

import it.dani.seniorparkour.SeniorParkour;
import it.dani.seniorparkour.configuration.ConfigManager;
import it.dani.seniorparkour.configuration.Messages;
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
    private final ConfigManager manager;

    public MoveListener(SeniorParkour plugin) {
        service = plugin.getParkourService();
        manager = plugin.getConfigManager();
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e){
        Player player = e.getPlayer();
        Optional<ParkourPlayer> optionalPlayer = service.getParkourPlayer(player);
        if(optionalPlayer.isPresent()){
            ParkourPlayer parkourPlayer = optionalPlayer.get();

            if(player.isFlying()){
                service.removeActivePlayer(player);

                player.teleport(parkourPlayer.getParkour().getStart());
                return;
            }

            Location checkpoint = parkourPlayer.getNextCheckpoint();
            if(checkpoint != null) {

                if (player.getLocation().getBlock().equals(checkpoint.getBlock())) {
                    parkourPlayer.incrementCheckpoint();

                    player.sendMessage(Messages.CHECKPOINT_PASSED.getMessage(manager));
                }
            } else {
                Location endPoint = parkourPlayer.getParkour().getEnd();
                if (endPoint != null) {


                    if (player.getLocation().getBlock().equals(endPoint.getBlock())) {
                        service.endParkour(player);

                        player.sendMessage(Messages.PARKOUR_FINISHED.getMessage(manager));
                    }
                }
            }

        } else {
            service.getParkourByStart(player.getLocation().getBlock()).ifPresent((parkour) -> {
                if(parkour.getEnd() == null)
                    return;

                service.startParkour(player,parkour);

                player.sendMessage(Messages.PARKOUR_STARTED.getMessage(manager));
            });
        }
    }
}
