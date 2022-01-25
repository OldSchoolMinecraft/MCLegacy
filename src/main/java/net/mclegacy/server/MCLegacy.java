package net.mclegacy.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import net.mclegacy.server.main.Main;
import net.mclegacy.server.page.Page;
import net.mclegacy.server.servlets.Asset;
import net.mclegacy.server.servlets.hosting.HostingIndex;
import net.mclegacy.server.servlets.hosting.MinecraftServers;
import net.mclegacy.server.servlets.hosting.SBoxServers;
import net.mclegacy.server.servlets.user.UserDashboard;
import net.mclegacy.server.servlets.user.UserLogin;
import net.mclegacy.server.servlets.videos.Videos;
import net.mclegacy.server.servlets.videos.ViewVideo;
import net.mclegacy.server.util.SystemConfiguration;
import net.mclegacy.server.servlets.Index;
import net.mclegacy.server.servlets.bans.Bans;
import net.mclegacy.server.servlets.bans.ViewBan;
import net.mclegacy.server.servlets.community.CommunityHome;
import net.mclegacy.server.servlets.error.GenericError;
import net.mclegacy.server.servlets.guides.Guides;
import net.mclegacy.server.servlets.guides.ViewGuide;
import net.mclegacy.server.servlets.mods.Mods;
import net.mclegacy.server.servlets.mods.ViewMod;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ErrorPageErrorHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;

import java.io.*;
import java.util.ArrayList;
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
    private ArrayList<Page> pages;

    public void init()
    {
        try
        {
            instance = this;
            long startTime = System.currentTimeMillis();
            cacheManager = CacheManagerBuilder.newCacheManagerBuilder()
                    .withCache("preConfigured",
                            CacheConfigurationBuilder.newCacheConfigurationBuilder(Long.class, String.class, ResourcePoolsBuilder.heap(10)))
                    .build(true);
            pages = new ArrayList<>();
            loadConfig();
            initWeb();
            debugCheck();
            long endTime = System.currentTimeMillis();

            log.info("MCLegacy initialized in " + (endTime - startTime) + " milliseconds.");
        } catch (Exception ex) {
            ex.printStackTrace();
            log.severe("MCLegacy initialization failed: " + ex.getMessage());
            System.exit(1);
        }
    }

    private void debugCheck()
    {
        if (System.getenv().containsKey("MCL_DEBUG"))
        {
            log.info("=====================");
            log.info("DEBUG MODE IS ENABLED");
            log.info("=====================");
        }
    }

    private void initWeb() throws Exception
    {
        jettyServer = new Server();
        ServerConnector connector = new ServerConnector(jettyServer);
        connector.setPort(config.jettyNetworkConfiguration.bindPort);
        jettyServer.setConnectors(new Connector[]{connector});
        ServletContextHandler handler = new ServletContextHandler();

        handler.setContextPath("/");

        // img holder
        ServletHolder imgHolder = new ServletHolder("img", DefaultServlet.class);
        imgHolder.setInitParameter("resourceBase", getConfig().contentDirectory + "img/");
        imgHolder.setInitParameter("dirAllowed", "true");
        imgHolder.setInitParameter("pathInfoOnly", "true");
        handler.addServlet(imgHolder, "/img/*");

        // css holder
        ServletHolder cssHolder = new ServletHolder("css", DefaultServlet.class);
        cssHolder.setInitParameter("resourceBase", getConfig().contentDirectory + "css/");
        cssHolder.setInitParameter("dirAllowed", "true");
        cssHolder.setInitParameter("pathInfoOnly", "true");
        handler.addServlet(cssHolder, "/css/*");

        // public js holder
        ServletHolder jsHolder = new ServletHolder("js-public", DefaultServlet.class);
        jsHolder.setInitParameter("resourceBase", getConfig().contentDirectory + "js-public/");
        jsHolder.setInitParameter("dirAllowed", "true");
        jsHolder.setInitParameter("pathInfoOnly", "true");
        handler.addServlet(jsHolder, "/js/*");

        // fonts holder
        ServletHolder fontsHolder = new ServletHolder("fonts", DefaultServlet.class);
        fontsHolder.setInitParameter("resourceBase", getConfig().contentDirectory + "fonts/");
        fontsHolder.setInitParameter("dirAllowed", "true");
        fontsHolder.setInitParameter("pathInfoOnly", "true");
        handler.addServlet(cssHolder, "/fonts/*");

        // index & backend
        pages.add(new Page(Index.class, "/"));
        pages.add(new Page(Asset.class, "/asset"));

        // bans
        pages.add(new Page(Bans.class, "/bans"));
        pages.add(new Page(ViewBan.class, "/vb"));

        // guides
        pages.add(new Page(Guides.class, "/guides"));
        pages.add(new Page(ViewGuide.class, "/g/*"));

        // videos
        pages.add(new Page(Videos.class, "/videos"));
        pages.add(new Page(ViewVideo.class, "/v/*"));

        // mods
        pages.add(new Page(Mods.class, "/mods"));
        pages.add(new Page(ViewMod.class, "/m/*"));

        // community
        pages.add(new Page(CommunityHome.class, "/community"));

        // user
        pages.add(new Page(UserDashboard.class, "/user/dashboard"));
        pages.add(new Page(UserLogin.class, "/user/login"));

        // hosting
        pages.add(new Page(HostingIndex.class, "/hosting"));
        pages.add(new Page(MinecraftServers.class, "/hosting/minecraft"));
        pages.add(new Page(SBoxServers.class, "/hosting/sbox"));
        pages.add(new Page(HostingIndex.class, "/hosting/admin"));

        // error
        handler.addServlet(GenericError.class, "/error");

        for (Page page : pages) handler.addServlet(page.getServletClass(), page.getPathSpec());

        ErrorPageErrorHandler errorHandler = new ErrorPageErrorHandler();
        errorHandler.addErrorPage(404, "/error");
        errorHandler.addErrorPage(500, "/error");
        //handler.setErrorHandler(errorHandler);

        jettyServer.setHandler(handler);
        jettyServer.start();
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
}
