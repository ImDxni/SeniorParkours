package it.dani.seniorparkour.commons;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.util.function.Function;

@Getter
@Setter
public abstract class HologramAdapter {
    private int entityID = -1;

    public abstract void sendHologram(Function<Player,String> function);

}
