package it.dani.seniorparkour.services.parkour;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Getter
public class Parkour {
    private final String name;
    private final Location start;

    private final List<Location> checkPoints = new ArrayList<>();

    @Setter
    private Location end;


    public void addCheckPoint(Location loc){
        checkPoints.add(loc);
    }


    public void removeCheckPoint(Location loc){
        checkPoints.remove(loc);
    }
}
