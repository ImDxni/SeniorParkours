package it.dani.seniorparkour.database;

import it.dani.seniorparkour.SeniorParkour;
import it.dani.seniorparkour.configuration.ConfigType;
import org.bukkit.configuration.file.YamlConfiguration;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;


public class DatabaseManager{
    private final ConnectionManager connectionManager;
    public DatabaseManager(SeniorParkour plugin) {
        YamlConfiguration config = plugin.getConfigManager().getConfig(ConfigType.MAIN_CONFIG);

        connectionManager = ConnectionManager
                .builder()
                .hostname(config.getString("database.hostname"))
                .port(config.getInt("database.port"))
                .user(config.getString("database.username"))
                .password(config.getString("database.password"))
                .database(config.getString("database.name"))
                .build();


        createTables();
    }

    public void createTables(){
        String sql = "";
        try(Connection connection = connectionManager.getConnection();
            Statement statement = connection.createStatement()){

            statement.addBatch(sql);

            statement.executeBatch();
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

}
