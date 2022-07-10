package net.mclegacy.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import net.mclegacy.server.cache.CacheManager;
import net.mclegacy.server.main.Main;
import net.mclegacy.server.servlets.Asset;
import net.mclegacy.server.servlets.api.bans.LookupBan;
import net.mclegacy.server.servlets.api.servers.FetchIcon;
import net.mclegacy.server.servlets.bans.Bans;
import net.mclegacy.server.servlets.api.bans.IssueBan;
import net.mclegacy.server.servlets.api.bans.RevokeBan;
import net.mclegacy.server.servlets.dynmap.DynmapIndex;
import net.mclegacy.server.servlets.servers.ViewServer;
import net.mclegacy.server.servlets.srv.Console;
import net.mclegacy.server.servlets.user.UserDashboard;
import net.mclegacy.server.servlets.user.UserLogin;
import net.mclegacy.server.util.APISecurityManager;
import net.mclegacy.server.util.SystemConfiguration;
import net.mclegacy.server.servlets.Index;
import net.mclegacy.server.servlets.error.GenericError;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ErrorPageErrorHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import java.io.*;
import java.time.Duration;
import java.time.Instant;
import java.util.logging.Logger;

public class MCLegacy
{
    public static final String VERSION = "1.0";

    private static MCLegacy instance;
    private static final Logger log = Main.getLogger();
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static MCLegacy getInstance() { return instance; }

    private CacheManager cacheManager;
    private SystemConfiguration config;
    private Server jettyServer;

    public void init()
    {
        try
        {
            instance = this;
            Instant startTime = Instant.now();
            cacheManager = new CacheManager();
            loadConfig();
            initWeb();
            debugCheck();
            log.info(String.format("MCLegacy v%s initialized in %s milliseconds.", VERSION, Duration.between(startTime, Instant.now()).toMillis()));
        } catch (Exception ex) {
            ex.printStackTrace();
            log.severe("MCLegacy initialization failed: " + ex.getMessage());
            System.exit(1);
        }
    }

    private void debugCheck()
    {
        try
        {
            if (System.getenv().containsKey("MCL_DEBUG"))
            {
                log.info("=====================");
                log.info("DEBUG MODE IS ENABLED");
                log.info("Access Key: " + APISecurityManager.registerNewKey("debug"));
                log.info("=====================");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void initWeb() throws Exception
    {
        jettyServer = new Server();
        ServerConnector connector = new ServerConnector(jettyServer);
        connector.setHost(config.jetty.bindAddress);
        connector.setPort(config.jetty.bindPort);
        jettyServer.setConnectors(new Connector[]{connector});
        jettyServer.setStopAtShutdown(true);
        ServletContextHandler handler = new ServletContextHandler();
        handler.setContextPath("/");

        // img holder
        ServletHolder imgHolder = new ServletHolder("img", DefaultServlet.class);
        imgHolder.setInitParameter("resourceBase", getConfig().web.contentDirectory + "img/");
        imgHolder.setInitParameter("dirAllowed", "false");
        imgHolder.setInitParameter("pathInfoOnly", "true");
        handler.addServlet(imgHolder, "/img/*");

        // css holder
        ServletHolder cssHolder = new ServletHolder("css", DefaultServlet.class);
        cssHolder.setInitParameter("resourceBase", getConfig().web.contentDirectory + "css/");
        cssHolder.setInitParameter("dirAllowed", "true");
        cssHolder.setInitParameter("pathInfoOnly", "true");
        handler.addServlet(cssHolder, "/css/*");

        // public js holder
        ServletHolder jsHolder = new ServletHolder("js-public", DefaultServlet.class);
        jsHolder.setInitParameter("resourceBase", getConfig().web.contentDirectory + "js-public/");
        jsHolder.setInitParameter("dirAllowed", "false");
        jsHolder.setInitParameter("pathInfoOnly", "true");
        handler.addServlet(jsHolder, "/js/*");

        // fonts holder
        ServletHolder fontsHolder = new ServletHolder("fonts", DefaultServlet.class);
        fontsHolder.setInitParameter("resourceBase", getConfig().web.contentDirectory + "fonts/");
        fontsHolder.setInitParameter("dirAllowed", "false");
        fontsHolder.setInitParameter("pathInfoOnly", "true");
        handler.addServlet(cssHolder, "/fonts/*");

        // downloads holder
        ServletHolder dlHolder = new ServletHolder("downloads", DefaultServlet.class);
        jsHolder.setInitParameter("resourceBase", getConfig().web.contentDirectory + "downloads/");
        jsHolder.setInitParameter("dirAllowed", "false");
        jsHolder.setInitParameter("pathInfoOnly", "true");
        handler.addServlet(jsHolder, "/dl/*");

        // dynmap holder
        ServletHolder dynmapHolder = new ServletHolder("dynmap", DefaultServlet.class);
        dynmapHolder.setInitParameter("resourceBase", getConfig().web.contentDirectory + "dynmap/");
        dynmapHolder.setInitParameter("dirAllowed", "true");
        dynmapHolder.setInitParameter("pathInfoOnly", "true");
        handler.addServlet(dynmapHolder, "/dynmap/*");

        // index & backend
        handler.addServlet(Index.class, "/");
        handler.addServlet(Asset.class, "/asset");
        handler.addServlet(GenericError.class, "/error");

        // bans
        handler.addServlet(Bans.class, "/bans");
        handler.addServlet(IssueBan.class, "/api/v1/bans/issue");
        handler.addServlet(RevokeBan.class, "/api/v1/bans/revoke");
        handler.addServlet(LookupBan.class, "/api/v1/bans/lookup");

        // users
        handler.addServlet(UserDashboard.class, "/user/dashboard");
        handler.addServlet(UserLogin.class, "/user/login");

        // servers
        handler.addServlet(ViewServer.class, "/servers/view");
        handler.addServlet(Console.class, "/srv/console");
        handler.addServlet(FetchIcon.class, "/api/v1/servers/fetchIcon");

        // misc
        handler.addServlet(DynmapIndex.class, "/dynmap/dmap");

        ErrorPageErrorHandler errorHandler = new ErrorPageErrorHandler();
        errorHandler.addErrorPage(404, "/error");
        errorHandler.addErrorPage(500, "/error");
        handler.setErrorHandler(errorHandler);

        jettyServer.setHandler(handler);
        jettyServer.start();

        // ensure the jetty server gets shutdown when the jvm exits
        Runtime.getRuntime().addShutdownHook(new Thread(() ->
        {
            try
            {
                jettyServer.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }));
    }

    private void loadConfig() throws IOException
    {
        File configFile = new File("config.json");
        if (configFile.exists()) config = gson.fromJson(new JsonReader(new FileReader(configFile)), SystemConfiguration.class);
        else
        {
            config = new SystemConfiguration();
            try (FileWriter fileWriter = new FileWriter(configFile))
            {
                gson.toJson(config, SystemConfiguration.class, fileWriter);
            }
        }
    }

    public SystemConfiguration getConfig()
    {
        return config;
    }

    public CacheManager getCacheManager()
    {
        return cacheManager;
    }

    public static int getLine()
    {
        return new Throwable().getStackTrace()[0].getLineNumber();
    }
}
