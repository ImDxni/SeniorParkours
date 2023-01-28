package it.dani.seniorparkour.database;

import it.dani.seniorparkour.SeniorParkour;
import it.dani.seniorparkour.configuration.ConfigType;
import it.dani.seniorparkour.database.entity.RPlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;


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
            if(exists) {
                updatePlayer(entity);
                return;
            }

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

    public void updatePlayer(RPlayer entity){
        String query = "UPDATE parkour SET time=? WHERE uuid=? AND parkour=? AND time>?";

        CompletableFuture.runAsync(() -> {
            try (Connection connection = connectionManager.getConnection();
                 PreparedStatement statement = connection.prepareStatement(query)) {

                statement.setLong(1, entity.time());
                statement.setString(2, entity.uuid().toString());
                statement.setString(3, entity.parkour());
                statement.setLong(4,entity.time());


                statement.executeUpdate();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public void deleteStats(String parkour){
        String sql = "DELETE FROM parkour WHERE parkour=?";

        try(Connection connection = connectionManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)){

            statement.setString(1,parkour);

            statement.executeUpdate();
        }catch(SQLException e){
            e.printStackTrace();
        }
    }


    public CompletableFuture<Boolean> playerExists(UUID player, String parkour){
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

    public CompletableFuture<Map<RPlayer,Integer>> getStats(String player){
        String query = "SELECT * FROM parkour WHERE username=?";

        return CompletableFuture.supplyAsync(() -> {
            List<RPlayer> records = new ArrayList<>();
            try(Connection connection = connectionManager.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)){

                statement.setString(1,player);

                ResultSet result = statement.executeQuery();

                while(result.next()){
                    UUID uuid = UUID.fromString(result.getString(1));
                    //Second string is username
                    String parkour = result.getString(3);
                    long time = result.getLong(4);

                    records.add(new RPlayer(uuid,player,parkour,time));
                }

            }catch(SQLException e){
                e.printStackTrace();
            }
            return records;
        }).thenApplyAsync((players) -> {
            Map<RPlayer,Integer> result = new HashMap<>();
            for (RPlayer rPlayer : players) {
                try {
                    result.put(rPlayer,getPosition(rPlayer.uuid(),rPlayer.parkour()).get());
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
            }

            return result;
        });
    }

    public CompletableFuture<List<RPlayer>> getTop(String parkour, int limit){
        String query = "SELECT * FROM parkour WHERE parkour=? ORDER BY time LIMIT ?";
        return CompletableFuture.supplyAsync(() -> {
            List<RPlayer> records = new ArrayList<>();
            try(Connection connection = connectionManager.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)){

                statement.setString(1,parkour);
                statement.setInt(2,limit);

                ResultSet result = statement.executeQuery();

                while(result.next()){
                    UUID uuid = UUID.fromString(result.getString(1));
                    String username = result.getString(2);
                    long time = result.getLong(4);

                    records.add(new RPlayer(uuid,username,parkour,time));
                }

            }catch(SQLException e){
                e.printStackTrace();
            }
            return records;
        });
    }

    public CompletableFuture<RPlayer> getPlayerInPosition(String parkour, int position){
        return getTop(parkour,position).thenApply(result -> {
            if(position >= result.size()){
                return null;
            }

            return result.get(result.size()-1);
        });
    }

    public CompletableFuture<Integer> getPosition(Player player, String parkour){
        return getPosition(player.getUniqueId(),parkour);
    }

    private CompletableFuture<Integer> getPosition(UUID uuid,String parkour){
        String query = "SELECT COUNT(uuid)+1 FROM parkour WHERE parkour=? AND time < (SELECT time FROM parkour WHERE uuid=? AND parkour=?);";
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = connectionManager.getConnection();
                 PreparedStatement statement = connection.prepareStatement(query)) {

                statement.setString(1, uuid.toString());
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
