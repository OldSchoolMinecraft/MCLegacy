package net.mclegacy.server.servlets.dynmap;

import net.mclegacy.server.servlets.ServletBase;
import net.mclegacy.server.util.ResourceLoader;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class DynmapIndex extends ServletBase
{
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        resource = "dynmap/index.html";

        if (!hasValidAPIKey(request))
        {
//            sendInternalError(response, "Invalid API key", 401);
//            return;
        }

        if (hasAllParams(request, "config", "update", "tiles", "markers"))
        {
            String config = request.getParameter("config");
            String update = request.getParameter("update");
            String tiles = request.getParameter("tiles");
            String markers = request.getParameter("markers");

            ResourceLoader rl = new ResourceLoader(resource).preLoad();
            rl.injectContent("CONFIG", config);
            rl.injectContent("UPDATE", update);
            rl.injectContent("TILES", tiles);
            rl.injectContent("MARKERS", markers);
            sendStringResource(response, rl.getPreLoadContent());
        } else sendInternalError(response, "Missing parameters", 400);
    }


}
