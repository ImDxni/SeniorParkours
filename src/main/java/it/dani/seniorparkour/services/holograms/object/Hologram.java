package it.dani.seniorparkour.services.holograms.object;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;


public class Hologram {
    @Getter
    private final Location startLocation;
    private Location linePosition;

    private final Map<ArmorStand, Consumer<ArmorStand>> lines = new HashMap<>();

    public Hologram(Location location,int height){
        this.startLocation = location;
        linePosition = location.clone().add(0,height,0);
    }

    public void addLine(String line){
        lines.put(createLine(linePosition,line),(entity) -> {});

        linePosition = linePosition.subtract(0,0.4,0);
    }

    public void addLine(String initialLine, Consumer<ArmorStand> updater){
        ArmorStand stand = createLine(linePosition,initialLine);

        lines.put(stand,updater);



        linePosition = linePosition.subtract(0,0.4,0);
    }

    public Runnable updateTask(){
        return () -> {
            for (Map.Entry<ArmorStand, Consumer<ArmorStand>> entry : lines.entrySet()) {
                entry.getValue().accept(entry.getKey());
            }
        };
    }

    public void destroy(){
        for (ArmorStand entity : lines.keySet()) {
            entity.remove();
        }

        lines.clear();
    }

    private ArmorStand createLine(Location loc, String line){
        ArmorStand armorstand = (ArmorStand) loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
        armorstand.setInvisible(true);
        armorstand.setInvulnerable(true);
        armorstand.setBasePlate(false);
        armorstand.setMarker(true);
        armorstand.setCollidable(false);
        armorstand.setAI(false);
        armorstand.setGravity(false);
        armorstand.setCustomNameVisible(true);
        armorstand.setCustomName(line);

        return armorstand;
    }
}
