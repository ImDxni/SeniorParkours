package it.dani.seniorparkour.database.entity;

import java.util.UUID;

public record RPlayer(
        UUID uuid,
        String username,
        String parkour,
        long time
) {}
