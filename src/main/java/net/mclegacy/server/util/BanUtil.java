package net.mclegacy.server.util;

import net.mclegacy.server.MCLegacy;
import net.mclegacy.server.main.Main;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.TimeZone;
import java.util.logging.Logger;

public class BanUtil
{
    private static Logger log = Main.getLogger();

    public static String convertExpiration(String input)
    {
        long duration = Long.parseLong(input);
        if (duration == -1) return "Permanent";
        return convertTimestamp(input);
    }

    public static String convertTimestamp(String input)
    {
        long unixTimestamp = Long.parseLong(input);
        Date date = new Date(unixTimestamp * 1000L);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
        sdf.setTimeZone(TimeZone.getTimeZone("PST"));
        return sdf.format(date);
    }

    public static ArrayList<BanHolder> getBanRange(int min, int max) throws SQLException
    {
        Instant start = Instant.now();
        SystemConfiguration config = MCLegacy.getInstance().getConfig();
        ArrayList<BanHolder> returnArray = new ArrayList<>();
        Connection con = MySQL.getConnection();
        PreparedStatement stmt = con.prepareStatement(String.format("SELECT * FROM global_bans ORDER BY issued_at DESC LIMIT %s", config.bans.entriesPerPage));
        ResultSet rs = stmt.executeQuery();

        while (rs.next())
        {
            String server = rs.getString("server");
            String username = rs.getString("username");
            String reason = rs.getString("reason");
            String expiration = rs.getString("expiration");
            String issued_by = rs.getString("issued_by");
            String issued_at = rs.getString("issued_at");
            BanHolder b = new BanHolder(server, username, reason, expiration, issued_by, issued_at);
            returnArray.add(b);
        }
        if (System.getenv().containsKey("MCL_DEBUG"))
            log.warning(String.format("Retrieved ban range %s-%s, took %sms", min, max, Duration.between(start, Instant.now()).toMillis()));
        return returnArray;
    }

    public static ArrayList<BanHolder> getReceivedBans(String usernameTarget) throws SQLException
    {
        Instant start = Instant.now();
        ArrayList<BanHolder> returnArray = new ArrayList<>();
        Connection con = MySQL.getConnection();
        PreparedStatement stmt = con.prepareStatement(String.format("SELECT * FROM global_bans WHERE username = ? ORDER BY issued_at DESC"));
        stmt.setString(1, usernameTarget);
        ResultSet rs = stmt.executeQuery();

        while (rs.next())
        {
            String server = rs.getString("server");
            String username = rs.getString("username");
            String reason = rs.getString("reason");
            String expiration = rs.getString("expiration");
            String issued_by = rs.getString("issued_by");
            String issued_at = rs.getString("issued_at");
            BanHolder b = new BanHolder(server, username, reason, expiration, issued_by, issued_at);
            returnArray.add(b);
        }
        if (System.getenv().containsKey("MCL_DEBUG"))
            log.warning(String.format("Retrieved bans for %s, took %sms", usernameTarget, Duration.between(start, Instant.now()).toMillis()));
        return returnArray;
    }

    public static ArrayList<BanHolder> getIssuedBans(String usernameTarget) throws SQLException
    {
        Instant start = Instant.now();
        ArrayList<BanHolder> returnArray = new ArrayList<>();
        Connection con = MySQL.getConnection();
        PreparedStatement stmt = con.prepareStatement(String.format("SELECT * FROM global_bans WHERE issued_by = ? ORDER BY issued_at DESC"));
        stmt.setString(1, usernameTarget);
        ResultSet rs = stmt.executeQuery();

        while (rs.next())
        {
            String server = rs.getString("server");
            String username = rs.getString("username");
            String reason = rs.getString("reason");
            String expiration = rs.getString("expiration");
            String issued_by = rs.getString("issued_by");
            String issued_at = rs.getString("issued_at");
            BanHolder b = new BanHolder(server, username, reason, expiration, issued_by, issued_at);
            returnArray.add(b);
        }
        if (System.getenv().containsKey("MCL_DEBUG"))
            log.warning(String.format("Retrieved bans issued by %s, took %sms", usernameTarget, Duration.between(start, Instant.now()).toMillis()));
        return returnArray;
    }

    public static int getStatistic(Statistic stat)
    {
        try
        {
            Connection con = MySQL.getConnection();
            PreparedStatement stmt;

            switch (stat)
            {
                default:
                case BANS:
                    stmt = con.prepareStatement("SELECT count(DISTINCT(username)) FROM global_bans");
                    break;
                case SERVERS:
                    stmt = con.prepareStatement("SELECT count(DISTINCT(server)) FROM global_bans");
                    break;
                case MODERATORS:
                    stmt = con.prepareStatement("SELECT count(DISTINCT(issued_by)) FROM global_bans");
                    break;
            }

            ResultSet rs = stmt.executeQuery();

            if (rs.next())
                return rs.getInt("count");
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return 0;
    }

    public enum Statistic
    {
        BANS, SERVERS, MODERATORS
    }
}
