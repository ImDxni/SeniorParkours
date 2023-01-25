package it.dani.seniorparkour.configuration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ConfigType
{
    MAIN_CONFIG("config"),
    MESSAGES("messages"),
    INVENTORIES("inventories"),
    PARKOUR("parkours");


    private final String path;
}
