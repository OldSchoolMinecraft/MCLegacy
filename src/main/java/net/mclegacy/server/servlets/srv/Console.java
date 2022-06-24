package net.mclegacy.server.servlets.srv;

import net.mclegacy.server.servlets.ServletBase;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class Console extends ServletBase
{
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        resource = "srv/console.html";
        basicDynamicStack();
        super.doGet(request, response);
    }
}
