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
        super.doGet(request, response);
        List<NameValuePair> params = URLEncodedUtils.parse(request.getQueryString(), StandardCharsets.UTF_8);
        String assetKey = "";
        for (NameValuePair p : params) if (p.getName().equalsIgnoreCase("key")) assetKey = p.getValue();
        if (assetKey == null || assetKey.isEmpty())
        {
            response.getWriter().println("Failed to read asset, assetKey=" + assetKey);
            return;
        }

        ResourceLoader resourceLoader = new ResourceLoader(assetKey);
        if (!resourceLoader.isGood()) return;

        response.setHeader("Last-Modified", resourceLoader.getAttributes().lastModifiedTime().toString());
        response.setHeader("Content-Length", String.valueOf(resourceLoader.getAttributes().size()));
        response.setHeader("Accept-Ranges", "bytes");
        response.setHeader("Connection", "Keep-Alive");
        response.setHeader("Keep-Alive", "timeout=5, max=100");

        if (assetKey.endsWith(".png") && resourceLoader.isGood()) response.setContentType("image/png");
        if (assetKey.endsWith(".woff")) response.setContentType("font/woff;charset=UTF8");
        if (assetKey.endsWith(".woff2")) response.setContentType("font/woff2;charset=UTF8");
        if (assetKey.endsWith(".css")) response.setContentType("text/css;charset=UTF8");

        if (resourceLoader.directTo(resourceLoader.getFileInputStream(), response.getWriter())) response.setStatus(200);
        else response.setStatus(500);
        response.setContentLength(resourceLoader.getLastReadBytesTotal());
    }
}
