package net.mclegacy.server.servlets.api.servers;

import net.mclegacy.server.servlets.ServletBase;
import net.mclegacy.server.util.ResourceLoader;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class FetchIcon extends ServletBase
{
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        ResourceLoader rl = new ResourceLoader("img/computer.png");
        response.setHeader("Last-Modified", rl.getAttributes().lastModifiedTime().toString());
        response.setHeader("Content-Length", String.valueOf(rl.getAttributes().size()));
        response.setHeader("Accept-Ranges", "bytes");
        response.setHeader("Connection", "Keep-Alive");
        response.setHeader("Keep-Alive", "timeout=5, max=100");
        status(response, 200, "image/png;charset=UTF-8");
        rl.directTo(rl.getFileInputStream(), response.getWriter());
        response.setContentLength(rl.getLastReadBytesTotal());
    }
}
