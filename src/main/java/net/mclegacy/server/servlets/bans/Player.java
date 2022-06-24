package net.mclegacy.server.servlets.bans;

import net.mclegacy.server.MCLegacy;
import net.mclegacy.server.cache.ObjectCache;
import net.mclegacy.server.cache.PlayerIssuedBans;
import net.mclegacy.server.cache.PlayerReceivedBans;
import net.mclegacy.server.servlets.ServletBase;
import net.mclegacy.server.util.BanHolder;
import net.mclegacy.server.util.BanUtil;
import net.mclegacy.server.util.ResourceLoader;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class Player extends ServletBase
{
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        try
        {
            resource = "bans/player.html";
            basicDynamicStack();
            String player = request.getParameter("player");
            if (player.isEmpty())
            {
                response.sendRedirect("/bans");
                return;
            }

            ObjectCache<BanHolder> issuedBansCache = MCLegacy.getInstance().getCacheManager().getCache("issuedBans-" + player);
            ObjectCache<BanHolder> receivedBansCache = MCLegacy.getInstance().getCacheManager().getCache("receivedBans-" + player);

            if (issuedBansCache == null) issuedBansCache = new PlayerIssuedBans(player, config.bans.cachePurgeInterval);
            if (receivedBansCache == null) receivedBansCache = new PlayerReceivedBans(player, config.bans.cachePurgeInterval);

            issuedBansCache.refresh();
            receivedBansCache.refresh();

            ResourceLoader rl = new ResourceLoader(resource).preLoad();

            StringBuilder sb = new StringBuilder();
            for (BanHolder ban : issuedBansCache.getAll())
            {
                ResourceLoader berl = new ResourceLoader("issued-ban-entry.html").useInternal().preLoad();

                berl.injectContent("USERNAME", ban.username);
                berl.injectContent("REASON", ban.reason);
                berl.injectContent("EXPIRATION", BanUtil.convertExpiration(ban.expiration));
                berl.injectContent("ISSUED_AT", BanUtil.convertTimestamp(ban.issued_at));
                sb.append(berl.getPreLoadContent());
            }
            rl.injectContent("BANS_ISSUED", sb.toString());

            sb = new StringBuilder();
            for (BanHolder ban : receivedBansCache.getAll())
            {
                ResourceLoader berl = new ResourceLoader("received-ban-entry.html").useInternal().preLoad();

                berl.injectContent("SERVER", ban.server);
                berl.injectContent("REASON", ban.reason);
                berl.injectContent("EXPIRATION", BanUtil.convertExpiration(ban.expiration));
                berl.injectContent("ISSUED_BY", ban.issued_by);
                berl.injectContent("ISSUED_AT", BanUtil.convertTimestamp(ban.issued_at));
                sb.append(berl.getPreLoadContent());
            }
            rl.injectContent("BANS_RECEIVED", sb.toString());
            rl.injectContent("USERNAME", player);
            sendResource(response, rl);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
