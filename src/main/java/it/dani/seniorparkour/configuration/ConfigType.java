package it.dani.seniorparkour.configuration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ConfigType
{
    MAIN_CONFIG("config"),
    MESSAGES("messages"),
    PARKOUR("parkours");


    private final String path;
}
