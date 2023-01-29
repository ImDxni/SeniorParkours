package it.dani.seniorparkour.listeners;

import it.dani.seniorparkour.services.parkour.ParkourService;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleFlightEvent;

@RequiredArgsConstructor
public class FlyListener implements Listener {
    private final ParkourService service;


    @EventHandler(priority = EventPriority.HIGH)
    public void onFly(PlayerToggleFlightEvent e){
        Player player = e.getPlayer();

        if(e.isFlying()){
            service.getParkourPlayer(player).ifPresent(parkourPlayer -> {
                service.removeActivePlayer(player);

                player.teleport(parkourPlayer.getParkour().getStart());
            });
        }
    }

}
