package fhsmc.minecraft.gary;

import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.sql.*;

public class Storage {

    private static Connection conn = null;
    private static Statement statement = null;

    private static void update(String sql) throws SQLException{
        if (conn != null && statement != null) {
                statement.executeUpdate(sql);
        }
    }

    private static boolean booleanQuery(String sql) throws SQLException{
        if (conn != null && statement != null) {
            ResultSet rs = statement.executeQuery(sql);
            return rs.isBeforeFirst();
        }
        return false;
    }

    public static void open(String databaseFilePath) throws SQLException, ClassNotFoundException {
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:" + databaseFilePath);
            statement = conn.createStatement();
            statement.setQueryTimeout(30);

            update("CREATE TABLE IF NOT EXISTS players (id INTEGER PRIMARY KEY AUTOINCREMENT, discord_id INTEGER, java_ign TEXT, java_uuid TEXT, bedrock_ign TEXT, bedrock_uuid TEXT)");

        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Gary encountered a problem while trying to connect to a database: " + e.getMessage());
            throw e;
        }
    }

    public static void addDiscordId(String discord_id) throws SQLException {
        if (!discordUserInWhitelist(discord_id)) {
            update("INSERT INTO players (discord_id) VALUES (" + discord_id + ")");
        }
    }

    public static void setIGNFromDiscord(String discord_id, String ign, boolean bedrock) throws SQLException {
        String platform = bedrock ? "bedrock" : "java";
        if (discordUserInWhitelist(discord_id)){
            update("UPDATE players SET " + platform + "_ign=\"" + ign + "\" WHERE discord_id=" + discord_id);
        } else {
            update("INSERT INTO players (discord_id," + platform + "_ign) VALUES (" + discord_id + ",\"" + ign + "\")");
        }
    }

    public static void setUUIDFromIGN(String ign, String uuid, boolean bedrock) throws SQLException {
        String platform = bedrock ? "bedrock" : "java";
        update("UPDATE players SET " + platform + "_uuid=\"" + uuid + "\" WHERE " + platform + "_ign=\"" + ign + "\"");
    }

    public static void setIGNFromUUID(String uuid, String ign, boolean bedrock) throws SQLException {
        String platform = bedrock ? "bedrock" : "java";
        update("UPDATE players SET " + platform + "_ign=\"" + ign + "\" WHERE " + platform + "_uuid=\"" + uuid + "\"");
    }

    public static boolean discordUserInWhitelist(String discord_id) throws SQLException {
            return booleanQuery("SELECT * FROM players WHERE discord_id=" + discord_id);
    }

    public static boolean isPlayerUUIDWhitelisted(String uuid) throws SQLException {
        return (
            booleanQuery("SELECT * FROM players WHERE java_uuid=\"" + uuid + "\"")
            || booleanQuery("SELECT * FROM players WHERE bedrock_uuid=\"" + uuid + "\"")
        );
    }

    public static boolean isPlayerIGNWhitelisted(String ign) throws SQLException {
        return (
            booleanQuery("SELECT * FROM players WHERE java_ign=\"" + ign + "\"")
            || booleanQuery("SELECT * FROM players WHERE bedrock_ign=\"" + ign + "\"")
        );
    }

}
