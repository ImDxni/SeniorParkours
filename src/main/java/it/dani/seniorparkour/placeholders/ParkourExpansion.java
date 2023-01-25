package it.dani.seniorparkour.placeholders;

import it.dani.seniorparkour.services.parkour.Parkour;
import it.dani.seniorparkour.services.parkour.ParkourService;
import it.dani.seniorparkour.services.parkour.object.ParkourPlayer;
import it.dani.seniorparkour.utils.Utils;
import lombok.RequiredArgsConstructor;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.Optional;

@RequiredArgsConstructor
public class ParkourExpansion extends PlaceholderExpansion {

    private final ParkourService service;

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
        String[] args = params.split("_");

        Optional<ParkourPlayer> optionalPlayer = service.getParkourPlayer(player);

        String fallback = "N/A";


        if(optionalPlayer.isEmpty())
            return fallback;

        ParkourPlayer parkourPlayer = optionalPlayer.get();
        switch(args[0].toLowerCase(Locale.ROOT)){
            case "name" -> {
                return parkourPlayer.getParkour().getName();
            }
            case "time" -> {
                long time = parkourPlayer.getParkourTime();
                return Utils.convertMillis(time);
            }

            case "position" -> {
                int position = parkourPlayer.getPosition();

                if (position != -1)
                    return String.valueOf(position);
            }

            case "record" -> {
                long time = parkourPlayer.getRecordTime();

                if(time != -1)
                    return Utils.convertMillis(time);
            }
        }

        return fallback;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "parkour";
    }

    @Override
    public @NotNull String getAuthor() {
        return "ImDxni";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }
}
