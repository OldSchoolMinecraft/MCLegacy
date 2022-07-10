package net.mclegacy.server.servlets.bans;

import net.mclegacy.server.MCLegacy;
import net.mclegacy.server.cache.Page1Bans;
import net.mclegacy.server.cache.PlayerIssuedBans;
import net.mclegacy.server.main.Main;
import net.mclegacy.server.servlets.ServletBase;
import net.mclegacy.server.servlets.bans.Player;
import net.mclegacy.server.util.BanHolder;
import net.mclegacy.server.util.BanUtil;
import net.mclegacy.server.util.ResourceLoader;
import net.mclegacy.server.util.SystemConfiguration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.logging.Logger;

public class Bans extends ServletBase
{
    private static Logger log = Main.getLogger();
    private MCLegacy mcLegacy = MCLegacy.getInstance();
    private SystemConfiguration config = MCLegacy.getInstance().getConfig();

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        try
        {
            if (request.getParameterMap().containsKey("player"))
            {
                new Player().doGet(request, response);
                return;
            }
            resource = "bans/bans.html";
            basicDynamicStack();
            Page1Bans banCache = (Page1Bans) MCLegacy.getInstance().getCacheManager().getCache("p1bans");
            if (banCache == null) banCache = new Page1Bans(config.bans.cachePurgeInterval);
            // statistics
            int stat_bans = BanUtil.getStatistic(BanUtil.Statistic.BANS);
            int stat_servers = BanUtil.getStatistic(BanUtil.Statistic.SERVERS);
            int stat_moderators = BanUtil.getStatistic(BanUtil.Statistic.MODERATORS);
            int page = request.getParameterMap().containsKey("page") ? Integer.parseInt(request.getParameter("page")) : 1;
            if (page == 1) banCache.refresh();
            int numPages = stat_bans / Math.max(1, config.bans.entriesPerPage);
            int minRangeForPage = config.bans.entriesPerPage * page;
            ArrayList<BanHolder> bans = page == 1 ? banCache.getAll() : BanUtil.getBanRange(minRangeForPage, minRangeForPage + config.bans.entriesPerPage);
            StringBuilder sb = new StringBuilder();
            for (BanHolder ban : bans)
            {
                ResourceLoader berl = new ResourceLoader("ban-entry.html").useInternal().preLoad();

                berl.injectContent("SERVER", ban.server);
                berl.injectContent("USERNAME", ban.username);
                berl.injectContent("REASON", ban.reason);
                berl.injectContent("EXPIRATION", BanUtil.convertExpiration(ban.expiration));
                berl.injectContent("ISSUED_BY", ban.issued_by);
                berl.injectContent("ISSUED_AT", BanUtil.convertTimestamp(ban.issued_at));
                sb.append(berl.getPreLoadContent());
            }

            ResourceLoader rl = new ResourceLoader(resource).preLoad();

            // ban entries
            rl.injectContent("BANS_DATA", sb.toString());

            rl.injectContent("STAT_BANS", String.valueOf(stat_bans));
            rl.injectContent("STAT_SERVERS", String.valueOf(stat_servers));
            rl.injectContent("STAT_MODS", String.valueOf(stat_moderators));

            // pagination
            StringBuilder sbPages = new StringBuilder();
            sbPages.append("<li class=\"page-item\"><a class=\"{disabled} page-link\" href=\"/bans?page={page}\">Previous</a></li>".replace("{page}", "").replace("{disabled} ", ((page - 1) > 1) ? "" : "disabled "));
            sbPages.append("<li class=\"page-item\"><a class=\"page-link\" href=\"/bans?page=1\">1</a></li>");
            for (int i = 1; i < numPages; i++)
                sbPages.append("<li class=\"page-item\"><a class=\"page-link\" href=\"/bans?page={page}\">{page}</a></li>".replace("{page}", String.valueOf(i + 1)));
            if ((page + 1) <= numPages) sbPages.append("<li class=\"page-item\"><a class=\"page-link\" href=\"/bans?page={page}\">Next</a></li>".replace("{page}", String.valueOf(page + 1)));
            rl.injectContent("PAGINATION_DATA", sbPages.toString());

            status(response, 200, contentType);
            sendResource(response, rl);
        } catch (Exception ex) {
            ex.printStackTrace();
            status(response, 500, "text/plain");
            sendStringResource(response, ex.getMessage());
        }
    }
}
