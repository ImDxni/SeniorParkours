package it.dani.seniorparkour.database;

import it.dani.seniorparkour.SeniorParkour;
import it.dani.seniorparkour.configuration.ConfigType;
import it.dani.seniorparkour.database.entity.RPlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;


public class DatabaseManager {
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

    public void createTables() {
        String sql = "CREATE TABLE IF NOT EXISTS parkour(uuid VARCHAR(36) PRIMARY KEY,username VARCHAR(16), parkour VARCHAR(20), time BIGINT);";

        try (Connection connection = connectionManager.getConnection();
             Statement statement = connection.createStatement()) {

            statement.addBatch(sql);

            statement.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertPlayer(RPlayer entity){
        playerExists(entity.uuid(),entity.parkour()).thenAccept((exists) -> {
            if(exists)
                return;

            String query = "INSERT INTO parkour(uuid,username,parkour,time) VALUES(?,?,?,?)";

            try (Connection connection = connectionManager.getConnection();
                 PreparedStatement statement = connection.prepareStatement(query)) {

                statement.setString(1, entity.uuid().toString());
                statement.setString(2, entity.username());
                statement.setString(3, entity.parkour());
                statement.setLong(4, entity.time());


                statement.executeUpdate();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }


    private CompletableFuture<Boolean> playerExists(UUID player, String parkour){
        String query = "SELECT * FROM parkour WHERE uuid=? AND parkour=?";

        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = connectionManager.getConnection();
                 PreparedStatement statement = connection.prepareStatement(query)) {

                statement.setString(1, player.toString());
                statement.setString(2,parkour);

                ResultSet result = statement.executeQuery();

                return result.next();

            } catch (SQLException e) {
                e.printStackTrace();
            }

            return false;
        });
    }

    public CompletableFuture<Long> getTime(Player player, String parkour) {
        String query = "SELECT time FROM parkour WHERE uuid=? AND parkour=?;";
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = connectionManager.getConnection();
                 PreparedStatement statement = connection.prepareStatement(query)) {

                statement.setString(1, player.getUniqueId().toString());
                statement.setString(2, parkour);

                ResultSet result = statement.executeQuery();

                if (result.next()) {
                    return result.getLong(1);
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }

            return 0L;
        });
    }

    public CompletableFuture<Integer> getPosition(Player player, String parkour){
        String query = "SELECT COUNT(*) FROM parkour WHERE uuid=? AND parkour=?;";
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = connectionManager.getConnection();
                 PreparedStatement statement = connection.prepareStatement(query)) {

                statement.setString(1, player.getUniqueId().toString());
                statement.setString(2, parkour);

                ResultSet result = statement.executeQuery();

                if (result.next()) {
                    return result.getInt(1);
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }

            return -1;
        });
    }

}
