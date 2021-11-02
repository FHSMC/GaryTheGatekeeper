package fhsmc.minecraft.gary;

import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.sql.*;

public class Storage {

    private static Connection conn = null;
    private static Statement statement = null;

    public static void open(String databaseFilePath) throws SQLException, ClassNotFoundException {
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:" + databaseFilePath);
            statement = conn.createStatement();
            statement.setQueryTimeout(30);

            statement.executeUpdate("CREATE TABLE IF NOT EXISTS players (id INTEGER PRIMARY KEY AUTOINCREMENT, discord_id INTEGER, java_ign TEXT, java_uuid TEXT, bedrock_ign TEXT, bedrock_uuid TEXT)");

        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Gary encountered a problem while trying to connect to a database: " + e.getMessage());
            throw e;
        }
    }

    public static void setIGNFromDiscord(String discord_id, String ign, boolean bedrock) throws SQLException {
        String platform = bedrock ? "bedrock" : "java";
        if (conn != null && statement != null) {
            if (discordUserInWhitelist(discord_id)){
                statement.executeUpdate("UPDATE players SET " + platform + "_ign=\"" + ign + "\" WHERE discord_id=" + discord_id);
            } else {
                statement.executeUpdate("INSERT INTO players (discord_id," + platform + "_ign) VALUES (" + discord_id + ",\"" + ign + "\")");
            }
        }
    }

    public static void setUUIDFromIGN(String ign, String uuid, boolean bedrock) throws SQLException {
        String platform = bedrock ? "bedrock" : "java";
        if (conn != null && statement != null) {
            statement.executeUpdate("UPDATE players SET " + platform + "_uuid=\"" + uuid + "\" WHERE " + platform + "_ign=\"" + ign + "\"");
        }
    }

    public static void setIGNFromUUID(String uuid, String ign, boolean bedrock) throws SQLException {
        String platform = bedrock ? "bedrock" : "java";
        if (conn != null && statement != null) {
            statement.executeUpdate("UPDATE players SET " + platform + "_ign=\"" + ign + "\" WHERE " + platform + "_uuid=\"" + uuid + "\"");
        }
    }

    public static boolean discordUserInWhitelist(String discord_id) throws SQLException {
        if (conn != null && statement != null) {
            ResultSet rs = statement.executeQuery("SELECT * FROM players WHERE discord_id=" + discord_id);
            return rs.isBeforeFirst();
        }
        return false;
    }

    public static boolean isPlayerUUIDWhitelisted(String uuid) throws SQLException {
        if (conn != null && statement != null) {
            return (
                    statement.executeQuery("SELECT * FROM players WHERE java_uuid=\"" + uuid + "\"").isBeforeFirst()
                    || statement.executeQuery("SELECT * FROM players WHERE bedrock_uuid=\"" + uuid + "\"").isBeforeFirst()
            );
        }
        return false;
    }

    public static boolean isPlayerIGNWhitelisted(String ign) throws SQLException {
        if (conn != null && statement != null) {
            return (
                    statement.executeQuery("SELECT * FROM players WHERE java_ign=\"" + ign + "\"").isBeforeFirst()
                    || statement.executeQuery("SELECT * FROM players WHERE bedrock_ign=\"" + ign + "\"").isBeforeFirst()
            );
        }
        return false;
    }


}
