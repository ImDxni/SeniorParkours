package it.dani.seniorparkour.configuration;

public interface ConfigLoader {

    void load(ConfigManager manager);


    default void unload(ConfigManager manager) {};
}
