package it.dani.seniorparkour.services.holograms;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum HologramType {
    PARKOUR_START("holograms.start"),
    PARKOUR_CHECKPOINT("holograms.checkpoint"),
    PARKOUR_END("holograms.end"),
    PARKOUR_TOP("holograms.top");

    private final String path;
}
