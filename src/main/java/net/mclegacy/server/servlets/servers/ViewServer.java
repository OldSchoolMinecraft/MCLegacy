package net.mclegacy.server.servlets.servers;

import net.mclegacy.server.servlets.ServletBase;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ViewServer extends ServletBase
{
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        resource = "servers/server.html";
        super.doGet(request, response);
    }
}
