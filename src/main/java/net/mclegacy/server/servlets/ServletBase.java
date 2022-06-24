package net.mclegacy.server.servlets;

import net.mclegacy.server.MCLegacy;
import net.mclegacy.server.util.ResourceLoader;
import net.mclegacy.server.util.SystemConfiguration;
import net.mclegacy.server.util.TimeKeeper;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;

public abstract class ServletBase extends HttpServlet
{
    protected String resource;
    protected String contentType = "text/html";
    protected int statusCode = 200;
    protected boolean enableKeyCheck;
    protected HashMap<String, String> dynamicResources = new HashMap<>();
    protected TimeKeeper timeKeeper = new TimeKeeper();
    protected SystemConfiguration config = MCLegacy.getInstance().getConfig();

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        timeKeeper.start();

        if (resource == null)
        {
            sendInternalError(response);
            return;
        }

        if (enableKeyCheck && !hasValidAPIKey(request))
        {
            sendInternalError(response, "Invalid API key", 403);
            return;
        }

        status(response, statusCode, contentType);
        sendResource(response, resource);
    }

    protected boolean hasValidAPIKey(HttpServletRequest request)
    {
        List<NameValuePair> params = URLEncodedUtils.parse("http://dummy/?" + request.getQueryString(), StandardCharsets.UTF_8);
        for (NameValuePair pair : params)
        {
            if (!pair.getName().equalsIgnoreCase("apiKey")) continue;
            return MCLegacy.getInstance().getConfig().web.apiKey.equals(pair.getValue());
        }
        return false;
    }

    protected void basicDynamicStack()
    {
        dynamicResources.put("META", "shared/template/meta.html");
        dynamicResources.put("CSS", "shared/template/css.html");
        dynamicResources.put("SCRIPTS", "shared/template/scripts.html");
        dynamicResources.put("HEADER", "shared/header.html");
        dynamicResources.put("FOOTER", "shared/footer.html");
    }

    protected void status(HttpServletResponse response, int code, String type)
    {
        response.setStatus(statusCode);
        response.setContentType(contentType);
        response.setHeader("Server", "MCLegacy");
    }

    protected void sendResource(HttpServletResponse response, String resource) throws IOException
    {
        ResourceLoader rl = new ResourceLoader(resource);
        sendResource(response, rl);
    }

    protected void sendResource(HttpServletResponse response, ResourceLoader resourceLoader) throws IOException
    {
        resourceLoader.preLoad();
        for (String token : dynamicResources.keySet())
            resourceLoader.injectResource(token, new ResourceLoader(dynamicResources.get(token)));
        resourceLoader.injectContent("TYPE", "WEBSITE");
        resourceLoader.injectContent("TITLE", "MCLegacy");
        resourceLoader.injectContent("DESCRIPTION", "Management at a glance - Legacy Minecraft Server Solutions");
        resourceLoader.injectContent("URL", "https://mclegacy.net");
        resourceLoader.injectContent("IMAGE", "https://mclegacy.net/img/minecraft.png");
        resourceLoader.injectContent("GEN_TIME", String.valueOf(timeKeeper.calc()));
        response.getWriter().print(resourceLoader.getPreLoadContent());
    }

    protected void sendStringResource(HttpServletResponse response, String str) throws IOException
    {
        response.getWriter().print(str);
    }

    protected void sendInternalError(HttpServletResponse response) throws IOException
    {
        sendInternalError(response, "An internal error occurred. If you are able, please notify the system administrator.");
    }

    protected void sendInternalError(HttpServletResponse response, String message) throws IOException
    {
        sendInternalError(response, message, 500);
    }

    protected void sendInternalError(HttpServletResponse response, String message, int statusCode) throws IOException
    {
        response.setStatus(statusCode);
        response.setContentType("text/plain");
        response.setHeader("Server", "MCLegacy");
        response.getWriter().println(message);
    }
}
