package net.mclegacy.server.servlets;

import net.mclegacy.server.util.ResourceLoader;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class Asset extends ServletBase
{
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        if (!hasAllParams(request, "key"))
        {
            sendStringResource(response, "Missing parameters");
            return;
        }

        String assetKey = request.getParameter("key");

        ResourceLoader resourceLoader = new ResourceLoader(assetKey);
        if (!resourceLoader.isGood()) return;

        response.setHeader("Last-Modified", resourceLoader.getAttributes().lastModifiedTime().toString());
        response.setHeader("Content-Length", String.valueOf(resourceLoader.getAttributes().size()));
        response.setHeader("Accept-Ranges", "bytes");
        response.setHeader("Connection", "Keep-Alive");
        response.setHeader("Keep-Alive", "timeout=5, max=100");

        response.setContentType("text/plain;charset=UTF8");
        if (assetKey.endsWith(".png") && resourceLoader.isGood()) response.setContentType("image/png");
        if (assetKey.endsWith(".woff")) response.setContentType("font/woff;charset=UTF8");
        if (assetKey.endsWith(".woff2")) response.setContentType("font/woff2;charset=UTF8");
        if (assetKey.endsWith(".css")) response.setContentType("text/css");
        if (assetKey.endsWith(".js")) response.setContentType("text/javascript;charset=UTF8");

        resourceLoader.directTo(resourceLoader.getFileInputStream(), response.getWriter());
        response.setStatus(200);
        response.setContentLength(resourceLoader.getLastReadBytesTotal());
    }
}
