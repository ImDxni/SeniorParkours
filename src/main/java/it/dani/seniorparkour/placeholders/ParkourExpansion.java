package it.dani.seniorparkour.placeholders;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ParkourExpansion extends PlaceholderExpansion {

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {

        return "";
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
