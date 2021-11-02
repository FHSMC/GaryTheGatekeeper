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

            statement.executeUpdate("CREATE TABLE IF NOT EXISTS players (id INTEGER PRIMARY KEY AUTOINCREMENT, discord_id INTEGER, ign TEXT, uuid TEXT)");

        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Gary encountered a problem while trying to connect to a database: " + e.getMessage());
            throw e;
        }
    }

    public static void addWhitelistEntry(String IGN) {
        try {
            if (conn != null && statement!= null) {
                statement.executeUpdate("INSERT INTO players (IGN) values(\"" + IGN + "\")");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean checkIfWhitelisted(ProxiedPlayer player) {
        try {
            if (conn != null && statement != null) {
                ResultSet rs = statement.executeQuery("SELECT 1 FROM players WHERE ign=\"" + player.getDisplayName() + "\"");
                return rs.isBeforeFirst();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void removeWhitelistEntry(String player) {
        try {
            if (conn != null && statement!= null) {
                statement.executeUpdate("DELETE FROM players WHERE ign=\"" + player + "\"");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
