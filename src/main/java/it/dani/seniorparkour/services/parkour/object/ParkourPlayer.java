package it.dani.seniorparkour.services.parkour.object;

import it.dani.seniorparkour.services.parkour.Parkour;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.Location;

import java.util.UUID;

@RequiredArgsConstructor
@Getter
public class ParkourPlayer {
    private final UUID uuid;
    private final Parkour parkour;
    private int checkpoint = 0;

    @Getter
    @Setter
    private int position = -1;

    @Getter
    @Setter
    private long recordTime = -1;

    private final long startTime = System.currentTimeMillis();



    public void incrementCheckpoint(){
        checkpoint++;
    }

    public Location getNextCheckpoint(){
        int index = checkpoint + 1;

        if(parkour.getCheckPoints().size() == index){
            return null;
        }

        return parkour.getCheckPoints().get(checkpoint + 1);
    }

    public long getParkourTime(){
        return System.currentTimeMillis() - startTime;
    }
}
