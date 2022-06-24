package net.mclegacy.server.util;

/**
 * TODO: make better configuration
 */
public class SystemConfiguration
{
    public Web web;
    public Bans bans;
    public Jetty jetty;
    public MySQL mysql;

    public SystemConfiguration()
    {
        web = new Web();
        bans = new Bans();
        jetty = new Jetty();
        mysql = new MySQL();
    }

    public class Web
    {
        public String apiKey;
        public String contentDirectory;

        public Web()
        {
            apiKey = "CHANGEME :D";
            contentDirectory = "content/";
        }
    }

    public class Bans
    {
        public int entriesPerPage;
        public int statCheckInterval;
        public int cachePurgeInterval;

        public Bans()
        {
            entriesPerPage = 25;
            statCheckInterval = 15;
            cachePurgeInterval = 15;
        }
    }

    public class Jetty
    {
        public String bindAddress;
        public int bindPort;

        public Jetty()
        {
            bindAddress = "127.0.0.1";
            bindPort = 8080;
        }
    }

    public class MySQL
    {
        public String host;
        public int port;
        public String user;
        public String pass;
        public String database;

        public MySQL()
        {
            host = "localhost";
            port = 3306;
            user = "root";
            pass = "root";
            database = "mydb";
        }
    }
}
