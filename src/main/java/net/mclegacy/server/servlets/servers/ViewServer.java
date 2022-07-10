package net.mclegacy.server.servlets.servers;

import net.mclegacy.server.servlets.ServletBase;
import net.mclegacy.server.util.ResourceLoader;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ViewServer extends ServletBase
{
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        resource = "servers/view.html";
        basicDynamicStack();
        String serverName = "Not Available";
        String description = "Server description is currently unavailable.";
        String rules = "Server rules is currently unavailable.";
        String dynmap = "Server dynmap is currently unavailable.";
        String serverIcon = "/img/computer.png";
        if (hasAllParams(request, "name"))
        {
            serverName = request.getParameter("name");
            //TODO: grab uplink from database, request needed data directly from server
        }
        ResourceLoader rl = new ResourceLoader(resource).preLoad();
        rl.injectContent("SERVER_DESCRIPTION", description);
        rl.injectContent("SERVER_RULES", rules);
        rl.injectContent("SERVER_DYNMAP", dynmap);
        rl.injectContent("SERVER_ICON", serverIcon);
        rl.injectContent("SERVER_NAME", serverName);
        sendResource(response, rl);
    }
}
