package it.dani.seniorparkour.listeners;

import it.dani.seniorparkour.services.parkour.ParkourService;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

@RequiredArgsConstructor
public class QuitListener implements Listener {
    private final ParkourService service;

    @EventHandler
    public void onQuit(PlayerQuitEvent e){
        service.removeActivePlayer(e.getPlayer());
    }
}
