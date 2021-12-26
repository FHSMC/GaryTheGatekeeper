package fhsmc.minecraft.gary;

import java.sql.*;

public class Storage {

    private static Connection conn = null;
    private static Statement statement = null;

    private static void update(String sql) throws SQLException{
        if (conn != null && statement != null) {
            statement.executeUpdate(sql);
        } else {
            throw new NullPointerException("connection and statement cannot be null");
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

            update("CREATE TABLE IF NOT EXISTS authenticated_users (id INTEGER PRIMARY KEY NOT NULL)");
            update("CREATE TABLE IF NOT EXISTS whitelist (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, ign TEXT NOT NULL, uuid TEXT, discord_id INTEGER, platform INTEGER NOT NULL");

        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Gary encountered a problem while trying to connect to a database: " + e.getMessage());
            throw e;
        }
    }

    public static void addDiscordId(String discord_id) throws SQLException {
        update("INSERT INTO authenticated_users (id) VALUES (" + discord_id + ")");
    }

    public static void setIGNFromDiscord(String discord_id, String ign, boolean bedrock) throws SQLException {
        String platform = bedrock ? "1" : "0";
        if (discordUserHasPlatform(discord_id, platform)) {
            update("UPDATE whitelist SET ign=\"" + ign + "\" WHERE discord_id=" + discord_id + ", platform=" + platform);
        } else {
            update("INSERT INTO whitelist (ign, discord_id, platform) VALUES (\"" + ign + "\", " + discord_id + ", " + platform + ")");
        }
    }

    public static void setUUIDFromIGN(String ign, String uuid, boolean bedrock) throws SQLException {
        String platform = bedrock ? "1" : "0";
        update("UPDATE whitelist SET uuid=\"" + uuid + "\" WHERE ign=\"" + ign + "\" AND platform=" + platform);
    }

    public static void setIGNFromUUID(String uuid, String ign, boolean bedrock) throws SQLException {
        String platform = bedrock ? "1" : "0";
        update("UPDATE whitelist SET ign=\"" + ign + "\" WHERE uuid=\"" + uuid + "\" AND platform=" + platform);
    }

    public static void addAnonymousIGN(String ign, boolean bedrock) throws SQLException {
        String platform = bedrock ? "1" : "0";
        update("INSERT INTO whitelist (ign, platform) VALUES (\"" + ign + "\", " + platform + ")");
    }

    public static void removeUUIDFromDiscord(String discord_id, boolean bedrock) throws SQLException{
        String platform = bedrock ? "1" : "0";
        update("UPDATE whitelist SET uuid = NULL WHERE discord_id=" + discord_id + " AND platform=" + platform);
    }

    public static boolean discordUserInWhitelist(String discord_id) throws SQLException {
        return booleanQuery("SELECT * FROM authenticated_users WHERE id=" + discord_id);
    }

    public static boolean isPlayerUUIDWhitelisted(String uuid, boolean bedrock) throws SQLException {
        String platform = bedrock ? "1" : "0";
        return booleanQuery("SELECT * FROM whitelist WHERE uuid=\"" + uuid + "\" AND platform=" + platform + "");
    }

    public static boolean isPlayerIGNWhitelisted(String ign, boolean bedrock) throws SQLException {
        String platform = bedrock ? "1" : "0";
        return booleanQuery("SELECT * FROM whitelist WHERE ign=\"" + ign + "\" AND platform=" + platform + "");
    }

    public static boolean discordUserHasPlatform(String discord_id, String platform) throws SQLException {
        return booleanQuery("SELECT * FROM whitelist WHERE discord_id=" + discord_id + " AND platform=" + platform);
    }

}
