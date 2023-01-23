package it.dani.seniorparkour.services.parkour.object;

import it.dani.seniorparkour.services.parkour.Parkour;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
@Getter
public class ParkourPlayer {
    private final UUID uuid;
    private final Parkour parkour;
    private int checkpoint = 0;


    public void incrementCheckpoint(){
        checkpoint++;
    }
}
