package net.mclegacy.server.util;

import net.mclegacy.server.MCLegacy;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQL
{
    private static SystemConfiguration config = MCLegacy.getInstance().getConfig();
    private static Connection connection;

    public static Connection getConnection() throws SQLException
    {
        if (connection == null) connection = DriverManager.getConnection(String.format("jdbc:mysql://%s:%s/%s", config.mysql.host, config.mysql.port, config.mysql.database), config.mysql.user, config.mysql.pass);
        return connection;
    }
}
