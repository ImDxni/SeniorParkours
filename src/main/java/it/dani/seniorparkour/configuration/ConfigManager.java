package it.dani.seniorparkour.configuration;

import it.dani.seniorparkour.SeniorParkour;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@Getter
@RequiredArgsConstructor
public class ConfigManager {
    private final Map<ConfigType, FileManager> configurations = new HashMap<>();

    private final SeniorParkour plugin;

    public void register(ConfigType... type){
        for (ConfigType configType : type) {
            configurations.put(configType, new FileManager(configType.getPath(), plugin));
        }
    }

    public YamlConfiguration getConfig(ConfigType type){
        return getManager(type).getConfig();
    }

    public File getFile(ConfigType type){
        return getManager(type).getFile();
    }

    private FileManager getManager(ConfigType type){
        return configurations.get(type);
    }

}
