package it.dani.seniorparkour.configuration;


import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class FileManager {
    private final String name;
    private final JavaPlugin plugin;
    private final File file;
    private YamlConfiguration config;

    public FileManager(String name, JavaPlugin plugin) {
        this.name = name;
        this.plugin = plugin;

        file = new File(plugin.getDataFolder(), name + ".yml");
        if(file.exists()) {
            config = YamlConfiguration.loadConfiguration(file);
        }

        createFolder();
        saveDefault();
    }

    public File getFolder() {
        return plugin.getDataFolder();
    }

    public void createFolder() {
        File folder = getFolder();
        if (!folder.exists()) folder.mkdir();

    }

    public void saveDefault() {
        if (file.exists()) return;

        createFolder();
        try {
            file.createNewFile();
            InputStream in = plugin.getResource(name + ".yml");
            OutputStream out = Files.newOutputStream(file.toPath());
            Files.copy(in, Paths.get(file.getPath()), StandardCopyOption.REPLACE_EXISTING);

            in.close();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public YamlConfiguration getConfig(){
        return config == null ? YamlConfiguration.loadConfiguration(file) : config;
    }

    public File getFile(){
        return file;
    }

}